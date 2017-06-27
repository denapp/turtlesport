package fr.turtlesport;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Library;

/**
 * @author Denis Apparicio
 */
public final class UsbProtocol2 {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UsbProtocol2.class);
  }

  /** GRAMIN VID */
  private int GARMIN_USB_VID  = 0x091e;
  
  /** GARMIN PID */
  private int GARMIN_USB_PID = 0x0003;

  /** Nom de la JNI. */
  private static final String LIBRARY_NAME = "turtleUsbjni";

  /** MD5 de la JNI Linux 32 bits */
  private String              digestLibrary32bits;

  /** MD5 de la JNI Linux 32 bits */
  private String              digestLibrary64bits;

  /** Instance unique */
  private static UsbProtocol2  singleton    = new UsbProtocol2();

  /**
   * Pour linux on copie les librairies dans .turtlesport. Moins problematique
   * pour la gestion des paquetages rpm et debian en 32bits et 64bits
   */
  private UsbProtocol2() {
    log.debug(">>UsbProtocol");

    // chargement de la librairie
    Library.load(UsbPacket.class, LIBRARY_NAME);

    log.debug("<<UsbProtocol");
  }

  /**
   * Restitue une instance unique.
   * 
   * @return une instance unique.
   */
  public static UsbProtocol2 getInstance() {
    return singleton;
  }

  /**
   * Initialisation USB.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  public synchronized void init() throws UsbProtocolException {
    init(GARMIN_USB_VID, GARMIN_USB_PID);
  }

  
  /**
   * Initialisation USB.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  public synchronized native void init(int idVendor, int idProduct) throws UsbProtocolException;

  /**
   * Close USB.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  public synchronized void close() throws UsbProtocolException {
    log.debug(">>close");
    closeInner();
    log.debug("<<close");
  }

  /**
   * Envoie un packet au device USB.
   * 
   * @throws UsbProtocolException
   */
  public synchronized void send(UsbPacket packet) throws UsbProtocolException {
    log.debug(">>send");

    if (packet == null) {
      throw new NullPointerException("packet est null");
    }

    byte[] buf = packet.makebuffer();
    log.debug(buf, "send");
    sendInner(buf);

    log.debug("<<send");
  }

  /**
   * Recoie un packet du device USB.
   * 
   * @throws UsbProtocolException
   */
  public synchronized UsbPacket read() throws UsbProtocolException {
    byte[] buf = readInner();
    log.debug(buf, "read");

    return new UsbPacket(buf);
  }

  /**
   * Envoi un packet au device USB.
   * 
   * @throws UsbProtocolException.
   */
  private synchronized native void sendInner(byte[] packet) throws UsbProtocolException;

  /**
   * Envoie un packet au device USB.
   * 
   * @throws UsbProtocolException.
   */
  private synchronized native byte[] readInner() throws UsbProtocolException;

  /**
   * Close USB.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  private synchronized native void closeInner() throws UsbProtocolException;

}
