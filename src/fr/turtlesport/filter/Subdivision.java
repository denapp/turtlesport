package fr.turtlesport.filter;

import fr.turtlesport.db.DataRunTrk;

/**
 * @author Denis Apparicio
 * 
 */
public final class Subdivision {

  private static final double W = 0.1;

  /**
   * 
   */
  private Subdivision() {
  }

  /**
   * 
   */
  public static double[] filter(double[] points) {
    double[] out;

    // Redimensionne la structure pour stocker les nouveaux points
    out = new double[2 * points.length];

    // Replace les points deja presents dans la nouvelle structure
    for (int i = 0; i < points.length; i++) {
      out[i] = points[i];
    }
    for (int i = points.length; i < out.length; i++) {
      out[i] = 0F;
    }

    for (int i = points.length - 1; i > 1; i--) {
      // Creation des nouveaux points par interpolation
      out[2 * i - 1] = (out[i] + out[i - 1]) / 2;

      // Deplace les points deja presents
      out[2 * i] = out[2 * i] * (1 - W) + W / 2
                   * (out[2 * i - 1] + out[2 * i + 1]);

    }

    return out;
  }

  /**
   * 
   */
  public static DataRunTrk[] filter(DataRunTrk[] points) {
    DataRunTrk[] out;

    // Redimensionne la structure pour stocker les nouveaux points
    out = new DataRunTrk[2 * points.length];

    // Replace les points deja pr�sents dans la nouvelle structure
    for (int i = 0; i < points.length; i++) {
      out[i] = new DataRunTrk();
      out[i].setDistance(points[i].getDistance());
      out[i].setHeartRate(points[i].getHeartRate());
      out[i].setAltitude(points[i].isValidAltitude() ? points[i].getAltitude()
          : 0);
    }
    for (int i = points.length; i < out.length; i++) {
      out[i] = new DataRunTrk();
    }

    double value;
    for (int i = points.length - 1; i > 1; i--) {
      // Replace les points deja pr�sents dans la nouvelle structure
      out[2 * i].setDistance(points[i].getDistance());
      out[2 * i].setHeartRate(points[i].getHeartRate());
      out[2 * i].setAltitude(points[i].getAltitude());

      // Creation des nouveaux points par interpolation
      out[2 * i - 1].setDistance((out[i].getDistance() + out[i - 1]
          .getDistance()) / 2);
      out[2 * i - 1].setHeartRate((out[i].getHeartRate() + out[i - 1]
          .getHeartRate()) / 2);
      out[2 * i - 1].setAltitude((out[i].getAltitude() + out[i - 1]
          .getAltitude()) / 2);

      // Deplace les points deja pr�sents
      value = out[2 * i].getDistance() * (1 - W) + W / 2
              * (out[2 * i - 1].getDistance() + out[2 * i + 1].getDistance());
      out[2 * i].setDistance((float) value);

      value = out[2 * i].getHeartRate() * (1 - W) + W / 2
              * (out[2 * i - 1].getHeartRate() + out[2 * i + 1].getHeartRate());
      out[2 * i].setHeartRate((int) value);

      value = out[2 * i].getAltitude() * (1 - W) + W / 2
              * (out[2 * i - 1].getAltitude() + out[2 * i + 1].getAltitude());
      out[2 * i].setAltitude((float) value);
    }

    return out;
  }

}
