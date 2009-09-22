package fr.turtlesport.geo.pcx5;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.GeoPositionWithAlt;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class Pcx5Point extends GeoPositionWithAlt {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Pcx5Point.class);
  }

  /**
   * 
   */
  protected Pcx5Point() {
    super();
  }

  /**
   * Conversion de la latitude.
   */
  protected void convertLatitude(String tmp) throws GeoLoadException {
    try {
      double latitude = Double.parseDouble(tmp.substring(1));

      switch (tmp.charAt(0)) {
        case 'N':
          break;
        case 'S':
          latitude = -latitude;
          break;
        default:
          throw new GeoLoadException("Trk latitude point incorrect " + tmp);
      }
      setLatitude(latitude);
    }
    catch (NumberFormatException e) {
      throw new GeoLoadException("Trk latitude point incorrect " + tmp);
    }
  }

  /**
   * Conversion de la latitude.
   */
  protected void convertLongitude(String tmp) throws GeoLoadException {
    try {
      double longitude = Double.parseDouble(tmp.substring(1));

      switch (tmp.charAt(0)) {
        case 'W':
          longitude = -longitude;
          break;
        case 'E':
          break;
        default:
          throw new GeoLoadException("Trk longitude point incorrect " + tmp);
      }
      setLongitude(longitude);
    }
    catch (NumberFormatException e) {
      throw new GeoLoadException("Trk longitude point incorrect " + tmp);
    }
  }

  /**
   * Conversion de la latitude.
   */
  protected void convertAlt(String tmp) throws GeoLoadException {
    try {
      if (!"-9999".equals(tmp)) {
        setElevation(Integer.parseInt(tmp));
      }
    }
    catch (NumberFormatException e) {
      throw new GeoLoadException("altitude incorrect " + tmp);
    }
  }

  /**
   * Conversion de la date en date java.
   */
  protected void convertDateTime(String dateFmt, String timeFmt) throws GeoLoadException {
    int dateOfday;
    int year;
    int month;
    int hourOfDay;
    int minute;
    int second;

    Calendar cal = Calendar.getInstance();

    // recuperation de l'annee mois jour
    StringTokenizer st = new StringTokenizer(dateFmt, "-");
    try {
      dateOfday = Integer.parseInt(st.nextToken());
      month = convertMonth(st.nextToken());
      year = 2000 + Integer.parseInt(st.nextToken());
    }
    catch (NumberFormatException e) {
      log.error("", e);
      throw new GeoLoadException("Trk point date incorrecte " + dateFmt);
    }
    catch (NoSuchElementException e) {
      log.error("", e);
      throw new GeoLoadException("Trk point date incorrecte " + dateFmt);
    }

    // recuperation de l'heure
    try {
      hourOfDay = Integer.parseInt(timeFmt.substring(0, 2));
      minute = Integer.parseInt(timeFmt.substring(3, 5));
      second = Integer.parseInt(timeFmt.substring(6, 8));
    }
    catch (Throwable e) {
      log.error("", e);
      throw new GeoLoadException("Trk point heure incorrecte " + timeFmt);
    }

    // valorisation de la date
    cal.set(year, month, dateOfday, hourOfDay, minute, second);
    setDate(cal.getTime());
  }

  /**
   * Conversion du mois en int.
   */
  protected int convertMonth(String month) throws GeoLoadException {
    if ("JAN".equals(month)) {
      return 0;
    }
    if ("FEB".equals(month)) {
      return 1;
    }
    if ("MAR".equals(month)) {
      return 2;
    }
    if ("APR".equals(month)) {
      return 3;
    }
    if ("MAY".equals(month)) {
      return 4;
    }
    if ("JUN".equals(month)) {
      return 5;
    }
    if ("JUL".equals(month)) {
      return 6;
    }
    if ("AUG".equals(month)) {
      return 7;
    }
    if ("SEP".equals(month)) {
      return 8;
    }
    if ("OCT".equals(month)) {
      return 9;
    }
    if ("NOV".equals(month)) {
      return 10;
    }
    if ("DEC".equals(month)) {
      return 11;
    }

    throw new GeoLoadException("Trk point mois incorrect " + month);
  }

}
