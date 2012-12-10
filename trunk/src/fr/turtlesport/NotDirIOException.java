package fr.turtlesport;

import java.io.IOException;

public class NotDirIOException extends IOException {

  public NotDirIOException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotDirIOException(String message) {
    super(message);
  }

}
