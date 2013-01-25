package fr.turtlesport.geo.gpx;

import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.geo.IGeoPositionWithAlt;

/**
 * @author Denis Apparicio
 * 
 */
public class Trkseg extends AbstractGpxSegment {
  private List<Wpt> listPoints;

  private String    name;

  private String    desc;

  /**
   * 
   */
  public Trkseg(int index) {
    super(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getPoints()
   */
  public List<IGeoPositionWithAlt> getPoints() {
    List<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
    if (listPoints != null) {
      for (Wpt p : listPoints) {
        list.add(p);
      }
    }
    return list;
  }

  /**
   * Restitue le nom du segment.
   * 
   * @return le nom du segment.
   */
  public String getName() {
    return name;
  }

  /**
   * Valorise le nom du segment.
   * 
   * @param name
   *          la nouvelle valeur.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Restitue la description du segment.
   * 
   * @return la description du segment.
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Valorise la description du segment.
   * 
   * @param desc
   *          la nouvelle valeur.
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * Restitue la liste des points.
   * 
   * @return la liste des points.
   */
  public List<Wpt> getListPoints() {
    return listPoints;
  }

  /**
   * @param point
   */
  public void addTrk(Wpt point) {
    if (point == null) {
      throw new IllegalArgumentException();
    }
    if (listPoints == null) {
      synchronized (Trkseg.class) {
        listPoints = new ArrayList<Wpt>();
      }
    }
    listPoints.add(point);
  }

  /**
   * Restitue le nombre de point.
   * 
   * @return
   */
  public int getTrkSize() {
    return (listPoints == null) ? 0 : listPoints.size();
  }

  /**
   * Restitue le point &agrave; l'index sp&eacute;cifi&eacute;. de segment.
   * 
   * @return le point &agrave; l'index sp&eacute;cifi&eacute;.
   */
  public Wpt getTrk(int index) {
    if (listPoints == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return listPoints.get(index);
  }

}
