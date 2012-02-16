package fr.turtlesport.googleearth;

import java.io.File;

import fr.turtlesport.Configuration;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author denis
 * 
 */
public final class GoogleEarthLinux extends AbstractGoogleEarth {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GoogleEarthLinux.class);
  }

  /**
   * 
   */
  protected GoogleEarthLinux() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#isInstalled()
   */
  public boolean isInstalled() {
    // recherche dans le path.
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.AbstractGoogleEarth#getPath()
   */
  public String getPath() {
    return Configuration.getConfig().getProperty("google",
                                                 "googleearth",
                                                 "googleearth");
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
    st.append(getPath());
    st.append(' ');
    st.append(file.getAbsolutePath());

    String cmd = st.toString();
    log.debug("cmd=" + cmd);

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
