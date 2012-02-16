package fr.turtlesport.geo;

import java.util.Date;
import java.util.List;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoSegment {

  /**
   * Restitue l'index de ce segment.
   * 
   * @return l'index de ce segment.
   */
  int index();

  /**
   * Restitue la date de d&eacute;burt de ce segment.
   * 
   * @return la date de d&eacute;burt de ce segment.
   */
  Date getStartTime();
  
  /**
   * Valorise la date de d&eacute;burt de ce segment.
   * 
   * @param la nouvelle valeur.
   */
  void setStartTime(Date startTime);

  /**
   * Restitue la dur&eacute;e  en milli-secondes.
   * 
   * @returnle la dur&eacute;e  en milli-secondes.
   */
  long getTotalTime();
  
  /**
   * Restitue la distance de ce segment.
   * 
   * @return la distance de ce segment.
   */
  double distance();
  
  /**
   * Restitue les calorises d&eacute;pens&eacute;s.
   * 
   * @return les calorises d&eacute;pens&eacute;s.
   */
  int getCalories();

  /**
   * Restitue la vitesse maximale.
   * 
   * @return la vitesse maximale.
   */
  double getMaxSpeed();
  
  /**
   * Restitue la fr&eacute;quence cardiaque moyenne.
   * 
   * @return la fr&eacute;quence cardiaque moyenne.
   */
  int getAvgHeartRate();
  
  /**
   * Restitue la fr&eacute;quence cardiaque maximale.
   * 
   * @return la fr&eacute;quence cardiaque maximale.
   */
  int getMaxHeartRate();
  
  /**
   * Restitue les points du segment.
   * 
   * @return les points d'un segment.
   */
  List<IGeoPositionWithAlt> getPoints();
}
