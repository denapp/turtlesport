package fr.turtlesport.map;

import java.io.File;
import java.util.HashMap;

import org.jdesktop.swingx.mapviewer.AbstractTileFactory;
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
    TileFactoryInfo tileProviderInfo;

    // mapnik
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://tile.openstreetmap.org",
                                                         "mapnik");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // osma plus valide
    // tileProviderInfo = new
    // OpenStreetMapTileProviderInfo("http://tah.openstreetmap.org/Tiles/tile",
    // "osma");
    // dir = new File(dirCache, tileProviderInfo.getName());
    // hashDiskCache.put(tileProviderInfo.getName(),
    // new DiskTitleCache(dir, tileProviderInfo));

    // opencyclemap
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://tile.opencyclemap.org/cycle",
                                                         "cyclemap");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));
    // MapQuest
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://otile1.mqcdn.com/tiles/1.0.0/osm",
                                                         "MapQuest");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // Transport
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://tile2.opencyclemap.org/transport",
                                                         "Transport");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // Landscape
    tileProviderInfo = new OpenStreetMapTileProviderInfo("http://tile3.opencyclemap.org/landscape",
                                                         "Landscape");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // IGN
    String ign = "http://gpp3-wxs.ign.fr/tyujsdxmzox31ituc2uw0qwl/geoportail/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&LAYER=GEOGRAPHICALGRIDSYSTEMS.MAPS&STYLE=normal&FORMAT=image/jpeg&TILEMATRIXSET=PM&TILEMATRIX=#zoom#&TILEROW=#y#&TILECOL=#x#&extParamId=aHR0cDovL3d3dy5nZW9wb3J0YWlsLmdvdXYuZnIvYWNjdWVpbA==";
    tileProviderInfo = new IGNMapTileProviderInfo(ign, "IGN");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // VirtualEarth Hubrid
    tileProviderInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP, "VirtualEarth Map");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // VirtualEarth Map
    tileProviderInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE, "VirtualEarth Satellite");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

    // VirtualEarth
    tileProviderInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID, "VirtualEarth Hybrid");
    dir = new File(dirCache, tileProviderInfo.getName());
    hashDiskCache.put(tileProviderInfo.getName(),
                      new DiskTitleCache(dir, tileProviderInfo));

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

  }

  /**
   * Restitue les noms des maps.
   * 
   * @return les noms des maps
   */
  public static String[] getTileNames() {
    String[] names = { TurtleEmptyTileFactory.NAME,
        "mapnik",
        "cyclemap",
        "MapQuest",
        "Transport",
        "Landscape",
        "IGN",
        "VirtualEarth Map",
        "VirtualEarth Satellite",
        "VirtualEarth Hybrid"};
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
      DiskTitleCache cache = hashDiskCache.get(key);
      if (cache != null) {
        length += cache.length();
      }
    }
    return length;
  }
}
