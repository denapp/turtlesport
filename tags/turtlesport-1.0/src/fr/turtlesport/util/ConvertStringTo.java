package fr.turtlesport.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public final class ConvertStringTo {

  /** Format d'une date */
  private static final String FORMAT_DATE      = "yyyy-MM-dd";

  /** Format date calendar */
  private static final String FORMAT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private ConvertStringTo() {
  }

  /**
   * Convertion d'un attribut en objet.
   * 
   * @param field
   *          <code>Field</code> d&eacute;crivant l'attribut.
   * @param value
   *          valeur de l'attribut.
   * @return l'objet.
   */
  public static Object toObject(Field field, String value) {
    if (field == null) {
      throw new IllegalArgumentException("field est null");
    }

    return toObject(field.getType(), value);
  }

  /**
   * Convertion d'un attribut en objet.
   * 
   * @param field
   *          <code>Field</code> d&eacute;crivant l'attribut.
   * @param value
   *          valeur de l'attribut.
   * @return l'objet.
   */
  public static Object toObject(Class<?> clazz, String value) {
    if (clazz == null) {
      throw new IllegalArgumentException("clazz est null");
    }
    // String
    if (clazz == String.class) {
      return value;
    }

    // Types primitifs
    // -------------------------------------
    // primitif Boolean
    if (clazz == Boolean.TYPE) {
      return toBoolean(value);
    }
    // primitif Byte
    if (clazz == Byte.TYPE) {
      return (value == null) ? -1 : Byte.valueOf(value);
    }
    // primitif Char
    if (clazz == Character.TYPE) {
      if (value == null) {
        throw new IllegalArgumentException("value est null");
      }
      return toCharacter(value);
    }
    // primitif Short
    if (clazz == Short.TYPE) {
      return (value == null) ? -1 : Short.valueOf(value);
    }
    // primitif Integer
    if (clazz == Integer.TYPE) {
      return (value == null) ? -1 : Integer.valueOf(value);
    }
    // primitif Long
    if (clazz == Long.TYPE) {
      return (value == null) ? -1 : Long.valueOf(value);
    }
    // primitif Float
    if (clazz == Float.TYPE) {
      return (value == null) ? -1 : Float.valueOf(value);
    }
    // primitif Double
    if (clazz == Double.TYPE) {
      return (value == null) ? -1 : Double.valueOf(value);
    }

    if (value == null) {
      return null;
    }
    
    // Type Classe
    // -----------------------------
    // Date
    if (clazz == Date.class) {
      return toDate(value);
    }
    // Calendar
    if (clazz == Calendar.class) {
      return toCalendar(value);
    }
    // BigDecimal
    if (clazz == BigDecimal.class) {
      return new BigDecimal(value);
    }
    // BigInteger
    if (clazz == BigInteger.class) {
      return new BigInteger(value);
    }
    // Boolean
    if (clazz == Boolean.class) {
      return toBoolean(value);
    }
    // Byte
    if (clazz == Byte.class) {
      return Byte.valueOf(value);
    }
    // Character
    if (clazz == Character.class) {
      return toCharacter(value);
    }
    // Short
    if (clazz == Short.class) {
      return Short.valueOf(value);
    }
    // Integer
    if (clazz == Integer.class) {
      return Integer.valueOf(value);
    }
    // Long
    if (clazz == Long.class) {
      return Long.valueOf(value);
    }
    // Float
    if (clazz == Float.class) {
      return Float.valueOf(value);
    }
    // Double
    if (clazz == Double.class) {
      return Double.valueOf(value);
    }

    throw new IllegalArgumentException("clazz invalide " + clazz);
  }

  /**
   * Conversion d'une chaine en <code>Boolean</code>.
   * 
   * @param value
   *          la chaine.
   * @return le <code>Boolean</code>.
   * @throws IllegalArgumentException
   *           si <code>value</code> diff&eacute;rent de 0, 1, <code>true</code>,
   *           <code>false</code>.
   */
  private static Boolean toBoolean(String value) {
    if ("0".equals(value)) {
      return Boolean.valueOf(false);
    }
    if ("1".equals(value)) {
      return Boolean.valueOf(true);
    }

    if ("true".equalsIgnoreCase(value)) {
      return Boolean.valueOf(true);
    }
    if ("false".equalsIgnoreCase(value)) {
      return Boolean.valueOf(false);
    }

    throw new IllegalArgumentException("boolean " + value);
  }

  /**
   * Conversion d'une chaine en <code>Boolean</code>.
   * 
   * @param value
   *          la chaine.
   * @return le <code>Character</code>.
   * @throws IllegalArgumentException
   *           si value est <code>null</code> ou de longueur diff&eacute;rente de 1;
   */
  private static Character toCharacter(String value) {
    if (value == null || value.length() != 1) {
      throw new IllegalArgumentException("character " + value);
    }
    return Character.valueOf(value.charAt(0));
  }

  /**
   * Conversion d'une chaine en <code>Date</code>.
   * 
   * @param value
   *          la chaine.
   * @return <code>Date</code>.
   * @throws IllegalArgumentException
   *           si value n'est pas une date.
   */
  private static Date toDate(String value) {
    if (value == null || value.length() < 10 || value.charAt(4) != '-'
        || value.charAt(7) != '-') {
      throw new IllegalArgumentException(value);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
    dateFormat.setLenient(false);

    Calendar calendar = Calendar.getInstance();
    synchronized (calendar) {
      try {
        return dateFormat.parse(value);
      }
      catch (ParseException e) {
        throw new IllegalArgumentException("date invalide " + value);
      }
    }
  }

  /**
   * Conversion d'une chaine en <code>Calendar</code>.<br>
   * Voici les formats support&eacute;s :<br>
   * <ul>
   * <li>2002-05-30T09:00:00</li>
   * <li>2002-05-30T09:30:10Z</li>
   * <li>2002-05-30T09:30:10-06:00</li>
   * <li>2002-05-30T09:30:10+06:00</li>
   * </ul>
   * 
   * @param value
   *          la chaine.
   * @return <code>Calendar</code>.
   * @throws IllegalArgumentException
   *           si value n'est pas une date calendar.
   */
  private static Calendar toCalendar(String value) {

    if (value == null || value.length() == 0) {
      throw new IllegalArgumentException("calendar null");
    }
    if (value.length() < 19) {
      throw new IllegalArgumentException(value);
    }
    if (value.charAt(4) != '-' || value.charAt(7) != '-'
        || value.charAt(10) != 'T') {
      throw new IllegalArgumentException(value);
    }
    if (value.charAt(13) != ':' || value.charAt(16) != ':') {
      throw new IllegalArgumentException(value);
    }

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_TIME);
    dateFormat.setLenient(false);
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

    return calendar;
  }

}
