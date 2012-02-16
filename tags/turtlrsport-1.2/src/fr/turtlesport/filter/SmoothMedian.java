package fr.turtlesport.filter;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class SmoothMedian {

  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(SmoothMedian.class);
  }

  /**
   * Applique un filtre median.
   * 
   * @param y
   *          les donn&eacute; d'&eacute;
   * @return les donn&eacute; apr&egrave;s filtre.
   */
  public static double[] filter(double[] y) {
    return filter(y, 2);
  }

  /**
   * Applique un filtre median.
   * 
   * @param y
   *          les donn&eacute; d'&eacute;
   * @param nb
   * @return les donn&eacute; apr&egrave;s filtre.
   */
  public static double[] filter(double[] y, int nb) {
    log.error(">>filter nb=" + nb);

    nb++;

    double[] yl = new double[y.length / nb + 1];
    log.error("y.length=" + y.length);
    log.error("yl.length=" + yl.length);

    double val = 0;
    int j = 0;
    for (int i = 0; i < y.length; i++) {
      if (i % nb != 0) {
        val += y[i];
      }
      else {
        yl[j] = val / nb;
        log.error(j + "=" + yl[j]);
        j++;
        val = 0;
      }
    }

    log.error("j=" + j);

    return yl;
  }
}
