package fr.turtlesport.util;

import fr.turtlesport.log.TurtleLogger;

/**
 * Classe utilitaire <code>String</code>.
 * 
 * @author Denis Apparicio
 * 
 */
public final class StringUtil {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Library.class);
  }

  private StringUtil() {
  }

  /**
   * Remplace les retours &agrave; la ligne par &lt;br&gt;.
   * 
   * @param value
   *          la chaine &agrave; formatter.
   * @return la chaine formatter.
   */
  public static String formatToHtml(String value) {
    if (value == null) {
      return null;
    }

    log.debug(">>formatToHTML value=" + value);

    StringBuffer st = new StringBuffer();
    st.append("<html><body>");
    st.append(value.replaceAll("\n", "<br>"));
    st.append("</body></html>");

    log.debug("<<formatToHTML res=" + st.toString());
    return st.toString();
  }

  /**
   * Concat&egrave;ne des chaines de caract&egrave;res au format HTML.
   * 
   * @param value
   *          les chaines de caract&egrave;res �&agrave;concat&eacute;ner.
   * @return la chaine HTML.
   */
  public static String formatToHtml(String[] value) {
    if (value == null) {
      return null;
    }

    log.debug(">>formatToHTML");
    StringBuffer st = new StringBuffer();
    st.append("<html><body>");
    if (value != null) {
      for (int i = 0; i < value.length; i++) {
        st.append(value[i].replaceAll("\n", "<br>"));
        if (i < value.length - 1) {
          st.append("<br>");
        }
      }
    }
    st.append("</body></html>");

    log.debug("<<formatToHTML");
    return st.toString();
  }
}
