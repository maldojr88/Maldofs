package file;

import storage.StorageInfo;

public class RegularFile extends File {
  private ContentType type;
  private StorageInfo content;

  @Override
  public boolean isDirectory() {
    return false;
  }
}
