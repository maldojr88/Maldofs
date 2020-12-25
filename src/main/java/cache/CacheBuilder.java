package cache;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import file.Directory;
import java.time.Duration;
import java.util.function.Function;
import path.MaldoPath;

/**
 * Builder for {@link Cache}.
 */
public class CacheBuilder {
  private boolean evictOnSize = false;
  private boolean evictOnTime = false;
  private int evictSize = -1;
  private Duration evictTime;
  private Function<MaldoPath, Directory> loader;

  private CacheBuilder(){}

  public static CacheBuilder newBuilder(){
    return new CacheBuilder();
  }

  /*
    Eviction
   */
  public CacheBuilder evictOnSize(int size){
    checkArgument(size > 0, "Eviction size must be greater than zero");
    checkState(!evictOnTime, "Only 1 eviction policy is supported");
    evictOnSize = true;
    evictSize = size;
    return this;
  }

  public CacheBuilder evictOnTime(Duration duration){
    checkState(!evictOnSize, "Only 1 eviction policy is supported");
    evictOnTime = true;
    evictTime = duration;
    return this;
  }

  /*
    Loading
   */

  public CacheBuilder setLoader(Function<MaldoPath, Directory> loader){
    this.loader = loader;
    return this;
  }

  public Cache build(){
    checkState(evictOnTime || evictOnSize, "Must select an eviction policy");
    checkNotNull(loader);
    return evictOnSize
        ? new CacheEvictBySize(loader, evictSize) : new CacheEvictByTime(loader, evictTime);
  }
}
