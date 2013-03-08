package fr.turtlesport.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class ZipUtil {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ZipUtil.class);
  }

  /**
   * Cr&eacute;ation d'un zip.
   * 
   * @param out
   * @param in
   */
  public static void create(File outFile, File inFile) {

    byte[] buffer = new byte[1024];
    ZipOutputStream out = null;
    FileInputStream in = null;
    try {
      out = new ZipOutputStream(new FileOutputStream(outFile));
      out.setLevel(Deflater.DEFAULT_COMPRESSION);
      in = new FileInputStream(inFile);
      out.putNextEntry(new ZipEntry(inFile.getName()));
      int len;
      while ((len = in.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
      out.closeEntry();
    }
    catch (IOException e) {
      log.error("", e);
    }
    finally {
      try {
        if (in != null) {
          in.close();
        }
      }
      catch (IOException e) {
      }
      try {
        if (out != null) {
          out.close();
        }
      }
      catch (IOException e) {
      }
    }

  }

}
