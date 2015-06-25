package fr.turtlesport.protocol;

import fr.turtlesport.UsbDecodeException;
import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.device.garmin.GarminUsbDevice;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.D1005WorkoutLimits;

/**
 * @author Denis Apparicio
 * 
 */
public class A1005WorkoutLimits extends AbstractTransfertProtocol {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(A1005WorkoutLimits.class);
  }

  /** Transfer workout limits : Cmnd_Transfer_Workout_Limits = 453. */
  private static final short  COMMAND_ID_TYPE    = 454;

  /** Pid reponse : Pid_Workout_Limits = 994. */
  private static final short  PID_WORKOUT_LIMITS = 994;

  /** Protocl associe a cette commande. */
  private static final String PROTOCOL_NAME      = "A1005";

  /** Les donnees. */
  private D1005WorkoutLimits  data;

  /**
   * 
   */
  public A1005WorkoutLimits() {
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
      UsbPacket packet = GarminUsbDevice.getDevice().read();

      if (log.isDebugEnabled()) {
        log.debug("PacketType=" + packet.getPacketType());
        log.debug("PacketID=" + packet.getPacketID());
        log.debug("size=" + packet.getSize());
      }

      if (packet.getPacketID() != PID_WORKOUT_LIMITS) {
        throw new UsbDecodeException("pid innatendu");
      }

      data = new D1005WorkoutLimits();
      data.parse(packet);
    }
    finally {
      // Fermeture du garmin
      try {
        log.debug("Fermeture du garmin");
        GarminUsbDevice.close();
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
  public D1005WorkoutLimits getData() {
    return data;
  }

}
