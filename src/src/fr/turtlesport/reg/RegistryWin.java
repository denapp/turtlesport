package fr.turtlesport.reg;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.util.Library;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public final class RegistryWin {

  /** Nom de la JNI. */
  private static final String LIBRARY_NAME        = "turtleRegistryWin";

  /** Nom des cles */
  private static final String HKEY_CLASSES_ROOT   = "HKEY_CLASSES_ROOT";

  private static final String HKEY_CURRENT_USER   = "HKEY_CURRENT_USER";

  private static final String HKEY_LOCAL_MACHINE  = "HKEY_LOCAL_MACHINE";

  private static final String HKEY_USERS          = "HKEY_USERS";

  private static final String HKEY_CURRENT_CONFIG = "HKEY_CURRENT_CONFIG";

  /** Instance unique avc les cles. */
  private static RegistryWin  hkeyClassesRoot     = new RegistryWin(HKEY_CLASSES_ROOT);

  private static RegistryWin  hkeyCurrentUser     = new RegistryWin(HKEY_CURRENT_USER);

  private static RegistryWin  hkeyLocalMachine    = new RegistryWin(HKEY_LOCAL_MACHINE);

  private static RegistryWin  hkeyUsers           = new RegistryWin(HKEY_USERS);

  private static RegistryWin  hkeyCurrentConfig   = new RegistryWin(HKEY_CURRENT_CONFIG);

  private String              key;

  private static boolean             isInit              = false;

  /**
   * 
   */
  private RegistryWin(String key) {
    if (!isInit) {
      if (!OperatingSystem.isWindows()) {
        throw new IllegalAccessError("os different de windows <"
                                   + OperatingSystem.name() + ">");
      }
      // chargement de la librairie
      Library.load(UsbPacket.class, LIBRARY_NAME);
      isInit = true;
    }
    this.key = key;
  }

  /**
   * @return
   */
  public static RegistryWin classesRoot() {
    return hkeyClassesRoot;
  }

  /**
   * @return
   */
  public static RegistryWin currentConfig() {
    return hkeyCurrentConfig;
  }

  /**
   * @return
   */
  public static RegistryWin currentUser() {
    return hkeyCurrentUser;
  }

  /**
   * @return
   */
  public static RegistryWin localMachine() {
    return hkeyLocalMachine;
  }

  /**
   * @return
   */
  public static RegistryWin keyUsers() {
    return hkeyUsers;
  }

  /**
   * Restitue la valeur de la cl&eacute; dans la base de registre.
   * 
   * @param key
   * @param subkey
   * @param name
   * @return
   */
  public String get(String subkey, String name) {
    return get(key, subkey, name);
  }

  /**
   * Restitue la valeur de la cl&eacute; dans la base de registre.
   * 
   * @param key
   * @param subkey
   * @param name
   * @return
   */
  private static native String get(String key, String subkey, String name);
}
