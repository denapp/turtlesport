package fr.turtlesport.geo.gpx;

import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.device.IProductDevice;
import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoSegment;
import fr.turtlesport.util.GeoUtil;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class Rte extends AbstractGeoRoute {

  private String                 name;

  private String                 desc;

  private ArrayList<Wpt>         listRtept;

  private ArrayList<IGeoSegment> listSegment;

  private RteSegment             segment               = new RteSegment();

  private boolean                isComputeDistanceDone = false;

  private IProductDevice         productDevice;

  /**
   * 
   */
  public Rte() {
    super();
  }

  @Override
  public IProductDevice getProductDevice() {
    return productDevice;
  }

  public void setProductDevice(IProductDevice productDevice) {
    this.productDevice = productDevice;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getName()
   */
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getAllPoints()
   */
  public List<IGeoPositionWithAlt> getAllPoints() {
    ArrayList<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
    if (listRtept != null) {
      for (Wpt p : listRtept) {
        list.add(p);
      }
      computeDistance(list);
    }
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getSegment(int)
   */
  public IGeoSegment getSegment(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException("size 0, index " + index);
    }
    computeDistance();
    return segment;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
   */
  public int getSegmentSize() {
    return 1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getSegments()
   */
  public List<IGeoSegment> getSegments() {
    if (listSegment == null) {
      synchronized (Rte.class) {
        listSegment = new ArrayList<IGeoSegment>();
        listSegment.add(segment);
      }
      computeDistance();
    }
    return listSegment;
  }

  /**
   * Valorise le nom de la route.
   * 
   * @param name
   *          la nouvelle valeur.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Restitue la descritpion de la route.
   * 
   * @return la descritpion de la route.
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Valorise la descritpion de la route.
   * 
   * @param desc
   *          la nouvelle valeur.
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * Ajoute un point.
   * 
   * @param wpt
   *          le point.
   */
  protected void addRtept(Wpt wpt) {
    if (wpt == null) {
      throw new IllegalArgumentException();
    }
    if (listRtept == null) {
      synchronized (Trkseg.class) {
        listRtept = new ArrayList<Wpt>();
      }
    }
    listRtept.add(wpt);
    isComputeDistanceDone = false;
  }

  /**
   * Restitue le nombre de points de la route.
   * 
   * @return le nombre de points de la route.
   */
  public int getRteptSize() {
    return (listRtept == null) ? 0 : listRtept.size();
  }

  /**
   * Restitue le point de la route &agrave; l'index sp&eacute;cifi&eacute;.
   * 
   * @param index
   * @return le point de la route &agrave; l'index sp&eacute;cifi&eacute;.
   */
  public Wpt getRtept(int index) {
    if (listRtept == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return listRtept.get(index);
  }

  /**
   * Mis &agrave; jour des distances.
   */
  private void computeDistance() {
    computeDistance(getAllPoints());
  }

  /**
   * Mis &agrave; jour des distances.
   */
  private void computeDistance(List<IGeoPositionWithAlt> list) {
    if (isComputeDistanceDone) {
      return;
    }

    double distance = 0;
    for (int i = 1; i < list.size(); i++) {
      distance += GeoUtil.computeDistance(list.get(i - 1), list.get(i));
      ((Wpt) list.get(i)).setDistanceMeters(distance);
    }
    isComputeDistanceDone = true;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class RteSegment extends AbstractGpxSegment {

    public RteSegment() {
      super(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getPoints()
     */
    public List<IGeoPositionWithAlt> getPoints() {
      return getAllPoints();
    }

  }

}
