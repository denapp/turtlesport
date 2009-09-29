package fr.turtlesport.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author denis
 * 
 */
public final class ResourceBundleUtility {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ResourceBundleUtility.class);
  }

  private ResourceBundleUtility() {
  }

  /**
   * Restitue le fichier de ressource.
   * 
   * @param lang
   *          le langage.
   * @param clazz
   *          la classe.
   * @throws IllegalArgumentException
   * @return
   * @throws IOException
   */
  public static ResourceBundle getBundle(ILanguage lang, Class<?> clazz) {
    if (lang == null) {
      throw new IllegalArgumentException("lang");
    }
    return getBundle(lang.getLocale(), clazz);
  }

  /**
   * Restitue le fichier de ressource.
   * 
   * @param locale
   * @param clazz
   *          la classe.
   * @throws IllegalArgumentException
   * @return
   * @throws IOException
   */
  public static ResourceBundle getBundle(Locale locale, Class<?> clazz) {
    if (locale == null) {
      throw new IllegalArgumentException("locale");
    }
    if (clazz == null) {
      throw new IllegalArgumentException("clazz");
    }

    // Extraction du nom :
    String baseName = clazz.getName().substring(clazz.getName()
        .lastIndexOf('.') + 1);

    // construction du nom de la ressource
    StringBuilder st = new StringBuilder();
    st.append(baseName);
    st.append('_');
    st.append(locale.getLanguage());
    if (locale.getCountry() != null && !"".equals(locale.getCountry())) {
      st.append('_');
      st.append(locale.getCountry());
    }
    st.append(".properties");

    InputStream in = null;
    try {
      in = clazz.getResourceAsStream(st.toString());
      if (log.isDebugEnabled()) {
        log.debug("ressource " + clazz + " " + st.toString());
      }
      return new PropertyResourceBundle(in);
    }
    catch (IOException e) {
      throw new MissingResourceException("Impossible de trouver la resource de nom "
                                             + baseName + ", locale " + locale,
                                         baseName + "_" + locale,
                                         "");
    }
    finally {
      try {
        if (in != null) {
          in.close();
        }
      }
      catch (IOException e) {
        log.error("", e);
      }
    }
  }

  /**
   * Restitue le fichier de ressource.
   * 
   * @param lang
   *          le langage.
   * @param clazz
   *          la classe.
   * @throws IllegalArgumentException
   * @return
   * @throws IOException
   */
  public static ResourceBundle getBundle(String baseName,
                                         ILanguage lang,
                                         Class<?> clazz) {
    if (lang == null) {
      throw new IllegalArgumentException("lang");
    }
    return getBundle(baseName, lang.getLocale(), clazz);
  }

  /**
   * Restitue le fichier de ressource.
   * 
   * @param locale
   *          le langage.
   * @param clazz
   *          la classe.
   * @throws IllegalArgumentException
   * @return
   * @throws IOException
   */
  public static ResourceBundle getBundle(String baseName,
                                         Locale locale,
                                         Class<?> clazz) {
    if (baseName == null || "".equals(baseName)) {
      throw new IllegalArgumentException("baseName");
    }
    if (locale == null) {
      throw new IllegalArgumentException("locale");
    }
    if (clazz == null) {
      throw new IllegalArgumentException("clazz");
    }

    // construction du nom de la ressource
    StringBuilder st = new StringBuilder();
    st.append(baseName);
    st.append('_');
    st.append(locale.getLanguage());
    if (locale.getCountry() != null && !"".equals(locale.getCountry())) {
      st.append('_');
      st.append(locale.getCountry());
    }
    st.append(".properties");

    InputStream in = null;
    try {
      in = clazz.getResourceAsStream(st.toString());
      return new PropertyResourceBundle(in);
    }
    catch (IOException e) {
      log.error("", e);
      throw new MissingResourceException("Impossible de trouver la resource de nom "
                                             + baseName + ", locale " + locale,
                                         baseName + "_" + locale,
                                         "");
    }
    finally {
      try {
        if (in != null) {
          in.close();
        }
      }
      catch (IOException e) {
        log.error("", e);
      }
    }
  }

}
