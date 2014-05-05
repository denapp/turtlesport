package fr.turtlesport.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.turtlesport.Configuration;
import fr.turtlesport.Version;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class Update {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Update.class);
  }

  private static boolean      isFinished      = false;

  private static boolean      needUpdateFirst = false;

  private Update() {
  }

  /**
   * Test s'il y a une nouvelle version disponible, au d&eacute;marrage de
   * l'application.
   */
  public static void init() {
    boolean bRun = Configuration.getConfig()
        .getPropertyAsBoolean("update", "checkatbook", false);
    if (bRun) {
      new Thread() {
        public void run() {
          try {
            needUpdateFirst = false;
            
            String version = currentVersion();
            if (isNewVersion(version)) {
              String iniVersion = Configuration.getConfig()
                  .getProperty("update", "version", Version.VERSION);
              if (!version.equals(iniVersion)) {
                needUpdateFirst = true;
                Configuration.getConfig().addProperty("update",
                                                      "version",
                                                      version);
                Configuration.getConfig().save();
              }
            }
          }
          catch (Throwable e) {
            log.error("", e);
          }
          finally {
            isFinished = true;
          }
        }
      }.start();
    }
    else {
      isFinished = true;
      needUpdateFirst = false;
    }
  }

  public static boolean isCheckFirstEnd() {
    return isFinished;
  }

  /**
   * 
   * D&eacute;termine si mise &agrave; jour diponible.
   * 
   * @return <code>true</code> si mise &agrave; jour disponible,
   *         <code>false</code> sinon.
   * @throws IOException
   */
  public static boolean checkFirst() {
    return needUpdateFirst;
  }

  /**
   * 
   * D&eacute;termine si mise &agrave; jour diponible.
   * 
   * @return <code>true</code> si mise &agrave; jour disponible,
   *         <code>false</code> sinon.
   * @throws IOException
   */
  public static boolean check() throws IOException {
    String version = currentVersion();
    return isNewVersion(version);
  }

  /**
   * 
   * Restitue la version en cours.
   * 
   * @return la version.
   * @throws IOException
   */
  public static String currentVersion() throws IOException {
    String version = null;

    URL url = new URL("http://turtlesport.sourceforge.net/version.txt");
    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
    cnx.setConnectTimeout(3000);
    cnx.setReadTimeout(3000);
    cnx.setRequestMethod("GET");
    cnx.setDoInput(true);

    if (cnx.getResponseCode() == HttpURLConnection.HTTP_OK) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
      version = reader.readLine();
      reader.close();
    }

    return version;
  }

  private static boolean isNewVersion(String version) {
    try {
      float fVersion = Float.parseFloat(Version.VERSION);
      float fNetVersion = Float.parseFloat(version);
      return (fVersion >  fNetVersion);
    }
    catch (Throwable e) {
      return false;
    }
  }
}
