package fr.turtlesport.googleearth;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class GoogleEarthMacosx implements IGoogleEarth {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GoogleEarthMacosx.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#getPath()
   */
  public String getPath() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#isInstalled()
   */
  public boolean isInstalled() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.googleearth.IGoogleEarth#open(java.io.File)
   */
  public void open(File kmFile) throws GoogleEarthException {
    if (kmFile == null) {
      throw new IllegalArgumentException();
    }
    if (!kmFile.isFile()) {
      log.debug("fichier inexistant : " + kmFile.getAbsolutePath());
      throw new GoogleEarthException(GoogleEarthException.FILE_NOT_FOUND);
    }

    try {
      // Execution par introspection pour compil os autre macosx
      Class<?> clazz = Class.forName("com.apple.eio.FileManager");
      Method method = clazz.getDeclaredMethod("openURL", String.class);
      String url = kmFile.toURL().toString();
      method.invoke(clazz, url);
    }
    catch (MalformedURLException e) {
      log.error("", e);
      throw new GoogleEarthException(GoogleEarthException.FILE_NOT_FOUND);
    }
    catch (Throwable e) {
      log.error("", e);
      throw new GoogleEarthException(GoogleEarthException.EXEC);
    }
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
