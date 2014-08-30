package fr.turtlesport.db;

import java.sql.Timestamp;
import java.util.Date;

import fr.turtlesport.meteo.DataMeteo;
import fr.turtlesport.unit.DistanceUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class DataRunWithoutPoints {
  private String    unit;

  private int       sportType;

  private Date      startTime;

  private Timestamp time;

  private String    comments;

  private String    equipement;

  private double    distanceTot = 0;

  private int       timeTot     = 0;

  private int       calories    = -1;

  private int       maxRate     = -1;

  private int       avgRate     = -1;

  private String    location;

  private DataMeteo meteo       = new DataMeteo(new Date());

  /** Identifiant de l'utilisateur du run. */
  private int       idUser      = -1;

  /**
   * 
   */
  public DataRunWithoutPoints() {
    super();
    unit = DistanceUnit.getDefaultUnit();
  }

  /**
   * Restitue l'id de l'utilisateur.
   * 
   * @return l'id de l'utilisateur.
   */
  public int getIdUser() {
    return idUser;
  }

  /**
   * Valorise l'id de l'utilisateur.
   * 
   * @param idUser
   *          la nouvelle valeur.
   */
  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  /**
   * Restitue l'unit&eacute; de distance.
   * 
   * @return l'unit&eacute; de distance.
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Valorise l'unit&eacute; de distance.
   * 
   * @param unit
   *          la nouvelle valeur.
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * Restitue la date du run.
   * 
   * @return
   */
  public Date getStartTime() {
    return startTime;
  }

  /**
   * Valorise la date du run.
   * 
   * @param startTime
   */
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  /**
   * @return the sportType
   */
  public int getSportType() {
    return sportType;
  }

  /**
   * @param sportType
   *          the sportType to set
   */
  public void setSportType(int sportType) {
    this.sportType = sportType;
  }

  /**
   * @return the time
   */
  public Timestamp getTime() {
    return time;
  }

  /**
   * @param time
   *          the time to set
   */
  public void setTime(Timestamp time) {
    this.time = time;
  }

  /**
   * Restitue le temps total.
   * 
   * @return le temps total.
   */
  public int getTimeTot() {
    return timeTot;
  }

  /**
   * Valorise le temps total.
   * 
   * @param @return le temps total.
   */
  public void setTimeTot(int timeTot) {
    this.timeTot = timeTot;
  }

  /**
   * @return the comments
   */
  public String getComments() {
    return comments;
  }

  /**
   * @param comments
   *          the comments to set
   */
  public void setComments(String comments) {
    this.comments = comments;
  }

  /**
   * @return the equipement
   */
  public String getEquipement() {
    return equipement;
  }

  /**
   * @param equipement
   *          the equipement to set
   */
  public void setEquipement(String equipement) {
    this.equipement = equipement;
  }

  /**
   * Restitue la distance totale.
   * 
   * @return la distance totale.
   */
  public double getDistanceTot() {
    return distanceTot;
  }

  /**
   * Valorise la distance totale.
   * 
   * @param distanceTot
   *          la nouvelle distance.
   */
  public void setDistanceTot(double distanceTot) {
    this.distanceTot = distanceTot;
  }

  /**
   * @return Le nombre de calories
   */
  public int getCalories() {
    return calories;
  }

  /**
   * Valorise le nombre de calories
   * 
   * @param calories
   *          le nombre de calories.
   */
  public void setCalories(int calories) {
    this.calories = calories;
  }

  /**
   * @return Restitue la fc max.
   */
  public int getMaxRate() {
    return maxRate;
  }

  /**
   * Valorise la fc max.
   * 
   * @param maxRate
   *          la fc max.
   */
  public void setMaxRate(int maxRate) {
    this.maxRate = maxRate;
  }

  /**
   * @return Restitue la fc moyenne.
   */
  public int getAvgRate() {
    return avgRate;
  }

  /**
   * Valorise la fc moy.
   * 
   * @param avgRate
   */
  public void setAvgRate(int avgRate) {
    this.avgRate = avgRate;
  }

  /**
   * @return
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  public DataMeteo getMeteo() {
    return meteo;
  }

  public void setMeteo(DataMeteo meteo) {
    this.meteo = meteo;
  }

}
