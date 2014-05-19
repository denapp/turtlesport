package fr.turtlesport.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import fr.turtlesport.ConfigurationException;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.util.Location;

/**
 * Classe pour la configuration des Maps de Turtle Sport.
 * 
 * @author Denis Apparicio
 * 
 */
public final class MapConfiguration {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MapConfiguration.class);
  }

  /** Nom du fichier de configuration. */
  private static final String     CONFIG_FILE = "maps.xml";

  private File                    fileMap;

  private Maps                    maps;

  private static MapConfiguration singleton   = new MapConfiguration();

  // We want to keep the same connection for a given thread
  // as long as we're in the same transaction
  private ThreadLocal<Maps>       transaction = new ThreadLocal<Maps>();

  /**
   * @return
   */
  public static MapConfiguration getConfig() {
    return singleton;
  }

  /**
   * Charge le fichier map.
   * 
   */
  private MapConfiguration() {
    maps = initMaps();
  }

  /**
   * Ajoute une map.
   * 
   * @param map
   *          la map.
   */
  public void addMap(DataMap map) {
    checkInTransaction();
    if (!transaction.get().getMaps().contains(map)) {
      transaction.get().getMaps().add(map);
    }
  }

  /**
   * Supprime une map.
   * 
   * @param map
   *          la map.
   */
  public void removeMap(DataMap map) {
    checkInTransaction();
    transaction.get().getMaps().remove(map);
  }

  private void checkInTransaction() {
    if (transaction.get() == null) {
      throw new IllegalStateException("not in transaction");
    }
  }

  private Maps initMaps() {
    Maps mapsRead = null;
    // chargement du fichier
    FileInputStream fis = null;
    try {
      fileMap = new File(Location.userLocation(), CONFIG_FILE);
      if (!fileMap.isFile()) {
        mapsRead = new Maps();
      }
      else {
        fis = new FileInputStream(fileMap);
        JAXBContext jc = JAXBContext.newInstance("fr.turtlesport.map");
        Unmarshaller um = jc.createUnmarshaller();
        mapsRead = (Maps) um.unmarshal(fis);
      }
    }
    catch (Throwable e) {
      log.error("", e);
      mapsRead = new ObjectFactory().createMaps();
    }
    finally {
      if (fis != null) {
        try {
          fis.close();
        }
        catch (IOException e) {
        }
      }
    }
    return mapsRead;
  }

  /**
   * Restitue les maps.
   * 
   * @return les maps.
   */
  public Maps getMaps() {
    return maps;
  }

  /**
   * Restitue les maps.
   * 
   * @return les maps.
   */
  public Maps getMapsTransaction() {
    return transaction.get();
  }

  /**
   * Transaction.
   */
  public synchronized void beginTransaction() {
    log.debug(">>beginTransaction");

    if (transaction.get() != null) {
      log.warn("This thread is already in a transaction");
      return;
    }

    Maps mapsNew = initMaps();

    transaction.set(mapsNew);

    log.debug("<<beginTransaction");
  }

  /**
   * Commit.
   * 
   * @throws ConfigurationException
   */
  public void commitTransaction() throws ConfigurationException {
    log.debug(">>commitTransaction");

    if (transaction.get() == null) {
      log.warn("Can't commit: this thread isn't currently in a transaction");
      return;
    }

    // check valid
    Maps mapsNew = getMapsTransaction();
    Iterator<DataMap> it = mapsNew.maps.iterator();
    while (it.hasNext()) {
      DataMap data = it.next();
      if (!data.isValidMap()) {
        it.remove();
      }
    }
    
    for (DataMap map : mapsNew.maps) {
      if (!maps.maps.contains(map)) {
        // Ajout d'une map
        ModelMapkitManager.getInstance().addMapTileFactory(map);
      }
    }
    for (DataMap map : maps.maps) {
      if (!mapsNew.maps.contains(map)) {
        // Suppression d'une map
        ModelMapkitManager.getInstance().removeMapTileFactory(map);
      }
    }
    for (DataMap map : mapsNew.maps) {
      int index = maps.maps.indexOf(map);
      if (index != -1 && !maps.maps.get(index).hasSameFields(map)) {
        // map mis a jour
        ModelMapkitManager.getInstance().removeMapTileFactory(map);
        ModelMapkitManager.getInstance().addMapTileFactory(map);
      }
    }

    transaction.set(null);

    log.debug("<<commitTransaction");
    save(mapsNew);
  }

  /**
   * Rollback.
   * 
   * @throws ConfigurationException
   */
  public void rollbackTransaction() {
    log.debug(">>rollbackTransaction");
    transaction.set(null);
    log.debug("<<rollbackTransaction");
  }

  /**
   * Sauvegarde du fichier
   */
  private void save(Maps mapsNew) {
    log.debug(">>save");

    maps = mapsNew;

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(fileMap);

      JAXBContext jc = JAXBContext.newInstance("fr.turtlesport.map");
      // Créer un programme de conversion
      Marshaller m = jc.createMarshaller();

      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      // Convertir l'objet en fichier.
      m.marshal(maps, fos);
    }
    catch (Throwable e) {
      log.error("Impossible de sauvegarder le fichier map", e);
    }
    finally {
      if (fos != null) {
        try {
          fos.close();
        }
        catch (IOException e) {
        }
      }
    }

    log.debug("<<save");
  }
}