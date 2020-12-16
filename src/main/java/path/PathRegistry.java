package path;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of all the paths known to the FS
 */
public class PathRegistry {
  private static final Map<String, MaldoPath> REGISTRY = new HashMap<>();

  public static MaldoPath createPath(MaldoFileSystem fs, String canonical){
    if (!REGISTRY.containsKey(canonical)){
      REGISTRY.put(canonical, new MaldoPath(fs, canonical));
    }

    return REGISTRY.get(canonical);
  }

  public static MaldoPath get(String canonical){
    checkArgument(REGISTRY.containsKey(canonical), "Path not found in registry");
    return REGISTRY.get(canonical);
  }

  public static boolean exists(MaldoPath path){
    return REGISTRY.containsKey(path.getCanonical());
  }

  private static void ensureIsDirectoryPath(MaldoPath path) {
    checkArgument(path.isDirectory(),"Path must be a Directory");
  }

  public static void remove(MaldoPath path) {
    String canonical = path.getCanonical();
    checkArgument(REGISTRY.containsKey(canonical), "Path to remove not found");
    REGISTRY.remove(canonical);
  }
}
