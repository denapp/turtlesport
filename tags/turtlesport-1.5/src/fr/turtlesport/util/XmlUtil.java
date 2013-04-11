package fr.turtlesport.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Denis Apparicio
 * 
 */
public final class XmlUtil {

  private XmlUtil() {
  }

  private static SimpleDateFormat dateFormat;
  static {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    dateFormat.setLenient(false);
  }
     
  /**
   * Restitue une date.
   * 
   * @param value
   * @return
   */
  public static Date getTime(String value) {
    if (value == null || value.length() < 19 || value.charAt(4) != '-'
        || value.charAt(7) != '-' || value.charAt(10) != 'T'
        || value.charAt(13) != ':' || value.charAt(16) != ':') {
      return null;
    }

    Calendar calendar = Calendar.getInstance();
    Date date;
    try {
      synchronized (dateFormat) {
        date = dateFormat.parse(value.substring(0, 19) + ".000Z");
      }
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e.toString());
    }
    int pos = 19;

    // parse milliseconds (optional)
    if (pos < value.length() && value.charAt(pos) == '.') {
      int milliseconds = 0;
      int start = ++pos;
      while (pos < value.length() && Character.isDigit(value.charAt(pos))) {
        pos++;
      }
      String decimal = value.substring(start, pos);
      if (decimal.length() == 3) {
        milliseconds = Integer.parseInt(decimal);
      }
      else if (decimal.length() < 3) {
        milliseconds = Integer.parseInt((decimal + "000").substring(0, 3));
      }
      else {
        milliseconds = Integer.parseInt(decimal.substring(0, 3));
        if (decimal.charAt(3) >= '5') {
          ++milliseconds;
        }
      }

      // Ajoute les millisecondes
      date.setTime(date.getTime() + milliseconds);
    }

    // parse timezone (optional)
    if (pos + 5 < value.length()
        && (value.charAt(pos) == '+' || (value.charAt(pos) == '-'))) {
      if (!Character.isDigit(value.charAt(pos + 1))
          || !Character.isDigit(value.charAt(pos + 2))
          || value.charAt(pos + 3) != ':'
          || !Character.isDigit(value.charAt(pos + 4))
          || !Character.isDigit(value.charAt(pos + 5))) {
        throw new IllegalArgumentException();
      }
      int hours = (value.charAt(pos + 1) - '0') * 10 + value.charAt(pos + 2)
                  - '0';
      int mins = (value.charAt(pos + 4) - '0') * 10 + value.charAt(pos + 5)
                 - '0';
      int milliseconds = (hours * 60 + mins) * 60 * 1000;

      // subtract milliseconds from current date to obtain GMT
      if (value.charAt(pos) == '+') {
        milliseconds = -milliseconds;
      }
      date.setTime(date.getTime() + milliseconds);
      pos += 6;
    }
    if (pos < value.length() && value.charAt(pos) == 'Z') {
      pos++;
      calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    if (pos < value.length()) {
      throw new IllegalArgumentException(value);
    }
    calendar.setTime(date);

    return calendar.getTime();
  }

}
