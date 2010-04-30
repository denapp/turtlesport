package fr.turtlesport.ui.swing.component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;

/**
 * @author Denis Apparicio
 * 
 */
public class JTextFieldTime extends JFormattedTextField {

  public JTextFieldTime() {
    this(0, 0, 0);
    setValue(GregorianCalendar.getInstance().getTime());
  }

  /**
   * @param hour
   * @param mn
   * @param seconde
   */
  public JTextFieldTime(int hour, int mn, int seconde) {
    super(new SimpleDateFormat("HH:mm:ss"));
    Calendar cal = GregorianCalendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, mn);
    cal.set(Calendar.SECOND, seconde);
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
    Calendar cal = Calendar.getInstance();
    cal.setTime((Date) getValue());

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
}
