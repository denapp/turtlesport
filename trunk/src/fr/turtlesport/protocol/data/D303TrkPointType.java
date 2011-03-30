package fr.turtlesport.protocol.data;

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
public class D303TrkPointType extends AbstractTrkPointType {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D303TrkPointType.class);
  }

  protected static final String PROTOCOL = "D303";

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.
   * UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    setPosn(input.readPositionType());
    setTime(input.readTime());
    setAlt(input.readFloat());
    setHeartRate(input.read());

    log.debug("<<decode");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractTrkPointType#getProtocolName()
   */
  @Override
  public String getProtocolName() {
    return PROTOCOL;
  }

}
