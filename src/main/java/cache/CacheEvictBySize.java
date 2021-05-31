package cache;


import file.Directory;
import java.util.Optional;
import java.util.function.Function;
import path.MaldoPath;

/**
 * As a learning exercise, decided to implement a cache after reading about Guava <a
 * href="https://github.com/google/guava/wiki/CachesExplained">caches</a>. There is no other reason
 * to implement this other than for learning purposes. Read the linked document and brainstormed
 * how to implement. Then compared my implementation to what Guava has.
 */
public class CacheEvictBySize extends Cache {
  //LRU for now
  //Later add LFU capability

  private final LRUStore<MaldoPath,Directory> store;

  public CacheEvictBySize(Function<MaldoPath, Directory> loader, int evictSize){
    this.loader = loader;
    this.store = LRUStore.newSizeBased(evictSize);
  }

  @Override
  public Directory getPutIfAbsent(MaldoPath key) {
    if(!store.containsKey(key)){
      put(key, loader.apply(key));
    }
    return store.get(key);
  }

  @Override
  public Optional<Directory> get(MaldoPath key){
    if(!store.containsKey(key)){
      return Optional.empty();
    }else {
      return Optional.of(store.get(key));
    }
  }

  @Override
  public void put(MaldoPath key, Directory value) {
    store.put(key, value);
  }

  @Override
  public int size() {
    return store.size();
  }

  @Override
  public void maintenance() {
    //Maintenance performed automatically by LRUStore's LinkedHashMap
  }
}
