package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

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

/**
 * @author Denis Apparicio
 * 
 */

public class D1002Step extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1002Step.class);
  }

  /** Nom de l'etape. */
  private String              customName;

  /** Zone de FC basse. */
  private float               targetCustomZoneLow;

  /** Zone de FC haute. */
  private float               targetCustomZoneHigh;

  /** Duree. */
  private int                 durationValue;

  /** Intensite. */
  private int                 intensity;

  /** Type de duree. */
  private int                 durationType;

  /** Type de duree. */
  private int                 targetType;

  /** Type de duree. */
  private int                 targetValue;

  /** Type de duree. */
  private int                 unused;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    customName = input.readString(16);
    targetCustomZoneLow = input.readFloat();
    targetCustomZoneHigh = input.readFloat();
    durationValue = input.readShort();
    intensity = input.read();
    durationType = input.read();
    targetType = input.read();
    targetValue = input.read();
    input.readShort();

    log.debug("<<decode");
  }

  /**
   * @return the customName
   */
  public String getCustomName() {
    return customName;
  }

  /**
   * @param customName
   *          the customName to set
   */
  public void setCustomName(String customName) {
    this.customName = customName;
  }

  /**
   * @return the durationType
   */
  public int getDurationType() {
    return durationType;
  }

  /**
   * @param durationType
   *          the durationType to set
   */
  public void setDurationType(int durationType) {
    this.durationType = durationType;
  }

  /**
   * @return the durationValue
   */
  public int getDurationValue() {
    return durationValue;
  }

  /**
   * @param durationValue
   *          the durationValue to set
   */
  public void setDurationValue(int durationValue) {
    this.durationValue = durationValue;
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
   * @return the targetCustomZoneHigh
   */
  public float getTargetCustomZoneHigh() {
    return targetCustomZoneHigh;
  }

  /**
   * @param targetCustomZoneHigh
   *          the targetCustomZoneHigh to set
   */
  public void setTargetCustomZoneHigh(float targetCustomZoneHigh) {
    this.targetCustomZoneHigh = targetCustomZoneHigh;
  }

  /**
   * @return the targetCustomZoneLow
   */
  public float getTargetCustomZoneLow() {
    return targetCustomZoneLow;
  }

  /**
   * @param targetCustomZoneLow
   *          the targetCustomZoneLow to set
   */
  public void setTargetCustomZoneLow(float targetCustomZoneLow) {
    this.targetCustomZoneLow = targetCustomZoneLow;
  }

  /**
   * @return the targetType
   */
  public int getTargetType() {
    return targetType;
  }

  /**
   * @param targetType
   *          the targetType to set
   */
  public void setTargetType(int targetType) {
    this.targetType = targetType;
  }

  /**
   * @return the targetValue
   */
  public int getTargetValue() {
    return targetValue;
  }

  /**
   * @param targetValue
   *          the targetValue to set
   */
  public void setTargetValue(int targetValue) {
    this.targetValue = targetValue;
  }

  /**
   * @return the unused
   */
  public int getUnused() {
    return unused;
  }

  /**
   * @param unused
   *          the unused to set
   */
  public void setUnused(int unused) {
    this.unused = unused;
  }

}
