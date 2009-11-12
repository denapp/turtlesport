package fr.turtlesport.protocol;

import fr.turtlesport.GarminDevice;
import fr.turtlesport.UsbDecodeException;
import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.D1013CourseLimitsType;

/**
 * @author Denis Apparicio
 * 
 */
public class A1009CourseLimits extends AbstractTransfertProtocol {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(A1009CourseLimits.class);
  }

  /** PID COMMAND : Cmnd_Transfer_Course_Limits = 565. */
  private static final short    COMMAND_ID_TYPE   = 565;

  /** Pid reponse : Pid_Workout_Limits = 1066. */
  private static final short    PID_COURSE_LIMITS = 1066;

  /** Protocl associe a cette commande. */
  private static final String   PROTOCOL_NAME     = "A1009";

  /** Les donnees. */
  private D1013CourseLimitsType data;

  /**
   * 
   */
  public A1009CourseLimits() {
  }

  /**
   * @throws UsbProtocolException
   */
  public void retreive() throws UsbProtocolException, UsbDecodeException {
    log.debug(">>retreive");

    checkInit();

    try {
      // Envoi de la requete
      sendCommand();

      // Recuperation de la reponse
      UsbPacket packet = GarminDevice.getDevice().read();

      if (log.isDebugEnabled()) {
        log.debug("PacketType=" + packet.getPacketType());
        log.debug("PacketID=" + packet.getPacketID());
        log.debug("size=" + packet.getSize());
      }

      if (packet.getPacketID() != PID_COURSE_LIMITS) {
        throw new UsbDecodeException("pid innatendu");
      }

      data = new D1013CourseLimitsType();
      data.parse(packet);
    }
    finally {
      // Fermeture du garmin
      try {
        log.debug("Fermeture du garmin");
        GarminDevice.close();
      }
      catch (UsbProtocolException e) {
        log.error("", e);
      }
      // fin
      end();
    }

    log.debug("<<retreive");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.AbstractTransfertProtocol#getCommandIdType()
   */
  @Override
  public short getCommandIdType() {
    return COMMAND_ID_TYPE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.AbstractTransfertProtocol#getProtocolName()
   */
  @Override
  public String getProtocolName() {
    return PROTOCOL_NAME;
  }

  /**
   * Restitue les donnees.
   * 
   * @return les donnees.
   */
  public D1013CourseLimitsType getData() {
    return data;
  }

}
