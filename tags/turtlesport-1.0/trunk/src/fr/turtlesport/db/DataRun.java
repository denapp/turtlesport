package fr.turtlesport.db;

import java.sql.SQLException;
import java.sql.Timestamp;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.unit.DistanceUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class DataRun {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(DataRun.class);
  }

  private String              unit;

  private int                 id;

  private int                 sportType;

  private int                 programType;

  private int                 multisport;

  private Timestamp           time;

  private String              comments;

  private String              equipement;

  private double              distanceTot = -1;

  private int                 timeTot     = -1;

  private int                 maxRate     = -1;

  private int                 minRate     = -1;

  private int                 avgRate     = -1;

  private int[]               alt;

  private int                 calories    = -1;

  /**
   * 
   */
  public DataRun() {
    super();
    unit = DistanceUnit.getDefaultUnit();
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
  public synchronized void setUnit(String unit) {
    if (distanceTot != -1 && !this.unit.equals(unit)) {
      distanceTot = DistanceUnit.convert(this.unit, unit, distanceTot);
    }
    this.unit = unit;
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
   * @return the multisport
   */
  public int getMultisport() {
    return multisport;
  }

  /**
   * @param multisport
   *          the multisport to set
   */
  public void setMultisport(int multisport) {
    this.multisport = multisport;
  }

  /**
   * @return the programType
   */
  public int getProgramType() {
    return programType;
  }

  /**
   * @param programType
   *          the programType to set
   */
  public void setProgramType(int programType) {
    this.programType = programType;
  }

  /**
   * @return the sportType
   */
  public int getSportType() {
    return sportType;
  }

  /**
   * @return the sportType
   */
  public String getLibelleSportType() {
    if (isSportBike()) {
      return DataLanguageRessource.getInstance().getString("sportBike");
    }
    if (isSportRunning()) {
      return DataLanguageRessource.getInstance().getString("sportRunning");
    }
    if (isSportRealOther()) {
      return DataLanguageRessource.getInstance().getString("sportOther");
    }
    try {
      return UserActivityTableManager.getInstance().retreiveName(sportType);
    }
    catch (SQLException e) {
      log.error("", e);
    }
    return null;
  }

  /**
   * @return
   */
  public boolean isSportRunning() {
    return (sportType == 0);
  }

  /**
   * @return
   */
  public boolean isSportBike() {
    return (sportType == 1);
  }

  /**
   * @return
   */
  public boolean isSportOther() {
    return (!isSportRunning() && !isSportBike());
  }

  /**
   * @return
   */
  public boolean isSportRealOther() {
    return (sportType == 2);
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
   * @throws SQLException
   */
  public double getComputeDistanceTot() throws SQLException {
    if (distanceTot == -1) {
      distanceTot = RunLapTableManager.getInstance().distanceTot(id);
      unit = DistanceUnit.getDefaultUnit();
      if (!DistanceUnit.isDefaultUnitKm()) {
        distanceTot = DistanceUnit.convert(DistanceUnit.unitKm(),
                                           unit,
                                           distanceTot);
      }
    }
    return distanceTot;
  }

  /**
   * Valorise la distance totale.
   * 
   * @param distanceTot
   *          la nouvelle distance.
   */
  public void setComputeDistanceTot(double distanceTot) {
    this.distanceTot = distanceTot;
  }

  /**
   * Restitue le temps total.
   * 
   * @return le temps total.
   * @throws SQLException
   */
  public int computeTimeTot() throws SQLException {
    if (timeTot == -1) {
      timeTot = RunLapTableManager.getInstance().timeTot(id);
    }
    return timeTot;
  }

  /**
   * Calcul de la fr&eacute;quence cardiaque moyenne.
   * 
   * @return la la fr&eacute;quence cardiaque moyenne.
   * @throws SQLException
   */
  public int computeAvgRate() throws SQLException {
    if (avgRate == -1) {
      avgRate = RunLapTableManager.getInstance().heartAvg(id);
    }
    return avgRate;
  }

  /**
   * Calcul de la fr&eacute;quence cardiaque minimale.
   * 
   * @return la la fr&eacute;quence cardiaque minimale.
   * @throws SQLException
   */
  public int computeMinRate() throws SQLException {
    if (minRate == -1) {
      minRate = RunTrkTableManager.getInstance().heartMin(id);
    }
    return minRate;
  }

  /**
   * Calcul de la fr&eacute;quence cardiaque maximale.
   * 
   * @return la la fr&eacute;quence cardiaque maximale.
   * @throws SQLException
   */
  public int computeMaxRate() throws SQLException {
    if (maxRate == -1) {
      maxRate = RunLapTableManager.getInstance().heartMax(id);
    }
    return maxRate;
  }

  /**
   * Calcul d&eacute;nivel&eacute; positif.
   * 
   * @return d&eacute;nivel&eacute; positif.
   * @throws SQLException
   */
  public int computeAltPlus() throws SQLException {
    if (alt == null) {
      alt = RunTrkTableManager.getInstance().altitude(id);
    }
    return alt[0];
  }

  /**
   * Calcul d&eacute;nivel&eacute; n&eacute;gatif.
   * 
   * @return d&eacute;nivel&eacute; n&eacute;gatif.
   * @throws SQLException
   */
  public int computeAltMoins() throws SQLException {
    if (alt == null) {
      alt = RunTrkTableManager.getInstance().altitude(id);
    }
    return alt[1];
  }

  /**
   * Calcul des calories.
   * 
   * @return les calories.
   * @throws SQLException
   */
  public int computeCalories() throws SQLException {
    if (calories == -1) {
      calories = RunLapTableManager.getInstance().computeCalories(id);
    }
    return calories;
  }
}
