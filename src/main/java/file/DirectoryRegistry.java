package file;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;
import path.MaldoPath;

/**
 * Registry of all directories in the FS as well as many of the core operations on directories.
 */
public class DirectoryRegistry {
  private static final Map<MaldoPath,Directory> registry = new HashMap<>();

  private DirectoryRegistry(){}

  public static Directory getDirectoryCreateIfNew(MaldoPath path){
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

  public static Directory getDirectory(MaldoPath path){
    checkArgument(registry.containsKey(path), "Directory does not exist");
    return registry.get(path);
  }

  public static Directory getFileDirectory(MaldoPath path){
    checkArgument(!path.isDirectory(), "Path must be a File");
    MaldoPath dirPath = MaldoPath.convert(path.getParent());
    return getDirectory(dirPath);
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
