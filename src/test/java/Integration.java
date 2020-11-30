import static com.google.common.truth.Truth.assertThat;

import client.MaldoFS;
import core.MaldoFileSystem;
import file.Directory;
import file.DirectoryRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class Integration {

  @Test
  public void basicCreateDirectory() throws IOException {
    MaldoFileSystem fs = MaldoFS.newFileSystem();
    Path first = fs.getPath("/a/b/c/");
    Files.createDirectory(first);
    Path second = fs.getPath("/a/b/d/");
    Files.createDirectory(second);

    Path dir = fs.getPath("/a/b/");
    Directory directory = DirectoryRegistry.getDirectoryCreateIfNew(dir);
    assertThat(directory.getAllPaths()).containsExactly(first,second);
  }

  @Test
  public void basicCreateFile() throws IOException {
    MaldoFileSystem fs = MaldoFS.newFileSystem();
    Path file = fs.getPath("/dummyFile.txt");//create in root as its guaranteed it will exist
    Files.createFile(file);
  }
}
