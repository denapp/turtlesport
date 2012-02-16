package fr.turtlesport.util;

import java.util.List;

import fr.turtlesport.db.DataRunTrk;
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
   * Restitue la latitude sous forme textuelle.
   * 
   * @return la latitude sous forme textuelle.
   */
  public static String latitude(double gLatitude) {
    StringBuilder st = new StringBuilder();
    toDegree(st, gLatitude);
    st.append((gLatitude < 0) ? "S" : "N");
    return st.toString();
  }

  /**
   * Restitue la position sous forme textuelle.
   * 
   * @return la position sous forme textuelle.
   */
  public static String longititude(double gLongitude) {
    StringBuilder st = new StringBuilder();
    toDegree(st, gLongitude);
    st.append((gLongitude < 0) ? "W" : "E");

    return st.toString();
  }

  /**
   * Restitue la position sous forme textuelle.
   * 
   * @return la position sous forme textuelle.
   */
  public static String geoPosition(double gLatitude, double gLongitude) {
    StringBuilder st = new StringBuilder();
    toDegree(st, gLatitude);
    st.append((gLatitude < 0) ? "S" : "N");
    st.append("    ");
    toDegree(st, gLongitude);
    st.append((gLongitude < 0) ? "W" : "E");

    return st.toString();
  }

  private static void toDegree(StringBuilder st, double value) {
    int valInt = Math.abs((int) value);

    st.append(valInt);
    st.append("\u00B0");
    double val = 60 * (Math.abs(value) - valInt);
    if (val < 10) {
      st.append(0);
    }
    st.append((int) val);
    st.append("'");
    val = 60 * (val - ((int) val));
    if (val < 10) {
      st.append(0);
    }
    st.append((int) val);
    st.append('.');

    valInt = (int) ((val - (int) val) * 100);
    if (valInt < 10) {
      st.append(0);
    }
    st.append(valInt);
    st.append("'' ");
  }

  /**
   * Conversion d'un point garmin en point g&eacute;graphique.
   * 
   * @param latitude
   * @param longitude
   * @return
   */
  public static IGeoPosition makeFromGarmin(int gLatitude, int gLongitude) {
    if (isInvalidGarminGpx(gLatitude) || isInvalidGarminGpx(gLongitude)) {
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
    return Wgs84.computeWsg84(geo1.getLatitude(),
                              geo1.getLongitude(),
                              geo2.getLatitude(),
                              geo2.getLongitude());
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

  /**
   * @param gPos
   * @return
   */
  public static boolean isInvalidGarminGpx(int gPos) {
    return (gPos == INVALID);
  }

  /**
   * Calcul le Nord West et Sud Est d'une liste de points.
   * 
   * @param sw
   *          le Sud Est.
   * @param ne
   *          le Nord est.
   * @param list
   *          la liste de points.
   */
  public static void calculateBounds(IGeoPosition sw,
                                     IGeoPosition ne,
                                     List<DataRunTrk> list) {
    int minLat = Integer.MAX_VALUE;
    int minLon = Integer.MAX_VALUE;
    int maxLat = Integer.MIN_VALUE;
    int maxLon = Integer.MIN_VALUE;

    for (DataRunTrk g : list) {
      if (g.isValidGps()) {
        if (minLat > g.getLatitude()) {
          minLat = g.getLatitude();
        }
        if (minLon > g.getLongitude()) {
          minLon = g.getLongitude();
        }
        if (maxLat < g.getLatitude()) {
          maxLat = g.getLatitude();
        }
        if (maxLon < g.getLongitude()) {
          maxLon = g.getLongitude();
        }
      }
    }

    sw.setLatitude(GeoUtil.makeLatitudeFromGarmin(maxLat));
    sw.setLongitude(GeoUtil.makeLatitudeFromGarmin(maxLon));
    ne.setLatitude(GeoUtil.makeLatitudeFromGarmin(minLat));
    ne.setLongitude(GeoUtil.makeLatitudeFromGarmin(minLon));
  }

}
