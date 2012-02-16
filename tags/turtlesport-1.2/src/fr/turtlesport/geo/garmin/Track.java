package fr.turtlesport.geo.garmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Denis Apparicio
 * 
 */
public class Track {
  private ArrayList<TrackPoint> listTrackPoint;

  /**
   * 
   */
  public Track() {
    super();
  }

  /**
   * Ajoute un point.
   * 
   * @param p
   *          le point.
   */
  public void addPoint(TrackPoint p) {
    if (listTrackPoint == null) {
      synchronized (Track.class) {
        listTrackPoint = new ArrayList<TrackPoint>();
      }
    }
    listTrackPoint.add(p);
  }

  /**
   * Restitue la liste des points.
   * 
   * @return la liste des points.
   */
  public List<TrackPoint> getTrackPoints() {
    if (listTrackPoint == null) {
      return Collections.emptyList();
    }
    return listTrackPoint;
  }

  /**
   * Restitue le point &agrave; l'index sp&eacute;cifi&eacute;.
   * 
   * @param index
   *          l'index du point.
   * @throws IndexOutofBoundException
   */
  public TrackPoint getTrackPoint(int index) {
    if (listTrackPoint == null) {
      throw new IndexOutOfBoundsException("size =0, index=" + index);
    }
    return listTrackPoint.get(index);
  }

  /**
   * Restitue le nombre de points.
   * 
   * @return le nombre de points.
   */
  public int getTrackPointSize() {
    return (listTrackPoint == null) ? 0 : listTrackPoint.size();
  }

}
