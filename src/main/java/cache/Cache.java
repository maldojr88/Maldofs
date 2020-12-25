package cache;

import file.Directory;
import java.util.Optional;
import java.util.function.Function;
import path.MaldoPath;

/**
 * Dictates how elements are removed from the Cache.
 */
public abstract class Cache  {

  protected Function<MaldoPath, Directory> loader;

  public abstract Optional<Directory> get(MaldoPath key);

  public abstract Directory getPutIfAbsent(MaldoPath key);

  public abstract void put(MaldoPath key, Directory value);

  public abstract int size();

  public abstract void maintenance();
}
