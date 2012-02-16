package fr.turtlesport.db;

import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * @author Denis Apparicio
 * 
 */
public class DataRunLap {

  private int       id;

  private int       lapIndex;

  private Timestamp startTime;

  private int       totalTime;

  private float     totalDist;

  private float     maxSpeed;

  private int       calories;

  private int       avgHeartRate;

  private int       maxHeartRate;

  private int       denivelePos = -1;

  private int       deniveleNeg = 1;

  /**
   * 
   */
  public DataRunLap() {
    super();
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
   * @return the calories
   */
  public int getCalories() {
    return calories;
  }

  /**
   * @param calories
   *          the calories to set
   */
  public void setCalories(int calories) {
    this.calories = calories;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(int id) {
    this.id = id;
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
  public Timestamp getStartTime() {
    return startTime;
  }

  /**
   * @param startTime
   *          the startTime to set
   */
  public void setStartTime(Timestamp startTime) {
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
   * Restitue le denivel&eacute; positif.
   * 
   * @return le denivel&eacute; positif.
   * @throws SQLException
   */
  public int computeDenivelePos() throws SQLException {
    computeDenivele();
    return denivelePos;
  }

  /**
   * Restitue le denivel&eacute; positif.
   * 
   * @return le denivel&eacute; positif.
   * @throws SQLException
   */
  public int computeDeniveleNeg() throws SQLException {
    computeDenivele();
    return deniveleNeg;
  }

  private void computeDenivele() throws SQLException {
    if (denivelePos == -1) {
      int[] res = RunLapTableManager.getInstance().altitude(id, lapIndex);
      denivelePos = res[0];
      deniveleNeg = res[1];
    }
  }

}
