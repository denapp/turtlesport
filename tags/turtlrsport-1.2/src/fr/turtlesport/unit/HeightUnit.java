package fr.turtlesport.unit;

import fr.turtlesport.Configuration;
import fr.turtlesport.unit.event.Unit;

/**
 * @author Denis Apparicio
 * 
 */
public final class HeightUnit extends Unit {
  private static final String[] UNITS = { "cm", "foot", "inch" };

  private HeightUnit() {
  }

  /**
   * Restitue l'unit&eacute; de hauteur par d&eacute;faut.
   * 
   * @return l'unit&eacute; de hauteur par d&eacute;faut.
   */
  public static String getDefaultUnit() {
    return Configuration.getConfig().getProperty("units", "height", UNITS[0]);
  }
  
  /**
   * D&eacute;termine si l'unit&eacute; de hauteur est le cm.
   * 
   * @return <code>true</code> si l'unit&eacute; de hauteur est le cm,
   *         <code>false</code> sinon.
   */
  public static boolean isUnitCm(String unit) {
    return unitCm().equals(unit);
  }

  /**
   * Restitue l'unit&eacute; de hauteur est le cm.
   * 
   * @return l'unit&eacute; de hauteur est le cm.
   */
  public static String unitCm() {
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
    return (Double) convertUnit(HeightUnit.class,
                                UNITS,
                                unit1,
                                unit2,
                                value,
                                Double.TYPE);
  }
  
  /**
   * Restitue les lib&eacute; des unit&eacute;s.
   * 
   * @return les lib&eacute; des unit&eacute;s.
   */
  public static String[] units() {
    return UNITS;
  }

  /**
   * Conversion de cm en foot.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertCmToFoot(double value) {
    return value / 30.48;
  }

  /**
   * Conversion de foot en cm.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertFootToCm(double value) {
    return value * 30.48;
  }

  /**
   * Conversion de cm en inch.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertCmToInch(double value) {
    return value / 2.54;
  }

  /**
   * Conversion de inch en cm.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertInchToCm(double value) {
    return value * 2.54;
  }
  
  /**
   * Conversion de foot en inch.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertFootToInch(double value) {
    return value * 12;
  }

  /**
   * Conversion de inch en foot.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static double convertInchToFoot(double value) {
    return value / 12;
  }
}
