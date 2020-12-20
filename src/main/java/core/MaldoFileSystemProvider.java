package core;

import static com.google.common.base.Preconditions.*;

import channel.MaldoOutputStream;
import file.Directory;
import file.DirectoryRegistry;
import file.RegularFile;
import file.RegularFileOperator;
import java.io.IOException;
import java.io.OutputStream;
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
import path.PathRegistry;

public class MaldoFileSystemProvider extends FileSystemProvider {

  private final MaldoFileSystem fs;
  private RegularFileOperator regularFileOperator = new RegularFileOperator();

  public MaldoFileSystemProvider(MaldoFileSystem fs){
    this.fs = fs;
  }

  public Directory getDirectory(MaldoPath path){
    return DirectoryRegistry.getDirectoryCreateIfNew(path);
  }

  public RegularFileOperator getRegularFileOperator(){
    return regularFileOperator;
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
    throw new UnsupportedOperationException("Use MaldoFS.newFileSystem() to create FS");
  }

  @Override
  public FileSystem getFileSystem(URI uri) {
    return fs;
  }

  @Override
  public Path getPath(URI uri) {
    return null;
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) throws IOException {
    RegularFile file = regularFileOperator.createFile(MaldoPath.convert(path), options, attrs);
    return regularFileOperator.createChannel(file);
  }

  @Override
  public OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
    MaldoPath maldoPath = MaldoPath.convert(path);
    RegularFile regularFile = regularFileOperator.getRegularFile(maldoPath);
    return new MaldoOutputStream(regularFile);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
      throws IOException {
    return null;
  }

  @Override
  public void createDirectory(Path dirPath, FileAttribute<?>... attrs) throws IOException {
    DirectoryRegistry.getDirectoryCreateIfNew(MaldoPath.convert(dirPath));
  }

  @Override
  public void delete(Path path) throws IOException {
    //TODO - prevent deleting root
    MaldoPath targetPath = MaldoPath.convert(path);
    Directory dir = DirectoryRegistry.getDirectory(targetPath.getParent());
    dir.remove(targetPath);

    if(targetPath.isDirectory()){
      DirectoryRegistry.remove(targetPath);
    }
  }

  @Override
  public void copy(Path src, Path tgt, CopyOption... options) throws IOException {
    MaldoPath sourcePath = MaldoPath.convert(src);
    MaldoPath targetPath = MaldoPath.convert(tgt);
    validateCopy(sourcePath, targetPath);

    if(sourcePath.isDirectory()){
      //TODO - allow for recursive copy
      checkArgument(!DirectoryRegistry.directoryExists(targetPath), "target already exists");
      DirectoryRegistry.getDirectoryCreateIfNew(targetPath);
      Directory sourceDir = DirectoryRegistry.getDirectory(sourcePath);
      for(RegularFile file : sourceDir.getAllRegularFiles()){
        MaldoPath copyPath = fs.getPath(targetPath.getCanonical() + file.getPath().getRelativeName());
        regularFileOperator.createCopy(copyPath, file);
      }
    } else {
      RegularFile sourceFile = regularFileOperator.getRegularFile(sourcePath);
      regularFileOperator.createCopy(targetPath, sourceFile);
    }
  }

  private void validateCopy(MaldoPath source, MaldoPath target) throws IOException {
    if((source.isDirectory() && !target.isDirectory())
    || !source.isDirectory() && target.isDirectory()){
      throw new IOException("source and target must be the same type of file");
    }
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    MaldoPath sourcePath = MaldoPath.convert(source);
    MaldoPath targetPath = MaldoPath.convert(target);
    validateMove(sourcePath,targetPath);
    if(sourcePath.isDirectory()){
      Directory sourceDir = DirectoryRegistry.getDirectory(sourcePath);
      Directory sourceParentDir = DirectoryRegistry.getDirectory(sourcePath);
      Directory targetParentDir = DirectoryRegistry.getDirectoryCreateIfNew(targetPath.getParent());
      sourceDir.resetPath(targetPath);
      targetParentDir.addFile(sourceDir);
      sourceParentDir.remove(sourcePath);
    }else{
      Directory sourceDir = DirectoryRegistry.getFileDirectory(sourcePath);
      Directory targetDir = DirectoryRegistry.getDirectoryCreateIfNew(targetPath.getParent());
      RegularFile sourceFile = regularFileOperator.getRegularFile(sourcePath);
      regularFileOperator.resetFilePath(sourceFile, targetPath);
      targetDir.addFile(sourceFile);
      sourceDir.remove(sourcePath);
    }
    PathRegistry.remove(sourcePath);
  }

  private void validateMove(MaldoPath sourcePath, MaldoPath targetPath) {
    checkArgument(sourcePath.isDirectory() == targetPath.isDirectory(),
        "Source and Target must be of the same type");
  }

  @Override
  public boolean isSameFile(Path path1, Path path2) throws IOException {
    //since MaldoPath's are singletons, must be the same reference
    return MaldoPath.convert(path1) == MaldoPath.convert(path2);
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
