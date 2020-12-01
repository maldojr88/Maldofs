import static com.google.common.truth.Truth.assertThat;

import client.MaldoFS;
import core.MaldoFileSystem;
import file.Directory;
import file.DirectoryRegistry;
import file.RegularFile;
import file.RegularFileOperator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import path.MaldoPath;

public class Integration {
  private static final MaldoFileSystem fs = MaldoFS.newFileSystem();

  @Test
  public void basicCreateDirectory() throws IOException {
    Path first = fs.getPath("/a/b/c/");
    Files.createDirectory(first);
    Path second = fs.getPath("/a/b/d/");
    Files.createDirectory(second);

    MaldoPath dir = fs.getPath("/a/b/");
    Directory directory = DirectoryRegistry.getDirectoryCreateIfNew(dir);
    assertThat(directory.getAllPaths()).containsExactly(first,second);
  }

  @Test
  public void basicCreateAndWrite() throws IOException {
    MaldoPath path = fs.getPath("/dummyFile.txt");//create in root as its guaranteed it will exist
    Files.createFile(path);
    RegularFileOperator regularFileOperator = new RegularFileOperator();
    RegularFile regularFile = regularFileOperator.getRegularFile(path);
    MaldoPath regularFilePath = regularFile.getPath();
    assertThat(path == regularFilePath).isTrue();

    Files.write(path, "a".getBytes());
    String contents = new String(regularFile.readAll());
    assertThat(contents.equals("a")).isTrue();
  }
}
