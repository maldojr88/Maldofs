package util;

import client.MaldoFS;
import core.MaldoFileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Run a REPL/Shell like interactive environment for working with the FileSystem
 */
public class MaldoREPL {

  private static final String PROMPT = "[MaldoFS] $ ";
  private static final String EXIT = "exit";
  private static final boolean DEBUG_MODE = true;
  private static MaldoFileSystem fs;
  private static InteractiveCmdRegistry registry;


  public static void main(String[] args) {
    fs = MaldoFS.newFileSystem();
    registry = new InteractiveCmdRegistry(fs);
    initialize();
    startREPL();
  }

  private static void initialize(){
    String[] cmds = {
        "mkdir /home/",
        "mkdir /home/maljos/",
        "mkdir /tmp/",
        "mkdir /usr/",
        "mkdir /var/",
        "mkdir /etc/",
        "mkdir /opt/",
        "mkdir /bin",
        "mkdir /sbin",
        "mkdir /dev"
    };
    for(String cmd : cmds){
      tokenizeAndExecute(cmd);
    }
  }

  private static void startREPL() {
    String userInput = "";

    while (!userInput.equals(EXIT)) {
      Scanner name = new Scanner(System.in);
      System.out.print(PROMPT);
      userInput = name.nextLine();
      if(userInput.equals("")){
        System.out.println("====> no command typed");
        continue;
      }
      tokenizeAndExecute(userInput);
    }
  }

  private static void tokenizeAndExecute(String cmd) {
    List<String> tokens = List.of(cmd.split(" "));
    String program = tokens.get(0);
    List<String> args = tokens.size() > 1 ? tokens.subList(1, tokens.size()) : new ArrayList<>();

    executeCommand(program, args);
  }

  private static void executeCommand(String program, List<String> args) {
    try {
      registry.executeCommand(program,args);
    } catch (Exception e) {
      if (DEBUG_MODE) {
        System.out.println(e.getMessage());
      }
    }
  }
}
