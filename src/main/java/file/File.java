package file;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Files are modeled similar to Unix filesystems.
 *
 * Logically, a file is a combination of 1) Metadata 2) Content
 */
public abstract class File {
  protected MetaData metaData;

  public File(Path path) {
    metaData = new MetaData();
    metaData.path = path;
    metaData.createTime = LocalDateTime.now();
    metaData.lastModified = metaData.createTime;
  }

  public abstract boolean isDirectory();
}
