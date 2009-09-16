package fr.turtlesport.db;

import java.util.Date;

/**
 * @author Denis Apparicio
 * 
 */
public class DataEquipement {

  private String  name;

  private Date    firstUsed;

  private Date    lastUsed;

  private boolean isAlert;

  private float   weight;

  private float   distance;

  private float   distanceMax;

  private String  path;

  private boolean isDefault;

  /**
   * Constructeur.
   */
  public DataEquipement() {
  }

  /**
   * Constructeur.
   * 
   * @param name
   *          nom de l'&eacute;quipement.
   */
  public DataEquipement(String name) {
    this.name = name;
    this.weight = (float) 0.300;
    this.distance = 0;
    this.distanceMax = 1000;
  }

  /**
   * @return the firstUsed
   */
  public Date getFirstUsed() {
    return firstUsed;
  }

  /**
   * @param firstUsed
   *          the firstUsed to set
   */
  public void setFirstUsed(Date firstUsed) {
    this.firstUsed = firstUsed;
  }

  /**
   * @return the isAlert
   */
  public boolean isAlert() {
    return isAlert;
  }

  /**
   * @param isAlert
   *          the isAlert to set
   */
  public void setAlert(boolean isAlert) {
    this.isAlert = isAlert;
  }

  /**
   * @return the lastUsed
   */
  public Date getLastUsed() {
    return lastUsed;
  }

  /**
   * @param lastUsed
   *          the lastUsed to set
   */
  public void setLastUsed(Date lastUsed) {
    this.lastUsed = lastUsed;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the weight
   */
  public float getWeight() {
    return weight;
  }

  /**
   * @param weight
   *          the weight to set
   */
  public void setWeight(float weight) {
    this.weight = weight;
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
   * @return the distanceMax
   */
  public float getDistanceMax() {
    return distanceMax;
  }

  /**
   * @param distanceMax
   *          the distanceMax to set
   */
  public void setDistanceMax(float distanceMax) {
    this.distanceMax = distanceMax;
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path
   *          the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the isDefault
   */
  public boolean isDefault() {
    return isDefault;
  }

  /**
   * @param isDefault
   *          the isDefault to set
   */
  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

}
