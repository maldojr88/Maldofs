package shell;

import client.MaldoFS;
import core.MaldoFileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    registry = new InteractiveCmdRegistry(fs, fs.getProvider().getRegularFileUtil());
    createUnixLikeDirs();
    startREPL();
  }

  private static void createUnixLikeDirs(){
    String[] cmds = {
        "mkdir /home/",
        "mkdir /home/maljos/",
        "touch /home/maljos/notes.txt",
        "echo 'This project was fun!' >> /home/maljos/notes.txt",
        "mkdir /tmp/",
        "mkdir /usr/",
        "mkdir /var/",
        "mkdir /etc/",
        "import /etc/nfs.conf /etc/nfs.conf",
        "import /etc/locate.rc /etc/locate.rc",
        "import /etc/man.conf /etc/man.conf",
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
      Scanner keyboard = new Scanner(System.in);
      System.out.print(PROMPT);
      userInput = keyboard.nextLine();
      if(userInput.equals("")){
        System.out.println("====> no command typed");
        continue;
      }
      tokenizeAndExecute(userInput);
    }
  }

  private static void tokenizeAndExecute(String userInput) {
    List<String> tokens = new ArrayList<>();
    Matcher m = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'").matcher(userInput);
    while (m.find()) {
      if (m.group(1) != null) {
        // Add double-quoted string without the quotes
        tokens.add(m.group(1));
      } else if (m.group(2) != null) {
        // Add single-quoted string without the quotes
        tokens.add(m.group(2));
      } else {
        // Add unquoted word
        tokens.add(m.group());
      }
    }
    String program = tokens.get(0);
    List<String> args = tokens.size() > 1 ? tokens.subList(1, tokens.size()) : new ArrayList<>();

    executeCommand(userInput, program, args);
  }

  private static void executeCommand(String userInput, String program, List<String> args) {
    try {
      registry.executeCommand(userInput,program,args);
    } catch (Exception e) {
      if (DEBUG_MODE) {
        System.out.println(e.getMessage());
      }
    }
  }
}
