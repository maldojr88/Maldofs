package path;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.FileSystem;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of all the paths known to the FS
 */
public class PathRegistry {
  private static final Map<String, MaldoPath> registry = new HashMap<>();

  public static MaldoPath createPath(FileSystem fs, String canonical){
    if (!registry.containsKey(canonical)){
      registry.put(canonical, new MaldoPath(fs, canonical));
    }

    return registry.get(canonical);
  }

  public static MaldoPath get(String canonical){
    checkArgument(registry.containsKey(canonical), "Path not found in registry");
    return registry.get(canonical);
  }

  public static boolean exists(MaldoPath path){
    ensureIsDirectoryPath(path);
    return registry.containsKey(path.getCanonical());
  }

  private static void ensureIsDirectoryPath(MaldoPath path) {
    checkArgument(path.isDirectory(),"Path must be a Directory");
  }
}
