package fr.turtlesport.geo;

import java.util.Date;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoPosition {
  double INVALID_POS = Double.NaN;

  /**
   * D&eacute;termine si cette position est invalide.
   * 
   * @return <code>true</code> si cette postion est invalide.
   */
  boolean isInvalidPosition();

  /**
   * Restitue la latitude.
   * 
   * @return la latitude.
   */
  double getLatitude();

  /**
   * Restitue la longitude.
   * 
   * @return la longitude.
   */
  double getLongitude();

  /**
   * Valorise la latitude.
   * 
   * @param la
   *          latitude.
   */
  void setLatitude(double latitude);

  /**
   * Valorise la longitude.
   * 
   * @param la
   *          longitude.
   */
  void setLongitude(double longitude);

  /**
   * Restitue la date de ce point.
   * 
   * @return la date de ce point.
   */
  Date getDate();

  /**
   * Valorise la date de ce point.
   * 
   * @return la date de ce point.
   */
  void setDate(Date date);

  /**
   * Restitue la position sosu forme textuelle.
   * 
   * @return la position sous forme textuelle.
   */
  String geoPosition();
}