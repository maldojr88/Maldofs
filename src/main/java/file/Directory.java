package file;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import storage.StorageInfo;

public class Directory extends File {
  private Map<Path, File> content;

  public Directory(Path path) {
    metaData.path = path;
    metaData.createTime = LocalDateTime.now();
    metaData.lastModified = metaData.createTime;
    content = new HashMap<>();
  }

  public List<Path> getContents(){
    return new ArrayList<>(content.keySet());
  }

  public void addFile(File file){
    content.put(file.metaData.path, file);
  }
}
