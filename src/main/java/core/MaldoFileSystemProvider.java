package core;

import file.Directory;
import file.DirectoryRegistry;
import file.RegularFile;
import file.RegularFileOperator;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import path.MaldoPath;

public class MaldoFileSystemProvider extends FileSystemProvider {

  private final MaldoFileSystem fileSystem;
  private RegularFileOperator regularFileOperator = new RegularFileOperator();

  public MaldoFileSystemProvider(MaldoFileSystem fileSystem){
    this.fileSystem = fileSystem;
  }

  public Directory getDirectory(MaldoPath path){
    return DirectoryRegistry.getDirectoryCreateIfNew(path);
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
    return null;
  }

  @Override
  public FileSystem getFileSystem(URI uri) {
    return null;
  }

  @Override
  public Path getPath(URI uri) {
    return null;
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) throws IOException {
    RegularFile file = regularFileOperator.createFile(path, options, attrs);
    return regularFileOperator.createChannel(file);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
      throws IOException {
    return null;
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    DirectoryRegistry.getDirectoryCreateIfNew(dir);
  }

  @Override
  public void delete(Path path) throws IOException {

  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {

  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {

  }

  @Override
  public boolean isSameFile(Path path, Path path2) throws IOException {
    return false;
  }

  @Override
  public boolean isHidden(Path path) throws IOException {
    return false;
  }

  @Override
  public FileStore getFileStore(Path path) throws IOException {
    return null;
  }

  @Override
  public void checkAccess(Path path, AccessMode... modes) throws IOException {

  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type,
      LinkOption... options) {
    return null;
  }

  @Override
  public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type,
      LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
      throws IOException {
    return null;
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options)
      throws IOException {

  }
}
