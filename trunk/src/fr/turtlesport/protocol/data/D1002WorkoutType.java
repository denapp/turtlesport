package fr.turtlesport.protocol.data;

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
public class D1002WorkoutType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1002WorkoutType.class);
  }

  /** Nombre d'etapes valide. */
  private int                 numValidSteps;

  /** Etapes. */
  private D1002Step[]         steps;

  /** Nom. */
  private String              name;

  /** Type de sport. */
  private int                 sportType;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    numValidSteps = input.readInt();

    steps = new D1002Step[numValidSteps];
    for (int i = 0; i < numValidSteps; i++) {
      steps[i] = new D1002Step();
      steps[i].parse(input);
    }

    name = input.readString(16);
    sportType = input.read();

    log.debug("<<decode");
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the numValidSteps
   */
  public int getNumValidSteps() {
    return numValidSteps;
  }

  /**
   * @param numValidSteps
   *          the numValidSteps to set
   */
  public void setNumValidSteps(int numValidSteps) {
    this.numValidSteps = numValidSteps;
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
   * @return the steps
   */
  public D1002Step[] getSteps() {
    return steps;
  }

  /**
   * @param steps
   *          the steps to set
   */
  public void setSteps(D1002Step[] steps) {
    this.steps = steps;
  }

}
