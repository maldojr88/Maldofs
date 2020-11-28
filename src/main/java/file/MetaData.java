package file;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Auxiliary information about a file
 */
public class MetaData {
  protected LocalDateTime createTime;
  protected LocalDateTime lastModified;
  /* add permissions (owner, group etc) in the future */

  protected Path path;

}
