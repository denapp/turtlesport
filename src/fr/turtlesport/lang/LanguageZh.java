package fr.turtlesport.lang;

import fr.turtlesport.ui.swing.img.ImagesRepository;

import javax.swing.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Denis Apparicio
 * 
 */
public final class LanguageZh extends AbstractLanguage {

  /** Instance unique. */
  private static LanguageZh singleton = new LanguageZh();

  private LanguageZh() {
  }
  
  /**
   * Restitue une instance de ce langage.
   * 
   * @return une instance de ce langage.
   */
  public static LanguageZh getInstance() {
    return singleton;
  }

  /* (non-Javadoc)
   * @see fr.turtlesport.lang.ILanguage#getEncoding()
   */
  public String getEncoding() {
    return "UTF-8";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getLocale()
   */
  public Locale getLocale() {
    return Locale.CHINA;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getFlag()
   */
  public ImageIcon getFlag() {
    final String name = "flag/zh.png";
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
    return "没有";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#yes()
   */
  public String yes() {
    return "是";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#cancel()
   */
  public String cancel() {
    return "取消";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#ok()
   */
  public String ok() {
    return "好";
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
