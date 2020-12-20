package core;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public abstract class MaldoFileStoreAbstract extends FileStore {

  @Override
  public String name() {
    return "maldoFileStore";
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
    return false;
  }

  @Override
  public boolean supportsFileAttributeView(String name) {
    return false;
  }

  @Override
  public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
    return null;
  }

  @Override
  public Object getAttribute(String attribute) throws IOException {
    return null;
  }
}
