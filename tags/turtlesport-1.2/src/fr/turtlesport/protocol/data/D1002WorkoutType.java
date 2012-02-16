package fr.turtlesport.protocol.data;

import java.util.ArrayList;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// uint32 num_valid_steps; /* Number of valid steps (1-20) */
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
// char name[16]; /* Null-terminated workout name */
// uint8 sport_type; /* Same as D1000 */
// } D1002_Workout_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1002WorkoutType extends AbstractWorkout {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1002WorkoutType.class);
  }

  protected static final String PROTOCOL = "D1002";

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.
   * UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    int numValidSteps = input.readInt();

    listSteps = new ArrayList<AbstractStep>();
    int i = 0;
    for (i = 0; i < numValidSteps; i++) {
      D1002Step d1002 = new D1002Step();
      listSteps.add(d1002);
      d1002.parse(input);
    }
    for (; i < MAX_VALID_STEP; i++) {
      D1002Step d1002 = new D1002Step();
      d1002.parse(input);
    }

    setName(input.readString(16));
    setSportType(input.read());

    log.debug("<<parse");
  }

}
