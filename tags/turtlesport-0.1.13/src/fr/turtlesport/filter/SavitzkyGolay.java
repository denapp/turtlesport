package fr.turtlesport.filter;

/**
 * @author Denis Apparicio
 * 
 */
public final class SavitzkyGolay {

  // private static final double[] TABLE_COEFF1 = { -2 / 21.0,
  // 3 / 21.0,
  // 6 / 21.0,
  // 7 / 21.0,
  // 6 / 21.0,
  // 3 / 21.0,
  // -2 / 21.0 };

  private static final double[] TABLE_COEFF2 = { -171 / 3059.0,
      -76 / 3059.0,
      9 / 3059.0,
      84 / 3059.0,
      149 / 3059.0,
      204 / 3059.0,
      249 / 3059.0,
      284 / 3059.0,
      309 / 3059.0,
      324 / 3059.0,
      329 / 3059.0,
      324 / 3059.0,
      309 / 3059.0,
      284 / 3059.0,
      249 / 3059.0,
      204 / 3059.0,
      149 / 3059.0,
      84 / 3059.0,
      9 / 3059.0,
      -76 / 3059.0,
      -171 / 3059.0                         };

  /**
   * 
   */
  private SavitzkyGolay() {
  }

  /**
   * Applique un filtre de SavitzkyGolay.
   * 
   * @param y
   *          les donn&eacute; d'&eacute;
   * @return les donn&eacute; apr&egrave;s filtre.
   */
  public static double[] filter(double[] y) {
    double[] yl = new double[y.length];

    int len = TABLE_COEFF2.length;
    int iInf = (len + 1) / 2 - 1;
    int iSup = y.length - (len - 1) / 2;

    if (iInf > y.length) {
      iInf = y.length;
    }

    for (int i = 0; i < iInf; i++) {
      yl[i] = y[i];
    }

    // Lissage
    if (iSup > 0) {
      double sum;
      for (int i = iInf; i < iSup; i++) {
        sum = 0;
        for (int j = 0; j < len; j++) {
          sum += y[i - iInf + j] * TABLE_COEFF2[j];
        }
        yl[i] = sum;
      }
      for (int i = iSup; i < y.length; i++) {
        yl[i] = y[i];
      }
    }

    return yl;
  }

}
