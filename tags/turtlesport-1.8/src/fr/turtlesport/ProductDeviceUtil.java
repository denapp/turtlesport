package fr.turtlesport;

import java.util.HashMap;

import com.garmin.fit.GarminProduct;

/**
 * @author Denis Apparicio
 * 
 */
public final class ProductDeviceUtil {

  private static HashMap<Integer, String> hashProduct = new HashMap<Integer, String>();
  static {
    hashProduct.put(GarminProduct.FR405, "Forerunner 405");
    hashProduct.put(GarminProduct.FR50, "Forerunner 50");
    hashProduct.put(GarminProduct.FR60, "Forerunner 60");
    hashProduct.put(GarminProduct.FR405, "Forerunner 405");
    hashProduct.put(GarminProduct.FR310XT, "Forerunner 310 XT");
    hashProduct.put(GarminProduct.EDGE500, "EDGE 500");
    hashProduct.put(GarminProduct.FR110, "Forerunner 110");
    hashProduct.put(GarminProduct.EDGE800, "EDGE 800");
    hashProduct.put(GarminProduct.EDGE200, "EDGE 200");
    hashProduct.put(GarminProduct.FR910XT, "Forerunner 910 XT");
    hashProduct.put(GarminProduct.EDGE800, "EDGE 800");
    hashProduct.put(GarminProduct.FR310XT_4T, "Forerunner 310 XT");
    hashProduct.put(1551, "Garmin Fenix");
    hashProduct.put(-1000, "Foretrex 401");
  }

  /**
   * D&eacute;termine si ce nom est connu.
   * 
   * @param name
   * @return <code>true</code> si nom connu.
   */
  public static boolean isKnown(String name) {
    return hashProduct.containsValue(name);
  }

  /**
   * Restitue le nom du produit.
   * 
   * @param id
   */
  public static String name(int id) {
    return hashProduct.get(id);
  }

  /**
   * Restitue le nom du produit.
   * 
   * @param sid
   * @param name
   * @param version
   * @return
   */
  public static String toExternalForm(String sid, String name, String version) {
    if (sid != null) {
      try {
        int id = Integer.parseInt(sid);
        if (hashProduct.containsKey(id)) {
          StringBuffer st = new StringBuffer();
          st.append(hashProduct.get(id));
          if (version != null) {
            st.append(" v");
            st.append(version);
          }
          return st.toString();
        }
      }
      catch (NumberFormatException e) {
      }
    }

    if (hashProduct.containsValue(name)) {
      StringBuffer st = new StringBuffer();
      st.append(name);
      if (version != null) {
        st.append(' ');
        st.append(version);
      }
      return st.toString();
    }

    return null;
  }
}
