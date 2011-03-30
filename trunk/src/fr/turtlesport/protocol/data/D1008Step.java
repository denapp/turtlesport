package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// struct
// {
// char custom_name[16]; /* Null-terminated step name */
// float32 target_custom_zone_low; /* See below */
// float32 target_custom_zone_high; /* See below */
// uint16 duration_value; /* Same as D1002 */
// uint8 intensity; /* Same as D1001 */
// uint8 duration_type; /* Same as D1002 */
// uint8 target_type; /* See below */
// uint8 target_value; /* See below */
// uint16 unused; /* Unused. Set to 0. */
// } steps[20];

/**
 * @author Denis Apparicio
 * 
 */
public class D1008Step extends AbstractStep {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1008Step.class);
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

    setCustomName(input.readString(16));
    setTargetCustomZoneLow(input.readFloat());
    setTargetCustomZoneHigh(input.readFloat());
    setDurationValue(input.readShort());
    setIntensity(input.read());
    setDurationType(input.read());
    setTargetType(input.read());
    setTargetValue(input.read());
    input.readShort();

    log.debug("<<parse");
  }

  protected void parseUnused(UsbPacketInputStream input) {
    input.readString(16);
    input.readFloat();
    input.readFloat();
    input.readShort();
    input.read();
    input.read();
    input.read();
    input.read();
    input.readShort();
  }

}
