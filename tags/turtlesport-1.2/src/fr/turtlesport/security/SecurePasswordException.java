package fr.turtlesport.security;

public class SecurePasswordException extends Exception {

  public SecurePasswordException() {
    super();
  }

  public SecurePasswordException(String message) {
    super(message);
  }

  public SecurePasswordException(Throwable cause) {
    super(cause);
  }

  public SecurePasswordException(String message, Throwable cause) {
    super(message, cause);
  }

}
