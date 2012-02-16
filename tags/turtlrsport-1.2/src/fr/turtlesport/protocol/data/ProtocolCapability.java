package fr.turtlesport.protocol.data;

import java.util.ArrayList;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class ProtocolCapability {
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ProtocolCapability.class);
  }

  /** Liste des protocoles. */
  private ArrayList<ProtocolDataType> listProtocolDataType;

  /**
   * @param packet
   * @throws UsbProtocolException
   */
  public ProtocolCapability(UsbPacket packet) throws UsbProtocolException {
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

    listProtocolDataType = new ArrayList<ProtocolDataType>();

    byte[] data = packet.getData();
    log.debug("data.length=" + data.length);
    for (int i = 0; i < data.length - 1; i += 3) {
      listProtocolDataType.add(new ProtocolDataType(data[i],
                                                    data[i + 1],
                                                    data[i + 2]));
    }
    log.debug("<<parse");
  }

  /**
   * Restitue la liste des protocoles.
   * 
   * @return la liste des protocoles.
   */
  public ArrayList<ProtocolDataType> getListProtocolDataType() {
    return listProtocolDataType;
  }

  /**
   * D�termine si ce ce protocle est pr�sent dans la liste.
   * 
   * @param protocol
   * @return
   */
  public boolean containsProtocol(String protocol) {
    if (protocol == null || listProtocolDataType == null) {
      return false;
    }

    // Liste des Protocoles
    for (ProtocolDataType data : listProtocolDataType) {
      if (protocol.equals(data.toString())) {
        return true;
      }
    }

    return false;

  }

}
