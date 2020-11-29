package util;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import core.MaldoFileSystemProvider;
import file.Directory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import path.MaldoPath;

public class InteractiveCmdRegistry {

  private final MaldoFileSystem fs;

  public InteractiveCmdRegistry(MaldoFileSystem fs) {
    this.fs = fs;
  }

  public void executeCommand(String identifier, List<String> args) throws IOException {
    switch (identifier) {
      case "cd"     -> cd(args);
      case "ls"     -> ls(args);
      case "pwd"    -> pwd(args);
      case "exit"   -> goodbye();
      case "mkdir"  -> mkdir(args);
      default -> System.out.println("Unknown command!!!");
    }
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
    }else{
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
        //relative path
        desiredPath = relativeNameToPath.get(desiredDir);
      }else{
        desiredPath = fs.getPath(dirAppend(desiredDir));
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
