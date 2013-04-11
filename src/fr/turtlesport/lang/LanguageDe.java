package fr.turtlesport.lang;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Denis Apparicio
 * 
 */
public final class LanguageDe extends AbstractLanguage {

  /** Instance unique. */
  private static LanguageDe singleton = new LanguageDe();

  private LanguageDe() {
  }

  /**
   * Restitue une instance de ce langage.
   * 
   * @return une instance de ce langage.
   */
  public static LanguageDe getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getEncoding()
   */
  public String getEncoding() {
    return "ISO-8859-1";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getLocale()
   */
  public Locale getLocale() {
    return Locale.GERMAN;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getDateFormatter()
   */
  public DateFormat getDateFormatter() {
    return new SimpleDateFormat("dd/MM/yyyy", getLocale());
  }

  /* (non-Javadoc)
   * @see fr.turtlesport.lang.ILanguage#getDateTimeShortFormatter()
   */
  public DateFormat getDateTimeShortFormatter() {
    return new SimpleDateFormat("dd/MM/yy HH:mm", getLocale());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getDateTimeShortWithoutYearFormatter()
   */
  public DateFormat getDateTimeShortWithoutYearFormatter() {
    return new SimpleDateFormat("dd/MM HH:mm", getLocale());
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
    return "No";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#yes()
   */
  public String yes() {
    return "Ja";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#cancel()
   */
  public String cancel() {
    return "Abbrechen";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#ok()
   */
  public String ok() {
    return "OK";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#hasWebSiteTranslate()
   */
  public boolean hasWebSiteTranslate() {
    return false;
  }
}
