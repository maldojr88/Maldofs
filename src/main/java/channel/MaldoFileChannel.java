package channel;

import static com.google.common.base.Preconditions.checkArgument;

import file.RegularFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SeekableByteChannel;

/**
 * Represents an open connection to a File that is capable of performing one or more distinct
 * I/O operations, for example reading or writing.
 * @see Channel
 */
public class MaldoFileChannel implements SeekableByteChannel {

  private RegularFile file;
  private boolean isOpen;

  public MaldoFileChannel(RegularFile file){
    this.file = file;
    this.isOpen = true;
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    checkOpen();
    dst.put(file.readAll());
    return 0;
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    checkOpen();
    file.append(src);
    return 0;
  }

  @Override
  public long position() throws IOException {
    checkOpen();
    return 0;
  }

  @Override
  public SeekableByteChannel position(long newPosition) throws IOException {
    checkOpen();
    return null;
  }

  @Override
  public long size() throws IOException {
    return 0;
  }

  @Override
  public SeekableByteChannel truncate(long size) throws IOException {
    return null;
  }

  @Override
  public boolean isOpen() {
    return isOpen;
  }

  @Override
  public void close() throws IOException {
    isOpen = false;
  }

  private void checkOpen() {
    checkArgument(isOpen, "Channel is no longer open");
  }
}
