package storage;

import core.MaldoFileStoreAbstract;
import file.ContentType;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Container for storage of binary data
 */
public class StorageContainer extends MaldoFileStoreAbstract {

  private static final int CAPACITY = 1024;
  private final ContentType contentType;
  ByteBuffer buffer;

  StorageContainer(ContentType contentType){
    buffer = ByteBuffer.allocate(CAPACITY);
    this.contentType = contentType;
  }

  public int getSize(){
    return buffer.position();
  }

  /**
   * Example:
   *
   * <pre>
   *   String k = "abc";
   *   ByteBuffer b = ByteBuffer.wrap(k.getBytes());
   *   String v = newString(b.array());
   *   v.equals(k); //true
   *   </pre>
   */

  public void append(byte[] toAppend) {
    buffer.put(toAppend);
  }

  public void truncate(){
    buffer.clear();
  }

  public byte[] readAll() {
    int size = buffer.position();
    return Arrays.copyOfRange(buffer.array(), 0, size);
  }

  @Override
  public String type() {
    return this.contentType.name();
  }

  @Override
  public long getTotalSpace() throws IOException {
    return buffer.position();
  }

  @Override
  public long getUsableSpace() throws IOException {
    return 0;
    //TODO
  }

  @Override
  public long getUnallocatedSpace() throws IOException {
    return CAPACITY - buffer.position();
  }
}
