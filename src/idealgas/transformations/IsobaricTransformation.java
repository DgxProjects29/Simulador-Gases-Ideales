package idealgas.transformations;

import idealgas.GasConstants;
import idealgas.GasPVRange;

import java.util.HashMap;

public class IsobaricTransformation extends BaseTransformation implements TransformationStrategy {

    public IsobaricTransformation(HashMap<String, Float> initialData, HashMap<String, Float> finalData) {
        super(initialData, finalData);

        pressure = pressure0;

        volume = volume0;
        temperature = temperature0;

        setTemperatureVelocityDeltaSign();
    }

    @Override
    public void updateData() {
        volume = volume0 * temperature / temperature0;

        work = pressure0 * (volume - volume0);
        heat = nMoles * GasConstants.CPM * (temperature - temperature0);

        internalEnergy = Math.abs(heat);

        temperature += deltaT;

        this.updateVelocity(temperature);
        this.updateGasData();
    }

    @Override
    public boolean isAbsorbingHeat() {
        return heat > 0;
    }

    @Override
    public boolean isLosingHeat() {
        return heat < 0;
    }

    @Override
    public boolean isGasBeingExpanded() {
        return work > 0;
    }

    @Override
    public boolean isGasBeingCompressed() {
        return work < 0;
    }

    @Override
    public HashMap<String, Float> getData() {
        return gasData;
    }

    @Override
    public boolean IsTheTransformationFinished() {

        float finalTemperature = finalData.get("temperature");
        float finalVolume = finalData.get("volume");

        if (deltaT <= 0) {
            // Temperatura bajando, volumen tambien

            if (finalTemperature != 0) {
                return temperature < finalTemperature;
            }

            if (finalVolume != 0) {
                return volume < finalVolume;
            }

        } else {
            // Temperatura subiendo, volumen subiendo

            if (finalTemperature != 0) {
                return temperature > finalTemperature;
            }

            if (finalVolume != 0) {
                return volume > finalVolume;
            }
        }

        return false;
    }

    private void setTemperatureVelocityDeltaSign() {

        float finalTemperature = finalData.get("temperature");
        if (temperature0 > finalTemperature && finalTemperature != 0) {
            deltaT *= -1;
            deltaVel *= -1;
        }

        float finalVolume = finalData.get("volume");
        if (volume0 > finalVolume && finalVolume != 0) {
            deltaT *= -1;
            deltaVel *= -1;
        }

    }

    @Override
    public GasPVRange getPVrange() {
        
        float finalTemperature = finalData.get("temperature");
        float finalVolume = finalData.get("volume");

        float deltaPressure = 1000;
        float deltaVolume = 10;

        float minVolume, maxVolume;

        if (finalVolume != 0){
            minVolume = Math.min(volume0, finalVolume);
            maxVolume = Math.max(volume0, finalVolume);
        }else{
            minVolume = Math.min(volume0, volume0 * finalTemperature / temperature0);
            maxVolume = Math.max(volume0, volume0 * finalTemperature / temperature0);
        }

        GasPVRange gasPVRange = new GasPVRange(pressure0 - deltaPressure, 
            pressure0 + deltaPressure, minVolume - deltaVolume, maxVolume + deltaVolume);
        
        return gasPVRange;
    }
    
}
