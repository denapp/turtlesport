package fr.turtlesport.geo;

/**
 * @author Denis Apparicio
 * 
 */
public class GpsDecodeException extends Exception {

  /**
   * 
   */
  public GpsDecodeException() {
    super();
  }

  /**
   * @param message
   * @param cause
   */
  public GpsDecodeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public GpsDecodeException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public GpsDecodeException(Throwable cause) {
    super(cause);
  }

}
