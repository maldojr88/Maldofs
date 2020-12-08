package core;

import file.Directory;
import file.DirectoryRegistry;
import file.File;
import file.RegularFileOperator;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;
import path.MaldoPath;
import path.PathRegistry;

/**
 * Core functionality of the filesystem bringing everything together
 */
public class MaldoFileSystem extends FileSystem {
  private static Directory rootDir;
  private static Directory currentWorkingDir;
  private final FileSystemProvider provider;
  private final RegularFileOperator regularFileOperator;

  public MaldoFileSystem(){
    rootDir = DirectoryRegistry.getDirectoryCreateIfNew(PathRegistry.createPath(this, "/"));
    currentWorkingDir = rootDir;
    provider = new MaldoFileSystemProvider(this);
    regularFileOperator = new RegularFileOperator();
  }

  public Directory getCurrentWorkingDir(){
    return currentWorkingDir;
  }

  public void setCurrentWorkingDir(Directory dir){
    currentWorkingDir = dir;
  }

  public static MaldoPath getRootDir() {
    return rootDir.getPath();
  }

  public MaldoPath getPath(String path){
    return PathRegistry.createPath(this, path);
  }

  public File getFile(MaldoPath path){
    if(path.isDirectory()){
      return DirectoryRegistry.getDirectory(path);
    }else{
      return regularFileOperator.getRegularFile(path);
    }
  }

  @Override
  public FileSystemProvider provider() {
    return provider;
  }

  @Override
  public void close() throws IOException {

  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public String getSeparator() {
    return null;
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    return null;
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    return null;
    //TODO
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    return null;
  }

  @Override
  public Path getPath(String first, String... more) {
    return null;
  }

  @Override
  public PathMatcher getPathMatcher(String syntaxAndPattern) {
    return null;
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    return null;
  }

  @Override
  public WatchService newWatchService() throws IOException {
    return null;
  }
}
