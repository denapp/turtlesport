package fr.turtlesport.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public final class Location {

  private static File    dirExe;

  private static String  userAppHome;

  private static String  userAppGooleEarth;

  private static Object  mutex  = new Object();

  private static boolean isInit = false;

  private Location() {
  }

  /**
   * Initialisation des localisations.
   * 
   * @throws LocationException
   */
  public static synchronized void initialize() throws LocationException {
    // user location
    File file = new File(System.getProperty("user.home"), ".turtlesport");
    if (!file.isDirectory() && !file.mkdir()) {
      throw new LocationException(LocationException.CREATE, file);
    }
    checkReadWrite(file);
    userAppHome = file.getPath();

    // kml location
    File fileKml = new File(file, "kml");
    if (!fileKml.isDirectory() && !fileKml.mkdir()) {
      throw new LocationException(LocationException.CREATE, fileKml);
    }
    checkReadWrite(fileKml);
    userAppGooleEarth = fileKml.getPath();

    isInit = true;
  }

  /**
   * Restitue la localisation de l'application dans le r&eacute;pertoire
   * user-home.
   * 
   * @return la localisation de l'application dans le r&eacute;pertoire
   *         user-home.
   */
  public static String userLocation() {
    checkInit();
    return userAppHome;
  }

  /**
   * D&eacute;termine si l'executable est dans le path..
   * 
   * @return l<code>true</code> est dans le path, <code>false</code> sinon.
   */
  public static boolean isInPath(String name) {
    String path = System.getenv("PATH");
    if (path != null) {
      StringTokenizer st = new StringTokenizer(path, ":");
      while (st.hasMoreElements()) {
        File f = new File(st.nextToken(), name);
        if (f.isFile()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Restitue la localisation du r&eacute;pertoire googleearth.
   * 
   * @return la localisation de l'application dans le r&eacute;pertoire
   *         user-home.
   */
  public static String googleEarthLocation() {
    checkInit();
    return userAppGooleEarth;
  }

  /**
   * Restitue le nom r&eacute;pertoire d'execution d'une classe.
   * 
   * @param clazz
   *          le nom de la classe.
   * @return le nom r&eacute;pertoire d'execution d'une classe.
   */
  public static String dirNameExecution(Class<?> clazz) {
    if (clazz == null) {
      throw new NullPointerException("clazz");
    }

    File dirExe = dirExecution(clazz);
    if (dirExe != null) {
      try {
        return dirExe.getCanonicalPath();
      }
      catch (IOException e) {
      }
    }

    return null;
  }

  /**
   * Restitue le r&eacute;pertoire d'execution d'une classe.
   * 
   * @param clazz
   *          le nom de la classe.
   * @return le r&eacute;pertoire d'execution d'une classe.
   */
  public static File dirExecution(Class<?> clazz) {

    if (clazz == null) {
      throw new NullPointerException("clazz");
    }

    File dirExe = null;
    try {
      // URL location
      URL codeURL = clazz.getProtectionDomain().getCodeSource().getLocation();
      if (codeURL == null) {
        throw new FileNotFoundException();
      }

      if (codeURL.getProtocol().equals("jar")) {
        // jar:file:/D:/projet/tooky.jar!/fr.sdvsdg.test.Tooky.class
        int index = codeURL.getFile().indexOf('!');
        codeURL = new URL(codeURL.getFile().substring(0, index));
      }

      String path = URLDecoder.decode(codeURL.getFile(), "UTF-8");
      dirExe = new File(path).getCanonicalFile().getParentFile();
    }
    catch (Throwable e) {
      e.printStackTrace();
    }

    return dirExe;
  }

  /**
   * Restitue le r&eacute;pertoire d'execution de l'application.
   * 
   * @return le r&eacute;pertoire 'execution de l'application.
   */
  public static File dirExecution() {
    if (dirExe == null) {
      synchronized (mutex) {
        dirExe = dirExecution(Location.class);
        if (dirExe == null || !dirExe.isDirectory() || !dirExe.exists()) {
          throw new RuntimeException("Impossible de r�cup�rer le repertoire d'execution");
        }
      }
    }
    return dirExe;
  }

  private static void checkReadWrite(File file) throws LocationException {
    if (!file.canRead()) {
      throw new LocationException(LocationException.READ, file);
    }
    if (!file.canWrite()) {
      throw new LocationException(LocationException.WRITE, file);
    }
  }

  private static void checkInit() {
    if (!isInit) {
      throw new IllegalStateException();
    }
  }
}
