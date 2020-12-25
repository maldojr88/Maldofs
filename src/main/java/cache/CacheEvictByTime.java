package cache;

import file.Directory;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import path.MaldoPath;

public class CacheEvictByTime extends Cache{

  private final Duration duration;

  public CacheEvictByTime(Function<MaldoPath, Directory> loader, Duration duration){
    this.loader = loader;
    this.duration = duration;
  }

  @Override
  public Optional<Directory> get(MaldoPath key) {
    return Optional.empty();
  }

  @Override
  public Directory getPutIfAbsent(MaldoPath key) {
    return null;
  }

  @Override
  public void put(MaldoPath key, Directory value) {

  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public void maintenance() {

  }
}
