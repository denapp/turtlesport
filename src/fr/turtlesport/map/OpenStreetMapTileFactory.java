package fr.turtlesport.map;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdesktop.swingx.mapviewer.AbstractTileFactory;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import fr.turtlesport.Configuration;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public final class OpenStreetMapTileFactory extends AbstractTileFactory
                                                                       implements
                                                                       TileFactoryExtended {

  private static File                       dirCache      = new File(Location.userLocation(),
                                                                     "openstreetmap");

  private static HashMap<String, TileCache> hashDiskCache = new HashMap<String, TileCache>();

  private static String[]                   names;
  static {
    List<String> list = new ArrayList<String>();

    // mercator
    list.add(TurtleEmptyTileFactory.NAME);

    // mapnik
    addMap(list,
           new OpenStreetMapTileProviderInfo("http://tile.openstreetmap.org",
                                             "mapnik"));
    // opencyclemap
    addMap(list,
           new OpenStreetMapTileProviderInfo("http://tile.opencyclemap.org/cycle",
                                             "cyclemap"));
    // MapQuest
    addMap(list,
           new OpenStreetMapTileProviderInfo("http://otile1.mqcdn.com/tiles/1.0.0/osm",
                                             "MapQuest"));
    // Transport
    addMap(list,
           new OpenStreetMapTileProviderInfo("http://tile2.opencyclemap.org/transport",
                                             "Transport"));
    // Landscape
    addMap(list,
           new OpenStreetMapTileProviderInfo("http://tile3.opencyclemap.org/landscape",
                                             "Landscape"));
    // IGN
    addMap(list, new IGNMapTileProviderInfo("ign"));

    // VirtualEarth Map
    addMap(list,
           new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP,
                                           "VirtualEarth Map"));
    // VirtualEarth Satellite
    addMap(list,
           new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE,
                                           "VirtualEarth Satellite"));
    // VirtualEarth Hybrid
    addMap(list,
           new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID,
                                           "VirtualEarth Hybrid"));

    // MapQuest-OSM
    // tileProviderInfo = new
    // OpenStreetMapTileProviderInfo("http://otile1.mqcdn.com/tiles/1.0.0/osm",
    // "MapQuest-OSM");
    // dir = new File(dirCache, tileProviderInfo.getName());
    // hashDiskCache.put(tileProviderInfo.getName(),
    // new DiskTitleCache(dir, tileProviderInfo));

    // MapQuest-Aerial
    // tileProviderInfo = new
    // OpenStreetMapTileProviderInfo("http://oatile2.mqcdn.com/naip",
    // "MapQuest-Aerial");
    // dir = new File(dirCache, tileProviderInfo.getName());
    // hashDiskCache.put(tileProviderInfo.getName(),
    // new DiskTitleCache(dir, tileProviderInfo));

    names = new String[list.size()];
    list.toArray(names);
  }

  private static void addMap(List<String> list, TileFactoryInfo tileProviderInfo) {
    File dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));
    list.add(tileProviderInfo.getName());
  }

  /**
   * Restitue les noms des maps.
   * 
   * @return les noms des maps
   */
  public static String[] getTileNames() {
    return names;
  }

  /**
   * @param url
   * @param name
   */
  private OpenStreetMapTileFactory(TileFactoryInfo tileProviderInfo) {
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
   * Restitue la factory.
   * 
   * @return
   */
  public static TileFactory getTileFactory(String name) {
    TileCache cache = hashDiskCache.get(name);
    TileFactory tileFactory;
    if (cache == null) {
      tileFactory = new TurtleEmptyTileFactory();
    }
    else if (cache instanceof DiskTitleCache) {
      tileFactory = new OpenStreetMapTileFactory(((DiskTitleCache) cache).getTileProviderInfo());
    }
    else {
      tileFactory = new OpenStreetMapTileFactory(((TitleCacheExt) cache).getTileProviderInfo());
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
    String key = Configuration.getConfig().getProperty("map", "tile", "mapnik");

    TileFactory tileFactory;
    if (TurtleEmptyTileFactory.NAME.equals(key) /* || !URLPing.ping() */) {
      tileFactory = new TurtleEmptyTileFactory();
    }
    else {
      tileFactory = getTileFactory(key);
      if (tileFactory == null) {
        tileFactory = getTileFactory(getTileNames()[1]);
      }
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
      TileCache cache = hashDiskCache.get(key);
      if (cache != null && cache instanceof DiskTitleCache) {
        length += ((DiskTitleCache)cache).length();
      }
    }
    return length;
  }
}
