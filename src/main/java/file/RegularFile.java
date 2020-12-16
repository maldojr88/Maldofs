package file;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import path.MaldoPath;
import storage.StorageContainer;

public class RegularFile extends File {
  private final ContentType type;
  private final StorageContainer content;

  public RegularFile(ContentType type, MaldoPath path){
    super(path);
    this.type = type;
    content = new StorageContainer();
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public long getByteSize() {
    return content.getSize();
  }

  public MaldoPath getPath(){
    return metaData.path;
  }

  void setPath(MaldoPath newPath){
    this.metaData.path = newPath;
  }

  public void append(ByteBuffer src) {
    content.append(src.array());
    this.metaData.lastModified = LocalDateTime.now();
  }

  public byte[] readAll(){
    return content.readAll();
  }
}
