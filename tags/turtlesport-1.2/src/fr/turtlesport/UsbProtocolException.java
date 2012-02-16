package fr.turtlesport;

import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 */
public class UsbProtocolException extends Exception {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UsbProtocolException.class);
  }

  // en private leve par JNI
//  /** Protocole non support&eacute;. */
//  private static final int     ERR_NO_GARMIN           = 0;
//
//  /** Impossible d'ouvrir le garmin. */
//  private static final int     ERR_ERR_OPEN_GARMIN     = 1;
//
//  /** Impossible de configurer le garmin. */
//  private static final int     ERR_CONFIG_GARMIN       = 2;
//
//  /** Linux echec libusb claim. */
//  private static final int     ERR_CLAIM_GARMIN        = 3;
//
//  /** Echec ecriture garmin. */
//  public static final int     ERR_SEND_GARMIN         = 4;
//
///** Echec lecture garmin. */
//public static final int     ERR_READ_GARMIN         = -1;

  /** Protocole non support&eacute;. */
  public static final int     ERR_NO_PROTOCOL         = 5;

  /** Packet invalide. */
  public static final int     ERR_PACKET_SIZE_INVALID = 6;

  /** Code erreur. */
  private int                 errorCode;

  /** Message erreur. */
  private String              msgErr;

  /**
   * 
   */
  public UsbProtocolException(int errorCode) {
    this.errorCode = errorCode;

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    if (msgErr == null) {
      try {
        ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
            .getManager().getCurrentLang(), UsbProtocolException.class);
        msgErr = rb.getString(Integer.toString(errorCode));
      }
      catch (Throwable th) {
        log.error("", th);
        msgErr = "";
      }
    }
    return msgErr;
  }
}
