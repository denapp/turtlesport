package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;

// typedef struct
// {
// uint32 index; /* Unique among all laps received from device */
// time_type start_time; /* Start of lap time */
// uint32 total_time; /* Duration of lap, in hundredths of a second */
// float32 total_dist; /* Distance in meters */
// float32 max_speed; /* In meters per second */
// position_type begin; /* Invalid if both lat and lon are 0x7FFFFFFF */
// position_type end; /* Invalid if both lat and lon are 0x7FFFFFFF */
// uint16 calories; /* Calories burned this lap */
// uint8 avg_heart_rate; /* In beats-per-minute, 0 if invalid */
// uint8 max_heart_rate; /* In beats-per-minute, 0 if invalid */
// uint8 intensity; /* See below */
// } D1001_Lap_Type;
/**
 * @author Denis Apparicio
 * 
 */
public class D1001LapType extends AbstractLapType {

  protected static final String PROTOCOL = "D1001";

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractLapType#getProtocolName()
   */
  @Override
  public String getProtocolName() {
    return PROTOCOL;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.data.AbstractLapType#beginParse(fr.turtlesport.
   * UsbPacketInputStream)
   */
  @Override
  protected void beginParse(UsbPacketInputStream input) {
    setIndex(input.readInt());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractLapType#nextParse(fr.turtlesport.
   * UsbPacketInputStream)
   */
  @Override
  protected void nextParse(UsbPacketInputStream input) {
  }

}
