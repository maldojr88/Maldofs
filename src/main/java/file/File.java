package file;

import path.MaldoPath;

/**
 * Files are modeled similar to files in Unix filesystems. Everything storable on the filesystem
 * is a subclass of a file.
 */
public abstract class File {
  protected MetaData metaData;

  public File(MaldoPath path) {
    this.metaData = new MetaData(path);
  }

  public abstract boolean isDirectory();

  public abstract long getByteSize();
}
