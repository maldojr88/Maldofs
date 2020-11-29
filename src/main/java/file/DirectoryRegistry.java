package file;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import path.MaldoPath;

/**
 * Registry of all directories in the FS. This should be the only place to create a directory
 */
public class DirectoryRegistry {
  private static Map<Path,Directory> registry = new HashMap<>();

  public static Directory getDirectoryCreateIfNew(Path path){
    MaldoPath maldoPath = validate(path);
    if(registry.containsKey(path)){
      return registry.get(path);
    }
    createParentDirectories(maldoPath);
    createDirectory(maldoPath);
    return registry.get(path);
  }

  private static void createParentDirectories(MaldoPath maldoPath) {
    for(Path childPath: maldoPath.getPathChain()){
      if(!registry.containsKey(childPath)){
        createDirectory(maldoPath);
      }
    }
  }

  private static void createDirectory(MaldoPath maldoPath) {
    if(maldoPath.isRoot()){
      registry.put(maldoPath, new Directory(maldoPath));
    }else{
      Directory newDir = new Directory(maldoPath);
      Directory parentDir = getDirectoryCreateIfNew(maldoPath.getParent());
      parentDir.addFile(newDir);
      registry.put(maldoPath, newDir);
    }
  }

  private static MaldoPath validate(Path path) {
    MaldoPath maldoPath = MaldoPath.convert(path);
    checkArgument(maldoPath.isDirectory(),"Path must be a Directory");
    return maldoPath;
  }
}
