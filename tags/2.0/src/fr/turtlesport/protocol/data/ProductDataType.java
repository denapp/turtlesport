package fr.turtlesport.protocol.data;

import java.util.ArrayList;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ByteUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class ProductDataType {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ProductDataType.class);
  }

  /** Product ID */
  private short               productID;

  private short               softwareVersion;

  private String[]            description;

  /**
   * @param packet
   * @throws UsbProtocolException
   */
  public ProductDataType(UsbPacket packet) throws UsbProtocolException {
    if (packet == null) {
      throw new NullPointerException();
    }
    parse(packet);
  }

  /**
   * 
   */
  private void parse(UsbPacket packet) throws UsbProtocolException {
    log.debug(">>parse");

    if (packet.getSize() == 0) {
      throw new UsbProtocolException(UsbProtocolException.ERR_PACKET_SIZE_INVALID);
    }

    byte[] data = packet.getData();

    productID = ByteUtil.toShort(data[1], data[2]);
    softwareVersion = ByteUtil.toShort(data[3], data[4]);

    if (packet.getSize() > 4) {
      ArrayList<String> list = new ArrayList<String>();

      for (int i = 4, deb = 4, len = 0; i < packet.getSize(); i++, len++) {
        if (data[i] == (byte) 0) {
          list.add(new String(data, deb, len));
          deb = ++i;
          len = 0;
        }
      }

      description = new String[list.size()];
      if (list.size() > 0) {
        list.toArray(description);
      }
    }

    log.debug("<<parse");
  }

  /**
   * Restitue la description.
   * 
   * @return la a description.
   */
  public String[] getDescription() {
    return description;
  }

  /**
   * Restitue le product ID.
   * 
   * @return le product ID.
   */
  public short getProductID() {
    return productID;
  }

  /**
   * Restitue la version du software.
   * 
   * @return la version du software.
   */
  public short getSoftwareVersion() {
    return softwareVersion;
  }

}
