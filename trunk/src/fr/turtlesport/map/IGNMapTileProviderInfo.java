package fr.turtlesport.map;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class IGNMapTileProviderInfo extends TileFactoryInfo {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(IGNMapTileProviderInfo.class);
  }

  private static final int    TOP_ZOOM_LEVEL = 19;

  protected IGNMapTileProviderInfo(String url, String name) {
    super(name, 1, TOP_ZOOM_LEVEL - 2, TOP_ZOOM_LEVEL, 256, true, true, // tile
        // size
        // is 256
        // and x/y
        // orientation
        // is normal
        url,
        "x",
        "y",
        "z");

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdesktop.swingx.mapviewer.TileFactoryInfo#getTileUrl(int, int,
   * int)
   */
  public String getTileUrl(int x, int y, int zoom) {
    if (log.isDebugEnabled()) {
      log.debug(">>getTileUrl [" + x + ", " + y + ", " + zoom + "]");
    }
    zoom = TOP_ZOOM_LEVEL - zoom;

    String url = baseURL.replaceFirst("#zoom#", "" + zoom)
        .replaceFirst("#x#", "" + x).replaceFirst("#y#", "" + y);
    System.out.println(url);
    if (log.isDebugEnabled()) {
      log.debug("<<getTileUrl " + url);
    }
    return url;
  }

}
