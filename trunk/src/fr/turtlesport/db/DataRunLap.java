package fr.turtlesport.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Denis Apparicio
 * 
 */
public class DataRunLap {

  private int       id;

  private int       lapIndex;

  private Timestamp startTime;

  private int       totalTime;

  private int       timePause       = -1;

  private float     totalDist;

  private float     maxSpeed;

  private int       calories;

  private int       avgHeartRate;

  private int       maxHeartRate;

  private int[]     alt;

  private int[]     altOriginal;

  private int       totalLap;

  private int       realTotalTime;

  private int       movingTotalTime = 0;

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

  public int getTotalLap() {
    return totalLap;
  }

  public void setTotalLap(int totalLap) {
    this.totalLap = totalLap;
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
   * @deprecated temps du tcx invalide avec les pauses.
   */
  public int getTotalTime() {
    return totalTime;
  }

  /**
   * @param realTotalTime
   *          the totalTime to set
   * @deprecated temps du tcx invalide avec les pauses.
   */
  public void setTotalTime(int totalTime) {
    this.totalTime = totalTime;
  }

  /**
   * @return the totalTime
   */
  public int getRealTotalTime() {
    return realTotalTime;
  }

  /**
   * @param totalTime
   *          the totalTime to set
   */
  public void setRealTotalTime(int realTotalTime) {
    this.realTotalTime = realTotalTime;
  }

  /**
   * @return the totalTime
   * @throws SQLException
   */
  public int getMovingTotalTime() throws SQLException {
    if (movingTotalTime <= 0) {
      movingTotalTime = realTotalTime - computeTimePauseTot();
    }
    return movingTotalTime;
  }

  /**
   * @param movingTotalTime
   */
  public void setMovingTotalTime(int movingTotalTime) {
    this.movingTotalTime = movingTotalTime;
  }

  /**
   * Restitue le temps total de pause.
   * 
   * @return le temps total pause.
   * @throws SQLException
   */
  public int computeTimePauseTot() throws SQLException {
    if (timePause == -1) {
      timePause = 0;

      if (lapIndex < (totalLap - 1)) {

      }
      Date dateEnd = new Date(getStartTime().getTime() + getRealTotalTime()
                              * 10);
      DataRunTrk[] trks = RunTrkTableManager.getInstance()
          .getTrks(id, getStartTime(), dateEnd);

      if (trks.length < 2) {
        return timePause;
      }

      int iPauseBegin = -1;
      int iPauseEnd = -1;
      // 2 points consecutifs = pause
      int size = trks.length - 1;
      for (int i = 0; i < size; i++) {
        if (trks[i].isPause() && trks[i + 1].isPause()) {
          iPauseBegin = i;
          iPauseEnd = ++i;
          // debut de pause recherche fin pause
          for (; i < size; i++) {
            if (!trks[i].isPause()) {
              iPauseEnd = i - 1;
              break;
            }
          }
          timePause += (trks[iPauseEnd].getTime().getTime() - trks[iPauseBegin]
              .getTime().getTime()) / 10;
          iPauseBegin = -1;
          iPauseEnd = -1;
        }
      }

    }
    return timePause;
  }

  /**
   * Restitue le denivel&eacute; positif.
   * 
   * @return le denivel&eacute; positif.
   * @throws SQLException
   */
  public int computeDenivelePos() throws SQLException {
    return computeDenivele()[0];
  }

  /**
   * Restitue le denivel&eacute; positif.
   * 
   * @return le denivel&eacute; positif.
   * @throws SQLException
   */
  public int computeDeniveleNeg() throws SQLException {
    return computeDenivele()[1];
  }

  /**
   * Restitue le denivel&eacute; positif sans correction.
   * 
   * @return le denivel&eacute; positif sans correction.
   * @throws SQLException
   */
  public int computeDenivelePosOriginal() throws SQLException {
    return computeDeniveleOriginal()[0];
  }

  /**
   * Restitue le denivel&eacute; positif sans correction.
   * 
   * @return le denivel&eacute; positif sans correction.
   * @throws SQLException
   */
  public int computeDeniveleNegOriginal() throws SQLException {
    return computeDeniveleOriginal()[1];
  }

  
  private int[] computeDenivele() throws SQLException {
    if (alt == null) {
      alt = RunLapTableManager.getInstance().altitude(id, lapIndex);
    }
    return alt;
  }

  private int[] computeDeniveleOriginal() throws SQLException {
    if (altOriginal == null) {
      altOriginal = RunLapTableManager.getInstance().altitudeOriginal(id, lapIndex);
    }
    return altOriginal;
  }

}
