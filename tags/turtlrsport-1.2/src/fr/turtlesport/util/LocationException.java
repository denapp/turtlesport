package fr.turtlesport.util;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageManager;

/**
 * @author Denis Apparicio
 * 
 */
public class LocationException extends Exception {
  public static final int READ   = 0;

  public static final int WRITE  = 1;

  public static final int CREATE = 2;

  private String          message;

  /**
   * 
   * @param codeErreur
   * 
   */
  protected LocationException(int action, File file) {
    super();
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), LocationException.class);

    message = MessageFormat.format(rb.getString(Integer.toString(action)), file
        .getAbsolutePath());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    return message;
  }

}
