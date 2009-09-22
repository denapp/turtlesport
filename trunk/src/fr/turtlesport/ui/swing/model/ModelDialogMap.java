package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogMap;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelDialogMap {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelDialogMap.class);
  }

  private List<IGeoPosition>  list;

  private String              title;

  private DataRun             dataRun;

  /**
   * 
   */
  public ModelDialogMap(DataRun dataRun) throws SQLException {
    super();

    this.dataRun = dataRun;

    // recuperation des donnees
    DataRunTrk[] trks = RunTrkTableManager.getInstance().getTrks(dataRun
        .getId());

    // Modification des points.
    list = new ArrayList<IGeoPosition>();
    for (DataRunTrk p : trks) {
      IGeoPosition geo = GeoUtil.makeFromGarmin(p.getLatitude(), p
          .getLongitude());
      if (geo != null) {
        list.add(geo);
      }
    }

    // Recuperation du libelle
    initTitle(dataRun);
  }

  /**
   * 
   */
  public ModelDialogMap(List<IGeoPosition> list, DataRun dataRun) {
    super();

    this.dataRun = dataRun;
    this.list = list;

    // Recuperation du libelle
    initTitle(dataRun);
  }

  /**
   * Restitue la liste des points.
   * 
   * @return la liste des points.
   */
  public List<IGeoPosition> getList() {
    return list;
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JDialogMap view) throws SQLException {
    log.debug(">>updateView");

    view.getJLabelTitle().setText(title);
    view.getJPanelMap().getModelMap().updateData(list);

    // resume
    // ----------------------------------------
    updateSummary(view);

    // mis a jour des tours intermediaires
    // -------------------------------------------------------
    // recuperation des donnees
    if (dataRun != null) {
      DataRunLap[] runLaps = RunLapTableManager.getInstance().findLaps(dataRun
          .getId());
      if (runLaps.length > 1) {
        // mis a jour de la vue.
        view.getJComboBoxLap().addItem(new DataRunLapInCombo(" "));
        for (int i = 0; i < runLaps.length; i++) {
          view.getJComboBoxLap().addItem(new DataRunLapInCombo(runLaps[i],
                                                               i + 1));
        }
      }
      else {
        view.getJComboBoxLap().setEnabled(false);
      }
    }

    log.debug("<<updateView");
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param dialogMap
   * @param selectedItem
   */
  public void updateViewLap(JDialogMap view, Object selectedItem) {
    if (selectedItem == null) {
      clearLap(view);
      return;
    }

    try {
      DataRunLap lap = ((DataRunLapInCombo) selectedItem).lap;
      if (lap == null) {
        clearLap(view);
        return;
      }

      // Map
      if (view.getJComboBoxLap().getItemCount() > 0) {
        DataRunTrk deb = null;
        DataRunTrk end = null;

        deb = RunLapTableManager.getInstance().lapTrkBegin(lap.getId(),
                                                           lap.getLapIndex());
        if (deb != null) {
          end = RunLapTableManager.getInstance().lapTrkEnd(lap.getId(),
                                                           lap.getLapIndex());
        }
        if (deb != null && end != null) {
          IGeoPosition p1 = GeoUtil.makeFromGarmin(deb.getLatitude(), deb
              .getLongitude());
          IGeoPosition p2 = GeoUtil.makeFromGarmin(end.getLatitude(), end
              .getLongitude());
          view.getJPanelMap().getModelMap().updateInt(p1, p2);
        }
        else {
          view.getJPanelMap().getModelMap().updateInt(null, null);
        }
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
      view.getJLabelValHeartLap().setText(Integer.toString(lap
          .getAvgHeartRate())
                                          + " / "
                                          + Integer.toString(lap
                                              .getMaxHeartRate()));
      // Calories
      view.getJLabelValCaloriesLap().setText(String.valueOf(lap.getCalories()));

      // Altitude
      view.getJLabelValAltitudeLap().setText("+"
                                             + Integer.toString(lap
                                                 .computeDenivelePos())
                                             + " / -"
                                             + Integer.toString(lap
                                                 .computeDeniveleNeg()));
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }

  private void clearLap(JDialogMap view) {
    view.getJPanelMap().getModelMap().updateInt(null, null);
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
  private void updateSummary(JDialogMap view) throws SQLException {
    log.info(">>updateSummary");

    if (dataRun == null) {
      return;
    }

    int value;
    if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
      // distance
      dataRun.setComputeDistanceTot(DistanceUnit
          .convert(DistanceUnit.unitKm(),
                   DistanceUnit.getDefaultUnit(),
                   dataRun.getComputeDistanceTot()));
    }

    // distance
    view.getJLabelValDistTot().setText(DistanceUnit.formatWithUnit(dataRun
        .getComputeDistanceTot()));

    // Temps
    view.getJLabelValTimeTot().setText(TimeUnit
        .formatHundredSecondeTime(dataRun.computeTimeTot()));

    // vitesse moyenne
    view.getJLabelValSpeedMoyTot().setText(SpeedPaceUnit
        .computeFormatSpeedWithUnit(dataRun.getComputeDistanceTot(), dataRun
            .computeTimeTot()));

    // allure moyenne
    view.getJLabelValAllureTot().setText(PaceUnit
        .computeFormatAllureWithUnit(dataRun.getComputeDistanceTot(), dataRun
            .computeTimeTot()));

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
    view.getJLabelValCategory().setText(dataRun.getLibelleSportType());

    // Equipement
    view.getJLabelValEquipment().setText(dataRun.getEquipement());

    // Commentaires
    view.getJTextFieldNotes().setText((dataRun.getComments() == null) ? ""
        : dataRun.getComments());

    log.info("<<updateSummary");
  }

  private void initTitle(DataRun dataRun) {
    if (dataRun == null) {
      log.error("dataRun est null");
      return;
    }
    try {
      title = LanguageManager.getManager().getCurrentLang().getDateFormatter()
          .format(dataRun.getTime())
              + "   "
              + new SimpleDateFormat("kk:mm:ss").format(dataRun.getTime())
              + "   "
              + DistanceUnit.formatWithUnit(dataRun.getComputeDistanceTot());
    }
    catch (SQLException e) {
      log.error("", e);
      title = null;
    }
  }

  private class DataRunLapInCombo {
    private DataRunLap lap;

    private String     text;

    public DataRunLapInCombo(DataRunLap lap, int index) {
      super();
      this.lap = lap;
      this.text = String.valueOf(index);
    }

    public DataRunLapInCombo(String text) {
      super();
      this.text = text;
    }

    public String toString() {
      return text;
    }

  }

}
