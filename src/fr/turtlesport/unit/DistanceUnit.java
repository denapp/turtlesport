/**
 * 
 */
package fr.turtlesport.unit;

import java.text.DecimalFormat;

import fr.turtlesport.Configuration;
import fr.turtlesport.unit.event.Unit;

/**
 * @author Denis Apparicio
 * 
 */
public final class DistanceUnit extends Unit {

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

  private static final String[]      UNITS          = { "km", "mile" };

  /**
   * 
   */
  private DistanceUnit() {
  }

  /**
   * Conversion de foot en metre.
   * 
   * @param ft
   * @return
   */
  public static double convertFtToMeter(double ft) {
    return ft * 0.30480061;
  }

  /**
   * Conversion de Miles en metres.
   * 
   * @param mile
   * @return
   */
  public static double convertMileToMeter(double mile) {
    return mile * 1609;
  }

  /**
   * D&eacute;termine si l'unit&eacute; de distance est le km.
   * 
   * @return <code>true</code> si l'unit&eacute; de distance est le km,
   *         <code>false</code> sinon.
   */
  public static boolean isUnitKm(String unit) {
    return unitKm().equals(unit);
  }

  /**
   * Restitue l'unit&eacute; de distance en km.
   * 
   * @return l'unit&eacute; de distance en km.
   */
  public static String unitKm() {
    return UNITS[0];
  }

  /**
   * Restitue l'unit&eacute; de vitesse par d&eacute;faut.
   * 
   * @return l'unit&eacute; de vitesse par d&eacute;faut.
   */
  public static String getDefaultUnit() {
    return Configuration.getConfig().getProperty("units", "distance", UNITS[0]);
  }

  /**
   * Restitue l'unit&eacute; de vitesse par d&eacute;faut.
   * 
   * @return l'unit&eacute; de vitesse par d&eacute;faut.
   */
  public static String getDefaultLowUnit() {
    return isDefaultUnitKm() ? "m" : "ft";
  }

  /**
   * D&eacute;termine si l'unit&eacute; par d&eacute;faut est le km.
   * 
   * @return <code>true</code> si l'unit&eacute; par d&eacute;faut est le km.
   */
  public static boolean isDefaultUnitKm() {
    return isUnitKm(getDefaultUnit());
  }

  /**
   * Conversion de <code>unit1</code> vers <code>unit2</code>.
   * 
   * @param unit1
   *          libell&eacute; de l'unit&eacute;.
   * @param unit2
   *          libell&eacute; de l'unit&eacute;
   * @param value
   *          la valeur.
   * @return la valeur convertie en unit&eacute; <code>unit2</code>.
   */
  public static double convert(String unit1, String unit2, double value) {
    return (Double) convertUnit(DistanceUnit.class,
                                UNITS,
                                unit1,
                                unit2,
                                value,
                                Double.TYPE);
  }

  /**
   * Conversion de Km en Mile.
   * 
   * @param km
   * @return
   */
  public static double convertKmToMile(double km) {
    return km / 1.609;
  }

  /**
   * Conversion de Miles en Km.
   * 
   * @param mile
   * @return
   */
  public static double convertMileToKm(double mile) {
    return mile * 1.609;
  }

  /**
   * Restitue les lib&eacute;ll&eacute;s des unit&eacute;s.
   * 
   * @return les lib&eacute;ll&eacute;s des unit&eacute;s.
   */
  public static String[] units() {
    return UNITS;
  }

  /**
   * Formate une distance.
   * 
   * @param distance
   *          distance.
   * @return la distance.
   */
  public static String format(double dist) {
    return DECIMAL_FORMAT.format(dist);
  }

  /**
   * Defformate une distance.
   * 
   * @param distance
   *          distance.
   * @return la distance.
   */
  public static double reverseFormat(String formatNumber) {
    if (formatNumber == null || "".equals(formatNumber)) {
      return 0;
    }
    formatNumber = formatNumber.replace(',', '.');
    return Double.valueOf(formatNumber.replace(',', '.'));
  }

  /**
   * Formate une distance en metre en km.
   * 
   * @param dist
   *          la distance en metre.
   * @return la distance en km.
   */
  public static String formatMetersInKmWithUnit(double dist) {
    StringBuilder st = new StringBuilder();
    st.append(DECIMAL_FORMAT.format(dist / 1000.0));
    st.append(" km");
    return st.toString();
  }

  /**
   * Formate une distance.
   * 
   * @param dist
   *          la distance.
   */
  public static String formatWithUnit(double dist) {
    StringBuilder st = new StringBuilder();
    st.append(DECIMAL_FORMAT.format(dist / 1000.0));
    st.append(' ');
    st.append(DistanceUnit.getDefaultUnit());
    return st.toString();
  }

  /**
   * Formate une distance.
   * 
   * @param dist
   *          la distance.
   */
  public static String formatWithDefaultUnit(double dist) {
    StringBuilder st = new StringBuilder();
    if (isDefaultUnitKm()) {
      st.append(DECIMAL_FORMAT.format(dist / 1000.0));
      st.append(' ');
    }
    else {
      st.append(DECIMAL_FORMAT.format(convertKmToMile(dist / 1000.0)));
    }
    st.append(DistanceUnit.getDefaultUnit());
    return st.toString();
  }

  /**
   * Formate une distance en metre en km.
   * 
   * @param distance
   *          distance en metre.
   * @return la distance en km.
   */
  public static String formatMetersInKm(double dist) {
    return DECIMAL_FORMAT.format(dist / 1000.0);
  }

}
