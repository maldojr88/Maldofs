package storage;

import file.ContentType;
import java.nio.file.FileStore;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import path.MaldoPath;

/**
 * Collection of {@link StorageContainer} which effectively store all the data for the files
 * on the Filesystem
 */
public class StoragePool {
  private static final Map<MaldoPath, StorageContainer> POOL = new HashMap<>();

  public StorageContainer createContainer(MaldoPath path, ContentType contentType){
    POOL.putIfAbsent(path, new StorageContainer(contentType));
    return POOL.get(path);
  }

  public StorageContainer getContainer(MaldoPath path){
    return POOL.get(path);
  }

  public Iterable<FileStore> getAll() {
    Collection<StorageContainer> values = POOL.values();
    return (Collection<FileStore>) (List<? extends FileStore>) values;
  }
}
