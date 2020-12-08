import static com.google.common.truth.Truth.assertThat;

import client.MaldoFS;
import core.MaldoFileSystem;
import file.Directory;
import file.DirectoryRegistry;
import file.File;
import file.RegularFile;
import file.RegularFileOperator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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

  @Test
  public void copy() throws IOException {
    MaldoPath testDirPath = fs.getPath("/testCopy/");
    Files.createDirectory(testDirPath);

    MaldoPath file1Path = fs.getPath(testDirPath.getCanonical() + "file1");
    Files.createFile(file1Path);
    String fileContent = "fileContent";
    Files.write(file1Path, fileContent.getBytes());
    MaldoPath file2Path = fs.getPath(testDirPath.getCanonical() + "file2");
    Files.copy(file1Path, file2Path);

    RegularFileOperator regularFileOperator = new RegularFileOperator();
    RegularFile file1 = regularFileOperator.getRegularFile(file1Path);
    RegularFile file2 = regularFileOperator.getRegularFile(file2Path);

    assertThat(Arrays.equals(file1.readAll(), file2.readAll())).isTrue();

    MaldoPath testDirCopyPath = fs.getPath("/testCopy2/");
    Files.copy(testDirPath, testDirCopyPath);
    Directory dir = DirectoryRegistry.getDirectory(testDirCopyPath);

    boolean contentIsTheSame = dir.getAllRegularFiles().stream()
        .allMatch(x->Arrays.equals(x.readAll(), fileContent.getBytes()));
    assertThat(contentIsTheSame).isTrue();
  }
}
