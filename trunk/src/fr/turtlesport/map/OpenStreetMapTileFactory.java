package fr.turtlesport.map;


/**
 * @author Denis Apparicio
 * 
 */
public final class OpenStreetMapTileFactory extends AbstractTileFactoryExtended {

  private String baseURL;

  /**
   * @param url
   * @param name
   */
  public OpenStreetMapTileFactory(String url, String name) {
    super(new OpenStreetMapTileProviderInfo(url, name));
    this.baseURL = url;
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
