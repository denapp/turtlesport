package fr.turtlesport.protocol;

import fr.turtlesport.GarminDevice;
import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class StartSessionCommand implements ICommand {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(StartSessionCommand.class);
  }

  /** Pid demande de start session */
  private static final byte          PID_START_SESSION  = 5;

  /** Pid de reponse session demarre . */
  private static final byte          PID_SESSION_STATED = 6;

  /** Instance unique. */
  private static StartSessionCommand singleton          = new StartSessionCommand();

  /**
   * 
   */
  private StartSessionCommand() {
  }

  /**
   * Restitue une instance unique de Start Session.
   * 
   * @return
   */
  public static StartSessionCommand getInstance() {
    return singleton;
  }

  /**
   * @throws UsbProtocolException
   */
  public void retreive() throws UsbProtocolException {
    log.debug(">>execute");
    UsbPacket packet;

    // Envoi de la requete
    send();

    // On attend jusqu'a ce que le device demarre la session.
    while (true) {
      packet = GarminDevice.getDevice().read();
      if (packet.getPacketType() == ICommand.USB_PROTOCOL_LAYER
          && packet.getPacketID() == PID_SESSION_STATED) {
        // session demarre
        break;
      }
    }

    log.debug("<<execute");
  }

  /**
   * Envoi de la requete.
   * 
   * @throws UsbProtocolException
   */
  private void send() throws UsbProtocolException {
    log.debug(">>send");

    UsbPacket packet = new UsbPacket(USB_PROTOCOL_LAYER, PID_START_SESSION);
    GarminDevice.getDevice().send(packet);

    log.debug("<<send");
  }
  
}
