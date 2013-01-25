package fr.turtlesport.geo;

import java.util.Date;
import java.util.List;

import fr.turtlesport.IProductDevice;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoRoute {

  /** Running. */
  int SPORT_TYPE_RUNNING = 0;

  /** Velo. */
  int SPORT_TYPE_BIKE    = 1;

  /** OTHER. */
  int SPORT_TYPE_OTHER   = 2;

  /**
   * Restitue le produit associ&eacute; &agrave; la route.
   * 
   * @return
   */
  IProductDevice getProductDevice();

  /**
   * Restitue le nom.
   * 
   * @return le nom.
   */
  String getName();

  /**
   * Ajoute des donn&eacute;es; compl&eacute;mentaires.
   * 
   * @return les donn&eacute;es; compl&eacute;mentaire.
   */
  Object getExtra();

  /**
   * Valorise des donn&eacute;es; compl&eacute;mentaires.
   * 
   * @param object
   *          les donn&eacute;es; compl&eacute;mentaire.
   */
  void setExtra(Object objet);

  /**
   * Restitue le type de sport.
   * 
   * @return le type de sport.
   */
  int getSportType();

  /**
   * Valorise le type de sport.
   * 
   * @param sportType
   *          le type de sport.
   */
  void setSportType(int sportType);

  /**
   * Restitue la date de d&eacute;but de la route.
   * 
   * @return la date de d&eacute;but de la route.
   */
  Date getStartTime();

  /**
   * Valorise la date de d&eacute;but de la route.
   * 
   * @return la date de d&eacute;but de la route.
   */
  void setStartTime(Date date);

  /**
   * Valorise la date de d&eacute;but de la route et la dur&eacute;e.
   * 
   * @return la date de d&eacute;but de la route et la dur&eacute;e.
   */
  void update(Date startTime, long totalTime);

  /**
   * Restitue la liste des points.
   * 
   * @return la liste des points.
   */
  List<IGeoPositionWithAlt> getAllPoints();

  /**
   * Restitue le nombre de segment.
   * 
   * @return la liste des points.
   */
  int getSegmentSize();

  /**
   * Restitue le segment &agrave; l'index sp&eacute;cifi&eacute;.
   * 
   * @param index
   *          l'index du segment.
   * @return les points d'un segment.
   */
  IGeoSegment getSegment(int index);

  /**
   * Restitue les segments.
   * 
   * @return les segments.
   */
  List<IGeoSegment> getSegments();

  /**
   * Restitue la distance en metre.
   * 
   * @param index
   *          l'index du segment.
   * @return les points d'un segment.
   */
  double distanceTot();

  /**
   * Restitue le temps ecoule pour cette course en milli secondes.
   * 
   * @return le temps ecoule pour cette course.
   */
  long totalTime();

  /**
   * Valorise le temps ecoule pour cette course en milli secondes.
   * 
   * @return le temps ecoule pour cette course.
   */
  void setTotalTime(long totalTime);

  /**
   * D&eacute;termine si tous les points ont une date correcte.
   * 
   * @return <code>true</code> si tous les points ont une date correcte.
   */
  boolean hasPointsDate();

}