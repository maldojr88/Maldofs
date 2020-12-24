package path;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import java.io.IOException;
import java.net.URI;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link Path} for MaldoFS
 */
public class MaldoPath implements Path {

  private final MaldoFileSystem maldoFs;
  private final boolean isDir;
  private boolean isRoot;
  private final String canonical;
  private List<String> splits;
  private List<MaldoPath> pathChain;

   MaldoPath(MaldoFileSystem fs, String canonical){
    this.canonical = canonical;
    this.isDir = canonical.endsWith("/");
    this.maldoFs = fs;
    splitCanonical(canonical);
    constructPathChain();
  }

  /* MaldoPath specific methods/functions */

  public String getCanonical() {
    return canonical;
  }

  /**
   * "/ab/cd/ef/" -> {"/ab/", "/ab/cd/", "/ab/cd/ef/"}
   */
  public List<MaldoPath> getPathChain(){
    return pathChain;
  }

  public boolean isDirectory(){
    return isDir;
  }

  /**
   * RegularFile  - "/a/b/c" -> "c"
   * Directory    - "/a/b/c/ - "c"
   */
  public String getRelativeName() {
    int start = canonical.length() - 1;
    if(isDir){
      start--;
    }
    int end = start;
    while(start > 0){
      if(canonical.charAt(start) == '/'){
        break;
      }
      start--;
    }
    return canonical.substring(start + 1, end + 1);
  }

  public boolean isRoot() {
    return isRoot;
  }

  /* Path specific methods/functions */

  @Override
  public MaldoFileSystem getFileSystem() {
    return maldoFs;
  }

  @Override
  public boolean isAbsolute() {
    return true;
  }

  @Override
  public Path getRoot() {
    return new MaldoPath(maldoFs, "/");//TODO fix!! - should not create new instance
  }

  @Override
  public Path getFileName() {
    if (isDir) {
      return null;
    }

    return new MaldoPath(this.maldoFs, splits.get(splits.size()-1));//TODO - shouldnt create new instance?
  }

  @Override
  public MaldoPath getParent() {
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
    return new MaldoPath(this.maldoFs, splits.get(index));//TODO - should not create new instance
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    //TODO - should not create new instance
    return new MaldoPath(this.maldoFs, String.join("/", splits.subList(beginIndex,endIndex)));
  }

  @Override
  public boolean startsWith(Path other) {
    return beginsWith(PathRegistry.convert(other));
  }

  @Override
  public boolean endsWith(Path other) {
    return finishesWith(PathRegistry.convert(other));
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

  private void splitCanonical(String inputPath){
    isRoot = inputPath.length() == 1 && inputPath.equals("/");

    if(isRoot){
      splits = new ArrayList<>();
      return;
    }

    List<String> tempSplit = List.of(inputPath.split("/"));
    splits = tempSplit.subList(1, tempSplit.size());//first one will always be empty

    boolean valid = splits.stream().noneMatch(x -> x.equals(""));
    checkArgument(valid, "Invalid Path{" + inputPath + "}");//TODO - shouldn't use this here
  }

  private void constructPathChain() {
    pathChain = new ArrayList<>();
    if (!isRoot) {
      StringBuilder sb = new StringBuilder();
      sb.append("/");
      pathChain.add(maldoFs.getRootDir());
      for (String split : splits.stream().limit(splits.size() - 1).collect(Collectors.toList())) {
        sb.append(split).append("/");
        pathChain.add(maldoFs.getPathRegistry().createPath(sb.toString()));
      }
    }
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
