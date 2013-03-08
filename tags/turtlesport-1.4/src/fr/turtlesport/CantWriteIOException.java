package fr.turtlesport;

import java.io.IOException;

public class CantWriteIOException extends IOException {

  public CantWriteIOException(String message, Throwable cause) {
    super(message, cause);
  }

  public CantWriteIOException(String message) {
    super(message);
  }

}
