package appcontrol;

import processing.core.PApplet;

import java.util.HashMap;

import appcontrol.sketchs.Barometer;
import appcontrol.sketchs.Cylinder;
import appcontrol.sketchs.HeatSource;
import appcontrol.sketchs.PVGraph;
import appcontrol.sketchs.StatusBar;
import appcontrol.sketchs.Thermometer;
import appcontrol.transformations.AdiabaticTransformation;
import appcontrol.transformations.IsobaricTransformation;
import appcontrol.transformations.IsothermalTransformation;
import appcontrol.transformations.IsovolumetricTransformation;
import appcontrol.transformations.TransformationStrategy;
import forms.TransformationType;

public class SimulationWorkspace extends PApplet{

    private final int SKETCH_WIDTH = 800;
    private final int SKETCH_HEIGHT = 650;

    private StatusBar statusBar;
    private Thermometer thermometer;
    private Barometer barometer;
    private PVGraph pvGraph;
    private HeatSource heatSource;
    private Cylinder cylinder;
    
    private TransformationStrategy transformationStrategy;

    public boolean isRunning;
    public boolean isPaused;

    @Override
    public void settings() {
        size(SKETCH_WIDTH, SKETCH_HEIGHT);
    }

    @Override
    public void setup() {
        frameRate(60);

        isRunning = false;
        isPaused = false;

        statusBar = new StatusBar(this, 0, 0, 600, 60);
        thermometer = new Thermometer(this, 600, 0, 200, 200);
        barometer = new Barometer(this, 600, 200, 200, 200);
        pvGraph = new PVGraph(this, 500, 400, 300, 250);
        cylinder = new Cylinder(this, 0, 60, 500, 480);
        heatSource = new HeatSource(this, 0, 540, 500, 650);

        cylinder.fillCylinder(55, 0.5f);

        isRunning = true;
    }

    @Override
    public void draw() {
        background(0);
        
        if (isRunning && !isPaused){
            transformationStrategy.updateData();

            statusBar.setData(transformationStrategy.getData());
            cylinder.setVolume(transformationStrategy.getData().get("volume"));
            
            thermometer.setTemperature(transformationStrategy.getData().get("temperature"));
            barometer.setPressure(transformationStrategy.getData().get("pressure"));

            heatSource.setTemperature(transformationStrategy.getData().get("temperature"));
            heatSource.setLosingHeat(transformationStrategy.isLosingHeat());
            heatSource.setAbsorbingHeat(transformationStrategy.isAbsorbingHeat());

            statusBar.update();
            thermometer.update();
            barometer.update();
            pvGraph.update();

            cylinder.update();
            heatSource.update();

            isRunning = !transformationStrategy.IsTheTransformationFinished();
        }else{
            statusBar.draw();
            cylinder.drawParticles();
        }

        drawSketchFragmentsDivisions();
    }


    public void setGasTransformation(HashMap<String, Float> initialData, 
        HashMap<String, Float> finalData, TransformationType transformationType){
        
        switch (transformationType) {
            case ISOBARIC:
                transformationStrategy = new IsobaricTransformation(initialData, finalData);
                break;
            case ISOVOLUMETRIC:
                transformationStrategy = new IsovolumetricTransformation(initialData, finalData);
                break;
            case ISOTHERMAL:
                transformationStrategy = new IsothermalTransformation(initialData, finalData);
                break;
            case ADIABATIC:
                transformationStrategy = new AdiabaticTransformation(initialData, finalData);
                break;
        }
    }

    public void run() {
        String[] processingArgs = { SimulationWorkspace.class.getName() };
        PApplet.runSketch(processingArgs, this);
    }
    
    private void drawSketchFragmentsDivisions(){
        statusBar.drawDivison();
        thermometer.drawDivison();
        barometer.drawDivison();
        pvGraph.drawDivison();
        cylinder.drawDivison();
        heatSource.drawDivison();
    }

}
