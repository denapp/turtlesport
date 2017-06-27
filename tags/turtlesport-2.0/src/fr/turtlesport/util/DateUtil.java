package fr.turtlesport.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Denis Apparicio
 * 
 */
public class DateUtil {

  private DateUtil() {
  }

  /**
   * D&eacute;termine si la date est la date du jour.
   * 
   * @param date
   *          la date.
   * @return <code>true</code> si la date est la date du jour,
   *         <code>false</code> sinon.
   */
  public static boolean isDayDate(Date date) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();

    cal1.setTime(date);

    if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
        && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
      return true;
    }

    return false;
  }

}
