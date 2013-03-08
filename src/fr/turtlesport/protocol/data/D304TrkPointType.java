package fr.turtlesport.protocol.data;

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
public class D304TrkPointType extends AbstractTrkPointType {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D304TrkPointType.class);
  }

  protected static final String PROTOCOL         = "D304";

  /**
   * 
   */
  public D304TrkPointType() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.
   * UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    setPosn(input.readPositionType());
    setTime(input.readTime());
    setAlt(input.readFloat());
    setDistance(input.readFloat());
    setHeartRate(input.read());
    setCadence(input.read());
    setSensor(input.readBoolean());

    log.debug("<<parse");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.
   * UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
    log.debug(">>serialize");

    output.writePositionType(getPosn());
    output.writeTime(getTime());
    output.writeFloat(getAltitude());
    output.writeFloat(getDistance());
    output.write(getHeartRate());
    output.write(getCadence());
    output.writeBoolean(isSensor());

    log.debug("<<serialize");
  }

  /* (non-Javadoc)
   * @see fr.turtlesport.protocol.data.AbstractTrkPointType#getProtocolName()
   */
  @Override
  public String getProtocolName() {
    return PROTOCOL;
  }

}
