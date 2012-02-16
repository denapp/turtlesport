package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// uint32 max_workouts; /* Maximum workouts */
// uint32 max_unscheduled_workouts; /* Maximum unscheduled workouts */
// uint32 max_
// } D1005_Workout_Limits;

/**
 * @author Denis Apparicio
 * 
 */
public class D1005WorkoutLimits extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1005WorkoutLimits.class);
  }

  /** Maximum workouts. */
  private short               maxWorkouts;

  /** Maximum unscheduled workouts. */
  private short               maxUnscheduledWorkouts;

  /** Maximum workouts occurences. */
  private short               maxOccurences;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    maxWorkouts = input.readShort();
    maxUnscheduledWorkouts = input.readShort();
    maxOccurences = input.readShort();

    log.debug("<<decode");
  }

  /**
   * Restitue Maximum workouts.
   * 
   * @return Maximum workouts.
   */
  public short getMaxWorkouts() {
    return maxWorkouts;
  }

  /**
   * Valorise le jour de naissance.
   * 
   * @param maxWorkouts
   *          la nouvelle valeur.
   */
  public void setMaxWorkouts(short maxWorkouts) {
    this.maxWorkouts = maxWorkouts;
  }

  /**
   * Restitue Maximum unscheduled workouts.
   * 
   * @return Maximum unscheduled workouts.
   */
  public short getMaxUnscheduledWorkouts() {
    return maxUnscheduledWorkouts;
  }

  /**
   * Valorise Maximum unscheduled workouts.
   * 
   * @param maxUnscheduledWorkouts
   *          Maximum unscheduled workouts.
   */
  public void setMaxUnscheduledWorkouts(short maxUnscheduledWorkouts) {
    this.maxUnscheduledWorkouts = maxUnscheduledWorkouts;
  }

  /**
   * Restitue Maximum workouts occurences.
   * 
   * @return Maximum workouts occurences.
   */
  public short getMaxOccurences() {
    return maxOccurences;
  }

  /**
   * Valorise Maximum workouts occurences.
   * 
   * @param maxOccurences
   *          Maximum workouts occurences.
   */
  public void setMaxOccurences(short maxOccurences) {
    this.maxOccurences = maxOccurences;
  }

}
