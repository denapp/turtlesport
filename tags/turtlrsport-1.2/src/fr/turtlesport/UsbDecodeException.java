package fr.turtlesport;

import java.io.IOException;

/** 
 * @author Denis Apparicio
 */
public class UsbDecodeException extends IOException {

  /**
   * 
   */
  public UsbDecodeException() {
  }

  /**
   * @param message
   */
  public UsbDecodeException(String message) {
    super(message);
  }

}
