package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.garmin.GarminUsbDevice;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractTrkPointType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractTrkPointType.class);
  }

  /** Protocole A302. */
  private static final String PROTOCOL         = "A302";

  /** Altitude invalide. */
  public static final float   INVALID_ALT      = 1E25f;

  /** Distance invalide. */
  public static final float   INVALID_DISTANCE = 1E25f;

  /** Position. */
  private PositionType        posn;

  /** Date. */
  private Date                time;

  /** Altitude en metres. */
  private float               altitude;

  /** Distance en metres. */
  private float               distance;

  /** FC beats/mn. */
  private int                 heartRate;

  /** /mn. */
  private int                 cadence          = 0xFF;   ;

  /** Temperature. */
  private int                 temperature;

  /** Determine si present. */
  private boolean             sensor           = false;

  /**
   * @return
   */
  public static AbstractTrkPointType newInstance() {
    log.debug(">>newInstance");

    AbstractTrkPointType res;
    String[] data = GarminUsbDevice.getDevice().getDataProtocol(PROTOCOL);
    if (data.length != 2) {
      throw new RuntimeException("pas de protocole " + PROTOCOL);
    }
    log.debug(PROTOCOL + "-->" + data[1]);

    if (D303TrkPointType.PROTOCOL.equals(data[1])) {
      res = new D303TrkPointType();
    }
    else if (D304TrkPointType.PROTOCOL.equals(data[1])) {
      res = new D304TrkPointType();
    }
    else {
      throw new RuntimeException("protocole non supporte" + data[1]);
    }

    log.debug("<<newInstance");
    return res;
  }

  /**
   * Restitue le nom du protocole.
   * 
   * @return le nom du protocole.
   */
  public abstract String getProtocolName();

  /**
   * @return the alt
   */
  public float getAltitude() {
    return altitude;
  }

  /**
   * @param alt
   *          the alt to set
   */
  public void setAlt(float alt) {
    this.altitude = alt;
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
   * @return
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
   * @return the posn
   */
  public PositionType getPosn() {
    return posn;
  }

  /**
   * @param posn
   *          the posn to set
   */
  public void setPosn(PositionType posn) {
    this.posn = posn;
  }

  /**
   * @return the sensor
   */
  public boolean isSensor() {
    return sensor;
  }

  /**
   * @param sensor
   *          the sensor to set
   */
  public void setSensor(boolean sensor) {
    this.sensor = sensor;
  }

  /**
   * @return the time
   */
  public Date getTime() {
    return time;
  }

  /**
   * @param time
   *          the time to set
   */
  public void setTime(Date time) {
    this.time = time;
  }

  /**
   * D&eacute;termine si l'altitude est valide.
   * 
   * @return <code>true</code> si l'altitude est valide, <code>false</code>
   *         sinon
   */
  public boolean isValidAltitude() {
    return (altitude != INVALID_ALT);
  }

  /**
   * D&eacute;termine si l'altitude est valide.
   * 
   * @return <code>true</code> si l'altitude est valide, <code>false</code>
   *         sinon
   */
  public boolean isValidDistance() {
    return (distance != INVALID_DISTANCE);
  }

}
