package fr.turtlesport.util;

import java.io.File;

/**
 * @author Denis Apparicio
 * 
 */
public class FileUtil {

  private FileUtil() {
  }

  /**
   * Supprime un r&eacute;pertoire.
   * 
   * @param path
   *          le r&eacute;pertoire.
   * @return <code>true</code> si ok
   */
  public static boolean deleteDirectory(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      if (files != null) {
        for (File f : files) {
          if (f.isFile()) {
            f.delete();
          }
        }
      }
    }
    return (path.delete());
  }

  /**
   * Restitue la taille d'un r&eacute;pertoire.
   * 
   * @param path
   *          le r&eacute;pertoire.
   * @return la taille d'un r&eacute;pertoire.
   */
  public static long dirLength(File path) {
    long length = 0;
    if (path.exists()) {
      File[] files = path.listFiles();
      if (files != null) {
        for (File f : files) {
          if (f.isFile()) {
            length += f.length();
          }
        }
      }
    }
    return length;
  }

}
