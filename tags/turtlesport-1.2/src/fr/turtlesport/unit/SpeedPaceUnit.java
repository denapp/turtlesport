package fr.turtlesport.unit;

import java.text.DecimalFormat;

import fr.turtlesport.Configuration;
import fr.turtlesport.unit.event.Unit;

/**
 * @author Denis Apparicio
 * 
 */
public final class SpeedPaceUnit extends Unit {
  private static final DecimalFormat DF_SPEED_1 = new DecimalFormat("##0.##");

  private static final DecimalFormat DF_SPEED_2 = new DecimalFormat("00.00");

  private static final String[]      UNITS      = { "km/h",
      "mn/km",
      "mile/h",
      "mn/mile"                                };

  private SpeedPaceUnit() {
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
   * Restitue l'unit&eacute; de vitesse en mn/km.
   * 
   * @return l'unit&eacute; de vitesse en mn/km.
   */
  public static String unitMnPerkm() {
    return UNITS[1];
  }

  /**
   * Restitue l'unit&eacute; de vitesse en mile/h.
   * 
   * @return l'unit&eacute; de vitesse en mile/h.
   */
  public static String unitMilePerH() {
    return UNITS[2];
  }

  /**
   * Restitue l'unit&eacute; de vitesse en mn/mile.
   * 
   * @return l'unit&eacute; de vitesse en mn/mile.
   */
  public static String unitMnPerMile() {
    return UNITS[3];
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
    return Configuration.getConfig().getProperty("units", "speed", UNITS[0]);
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
   * Conversion.
   * 
   * @param value
   * @return la valeur convertie.
   */
  public static String convertToTime(double value) {
    String val = SpeedPaceUnit.DF_SPEED_2.format(value);
    val = val.replace(',', ':');
    return val;
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
  public static Object convert(String unit1, String unit2, String value) {
    return convertUnit(SpeedPaceUnit.class,
                       UNITS,
                       unit1,
                       unit2,
                       value,
                       String.class);
  }

  /**
   * Conversion de km/h en mile/h.
   * 
   * @param kmPerh
   * @return
   */
  public static double convertKmperhToMileperh(double kmPerh) {
    return SpeedUnit.convertKmperhToMileperh(kmPerh);
  }

  /**
   * Conversion de km/h en mn/km.
   * 
   * @param km
   * @return
   */
  public static String convertKmperhToMnperkm(double kmPerh) {
    return PaceUnit.computeAllure(kmPerh * 1000, 3600 * 100);
  }

  /**
   * Conversion de km/h en mn/mile.
   * 
   * @param km
   * @return
   */
  public static String convertKmperhToMnpermile(double kmPerh) {
    return PaceUnit.computeAllure((kmPerh * 1000) / 1.609, 3600 * 100);
  }

  /**
   * Conversion de mn/km en km/h.
   * 
   * @param mnKm
   * @return
   */
  public static double convertMnperkmToKmperh(String mnKm) {
    int secondes = Integer.valueOf(mnKm.substring(0, 2)) * 60;
    secondes += Integer.valueOf(mnKm.substring(3));
    return (secondes == 0) ? 0 : 3600.0 / secondes;
  }

  /**
   * Conversion de mn/km en km/h.
   * 
   * @param mnKm
   * @return
   */
  public static double convertMnperkmToKmperh(double mnKm) {
    return convertMnperkmToKmperh(DF_SPEED_2.format(mnKm));
  }

  /**
   * Conversion de mn/km en km/h.
   * 
   * @param mnKm
   * @return
   */
  public static double convertMnperkmToMileperh(String mnKm) {
    return convertKmperhToMileperh(convertMnperkmToKmperh(mnKm));
  }

  /**
   * Conversion de mn/km en km/h.
   * 
   * @param mnKm
   * @return
   */
  public static double convertMnperkmToMileperh(double mnKm) {
    return convertMnperkmToMileperh(DF_SPEED_2.format(mnKm));
  }

  /**
   * Conversion de mn/km en mn/mile.
   * 
   * @param mnKm
   * @return
   */
  public static String convertMnperkmToMnpermile(String mnKm) {
    return PaceUnit.convertMnperkmToMnpermile(mnKm);
  }

  /**
   * Conversion de mn/km en mn/mile.
   * 
   * @param km
   * @return
   */
  public static String convertMnperkmToMnpermile(double mnKm) {
    return PaceUnit.convertMnperkmToMnpermile(DF_SPEED_2.format(mnKm));
  }

  /**
   * Conversion de mile/h en mn/mile.
   * 
   * @param milePerh
   * @return
   */
  public static String convertMileperhToMnpermile(double milePerh) {
    return PaceUnit.computeAllure(milePerh * 1000, 3600 * 100);
  }

  /**
   * Conversion de mile/h en km/h.
   * 
   * @param km
   * @return
   */
  public static double convertMileperhToKmperh(double milePerh) {
    return SpeedUnit.convertMileperhToKmperh(milePerh);
  }

  /**
   * Conversion de mile/h en km/h.
   * 
   * @param km
   * @return
   */
  public static String convertMileperhToMnperkm(double milePerh) {
    return convertKmperhToMnperkm(convertMileperhToKmperh(milePerh));
  }

  /**
   * Conversion de mn/mile en mn/km.
   * 
   * @param mnMile
   * @return
   */
  public static String convertMnpermileToMnperkm(String mnMile) {
    return PaceUnit.convertMnpermileToMnperkm(mnMile);
  }

  /**
   * Conversion de mn/mile en mn/km.
   * 
   * @param mnMile
   * @return
   */
  public static String convertMnpermileToMnperkm(double mnMile) {
    return PaceUnit.convertMnpermileToMnperkm(DF_SPEED_2.format(mnMile));
  }

  /**
   * Conversion de mn/mile en mn/km.
   * 
   * @param mnMile
   * @return
   */
  public static double convertMnpermileToKmperh(String mnMile) {
    int secondes = Integer.valueOf(mnMile.substring(0, 2)) * 60;
    secondes += Integer.valueOf(mnMile.substring(3));
    secondes /= 1.609;
    return convertMnperkmToKmperh(computeSecToTime(secondes));
  }

  /**
   * Conversion de mn/mile en mn/km.
   * 
   * @param mnMile
   * @return
   */
  public static double convertMnpermileToKmperh(double mnMile) {
    return convertMnpermileToKmperh(DF_SPEED_2.format(mnMile));
  }

  /**
   * Conversion de mn/mile en mile/h.
   * 
   * @param mnMile
   * @return
   */
  public static double convertMnpermileToMileperh(String mnMile) {
    return convertKmperhToMileperh(convertMnpermileToKmperh(mnMile));
  }

  /**
   * Conversion de mn/mile en mile/h.
   * 
   * @param mnMile
   * @return
   */
  public static double convertMnpermileToMileperh(double mnMile) {
    return convertMnpermileToMileperh(DF_SPEED_2.format(mnMile));
  }

  /**
   * Calcule la vitesse en km/h.
   * 
   * @param distance
   *          distance en metre.
   * @param sec100
   *          distance en seconde/100.
   * @return la vitesse en km/h.
   */
  public static double computeSpeedKmS(double distance, double sec100) {
    return (sec100 == 0) ? 0 : (360.0 * distance) / sec100;
  }

  /**
   * Calcule et formate la vitesse en km/h.
   * 
   * @param distance
   *          distance en metre.
   * @param sec100
   *          distance en seconde/100.
   * @return la vitesse en hm/h.
   */
  public static String computeFormatSpeedKmhWithUnit(double distance,
                                                     double sec100) {
    return computeFormatSpeed(distance, sec100) + " km/h";
  }

  /**
   * Calcule et formate la vitesse.
   * 
   * @param distance
   *          distance en metre.
   * @param sec100
   *          distance en seconde/100.
   * @return la vitesse en hm/h.
   */
  public static String computeFormatSpeedWithUnit(double distance, double sec100) {
    StringBuilder st = new StringBuilder();
    st.append(computeFormatSpeed(distance, sec100));
    st.append(' ');
    st.append(SpeedUnit.getDefaultUnit());
    return st.toString();
  }

  /**
   * Calcule et formate la vitesse.
   * 
   * @param distance
   *          la distance.
   * @param sec100
   *          distance en seconde/100.
   * @return la vitesse.
   */
  public static String computeFormatSpeed(double distance, double sec100) {
    return DF_SPEED_1.format(computeSpeedKmS(distance, sec100));
  }

  /**
   * Formate la vitesse.
   * 
   * @param speed
   *          la vitesse.
   * @return la vitesse.
   */
  public static String formatSpeed(double speed) {
    return DF_SPEED_1.format(speed);
  }

}
