import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {

    public static List<String> walk(String path) {
        List<String> result = null;
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            // We want to find only regular files
            result = walk.filter(Files::isRegularFile).map(x -> (x.toString() + "<"+(new File(x.toString()).length())+"<"+(new File(x.toString()).lastModified()))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
