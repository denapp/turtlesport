package fr.turtlesport.geo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class GeoPositionWithAlt implements IGeoPositionWithAlt {

  private static final SimpleDateFormat DF        = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");

  private boolean                       hasSensor = false;

  private double                        latitude;

  private double                        longitude;

  private double                        elevation;

  private Date                          date;

  private int                           heartRate = 0;

  private int                           cadence   = 0xFF;

  private double                        distanceMeters;

  /**
   * Construit une instance de GeoPosition.
   */
  public GeoPositionWithAlt() {
    super();
    latitude = INVALID_POS;
    longitude = INVALID_POS;
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
    this.latitude = latitude;
    this.longitude = longitude;
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
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
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
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + latitude + ", " + longitude + ", " + elevation + ", "
           + DF.format(date) + "]";
  }

}