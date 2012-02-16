package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// char workout_name[16]; /* Null-terminated workout name */
// time_type day; /* Day on which the workout falls */
// } D1003_Workout_Occurrence_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1003WorkoutOccurenceType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(D1003WorkoutOccurenceType.class);
  }

  /** Nom de la seance. */
  private String              workoutName;

  /** Date et heure de la seance. */
  private Date                day;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    workoutName = input.readString(16);
    day = input.readTime();

    log.debug("<<decode");
  }

  /**
   * @return the day
   */
  public Date getDay() {
    return day;
  }

  /**
   * @param day
   *          the day to set
   */
  public void setDay(Date day) {
    this.day = day;
  }

  /**
   * @return the workoutName
   */
  public String getWorkoutName() {
    return workoutName;
  }

  /**
   * @param workoutName
   *          the workoutName to set
   */
  public void setWorkoutName(String workoutName) {
    this.workoutName = workoutName;
  }

}
