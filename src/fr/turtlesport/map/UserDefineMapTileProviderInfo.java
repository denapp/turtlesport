package fr.turtlesport.map;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class UserDefineMapTileProviderInfo extends TileFactoryInfo {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(UserDefineMapTileProviderInfo.class);
  }
  
  private static final int    TOP_ZOOM_LEVEL = 19;

  public UserDefineMapTileProviderInfo(String baseURL,
                                       String name) {
    this(baseURL, name, 1, TOP_ZOOM_LEVEL);
  }

  public UserDefineMapTileProviderInfo(DataMap data) {
    super(data.getName(),
          data.getZoomMin(),
          data.getZoomMax() - 2,
          data.getZoomMax(),
          256,
          false,
          false,
          data.getUrl(),
          null,
          null,
          null);
  }

  public UserDefineMapTileProviderInfo(String baseURL,
                                       String name,
                                       int minimumZoomLevel,
                                       int maximumZoomLevel) {
    super(name,
          minimumZoomLevel,
          maximumZoomLevel - 2,
          maximumZoomLevel,
          256,
          false,
          false,
          baseURL,
          null,
          null,
          null);
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
    zoom = getTotalMapZoom() - zoom;

    String url = baseURL.replaceFirst("#zoom#", "" + zoom)
        .replaceFirst("#x#", "" + x).replaceFirst("#y#", "" + y);
    if (log.isDebugEnabled()) {
      log.debug("<<getTileUrl " + url);
    }
    return url;
  }
  

}
