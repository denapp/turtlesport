package fr.turtlesport.protocol;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.device.garmin.GarminUsbDevice;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.ProductDataType;
import fr.turtlesport.protocol.data.ProtocolCapability;

/**
 * @author Denis Apparicio
 * 
 */
public class AppProtocolCommand implements ICommand {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AppProtocolCommand.class);
  }

  private static final short  PID_PRODUCT_RQST   = 254;

  private static final short  PID_PRODUCT_DATA   = 255;

  private static final short  PID_PROTOCOL_ARRAY = 253;

  /** Le produit Data Type */
  private ProductDataType     pdtDataType;

  /** Le produit Data Type */
  private ProtocolCapability  protoCapability;

  /**
   * 
   */
  public AppProtocolCommand() {
  }

  /**
   * Restitue le produit data type.
   * 
   * @return le produit data type.
   */
  public ProductDataType getPdtDataType() {
    return pdtDataType;
  }

  /**
   * Restitue les protocoles.
   * 
   * @return les protocoles.
   */
  public ProtocolCapability getProtoCapability() {
    return protoCapability;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.ICommand#retreive()
   */
  public void retreive() throws UsbProtocolException {
    log.debug(">>retreive");

    UsbPacket packet;

    // Envoi de la requete
    send();

    // Recuperation de la reponse
    while (true) {
      packet = GarminUsbDevice.getDevice().read();
      if (packet.getPacketType() == ICommand.PACKET_TYPE_APP_LAYER) {
        if (packet.getPacketID() == PID_PRODUCT_DATA) {
          // Recuperation Pid_Product_Data
          pdtDataType = new ProductDataType(packet);
        }
        else if (packet.getPacketID() == PID_PROTOCOL_ARRAY) {
          // recuperation du Pid_Protocol_Array
          protoCapability = new ProtocolCapability(packet);
          // derniere trame
          break;
        }
      }
    }

    log.debug("<<retreive");
  }

  /**
   * Envoi de la requete.
   * 
   * @throws UsbProtocolException
   */
  private void send() throws UsbProtocolException {
    log.debug(">>send");

    UsbPacket packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                     PID_PRODUCT_RQST);
    GarminUsbDevice.getDevice().send(packet);

    log.debug("<<send");
  }

}
