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
    ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.putInt(b);
    file.append(buffer);
  }
}
