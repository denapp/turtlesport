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
public class D1010RunType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1010RunType.class);
  }

  // Constantes programme type
  private static final int    PROGRAM_TYPE_NONE            = 0;

  private static final int    PROGRAM_TYPE_VIRTUAL_PARTNER = 1;

  private static final int    PROGRAM_TYPE_WORKOUT         = 2;

  private static final int    PROGRAM_TYPE_AUTO_MULTISPORT = 3;

  /** Index de la track. */
  private int                 trackIndex;

  /** Index du premier intermediaire. */
  private int                 firstLapIndex;

  /** Index du dernier intermediaire. */
  private int                 lastLapIndex;

  /** Type de sport. */
  private int                 sportType;

  /** Type de programme. */
  private int                 programType;

  /** Multisport. */
  private int                 multisport;

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

    trackIndex = input.readInt();
    firstLapIndex = input.readInt();
    lastLapIndex = input.readInt();
    sportType = input.read();
    programType = input.read();
    multisport = input.read();
    input.read();

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
    return (programType == PROGRAM_TYPE_NONE);
  }

  /**
   * @return
   */
  public boolean isPogramTypeVirtualPartner() {
    return (programType == PROGRAM_TYPE_VIRTUAL_PARTNER);
  }

  /**
   * @return
   */
  public boolean isPogramTypeWorkout() {
    return (programType == PROGRAM_TYPE_WORKOUT);
  }

  /**
   * @return
   */
  public boolean isPogramTypeAutoMultisport() {
    return (programType == PROGRAM_TYPE_AUTO_MULTISPORT);
  }

  /**
   * @return the firstLapIndex
   */
  public int getFirstLapIndex() {
    return firstLapIndex;
  }

  /**
   * @param firstLapIndex
   *          the firstLapIndex to set
   */
  public void setFirstLapIndex(int firstLapIndex) {
    this.firstLapIndex = firstLapIndex;
  }

  /**
   * @return the lastLapIndex
   */
  public int getLastLapIndex() {
    return lastLapIndex;
  }

  /**
   * @param lastLapIndex
   *          the lastLapIndex to set
   */
  public void setLastLapIndex(int lastLapIndex) {
    this.lastLapIndex = lastLapIndex;
  }

  /**
   * @return the multisport
   */
  public int getMultisport() {
    return multisport;
  }

  /**
   * @param multisport
   *          the multisport to set
   */
  public void setMultisport(int multisport) {
    this.multisport = multisport;
  }

  /**
   * @return the programType
   */
  public int getProgramType() {
    return programType;
  }

  /**
   * @param programType
   *          the programType to set
   */
  public void setProgramType(int programType) {
    this.programType = programType;
  }

  /**
   * @return the sportType
   */
  public int getSportType() {
    return sportType;
  }

  /**
   * @param sportType
   *          the sportType to set
   */
  public void setSportType(int sportType) {
    this.sportType = sportType;
  }

  /**
   * @return the trackIndex
   */
  public int getTrackIndex() {
    return trackIndex;
  }

  /**
   * @param trackIndex
   *          the trackIndex to set
   */
  public void setTrackIndex(int trackIndex) {
    this.trackIndex = trackIndex;
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
