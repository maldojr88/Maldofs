package cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUStore<K, V> extends LinkedHashMap<K,V> {
  private final int maxSize;

  public LRUStore(int maxSize){
    super(maxSize, 1.0f, true);
    this.maxSize = maxSize;
  }

  @Override
  protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
    return super.size() > maxSize;
  }
}
