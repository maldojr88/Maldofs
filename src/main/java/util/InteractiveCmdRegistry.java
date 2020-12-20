package util;

import static com.google.common.base.Preconditions.checkArgument;

import core.MaldoFileSystem;
import core.MaldoFileSystemProvider;
import file.Directory;
import file.DirectoryRegistry;
import file.File;
import file.RegularFile;
import file.RegularFileOperator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import path.MaldoPath;
import path.PathRegistry;

/**
 * Shell commands available for Interactive Shell
 */
public class InteractiveCmdRegistry {

  private static final List<String> HISTORY = new ArrayList<>();
  public static final int DEFAULT_HISTORY_COUNT = 10;
  //https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_RESET = "\u001B[0m";

  private final MaldoFileSystem fs;
  private final PathRegistry pathRegistry;
  private final RegularFileOperator regularFileOperator;

  public InteractiveCmdRegistry(MaldoFileSystem fs, RegularFileOperator regularFileOperator) {
    this.fs = fs;
    this.pathRegistry = fs.getPathRegistry();
    this.regularFileOperator = regularFileOperator;
  }

  public void executeCommand(String userInput, String identifier, List<String> args)
      throws IOException, InterruptedException {
    switch (InteractiveCmd.get(identifier)) {
      case CD       -> cd(args);
      case CP       -> cp(args);
      case MV       -> mv(args);
      case RM       -> rm(args);
      case LS       -> ls(args);
      case VIM      -> vim(args);
      case CAT      -> cat(args);
      case PWD      -> pwd(args);
      case EXIT     -> goodbye();
      case ECHO     -> echo(args);
      case HELP     -> help();
      case MKDIR    -> mkdir(args);
      case TOUCH    -> touch(args);
      case IMPORT   -> imp0rt(args);
      case EXPORT   -> export(args);
      case HISTORY  -> history(args);
      case UNKNOWN  -> throw new UnsupportedOperationException("Invalid command");
      default -> System.out.println("???!!!");
    }
    HISTORY.add(userInput);
  }

  /**
   * Export a file from MaldoFS into the native OS
   */
  private void export(List<String> args) throws IOException {
    checkArgument(args.size() == 2, "2 arguments expected");
    checkOS();
    MaldoPath maldoPath = pathRegistry.getAbsolutePathExists(args.get(0));
    Path unixPath = Paths.get(args.get(1));
    RegularFile regularFile = regularFileOperator.getRegularFile(maldoPath);
    Files.write(unixPath, regularFile.readAll());
  }

  /**
   * Import a file from the native OS into MaldoFS
   */
  private void imp0rt(List<String> args) throws IOException {
    checkArgument(args.size() == 2, "2 arguments expected");
    checkOS();
    Path unixPath = Paths.get(args.get(0));
    MaldoPath maldoPath = fs.getPath(args.get(1));
    byte[] bytes = Files.readAllBytes(unixPath);
    Files.createFile(maldoPath);
    RegularFile regularFile = regularFileOperator.getRegularFile(maldoPath);
    regularFile.writeAll(bytes);
  }

  private void history(List<String> args) {
    checkArgument(args.size() <= 1, "Only 1 argument expected");
    int count = args.size() == 1 ? Integer.parseInt(args.get(0)) : DEFAULT_HISTORY_COUNT;
    HISTORY.stream().skip(HISTORY.size() - count).forEach(System.out::println);
  }

  /**
   * Cheap way to allow text editing
   */
  private void vim(List<String> args) throws IOException, InterruptedException {
    checkArgument(args.size() == 1, "Only 1 expected arg");
    checkOS();
    MaldoPath maldoPath = pathRegistry.getAbsolutePathSmart(args.get(0));
    Directory maldoDir = DirectoryRegistry.getDirectory(maldoPath.getParent());
    Path unixPath = Files.createTempFile("maldoFSvim", null);
    RegularFile regularFile;
    if(maldoDir.contains(maldoPath)){//Existing file
       regularFile = maldoDir.getRegularFile(maldoPath);
      Files.write(unixPath, regularFile.readAll());
    }else{
      regularFile = regularFileOperator.createFile(maldoPath, new HashSet<>());
    }

    openTextEditor(unixPath);

    byte[] bytes = Files.readAllBytes(unixPath);
    regularFile.writeAll(bytes);
    Files.delete(unixPath);
  }

  private void openTextEditor(Path unixPath) throws IOException, InterruptedException {
    String[] terminalArgs = {"/usr/bin/open", "-eW", unixPath.toFile().getAbsolutePath()};
    ProcessBuilder editor = new ProcessBuilder(terminalArgs);
    editor.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    editor.redirectError(ProcessBuilder.Redirect.INHERIT);
    editor.redirectInput(ProcessBuilder.Redirect.INHERIT);
    Process p = editor.start();
    String openMsg =
        "--> Must close the Editor before coming back (The program, not just the document)";
    System.out.print(openMsg);
    p.waitFor();
    System.out.print("\b".repeat(openMsg.length()));
  }

  private void mv(List<String> args) throws IOException {
    checkArgument(args.size() == 2, "2 arguments expected (source, target)");
    String sourceStr = args.get(0);
    String targetStr = args.get(1);
    MaldoPath source = pathRegistry.getAbsolutePathExists(sourceStr);
    Optional<MaldoPath> localPath = pathRegistry.localDirReference(targetStr);

    MaldoPath target;
    if(localPath.isPresent()){//mv myfile etc
      target = localPath.get();
      if(target.isDirectory() && !source.isDirectory()){
        target = fs.getPath(target.getCanonical() + source.getRelativeName());
      }
    }else{
      target = pathRegistry.getAbsolutePathNotExists(targetStr, source.isDirectory());
    }
    Files.move(source, target);
  }

  private void rm(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Only 1 arguments - target");
    MaldoPath target = pathRegistry.getAbsolutePathExists(args.get(0));
    Files.delete(target);
  }

  private void cp(List<String> args) throws IOException {
    checkArgument(args.size() == 2, "2 arguments expected (source, target)");
    MaldoPath source = pathRegistry.getAbsolutePathExists(args.get(0));
    MaldoPath target = pathRegistry.getAbsolutePathNotExists(args.get(1), source.isDirectory());
    Files.copy(source, target);
  }

  private void help() {
    System.out.println("Available commands:");
    for (InteractiveCmd command : InteractiveCmd.getValidCommands()) {
      System.out.printf("\t%s%n", command.getIdentifier());
    }
  }

  private void cat(List<String> args) {
    checkArgument(args.size() == 1, "Too many arguments");
    String filename = args.get(0);
    Directory dir;
    RegularFile regularFile;
    if(filename.startsWith("/")){
      MaldoPath path = fs.getPath(filename);
      regularFile = regularFileOperator.getRegularFile(path);
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
      MaldoPath filePath = pathRegistry.getAbsolutePathExists(filename);
      Files.write(filePath, strToEcho.getBytes());
    }else {
      throw new RuntimeException("Wrong number of arguments passed");
    }
  }

  private void touch(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Too many arguments");
    String filename = args.get(0);
    MaldoPath newFilePath = pathRegistry.getAbsolutePathNotExists(filename, false);
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
    checkArgument(args.size() <= 2, "Too many arguments");
    if(args.isEmpty()){
      lsStandard(fs.getCurrentWorkingDir());
    } else if(args.size() == 2){//ls -l /somedir
      lsDetailed(DirectoryRegistry.getDirectory(fs.getPath(pathRegistry.dirAppend(args.get(1)))));
    } else{
      String arg = args.get(0);
      if(arg.startsWith("/")){//ls /home
        lsStandard(DirectoryRegistry.getDirectory(fs.getPath(pathRegistry.dirAppend(arg))));
      }else{// ls -l
        lsDetailed(fs.getCurrentWorkingDir());
      }
    }
  }

  private void lsStandard(Directory dir) {
    List<String> printableContents = getDirectoryPrintableSimpleContents(dir);
    int i=0;
    for(String str : printableContents){
      System.out.print(str + " ".repeat(20 - str.length()));
      boolean isLast = i == printableContents.size()-1;
      if((i != 0 && i % 4 == 0) || isLast){
        System.out.print("\n");
      }
      i++;
    }
  }

  private void lsDetailed(Directory dir) {
    List<String> detailedContents = dir.getPrintableDetailedContents();
    for(String fileDetail :detailedContents){
      System.out.println(fileDetail);
    }
  }

  private List<String> getDirectoryPrintableSimpleContents(Directory dir){
    List<String> ret = new ArrayList<>();
    for (Entry<MaldoPath, File> e : dir.getContent()) {
      MaldoPath path = e.getKey();
      String decorator = e.getValue().isDirectory() ? ANSI_BLUE : "";
      ret.add(decorator + path.getRelativeName() + ANSI_RESET);
    }
    return ret;
  }

  private void cd(List<String> args) {
    checkArgument(args.size() == 1, "Exactly 1 argument expected");
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
        desiredPath = fs.getPath(pathRegistry.dirAppend(desiredDir));//absolute path
      }
    }

    Directory dir = provider.getDirectory(desiredPath);
    fs.setCurrentWorkingDir(dir);
  }

  private void mkdir(List<String> args) throws IOException {
    checkArgument(args.size() == 1, "Invalid - expected 'mkdir <>'");
    String newDirName = args.get(0);
    MaldoPath path = pathRegistry.getAbsolutePathNotExists(newDirName, true);
    Files.createDirectory(path);
  }


  private void checkOS() {
    checkArgument(System.getProperty("os.name").equals("Mac OS X"),
        "Text Editor is only supported on MacOS");
  }
}
