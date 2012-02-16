package fr.turtlesport;

/**
 * Exception lors de la configuration.
 * 
 * @author Denis Apparicio
 * 
 */
public class ConfigurationException extends Exception {

  public ConfigurationException() {
    super();
  }

  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(Throwable cause) {
    super(cause);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

}
