package fr.turtlesport.util;

/**
 * @author Denis Apparicio
 * 
 */
public class SystemProperties {

  static {
    System
        .setProperty("http.agent",
                     "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/31.0.1650.63 Chrome/31.0.1650.63 Safari/537.36");
  }
  
  public static void configure() {
  }
}
