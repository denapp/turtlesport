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

  private static final String BASE_URL       = "http://gpp3-wxs.ign.fr/tyujsdxmzox31ituc2uw0qwl/geoportail/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&LAYER=GEOGRAPHICALGRIDSYSTEMS.MAPS&STYLE=normal&FORMAT=image/jpeg&TILEMATRIXSET=PM&TILEMATRIX=#zoom#&TILEROW=#y#&TILECOL=#x#&extParamId=aHR0cDovL3d3dy5nZW9wb3J0YWlsLmdvdXYuZnIvYWNjdWVpbA==";

  protected IGNMapTileProviderInfo(String name) {
    super(name,
          1,
          TOP_ZOOM_LEVEL - 2,
          TOP_ZOOM_LEVEL,
          256,
          false,
          false,
          null,
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
    zoom = TOP_ZOOM_LEVEL - zoom;

    String url = BASE_URL.replaceFirst("#zoom#", "" + zoom)
        .replaceFirst("#x#", "" + x).replaceFirst("#y#", "" + y);
    System.out.println(url);
    if (log.isDebugEnabled()) {
      log.debug("<<getTileUrl " + url);
    }
    return url;
  }

}
