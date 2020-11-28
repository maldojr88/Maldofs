package file;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DirectoryRegistry {
  private static Map<Path,Directory> registry = new HashMap<>();

  public static Directory getDirectory(Path path){
    if(!registry.containsKey(path)){
      registry.put(path, new Directory(path));
    }
    return registry.get(path);
  }
}
