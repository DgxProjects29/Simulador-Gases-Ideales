package inevaup.resources;
import inevaup.preferences.AppSettings;

//Json Simple dependency: https://cliftonlabs.github.io/json-simple/target/json-simple-3.1.1.jar

public class Main {
    
    public static void main(String[] args) {

        //Run this class to autogenerated the R class
        AppSettings settings = AppSettings.getSettings();
        settings.loadSettings();
        
        AppResources appResources = AppResources.getResources();
        appResources.loadResources();

        if(appResources.AreAllResourcesLoaded()){
            ClassBuilder classBuilder = new ClassBuilder("inevaup.resources", "R", "src/inevaup/resources/R.java");
            classBuilder.generateClass();
        }else{
            System.out.println(appResources.getErrorMessage());
        }

    }
}
