package core;

import static com.google.common.base.Preconditions.*;

import channel.MaldoOutputStream;
import file.Directory;
import file.DirectoryRegistry;
import file.RegularFile;
import file.RegularFileUtil;
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

/**
 * {@link FileSystemProvider} implementation for MaldoFS.
 */
public class MaldoFileSystemProvider extends FileSystemProvider {

  private final MaldoFileSystem fs;
  private final RegularFileUtil regularFileUtil = new RegularFileUtil();
  private final PathRegistry pathRegistry;

  public MaldoFileSystemProvider(MaldoFileSystem fs){
    this.fs = fs;
    this.pathRegistry = fs.getPathRegistry();
  }

  public Directory getDirectory(MaldoPath path){
    return DirectoryRegistry.getDirectoryCreateIfNew(path);
  }

  public RegularFileUtil getRegularFileUtil(){
    return regularFileUtil;
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) {
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
    RegularFile file = regularFileUtil.createFile(PathRegistry.convert(path), options, attrs);
    return regularFileUtil.createChannel(file);
  }

  @Override
  public OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
    MaldoPath maldoPath = PathRegistry.convert(path);
    RegularFile regularFile = regularFileUtil.getRegularFile(maldoPath);
    return new MaldoOutputStream(regularFile);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) {
    return null;
  }

  @Override
  public void createDirectory(Path dirPath, FileAttribute<?>... attrs) {
    DirectoryRegistry.getDirectoryCreateIfNew(PathRegistry.convert(dirPath));
  }

  @Override
  public void delete(Path path) throws IOException {
    MaldoPath targetPath = PathRegistry.convert(path);
    preventRootDeletion(targetPath);
    Directory parentDir = DirectoryRegistry.getDirectory(targetPath.getParent());
    parentDir.remove(targetPath);

    if(targetPath.isDirectory()){
      DirectoryRegistry.remove(targetPath);
    }
  }

  private void preventRootDeletion(MaldoPath targetPath) throws IOException {
    if(targetPath.getCanonical().equals("/")){
      throw new IOException("Deleting root directory is not allowed");
    }
  }

  @Override
  //TODO - Add support for recursive copies
  public void copy(Path src, Path tgt, CopyOption... options) throws IOException {
    MaldoPath sourcePath = PathRegistry.convert(src);
    MaldoPath targetPath = PathRegistry.convert(tgt);
    validateCopy(sourcePath, targetPath);

    if(sourcePath.isDirectory()){
      checkArgument(!DirectoryRegistry.directoryExists(targetPath), "target already exists");
      DirectoryRegistry.getDirectoryCreateIfNew(targetPath);
      Directory sourceDir = DirectoryRegistry.getDirectory(sourcePath);
      for(RegularFile file : sourceDir.getAllRegularFiles()){
        MaldoPath copyPath = fs.getPath(targetPath.getCanonical() + file.getPath().getRelativeName());
        regularFileUtil.createCopy(copyPath, file);
      }
    } else {
      RegularFile sourceFile = regularFileUtil.getRegularFile(sourcePath);
      regularFileUtil.createCopy(targetPath, sourceFile);
    }
  }

  private void validateCopy(MaldoPath source, MaldoPath target) throws IOException {
    if((source.isDirectory() && !target.isDirectory())
    || (!source.isDirectory() && target.isDirectory())){
      throw new IOException("source and target must be the same type of file");
    }
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    MaldoPath sourcePath = PathRegistry.convert(source);
    MaldoPath targetPath = PathRegistry.convert(target);
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
      RegularFile sourceFile = regularFileUtil.getRegularFile(sourcePath);
      regularFileUtil.resetFilePath(sourceFile, targetPath);
      targetDir.addFile(sourceFile);
      sourceDir.remove(sourcePath);
    }
    pathRegistry.remove(sourcePath);
  }

  private void validateMove(MaldoPath sourcePath, MaldoPath targetPath) {
    checkArgument(sourcePath.isDirectory() == targetPath.isDirectory(),
        "Source and Target must be of the same type");
  }

  @Override
  public boolean isSameFile(Path path1, Path path2) {
    //since MaldoPath's are singletons, must be the same reference
    return PathRegistry.convert(path1) == PathRegistry.convert(path2);
  }

  @Override
  public boolean isHidden(Path path) {
    return false;
  }

  @Override
  public FileStore getFileStore(Path path) {
    return fs.getStoragePool().getContainer(PathRegistry.convert(path));
  }

  @Override
  public void checkAccess(Path path, AccessMode... modes) {

  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type,
      LinkOption... options) {
    return null;
  }

  @Override
  public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type,
      LinkOption... options) {
    return null;
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) {
    return null;
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options) {

  }
}
