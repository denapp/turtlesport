package fr.turtlesport.util;

import java.awt.Color;

/**
 * @author Denis Apparicio
 * 
 */
public final class ColorUtil {

  private ColorUtil() {
  }

  /**
   * Transformation d'une couleur en hex string.
   * 
   * @param args
   *          color.
   * @return la couleur au format #FFFFFF;
   */
  public static String toHexString(Color color) {
    float[] res = color.getRGBColorComponents(null);

    StringBuilder st = new StringBuilder();
    st.append("#");
    st.append(Float.toHexString(res[0]));
    st.append(Float.toHexString(res[1]));
    st.append(Float.toHexString(res[2]));

    return st.toString();
  }

}
