package cache;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LRUStore<K, V> extends LinkedHashMap<K,V> {

  public static final int DEFAULT_DURATION_SIZE = 1000;
  private final int maxSize;
  private final boolean sizeBased;
  private Duration duration;
  private LocalDateTime lastAccess;

  private LRUStore(int maxSize, Duration duration){
    super(maxSize, 1.0f, true);
    this.maxSize = maxSize;
    this.sizeBased = true;
    this.duration = duration;
    this.lastAccess = LocalDateTime.now();
  }

  /**
   * An LRU store which evicts based on the maxSize.
   */
  public static <K, V> LRUStore<K, V> newSizeBased(int maxSize){
    return new LRUStore<>(maxSize, null);
  }

  /**
   * An LRU store which does not evict.
   */
  public static <K, V> LRUStore<K, V> newNoEvict(Duration duration){
    return new LRUStore<>(DEFAULT_DURATION_SIZE, duration);
  }

  @Override
  public V get(Object key){
    V val = super.get(key);
    lastAccess = LocalDateTime.now();
    return  val;
  }

  @Override
  public V put(K key, V value) {
    super.put(key, value);
    lastAccess = LocalDateTime.now();
    return value;
  }

  @Override
  protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
    return sizeBased && (super.size() > maxSize);
  }

  public void maintenance(){
    if(!sizeBased){
      while(true){
        Entry<K, V> eldest = entrySet().iterator().next();
      }
    }
  }
}
