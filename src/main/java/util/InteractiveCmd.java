package util;

import java.util.HashMap;
import java.util.Map;

public enum InteractiveCmd {
  CD("cd"),
  CP("cp"),
  LS("ls"),
  CAT("cat"),
  PWD("pwd"),
  EXIT("exit"),
  ECHO("echo"),
  HELP("help"),
  MKDIR("mkdir"),
  TOUCH("touch");

  private static final Map<String, InteractiveCmd> lookup = new HashMap<>();
  private final String identifier;

  static {
    for(InteractiveCmd cmd : InteractiveCmd.values()) {
      lookup.put(cmd.identifier, cmd);
    }
  }

  InteractiveCmd(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier(){
    return this.identifier;
  }

  public static InteractiveCmd get(String id) {//reverse lookup
    return lookup.get(id);
  }
}
