package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import fr.turtlesport.ProductDeviceUtil;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogMap;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.TimeUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelDialogMap {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelDialogMap.class);
  }

  private DataRun dataRun;

  /**
   * 
   */
  public ModelDialogMap() {
    super();
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JDialogMap view) throws SQLException {
    log.debug(">>updateView");

    dataRun = ModelPointsManager.getInstance().getDataRun();

    view.getJLabelTitle().setText(title(dataRun));

    // resume
    // ----------------------------------------
    updateSummary(view);

    // mis a jour des tours intermediaires
    // -------------------------------------------------------
    // recuperation des donnees
    int size = ModelPointsManager.getInstance().runLapsSize();
    if (size > 1) {
      view.getJPanelRight().getJComboBoxLap().addItem(" ");
      // mis a jour de la vue.
      for (int i = 0; i < size; i++) {
        view.getJPanelRight().getJComboBoxLap().addItem(String.valueOf(i + 1));
      }
    }
    else {
      view.getJPanelRight().getJComboBoxLap().setEnabled(false);
    }

    log.debug("<<updateView");
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param dialogMap
   * @param selectedItem
   */
  public void updateViewLap(JDialogMap view, int selectedItem) {
    clearLap(view);

    try {
      DataRunLap lap = ModelPointsManager.getInstance().getRunLaps()[selectedItem];
      if (lap == null) {
        clearLap(view);
        return;
      }

      // Date
      view.getJPanelRight()
          .getJLabelValDayLap()
          .setText(LanguageManager.getManager().getCurrentLang()
              .getDateFormatter().format(lap.getStartTime()));

      // Heure
      SimpleDateFormat dfTime = new SimpleDateFormat("kk:mm:ss");
      view.getJPanelRight().getJLabelValHourLap()
          .setText(dfTime.format(lap.getStartTime()));

      // Distance
      view.getJPanelRight().getJLabelValDistanceLap()
          .setText(DistanceUnit.formatMetersInKm(lap.getTotalDist()));

      // Temps Total
      view.getJPanelRight().getJLabelValTimeTotLap()
          .setText(TimeUnit.formatHundredSecondeTime(lap.getRealTotalTime()));

      // Temps
      view.getJPanelRight().getjLabelValTimeMovingLap()
          .setText(TimeUnit.formatHundredSecondeTime(lap.getMovingTotalTime()));

      // Temps de pause
      view.getJPanelRight()
          .getjLabelValTimePauseLap()
          .setText(TimeUnit.formatHundredSecondeTime(lap.computeTimePauseTot()));

      // Allure moy.
      view.getJPanelRight()
          .getJLabelValPaceLap()
          .setText(PaceUnit.computeAllure(lap.getTotalDist(),
                                          lap.getMovingTotalTime()));

      // Vitesse Moy.
      view.getJPanelRight()
          .getJLabelValSpeedLap()
          .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(lap.getTotalDist(),
                                                            lap.getMovingTotalTime()));

      // Frequence cardiaque
      view.getJPanelRight()
          .getJLabelValHeartLap()
          .setText(Integer.toString(lap.getAvgHeartRate()) + " / "
                   + Integer.toString(lap.getMaxHeartRate()));
      // Calories
      view.getJPanelRight().getJLabelValCaloriesLap()
          .setText(String.valueOf(lap.getCalories()));

      // Altitude
      view.getJPanelRight()
          .getJLabelValAltitudeLap()
          .setText("+" + Integer.toString(lap.computeDenivelePos()) + " / -"
                   + Integer.toString(lap.computeDeniveleNeg()));

      // on déclenche l'evenement.
      ModelPointsManager.getInstance().setLap(this, selectedItem);
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }

  private void clearLap(JDialogMap view) {
    view.getJPanelRight().getjLabelValDateTime().setText("");
    view.getJPanelRight().getJLabelValDayLap().setText("");
    view.getJPanelRight().getJLabelValHourLap().setText("");
    view.getJPanelRight().getJLabelValTimeTotLap().setText("");
    view.getJPanelRight().getjLabelValTimeMovingLap().setText("");
    view.getJPanelRight().getjLabelValTimePauseLap().setText("");
    view.getJPanelRight().getJLabelValDistanceLap().setText("");
    view.getJPanelRight().getJLabelValTimeTotLap().setText("");
    view.getJPanelRight().getJLabelValPaceLap().setText("");
    view.getJPanelRight().getJLabelValSpeedLap().setText("");
    view.getJPanelRight().getJLabelValHeartLap().setText("");
    view.getJPanelRight().getJLabelValCaloriesLap().setText("");
    view.getJPanelRight().getJLabelValAltitudeLap().setText("");
  }

  /**
   * 
   */
  private void updateSummary(JDialogMap view) throws SQLException {
    log.info(">>updateSummary");

    if (dataRun == null) {
      return;
    }

    // Date et heure
    SimpleDateFormat df = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss",
                                               LanguageManager.getManager()
                                                   .getLocale());
    view.getJPanelRight().getjLabelValDateTime()
        .setText(df.format(dataRun.getTime()));

    int value;
    if (!dataRun.getUnit().equals(DistanceUnit.getDefaultUnit())) {
      // distance
      dataRun.setUnit(DistanceUnit.getDefaultUnit());
    }

    // distance
    view.getJPanelRight().getJLabelValDistTot()
        .setText(DistanceUnit.formatWithUnit(dataRun.getComputeDistanceTot()));

    // Temps tot
    view.getJPanelRight()
        .getJLabelValTimeTot()
        .setText(TimeUnit.formatHundredSecondeTime(dataRun.computeTimeTot()));

    // Temps
    int timeMoving = dataRun.computeTimeTot() - dataRun.computeTimePauseTot();
    view.getJPanelRight().getjLabelValTimeMovingTot()
        .setText(TimeUnit.formatHundredSecondeTime(timeMoving));

    // Temps pause
    view.getJPanelRight()
        .getjLabelValTimePauseTot()
        .setText(TimeUnit.formatHundredSecondeTime(dataRun
            .computeTimePauseTot()));

    // vitesse moyenne
    view.getJPanelRight()
        .getJLabelValSpeedMoyTot()
        .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(dataRun
            .getComputeDistanceTot(), timeMoving));

    // allure moyenne
    view.getJPanelRight()
        .getJLabelValAllureTot()
        .setText(PaceUnit.computeFormatAllureWithUnit(dataRun
            .getComputeDistanceTot(), timeMoving));

    // calories.
    value = RunLapTableManager.getInstance().computeCalories(dataRun.getId());
    view.getJPanelRight().getJLabelValCaloriesTot()
        .setText(Integer.toString(value));

    // frequence moyenne/max/min.
    int avg = dataRun.computeAvgRate();
    int min = dataRun.computeMinRate();
    int max = dataRun.computeMaxRate();

    view.getJPanelRight()
        .getJLabelValHeartTot()
        .setText(Integer.toString(avg) + " / " + Integer.toString(min) + " / "
                 + Integer.toString(max));

    // Altitude.
    correctAltitude(view);
    
    // Activite
    view.getJPanelRight().getJLabelValActivity().setIcon(dataRun.getSportTypeIcon());
    view.getJPanelRight().getJLabelValActivity()
        .setText(dataRun.getLibelleSportType());

    // Equipement
    view.getJPanelRight().getJLabelValEquipment()
        .setText(dataRun.getEquipement());

    // Location
    view.getJPanelRight().getJLabelValLocation().setText(dataRun.getLocation());
    
    // Product
    String product = ProductDeviceUtil.toExternalForm(dataRun.getProductId(),
                                                      dataRun.getProductName(),
                                                      dataRun
                                                          .getProductVersion());
    view.getJPanelRight().getJLabelValProduct().setText(product);

    log.info("<<updateSummary");
  }

  /**
   * Mis &agrave; jour des altitudes.
   * 
   * @param view
   * @throws SQLException
   */
  public void correctAltitude(JDialogMap view) throws SQLException {
    if (dataRun == null) {
      return;
    }
    int[] alt = (view.getJPanelRight().getJSwitchBox().isOn()) ? dataRun
        .computeAlt() : dataRun.computeAltOriginal();
    view.getJPanelRight()
        .getJLabelValAltitudeTot()
        .setText("+" + Integer.toString(alt[0]) + " / -"
                 + Integer.toString(alt[1]));
  }
  
  private String title(DataRun dataRun) {
    if (dataRun == null) {
      log.error("dataRun est null");
      return null;
    }

    try {
      return LanguageManager.getManager().getCurrentLang().getDateFormatter()
          .format(dataRun.getTime())
             + "   "
             + new SimpleDateFormat("kk:mm:ss").format(dataRun.getTime())
             + "   "
             + DistanceUnit.formatWithUnit(dataRun.getComputeDistanceTot());
    }
    catch (SQLException e) {
      log.error("", e);
    }

    return null;
  }

}
