package fr.turtlesport.lang;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.ImageIcon;

import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * @author Denis Apparicio
 * 
 */
public final class LanguageUs extends AbstractLanguage {

  /** Instance unique. */
  private static LanguageUs singleton = new LanguageUs();

  private LanguageUs() {
  }
  
  /**
   * Restitue une instance de ce langage.
   * 
   * @return une instance de ce langage.
   */
  public static LanguageUs getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getLocale()
   */
  public Locale getLocale() {
    return Locale.US;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getFlag()
   */
  public ImageIcon getFlag() {
    String name = "flag/us.png";
    URL url = ImagesRepository.class.getResource(name);
    return (url == null) ? null : new ImageIcon(url);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getName()
   */
  public String getName() {
    
    return getLocale().getDisplayLanguage(LanguageManager.getManager()
        .getCurrentLang().getLocale());
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getDateFormatter()
   */
  public DateFormat getDateFormatter() {
    return new SimpleDateFormat("MM/dd/yyyy", getLocale());
  }

  /* (non-Javadoc)
   * @see fr.turtlesport.lang.ILanguage#getDateTimeShortFormatter()
   */
  public DateFormat getDateTimeShortFormatter() {
    return new SimpleDateFormat("MM/dd/yy HH:mm", getLocale());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getDateTimeShortWithoutYearFormatter()
   */
  public DateFormat getDateTimeShortWithoutYearFormatter() {
    return new SimpleDateFormat("MM/dd HH:mm", getLocale());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getDateTimeFormatterWithoutSep()
   */
  public DateFormat getDateTimeFormatterWithoutSep() {
    return new SimpleDateFormat("MMddyyyy_kkmmss", getLocale());
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
    return "Yes";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#cancel()
   */
  public String cancel() {
    return "Cancel";
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
    return true;
  }
  
  
}
