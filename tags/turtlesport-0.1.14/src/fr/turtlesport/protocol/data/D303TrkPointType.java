package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// position_type posn; /* position */
// time_type time; /* time */
// float32 alt; /* altitude in meters */
// uint8 heart_rate; /* heart rate in beats per minute */
// } D303_Trk_Point_Type;
/**
 * @author Denis Apparicio
 * 
 */
public class D303TrkPointType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D303TrkPointType.class);
  }

  /** Position. */
  private PositionType        posn;

  /** Date. */
  private Date                time;

  /** Altitude en metres. */
  private float               alt;

  /** FC beats/mn. */
  private int                 heartRate;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    posn = input.readPositionType();
    time = input.readTime();
    alt = input.readFloat();
    heartRate = input.read();

    log.debug("<<decode");
  }

  /**
   * @return the alt
   */
  public float getAlt() {
    return alt;
  }

  /**
   * @param alt
   *          the alt to set
   */
  public void setAlt(float alt) {
    this.alt = alt;
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

}
