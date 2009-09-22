package fr.turtlesport.geo.pcx5;

import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoSegment;

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
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
    return null;
  }

}
