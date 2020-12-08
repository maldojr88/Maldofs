package storage;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Container for storage of binary data
 */
public class StorageContainer {

  public static final int CAPACITY = 1024;
  ByteBuffer buffer;

  public StorageContainer(){
    buffer = ByteBuffer.allocate(CAPACITY);
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

  public byte[] readAll() {
    int size = buffer.position();
    return Arrays.copyOfRange(buffer.array(), 0, size);
  }
}
