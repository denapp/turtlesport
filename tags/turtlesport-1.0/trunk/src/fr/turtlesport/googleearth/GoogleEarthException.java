package fr.turtlesport.googleearth;

import java.util.ResourceBundle;

import fr.turtlesport.GenericException;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 */
public class GoogleEarthException extends GenericException {

  /** Googleearth absent. */
  public static final int ABSENT         = 1;

  /** Impossible d'executer la commande googleearth. */
  public static final int EXEC           = 2;

  /** Fichier googleearth inexistant. */
  public static final int FILE_NOT_FOUND = 3;

  private String          message;

  /**
   * 
   * @param codeErreur
   * 
   */
  protected GoogleEarthException(int codeErreur) {
    super(codeErreur);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    if (message == null) {
      ILanguage lang = LanguageManager.getManager().getCurrentLang();
      ResourceBundle rb = ResourceBundleUtility.getBundle(lang, getClass());
      message = rb.getString(Integer.toString(getErrorCode()));
    }
    return message;
  }

}
