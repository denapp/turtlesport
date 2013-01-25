package fr.turtlesport.db;

import java.util.Date;

import fr.turtlesport.unit.DistanceUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class DataSearchRun {

  private String unitDistance = DistanceUnit.getDefaultUnit();

  private int    tempMin      = Integer.MIN_VALUE;

  private int    tempMax      = Integer.MAX_VALUE;

  private int    sportType    = -1;

  private String location;

  private String equipment;

  private String comments;

  private int    distanceMin  = -1;

  private int    distanceMax  = -1;

  private Date   dateMin;

  private Date   dateMax;

  private int    condition    = -1;

  private long   durationMin  = -1;

  private long   durationMax  = -1;

  public DataSearchRun() {
  }

  public boolean isEmpty() {
    return !hasDataMeteo() && (location == null || "".equals(location))
           && (equipment == null || "".equals(equipment))
           && (comments == null || "".equals(comments))
           && !isDistanceMaxValid() && !isDistanceMinValid() && dateMin == null
           && dateMax == null && sportType == -1 && durationMin <= 0
           && durationMax <= 0;
  }

  public String getUnitDistance() {
    return unitDistance;
  }

  public void setUnitDistance(String unitDistance) {
    this.unitDistance = unitDistance;
  }

  public int getCondition() {
    return condition;
  }

  public void setCondition(int condition) {
    this.condition = condition;
  }

  public boolean hasDataMeteo() {
    return (isTempMinValid() || isTempMaxValid() || isConditionValid());
  }

  public boolean isConditionValid() {
    return condition != -1;
  }

  public boolean isTempMinValid() {
    return tempMin != Integer.MIN_VALUE;
  }

  public boolean isTempMaxValid() {
    return tempMax != Integer.MAX_VALUE;
  }

  public boolean isDistanceMaxValid() {
    return distanceMax != -1;
  }

  public boolean isDistanceMinValid() {
    return distanceMin != -1;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getEquipment() {
    return equipment;
  }

  public void setEquipment(String equipment) {
    this.equipment = equipment;
  }

  public int getDistanceMin() {
    return distanceMin;
  }

  public void setDistanceMin(int distanceMin) {
    this.distanceMin = distanceMin;
  }

  public int getDistanceMax() {
    return distanceMax;
  }

  public int getDistanceMaxKm() {
    if (isDistanceMaxValid() && !DistanceUnit.isUnitKm(getUnitDistance())) {
      return (int) DistanceUnit.convert(getUnitDistance(),
                                        DistanceUnit.unitKm(),
                                        getDistanceMax());
    }
    return distanceMax;
  }

  public int getDistanceMinKm() {
    if (isDistanceMinValid() && !DistanceUnit.isUnitKm(getUnitDistance())) {
      return (int) DistanceUnit.convert(getUnitDistance(),
                                        DistanceUnit.unitKm(),
                                        getDistanceMin());
    }
    return distanceMin;
  }

  public void setDistanceMax(int distanceMax) {
    this.distanceMax = distanceMax;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public int getSportType() {
    return sportType;
  }

  public void setSportType(int sportType) {
    this.sportType = sportType;
  }

  public Date getDateMin() {
    return dateMin;
  }

  public void setDateMin(Date dateMin) {
    this.dateMin = dateMin;
  }

  public Date getDateMax() {
    return dateMax;
  }

  public void setDateMax(Date dateMax) {
    this.dateMax = dateMax;
  }

  public int getTempMin() {
    return tempMin;
  }

  public void setTempMin(int tempMin) {
    this.tempMin = tempMin;
  }

  public int getTempMax() {
    return tempMax;
  }

  public void setTempMax(int tempMax) {
    this.tempMax = tempMax;
  }

  public long getDurationMin() {
    return durationMin;
  }

  public void setDurationMin(long durationMin) {
    this.durationMin = durationMin;
  }

  public long getDurationMax() {
    return durationMax;
  }

  public void setDurationMax(long durationMax) {
    this.durationMax = durationMax;
  }

}
