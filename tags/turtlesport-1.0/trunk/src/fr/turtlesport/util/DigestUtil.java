package fr.turtlesport.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import fr.turtlesport.log.TurtleLogger;

/**
 * Classe utilitaire de Hash.
 * 
 * @author Denis Apparicio
 * 
 */
public final class DigestUtil {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Library.class);
  }

  private DigestUtil() {
  }

  /**
   * Restitue le digest d'un inputStream.
   * 
   * @param input
   *          l'<code>InputStream</code>.
   * @parm algo du digest.
   * @return le digest d'un inputStream ou <code>null</code> si erreur.
   */
  public static byte[] digest(InputStream input, String algo) {
    if (input == null) {
      log.error("InputStream est null");
      return null;
    }

    try {
      // calcul du digest
      MessageDigest md = MessageDigest.getInstance(algo, "SUN");
      byte[] buf = new byte[4096];
      int len = input.read(buf);
      while (len != -1) {
        md.update(buf, 0, len);
        len = input.read(buf);
      }

      return md.digest();
    }
    catch (Throwable e) {
      log.error("", e);
      return null;
    }
    finally {
      try {
        input.close();
      }
      catch (IOException e) {
      }
    }
  }

  /**
   * Restititue le hash SHA-1 d'un fichier.
   * 
   * @param file
   *          le fichier.
   * @parm algorythme du digest.
   * @return le hash SHA-1
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws NullPointerException
   */
  /**
   * Restitue le digest d'un fichier.
   * 
   * @param file
   *          le fichier.
   * @parm algo du digest.
   * @return le digest d'un inputStream ou <code>null</code> si erreur.
   */
  public static byte[] digest(File file, String algo) throws FileNotFoundException {
    return digest(new FileInputStream(file), algo);
  }
}
