package inevaup.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Constructor de clase R dependiendo de los recursos que se encuentren en la carpeta "Data".  
 */
public class ClassBuilder {

    private final String className;
    private final String packageName;
    private final String classPath;

    private String generatedClassText;
    private final AppResources appResources;

    /**
     * Construye la clase con la ruta, nombre del paquetes y el nombre de la clase
     * 
     * @param packageName Nombre del paquete
     * @param className Nombre de la clase 
     * @param classPath Ruta de la clase
     */
    public ClassBuilder(String packageName, String className, String classPath) {
        this.className = className;
        this.classPath = classPath;
        this.packageName = packageName;

        appResources = AppResources.getResources();
        generatedClassText = "";
    }

    /**
     * Genera la clase R
     */
    public void generateClass(){

        try {
            generateText();
            writeTextToClass();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void writeTextToClass() throws IOException {

        FileWriter fileWriter = new FileWriter(classPath);
        fileWriter.write(generatedClassText);
        fileWriter.close();

    }

    private void generateText(){

        String comment = "// This class is auto generated";
        String base = 
            String.format("package %s;\n\n%s\n\npublic class %s {\n\n", packageName, comment , className);
        
        String stringEnum = createEnum("strings ", appResources.getStringsJsonObject().keySet());
        String colorEnum = createEnum("colors ", appResources.getThemeJsonObject().keySet());
        String fontsEnum = createEnum("fonts", appResources.getCustomFonts().keySet());

        String imagesClass = createStaticResource("images", new File(ResourcesPath.getFullImagesPath()));
        String iconsClass = createStaticResource("icons", new File(ResourcesPath.getFullIconsPath()));
        String soundsClass = createStaticResource("sounds", new File(ResourcesPath.getFullSoundsPath()));
        
        String close = "\n}"; 

        generatedClassText = 
            base + stringEnum + colorEnum + fontsEnum +
            imagesClass + iconsClass + soundsClass + close;

    }

    private String createEnum(String enumName, Set<String> keySet){

        String start = "\t" + String.format("public enum %s{\n", enumName);
        String enumContent = "";

        String nextLine = ",\n";

        Iterator<String> keySetIterator = keySet.iterator();
        while (keySetIterator.hasNext()) {
            enumContent += "\t\t" + keySetIterator.next();

            if (keySetIterator.hasNext()){
                enumContent += nextLine;
            }
        }

        String close = "\n\t}\n\n"; 
        
        return start + enumContent + close;
    }

    private String createStaticResource(String className, File resourceFile){

        String start = "\t" + String.format("public static class %s {\n", className);
        String classContent = "";
        
        File[] resourcefiles = resourceFile.listFiles();
        for (File resourcefile : resourcefiles) {
            String fileName = fromFontNameToJavaName(resourcefile.getName());
            String filePath = "/" + resourcefile.getName();

            classContent += "\t\t" + generateRField(fileName, filePath);
        }

        String close = "\n\t}\n\n"; 

        return start + classContent + close;
    }


    private String fromFontNameToJavaName(String fontName) {

        String fontNamePhase1 = fontName.toLowerCase().replace('-', '_');
        return fontNamePhase1.substring(0, fontNamePhase1.indexOf('.'));
    }

    private String generateRField(String name, String content){

        return String.format("public final static String %s = \"%s\";\n", name, content);
        
    }
    
    

}
