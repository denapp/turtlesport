package fr.turtlesport.geo.pcx5;

import fr.turtlesport.device.IProductDevice;
import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Apparicio
 * 
 */
public class Pcx5GeoRoute extends AbstractGeoRoute {

  /** Liste des points GPS. */
  private ArrayList<IGeoPositionWithAlt> listGpsPoint;

  /**
   * 
   */
  public Pcx5GeoRoute() {
    super();
    listGpsPoint = new ArrayList<IGeoPositionWithAlt>();
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see fr.turtlesport.geo.IGeoRoute#getAllPoints()
  // */
  // public List<IGeoPositionWithAlt> getAllPoints() {
  // return listGpsPoint;
  // }
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see fr.turtlesport.geo.IGeoRoute#getName()
  // */
  // public String getName() {
  // return null;
  // }
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see fr.turtlesport.geo.IGeoRoute#getSegmentPoints(int)
  // */
  // public List<IGeoPositionWithAlt> getSegmentPoints(int index) {
  // if (index != 0) {
  // throw new IndexOutOfBoundsException("index=" + index + " , size=1");
  // }
  // return listGpsPoint;
  // }
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
  // */
  // public int getSegmentSize() {
  // return 1;
  // }

  /**
   * Ajoute un point pcx5.
   * 
   * @param p
   *          le point pcx5.
   */
  protected void add(Pcx5Point p) {
    listGpsPoint.add(p);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getAllPoints()
   */
  public List<IGeoPositionWithAlt> getAllPoints() {
    return listGpsPoint;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getName()
   */
  public String getName() {
    return null;
  }

  public IGeoSegment getSegment(int index) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
   */
  public int getSegmentSize() {
    return 1;
  }

  public List<IGeoSegment> getSegments() {
    return null;
  }

  @Override
  public IProductDevice getProductDevice() {
    return null;
  }

}
