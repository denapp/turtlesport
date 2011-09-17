package fr.turtlesport.protocol;

import fr.turtlesport.UsbDecodeException;
import fr.turtlesport.UsbProtocolException;

/**
 * @author Denis Apparicio
 * 
 */
public interface ICommand {

  /** Packet type usb protocol. */
  byte  USB_PROTOCOL_LAYER    = 0;

  /** Packet type Application. */
  byte  PACKET_TYPE_APP_LAYER = 0x14;

  /** Packet ID Command. */
  short PID_COMMAND_DATA      = 10;

  /** Packet ID Records. */
  short PID_RECORD            = 27;

  /** Packet ID Xfer Cmplt. */
  short PID_XFER_CMPLT        = 12;

  /**
   * Execute la commande.
   * 
   * @throws UsbProtocolException
   * @throws UsbDecodeException
   */
  void retreive() throws UsbProtocolException, UsbDecodeException;
}
