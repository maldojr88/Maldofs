package util;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import core.MaldoFileSystemProvider;
import file.Directory;
import file.DirectoryRegistry;
import file.RegularFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import path.MaldoPath;

/**
 * Shell commands available for Interactive Shell
 */
public class InteractiveCmdRegistry {

  private final MaldoFileSystem fs;

  public InteractiveCmdRegistry(MaldoFileSystem fs) {
    this.fs = fs;
  }

  public void executeCommand(String identifier, List<String> args) throws IOException {
    switch (InteractiveCmd.get(identifier)) {
      case CD     -> cd(args);
      case CP     -> cp(args);
      case RM     -> rm(args);
      case LS     -> ls(args);
      case CAT    -> cat(args);
      case PWD    -> pwd(args);
      case EXIT   -> goodbye();
      case ECHO   -> echo(args);
      case HELP   -> help();
      case MKDIR  -> mkdir(args);
      case TOUCH  -> touch(args);
      default -> System.out.println("Unknown command!!!");
    }
  }

  private void rm(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Only 1 arguments target");
    MaldoPath target = getAbsolutePathExists(args.get(0));
    Files.delete(target);
  }

  private void cp(List<String> args) throws IOException {
    checkArgument(args.size() == 2, "Only 2 arguments (source, target)");
    MaldoPath source = getAbsolutePathExists(args.get(0));
    MaldoPath target = getAbsolutePathNotExists(args.get(1), source.isDirectory());
    Files.copy(source, target);
  }

  /**
   * Get the absolute path for a path which should exist
   */
  private MaldoPath getAbsolutePathExists(String path){
    if(path.startsWith("/")){
      return fs.getPath(path);
    }else{
      Directory dir = DirectoryRegistry.getDirectory(fs.getCurrentWorkingDir().getPath());
      return dir.getRelativeNameToPath().get(path);
    }
  }

  /**
   * Get the absolute path for a path which does not exist yet
   */
  private MaldoPath getAbsolutePathNotExists(String path, boolean directory){
    if(path.startsWith("/")){
      return fs.getPath(path);
    }else{
      String append = directory ? "/" : "";
      return fs.getPath(fs.getCurrentWorkingDir().getPath().getCanonical() + path + append);
    }
  }

  private void help() {
    System.out.println("Available commands:");
    for (InteractiveCmd command : InteractiveCmd.values()) {
      System.out.printf("\t%s%n", command.getIdentifier());
    }
  }

  /**
   * Create a new path --> fs.getPath("string")
   */


  private void cat(List<String> args) {
    checkArgument(args.size() == 1, "Too many arguments");
    String filename = args.get(0);
    Directory dir;
    RegularFile regularFile;
    if(filename.startsWith("/")){
      MaldoPath path = fs.getPath(filename);
      MaldoPath dirPath = path.getParent();
      dir = DirectoryRegistry.getDirectory(dirPath);
      regularFile = dir.getRegularFile(path);

    }else{
      MaldoPath cwdPath = MaldoPath.convert(fs.getCurrentWorkingDir().getPath());
      dir = DirectoryRegistry.getDirectory(cwdPath);
      String canonical = cwdPath.getCanonical() + filename;
      regularFile = dir.getRegularFile(canonical);
    }
    String contents = new String(regularFile.readAll());
    System.out.println(contents);
  }

  private void echo(List<String> args) throws IOException {
    checkArgument(args.size() <=4, "Too many arguments" );
    String strToEcho = args.get(0).replaceAll("'","");
    if(args.size() == 1){//echo 'hello' | echo hello
      System.out.println(strToEcho);
    }else if(args.size() == 3){//echo 'hello' >> myFile.txt
      checkArgument(args.get(1).equals(">>"), "Expected '>>' as second argument");
      String filename = args.get(2);
      MaldoPath cwdPath = MaldoPath.convert(fs.getCurrentWorkingDir().getPath());
      MaldoPath filePath = fs.getPath(cwdPath.getCanonical() + filename);
      Files.write(filePath, strToEcho.getBytes());
    }else {
      throw new RuntimeException("Wrong number of arguments passed");
    }
  }

  private void touch(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Too many arguments");
    String filename = args.get(0);
    MaldoPath cwdPath = MaldoPath.convert(fs.getCurrentWorkingDir().getPath());
    MaldoPath newFilePath = fs.getPath(cwdPath.getCanonical() + filename);
    Files.createFile(newFilePath);
  }

  private void goodbye() {
    System.out.println("Goodbye!!!");
  }

  private void pwd(List<String> args) {
    checkArgument(args.isEmpty(), "No arguments expected");
    MaldoPath path = fs.getCurrentWorkingDir().getPath();
    System.out.println(path.getCanonical());
  }

  private void ls(List<String> args) {
    int i=0;
    if(args.isEmpty()){
      List<String> printableContents = fs.getCurrentWorkingDir().getPrintableSimpleContents();
      for(String str : printableContents){
        System.out.print(str + " ".repeat(20 - str.length()));
        boolean isLast = i == printableContents.size()-1;
        if((i != 0 && i % 4 == 0) || isLast){
          System.out.print("\n");
        }
        i++;
      }
    }else{ // ls -l
      List<String> detailedContents = fs.getCurrentWorkingDir().getPrintableDetailedContents();
      for(String fileDetail :detailedContents){
        System.out.println(fileDetail);
      }
    }
  }

  private void cd(List<String> args) {
    checkArgument(args.size() == 1, "Too many arguments");
    MaldoFileSystemProvider provider = (MaldoFileSystemProvider) fs.provider();
    String desiredDir = args.get(0);

    MaldoPath desiredPath;
    if(desiredDir.equals("..")){
      desiredPath = fs.getCurrentWorkingDir().getPath().getParent();
    }else{
      Map<String, MaldoPath> relativeNameToPath = fs.getCurrentWorkingDir().getRelativeNameToPath();
      if(relativeNameToPath.containsKey(desiredDir)){
        desiredPath = relativeNameToPath.get(desiredDir);//relative path
      }else{
        desiredPath = fs.getPath(dirAppend(desiredDir));//absolute path
      }
    }

    Directory dir = provider.getDirectory(desiredPath);
    fs.setCurrentWorkingDir(dir);
  }

  private void mkdir(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Invalid - expected 'mkdir <>'");
    String newDirName = args.get(0);
    Path path;
    if(newDirName.startsWith("/")){
      newDirName = dirAppend(newDirName);
    }else{
      MaldoPath currentDir = fs.getCurrentWorkingDir().getPath();
      newDirName = dirAppend(currentDir.getCanonical() + newDirName);
    }

    path = fs.getPath(newDirName);
    Files.createDirectory(path);
  }


  private String dirAppend(String str){
    if(!str.endsWith("/")){
      str = str + "/";
    }
    return str;
  }
}
