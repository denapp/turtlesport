package fr.turtlesport.util;

import fr.turtlesport.geo.GeoPosition;
import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.protocol.data.PositionType;

/**
 * @author Denis Apparicio
 * 
 */
public final class GeoUtil {

  private GeoUtil() {
  }

  private static final int    INVALID = 0x7FFFFFFF;

  private static final double VAL     = 180.0 / Math.pow(2, 31);

  /**
   * Conversion d'un point garmin en point g&eacute;graphique.
   * 
   * @param latitude
   * @param longitude
   * @return
   */
  public static IGeoPosition makeFromGarmin(int gLatitude, int gLongitude) {
    if (isInvalid(gLatitude) || isInvalid(gLongitude)) {
      return null;
    }
    return new GeoPosition(makeLatitudeFromGarmin(gLatitude),
                           makeLongitudeFromGarmin(gLongitude));
  }

  /**
   * Conversion de la latitude garmin en latitude g&eacute;graphique.
   * 
   * @param latitude
   * @return
   */
  public static double makeLatitudeFromGarmin(int latitude) {
    return latitude * VAL;
  }

  /**
   * Conversion de la latitude garmin en latitude g&eacute;graphique.
   * 
   * @param latitude
   * @return
   */
  public static double makeLongitudeFromGarmin(int longitude) {
    return longitude * VAL;
  }

  /**
   * Conversion de la latitude geographique en garmin latitude.
   * 
   * @param latitude
   * @return
   */
  public static int makeLatitudeFromGeo(double latitude) {
    return (latitude == IGeoPosition.INVALID_POS) ? PositionType.INVALID
        : (int) (latitude / VAL);
  }

  /**
   * Conversion de la longitude geographique en garmin longitude.
   * 
   * @param longitude
   * @return
   */
  public static int makeLongitudeFromGeo(double longitude) {
    return (longitude == IGeoPosition.INVALID_POS) ? PositionType.INVALID
        : (int) (longitude / VAL);
  }

  /**
   * Conversion d'une position garmin en position g&eacute;graphique.
   * 
   * @param posn
   *          garmin position
   * @return
   */
  public static IGeoPosition makeFromGarmin(PositionType posn) {
    if (posn == null || posn.iPositionInvalid()) {
      return null;
    }
    return new GeoPosition(makeLatitudeFromGarmin(posn.getLatitude()),
                           makeLongitudeFromGarmin(posn.getLongitude()));
  }

  /**
   * Conversion d'une position garmin en position g&eacute;graphique.
   * 
   * @param posn
   *          garmin position
   * @return
   */
  public static PositionType makeFromGarmin(IGeoPosition geo) {
    if (geo == null) {
      return null;
    }
    return new PositionType(makeLatitudeFromGeo(geo.getLatitude()),
                            makeLongitudeFromGeo(geo.getLongitude()));
  }

  /**
   * Calcule la distance entre 2 points g&eacute;ographiques en m&ecirc;tre.
   * 
   * @param geo1
   *          permier point g&eacute;ographique.
   * @param geo2
   *          second point g&eacute;ographiques.
   * @return la distance entre 2 points g&eacute;ographiques en m&ecirc;tre.
   */
  public static double computeDistance(IGeoPosition geo1, IGeoPosition geo2) {
    if (geo1.isInvalidPosition() || geo2.isInvalidPosition()) {      
      return 0;
    }
    return Wgs84.computeWsg84(geo1.getLatitude(), geo1.getLongitude(), geo2
        .getLatitude(), geo2.getLongitude());
  }

  /**
   * Calcule la distance d'une liste de points g&eacute;ographiques.
   * 
   * @param list
   *          les points g&eacute;ographiques.
   * @return la distance en m&ecirc;tre.
   */
  public static double computeDistance(IGeoPosition[] list) {
    double distance = 0;

    if (list != null && list.length > 1) {
      for (int i = 0; i < list.length - 1; i++) {
        distance += GeoUtil.computeDistance(list[i], list[i + 1]);
      }
    }

    return distance;
  }

  private static boolean isInvalid(int gPos) {
    return (gPos == INVALID);
  }
}
