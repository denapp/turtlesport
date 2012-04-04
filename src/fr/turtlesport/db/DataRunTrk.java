package fr.turtlesport.db;

import java.sql.Timestamp;

/**
 * @author Denis Apparicio
 * 
 */
public class DataRunTrk {

  private static final int INVALID        = 0x7FFFFFFF;

  private int              id;

  private int              latitude       = INVALID;

  private int              longitude      = INVALID;

  private Timestamp        time;

  private float            altitude;

  private float            distance;

  private int              heartRate      = 0;

  private int              cadence        = 0xff;

  /** Vitesse en km/h */
  private double           speed          = 0;

  /** Allure en mn/km */
  private double           pace           = 0;

  private double           MAX_PACE_VALUE = 20;

  /**
   * 
   */
  public DataRunTrk() {
    super();
  }

  /**
   * @return the altitude
   */
  public float getAltitude() {
    return altitude;
  }

  /**
   * D&eacute;termine si coordonn&eacute;es GPS sont valides.
   * 
   * @return <code>true</code> si si coordonn&eacute;es GPS sont valides,
   *         <code>false</code> sinon.
   */
  public boolean isValidGps() {
    return (getLongitude() != INVALID) && (getLatitude() != INVALID);
  }

  /**
   * @param altitude
   *          the altitude to set
   */
  public void setAltitude(float altitude) {
    this.altitude = altitude;
  }

  /**
   * @return the cadence
   */
  public int getCadence() {
    return cadence;
  }

  /**
   * @param cadence
   *          the cadence to set
   */
  public void setCadence(int cadence) {
    this.cadence = cadence;
  }

  /**
   * @return the distance
   */
  public float getDistance() {
    return distance;
  }

  /**
   * @param distance
   *          the distance to set
   */
  public void setDistance(float distance) {
    this.distance = distance;
  }

  /**
   * @return the heartRate
   */
  public int getHeartRate() {
    return heartRate;
  }

  /**
   * @param heartRate
   *          the heartRate to set
   */
  public void setHeartRate(int heartRate) {
    this.heartRate = heartRate;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * @return the latitude
   */
  public int getLatitude() {
    return latitude;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(int latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the longitude
   */
  public int getLongitude() {
    return longitude;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(int longitude) {
    this.longitude = longitude;
  }

  /**
   * @return the time
   */
  public Timestamp getTime() {
    return time;
  }

  /**
   * @param time
   *          the time to set
   */
  public void setTime(Timestamp time) {
    this.time = time;
  }

  /**
   * D&eacute;termine si la cadence est valide.
   * 
   * @return <code>true</code> si la cadence est valide, <code>false</code>
   *         sinon
   */
  public boolean isValidCadence() {
    return (cadence > 0 && cadence < 0xff);
  }

  /**
   * D&eacute;termine si l'altitude est valide.
   * 
   * @return <code>true</code> si l'altitude est valide, <code>false</code>
   *         sinon
   */
  public boolean isValidAltitude() {
    return (altitude != 1.0e25f);
  }

  /**
   * Restitue la vitesse en km/h.
   * 
   * @return la vitesse en km/h.
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * Valorise la vitesse en km/h.
   * 
   * @param speed
   *          la nouvelle valeur
   */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * Restitue l'allure en mn/km.
   * 
   * @return l'allure en mn/km
   */
  public double getPace() {
    return pace;
  }

  /**
   * 
   * Valorise l'allure en mn/km.
   * 
   * @param pace
   *          la nouvelle valeur
   */
  public void setPace(double pace) {
    this.pace = (pace > MAX_PACE_VALUE) ? MAX_PACE_VALUE : pace;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "[" + latitude + ", " + longitude + ", " + altitude + ", "
           + distance + ", " + time + "]";
  }

}
