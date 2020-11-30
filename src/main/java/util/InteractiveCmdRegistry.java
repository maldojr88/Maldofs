package util;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import core.MaldoFileSystemProvider;
import file.Directory;
import file.DirectoryRegistry;
import file.RegularFile;
import file.RegularFileOperator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import path.MaldoPath;

/**
 * Shell commands available for Interactive
 */
public class InteractiveCmdRegistry {

  private final MaldoFileSystem fs;

  public InteractiveCmdRegistry(MaldoFileSystem fs) {
    this.fs = fs;
  }

  public void executeCommand(String identifier, List<String> args) throws IOException {
    switch (identifier) {
      case "cd"     -> cd(args);
      case "ls"     -> ls(args);
      case "cat"    -> cat(args);
      case "pwd"    -> pwd(args);
      case "exit"   -> goodbye();
      case "echo"   -> echo(args);
      case "mkdir"  -> mkdir(args);
      case "touch"  -> touch(args);
      default -> System.out.println("Unknown command!!!");
    }
  }

  //TODO - consolidate this file. Lots of ways of doing the same things

  private void cat(List<String> args) {
    checkArgument(args.size() == 1, "Too many arguments");
    RegularFileOperator operator = new RegularFileOperator();
    String filename = args.get(0);
    Directory dir;
    RegularFile regularFile;
    if(filename.startsWith("/")){
      MaldoPath path = (MaldoPath) fs.getPath(filename);
      MaldoPath dirPath = (MaldoPath)path.getParent();
      DirectoryRegistry registry = new DirectoryRegistry();
      dir = registry.getDirectory(dirPath);
      regularFile = dir.getRegularFile(path);

    }else{
      MaldoPath cwdPath = MaldoPath.convert(fs.getCurrentWorkingDir().getPath());
      DirectoryRegistry registry = new DirectoryRegistry();
      dir = registry.getDirectory(cwdPath);
      String canonical = cwdPath.getCanonical() + filename;
      regularFile = dir.getRegularFile(canonical);

    }
    String contents = new String(regularFile.readAll());
    System.out.println(contents);
  }

  private void echo(List<String> args) throws IOException {
    checkArgument(args.size() <=4, "Too many arguments" );
    String strToEcho = args.get(0).replaceAll("\'","");
    if(args.size() == 1){//echo 'hello' | echo hello
      System.out.println(strToEcho);
    }else if(args.size() == 3){//echo 'hello' >> myFile.txt
      checkArgument(args.get(1).equals(">>"), "Expected '>>' as second argument");
      String filename = args.get(2);
      MaldoPath cwdPath = MaldoPath.convert(fs.getCurrentWorkingDir().getPath());
      MaldoPath filePath = (MaldoPath) fs.getPath(cwdPath.getCanonical() + filename);
      Files.write(filePath, strToEcho.getBytes());
    }else {
      throw new RuntimeException("Wrong number of arguments passed");
    }
  }

  private void touch(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Too many arguments");
    String filename = args.get(0);
    MaldoPath cwdPath = MaldoPath.convert(fs.getCurrentWorkingDir().getPath());
    MaldoPath newFilePath = (MaldoPath) fs.getPath(cwdPath.getCanonical() + filename);
    Files.createFile(newFilePath);
  }

  private void goodbye() {
    System.out.println("Goodbye!!!");
  }

  private void pwd(List<String> args) {
    checkArgument(args.isEmpty(), "No arguments expected");
    MaldoPath path = (MaldoPath) fs.getCurrentWorkingDir().getPath();
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

    Path desiredPath;
    if(desiredDir.equals("..")){
      desiredPath =  fs.getCurrentWorkingDir().getPath().getParent();
    }else{
      Map<String, MaldoPath> relativeNameToPath = fs.getCurrentWorkingDir().getRelativeNameToPath();
      if(relativeNameToPath.containsKey(desiredDir)){
        desiredPath = relativeNameToPath.get(desiredDir);//relative path
      }else{
        desiredPath = fs.getPath(dirAppend(desiredDir));//absolute path
      }
    }

    Directory dir = provider.getDirectory((MaldoPath) desiredPath);
    fs.setCurrentWorkingDir(dir);
  }

  private void mkdir(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Invalid - expected 'mkdir <>'");
    String newDirName = args.get(0);
    Path path;
    if(newDirName.startsWith("/")){
      newDirName = dirAppend(newDirName);
      path = fs.getPath(newDirName);
    }else{
      MaldoPath currentDir = (MaldoPath) fs.getCurrentWorkingDir().getPath();
      newDirName = dirAppend(currentDir.getCanonical() + newDirName);
      path = fs.getPath(newDirName);
    }
    Files.createDirectory(path);
  }


  private String dirAppend(String str){
    if(!str.endsWith("/")){
      str = str + "/";
    }
    return str;
  }
}
