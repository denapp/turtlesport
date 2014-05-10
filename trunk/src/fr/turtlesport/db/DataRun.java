package fr.turtlesport.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.swing.ImageIcon;

import fr.turtlesport.lang.CommonLang;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;
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

  private String              location;

  private double              distanceTot = -1;

  private int                 timePause   = -1;

  private int                 timeTot     = -1;

  private int                 maxRate     = -1;

  private int                 minRate     = -1;

  private int                 avgRate     = -1;

  private int[]               alt;

  private int[]               altOrignal;

  private int                 calories    = -1;

  private String              productId;

  private String              productVersion;

  private String              productName;

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

  /**
   * @return the sportType
   */
  public String getLibelleSportType() {
    if (isSportBike()) {
      return CommonLang.INSTANCE.getString("sportBike");
    }
    if (isSportRunning()) {
      return CommonLang.INSTANCE.getString("sportRunning");
    }
    if (isSportRealOther()) {
      return CommonLang.INSTANCE.getString("sportOther");
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
   * @return l'icone du sport.
   */
  public ImageIcon getSportTypeIcon() {
    try {
      if (isSportRunning()) {
        return ImagesActivityRepository.ICON_SMALL_RUN;
      }
      if (isSportBike()) {
        return ImagesActivityRepository.ICON_SMALL_BIKE;
      }
      if (isSportRealOther()) {
        return ImagesActivityRepository.ICON_SMALL_OTHER;
      }
      String iconName = UserActivityTableManager.getInstance().retreiveIcon(getSportType());
      if (iconName == null) {
        return ImagesActivityRepository.ICON_SMALL_OTHER;
      }
      
      ImageIcon icon = ImagesActivityRepository.getImageIconSmall(iconName);
      if (icon == null) {
        return ImagesActivityRepository.ICON_SMALL_OTHER;
      }
      return icon;
    }
    catch(Throwable e) {
      log.error("", e);
      return ImagesActivityRepository.getImageIconSmallTransparent();
    }
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

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getProductVersion() {
    return productVersion;
  }

  public void setProductVersion(String productVersion) {
    this.productVersion = productVersion;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
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
    if (!DistanceUnit.isDefaultUnitKm()) {
      this.distanceTot = DistanceUnit.convert(DistanceUnit.unitKm(),
                                              DistanceUnit.getDefaultUnit(),
                                              distanceTot);
    }
  }

  /**
   * Restitue le temps total avec les pauses.
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
   * Restitue le temps total de pause.
   * 
   * @return le temps total pause.
   * @throws SQLException
   */
  public int computeTimePauseTot() throws SQLException {
    if (timePause == -1) {
      timePause = 0;

      computeTimePauseTotByLaps();
      if (timePause <= 0) {
        computeTimePauseTotByPoints();
      }
    }
    return timePause;
  }

  private void computeTimePauseTotByLaps() throws SQLException {
    DataRunLap[] laps = RunLapTableManager.getInstance().findLaps(id);
    if (laps != null) {
      for (DataRunLap l : laps) {
        if (l.getMovingTotalTime() > 0) {
          timePause += l.getMovingTotalTime();
        }
      }
      if (timePause > 0) {
        timePause -= computeTimeTot();
        timePause = Math.abs(timePause);
      }
    }
  }

  private void computeTimePauseTotByPoints() throws SQLException {
    List<DataRunTrk> list = RunTrkTableManager.getInstance().getAllTrks(id);
    if (list.size() < 2) {
      return;
    }

    int iPauseBegin = -1;
    int iPauseEnd = -1;
    // 2 points consecutifs = pause
    int size = list.size() - 1;
    for (int i = 0; i < size; i++) {
      if (list.get(i).isPause() && list.get(i + 1).isPause()) {
        iPauseBegin = i;
        iPauseEnd = ++i;
        // debut de pause recherche fin pause
        for (; i < size; i++) {
          if (!list.get(i).isPause()) {
            iPauseEnd = i - 1;
            break;
          }
        }
        timePause += (list.get(iPauseEnd).getTime().getTime() - list
            .get(iPauseBegin).getTime().getTime()) / 10;
        iPauseBegin = -1;
        iPauseEnd = -1;
      }
    }
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
    return computeAlt()[0];
  }

  /**
   * Calcul d&eacute;nivel&eacute; positif dans lissage.
   * 
   * @return d&eacute;nivel&eacute; positif.
   * @throws SQLException
   */
  public int computeAltPlusOriginal() throws SQLException {
    return computeAltOriginal()[0];
  }

  /**
   * @return Calcul des altitudes.
   * @throws SQLException
   */
  public int[] computeAlt() throws SQLException {
    if (alt == null) {
      alt = RunTrkTableManager.getInstance().altitude(id);
    }
    return alt;
  }

  /**
   * Calcul d&eacute;nivel&eacute; n&eacute;gatif.
   * 
   * @return d&eacute;nivel&eacute; n&eacute;gatif.
   * @throws SQLException
   */
  public int computeAltMoins() throws SQLException {
    return computeAlt()[1];
  }

  /**
   * @return Calcul des altitudes sans lissage.
   * @throws SQLException
   */
  public int[] computeAltOriginal() throws SQLException {
    if (altOrignal == null) {
      altOrignal = RunTrkTableManager.getInstance().altitudeOriginal(id);
    }
    return altOrignal;
  }
  
  /**
   * Calcul d&eacute;nivel&eacute; n&eacute;gatif sans lissage.
   * 
   * @return d&eacute;nivel&eacute; n&eacute;gatif.
   * @throws SQLException
   */
  public int computeAltMoinsOriginal() throws SQLException {
    return computeAltOriginal()[1];
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

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof DataRun) {
      return ((DataRun) obj).id == id;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id;
  }

}
