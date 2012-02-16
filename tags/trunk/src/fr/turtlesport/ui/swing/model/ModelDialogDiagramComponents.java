package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogDiagramComponents;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.TimeUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelDialogDiagramComponents {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(ModelDialogDiagramComponents.class);
  }

  /**
   * 
   */
  public ModelDialogDiagramComponents() {
    super();
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JDialogDiagramComponents view) throws SQLException {
    log.debug(">>updateView");

    DataRun dataRun = ModelPointsManager.getInstance().getDataRun();

    // resume
    // ----------------------------------------
    updateSummary(view, dataRun);

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
  public void updateViewLap(JDialogDiagramComponents view, int selectedItem) {
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

      // Temps
      view.getJPanelRight().getJLabelValTimeLap()
          .setText(TimeUnit.formatHundredSecondeTime(lap.getTotalTime()));

      // Allure moy.
      view.getJPanelRight()
          .getJLabelValPaceLap()
          .setText(PaceUnit.computeAllure(lap.getTotalDist(),
                                          lap.getTotalTime()));

      // Vitesse Moy.
      view.getJPanelRight()
          .getJLabelValSpeedLap()
          .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(lap
                  .getTotalDist(), lap.getTotalTime()));

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

  private void clearLap(JDialogDiagramComponents view) {
    view.getJPanelRight().getjLabelValDateTime().setText("");
    view.getJPanelRight().getJLabelValDayLap().setText("");
    view.getJPanelRight().getJLabelValHourLap().setText("");
    view.getJPanelRight().getJLabelValTimeLap().setText("");
    view.getJPanelRight().getJLabelValDistanceLap().setText("");
    view.getJPanelRight().getJLabelValTimeLap().setText("");
    view.getJPanelRight().getJLabelValPaceLap().setText("");
    view.getJPanelRight().getJLabelValSpeedLap().setText("");
    view.getJPanelRight().getJLabelValHeartLap().setText("");
    view.getJPanelRight().getJLabelValCaloriesLap().setText("");
    view.getJPanelRight().getJLabelValAltitudeLap().setText("");
  }

  /**
   * 
   */
  private void updateSummary(JDialogDiagramComponents view, DataRun dataRun) throws SQLException {
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

    // Temps
    view.getJPanelRight().getJLabelValTimeTot()
        .setText(TimeUnit.formatHundredSecondeTime(dataRun.computeTimeTot()));

    // vitesse moyenne
    view.getJPanelRight()
        .getJLabelValSpeedMoyTot()
        .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(dataRun
            .getComputeDistanceTot(), dataRun.computeTimeTot()));

    // allure moyenne
    view.getJPanelRight()
        .getJLabelValAllureTot()
        .setText(PaceUnit.computeFormatAllureWithUnit(dataRun
            .getComputeDistanceTot(), dataRun.computeTimeTot()));

    // calories.
    value = RunLapTableManager.getInstance().computeCalories(dataRun.getId());
    view.getJPanelRight().getJLabelValCaloriesTot()
        .setText(Integer.toString(value));

    // frequence moyenne/max/min.
    int avg = RunLapTableManager.getInstance().heartAvg(dataRun.getId());
    int min = RunTrkTableManager.getInstance().heartMin(dataRun.getId());
    int max = RunLapTableManager.getInstance().heartMax(dataRun.getId());

    view.getJPanelRight()
        .getJLabelValHeartTot()
        .setText(Integer.toString(avg) + " / " + Integer.toString(min) + " / "
                 + Integer.toString(max));

    // Altitude.
    int[] alt = RunTrkTableManager.getInstance().altitude(dataRun.getId());
    view.getJPanelRight()
        .getJLabelValAltitudeTot()
        .setText("+" + Integer.toString(alt[0]) + " / -"
                 + Integer.toString(alt[1]));

    // Categorie
    view.getJPanelRight().getJLabelValActivity()
        .setText(dataRun.getLibelleSportType());

    // Equipement
    view.getJPanelRight().getJLabelValEquipment()
        .setText(dataRun.getEquipement());
    
    // Location
    view.getJPanelRight().getjLabelValLocation()
        .setText(dataRun.getLocation());


    log.info("<<updateSummary");
  }

}
