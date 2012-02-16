package fr.turtlesport.geo.garmin;

/**
 * @author Denis Apparicio
 * 
 */
public class Position {
  private double latitude;

  private double longitude;

  /**
   * 
   */
  public Position() {
    super();
  }

  /**
   * @param latitude
   * @param logitude
   */
  public Position(double latitude, double longitude) {
    super();
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the logitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * @param logitude
   *          the logitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "(" + latitude + "," + longitude + ")";
  }
}
