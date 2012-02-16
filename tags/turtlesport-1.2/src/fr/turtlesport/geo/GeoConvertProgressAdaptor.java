package fr.turtlesport.geo;

/**
 * @author Denis Apparicio
 *
 */
public class GeoConvertProgressAdaptor implements IGeoConvertProgress {

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertProgress#cancel()
   */
  public boolean cancel() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertProgress#begin()
   */
  public void begin(int nbRuns) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertProgress#convert(int, int)
   */
  public void convert(int index, int nbRuns) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertProgress#end()
   */
  public void end() {
  }

}
