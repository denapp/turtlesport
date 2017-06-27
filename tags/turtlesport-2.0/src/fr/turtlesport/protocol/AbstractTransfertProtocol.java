package fr.turtlesport.protocol;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.device.garmin.GarminUsbDevice;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ByteUtil;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractTransfertProtocol {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(AbstractTransfertProtocol.class);
  }

  /** Packet type usb protocol. */
  public static final byte    USB_PROTOCOL_LAYER    = 0;

  /** Packet type Application. */
  public static final byte    PACKET_TYPE_APP_LAYER = 0x14;

  /** Packet ID Command. */
  public static final short   PID_COMMAND_DATA      = 10;

  /** Packet ID Records. */
  public static final short   PID_RECORDS           = 27;

  /** Packet ID Xfer Cmplt. */
  public static final short   PID_XFER_CMPLT        = 12;

  private boolean             isInit                = false;

  /**
   * Initialisation avant r&eacute;cup&eacute;ration des courses.
   * 
   * @throws UsbProtocolException
   */
  public void init() throws UsbProtocolException {
    if (isInit) {
      return;
    }

    // Initialisation
    GarminUsbDevice.init();

    // Verification presence du protocol
    if (!GarminUsbDevice.getDevice().containsProtocol(getProtocolName())) {
      log.error("Pas de protocole " + getProtocolName());
      throw new UsbProtocolException(UsbProtocolException.ERR_NO_PROTOCOL);
    }

    isInit = true;
  }

  /**
   * Initialisation avant r&eacute;cup&eacute;ration des courses.
   * 
   * @throws UsbProtocolException
   */
  public void abortTransfert() throws UsbProtocolException {
    if (isInit) {
      return;
    }
    log.info("abortTransfert");
    // Cmnd_Abort_Transfer = 0 abort current transfer
    sendCommand(0);
  }

  /**
   * Retitue le command ID type de la commande.
   * 
   * @return le command ID type de la commande.
   */
  public abstract short getCommandIdType();

  /**
   * Retitue le protocole associe a cette commande.
   * 
   * @return le protocole associe a cette commande.
   */
  public abstract String getProtocolName();

  /**
   * Envoie de la commande.
   * 
   * @throws UsbProtocolException
   */
  public void sendCommand() throws UsbProtocolException {
    log.debug(">>sendCommand");

    // construction du packet
    UsbPacket packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                     ICommand.PID_COMMAND_DATA);
    packet.setData(ByteUtil.to2Bytes(getCommandIdType()));

    // envoie du packet
    GarminUsbDevice.getDevice().send(packet);

    log.debug("<<sendCommand");
  }

  /**
   * Envoie de la commande.
   * 
   * @throws UsbProtocolException
   */
  public void sendCommand(int commandId) throws UsbProtocolException {
    log.debug(">>sendCommand commandId=" + commandId);

    // construction du packet
    UsbPacket packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                     ICommand.PID_COMMAND_DATA);
    packet.setData(ByteUtil.to2Bytes(commandId));

    // envoie du packet
    GarminUsbDevice.getDevice().send(packet);

    log.debug("<<sendCommand");
  }

  /**
   * Envoie de la commande.
   * 
   * @throws UsbProtocolException
   */
  public void sendCommand(UsbPacket packet) throws UsbProtocolException {
    log.debug(">>sendCommand");

    if (packet == null) {
      throw new IllegalArgumentException("packet est null");
    }
    GarminUsbDevice.getDevice().send(packet);

    log.debug("<<sendCommand");
  }

  /**
   * Envoie de la commande Pid_Records.
   * 
   * @throws UsbProtocolException
   */
  public void sendRecord(int size) throws UsbProtocolException {
    log.debug(">>sendRecord");

    UsbPacket packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                     ICommand.PID_COMMAND_DATA);

    byte[] data = new byte[2];
    data[0] = (byte) (getCommandIdType() & 0x00FF);
    data[1] = (byte) ((getCommandIdType() & 0xFF00) >> 8);
    packet.setData(data);

    GarminUsbDevice.getDevice().send(packet);

    log.debug("<<sendRecord");
  }
  
  /**
   * Lecture PID record.
   */
  public int retrievePidRecords() throws UsbProtocolException {
    log.debug(">>retrievePidRecords");

    int nbPaquet = 0;

    // Lecture
    UsbPacket packet = GarminUsbDevice.getDevice().read();

    // Premier paquet Pid_Records (spec 5.4)
    if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
        && packet.getPacketID() == PID_RECORDS) {
      nbPaquet = ByteUtil.toShort(packet.getData()[0], packet.getData()[1]);
      log.debug("nbPaquet=" + nbPaquet);
    }
    else {
      log.warn("packet.getPacketType()=" + packet.getPacketType());
      log.warn("packet.getPacketID()=" + packet.getPacketID());
      log.warn("PidRecords attendu");
    }

    log.debug("<<retrievePidRecords");
    return nbPaquet;
  }


  /**
   * Initialisation avant r&eacute;cup&eacute;ration des courses.
   * 
   * @throws UsbProtocolException
   */
  protected void end() {
    isInit = false;
  }

  /**
   * 
   */
  protected void checkInit() {
    if (!isInit) {
      throw new RuntimeException("begin first");
    }
    isInit = false;
  }

}
