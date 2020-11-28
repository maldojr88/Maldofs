package file;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import path.MaldoPath;
import storage.StorageInfo;

public class Directory extends File {
  private Map<Path, File> content;

  Directory(Path path) {
    checkArgument(MaldoPath.convert(path).isValidDirectory(), "Path must be valid");
    metaData = new MetaData();
    metaData.path = path;
    metaData.createTime = LocalDateTime.now();
    metaData.lastModified = metaData.createTime;
    content = new HashMap<>();
  }

  public Path getPath(){
    return metaData.path;
  }

  public List<Path> getContents(){
    return new ArrayList<>(content.keySet());
  }

  public void addFile(File file){
    content.put(file.metaData.path, file);
  }
}
