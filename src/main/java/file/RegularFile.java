package file;

import java.nio.file.Path;
import storage.StorageInfo;

public class RegularFile extends File {
  private ContentType type;
  private StorageInfo content;

  public RegularFile(ContentType type, Path path){
    super(path);
    this.type = type;
  }

  @Override
  public boolean isDirectory() {
    return false;
  }
}
