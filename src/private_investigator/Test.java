package private_investigator;

import java.io.File;
import java.util.Set;

public class Test {

    public static void main(String[] args) {

        FileProcessor fileProcessor = new FileProcessor();

        Set<File> files = new FileUpload().getAllTextFiles();

        for(File file : files){
            fileProcessor.process(file);
        }
    }
}