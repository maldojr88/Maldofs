package file;

import java.time.LocalDateTime;
import path.MaldoPath;

/**
 * Auxiliary information about a file.
 */
public class MetaData {
  protected LocalDateTime createTime;
  protected LocalDateTime lastModified;
  protected MaldoPath path;
  /* add permissions (owner, group etc) in the future */

  public MetaData(MaldoPath path){
    this.path = path;
    createTime = LocalDateTime.now();
    lastModified = createTime;
  }
}
