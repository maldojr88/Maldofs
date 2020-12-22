package storage;

import core.MaldoFileStoreAbstract;
import file.ContentType;
import file.RegularFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Container for storage of binary data. Storage Containers are used to store the actual content of
 * a {@link RegularFile}.
 */
public class StorageContainer extends MaldoFileStoreAbstract {

  private static final int INIT_CAPACITY = 100;
  private final ContentType contentType;
  ByteBuffer buffer;

  StorageContainer(ContentType contentType){
    buffer = ByteBuffer.allocate(INIT_CAPACITY);
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
   *  </pre>
   */

  public void append(byte[] toAppend) {
    if(needMoreSpace(toAppend.length)){
      allocateSpace(toAppend.length);
    }
    buffer.put(toAppend);
  }

  private boolean needMoreSpace(int toAdd) {
    return buffer.remaining() - toAdd < 0;
  }

  private void allocateSpace(int lengthToAdd) {
    int expandLength = expandBy(lengthToAdd + buffer.capacity());
    ByteBuffer newBuffer = ByteBuffer.allocate(expandLength);
    newBuffer.put(buffer.array());
    buffer = newBuffer;
  }

  /**
   * Expand by a power of 2
   */
  private int expandBy(int value){
    int highestOneBit = Integer.highestOneBit(value);
    if (value == highestOneBit) {
      return value;
    }
    return highestOneBit << 1;
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
  }

  @Override
  public long getUnallocatedSpace() throws IOException {
    return buffer.remaining();
  }
}
