package fr.turtlesport.googleearth;

import java.io.File;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.reg.RegistryWin;

/**
 * @author Denis Apparicio
 * 
 */
public final class GoogleEarthWin extends AbstractGoogleEarth {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GoogleEarthWin.class);
  }

  /** Google earth subkey */
  private static final String SUBKEY = "Software\\Classes\\Applications\\googleearth.exe\\shell\\Open\\command";

  /** Commande pour executer. */
  private String              command;

  private static final String PATH_C = "C:\\Program Files\\Google\\Google Earth\\googleearth.exe";

  private static final String PATH_D = "C:\\Program Files\\Google\\Google Earth\\googleearth.exe";

  /**
   * 
   */
  protected GoogleEarthWin() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#isInstalled()
   */
  public boolean isInstalled() {
    if (super.getPath() != null) {
      File f = new File(super.getPath());
      command = f.getPath() + " %1";
      return true;
    }

    File f = new File(PATH_C);
    if (f.isFile()) {
      command = f.getPath() + " %1";
      return true;
    }

    f = new File(PATH_D);
    if (f.isFile()) {
      command = f.getPath() + " %1";
      return true;
    }

    // Valeur de la cle
    command = RegistryWin.localMachine().get(SUBKEY, null);
    if (log.isDebugEnabled()) {
      log.debug("SUBKEY=" + SUBKEY);
      log.debug("command=" + command);
    }
    if (command != null) {
      command = command.replace('\"', ' ');
    }
    return (command != null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.AbstractGoogleEarth#getPath()
   */
  public String getPath() {
    if (!isInstalled()) {
      return null;
    }
    return command.substring(0, command.indexOf("%1"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.googleearth.AbstractGoogleEarth#getOpenCommand(java.io.File)
   */
  @Override
  public String getOpenCommand(File file) {
    log.debug(">>getOpenCommand");

    // "C:\Program Files\Google\Google Earth\googleearth.exe" "%1"
    int index = command.indexOf("%1");

    // Ajout du nom entre quotes sinon plante avace les noms avec espace.
    StringBuilder st = new StringBuilder();
    if (index > 0) {
      st.append(command.substring(0, index));
      st.append("\"");
      st.append(file.getAbsolutePath());
      st.append("\"");
      if (index < command.length()) {
        st.append(command.substring(index + 2));
      }
    }
    else {
      st.append(command);
      st.append(' ');
      st.append("\"");
      st.append(file.getAbsolutePath());
      st.append("\"");
    }

    String cmd = st.toString();
    // Constitution de la commande
    log.debug("kmFile.getAbsolutePath() <" + file.getAbsolutePath() + ">");
    log.debug("commande google-earth <" + cmd + ">");

    log.debug("<<getOpenCommand");
    return cmd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#isConfigurable()
   */
  public boolean isConfigurable() {
    return false;
  }

}
