package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// uint32 track_index; /* Index of associated track */
// uint32 first_lap_index; /* Index of first associated lap */
// uint32 last_lap_index; /* Index of last associated lap */
// uint8 sport_type; /* Sport type (same as D1000) */
// uint8 program_type; /* See below */
// uint8 multisport; /* Same as D1009 */
// uint8 unused; /* Unused. Set to 0. */
// struct
// {
// uint32 time; /* Time result of virtual partner */
// float32 distance; /* Distance result of virtual partner */
// } virtual_partner;
// D1002_Workout_Type workout; /* Workout */
// } D1010_Run_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1010RunType extends AbstractRunType {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1010RunType.class);
  }
  protected static final String PROTOCOL = "D1010";

  // Constantes programme type
  private static final int    PROGRAM_TYPE_NONE            = 0;

  private static final int    PROGRAM_TYPE_VIRTUAL_PARTNER = 1;

  private static final int    PROGRAM_TYPE_WORKOUT         = 2;

  private static final int    PROGRAM_TYPE_AUTO_MULTISPORT = 3;

  /** Partenaire virtuel. */
  private D1010VirtualPartner virtualPartner;

  /** Workout. */
  private D1002WorkoutType    workout;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    setTrackIndex(input.readInt());
    setFirstLapIndex(input.readInt());
    setLastLapIndex(input.readInt());
    setSportType(input.read());
    setProgramType(input.read());
    setMultisport(input.read());
    
    input.readUnused();

    virtualPartner = new D1010VirtualPartner();
    virtualPartner.parse(input);

    workout = new D1002WorkoutType();
    workout.parse(input);

    log.debug("<<decode");
  }

  /**
   * @return
   */
  public boolean isPogramTypeNone() {
    return (getProgramType() == PROGRAM_TYPE_NONE);
  }

  /**
   * @return
   */
  public boolean isPogramTypeVirtualPartner() {
    return (getProgramType() == PROGRAM_TYPE_VIRTUAL_PARTNER);
  }

  /**
   * @return
   */
  public boolean isPogramTypeWorkout() {
    return (getProgramType() == PROGRAM_TYPE_WORKOUT);
  }

  /**
   * @return
   */
  public boolean isPogramTypeAutoMultisport() {
    return (getProgramType() == PROGRAM_TYPE_AUTO_MULTISPORT);
  }

  /**
   * @return the virtualPartner
   */
  public D1010VirtualPartner getVirtualPartner() {
    return virtualPartner;
  }

  /**
   * @param virtualPartner
   *          the virtualPartner to set
   */
  public void setVirtualPartner(D1010VirtualPartner virtualPartner) {
    this.virtualPartner = virtualPartner;
  }

  /**
   * @return the workout
   */
  public D1002WorkoutType getWorkout() {
    return workout;
  }

  /**
   * @param workout
   *          the workout to set
   */
  public void setWorkout(D1002WorkoutType workout) {
    this.workout = workout;
  }

}
