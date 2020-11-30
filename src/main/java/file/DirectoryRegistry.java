package file;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import path.MaldoPath;

/**
 * Registry of all directories in the FS as well as many of the core operations on directories.
 */
public class DirectoryRegistry {
  private static Map<Path,Directory> registry = new HashMap<>();

  public static Directory getDirectoryCreateIfNew(Path path){
    MaldoPath maldoPath = validate(path);
    if(registry.containsKey(path)){
      return registry.get(path);
    }
    createParentDirectoriesIfNotExist(maldoPath);
    createDirectoryIfNotExist(maldoPath);
    return registry.get(path);
  }

  /*public boolean directoryExists(Path path){
    return registry.containsKey(validate(path));
  }*/

  public Directory getDirectory(Path path){
    MaldoPath maldoPath = validate(path);
    checkArgument(registry.containsKey(validate(maldoPath)), "Directory does not exist");
    return registry.get(maldoPath);
  }

  public Directory getFileDirectory(Path path){
    MaldoPath filePath = MaldoPath.convert(path);
    checkArgument(!filePath.isDirectory(), "Path must be a File");
    MaldoPath dirPath = MaldoPath.convert(filePath.getParent());
    return getDirectory(dirPath);
  }

  private static void createParentDirectoriesIfNotExist(MaldoPath maldoPath) {
    for(Path childPath: maldoPath.getPathChain()){
      if(!registry.containsKey(childPath)){
        createDirectoryIfNotExist(maldoPath);
      }
    }
  }

  private static void createDirectoryIfNotExist(MaldoPath maldoPath) {
    if(maldoPath.isRoot()){
      registry.put(maldoPath, new Directory(maldoPath));
    }else{
      Directory newDir = new Directory(maldoPath);
      Directory parentDir = getDirectoryCreateIfNew(maldoPath.getParent());
      parentDir.addFileIfNotExist(newDir);
      registry.put(maldoPath, newDir);
    }
  }

  private static MaldoPath validate(Path path) {
    MaldoPath maldoPath = MaldoPath.convert(path);
    checkArgument(maldoPath.isDirectory(),"Path must be a Directory");
    return maldoPath;
  }
}
