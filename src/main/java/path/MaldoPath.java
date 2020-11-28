package path;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MaldoPath implements Path {

  private final FileSystem malodFs;
  private final boolean isDir;
  private final boolean isAbsolute;
  private boolean isRoot;
  private final String inputPath;
  private List<String> splits;

  public MaldoPath(FileSystem fs, String inputPath){
    checkArgument(fs.getClass() == MaldoFileSystem.class);
    this.inputPath = inputPath;
    this.isDir = inputPath.endsWith("/");
    this.isAbsolute = inputPath.startsWith("/");
    validate(inputPath);
    this.malodFs = fs;
  }

  @Override
  public FileSystem getFileSystem() {
    return malodFs;
  }

  @Override
  public boolean isAbsolute() {
    return isAbsolute;
  }

  @Override
  public Path getRoot() {
    return new MaldoPath(malodFs, "/");
  }

  @Override
  public Path getFileName() {
    if (isDir) {
      return null;
    }

    return new MaldoPath(this.malodFs, splits.get(splits.size()-1));
  }

  @Override
  public Path getParent() {
    if (!isAbsolute || isRoot) {
      return null;
    }

    return new MaldoPath(this.malodFs,
        String.join("/", splits.stream().limit(splits.size()-1)
            .collect(Collectors.toList())));
  }

  @Override
  public int getNameCount() {
    return splits.size();
  }

  @Override
  public Path getName(int index) {
    return new MaldoPath(this.malodFs, splits.get(index));
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    return new MaldoPath(this.malodFs, String.join("/", splits.subList(beginIndex,endIndex)));
  }

  @Override
  public boolean startsWith(Path other) {
    return beginsWith(validatePath(other));
  }

  @Override
  public boolean endsWith(Path other) {
    return finishesWith(validatePath(other));
  }

  @Override
  public Path normalize() {
    return null;
  }

  @Override
  public Path resolve(Path other) {
    return null;
  }

  @Override
  public Path relativize(Path other) {
    return null;
  }

  @Override
  public URI toUri() {
    return null;
  }

  @Override
  public Path toAbsolutePath() {
    return null;
  }

  @Override
  public Path toRealPath(LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
      throws IOException {
    return null;
  }

  @Override
  public int compareTo(Path other) {
    return 0;
  }

  public List<String> getSplits(){
    return splits;
  }

  private boolean finishesWith(MaldoPath path) {
    if(path.getSplits().size() > splits.size()){
      return false;
    }
    return path.getSplits().equals(splits.subList(splits.size() - path.splits.size(), splits.size()));
  }

  private boolean beginsWith(MaldoPath path){
    if(path.getSplits().size() > splits.size()){
      return false;
    }
    return path.getSplits().equals(splits.subList(0, path.getSplits().size()));
  }

  private MaldoPath validatePath(Path path){
    Objects.requireNonNull(path);
    if(!path.getFileSystem().equals(malodFs) || !(path instanceof MaldoPath)){
      throw new IllegalArgumentException("Path should be a MaldoPath");
    }
    return (MaldoPath) path;
  }

  private void validate(String inputPath) {
    boolean invalid = false;
    this.splits = List.of(inputPath.split("/"));
    this.isRoot = inputPath.length() == 1 && inputPath.equals("/");

    for(int i=0; i < splits.size(); i++){
      String current = splits.get(i);
      if(current.equals("") && i != 0){
        invalid = true;
        break;
      }
    }

    if(invalid){
      throw new IllegalArgumentException("Invalid Path{" + inputPath + "}");
    }
  }
}
