package fr.turtlesport.unit;

import fr.turtlesport.Configuration;
import fr.turtlesport.unit.event.Unit;

/**
 * @author Denis Apparicio
 * 
 */
public final class WeightUnit extends Unit {

  private static final String[] UNITS = { "kg", "pound", "stone" };

  private WeightUnit() {
  }

  /**
   * Restitue l'unit&eacute; de poids par d&eacute;faut.
   * 
   * @return l'unit&eacute; de poids par d&eacute;faut.
   */
  public static String getDefaultUnit() {
    return Configuration.getConfig().getProperty("units", "weight", UNITS[0]);
  }

  /**
   * D&eacute;termine si l'unit&eacute; de poids est le kg.
   * 
   * @return <code>true</code> si l'unit&eacute; de poids est le kg,
   *         <code>false</code> sinon.
   */
  public static boolean isUnitKg(String unit) {
    return unitKg().equals(unit);
  }

  /**
   * Restitue l'unit&eacute; de poids en kg.
   * 
   * @return l'unit&eacute; de poids en kg.
   */
  public static String unitKg() {
    return UNITS[0];
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
    return (Double) convertUnit(WeightUnit.class,
                                UNITS,
                                unit1,
                                unit2,
                                value,
                                Double.TYPE);
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
   * Conversion de kg en pound.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertKgToPound(double value) {
    return value * 2.204622;
  }

  /**
   * Conversion de pound en kg.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertPoundToKg(double value) {
    return value / 2.204622;
  }

  /**
   * Conversion de kg en stone.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertKgToStone(double value) {
    return value * 0.157473;
  }

  /**
   * Conversion de pounds en kg.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertStoneToKg(double value) {
    return value / 0.157473;
  }

  /**
   * Conversion de pound en stone.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertPoundToStone(double value) {
    return value / 14;
  }

  /**
   * Conversion de stone en pound.
   * 
   * @param stone
   * @return la valeur convertie.
   */
  public static double convertStoneToPound(double value) {
    return value * 14;
  }

}
