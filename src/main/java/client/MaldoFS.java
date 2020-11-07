package client;

/**
 * Static factory methods for creating MaldoFS
 */
public class MaldoFS {

  private MaldoFS(){}

  public static MaldoFileSystem newFileSystem(){
    return new MaldoFileSystem();
  }
}
