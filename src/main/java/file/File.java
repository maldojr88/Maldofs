package file;

import path.MaldoPath;

/**
 * Files are modeled similar to Unix filesystems.
 *
 * Logically, a file is a combination of 1) Metadata 2) Content
 */
public abstract class File {
  protected MetaData metaData;

  public File(MaldoPath path) {
    this.metaData = new MetaData(path);
  }

  public abstract boolean isDirectory();

  public abstract long getByteSize();
}
