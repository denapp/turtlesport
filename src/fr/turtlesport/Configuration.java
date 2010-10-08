package fr.turtlesport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.IniFile;
import fr.turtlesport.util.Location;

/**
 * Classe pour la configuration de Turtle Sport.
 * 
 * @author Denis Apparicio
 * 
 */
public final class Configuration {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Configuration.class);
  }

  /** Nom du fichier de configuration. */
  private static final String  CONFIG_FILE = "turtlesport.ini";

  private File                 configFileName;

  private IniFile              ini;

  private static Configuration config;

  /**
   * Charge le fichier de configuration.
   * 
   * @throws ConfigurationException
   */
  private Configuration() throws ConfigurationException {
    // chargement du fichier de configuration
    try {
      ini = new IniFile();
      configFileName = new File(Location.userLocation(), CONFIG_FILE);

      if (!configFileName.isFile()) {
        log.warn("Fichier de configuration non trouv&eacute;");
        InputStream input = getClass().getResourceAsStream(CONFIG_FILE);
        if (input == null) {
          throw new ConfigurationException("Impossible de charger le fichier de configuration");
        }
        ini.load(input);
      }
      else {
        ini.load(new FileInputStream(configFileName));
      }
    }
    catch (IOException e) {
      throw new ConfigurationException("Impossible de charger le fichier de configuration");
    }
  }

  /**
   * Restitue une instance de la configuration.
   * 
   * @return une instance de la configuration.
   */
  public static Configuration getConfig() {
    return config;
  }

  /**
   * @throws ConfigurationException
   */
  public static void initialize() throws ConfigurationException {
    if (config == null) {
      config = new Configuration();
    }
  }

  /**
   * Transaction.
   * 
   */
  public synchronized void beginTransaction() {
    ini.beginTransaction();
  }

  /**
   * Commit.
   * 
   * @throws ConfigurationException
   * 
   * @throws ConfigurationException
   */
  public void commitTransaction() throws ConfigurationException {
    ini.commitTransaction();
    save();
  }

  /**
   * Rollback.
   * 
   * @throws ConfigurationException
   */
  public void rollbackTransaction() {
    ini.rollbackTransaction();
  }

  /**
   * Restitue la valeur de la propri&eacute;t&eacute; <code>propName</code> de
   * la section <code>sectionName</code>.
   * 
   * @param sectionName
   *          nom de la section
   * @param propName
   *          nom de la propri&eacute;t&eacute;
   * @param defaultValue
   *          valeur par d&eacute;faut
   * @return la valeur de la propri&eacute;t&eacute;
   */
  public String getProperty(String sectionName,
                            String propName,
                            String defaultValue) {
    String value = ini.getProperty(sectionName, propName);
    if (value == null || value.length() == 0) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Restitue la valeur de la propri&eacute;t&eacute; <code>propName</code> de
   * la section <code>sectionName</code>.
   * 
   * @param sectionName
   *          nom de la section
   * @param propName
   *          nom de la propri&eacute;t&eacute;
   * @return la valeur de la propri&eacute;t&eacute;
   */
  public String getProperty(String sectionName, String propName) {
    return getProperty(sectionName, propName, null);
  }

  /**
   * Restitue la valeur de la propri&eacute;t&eacute; <code>propName</code> de
   * la section <code>sectionName</code>.
   * 
   * @param sectionName
   *          nom de la section
   * @param propName
   *          nom de la propri&eacute;t&eacute;
   * @param defaultValue
   *          valeur par d&eacute;faut
   * @return la valeur de la propri&eacute;t&eacute;
   */
  public int getPropertyAsInt(String sectionName,
                              String propName,
                              int defaultValue) {
    try {
      String value = getProperty(sectionName, propName);
      if (value != null && value.length() != 0) {
        return Integer.parseInt(value);
      }
    }
    catch (Exception e) {
      log.warn("Erreur getPropertyAsInt: ".concat(String.valueOf(String
          .valueOf(propName))));
    }
    return defaultValue;
  }

  /**
   * Restitue la valeur de la propri&eacute;t&eacute; <code>propName</code> de
   * la section <code>sectionName</code>.
   * 
   * @param sectionName
   *          nom de la section
   * @param propName
   *          nom de la propri&eacute;t&eacute;
   * @param defaultValue
   *          valeur par d&eacute;faut
   * @return la valeur de la propri&eacute;t&eacute;
   */
  public long getPropertyAsLong(String sectionName,
                                String propName,
                                long defaultValue) {
    try {
      String value = getProperty(sectionName, propName);
      if (value != null && value.length() != 0) {
        return Long.parseLong(value);
      }
    }
    catch (Exception e) {
      log.warn("Erreur getPropertyAsLong: ".concat(String.valueOf(String
          .valueOf(propName))));
    }
    return defaultValue;
  }

  /**
   * Restitue la valeur de la propri&eacute;t&eacute; <code>propName</code> de
   * la section <code>sectionName</code>.
   * 
   * @param sectionName
   *          nom de la section
   * @param propName
   *          nom de la propri&eacute;t&eacute;
   * @param defaultValue
   *          valeur par d&eacute;faut
   * @return la valeur de la propri&eacute;t&eacute;
   */
  public boolean getPropertyAsBoolean(String sectionName,
                                      String propName,
                                      boolean defaultValue) {
    try {
      String value = getProperty(sectionName, propName);
      if (value != null) {
        return Boolean.valueOf(value).booleanValue();
      }
    }
    catch (Exception e) {
      log.warn("Erreur getPropertyAsBoolean: ".concat(String.valueOf(String
          .valueOf(propName))));
    }
    return defaultValue;
  }

  /**
   * Ajoute une propri&eacute;t&eacute; a une section.
   * 
   * @param sectionName
   *          nom de la section
   * @param propName
   *          nom de la propri&eacute;t&eacute;
   * @param propValue
   *          valeur de la propri&eacute;t&eacute;
   * @throws ConfigurationException
   */
  public void addProperty(String sectionName, String propName, String propValue) {
    ini.addProperty(sectionName, propName, propValue);
  }

  /**
   * Supprime une propri&eacute;t&eacute; a une section.
   * 
   * @param sectionName
   *          nom de la section
   * @param propName
   *          nom de la propri&eacute;t&eacute;
   * @throws ConfigurationException
   */
  public void removeProperty(String sectionName, String propName) {
    ini.removeProperty(sectionName, propName);
  }

  /**
   * Sauvegarde du fichier de configuration.
   * 
   * @throws ConfigurationException
   */
  public void save() throws ConfigurationException {
    log.debug(">>save");

    try {
      ini.save(configFileName);
    }
    catch (IOException e) {
      throw new ConfigurationException("Impossible de sauvegarder le fichier de configuration",
                                       e);
    }

    log.debug("<<save");
  }

  /**
   * Sauvegarde du fichier avant sortie du programme.
   */
  public void exit() {
    log.debug(">>exit");

    try {
      ini.save(configFileName);
    }
    catch (IOException e) {
      log.error("", e);
    }

    log.debug("<<exit");
  }
}