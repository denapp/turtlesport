package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// position_type posn; /* position */
// time_type time; /* time */
// float32 alt; /* altitude in meters */
// float32 dpth; /* depth in meters */
// bool new_trk; /* new track segment? */
// } D301_Trk_Point_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D301TrkPointType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D301TrkPointType.class);
  }

  /** Position. */
  private PositionType        posn;

  /** Date. */
  private Date                time;

  /** Altitude en metres. */
  private float               alt;

  /** Depth en metre. */
  private float               depth;

  /** Determine si nouvelle track */
  private boolean             isNewTrack;

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
    depth = input.readFloat();
    isNewTrack = input.readBoolean();

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
   * @return the depth
   */
  public float getDepth() {
    return depth;
  }

  /**
   * @param depth
   *          the depth to set
   */
  public void setDepth(float depth) {
    this.depth = depth;
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

  /**
   * @return the isNewTrack
   */
  public boolean isNewTrack() {
    return isNewTrack;
  }

  /**
   * @param isNewTrack
   *          the isNewTrack to set
   */
  public void setNewTrack(boolean isNewTrack) {
    this.isNewTrack = isNewTrack;
  }

}
