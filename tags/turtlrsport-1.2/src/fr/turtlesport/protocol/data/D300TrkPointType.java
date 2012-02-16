package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// position_type posn; /* position */
// time_type time; /* time */
// bool
// } D300_Trk_Point_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D300TrkPointType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D300TrkPointType.class);
  }

  /** position. */
  private PositionType        posn;

  /** Date. */
  private Date                time;

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
    isNewTrack = input.readBoolean();

    log.debug("<<decode");
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
