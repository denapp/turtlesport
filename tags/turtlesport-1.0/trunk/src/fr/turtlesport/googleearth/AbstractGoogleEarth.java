package fr.turtlesport.googleearth;

import java.io.File;
import java.io.IOException;

import fr.turtlesport.Configuration;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Exec;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractGoogleEarth implements IGoogleEarth {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractGoogleEarth.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#getPath()
   */
  public String getPath() {
    return Configuration.getConfig().getProperty("google", "googleearth");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#open(java.io.File)
   */
  public void open(File kmFile) throws GoogleEarthException {
    log.debug(">>open");

    if (kmFile == null) {
      throw new IllegalArgumentException();
    }
    if (!kmFile.isFile()) {
      log.debug("fichier inexistant : " + kmFile.getAbsolutePath());
      throw new GoogleEarthException(GoogleEarthException.FILE_NOT_FOUND);
    }

    // on verifie que googleearth est installee.
    if (!isInstalled()) {
      throw new GoogleEarthException(GoogleEarthException.ABSENT);
    }

    // Lancement de la commande
    try {
      Exec.exec(getOpenCommand(kmFile));
    }
    catch (IOException e) {
      log.error("", e);
      throw new GoogleEarthException(GoogleEarthException.EXEC);
    }

    log.debug("<<open");
  }

  /**
   * Restitue la commande pour ouvrir un fichier googleearth.
   * 
   * @param file
   *          nom du fichier googleearth
   * @return la commande.
   */
  public abstract String getOpenCommand(File file);

}
