package fr.turtlesport.protocol.data;

// typedef struct
// {
// uint16 index; /* Unique among all laps received from device */
// uint16 unused; /* Unused. Set to 0. */
// time_type start_time; /* Start of lap time */
// uint32 total_time; /* Duration of lap, in hundredths of a second */
// float32 total_dist; /* Distance in meters */
// float32 max_speed; /* In meters per second */
// position_type begin; /* Invalid if both lat and lon are 0x7FFFFFFF */
// position_type end; /* Invalid if both lat and lon are 0x7FFFFFFF */
// uint16 calories; /* Calories burned this lap */
// uint8 avg_heart_rate; /* In beats-per-minute, 0 if invalid */
// uint8 max_heart_rate; /* In beats-per-minute, 0 if invalid */
// uint8 intensity; /* Same as D1001 */
// uint8 avg_cadence; /* In revolutions-per-minute, 0xFF if invalid */
// uint8 trigger_method; /* See below */
// } D1011_Lap_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1011LapType extends AbstractLapType {

  protected static final String PROTOCOL = "D1011";

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractLapType#getProtocolName()
   */
  @Override
  public String getProtocolName() {
    return PROTOCOL;
  }

}
