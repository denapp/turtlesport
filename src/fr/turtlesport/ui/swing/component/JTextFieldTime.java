package fr.turtlesport.ui.swing.component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * @author Denis Apparicio
 * 
 */
public class JTextFieldTime extends JFormattedTextField {

  public JTextFieldTime(boolean allowNull) {
    super();
    if (allowNull) {
      setFormatter(new DateFormatter(new SimpleDateFormat("HH:mm:ss")));
      DefaultFormatterFactory dff = new DefaultFormatterFactory(new BlankDateFormatter(new SimpleDateFormat("HH:mm:ss")));
      setFormatterFactory(dff);
    }
    else {
      DefaultFormatterFactory dff = new DefaultFormatterFactory(new DateFormatter(new SimpleDateFormat("HH:mm:ss")));
      setFormatterFactory(dff);
    }
    setValue(0, 0, 0);
  }

  public JTextFieldTime() {
    this(0, 0, 0);
    setValue(GregorianCalendar.getInstance().getTime());
  }

  /**
   * @param hour
   * @param mn
   * @param second
   */
  public JTextFieldTime(int hour, int mn, int second) {
    super(new SimpleDateFormat("HH:mm:ss"));
    setValue(hour, mn, second);
  }

  /**
   * @param hour
   * @param mn
   * @param second
   */
  public void setValue(int hour, int mn, int second) {
    Calendar cal = GregorianCalendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, mn);
    cal.set(Calendar.SECOND, second);
    cal.set(Calendar.MILLISECOND, 0);
    setValue(cal.getTime());
  }

  /**
   * 
   * Restitue le temps en secondes
   * 
   * @return le temps en secondes
   */
  public int getTime() {
    Date date = (Date) getValue();
    if (date == null) {
      return -1;
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    int time = 3600 * cal.get(Calendar.HOUR_OF_DAY) + 60
               * cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND);
    return time;
  }

  /**
   * Restitue les heures.
   * 
   * @return les heures.
   */
  public int getHour() {
    Calendar cal = Calendar.getInstance();
    cal.setTime((Date) getValue());
    return cal.get(Calendar.HOUR_OF_DAY);
  }

  /**
   * Restitue les secondes.
   * 
   * @return les secondes
   */
  public int getSecond() {
    Calendar cal = Calendar.getInstance();
    cal.setTime((Date) getValue());
    return cal.get(Calendar.SECOND);
  }

  /**
   * Restitue les heures
   * 
   * @return les heures
   */
  public int getMinute() {
    Calendar cal = Calendar.getInstance();
    cal.setTime((Date) getValue());
    return cal.get(Calendar.MINUTE);
  }

  /**
   * @author Denis Apparicio
   *
   */
  private class BlankDateFormatter extends DateFormatter {

    public BlankDateFormatter(DateFormat format) {
      super(format);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.text.InternationalFormatter#stringToValue(java.lang.String)
     */
    @Override
    public Object stringToValue(String text) throws ParseException {
      if (text.equals("") || text == null) {
        return null;
      }
      return super.stringToValue(text);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.text.InternationalFormatter#valueToString(java.lang.Object)
     */
    @Override
    public String valueToString(Object value) throws ParseException {
      if (value == null || value.equals("")) {
        return "";
      }
      return super.valueToString(value);
    }

  }
}
