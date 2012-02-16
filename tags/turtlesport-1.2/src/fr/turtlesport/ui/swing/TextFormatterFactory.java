package fr.turtlesport.ui.swing;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class TextFormatterFactory {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(TextFormatterFactory.class);
  }

  private TextFormatterFactory() {
  }

  /**
   * Formatteur.
   * 
   * @param s
   *          le mask.
   * @return
   */
  public static DefaultFormatterFactory formatter(String s) {
    MaskFormatter mf;
    try {
      mf = new MaskFormatter(s);
      mf.setCommitsOnValidEdit(true);
      return new DefaultFormatterFactory(mf);
    }
    catch (ParseException e) {
      log.error("", e);
    }
    return null;
  }

  /**
   * Formatteur date francaise.
   * 
   * @return
   */
  public static DefaultFormatterFactory createDate() {
    DateFormatter df = new DateFormatter(LanguageManager.getManager()
        .getCurrentLang().getDateFormatter());
    df.setCommitsOnValidEdit(true);
    return new DefaultFormatterFactory(df);
  }

  /**
   * Formatteur date francaise.
   * 
   * @return
   */
  public static DefaultFormatterFactory createDate(ILanguage lang) {
    DateFormatter df = new DateFormatter(lang.getDateFormatter());
    df.setCommitsOnValidEdit(true);
    return new DefaultFormatterFactory(df);
  }

  /**
   * Formatteur heure.
   * 
   * @return
   */
  public static DefaultFormatterFactory createTime() {
    DateFormatter df = new DateFormatter(new SimpleDateFormat("mm:ss"));
    df.setCommitsOnValidEdit(true);
    return new DefaultFormatterFactory(df);
  }

  /**
   * Formatteur.
   * 
   * @param size
   * 
   * @return
   */
  public static DefaultFormatterFactory createNumber(int size) {
    StringBuilder st = new StringBuilder();
    for (int i = 0; i < size; i++) {
      st.append("#");
    }
    return formatter(st.toString());
  }

  /**
   * Formatteur.
   * 
   * @param size
   * 
   * @return
   */
  public static DefaultFormatterFactory createNumber(NumberFormat format) {
    NumberFormatter nf = new NumberFormatter(format);
    nf.setCommitsOnValidEdit(true);
    return new DefaultFormatterFactory(nf);
  }

  /**
   * Formatteur.
   * 
   * @param size
   * 
   * @return
   */
  public static DefaultFormatterFactory createNumber(int nbDigit, int nbFraction) {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator(' ');

    DecimalFormat format = new DecimalFormat();
    format.setMaximumIntegerDigits(nbDigit);
    format.setMaximumFractionDigits(nbFraction);
    format.setDecimalFormatSymbols(symbols);

    NumberFormatter nf = new NumberFormatter(format);
    nf.setCommitsOnValidEdit(true);
    return new DefaultFormatterFactory(nf);
  }

  /**
   * Formatteur.
   * 
   * @param size
   * 
   * @return
   */
  public static DefaultFormatterFactory createLetterUpper(int size) {
    StringBuilder st = new StringBuilder();
    for (int i = 0; i < size; i++) {
      st.append('U');
    }
    return formatter(st.toString());
  }

  /**
   * Formatteur.
   * 
   * @param size
   *          le mask.
   * @return
   */
  public static DefaultFormatterFactory createLetterLower(int size) {
    StringBuilder st = new StringBuilder();
    for (int i = 0; i < size; i++) {
      st.append('L');
    }
    return formatter(st.toString());
  }

  /**
   * Formatteur.
   * 
   * @param size
   *          le mask.
   * @return
   */
  public static DefaultFormatterFactory createLetter(int size) {
    StringBuilder st = new StringBuilder();
    for (int i = 0; i < size; i++) {
      st.append('?');
    }
    return formatter(st.toString());
  }

  /**
   * Formatteur.
   * 
   * @param size
   *          le mask.
   * @return
   */
  public static DefaultFormatterFactory createLetterDigit(int size) {
    StringBuilder st = new StringBuilder();
    for (int i = 0; i < size; i++) {
      st.append('A');
    }
    return formatter(st.toString());
  }

}
