package fr.turtlesport.util;

import java.io.File;
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
   */
  public static boolean copy(InputStream in, File file) {
    byte[] buf = new byte[4096];
    int len = -1;

    boolean isOk = false;
    boolean isCopyBegin = false;
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(file);
      isCopyBegin = true;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      in.close();
      out.close();
      isOk = true;
    }
    catch (Throwable th) {
      isOk = false;
      file.delete();
      try {
        if (out != null) {
          out.close();
        }
      }
      catch (IOException ioe) {
      }
      try {
        if (in != null) {
          in.close();
        }
      }
      catch (IOException ioe) {
      }
      // suppression du fichier
      if (isCopyBegin && file.exists()) {
        file.delete();
      }
    }

    return isOk;
  }

  /**
   * chmod
   * 
   * @param permision
   *          la permission ex : 755
   * @param path
   *          le path du fichier
   */
  public static boolean chmod(String permision, String path) {
    if (OperatingSystem.isWindows()) {
      return true;
    }

    try {
      Runtime.getRuntime().exec(new String[] { "chmod", permision, path })
          .waitFor();
    }
    catch (Throwable e) {
      return false;
    }

    return true;
  }

}
