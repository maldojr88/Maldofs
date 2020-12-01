package file;

import java.nio.ByteBuffer;
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

  public MaldoPath getPath(){
    return metaData.path;
  }

  public void append(ByteBuffer src) {
    content.append(src.array());
  }

  public byte[] readAll(){
    return content.readAll();
  }
}
