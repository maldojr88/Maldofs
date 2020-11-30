package storage;

import java.nio.ByteBuffer;

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
    return CAPACITY - buffer.remaining();
  }

  public void append(byte[] toAppend){
    buffer.put(toAppend);
  }

  public byte[] readAll() {
    byte[] arr = new byte[buffer.position()];
    buffer.get(arr);
    return arr;
  }
}
