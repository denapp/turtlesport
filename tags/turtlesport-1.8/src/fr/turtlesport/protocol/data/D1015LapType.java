package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class D1015LapType extends AbstractLapType {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1015LapType.class);
  }

  protected static final String PROTOCOL = "D1015";

  private int                   workout;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractLapType#getProtocolName()
   */
  @Override
  public String getProtocolName() {
    return PROTOCOL;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractLapType#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    super.parse(input);

    input.readUnused();
    workout = input.readInt();
    input.readUnusedShort();

    log.debug("<<parse");
  }

  /**
   * @return the workout
   */
  public int getWorkout() {
    return workout;
  }

  /**
   * @param workout
   *          the workout to set
   */
  public void setWorkout(int workout) {
    this.workout = workout;
  }

}
