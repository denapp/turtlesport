package fr.turtlesport.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import fr.turtlesport.ProxyConfiguration;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Base64;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public final class SecurePassword {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ProxyConfiguration.class);
  }

  private static final char[] PASSWORD_KS = "turtle".toCharArray();

  private SecurePassword() {
  }

  /**
   * Encrypte le mot de passe.
   * 
   * @param password
   *          le mot de passe.
   * 
   * @return le mot de passe encrypté
   * @throws SecurePasswordException
   *           si erreur.
   */
  public static String encrypt(String password) throws SecurePasswordException {
    try {
      SecretKey secretKey = getSecretKey();

      // Encryption
      Cipher encrypter = Cipher.getInstance("DES");
      encrypter.init(Cipher.ENCRYPT_MODE, secretKey);

      byte[] bEncrypted = encrypter.doFinal(password.getBytes());

      // Encodage base64
      return new String(Base64.encode(bEncrypted));

    }
    catch (Throwable e) {
      log.error("", e);
      throw new SecurePasswordException(e);
    }
  }

  /**
   * Decrypte le mot de passe.
   * 
   * @param password
   *          le mot de passe.
   * @return le mot de passe d&eacute;crypt&eacute;
   * @throws SecurePasswordException,
   *           si erreur.
   */
  public static String decrypt(String password) throws SecurePasswordException {
    try {
      SecretKey secretKey = getSecretKey();

      // Decryption
      Cipher encrypter = Cipher.getInstance("DES");
      encrypter.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] bDecrypted = encrypter.doFinal(Base64.decode(password.getBytes()));

      // Encodage base64
      return new String(bDecrypted);
    }
    catch (Throwable e) {
      log.error("", e);
      throw new SecurePasswordException(e);
    }
  }

  private static SecretKey getSecretKey() throws KeyStoreException,
                                         NoSuchAlgorithmException,
                                         CertificateException,
                                         IOException {
    SecretKey secretKey = null;

    KeyStore ks = KeyStore.getInstance("JCEKS");
    File keystoreFile = new File(Location.userLocation(), "turtlesport.jks");

    if (keystoreFile.isFile()) {
      try {
        FileInputStream fis = new FileInputStream(keystoreFile);
        ks.load(fis, PASSWORD_KS);
        fis.close();
        if (ks.isKeyEntry("secretKeyAlias")) {
          secretKey = (SecretKey) ks.getKey("secretKeyAlias", PASSWORD_KS);
        }
      }
      catch (Throwable e) {
        // creation d'un keystore vide
        ks.load(null, PASSWORD_KS);
      }
    }
    else {
      // creation d'un keystore vide
      ks.load(null, PASSWORD_KS);
    }

    if (secretKey == null) {
      // creation de la cle
      secretKey = KeyGenerator.getInstance("DES").generateKey();

      // sauvegarde du keystore
      ks.setKeyEntry("secretKeyAlias", secretKey, PASSWORD_KS, null);

      FileOutputStream fos = new FileOutputStream(keystoreFile);
      ks.store(fos, PASSWORD_KS);
      fos.close();
    }

    return secretKey;
  }

}
