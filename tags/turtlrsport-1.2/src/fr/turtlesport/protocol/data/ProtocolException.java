package fr.turtlesport.protocol.data;

/**
 * @author Denis Apparicio
 * 
 */
public class ProtocolException extends Exception {

  /**
   * 
   */
  public ProtocolException() {
    super();
  }

  /**
   * @param message
   * @param cause
   */
  public ProtocolException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public ProtocolException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public ProtocolException(Throwable cause) {
    super(cause);
  }

}
