package fr.turtlesport.util;

/**
 * @author Denis Apparicio
 * 
 */
public final class Wgs84 {
  /**
   * Equatorial radius of earth is required for distance computation.
   */
  public static final double EQUATORIALRADIUS = 6378137.0;

  /**
   * Polar radius of earth is required for distance computation.
   */
  public static final double POLARRADIUS = 6356752.3142;

  /**
   * The flattening factor of the earth's ellipsoid is required for distance
   * computation.
   */
  public static final double INVERSEFLATTENING = 298.257223563;

  /**
   * 
   */
  private Wgs84() {
  }

  /**
   * Calcule la distance entre deux points.
   * 
   * @param lat1
   * @param lon1
   * @param lat2
   * @param lon2
   * @return la distance entre deux points en m&ecirc;tre.
   */
  public static double computeWsg84(double lat1,
                                    double lon1,
                                    double lat2,
                                    double lon2) {
    double val;
    double distance = 0;

    double p = Math.PI / 180;
    double lat1p = lat1 * p;
    double lon1p = lon1 * p;
    double lat2p = lat2 * p;
    double lon2p = lon2 * p;

    val = Math.cos(lat2p) * Math.cos(lat1p) * Math.cos(lon1p - lon2p)
          + Math.sin(lat2p) * Math.sin(lat1p);

    if (val > 1) {
      val = 1;
    }
    else if (val < -1) {
      val = -1;
    }
    distance = 6378137 * Math.acos(val);
    return distance;

    // val = 2 * Math.pow(Math.asin(Math.sqrt(Math.sin((lat1p - lat2p) / 2))),
    // 2)
    // + Math.cos(lat1p) * Math.cos(lat2p)
    // * Math.pow(Math.sin((lon1p - lon2p) / 2), 2);
    // distance = 6378137 * val;
    // return Math.abs(distance);

    // Autre formule plus precise
    //val = 2*asin(sqrt(
    //pow(sin((lat1p - lat2p)/2), 2.0) +
    //        cos(lat1p)*cos(lat2p) * pow(sin((lon1p - lon2p)/2) , 2.0)
    //))*6378137;
  }

  /**
   * Calculates geodetic distance between two GeoCoordinates using Vincenty inverse formula for ellipsoids.
   *  This is very accurate but consumes more resources and time than the sphericalDistance method Adaptation
   * of Chriss Veness' JavaScript Code on http://www.movable-type.co.uk/scripts/latlong-vincenty.html Paper:
   * Vincenty inverse formula - T Vincenty, "Direct and Inverse Solutions of Geodesics on the Ellipsoid with
   * application of nested equations", Survey Review, vol XXII no 176, 1975
   * (http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)
   *
   * @param lat1
   * @param lon1
   * @param lat2
   * @param lon2
   *
   * @return distance in meters between points as a double
   */
  public static double vincentyDistance(double lat1,
                                        double lon1,
                                        double lat2,
                                        double lon2) {
    double f = 1 / INVERSEFLATTENING;
    double L = Math.toRadians(lon2 - lon1);
    double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
    double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
    double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
    double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

    double lambda = L, lambdaP, iterLimit = 100;

    double cosSqAlpha = 0, sinSigma = 0, cosSigma = 0, cos2SigmaM = 0, sigma = 0, sinLambda = 0, sinAlpha = 0, cosLambda = 0;
    do {
      sinLambda = Math.sin(lambda);
      cosLambda = Math.cos(lambda);
      sinSigma = Math
              .sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                      + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                      * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
      if (sinSigma == 0) {
        return 0; // co-incident points
      }
      cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
      sigma = Math.atan2(sinSigma, cosSigma);
      sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
      cosSqAlpha = 1 - sinAlpha * sinAlpha;
      if (cosSqAlpha != 0) {
        cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
      } else {
        cos2SigmaM = 0;
      }
      double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
      lambdaP = lambda;
      lambda = L + (1 - C) * f * sinAlpha
              * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
    } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

    if (iterLimit == 0) {
      return 0; // formula failed to converge
    }

    double uSq = cosSqAlpha * (Math.pow(EQUATORIALRADIUS, 2) - Math.pow(POLARRADIUS, 2))
            / Math.pow(POLARRADIUS, 2);
    double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
    double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
    double deltaSigma = B
            * sinSigma
            * (cos2SigmaM + B
            / 4
            * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
            * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
    double s = POLARRADIUS * A * (sigma - deltaSigma);

    return s;
  }}
