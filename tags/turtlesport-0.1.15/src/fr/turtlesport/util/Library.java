package fr.turtlesport.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

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
   *          classe qui permet de d�duire le path.
   * @param libname
   *          nom de la librairie.
   * @exception UnsatisfiedLinkError
   *              si la librairie est non trouv�e.
   * @exception NullPointerException
   *              si <code>clazz</code> est <code>null</code> ou si
   *              <code>libname</code> est <code>null</code>.
   * @see java.lang.System#load(java.lang.String)
   */
  public static void load(Class<?> clazz, final String libname) {
    log.debug(">>load");

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

    log.debug("<<load");
  }

  private static void unsatisfiedLinkError(String file) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), Library.class);
    String msg = MessageFormat.format(rb.getString("UnsatisfiedLinkError"), file);
    throw new UnsatisfiedLinkError(msg);
  }
}
