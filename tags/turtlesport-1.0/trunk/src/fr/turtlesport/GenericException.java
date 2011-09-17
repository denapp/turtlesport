package fr.turtlesport;

/**
 * @author Denis Apparicio
 * 
 */
public class GenericException extends Exception {

  /** Code erreur de cette exception. */
  private int errorCode;

  public GenericException() {
    super();
  }

  public GenericException(String message, Throwable cause) {
    super(message, cause);
  }

  public GenericException(String message) {
    super(message);
  }

  public GenericException(Throwable cause) {
    super(cause);
  }

  public GenericException(int codeErreur) {
    this.errorCode = codeErreur;
  }

  /**
   * Restitue le code erreur de cette exception.
   * 
   * @return le code erreur de cette exception.
   */
  public int getErrorCode() {
    return errorCode;
  }
}
