package fr.turtlesport.googleearth;

import fr.turtlesport.util.OperatingSystem;

/**
 * @author denis
 * 
 */
public final class GoogleEarthFactory {

  private static IGoogleEarth googleEarth;

  /**
   * 
   */
  private GoogleEarthFactory() {
  }

  /**
   * Restitue l'interface IGoogleEarth sp&eacute;cifique &agrave; l'OS.
   * 
   * @return <code>IGoogleEarth</code> de l'OS.
   */
  public static IGoogleEarth getDefault() {
    if (googleEarth == null) {
      synchronized (GoogleEarthFactory.class) {
        if (googleEarth == null) {
          if (OperatingSystem.isWindows()) {
            googleEarth = new GoogleEarthWin();
          }
          else if (OperatingSystem.isMacOSX()) {
            googleEarth = new GoogleEarthMacosx();
          }
          else {
            googleEarth = new GoogleEarthLinux();
          }
        }
      }
    }
    return googleEarth;
  }
}
