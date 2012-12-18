package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataSearchRun {

  private String location;

  private String equipment;

  private double distanceMin = -1;

  private double distanceMax = -1;

  public DataSearchRun() {
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

  public double getDistanceMin() {
    return distanceMin;
  }

  public void setDistanceMin(double distanceMin) {
    this.distanceMin = distanceMin;
  }

  public double getDistanceMax() {
    return distanceMax;
  }

  public void setDistanceMax(double distanceMax) {
    this.distanceMax = distanceMax;
  }

}
