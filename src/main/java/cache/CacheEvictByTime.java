package cache;

import file.Directory;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import path.MaldoPath;

public class CacheEvictByTime extends Cache{
  private final LRUStore<MaldoPath,Directory> store;
  private final Duration duration;

  public CacheEvictByTime(Function<MaldoPath, Directory> loader, Duration duration){
    this.loader = loader;
    this.duration = duration;
    this.store = LRUStore.newNoEvict(duration);
  }

  @Override
  public Optional<Directory> get(MaldoPath key) {
    if(!store.containsKey(key)){
      return Optional.empty();
    }else {
      Directory value = store.get(key);
      maintenance();
      return Optional.of(value);
    }
  }

  @Override
  public Directory getPutIfAbsent(MaldoPath key) {
    if(!store.containsKey(key)){
      put(key, loader.apply(key));
    }
    return store.get(key);
  }

  @Override
  public void put(MaldoPath key, Directory value) {
    store.put(key, value);
    maintenance();
  }

  @Override
  public int size() {
    return store.size();
  }

  @Override
  public void maintenance() {

  }
}
