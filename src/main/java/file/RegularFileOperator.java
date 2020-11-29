package file;

import channel.MaldoFileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

/**
 * Facilitate operations on regular files
 */
public class RegularFileOperator {

  /* READ operations */

  public RegularFile getRegularFile(Path path){
    DirectoryRegistry registry = new DirectoryRegistry();
    Directory directory = registry.getFileDirectory(path);
    return directory.getRegularFile(path);
  }

  /* WRITE operations */

  public RegularFile createFile(Path path, Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) {
    DirectoryRegistry registry = new DirectoryRegistry();
    Directory directory = registry.getFileDirectory(path);
    RegularFile file = new RegularFile(ContentType.STRING, path);
    directory.addFile(file);
    return file;
  }

  public SeekableByteChannel createChannel(RegularFile file) {
    return new MaldoFileChannel(file);
  }
}
