package path;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import core.MaldoFileSystem;
import file.Directory;
import file.DirectoryRegistry;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of all the paths known to the FS and their corresponding operations. Paths are stored
 * as singletons, so there will only be 1 object corresponding to a Path. All Path related
 * operations should leverage this class.
 */
public class PathRegistry {
  private final Map<String, MaldoPath> registry = new HashMap<>();
  private final MaldoFileSystem fs;

  public PathRegistry(MaldoFileSystem fs){
    checkArgument(fs.getClass().equals(MaldoFileSystem.class),
        "Path filesystem must be MaldoFileSystem");
    this.fs = fs;
  }

  public MaldoPath createPath(String canonical){
    checkNotNull(canonical);
    checkArgument(!canonical.isBlank() && !canonical.isEmpty(),
        "InputPath must not be empty or blank");
    checkArgument(canonical.startsWith("/"), "MaldoPaths must be absolute");
    if (!registry.containsKey(canonical)){
      registry.put(canonical, new MaldoPath(fs, canonical));
    }

    return registry.get(canonical);
  }

  public static MaldoPath convert(Path path){
    checkNotNull(path);
    checkArgument(path instanceof MaldoPath, "Path must be a MaldoPath");
    return (MaldoPath) path;
  }

  public boolean exists(MaldoPath path){
    return registry.containsKey(path.getCanonical());
  }

  public void remove(MaldoPath path) {
    String canonical = path.getCanonical();
    checkArgument(registry.containsKey(canonical), "Path to remove not found");//TODO - remove from here?
    registry.remove(canonical);
  }

  /**
   * Attempt to get an existing file, if it doesn't exist, get Path for a file that doesn't exist
   */
  public MaldoPath getAbsolutePathSmart(String path) throws IOException {
    MaldoPath existingFilePath = getAbsolutePathExists(path);
    if(existingFilePath != null){
      return existingFilePath;
    }

    return getAbsolutePathNotExists(path, false);
  }

  /**
   * Get the absolute path for a path which should exist
   */
  public MaldoPath getAbsolutePathExists(String path) throws IOException {
    if(path.startsWith("/")){
      return fs.getPath(path);
    }else{
      Directory dir = DirectoryRegistry.getDirectory(fs.getCurrentWorkingDir().getPath());
      return dir.getRelativeNameToPath().get(path);
    }
  }

  /**
   * Get the absolute path for a path which does not exist yet
   */
  public MaldoPath getAbsolutePathNotExists(String path, boolean isDirectory){
    if(path.startsWith("/")){
      if(isDirectory){
        path = dirAppend(path);
      }
      return fs.getPath(path);
    }else{
      String append = isDirectory ? "/" : "";
      return fs.getPath(fs.getCurrentWorkingDir().getPath().getCanonical() + path + append);
    }
  }

  /**
   * If path refers to something local in the current working dir, that takes precedence
   */
  public Optional<MaldoPath> localDirReference(String path){
    Map<String, MaldoPath> relativeNameToPath = fs.getCurrentWorkingDir().getRelativeNameToPath();
    if(relativeNameToPath.containsKey(path)){
      return Optional.of(relativeNameToPath.get(path));
    }
    return Optional.empty();
  }

  public String dirAppend(String str){
    if(!str.endsWith("/")){
      str = str + "/";
    }
    return str;
  }
}
