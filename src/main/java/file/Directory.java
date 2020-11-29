package file;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import path.MaldoPath;

public class Directory extends File {
  private Map<Path, File> content;

  //doesn't really belong here
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_RESET = "\u001B[0m";
  //https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

  Directory(Path path) {
    checkArgument(MaldoPath.convert(path).isDirectory(), "Path must be valid");
    metaData = new MetaData();
    metaData.path = path;
    metaData.createTime = LocalDateTime.now();
    metaData.lastModified = metaData.createTime;
    content = new HashMap<>();
  }

  public Path getPath(){
    return metaData.path;
  }

  public List<Path> getContents(){
    return new ArrayList<>(content.keySet());
  }

  public Map<String, MaldoPath> getRelativeNameToPath(){
    Map<String, MaldoPath> ret = new HashMap<>();
    for (Entry<Path, File> e : content.entrySet()) {
      MaldoPath path = (MaldoPath) e.getKey();
      ret.put(path.getRelativeName(), path);
    }
    return ret;
  }

  public List<String> getPrintableSimpleContents(){
    List<String> ret = new ArrayList<>();
    for (Entry<Path, File> e : content.entrySet()) {
      MaldoPath path = (MaldoPath) e.getKey();
      String decorator = e.getValue().isDirectory() ? ANSI_BLUE : "";
      ret.add(decorator + path.getRelativeName() + ANSI_RESET);
    }
    return ret;
  }

  public List<String> getPrintableDetailedContents(){
    List<String> ret = new ArrayList<>();
    for (Entry<Path, File> e : content.entrySet()) {
      MaldoPath path = (MaldoPath) e.getKey();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd hh:mm");
      String str = String.format("%s root root 52 %s %s",
          "drwxr-xr-x ", e.getValue().metaData.lastModified.format(format), path.getRelativeName());
      ret.add(str);
    }
    return ret;
  }

  public void addFile(File file){
    content.put(file.metaData.path, file);
  }

  @Override
  public boolean isDirectory() {
    return true;
  }
}
