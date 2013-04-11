package fr.turtlesport.util;

import java.awt.Desktop;
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
        Exec.exec(new String[] {"xdg-open", uri.toString()});
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
      return Desktop.isDesktopSupported()
             && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    }
    catch (Throwable e) {
      return false;
    }
  }

  private static boolean desktopBrowse(URI uri) {
    try {
      Desktop.getDesktop().browse(uri);
      return true;
    }
    catch (Throwable e) {
      log.error(",e");
      return false;
    }
  }

}
