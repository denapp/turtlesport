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

  private static boolean      hasUpdateAtBoot = false;

  private Update() {
  }

  public static boolean detectUpdateAtBoot() {
    return hasUpdateAtBoot;
  }

  /**
   * Test s'il y a une nouvelle version disponible, au d&eacute;marrage de
   * l'application.
   */
  public static boolean checkAtBoot() {
    boolean isCheckatbook = Configuration.getConfig()
        .getPropertyAsBoolean("update", "checkatbook", false);
    if (!isCheckatbook) {
      return false;
    }

    try {
      String version = currentVersion();
      if (isNewVersion(version)) {
        String iniVersion = Configuration.getConfig()
            .getProperty("update", "version", Version.VERSION);
        if (!version.equals(iniVersion)) {
          hasUpdateAtBoot = true;
          Configuration.getConfig().addProperty("update", "version", version);
          Configuration.getConfig().save();
        }
      }
    }
    catch (Throwable e) {
      log.error("", e);
    }

    return hasUpdateAtBoot;
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
  private static String currentVersion() throws IOException {
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

  private static boolean isNewVersion(String netVersion) {
    try {
      float fNetVersion = Float.parseFloat(netVersion);
      float fVersion = Float.parseFloat(Version.VERSION);
      return (fNetVersion > fVersion);
    }
    catch (Throwable e) {
      return false;
    }
  }
}
