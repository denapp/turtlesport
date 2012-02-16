package fr.turtlesport.protocol.data;

public abstract class AbstractStep extends AbstractData {

  /** Nom de l'etape. */
  private String customName;

  /** Zone de FC basse. */
  private float  targetCustomZoneLow;

  /** Zone de FC haute. */
  private float  targetCustomZoneHigh;

  /** Duree. */
  private int    durationValue;

  /** Intensite. */
  private int    intensity;

  /** Type de duree. */
  private int    durationType;

  /** Type de duree. */
  private int    targetType;

  /** Type de duree. */
  private int    targetValue;

  public int getIntensity() {
    return intensity;
  }

  public void setIntensity(int intensity) {
    this.intensity = intensity;
  }

  public int getDurationType() {
    return durationType;
  }

  public void setDurationType(int durationType) {
    this.durationType = durationType;
  }

  public int getTargetType() {
    return targetType;
  }

  public void setTargetType(int targetType) {
    this.targetType = targetType;
  }

  public int getTargetValue() {
    return targetValue;
  }

  public void setTargetValue(int targetValue) {
    this.targetValue = targetValue;
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

}
