package fr.turtlesport;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Library;

/**
 * @author Denis Apparicio
 */
public final class UsbProtocol {
  private static TurtleLogger    log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UsbProtocol.class);
  }
  
  /** Nom de la JNI. */
  private static final String LIBRARY_NAME = "turtleUsbjni";
  

  /** Instance unique */
  private static UsbProtocol  singleton    = new UsbProtocol();

  /**
   * 
   */
  private UsbProtocol() {
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
  protected static UsbProtocol getInstance() {
    return singleton;
  }

  /**
   * Initialisation USB.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  public synchronized native void init() throws UsbProtocolException;

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
