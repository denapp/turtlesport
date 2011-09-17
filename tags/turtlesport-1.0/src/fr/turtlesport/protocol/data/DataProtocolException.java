package fr.turtlesport.protocol.data;

/**
 * @author Denis Apparicio
 * 
 */
public class DataProtocolException extends Exception {

  /**
   * 
   */
  public DataProtocolException() {
    super();
  }

  /**
   * @param message
   * @param cause
   */
  public DataProtocolException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public DataProtocolException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public DataProtocolException(Throwable cause) {
    super(cause);
  }

}
