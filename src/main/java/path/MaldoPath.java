package path;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MaldoPath implements Path {

  private final FileSystem maldoFs;
  private final boolean isDir;
  private boolean isRoot;
  private final String canonical;
  private List<String> splits;
  private List<Path> pathChain = new ArrayList<>();

   MaldoPath(FileSystem fs, String canonical){
    validate(fs, canonical);
    this.canonical = canonical;
    this.isDir = canonical.endsWith("/");
    this.maldoFs = fs;
    constructPathChain();
  }

  /* MaldoPath specific methods/functions */

  public static MaldoPath convert(Path path){
    Objects.requireNonNull(path);
    validateFileSystem(path.getFileSystem());
    checkArgument(path instanceof MaldoPath, "Path must be a MaldoPath");
    return (MaldoPath) path;
  }

  String getCanonical() {
    return canonical;
  }

  /**
   * "/ab/cd/ef/" -> {"/ab/", "/ab/cd/", "/ab/cd/ef/"}
   */
  public List<Path> getPathChain(){
    return pathChain;
  }

  public boolean isValidDirectory(){
    return isDir;
  }

  public boolean isRoot() {
    return isRoot;
  }

  /* Path specific methods/functions */

  @Override
  public FileSystem getFileSystem() {
    return maldoFs;
  }

  @Override
  public boolean isAbsolute() {
    return true;
  }

  @Override
  public Path getRoot() {
    return new MaldoPath(maldoFs, "/");
  }

  @Override
  public Path getFileName() {
    if (isDir) {
      return null;
    }

    return new MaldoPath(this.maldoFs, splits.get(splits.size()-1));
  }

  @Override
  public Path getParent() {
    if (isRoot) {
      return null;
    }

    return pathChain.get(pathChain.size()-1);

  }

  @Override
  public int getNameCount() {
    return splits.size();
  }

  @Override
  public Path getName(int index) {
    return new MaldoPath(this.maldoFs, splits.get(index));
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    return new MaldoPath(this.maldoFs, String.join("/", splits.subList(beginIndex,endIndex)));
  }

  @Override
  public boolean startsWith(Path other) {
    return beginsWith(convert(other));
  }

  @Override
  public boolean endsWith(Path other) {
    return finishesWith(convert(other));
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

  private List<String> getSplits(){
    return splits;
  }

  private void validate(FileSystem fs, String inputPath){
    validateFileSystem(fs);
    validateInputPath(inputPath);
  }

  private void validateInputPath(String inputPath) {
    checkArgument(!inputPath.isBlank() && !inputPath.isEmpty(),
        "InputPath must not be empty or blank");
    checkArgument(inputPath.startsWith("/"), "MaldoPaths must be absolute");
    isRoot = inputPath.length() == 1 && inputPath.equals("/");

    if(isRoot){
      splits = new ArrayList<>();
      return;
    }

    List<String> tempSplit = List.of(inputPath.split("/"));
    splits = tempSplit.subList(1, tempSplit.size());//first one will always be empty

    boolean valid = splits.stream().noneMatch(x -> x.equals(""));
    checkArgument(valid, "Invalid Path{" + inputPath + "}");
  }

  private static void validateFileSystem(FileSystem fs){
    checkArgument(fs.getClass().equals(MaldoFileSystem.class),
        "Path filesystem must be MaldoFileSystem");
  }

  private void constructPathChain() {
    if (!isRoot) {
      StringBuilder sb = new StringBuilder();
      sb.append("/");
      pathChain = new ArrayList<>();
      pathChain.add(MaldoFileSystem.getRootDir());
      for (String split : splits.stream().limit(splits.size() - 1).collect(Collectors.toList())) {
        sb.append(split).append("/");
        pathChain.add(PathRegistry.createPath(maldoFs, sb.toString()));
      }
    }
    //pathChain.add(this);
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
}