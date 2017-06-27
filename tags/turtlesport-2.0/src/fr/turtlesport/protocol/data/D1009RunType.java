package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// uint16 track_index; /* Index of associated track */
// uint16 first_lap_index; /* Index of first associated lap */
// uint16 last_lap_index; /* Index of last associated lap */
// uint8 sport_type; /* Same as D1000 */
// uint8 program_type; /* See below */
// uint8 multisport; /* See below */
// uint8 unused1; /* Unused. Set to 0. */
// uint16 unused2; /* Unused. Set to 0. */
// struct
// {
// uint32 time; /* Time result of quick workout */
// float32 distance; /* Distance result of quick workout */
// } quick_workout;
// D1008_Workout_Type workout; /* Workout */
// } D1009_Run_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1009RunType extends AbstractRunType {
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1009RunType.class);
  }

  protected static final String       PROTOCOL                        = "D1009";

 
  /** Quick Workout. */
  private D1009QuickWorkout           quickWorkout;

  /** Workout. */
  private D1008WorkoutType            workout;

  /**
   * 
   */
  public D1009RunType() {
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

    setTrackIndex(input.readShort());
    setFirstLapIndex(input.readShort());
    setLastLapIndex(input.readShort());
    setSportType(input.read());
    setProgramType(input.read());
    setMultisport(input.read());
    
    input.readUnused();
    input.readUnusedShort();

    quickWorkout = new D1009QuickWorkout();
    quickWorkout.parse(input);

    workout = new D1008WorkoutType();
    workout.parse(input);

    log.debug("<<parse");
  }

  
  /**
   * @return the quickWorkout
   */
  public D1009QuickWorkout getQuickWorkout() {
    return quickWorkout;
  }

  /**
   * @param quickWorkout
   *          the quickWorkout to set
   */
  public void setQuickWorkout(D1009QuickWorkout quickWorkout) {
    this.quickWorkout = quickWorkout;
  }

  /**
   * @return the workout
   */
  public D1008WorkoutType getWorkout() {
    return workout;
  }

  /**
   * @param workout
   *          the workout to set
   */
  public void setWorkout(D1008WorkoutType workout) {
    this.workout = workout;
  }
  
}
