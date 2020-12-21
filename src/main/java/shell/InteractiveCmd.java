package shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum InteractiveCmd {
  CD("cd"),
  CP("cp"),
  MV("mv"),
  RM("rm"),
  LS("ls"),
  VIM("vim"),
  CAT("cat"),
  PWD("pwd"),
  EXIT("exit"),
  ECHO("echo"),
  HELP("help"),
  MKDIR("mkdir"),
  TOUCH("touch"),
  IMPORT("import"),
  EXPORT("export"),
  HISTORY("history"),
  UNKNOWN("N/A");


  private static final Map<String, InteractiveCmd> lookup = new HashMap<>();
  private static final List<InteractiveCmd> validCommands = new ArrayList<>();
  private final String identifier;

  static {
    for(InteractiveCmd cmd : InteractiveCmd.values()) {
      lookup.put(cmd.identifier, cmd);
      if(!cmd.equals(UNKNOWN)){
        validCommands.add(cmd);
      }
    }
  }

  InteractiveCmd(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier(){
    return this.identifier;
  }

  public static InteractiveCmd get(String id) {//reverse lookup
    if(!lookup.containsKey(id)){
      return UNKNOWN;
    }
    return lookup.get(id);
  }

  public static List<InteractiveCmd> getValidCommands(){
    return validCommands;
  }
}
