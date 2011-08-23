package fr.turtlesport.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

import fr.turtlesport.UsbProtocol;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class Library {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Library.class);
  }

  private Library() {
  }

  /**
   * Chargement d'une librairie &agrave; partir de la localisation d'une classe
   * et de son nom.
   * 
   * @param clazz
   *          classe qui permet de d&eacute;duire le path.
   * @param libname
   *          nom de la librairie.
   * @exception UnsatisfiedLinkError
   *              si la librairie est non trouv&eacute;e.
   * @exception NullPointerException
   *              si <code>clazz</code> est <code>null</code> ou si
   *              <code>libname</code> est <code>null</code>.
   * @see java.lang.System#load(java.lang.String)
   */
  public static void load(Class<?> clazz, final String libname) {
    log.debug(">>load");

    if (OperatingSystem.isLinux()) {
      loadLinux(clazz, libname);
    }
    else {
      loadOther(clazz, libname);
    }

    log.debug("<<load");
  }
  
  /**
   * Chargement d'une librairie pour os autre que linux
   */
  public static void loadOther(Class<?> clazz, final String libname) {
    log.debug(">>loadOther");

    try {
      // recuperation du path complet de la librairie.
      String dirLocation = Location.dirNameExecution(clazz);
      log.debug("dirLocation  <" + dirLocation + ">");
      File libFile = new File(dirLocation, System.mapLibraryName(libname));
      if (!libFile.isFile()) {
        unsatisfiedLinkError(libFile.getCanonicalPath());
      }

      // chargement de la librearie.
      String path = libFile.getAbsolutePath();
      log.info("chargement de <" + path + ">");
      System.load(path);
    }
    catch (IOException e) {
      unsatisfiedLinkError(libname);
    }

    log.debug("<<loadOther");
  }

  /**
   * Chargement d'une librairie pour os linux.
   */
  private static void loadLinux(Class<?> clazz, String libname) {
    log.debug(">>loadLinux " + libname);

    boolean isLoad = false;
    String libNameOS = null;

    if (OperatingSystem.is64bits()) {
      // chargement 64bits
      libNameOS = System.mapLibraryName(libname + "64");
      isLoad = doLoadLinux(clazz, libNameOS);
    }

    if (!isLoad) {
      // chargement 32bits
      libNameOS = System.mapLibraryName(libname);
      if (!doLoadLinux(clazz, libNameOS)) {
        unsatisfiedLinkError(libNameOS);
      }
    }
    
    log.debug("<<loadLinux");
  }

  private static boolean doLoadLinux(Class<?> clazz, String libNameOS) {
    final String algo = "MD5";
    boolean isEqual = false;

    // calcul du digest de la librairie du jar
    byte[] digestJarLibrary = DigestUtil.digest(UsbProtocol.class
        .getResourceAsStream(libNameOS), algo);

    // Construction du nom de la librairie dans le repertoire (.turtlesport)
    String path = Location.userLocation();
    File dir = new File(path);
    File fileLibName = new File(dir, libNameOS);

    // si la librarrie existe calcul du digest et comparaison
    if (fileLibName.isFile() && digestJarLibrary != null) {
      byte[] digestJar = null;
      try {
        digestJar = DigestUtil.digest(fileLibName, algo);
      }
      catch (FileNotFoundException e) {
        // ne peut arriver
      }
      if (digestJar != null) {
        isEqual = Arrays.equals(digestJar, digestJarLibrary);
      }
    }

    // Si library du jar par identique a librairie du poste => copie
    if (!isEqual) {
      // on extrait la librairie du jar
      if (FileUtil.copy(clazz.getResourceAsStream(libNameOS), fileLibName)) {
        // chmod 755
        FileUtil.chmod("755", fileLibName.getAbsolutePath());
        // chargement de la librairie
        return load(fileLibName.getAbsolutePath());
      }
      log.error("Echec copie de la librairie");
      return false;
    }

    // librarie egale : chargement de la librairie
    return load(fileLibName.getAbsolutePath());
  }

  private static void unsatisfiedLinkError(String file) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), Library.class);
    String msg = MessageFormat.format(rb.getString("UnsatisfiedLinkError"),
                                      file);
    throw new UnsatisfiedLinkError(msg);
  }

  private static boolean load(String path) {
    try {
      System.load(path);
      return true;
    }
    catch (UnsatisfiedLinkError e) {
      log.warn("Echec chargement de la librairie " + path);
      log.warn("", e);
    }
    return false;
  }
}
