package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

// typedef struct
// {
// uint16 course_index; /* Index of associated course */
// uint16 lap_index; /* This lap's index in the course */
// uint32 total_time; /* In hundredths of a second */
// float32 total_dist; /* In meters */
// position_type begin; /* Starting position of the lap */
// position_type end; /* Final position of the lap */
// uint8 avg_heart_rate; /* In beats-per-minute */
// uint8 max_heart_rate; /* In beats-per-minute */
// uint8 intensity; /* Same as D1001 */
// uint8 avg_cadence; /* In revolutions-per-minute */
// } D1007_Course_Lap_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1007CourseLapType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1007CourseLapType.class);
  }

  /** Designe un tour actif. */
  private static final int    INTENSITY_ACTIVE = 0;

  /** Designe le reste d'un tour */
  private static final int    INTENSITY_REST   = 1;

  /** Index de la course. */
  private int                 courseIndex;

  /** Lap index. */
  private int                 lapIndex;

  /** Temps total. */
  private int                 totalTime;

  /** Distance. */
  private float               totalDist;

  /** Position du debut. */
  private PositionType        begin;

  /** Position finale. */
  private PositionType        end;

  /** Moyenne battement cardiaque par minute. */
  private int                 avgHeartRate;

  /** Battement cardiaque max par minute. */
  private int                 maxHeartRate;

  /** Intensite. */
  private int                 intensity;

  /** Cadance par minute. */
  private int                 avgCadence;

  /**
   * 
   */
  public D1007CourseLapType() {
    super();
    avgHeartRate = 0;
    maxHeartRate = 0;
    avgCadence = 0xFF;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    courseIndex = input.readShort();
    lapIndex = input.readShort();
    totalTime = input.readInt();
    totalDist = input.readFloat();

    begin = input.readPositionType();
    end = input.readPositionType();

    avgHeartRate = input.read();
    maxHeartRate = input.read();
    intensity = input.read();
    avgCadence = input.read();

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

    output.writeShort(courseIndex);
    output.writeShort(lapIndex);
    output.writeInt(totalTime);
    output.writeFloat(totalDist);

    output.writePositionType(begin);
    output.writePositionType(end);

    output.write(avgHeartRate);
    output.write(maxHeartRate);
    output.write(intensity);
    output.write(avgCadence);

    log.debug("<<serialize");
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
   * @return the courseIndex
   */
  public int getCourseIndex() {
    return courseIndex;
  }

  /**
   * @param courseIndex
   *          the courseIndex to set
   */
  public void setCourseIndex(int courseIndex) {
    this.courseIndex = courseIndex;
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
   * @return the lapIndex
   */
  public int getLapIndex() {
    return lapIndex;
  }

  /**
   * @param lapIndex
   *          the lapIndex to set
   */
  public void setLapIndex(int lapIndex) {
    this.lapIndex = lapIndex;
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
   * D&eacute;termine si intensit&eacute; d'un tour.
   * 
   * @return <code>true</code> si active.
   */
  public boolean isIntensityActive() {
    return (intensity == INTENSITY_ACTIVE);
  }

  /**
   * D&eacute;termine si intensit&eacute; reste d'un tour.
   * 
   * @return <code>true</code> si active.
   */
  public boolean isIntensityRest() {
    return (intensity == INTENSITY_REST);
  }

}
