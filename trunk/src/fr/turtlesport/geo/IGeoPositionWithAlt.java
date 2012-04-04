/*
 * GeoPosition.java
 *
 * Created on March 31, 2006, 9:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.turtlesport.geo;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public interface IGeoPositionWithAlt extends IGeoPosition {

  /**
   * Restitue la fr&eacute;quence cardiaque pour ce point.
   * 
   * @return la fr&eacute;quence cardiaque pour ce point.
   */
  int getHeartRate();

  /**
   * Restitue la distance pour ce point.
   * 
   * @return la distance pour ce point.
   */
  double getDistanceMeters();

  /**
   * Restitue la cadence pour ce point.
   * 
   * @return la cadence pour ce point.
   */
  int getCadence();

  /**
   * D&eacute;termine si ce point est avec un cardio.
   * 
   * @return <code>true</code> si ce point est avec un cardio.
   */
  boolean hasSensor();

  /**
   * Restitue l'altitude.
   * 
   * @return l'altitude.
   */
  double getElevation();

  /**
   * D&eacute;termine si l'altitude est valide.
   * 
   * @return <code>true</code> si l'altitude est valide.
   */
  boolean isValidElevation();

  /**
   * D&eacute;termine si cette distance est valide.
   * 
   * @return <code>true</code> si cette distance est valide.
   */
  boolean isValidDistance();

  /**
   * D&eacute;termine si la cadence est valide.
   * 
   * @return <code>true</code> si la cadence est valide.
   */
  boolean isValidCadence();

}