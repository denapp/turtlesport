package fr.turtlesport.unit;

import fr.turtlesport.Configuration;
import fr.turtlesport.unit.event.Unit;

/**
 * @author Denis Apparicio
 * 
 */
public final class TemperatureUnit extends Unit {
  private static final String[] UNITS = { "\u00B0C", "\u00B0F" };

  private TemperatureUnit() {
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
   * D&eacute;termine si l'unit&eacute; par defaut est le degre.
   * 
   * @return <code>true</code> si l'unit&eacute; par defaut est le degre,
   *         <code>false</code> sinon.
   */
  public static boolean isDefaultUnitDegree() {
    return unitDegree().equals(getDefaultUnit());
  }

  /**
   * D&eacute;termine si l'unit&eacute; d'allure est en mn/km.
   * 
   * @return <code>true</code> si l'unit&eacute; d'allure est en mn/km,
   *         <code>false</code> sinon.
   */
  public static boolean isDegree(String unit) {
    return unitDegree().equals(unit);
  }

  /**
   * D&eacute;termine si l'unit&eacute; d'allure est en mn/km.
   * 
   * @return <code>true</code> si l'unit&eacute; d'allure est en mn/km,
   *         <code>false</code> sinon.
   */
  public static boolean isFahrenheit(String unit) {
    return unitFahrenheit().equals(unit);
  }

  /**
   * Restitue l'unit&eacute; d'allure.
   * 
   * @param unitDistance
   *          l'unit&eacute; de distance.
   * @return l'unit&eacute; de distance.
   */
  public static String unit(String unitDistance) {
    for (String unit : UNITS) {
      if (unit.endsWith(unitDistance)) {
        return unit;
      }
    }
    return null;
  }

  /**
   * Restitue l'unit&eacute; temp&eacute;rature ne degr&eacute;.
   * 
   * @return l'unit&eacute; temp&eacute;rature ne degr&eacute;
   */
  public static String unitDegree() {
    return UNITS[0];
  }

  /**
   * Restitue l'unit&eacute; temp&eacute;rature en Fahrenheit.
   * 
   * @return l'unit&eacute; temp&eacute;rature en Fahrenheit
   */
  public static String unitFahrenheit() {
    return UNITS[1];
  }

  /**
   * Restitue l'unit&eacute; de temp&eacute;re par par d&eacute;faut.
   * 
   * @return l'unit&eacute; de temp&eacute;re par par d&eacute;faut.
   */
  public static String getDefaultUnit() {
    return Configuration.getConfig().getProperty("units",
                                                 "temperature",
                                                 UNITS[0]);
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
    if (isDegree(unit1) && isFahrenheit(unit2)) {
      return convertToFahrenheit(value);
    }
    if (isDegree(unit1) && isFahrenheit(unit2)) {
      return convertToDegree(value);
    }
    throw new IllegalArgumentException(unit1 + "->" + unit2);
  }

  /**
   * Conversion de degre vers fahrenheit.
   * 
   * @param value
   *          la valeur.
   * @return la valeur convertie.
   */
  public static double convertToFahrenheit(double value) {
    return (9.0 / 5) * value + 32;
  }

  /**
   * Conversion de degre vers fahrenheit.
   * 
   * @param value
   *          la valeur.
   * @return la valeur convertie.
   */
  public static double convertToDegree(double value) {
    return (5.0 / 9) * (value - 32);
  }

}
