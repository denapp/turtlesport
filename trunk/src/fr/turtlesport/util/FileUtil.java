package fr.turtlesport.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

  /**
   * Copie vers un fichier.
   * 
   * @param in
   *          l'inpustream &agrave; copier.
   * @param file
   *          la destination.
   * @throws FileNotFoundException
   */
  public static void copy(InputStream in, File file) throws FileNotFoundException {
    byte[] buf = new byte[1024];
    int len = -1;

    FileOutputStream out = new FileOutputStream(file);

    try {
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      } 
      out.close();
    }
    catch (IOException e) {
      file.delete();
    }
  }

}
