package fr.turtlesport.geo.gpx;

import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoSegment;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class Trk extends AbstractGeoRoute {

  private String       name;

  private String       desc;

  private List<Trkseg> listTrkseg;

  private boolean      isComputeDistanceDone = false;

  /**
   * 
   */
  public Trk() {
    super();
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
    if (listTrkseg != null) {
      for (Trkseg seg : listTrkseg) {
        if (seg.getListPoints() != null) {
          for (Wpt p : seg.getListPoints()) {
            list.add(p);
          }
        }
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
    computeDistance();
    return getTrkseg(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
   */
  public int getSegmentSize() {
    return (listTrkseg == null) ? 0 : listTrkseg.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getSegments()
   */
  public List<IGeoSegment> getSegments() {
    ArrayList<IGeoSegment> list = new ArrayList<IGeoSegment>();
    if (listTrkseg != null) {
      computeDistance();
      for (Trkseg s : listTrkseg) {
        list.add(s);
      }
    }
    return list;
  }

  /**
   * Valorise le nom du de la piste.
   * 
   * @param name
   *          la nouvelle valeur.
   */
  protected void setName(String name) {
    this.name = name;
  }

  /**
   * Restitue la description de la piste.
   * 
   * @return la description de la piste.
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Valorise la description de la piste.
   * 
   * @param desc
   *          la nouvelle valeur.
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * Restitue le nombre de point.
   * 
   * @return le nombre de point.
   */
  public int getTrkSize() {
    int nb = 0;
    if (listTrkseg != null) {
      for (Trkseg seg : listTrkseg) {
        nb += seg.getTrkSize();
      }
    }
    return nb;
  }

  /**
   * Ajoute un segment.
   * 
   * @param seg
   */
  protected void addTrkseg(Trkseg seg) {
    if (seg == null) {
      throw new IllegalArgumentException();
    }
    if (listTrkseg == null) {
      synchronized (Trkseg.class) {
        listTrkseg = new ArrayList<Trkseg>();
      }
    }
    listTrkseg.add(seg);
    isComputeDistanceDone = false;
  }

  /**
   * Restitue le segment &agrave; l'index sp&eacute;cifi&eacute;. de segment.
   * 
   * @return le segment &agrave; l'index sp&eacute;cifi&eacute;.
   */
  protected Trkseg getTrkseg(int index) {
    if (listTrkseg == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    computeDistance();
    return listTrkseg.get(index);
  }

  /**
   * Mis &agrave; jour des distances.
   */
  private void computeDistance() {
    if (!isComputeDistanceDone) {
      getAllPoints();
    }
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

}
