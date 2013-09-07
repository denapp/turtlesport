package fr.turtlesport.map;

import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 * @author Denis Apparicio
 * 
 */
public class TitleCacheExt extends TileCache {
 
  private TileFactoryInfo tileProviderInfo;

  /**
   * @param tileProviderInfo
   */
  protected TitleCacheExt(TileFactoryInfo tileProviderInfo) {
    this.tileProviderInfo = tileProviderInfo;
  }

  /**
   * @return
   */
  public TileFactoryInfo getTileProviderInfo() {
    return tileProviderInfo;
  }
}
