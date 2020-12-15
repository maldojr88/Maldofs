package file;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import path.MaldoPath;

public class Directory extends File {
  private final Map<MaldoPath, File> content;

  //doesn't really belong here
  //https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_RESET = "\u001B[0m";

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

  /* display related READS */

  public Map<String, MaldoPath> getRelativeNameToPath(){
    Map<String, MaldoPath> ret = new HashMap<>();
    for (Entry<MaldoPath, File> e : content.entrySet()) {
      MaldoPath path = e.getKey();
      ret.put(path.getRelativeName(), path);
    }
    return ret;
  }

  public List<String> getPrintableSimpleContents(){
    List<String> ret = new ArrayList<>();
    for (Entry<MaldoPath, File> e : content.entrySet()) {
      MaldoPath path = e.getKey();
      String decorator = e.getValue().isDirectory() ? ANSI_BLUE : "";
      ret.add(decorator + path.getRelativeName() + ANSI_RESET);
    }
    return ret;
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
    checkArgument(!content.containsKey(path), "File already exists " + path.getCanonical());
    content.put(path, file);
  }

  public void remove(MaldoPath targetPath) {
    checkArgument(content.containsKey(targetPath), "Directory doesn't contain file to remove");
    content.remove(targetPath);
  }
}
