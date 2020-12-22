package file;

import channel.MaldoFileChannel;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Set;
import path.MaldoPath;

/**
 * Factory and Utility class for working with {@link RegularFile}
 */
public class RegularFileUtil {

  /* READ operations */

  public RegularFile getRegularFile(MaldoPath path){
    Directory directory = DirectoryRegistry.getFileDirectory(path);
    return directory.getRegularFile(path);
  }

  /* WRITE operations */

  public RegularFile createFile(MaldoPath path, Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) {
    Directory directory = DirectoryRegistry.getFileDirectory(path);
    RegularFile file = RegularFile.create(ContentType.STRING, path);
    directory.addFile(file);
    return file;
  }

  public void createCopy(MaldoPath target, RegularFile sourceFile) {
    RegularFile copy = createFile(target, new HashSet<>());
    copy.append(ByteBuffer.wrap(sourceFile.readAll()));
  }

  public void resetFilePath(RegularFile regularFile, MaldoPath newPath){
    regularFile.setPath(newPath);
  }

  public SeekableByteChannel createChannel(RegularFile file) {
    return new MaldoFileChannel(file);
  }

}
