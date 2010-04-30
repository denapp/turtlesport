package fr.turtlesport.lang;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Denis Apparicio
 * 
 */
public final class LanguageFr extends AbstractLanguage {

  /** Instance unique. */
  private static LanguageFr singleton = new LanguageFr();

  private LanguageFr() {
  }

  /**
   * Restitue une instance de ce langage.
   * 
   * @return une instance de ce langage.
   */
  public static LanguageFr getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getLocale()
   */
  public Locale getLocale() {
    return Locale.FRENCH;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getDateFormatter()
   */
  public DateFormat getDateFormatter() {
    return new SimpleDateFormat("dd/MM/yyyy", getLocale());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getDateTimeFormatterWithoutSep()
   */
  public DateFormat getDateTimeFormatterWithoutSep() {
    return new SimpleDateFormat("ddMMyyyy_kkmmss", getLocale());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#no()
   */
  public String no() {
    return "Non";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#yes()
   */
  public String yes() {
    return "Oui";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#cancel()
   */
  public String cancel() {
    return "Annuler";
  }

}
