package core;

import file.Directory;
import file.DirectoryRegistry;
import file.File;
import file.RegularFileUtil;
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
import storage.StoragePool;

/**
 * Core functionality of the filesystem bringing everything together
 */
public class MaldoFileSystem extends FileSystem {
  private Directory rootDir;
  private Directory currentWorkingDir;
  private final MaldoFileSystemProvider provider;
  private final RegularFileUtil regularFileUtil;
  private final PathRegistry pathRegistry;
  private final StoragePool storagePool;

  public MaldoFileSystem(){
    pathRegistry = new PathRegistry(this);
    rootDir = DirectoryRegistry.getDirectoryCreateIfNew(pathRegistry.createPath("/"));
    currentWorkingDir = rootDir;
    provider = new MaldoFileSystemProvider(this);
    regularFileUtil = new RegularFileUtil();
    storagePool = new StoragePool();
  }

  public MaldoFileSystemProvider getProvider(){
    return provider;
  }

  public StoragePool getStoragePool(){
    return storagePool;
  }

  public PathRegistry getPathRegistry(){
    return pathRegistry;
  }

  public Directory getCurrentWorkingDir(){
    return currentWorkingDir;
  }

  public void setCurrentWorkingDir(Directory dir){
    currentWorkingDir = dir;
  }

  public MaldoPath getRootDir() {
    return rootDir.getPath();
  }

  public MaldoPath getPath(String path){
    return pathRegistry.createPath(path);
  }

  public File getFile(MaldoPath path) throws IOException {
    if(path.isDirectory()){
      return DirectoryRegistry.getDirectory(path);
    }else{
      return regularFileUtil.getRegularFile(path);
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
    return storagePool.getAll();
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
