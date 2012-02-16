package fr.turtlesport.util;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public final class OperatingSystem {

  /** nom de l'os */
  private static String osname = System.getProperty("os.name");

  private OperatingSystem() {
  }

  /**
   * Restitue le nom de l'OS.
   * 
   * @return le nom de l'OS.
   */
  public static String name() {
    return osname;
  }

  /**
   * D&eacute;termine si OS Unix like.
   * 
   * @return <code>true</code> si OS Unix like, <code>false</code> sinon.
   */
  public static boolean isUnix() {
    return (isAix() || isFreeBSD() || isHPUX() || isLinux() || isOpenUnix() || isSolaris());
  }

  /**
   * D&eacute;termine si OS linux.
   * 
   * @return <code>true</code> si OS linux, <code>false</code> sinon.
   */
  public static boolean isLinux() {
    return (osname.toLowerCase().indexOf("linux") > -1);
  }

  /**
   * D&eacute;termine si OS 32 bits.
   * 
   * @return <code>true</code> si OS 32 bits, <code>false</code> sinon.
   */
  public static boolean is32bits() {
    return "32".equals(System.getProperty("sun.arch.data.model"));
  }
  
  /**
   * D&eacute;termine si OS 64 bits.
   * 
   * @return <code>true</code> si OS 64 bits, <code>false</code> sinon.
   */
  public static boolean is64bits() {
    return "64".equals(System.getProperty("sun.arch.data.model"));
  }
  
  /**
   * D&eacute;termine si kde.
   * 
   * @return <code>true</code> si kde, <code>false</code> sinon.
   */
  public static boolean isKde() {
    return "kde".equals(System.getenv("DESKTOP_SESSION"));
  }

  /**
   * D&eacute;termine si gnome.
   * 
   * @return <code>true</code> si gnome, <code>false</code> sinon.
   */
  public static boolean isGnome() {
    return "gnome".equals(System.getenv("DESKTOP_SESSION"));
  }

  /**
   * D&eacute;termine si xfce.
   * 
   * @return <code>true</code> si xfce<code>false</code> sinon.
   */
  public static boolean isXfce() {
    String desktop = System.getenv("DESKTOP_SESSION");
    return desktop != null && desktop.startsWith("xfce");
  }

  /**
   * D&eacute;termine si OS MacOS X.
   * 
   * @return <code>true</code> si OS MacOS X, <code>false</code> sinon.
   */

  public static boolean isMacOSX() {
    return (osname.toLowerCase().indexOf("mac os x") > -1);
  }

  /**
   * D&eacute;termine si OS Mac.
   * 
   * @return <code>true</code> si OS Mac, <code>false</code> sinon.
   */
  public static boolean isMac() {
    return (!isMacOSX() && (osname.toLowerCase().indexOf("mac") > -1));
  }

  /**
   * D&eacute;termine si OS Windows.
   * 
   * @return <code>true</code> si OS Windows, <code>false</code> sinon.
   */
  public static boolean isWindows() {
    return osname.toLowerCase().startsWith("windows");
  }

  /**
   * D&eacute;termine si OS Windows 95.
   * 
   * @return <code>true</code> si OS Windows 95, <code>false</code> sinon.
   */
  public static boolean isWindows95() {
    return (osname.toLowerCase().indexOf("windows 95") > -1);
  }

  /**
   * D&eacute;termine si OS Windows 98.
   * 
   * @return <code>true</code> si OS Windows 98, <code>false</code> sinon.
   */
  public static boolean isWindows98() {
    return (osname.toLowerCase().indexOf("windows 98") > -1);
  }

  /**
   * D&eacute;termine si OS Windows ME.
   * 
   * @return <code>true</code> si OS Windows ME, <code>false</code> sinon.
   */
  public static boolean isWindowsME() {
    return (osname.toLowerCase().indexOf("windows me") > -1);
  }

  /**
   * D&eacute;termine si OS Windows NT.
   * 
   * @return <code>true</code> si OS Windows NT, <code>false</code> sinon.
   */
  public static boolean isWindowsNT() {
    return (osname.toLowerCase().indexOf("windows nt") > -1);
  }

  /**
   * D&eacute;termine si OS Windows 2000.
   * 
   * @return <code>true</code> si OS Windows 2000, <code>false</code> sinon.
   */
  public static boolean isWindows2000() {
    return (osname.toLowerCase().indexOf("windows 2000") > -1);
  }

  /**
   * D&eacute;termine si OS Windows XP.
   * 
   * @return <code>true</code> si OS Windows XP, <code>false</code> sinon.
   */
  public static boolean isWindowsXP() {
    return (osname.toLowerCase().indexOf("windows xp") > -1);
  }

  /**
   * D&eacute;termine si OS Windows 2003.
   * 
   * @return <code>true</code> si OS Windows 2003, <code>false</code> sinon.
   */
  public static boolean isWindows2003() {
    return (osname.toLowerCase().indexOf("windows 2003") > -1);
  }

  /**
   * D&eacute;termine si OS Solaris.
   * 
   * @return <code>true</code> si OS Solaris, <code>false</code> sinon.
   */
  public static boolean isSolaris() {
    return (osname.toLowerCase().indexOf("solaris") > -1)
           || (osname.toLowerCase().indexOf("sunos") > -1);
  }

  /**
   * D&eacute;termine si OS HPUX.
   * 
   * @return <code>true</code> si OS HPUX, <code>false</code> sinon.
   */
  public static boolean isHPUX() {
    return (osname.toLowerCase().indexOf("hp-ux") > -1);
  }

  /**
   * D&eacute;termine si OS Aix.
   * 
   * @return <code>true</code> si OS AIX, <code>false</code> sinon.
   */
  public static boolean isAix() {
    return (osname.toLowerCase().indexOf("aix") > -1);
  }

  /**
   * D&eacute;termine si OS Free BSD.
   * 
   * @return <code>true</code> si OS Free BSD, <code>false</code> sinon.
   */
  public static boolean isFreeBSD() {
    return (osname.toLowerCase().indexOf("freebsd") > -1);
  }

  /**
   * D&eacute;termine si OS OpenUnix.
   * 
   * @return <code>true</code> si OS OpenUnix, <code>false</code> sinon.
   */
  public static boolean isOpenUnix() {
    return (osname.toLowerCase().indexOf("openunix") > -1);
  }

  /**
   * D&eacute;termine si OS UnixWare.
   * 
   * @return <code>true</code> si OS UnixWare, <code>false</code> sinon.
   */
  public static boolean isUnixWare() {
    return (osname.toLowerCase().indexOf("unixware") > -1);
  }
}
