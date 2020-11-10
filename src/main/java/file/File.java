package file;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Files are modeled similar to Unix filesystems.
 *
 * Logically, a file is a combination of 1) Metadata 2) Content
 */
public abstract class File {
  MetaData metaData;
}