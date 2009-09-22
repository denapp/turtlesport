package fr.turtlesport.map;

import java.io.File;
import java.util.HashMap;

import org.jdesktop.swingx.mapviewer.AbstractTileFactory;
import org.jdesktop.swingx.mapviewer.TileFactory;

import fr.turtlesport.Configuration;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public final class OpenStreetMapTileFactory extends AbstractTileFactory
                                                                       implements
                                                                       TileFactoryExtended {
  // "http://tile.openstreetmap.org", mapnik
  // "http://tah.openstreetmap.org/Tiles/tile",osma
  // "http://andy.sandbox.cloudmade.com/tiles/cycle" cyclemap
  // "http://data.giub.uni-bonn.de/openrouteservice" openrouteservice

  private static File                            dirCache      = new File(Location
                                                                              .userLocation(),
                                                                          "openstreetmap");

  private static HashMap<String, DiskTitleCache> hashDiskCache = new HashMap<String, DiskTitleCache>();
  static {
    File dir;
    OpenStreetMapTileProviderInfo tileProviderInfo;

    // mapnik
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://tile.openstreetmap.org",
                                                         "mapnik");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // osma
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://tah.openstreetmap.org/Tiles/tile",
                                                         "osma");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // cyclemap
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://andy.sandbox.cloudmade.com/tiles/cycle",
                                                         "cyclemap");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

  }

  /**
   * @param url
   * @param name
   */
  private OpenStreetMapTileFactory(OpenStreetMapTileProviderInfo tileProviderInfo) {
    super(tileProviderInfo);
    setTileCache(hashDiskCache.get(tileProviderInfo.getName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.TileFactoryName#getName()
   */
  public String getName() {
    return getInfo().getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.TileFactoryExtended#isConnected()
   */
  public boolean isConnected() {
    return true;
  }

  /**
   * Restitue les noms des maps.
   * 
   * @return les noms des maps
   */
  public static String[] getTileNames() {
    String[] names = { TurtleEmptyTileFactory.NAME,
        "mapnik",
        "osma",
        "cyclemap" };
    return names;
  }

  /**
   * Restitue la factory.
   * 
   * @return
   */
  public static TileFactory getTileFactory(String name) {
    DiskTitleCache cache = hashDiskCache.get(name);
    TileFactory tileFactory;
    if (cache == null) {
      tileFactory = new TurtleEmptyTileFactory();
    }
    else {
      tileFactory = new OpenStreetMapTileFactory(cache.getTileProviderInfo());
    }

    Configuration.getConfig().addProperty("map",
                                          "tile",
                                          tileFactory.getInfo().getName());
    return tileFactory;
  }

  /**
   * Restitue la factory par d&eacute;faut.
   * 
   * @return la factory par d&eacute;faut.
   */
  public static TileFactory getDefaultTileFactory() {
    String key = Configuration.getConfig()
        .getProperty("map", "tile", TurtleEmptyTileFactory.NAME);

    TileFactory tileFactory;
    if (TurtleEmptyTileFactory.NAME.equals(key) /* || !URLPing.ping() */) {
      tileFactory = new TurtleEmptyTileFactory();
    }
    else {
      tileFactory = getTileFactory(key);
      if (tileFactory == null) {
        tileFactory = new TurtleEmptyTileFactory();
      }
    }

    setDefaultTileFactory(tileFactory);
    return tileFactory;
  }

  /**
   * Sauvegarde la factory par d&eacute;faut.
   * 
   * @return la factory par d&eacute;faut.
   */
  public static void setDefaultTileFactory(TileFactory tileFactory) {
    Configuration.getConfig().addProperty("map",
                                          "tile",
                                          tileFactory.getInfo().getName());
  }

  /**
   * Efface le cache.
   */
  public static void cleanCache() {
    for (String key : hashDiskCache.keySet()) {
      hashDiskCache.get(key).needMoreMemory();
    }

    // Effacement des residus de la version 0.1.12
    File dirCache112 = new File(Location.userLocation(), "openstreeetmap");
    if (dirCache112.exists()) {
      File[] files = dirCache112.listFiles();
      if (files != null) {
        for (File f : files) {
          if (f.isFile()) {
            f.delete();
          }
        }
      }
    }
    dirCache112.delete();
  }

  /**
   * Restitue la taille du cache.
   * 
   * @return la taille du cache.
   */
  public static long cacheSize() {
    long length = 0;
    for (String key : getTileNames()) {
      DiskTitleCache cache = hashDiskCache.get(key);
      if (cache != null) {
       length += cache.length(); 
      }
    }
    return length;
  }
}
