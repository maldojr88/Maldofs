package cache;

import static com.google.common.truth.Truth.assertThat;

import client.MaldoFS;
import core.MaldoFileSystem;
import file.Directory;
import file.DirectoryRegistry;
import java.util.Optional;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import path.MaldoPath;
import path.PathRegistry;

class CacheEvictBySizeTest {

  private static final MaldoFileSystem fs = MaldoFS.newFileSystem();
  private static final PathRegistry pathRegistry = fs.getPathRegistry();

  @Test
  void get() {
    Function<MaldoPath, @Nullable Directory> loader = DirectoryRegistry::getDirectoryCreateIfNew;
    Cache cache = new CacheEvictBySize(loader, 2);

    MaldoPath path1 = pathRegistry.createPath("/cacheDir1/");
    cache.getPutIfAbsent(path1);
    assertThat(cache.size()).isEqualTo(1);

    MaldoPath path2 = pathRegistry.createPath("/cacheDir2/");
    cache.getPutIfAbsent(path2);
    assertThat(cache.size()).isEqualTo(2);

    MaldoPath path3 = pathRegistry.createPath("/cacheDir3/");
    cache.getPutIfAbsent(path3);
    assertThat(cache.size()).isEqualTo(2);
    assertThat(cache.get(path1)).isEqualTo(Optional.empty());
  }

  @Test
  void put() {
    Function<MaldoPath, @Nullable Directory> loader = DirectoryRegistry::getDirectoryCreateIfNew;
    Cache cache = new CacheEvictBySize(loader, 2);

    MaldoPath path = pathRegistry.createPath("/cachePutDir1/");
    Directory directory = DirectoryRegistry.getDirectoryCreateIfNew(path);
    cache.put(path, directory);
    assertThat(cache.get(path).isPresent()).isTrue();
  }
}