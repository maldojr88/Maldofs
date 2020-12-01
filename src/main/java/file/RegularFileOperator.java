package file;

import channel.MaldoFileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import path.MaldoPath;

/**
 * Facilitate operations on regular files
 */
public class RegularFileOperator {

  /* READ operations */

  public RegularFile getRegularFile(MaldoPath path){
    Directory directory = DirectoryRegistry.getFileDirectory(path);
    return directory.getRegularFile(path);
  }

  /* WRITE operations */

  public RegularFile createFile(MaldoPath path, Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) {
    Directory directory = DirectoryRegistry.getFileDirectory(path);
    RegularFile file = new RegularFile(ContentType.STRING, path);
    directory.addFile(file);
    return file;
  }

  public SeekableByteChannel createChannel(RegularFile file) {
    return new MaldoFileChannel(file);
  }
}
