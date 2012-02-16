package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

// struct
// {
// uint8 low_heart_rate; /* In beats-per-minute, must be > 0 */
// uint8 high_heart_rate; /* In beats-per-minute, must be > 0 */
// uint16 unused; /* Unused. Set to 0. */
// } heart_rate_zones[5];

/**
 * @author Denis Apparicio
 * 
 */
public class D1004RateZone extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1004RateZone.class);
  }

  /** FC au repos. */
  private int                 lowHeartRate;

  /** FCMax */
  private int                 highHeartRate;

  /**
   * 
   */
  public D1004RateZone() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    lowHeartRate = input.read();
    highHeartRate = input.read();

    // unused
    input.readShort();

    log.debug("<<parse");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
    log.debug(">>serialize");

    output.write(lowHeartRate);
    output.write(highHeartRate);
    output.writeUnusedShort();

    log.debug("<<serialize");

  }

  /**
   * Restitue la FCMax.
   * 
   * @return la FCMax.
   */
  public int getHighHeartRate() {
    return highHeartRate;
  }

  /**
   * Valorise la FCMax.
   * 
   * @param highHeartRate
   *          la nouvelle valeur.
   */
  public void setHighHeartRate(int highHeartRate) {
    this.highHeartRate = highHeartRate;
  }

  /**
   * Valorise la FC au repos.
   * 
   * @return la FC au repos.
   */
  public int getLowHeartRate() {
    return lowHeartRate;
  }

  /**
   * Valorise la FC au repos.
   * 
   * @param lowHeartRate
   *          la nouvelle valeur.
   */
  public void setLowHeartRate(int lowHeartRate) {
    this.lowHeartRate = lowHeartRate;
  }

}
