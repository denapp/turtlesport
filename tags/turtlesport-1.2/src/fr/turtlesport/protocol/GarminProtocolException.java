package fr.turtlesport.protocol;

/**
 * @author Denis Apparicio
 * 
 */
public class GarminProtocolException extends Exception {

  /**
   * 
   */
  public GarminProtocolException() {
    super();
  }

  /**
   * @param message
   * @param cause
   */
  public GarminProtocolException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public GarminProtocolException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public GarminProtocolException(Throwable cause) {
    super(cause);
  }

}
