package fr.turtlesport.unit;

import fr.turtlesport.unit.event.Unit;


/**
 * @author denis
 * 
 */
public final class SpeedUnit extends Unit {

  private static final String[] UNITS = { "km/h", "mile/h" };

  private SpeedUnit() {
  }

  /**
   * D&eacute;termine si l'unit&eacute; de vitesse est le km/h.
   * 
   * @return <code>true</code> si l'unit&eacute; de vitesse est le km/h,
   *         <code>false</code> sinon.
   */
  public static boolean isUnitKmPerH(String unit) {
    return unitKmPerH().equals(unit);
  }
  
  /**
   * Restitue l'unit&eacute; de vitesse.
   * 
   * @param unitDistance
   *          l'unit&eacute; de vitesse.
   * @return l'unit&eacute; de vitesse.
   */
  public static String unit(String unitDistance) {
    for (String unit : UNITS) {
      if (unit.startsWith(unitDistance)) {
        return unit;
      }
    }
    return null;
  }

  /**
   * Restitue l'unit&eacute; de vitesse en km/h.
   * 
   * @return l'unit&eacute; de vitesse en km/h.
   */
  public static String unitKmPerH() {
    return UNITS[0];
  }

  /**
   * Restitue l'unit&eacute; de vitesse par d&eacute;faut.
   * 
   * @return l'unit&eacute; de vitesse par d&eacute;faut.
   */
  public static String getDefaultUnit() {
    return UNITS[DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit()) ? 0 : 1];
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
  public static Object convert(String unit1, String unit2, double value) {
    return convertUnit(SpeedPaceUnit.class,
                       UNITS,
                       unit1,
                       unit2,
                       value,
                       Double.TYPE);
  }
  
  /**
   * Conversion de km/h en mile/h.
   * 
   * @param kmPerh
   * @return
   */
  public static double convertKmperhToMileperh(double kmPerh) {
    return kmPerh / 1.609;
  }
  
  /**
   * Conversion de mile/h en km/h.
   * 
   * @param km
   * @return
   */
  public static double convertMileperhToKmperh(double milePerh) {
    return milePerh * 1.609;
  }

}
