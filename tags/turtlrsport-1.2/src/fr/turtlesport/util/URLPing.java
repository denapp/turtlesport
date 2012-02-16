package fr.turtlesport.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Denis Apparicio
 * 
 */
public final class URLPing {

  private URLPing() {
  }

  /**
   * Ping sur une URL.
   * 
   * @param sUrl
   *          url.
   * @param timeout
   * @return <code>true</code> si ok, <code>false</code> sinon.
   */
  public static boolean ping() {
    return ping("http://www.google.fr", 3000);
  }

  /**
   * Ping sur une URL.
   * 
   * @param sUrl
   *          url .
   * @param timeout
   * @return <code>true</code> si ok, <code>false</code> sinon.
   */
  public static boolean ping(String sUrl, int timeout) {
    try {
      URL url = new URL(sUrl);
      HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
      cnx.setConnectTimeout(3000);
      cnx.setRequestMethod("GET");
      cnx.setDoInput(true);

      if (cnx.getResponseCode() == HttpURLConnection.HTTP_OK) {
        return true;
      }
      return false;
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
