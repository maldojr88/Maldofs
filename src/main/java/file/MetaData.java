package file;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Auxiliary information about a file
 */
public class MetaData {
  private LocalDateTime createTime;
  private LocalDateTime lastModified;
  /* add permissions (owner, group etc) in the future */

  private Path path;

}
