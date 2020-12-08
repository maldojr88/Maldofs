package channel;

import file.RegularFile;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MaldoOutputStream extends OutputStream {

  private final RegularFile file;

  public MaldoOutputStream(RegularFile file){
    this.file = file;
  }

  @Override
  public void write(int b) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(1);
    byte lowerByte = (byte)(b & 0xFF);
    buffer.put(lowerByte);
    file.append(buffer);
  }
}
