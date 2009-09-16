/*
 * GeoPosition.java
 *
 * Created on March 31, 2006, 9:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.turtlesport.geo;

import java.util.Date;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class GeoPosition implements IGeoPosition {
  private double latitude;

  private double longitude;

  private Date   date;

  /**
   * Construit une instance de GeoPosition en sp&eacute;cifiant la latitude, la
   * longitude (en degr&eacute;) et la date.
   * 
   * @param latitude
   *          la latitude en degr&eacute;
   * @param longitude
   *          la longitude en degr&eacute;
   */
  public GeoPosition(double latitude, double longitude) {
    this(latitude, longitude, null);
  }

  /**
   * Construit une instance de GeoPosition en sp&eacute;cifiant la latitude, la
   * longitude (en degr&eacute;) et la date.
   * 
   * @param latitude
   *          la latitude en degr&eacute;
   * @param longitude
   *          la longitude en degr&eacute;
   * @param date
   *          la date du point.
   */
  public GeoPosition(double latitude, double longitude, Date date) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.date = date;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPosition#iPositionInvalid()
   */
  public boolean isInvalidPosition() {
    return (latitude == INVALID_POS && longitude == INVALID_POS);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPosition#getLatitude()
   */
  public double getLatitude() {
    return latitude;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPosition#getLongitude()
   */
  public double getLongitude() {
    return longitude;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPosition#getDate()
   */
  public Date getDate() {
    return date;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPosition#setDate(java.util.Date)
   */
  public void setDate(Date date) {
    if (date == null) {
      throw new IllegalArgumentException();
    }
    this.date = date;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GeoPosition) {
      GeoPosition coord = (GeoPosition) obj;
      return coord.latitude == latitude && coord.longitude == longitude;
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return ((Double) latitude).hashCode() + ((Double) longitude).hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + latitude + ", " + longitude + ", " + date + "]";
  }

}