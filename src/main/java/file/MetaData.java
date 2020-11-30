package file;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Auxiliary information about a file
 */
public class MetaData {
  protected LocalDateTime createTime;
  protected LocalDateTime lastModified;
  protected Path path;
  /* add permissions (owner, group etc) in the future */

  public MetaData(Path path){
    this.path = path;
    createTime = LocalDateTime.now();
    lastModified = createTime;
  }



}
