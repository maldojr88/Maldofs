package file;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import path.MaldoPath;

public class Directory extends File {
  private final Map<MaldoPath, File> content;

  Directory(MaldoPath path) {
    super(path);
    checkArgument(MaldoPath.convert(path).isDirectory(), "Path must be valid");
    content = new HashMap<>();
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  @Override
  public long getByteSize() {
    return content.keySet().stream().mapToLong(x->x.getCanonical().getBytes().length).sum();
  }

  /* READ Operations */

  public MaldoPath getPath(){
    return metaData.path;
  }

  private boolean containsFile(MaldoPath path) {
    return content.containsKey(path);
  }

  public List<MaldoPath> getAllPaths(){
    return new ArrayList<>(content.keySet());
  }

  public RegularFile getRegularFile(String canonical) {
    List<MaldoPath> paths = getAllPaths();
    for (MaldoPath path : paths) {
      if(path.getCanonical().equals(canonical)){
        return (RegularFile) content.get(path);
      }
    }
    checkArgument(false, "File not found " + canonical);
    return null;
  }

  public RegularFile getRegularFile(MaldoPath path){
    MaldoPath regularPath = MaldoPath.convert(path);
    checkArgument(content.containsKey(path), "Directory does not contain " + regularPath.getCanonical());
    File file = content.get(path);
    return (RegularFile) file;
  }

  public List<RegularFile> getAllRegularFiles() {
    return content.values().stream()
        .filter(x-> !x.isDirectory())
        .map(x -> (RegularFile) x)
        .collect(Collectors.toList());
  }

  public boolean contains(MaldoPath filePath) {
    return content.containsKey(filePath);
  }

  /* display related READS */

  public Map<String, MaldoPath> getRelativeNameToPath(){
    Map<String, MaldoPath> ret = new HashMap<>();
    for (Entry<MaldoPath, File> e : content.entrySet()) {
      MaldoPath path = e.getKey();
      ret.put(path.getRelativeName(), path);
    }
    return ret;
  }

  public Set<Entry<MaldoPath, File>> getContent(){
    return content.entrySet();
  }

  public List<String> getPrintableDetailedContents(){
    List<String> ret = new ArrayList<>();
    for (Entry<MaldoPath, File> e : content.entrySet()) {
      MaldoPath path = e.getKey();
      File file = path.getFileSystem().getFile(path);
      DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd hh:mm");
      String str = String.format("%s root root %d %s %s",
          "drwxr-xr-x ",
          file.getByteSize(),
          e.getValue().metaData.lastModified.format(format),
          path.getRelativeName());
      ret.add(str);
    }
    return ret;
  }

  /* WRITE operations */

  public void addFileIfNotExist(File file){
    MaldoPath path = MaldoPath.convert(file.metaData.path);
    if(!containsFile(path)){
      addFile(file);
    }
  }

  public void addFile(File file){
    MaldoPath path = MaldoPath.convert(file.metaData.path);
    String relativeName = path.getRelativeName();
    checkArgument(content.keySet().stream().map(MaldoPath::getRelativeName)
        .noneMatch(x -> x.equals(relativeName)),
        "File already exists " + path.getCanonical());
    content.put(path, file);
  }

  public void remove(MaldoPath path) {
    checkArgument(content.containsKey(path), "Directory doesn't contain file to remove");
    content.remove(path);
  }

  public void resetPath(MaldoPath path) {
    this.metaData.path = path;
  }
}
