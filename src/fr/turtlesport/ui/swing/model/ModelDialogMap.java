package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
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

    DataRun dataRun = ModelPointsManager.getInstance().getDataRun();

    view.getJLabelTitle().setText(title(dataRun));

    // resume
    // ----------------------------------------
    updateSummary(view, dataRun);

    // mis a jour des tours intermediaires
    // -------------------------------------------------------
    // recuperation des donnees
    int size = ModelPointsManager.getInstance().runLapsSize();
    if (size > 1) {
      view.getJComboBoxLap().addItem(" ");
      // mis a jour de la vue.
      for (int i = 0; i < size; i++) {
        view.getJComboBoxLap().addItem(String.valueOf(i + 1));
      }
    }
    else {
      view.getJComboBoxLap().setEnabled(false);
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
      view.getJLabelValDayLap().setText(LanguageManager.getManager()
          .getCurrentLang().getDateFormatter().format(lap.getStartTime()));

      // Heure
      SimpleDateFormat dfTime = new SimpleDateFormat("kk:mm:ss");
      view.getJLabelValHourLap().setText(dfTime.format(lap.getStartTime()));

      // Distance
      view.getJLabelValDistanceLap().setText(DistanceUnit.formatMetersInKm(lap
          .getTotalDist()));

      // Temps
      view.getJLabelValTimeLap().setText(TimeUnit.formatHundredSecondeTime(lap
          .getTotalTime()));

      // Allure moy.
      view.getJLabelValPaceLap().setText(PaceUnit.computeAllure(lap
          .getTotalDist(), lap.getTotalTime()));

      // Vitesse Moy.
      view.getJLabelValSpeedLap().setText(PaceUnit.computeAllure(lap
          .getTotalDist(), lap.getTotalTime()));

      // Frequence cardiaque
      view.getJLabelValHeartLap()
          .setText(Integer.toString(lap.getAvgHeartRate()) + " / "
                   + Integer.toString(lap.getMaxHeartRate()));
      // Calories
      view.getJLabelValCaloriesLap().setText(String.valueOf(lap.getCalories()));

      // Altitude
      view.getJLabelValAltitudeLap().setText("+"
                                             + Integer.toString(lap
                                                 .computeDenivelePos())
                                             + " / -"
                                             + Integer.toString(lap
                                                 .computeDeniveleNeg()));

      // on déclenche l'evenement.
      ModelPointsManager.getInstance().setLap(this, selectedItem);
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }

  private void clearLap(JDialogMap view) {
    view.getJLabelValDayLap().setText("");
    view.getJLabelValHourLap().setText("");
    view.getJLabelValTimeLap().setText("");
    view.getJLabelValDistanceLap().setText("");
    view.getJLabelValTimeLap().setText("");
    view.getJLabelValPaceLap().setText("");
    view.getJLabelValSpeedLap().setText("");
    view.getJLabelValHeartLap().setText("");
    view.getJLabelValCaloriesLap().setText("");
    view.getJLabelValAltitudeLap().setText("");
  }

  /**
   * 
   */
  private void updateSummary(JDialogMap view, DataRun dataRun) throws SQLException {
    log.info(">>updateSummary");

    if (dataRun == null) {
      return;
    }

    int value;
    if (!dataRun.getUnit().equals(DistanceUnit.getDefaultUnit())) {
      // distance
      dataRun.setUnit(DistanceUnit.getDefaultUnit());
    }

    // distance
    view.getJLabelValDistTot().setText(DistanceUnit.formatWithUnit(dataRun
        .getComputeDistanceTot()));

    // Temps
    view.getJLabelValTimeTot()
        .setText(TimeUnit.formatHundredSecondeTime(dataRun.computeTimeTot()));

    // vitesse moyenne
    view.getJLabelValSpeedMoyTot()
        .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(dataRun
            .getComputeDistanceTot(), dataRun.computeTimeTot()));

    // allure moyenne
    view.getJLabelValAllureTot()
        .setText(PaceUnit.computeFormatAllureWithUnit(dataRun
            .getComputeDistanceTot(), dataRun.computeTimeTot()));

    // calories.
    value = RunLapTableManager.getInstance().computeCalories(dataRun.getId());
    view.getJLabelValCaloriesTot().setText(Integer.toString(value));

    // frequence moyenne/max/min.
    int avg = RunLapTableManager.getInstance().heartAvg(dataRun.getId());
    int min = RunTrkTableManager.getInstance().heartMin(dataRun.getId());
    int max = RunLapTableManager.getInstance().heartMax(dataRun.getId());

    view.getJLabelValHeartTot().setText(Integer.toString(avg) + " / "
                                        + Integer.toString(min) + " / "
                                        + Integer.toString(max));

    // Altitude.
    int[] alt = RunTrkTableManager.getInstance().altitude(dataRun.getId());
    view.getJLabelValAltitudeTot().setText("+" + Integer.toString(alt[0])
                                           + " / -" + Integer.toString(alt[1]));

    // Categorie
    view.getJLabelValActivity().setText(dataRun.getLibelleSportType());

    // Equipement
    view.getJLabelValEquipment().setText(dataRun.getEquipement());

    log.info("<<updateSummary");
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
