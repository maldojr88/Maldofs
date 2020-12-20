package file;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import path.MaldoPath;
import storage.StorageContainer;

public class RegularFile extends File {
  private final ContentType contentType;
  private final StorageContainer content;

  private RegularFile(ContentType type, MaldoPath path){
    super(path);
    this.contentType = type;
    content = path.getFileSystem().getStoragePool().createContainer(path,contentType);
  }

  public static RegularFile create(ContentType type, MaldoPath path){
    return new RegularFile(type,path);
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

  public void writeAll(byte[] arr){
    content.truncate();
    append(ByteBuffer.wrap(arr));
  }

  public byte[] readAll(){
    return content.readAll();
  }
}
