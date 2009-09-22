package fr.turtlesport.unit;

import java.text.DecimalFormat;

import fr.turtlesport.unit.event.Unit;

/**
 * @author denis
 * 
 */
public final class PaceUnit extends Unit {
  private static final String[]      UNITS    = { "mn/km", "mn/mile" };

  private static final DecimalFormat DF_SPEED = new DecimalFormat("00.00");

  private PaceUnit() {
  }

  /**
   * D&eacute;termine si l'unit&eacute; d'allure est en mn/km.
   * 
   * @return <code>true</code> si l'unit&eacute; d'allure est en mn/km,
   *         <code>false</code> sinon.
   */
  public static boolean isUnitMnPerKm(String unit) {
    return unitMnPerKm().equals(unit);
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
   * Restitue l'unit&eacute; d'allure est en mn/km.
   * 
   * @return l'unit&eacute; d'allure est en mn/km.
   */
  public static String unitMnPerKm() {
    return UNITS[0];
  }

  /**
   * Restitue l'unit&eacute; d'allure est en mn/mile.
   * 
   * @return l'unit&eacute; d'allure est en mn/mile.
   */
  public static String unitMnPerMile() {
    return UNITS[1];
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
   * Conversion de mn/km en mn/mile.
   * 
   * @param km
   * @return
   */
  public static String convertMnperkmToMnpermile(String mnKm) {
    int secondes = Integer.valueOf(mnKm.substring(0, 2)) * 60;
    secondes += Integer.valueOf(mnKm.substring(3));
    secondes *= 1.609;
    return computeSecToTime(secondes);
  }

  /**
   * Conversion de mn/mile en mn/km.
   * 
   * @param mnMile
   * @return
   */
  public static String convertMnpermileToMnperkm(String mnMile) {
    int secondes = Integer.valueOf(mnMile.substring(0, 2)) * 60;
    secondes += Integer.valueOf(mnMile.substring(3));
    secondes /= 1.609;
    return computeSecToTime(secondes);
  }

  /**
   * Conversion de mn/mile en mn/km.
   * 
   * @param mnMile
   * @return
   */
  public static String convertMnpermileToMnperkm(double mnMile) {
    return convertMnpermileToMnperkm(DF_SPEED.format(mnMile));
  }

  /**
   * Conversion de mn/km en mn/mile.
   * 
   * @param km
   * @return
   */
  public static String convertMnperkmToMnpermile(double mnKm) {
    return SpeedPaceUnit.convertMnpermileToMnperkm(mnKm);
  }

  /**
   * Calcule de l'allure en mn/km.
   * 
   * @param distance
   *          distance en metre.
   * @param sec100
   *          distance en seconde*100.
   * @return l'allure en mn/km.
   */
  public static String computeAllure(double distance, double sec100) {
    StringBuilder st = new StringBuilder();

    double allure = (distance == 0) ? 0 : sec100 / (6.0 * distance);
    if (allure == 0) {
      st.append("00");
    }
    else if (allure < 10) {
      st.append('0');
      st.append((int) allure);
    }
    else {
      st.append((int) allure);
    }
    st.append(":");

    allure -= ((int) allure);
    allure *= 60;
    if (allure == 0) {
      st.append("00");
    }
    else if (allure < 10) {
      st.append('0');
      st.append((int) allure);
    }
    else {
      st.append((int) allure);
    }

    return st.toString();
  }

  /**
   * Calcule de l'allure en mn/km.
   * 
   * @param distance
   *          distance en metre.
   * @param sec100
   *          distance en seconde/100.
   * @return l'allure en mn/km.
   */
  public static String computeFormatAllureMnKmWithUnit(double distance,
                                                       double sec100) {
    return computeAllure(distance, sec100) + "/km";
  }

  /**
   * Calcule de l'allure en mn/km.
   * 
   * @param distance
   *          distance en metre.
   * @param sec100
   *          distance en seconde/100.
   * @return l'allure en mn/km.
   */
  public static String computeFormatAllureWithUnit(double distance,
                                                   double sec100) {
    return computeAllure(distance, sec100)
           + getDefaultUnit().substring( getDefaultUnit().indexOf('/'));
  }

}
