package fr.turtlesport.geo.gpx;

import fr.turtlesport.geo.GeoPositionWithAlt;

/**
 * @author Denis Apparicio
 * 
 */
public class Wpt extends GeoPositionWithAlt {
  private String name;

  private String desc;

  /**
   * @param latitude
   * @param longitude
   * @param elevation
   */
  public Wpt(double latitude, double longitude, double elevation) {
    super(latitude, longitude, elevation);
  }

  /**
   * @param latitude
   * @param longitude
   */
  public Wpt(double latitude, double longitude) {
    super(latitude, longitude);
  }

  /**
   * Restitue le nom du point.
   * 
   * @return le nom du point.
   */
  public String getName() {
    return name;
  }

  /**
   * Valorise le nom du point.
   * 
   * @param name
   *          la nouvelle valeur.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Restitue la description du point.
   * 
   * @return la description du point.
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Valorise la description du point.
   * 
   * @param desc
   *          la nouvelle valeur.
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

}
