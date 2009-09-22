/**
 * 
 */
package fr.turtlesport.unit;

import java.util.Calendar;
import java.util.Date;

/**
 * @author denis
 * 
 */
public final class TimeUnit {

  private TimeUnit() {
  }

  /**
   * Formatte les milli-secondes au format hh mn secondes.
   * 
   * @param milli
   *          les milli-secondes.
   * @return la date.
   */
  public static String formatMilliSecondeTime(long milli) {
    return formatHundredSecondeTime(milli / 10);
  }

  /**
   * Restitue le temps ecoulee pour l'heure de cette date.
   * 
   * @param milli
   *          les milli-secondes.
   * @return la date.
   */
  public static long computeTimeMilliSeconde(Date date) {
    if (date == null) {
      return 0;
    }
    
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date);

    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date);
    cal2.set(Calendar.HOUR, 0);
    cal2.set(Calendar.MINUTE, 0);
    cal2.set(Calendar.SECOND, 0);
    cal2.set(Calendar.MILLISECOND, 0);

    return cal1.getTimeInMillis() - cal2.getTimeInMillis();
  }

  /**
   * Formatte les centi&eagrave;mes de secondes au format hh mn secondes.
   * 
   * @param sec100
   *          les centie&eagrave;mes de seconde.
   * @return la date.
   */
  public static String formatHundredSecondeTime(long sec100) {
    StringBuilder st = new StringBuilder();

    long sec = sec100 / 100;

    long hour = sec / 3600;
    if (hour == 0) {
      st.append("00:");
    }
    else if (hour < 10) {
      st.append('0');
      st.append(hour);
      st.append(':');
    }
    else {
      st.append(hour);
      st.append(':');
    }

    long mn = (sec % 3600) / 60;
    if (mn == 0) {
      st.append("00:");
    }
    else if (mn < 10) {
      st.append('0');
      st.append(mn);
      st.append(':');
    }
    else {
      st.append(mn);
      st.append(':');
    }

    long s = (sec % 3600) % 60;
    if (s == 0) {
      st.append("00");
    }
    else if (s < 10) {
      st.append('0');
      st.append(s);
    }
    else {
      st.append(s);
    }

    return st.toString();
  }

}
