package fr.turtlesport.geo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class GeoPositionWithAlt extends GeoPosition implements
                                                   IGeoPositionWithAlt {

  private static final SimpleDateFormat DF          = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");

  private boolean                       hasSensor   = false;

  private double                        elevation;

  private Date                          date;

  private int                           heartRate   = 0;

  private int                           cadence     = 0xFF;

  private double                        distanceMeters;

  private float                         speed       = -1;

  private int                           temperature = 0xFF;

  /**
   * Construit une instance de GeoPosition.
   * 
   * @param latitude
   *          la latitude en degr&eacute;
   * @param longitude
   *          la longitude en degr&eacute;
   */
  public GeoPositionWithAlt() {
    super(INVALID_POS, INVALID_POS);
    elevation = Double.NaN;
    distanceMeters = Double.NaN;
  }

  /**
   * Construit une instance de GeoPosition en sp&eacute;cifiant la latitude et
   * la longitude (en degr&eacute;).
   * 
   * @param latitude
   *          la latitude en degr&eacute;
   * @param longitude
   *          la longitude en degr&eacute;
   * @param elevation
   *          l'altitude en metre;
   */
  public GeoPositionWithAlt(double latitude, double longitude, double elevation) {
    super(latitude, longitude);
    this.elevation = elevation;
    distanceMeters = Double.NaN;
  }

  /**
   * Construit une instance de GeoPosition en sp&eacute;cifiant la latitude et
   * la longitude (en degr&eacute;).
   * 
   * @param latitude
   *          la latitude en degr&eacute;
   * @param longitude
   *          la longitude en degr&eacute;
   * @param elevation
   *          l'altitude en metre;
   */
  public GeoPositionWithAlt(double latitude, double longitude) {
    this(latitude, longitude, Double.NaN);
  }

  public boolean isValidHeartRate() {
    return (heartRate >= 10 && heartRate < 230);
  }

  public boolean isInvalidSpeed() {
    return speed <= -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#getTemperature()
   */
  public int getTemperature() {
    return temperature;
  }

  /**
   * @param temperature
   */
  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#getSpeed()
   */
  @Override
  public float getSpeed() {
    return speed;
  }

  /**
   * @param speed
   */
  public void setSpeed(float speed) {
    this.speed = speed;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#getElevation()
   */
  public double getElevation() {
    return elevation;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPosition#getDate()
   */
  public Date getDate() {
    return date;
  }

  /**
   * @param elevation
   *          the elevation to set
   */
  public void setElevation(double elevation) {
    this.elevation = elevation;
  }

  /**
   * Valorise la date de ce point.
   * 
   * @param time
   *          la date de ce point.
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Valorise la pr&eacute;sence d'un cardio.
   * 
   * @param hasSensor
   *          la pr&eacute;sence d'un cardio.
   */
  public void setSensor(boolean hasSensor) {
    this.hasSensor = hasSensor;
  }

  /**
   * Valorise la fr&eacute;quence cardiaque.
   * 
   * @param heartRate
   *          la fr&eacute;quence cardiaque.
   */
  public void setHeartRate(int heartRate) {
    this.heartRate = heartRate;
  }

  /**
   * Valorise la cadence.
   * 
   * @param cadence
   *          la nouvelle valeur.
   */
  public void setCadence(int cadence) {
    this.cadence = cadence;
  }

  /**
   * Valorise la distance.
   * 
   * @param distanceMeters
   *          la distance.
   */
  public void setDistanceMeters(double distanceMeters) {
    this.distanceMeters = distanceMeters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#getCadence()
   */
  public int getCadence() {
    return cadence;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#getDistanceMeters()
   */
  public double getDistanceMeters() {
    return distanceMeters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#getHeartRate()
   */
  public int getHeartRate() {
    return heartRate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#hasSensor()
   */
  public boolean hasSensor() {
    return hasSensor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#isValidElevation()
   */
  public boolean isValidElevation() {
    return !Double.isNaN(elevation);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#isValidDistance()
   */
  public boolean isValidDistance() {
    return !Double.isNaN(distanceMeters);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoPositionWithAlt#isValidCadence()
   */
  public boolean isValidCadence() {
    return cadence >= 0 && cadence < 0xFF;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (date != null) {
      return "[" + getLatitude() + ", " + getLongitude() + ", " + elevation
             + ", " + DF.format(date) + "]";
    }
    return "[" + getLatitude() + ", " + getLongitude() + ", " + elevation;
  }

}