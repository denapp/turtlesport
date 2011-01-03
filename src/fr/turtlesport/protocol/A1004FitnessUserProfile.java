package fr.turtlesport.protocol;

import fr.turtlesport.GarminDevice;
import fr.turtlesport.UsbDecodeException;
import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.D1004FitnessUserProfileType;

/**
 * @author Denis Apparicio
 * 
 */
public class A1004FitnessUserProfile extends AbstractTransfertProtocol {
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(A1004FitnessUserProfile.class);
  }

  /** Transfer fitness user profile : Cmnd_Transfer_Fitness_User_Profile = 453. */
  private static final short          COMMAND_ID_TYPE          = 453;

  /** Pid reponse fitness user profile : Pid_Fitness_User_Profile = 993. */
  private static final short          PID_FITNESS_USER_PROFILE = 993;

  /** Protocl associe a cette commande. */
  private static final String         PROTOCOL_NAME            = "A1004";

  private D1004FitnessUserProfileType data;

  /**
   * 
   */
  public A1004FitnessUserProfile() {
  }

  /**
   * Mis &agrave; jour des informations de fitness du garmin.
   * 
   * @throws UsbProtocolException
   * @throws GarminProtocolException
   */
  public void update() throws UsbProtocolException, GarminProtocolException {
    log.debug(">>update");

    // serialisation
    UsbPacketOutputStream out = new UsbPacketOutputStream();
    data.serialize(out);

    // construction du packet
    UsbPacket packet = new UsbPacket();
    packet.setPacketType(PACKET_TYPE_APP_LAYER);
    packet.setPacketID(PID_FITNESS_USER_PROFILE);
    packet.setData(out.toByteArray());

    // Envoi de la requete
    sendCommand(packet);

    log.debug("<<update");
  }

  /**
   * R&eacute;cup&egrave;re les informations de fitness du garmin.
   * 
   * @throws UsbProtocolException
   * @throws UsbDecodeException
   */
  public void retrieve() throws UsbProtocolException, UsbDecodeException {
    log.debug(">>retrieve");

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

      if (packet.getPacketID() != PID_FITNESS_USER_PROFILE) {
        throw new UsbDecodeException("pid innatendu");
      }

      data = new D1004FitnessUserProfileType();
      data.parse(new UsbPacketInputStream(packet));
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

    log.debug("<<retrieve");
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
   * Restitue les donn&eacute;es.
   * 
   * @return les donn&eacute;es.
   */
  public D1004FitnessUserProfileType getData() {
    return data;
  }

  /**
   * Valorise les donn&eacute;es.
   * 
   * @param data
   *          la nouvelle valeur.
   */
  public void setData(D1004FitnessUserProfileType data) {
    this.data = data;
  }

}
