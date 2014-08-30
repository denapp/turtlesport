package fr.turtlesport.map;

import javax.swing.ImageIcon;

/**
 * @author Denis Apparicio
 * 
 */
public final class OpenStreetMapTileFactory extends AbstractTileFactoryExtended {

  private String                baseURL;

  public static final ImageIcon ICON = new ImageIcon(OpenStreetMapTileFactory.class
                                         .getResource("osm14.png"));

  /**
   * @param url
   * @param name
   */
  public OpenStreetMapTileFactory(String url, String name) {
    this(url, name, null);
  }

  /**
   * @param url
   * @param name
   * @param icon
   */
  public OpenStreetMapTileFactory(String url, String name, ImageIcon icon) {
    super(new OpenStreetMapTileProviderInfo(url, name));
    this.baseURL = url;
    setSmallIcon(icon == null ? ICON : icon);
    setThreadPoolSize(8);
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

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.AbstractTileFactoryExtended#isEditable()
   */
  @Override
  public boolean isEditable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.AbstractTileFactoryExtended#getBaseURL()
   */
  @Override
  public String getBaseURL() {
    return baseURL;
  }

}
