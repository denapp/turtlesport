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
// uint16 duration_value; /* Same as D1002 */
// uint8 intensity; /* Same as D1001 */
// uint8 duration_type; /* Same as D1002 */
// uint8 target_type; /* See below */
// uint8 target_value; /* See below */
// uint16 unused; /* Unused. Set to 0. */
// } steps[20];
// char name[16]; /* Null-terminated workout name */
// uint8
// } D1008_Workout_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1008WorkoutType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1008WorkoutType.class);
  }

  /** Nombre max de lap valide */
  private static final int    MAX_VALID_STEP = 20;

  /** Nombre etape valide (1-20). */
  private int                 numValidSteps;

  /** Etapes. */
  private D1008Step[]         steps;

  /** Nom de l'etape. */
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
    log.debug(">>parse");

    numValidSteps = input.readInt();

    steps = new D1008Step[numValidSteps];
    int i = 0;
    for (i = 0; i < numValidSteps; i++) {
      steps[i] = new D1008Step();
      steps[i].parse(input);
    }
    for (; i < MAX_VALID_STEP; i++) {
      D1008Step.readUnsed(input);
    }

    name = input.readString(16);
    sportType = input.read();

    log.debug("<<parse");
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
   * @return the steps
   */
  public D1008Step[] getSteps() {
    return steps;
  }

  /**
   * @param steps
   *          the steps to set
   */
  public void setSteps(D1008Step[] steps) {
    this.steps = steps;
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
   * D&eacute;termine si le sport est de la course &agrve; pied.
   * 
   * @return <code>true</code> si course &agrve; pied.
   */
  public boolean isSportRunning() {
    return isSportRunning(sportType);
  }

  /**
   * D&eacute;termine si le sport est du v&eacute,lo.
   * 
   * @return <code>true</code> si le sport est du v&eacute,lo.
   */
  public boolean isSportBike() {
    return isSportBike(sportType);
  }

  /**
   * D&eacute;termine si le sport n'est pas v&eacute,lo ou de la la course
   * &agrve; pied.
   * 
   * @return <code>true</code> si n'est pas v&eacute,lo ou de la la course
   *         &agrve; pied.
   */
  public boolean isSportOther() {
    return isSportOther(sportType);
  }
}
