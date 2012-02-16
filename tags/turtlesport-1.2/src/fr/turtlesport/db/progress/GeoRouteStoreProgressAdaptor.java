package fr.turtlesport.db.progress;

import fr.turtlesport.geo.IGeoRoute;

/**
 * @author Denis Apparicio
 * 
 */
public class GeoRouteStoreProgressAdaptor implements IGeoRouteStoreProgress {

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#beginStore(int)
   */
  public void beginStore(int maxPoint) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#beginStore(fr.turtlesport.geo.IGeoRoute)
   */
  public void beginStore(IGeoRoute route) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#endStore(fr.turtlesport.geo.IGeoRoute)
   */
  public void endStore(IGeoRoute route) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#endStore()
   */
  public void endStore() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#store(int, int)
   */
  public void store(int current, int maxPoint) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#storePoint(fr.turtlesport.geo.IGeoRoute,
   *      int, int)
   */
  public void storePoint(IGeoRoute route, int currentPoint, int maxPoint) {
  }

}
