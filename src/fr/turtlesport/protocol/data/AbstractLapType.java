package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.GarminDevice;
import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// uint16 index; /* Unique among all laps received from device */
// uint16 unused; /* Unused. Set to 0. */
// time_type start_time; /* Start of lap time */
// uint32 total_time; /* Duration of lap, in hundredths of a second */
// float32 total_dist; /* Distance in meters */
// float32 max_speed; /* In meters per second */
// position_type begin; /* Invalid if both lat and lon are 0x7FFFFFFF */
// position_type end; /* Invalid if both lat and lon are 0x7FFFFFFF */
// uint16 calories; /* Calories burned this lap */
// uint8 avg_heart_rate; /* In beats-per-minute, 0 if invalid */
// uint8 max_heart_rate; /* In beats-per-minute, 0 if invalid */
// uint8 intensity; /* Same as D1001 */
// uint8 avg_cadence; /* In revolutions-per-minute, 0xFF if invalid */
// uint8 trigger_method; /* See below */
// } D1011_Lap_Type;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractLapType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractLapType.class);
  }

  /** index. */
  private int                 index;

  /** Temps du depart. */
  private Date                startTime;

  /** Duree. */
  private int                 totalTime;

  /** Distance totale en metre. */
  private float               totalDist;

  /** Vitesse max en m/s. */
  private float               maxSpeed;

  /** Position deb. */
  private PositionType        begin;

  /** Position fin. */
  private PositionType        end;

  /** Calories. */
  private short               calories;

  /** FC moyenne beat/mn. */
  private int                 avgHeartRate;

  /** FCMax beat/mn. */
  private int                 maxHeartRate;

  /** Intensite */
  private int                 intensity;

  /** Cadence. */
  private int                 avgCadence;

  /** Trigger */
  private int                 triggerMethod;

  /** Protocole A906. */
  private static final String PROTOCOL = "A906";

  /**
   * @return
   */
  public static AbstractLapType newInstance() {
    log.debug(">>newInstance");

    AbstractLapType res;
    String[] data = GarminDevice.getDevice().getDataProtocol(PROTOCOL);
    if (data.length != 1) {
      throw new RuntimeException("pas de protocole " + PROTOCOL);
    }
    log.debug(PROTOCOL + "-->" + data[0]);

    if (D1001LapType.PROTOCOL.equals(data[0])) {
      res = new D1001LapType();
    }
    else if (D1015LapType.PROTOCOL.equals(data[0])) {
      res = new D1015LapType();
    }
    else if (D1011LapType.PROTOCOL.equals(data[0])) {
      res = new D1011LapType();
    }
    else {
      throw new RuntimeException("protocole non supporte" + data[0]);
    }

    log.debug("<<newInstance");
    return res;
  }

  /**
   * Restitue le nom du protocole.
   * 
   * @return le nom du protocole.
   */
  public abstract String getProtocolName();

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.
   * UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    beginParse(input);

    startTime = input.readTime();
    totalTime = input.readInt();
    totalDist = input.readFloat();
    maxSpeed = input.readFloat();

    begin = input.readPositionType();
    end = input.readPositionType();

    calories = input.readShort();
    avgHeartRate = input.read();
    maxHeartRate = input.read();
    intensity = input.read();

    nextParse(input);

    log.debug("<<parse");
  }

  protected void beginParse(UsbPacketInputStream input) {
    index = input.readShort();
    input.readUnusedShort();
  }

  protected void nextParse(UsbPacketInputStream input) {
    avgCadence = input.read();
    triggerMethod = input.read();
  }

  /**
   * @return the avgCadence
   */
  public int getAvgCadence() {
    return avgCadence;
  }

  /**
   * @param avgCadence
   *          the avgCadence to set
   */
  public void setAvgCadence(int avgCadence) {
    this.avgCadence = avgCadence;
  }

  /**
   * @return the avgHeartRate
   */
  public int getAvgHeartRate() {
    return avgHeartRate;
  }

  /**
   * @param avgHeartRate
   *          the avgHeartRate to set
   */
  public void setAvgHeartRate(int avgHeartRate) {
    this.avgHeartRate = avgHeartRate;
  }

  /**
   * @return the begin
   */
  public PositionType getBegin() {
    return begin;
  }

  /**
   * @param begin
   *          the begin to set
   */
  public void setBegin(PositionType begin) {
    this.begin = begin;
  }

  /**
   * @return the calories
   */
  public short getCalories() {
    return calories;
  }

  /**
   * @param calories
   *          the calories to set
   */
  public void setCalories(short calories) {
    this.calories = calories;
  }

  /**
   * @return the end
   */
  public PositionType getEnd() {
    return end;
  }

  /**
   * @param end
   *          the end to set
   */
  public void setEnd(PositionType end) {
    this.end = end;
  }

  /**
   * @return the intensity
   */
  public int getIntensity() {
    return intensity;
  }

  /**
   * @param intensity
   *          the intensity to set
   */
  public void setIntensity(int intensity) {
    this.intensity = intensity;
  }

  /**
   * @return the maxHeartRate
   */
  public int getMaxHeartRate() {
    return maxHeartRate;
  }

  /**
   * @param maxHeartRate
   *          the maxHeartRate to set
   */
  public void setMaxHeartRate(int maxHeartRate) {
    this.maxHeartRate = maxHeartRate;
  }

  /**
   * @return the maxSpeed
   */
  public float getMaxSpeed() {
    return maxSpeed;
  }

  /**
   * @param maxSpeed
   *          the maxSpeed to set
   */
  public void setMaxSpeed(float maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  /**
   * @return the startTime
   */
  public Date getStartTime() {
    return startTime;
  }

  /**
   * @param startTime
   *          the startTime to set
   */
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  /**
   * @return the totalDist
   */
  public float getTotalDist() {
    return totalDist;
  }

  /**
   * @param totalDist
   *          the totalDist to set
   */
  public void setTotalDist(float totalDist) {
    this.totalDist = totalDist;
  }

  /**
   * @return the totalTime
   */
  public int getTotalTime() {
    return totalTime;
  }

  /**
   * @param totalTime
   *          the totalTime to set
   */
  public void setTotalTime(int totalTime) {
    this.totalTime = totalTime;
  }

  /**
   * @return the triggerMethod
   */
  public int getTriggerMethod() {
    return triggerMethod;
  }

  /**
   * @param triggerMethod
   *          the triggerMethod to set
   */
  public void setTriggerMethod(int triggerMethod) {
    this.triggerMethod = triggerMethod;
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  /**
   * @param index
   *          the index to set
   */
  public void setIndex(int index) {
    this.index = index;
  }

}
