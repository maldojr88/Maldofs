package file;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import path.MaldoPath;
import path.PathRegistry;

/**
 * Registry of all directories in the FS. This class also serves as the Factory for creating
 * {@link Directory}.
 */
public class DirectoryRegistry {
  private static final Map<MaldoPath,Directory> registry = new HashMap<>();

  private DirectoryRegistry(){}

  public static Directory getDirectoryCreateIfNew(MaldoPath path){
    checkNotNull(path);
    if(registry.containsKey(path)){
      return registry.get(path);
    }
    createParentDirectoriesIfNotExist(path);
    createDirectoryIfNotExist(path);
    return registry.get(path);
  }

  public static boolean directoryExists(MaldoPath path){
    return registry.containsKey(path);
  }

  public static Directory getDirectory(MaldoPath path) throws IOException {
    checkContains(path);
    return registry.get(path);
  }

  public static Directory getFileDirectory(MaldoPath path) throws IOException {
    checkArgument(!path.isDirectory(), "Path must be a File");
    MaldoPath dirPath = PathRegistry.convert(path.getParent());
    return getDirectory(dirPath);
  }

  public static void remove(MaldoPath path) throws IOException {
    checkContains(path);
    registry.remove(path);
  }

  private static void checkContains(MaldoPath path) throws NoSuchFileException {
    if(!registry.containsKey(path)){
      throw new NoSuchFileException(path.getCanonical());
    }
  }

  private static void createParentDirectoriesIfNotExist(MaldoPath maldoPath) {
    for(MaldoPath childPath: maldoPath.getPathChain()){
      if(!registry.containsKey(childPath)){
        createDirectoryIfNotExist(maldoPath);
      }
    }
  }

  private static void createDirectoryIfNotExist(MaldoPath path) {
    if(path.isRoot()){
      registry.put(path, new Directory(path));
    }else{
      Directory newDir = new Directory(path);
      Directory parentDir = getDirectoryCreateIfNew(path.getParent());
      parentDir.addFileIfNotExist(newDir);
      registry.put(path, newDir);
    }
  }
}
