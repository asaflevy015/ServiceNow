package private_investigator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUpload {

    public static Set<File> getAllTextFiles() {

        String folderPath = System.getProperty("user.dir") + "\\src\\private_investigator\\files";

        return Stream.of(new File(folderPath).listFiles())
                .filter(file -> !file.isDirectory())
                .filter(file -> file.getName().endsWith(".txt"))
                //.map(File::getName)
                .collect(Collectors.toSet());
    }

    public static List<String> upload(File f){

        List<String> list;

        try {
            list = Files.readAllLines(Paths.get(f.getPath()));
        } catch (IOException e) {
            System.out.println("ERROR - Problem loading sentences file: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return list;
    }
}