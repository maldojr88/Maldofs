package file;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import storage.StorageContainer;

public class RegularFile extends File {
  private ContentType type;
  private StorageContainer content;

  public RegularFile(ContentType type, Path path){
    super(path);
    this.type = type;
    content = new StorageContainer();
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  public void append(ByteBuffer src) {
    content.append(src.array());
  }

  public byte[] readAll(){
    return content.readAll();
  }
}
