package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// struct
// {
// uint32 time; /* Time result of quick workout */
// float32 distance; /* Distance result of quick workout */
// } quick_workout;

/**
 * @author Denis Apparicio
 * 
 */
public class D1009QuickWorkout extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1009QuickWorkout.class);
  }

  /** Temps ecoule. */
  private int                 time;

  /** Distance parcouru. */
  private float               distance;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    time = input.readInt();
    distance = input.readFloat();

    log.debug("<<parse");
  }

  /**
   * @return the distance
   */
  public float getDistance() {
    return distance;
  }

  /**
   * @param distance
   *          the distance to set
   */
  public void setDistance(float distance) {
    this.distance = distance;
  }

  /**
   * @return the time
   */
  public int getTime() {
    return time;
  }

  /**
   * @param time
   *          the time to set
   */
  public void setTime(int time) {
    this.time = time;
  }

}
