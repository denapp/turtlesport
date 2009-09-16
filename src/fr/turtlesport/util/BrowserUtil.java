package fr.turtlesport.util;

import java.lang.reflect.Method;
import java.net.URI;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class BrowserUtil {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(BrowserUtil.class);
  }

  private BrowserUtil() {
    super();
  }

  /**
   * Ouvre cette URI avec le navigateur par défaut.
   * 
   * @param uri
   * @return <code>true</code> si l'url a ete ouverte.
   */
  public static boolean browse(URI uri) {
    if (uri == null) {
      throw new IllegalArgumentException();
    }

    if (!OperatingSystem.isMacOSX() && isDesktopBrowseSupported()) {
      if (desktopBrowse(uri)) {
        return true;
      }
    }
    return defaultBrowse(uri);
  }

  private static boolean defaultBrowse(URI uri) {
    try {
      if (OperatingSystem.isWindows()) {
        // String[] cmd = new String[4];
        // cmd[0] = "cmd.exe";
        // cmd[1] = "/C";
        // cmd[2] = "start";
        // cmd[3] = uri.toString();
        // Runtime.getRuntime().exec(cmd);
        LaunchWinApp.launch("open", uri.toString());
      }
      else if (OperatingSystem.isUnix()) {
        String[] browsers = { "firefox",
            "konqueror",
            "epiphany",
            "mozilla",
            "netscape",
            "opera",
            "links",
            "lynx" };
        String browser = null;
        for (int i = 0; i < browsers.length; i++) {
          if (Runtime.getRuntime().exec(new String[] { "which", browsers[i] })
              .waitFor() == 0) {
            browser = browsers[i];
            break;
          }
        }
        if (browser != null) {
          Runtime.getRuntime().exec(new String[] { browser, uri.toString() });
        }
      }
      else if (OperatingSystem.isMacOSX()) {
        try {
          Class<?> clazz = Class.forName("com.apple.eio.FileManager");
          Method method = clazz.getMethod("openURL", String.class);
          method.invoke(clazz, uri.toString());
        }
        catch (Throwable e) {
          log.error("", e);
          Runtime.getRuntime().exec("open " + uri.toString());
        }
      }
      return true;
    }
    catch (Throwable e) {
      log.error("", e);
      return false;
    }
  }

  private static boolean isDesktopBrowseSupported() {
    try {
      Class<?> classDesktop = Class.forName("java.awt.Desktop");

      Boolean b = (Boolean) classDesktop.getMethod("isDesktopSupported")
          .invoke(classDesktop);
      if (!b.booleanValue()) {
        return false;
      }

      Object objDesktop = classDesktop.getMethod("getDesktop")
          .invoke(classDesktop);
      Class<?> classAction = Class.forName("java.awt.Desktop$Action");
      Method method = classDesktop.getMethod("isSupported", classAction);

      Object enumMail = null;
      for (Object c : classAction.getEnumConstants()) {
        if ("BROWSE".equals(c.toString())) {
          enumMail = c;
          break;
        }
      }

      b = (Boolean) method.invoke(objDesktop, enumMail);
      return b.booleanValue();
    }
    catch (Throwable e) {
      return false;
    }
  }

  private static boolean desktopBrowse(URI uri) {
    try {
      Class<?> clazz = Class.forName("java.awt.Desktop");
      Object objDesktop = clazz.getMethod("getDesktop").invoke(clazz);

      Method method = clazz.getMethod("browse", URI.class);
      method.invoke(objDesktop, uri);
      return true;
    }
    catch (Throwable e) {
      log.error(",e");
      return false;
    }
  }

}
