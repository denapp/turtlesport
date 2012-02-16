package fr.turtlesport.geo;

import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class GeoConvertException extends Exception {
  private String message;

  /**
   * 
   */
  public GeoConvertException() {
    super();
  }

  /**
   * @param cause
   */
  public GeoConvertException(Throwable cause) {
    super(cause);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    if (message == null) {
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), getClass());
      message = rb.getString("error");
    }
    return message;
  }

}
