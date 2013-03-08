package fr.turtlesport.googleearth;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

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
  private static final String SUBKEY = "SOFTWARE\\Classes\\Applications\\googleearth.exe\\shell\\Open\\command";

  /** Commande pour executer. */
  private String              command;

  private String[]            PATH   = { "C:\\Program Files\\Google\\Google Earth\\googleearth.exe",
      "C:\\Program Files\\Google\\Google Earth\\client\\googleearth.exe",
      "C:\\Program Files (x86)\\Google\\Google Earth\\googleearth.exe",
      "C:\\Program Files (x86)\\Google\\Google Earth\\client\\googleearth.exe" };

  /**
   * 
   */
  protected GoogleEarthWin() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.AbstractGoogleEarth#open(java.io.File)
   */
  @Override
  public void open(File kmFile) throws GoogleEarthException {
    try {
      super.open(kmFile);
    }
    catch (GoogleEarthException e) {
      if (e.getErrorCode() != GoogleEarthException.EXEC
          || !Desktop.isDesktopSupported()) {
        throw e;
      }

      Desktop desktop = Desktop.getDesktop();
      if (!desktop.isSupported(Desktop.Action.OPEN)) {
        throw e;
      }

      try {
        desktop.open(kmFile);
      }
      catch (IOException ioe) {
        log.error("", ioe);
        throw new GoogleEarthException(GoogleEarthException.EXEC);
      }
    }
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

    for (String s : PATH) {
      File f = new File(s);
      if (f.isFile()) {
        command = f.getPath() + " %1";
        return true;
      }
    }

    // Valeur de la cle
    try {
      command = RegistryWin.localMachine().get(SUBKEY, null);
      if (log.isDebugEnabled()) {
        log.debug("SUBKEY=" + SUBKEY);
        log.debug("command=" + command);
      }
    }
    catch (Throwable e) {
      log.error("", e);
    }

    if (command != null) {
      command = command.replace('\"', ' ');
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.AbstractGoogleEarth#getPath()
   */
  public String getPath() {
    if (command != null) {
      return command.substring(0, command.indexOf("%1"));
    }
    return "googleearth";
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

    StringBuilder st = new StringBuilder();

    if (command != null) {
      // "C:\Program Files\Google\Google Earth\googleearth.exe" "%1"
      int index = command.indexOf("%1");

      // Ajout du nom entre quotes sinon plante avace les noms avec espace.
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
    }
    else {
      st.append("googleearth");
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
    return true;
  }

}
