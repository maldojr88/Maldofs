package path;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of all the paths known to the FS
 */
public class PathRegistry {
  private static Map<String, Path> registry = new HashMap<>();

  public static Path createPath(FileSystem fs, String canonical){
    if (!registry.containsKey(canonical)){
      registry.put(canonical, new MaldoPath(fs, canonical));
    }

    return registry.get(canonical);
  }

  public static boolean exists(Path somePath){
    MaldoPath path = validate(somePath);
    return registry.containsKey(path.getCanonical());
  }

  private static MaldoPath validate(Path path) {
    MaldoPath maldoPath = MaldoPath.convert(path);
    checkArgument(maldoPath.isDirectory(),"Path must be a Directory");
    return maldoPath;
  }
}
