package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

// typedef struct
// {
// position_type posn; /* position */
// time_type time; /* time */
// float32 alt; /* altitude in meters */
// float32 distance; /* distance traveled in meters. See below. */
// uint8 heart_rate; /* heart rate in beats per minute */
// uint8 cadence; /* in revolutions per minute */
// bool
// } D304_Trk_Point_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D304TrkPointType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D304TrkPointType.class);
  }

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
  private int                 cadence;

  /** Determine si present. */
  private boolean             sensor;

  /**
   * 
   */
  public D304TrkPointType() {
    super();
    heartRate = 0;
    cadence = 0xFF;
    sensor = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    posn = input.readPositionType();
    time = input.readTime();
    altitude = input.readFloat();
    distance = input.readFloat();
    heartRate = input.read();
    cadence = input.read();
    sensor = input.readBoolean();

    log.debug("<<parse");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
    log.debug(">>serialize");

    output.writePositionType(posn);
    output.writeTime(time);
    output.writeFloat(altitude);
    output.writeFloat(distance);
    output.write(heartRate);
    output.write(cadence);
    output.writeBoolean(sensor);

    log.debug("<<serialize");
  }

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
    return (altitude != INVALID_DISTANCE);
  }

}
