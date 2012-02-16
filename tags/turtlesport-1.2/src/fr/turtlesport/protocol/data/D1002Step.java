package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// struct
// {
// char custom_name[16]; /* Null-terminated step name */
// float32 target_custom_zone_low; /* See below */
// float32 target_custom_zone_high; /* See below */
// uint16 duration_value; /* See below */
// uint8 intensity; /* Same as D1001 */
// uint8 duration_type; /* See below */
// uint8 target_type; /* See below */
// uint8 target_value; /* See below */
// uint16 unused; /* Unused. Set to 0. */
// } steps[20];
/**
 * @author Denis Apparicio
 * 
 */

public class D1002Step extends AbstractStep {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1002Step.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.
   * UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    setCustomName(input.readString());
    setTargetCustomZoneLow(input.readFloat());
    setTargetCustomZoneHigh(input.readFloat());
    setDurationValue(input.readShort());
    setIntensity(input.read());
    setDurationType(input.read());
    setTargetType(input.read());
    setTargetValue(input.read());
    input.readShort();

    log.debug("<<decode");
  }

}
