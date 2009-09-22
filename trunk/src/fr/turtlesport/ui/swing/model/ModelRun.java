package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.SwingLookAndFeel;
import fr.turtlesport.ui.swing.component.calendar.JPanelCalendar;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelRun {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelRun.class);
  }

  private DataRun             dataRun;

  /**
   * 
   */
  public ModelRun() {
    super();
  }

  /**
   * @return the dataRuns
   */
  public DataRun getDataRun() {
    return dataRun;
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JPanelRun view, Date date) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>updateView " + date);
    }

    eraseGui(view);
    if (date == null) {
      dataRun = null;
      return;
    }

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().findNext(MainGui.getWindow()
                                                         .getCurrentIdUser(),
                                                     date);
    log.info("dataRun id=" + dataRun.getId());

    // mis a jour de la vue
    update(view);

    log.info("<<updateView");
  }

  /**
   * @param summary
   * @throws SQLException
   */
  public void updateViewNext(JPanelRun view) throws SQLException {
    log.debug(">>updateViewNext");

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().findNext(MainGui.getWindow()
                                                         .getCurrentIdUser(),
                                                     dataRun.getTime());

    // mis a jour de la vue
    update(view);

    // mis a jour du calendrier
    JPanelCalendar jPanelCalendar = MainGui.getWindow().getJPanelCalendar();
    if (jPanelCalendar != null) {
      jPanelCalendar.fireDateChanged(dataRun.getTime());
    }

    log.debug(">>updateViewNext");
  }

  /**
   * @param summary
   * @throws SQLException
   */
  public void updateViewPrev(JPanelRun view) throws SQLException {
    log.debug(">>updateViewPrev");

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().findPrev(MainGui.getWindow()
                                                         .getCurrentIdUser(),
                                                     dataRun.getTime());

    // mis a jour de la vue
    update(view);

    // mis a jour du calendrier
    JPanelCalendar jPanelCalendar = MainGui.getWindow().getJPanelCalendar();
    if (jPanelCalendar != null) {
      jPanelCalendar.fireDateChanged(dataRun.getTime());
    }

    log.debug(">>updateViewPrev");
  }

  /**
   * @param run
   * @param event
   */
  public void performedUnit(JPanelRun view, UnitEvent e) {
    if (!e.isEventDistance()) {
      return;
    }

    if (dataRun != null) {
      double distTot = 0;
      int timeTot = 0;
      try {
        distTot = DistanceUnit.convert(dataRun.getUnit(), e.getUnit(), dataRun
            .getComputeDistanceTot());
        dataRun.setComputeDistanceTot(distTot);
        timeTot = dataRun.computeTimeTot();

        dataRun.setUnit(e.getUnit());
      }
      catch (SQLException sqle) {
        // ne peut arriver
      }

      dataRun.setComputeDistanceTot(distTot);

      // distance
      view.getJLabelValDistTot().setText(DistanceUnit.formatWithUnit(distTot));

      // Temps
      view.getJLabelValTimeTot().setText(TimeUnit
          .formatHundredSecondeTime(timeTot));

      // vitesse moyenne
      view.getJLabelValSpeedMoy().setText(SpeedPaceUnit
          .computeFormatSpeedWithUnit(distTot, timeTot));

      // allure moyenne
      view.getJLabelValAllure().setText(PaceUnit
          .computeFormatAllureWithUnit(distTot, timeTot));
    }

    // mis a jour des tours intermediaires.
    // ------------------------------------------------------
    view.getTableModelLap().performedUnit(e);
  }

  /**
   * Mis a jour de la vue.
   */
  private void update(JPanelRun view) throws SQLException {
    log.info(">>update");

    // mis a jour du panel general
    // -------------------------------
    updateSummary(view);

    // mis a jour des tours intermediaires
    // -------------------------------------------------------
    if (dataRun == null) {
      // mis a jour de la vue.
      view.getTableModelLap().clear();
    }
    else {
      // recuperation des donnees
      DataRunLap[] runLaps = RunLapTableManager.getInstance().findLaps(dataRun
          .getId());

      // mis a jour de la vue.
      view.getTableModelLap().updateData(runLaps);
    }

    // mis a jour du graph
    // -----------------------------------
    // recuperation des donnees

    DataRunTrk[] trks = RunTrkTableManager.getInstance().getTrks(dataRun
        .getId());
    if (trks != null) {
      if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
        for (DataRunTrk t : trks) {
          t.setDistance((float) DistanceUnit.convert(DistanceUnit.unitKm(),
                                                     DistanceUnit
                                                         .getDefaultUnit(),
                                                     t.getDistance()));
        }
      }
    }

    // mis a jour de la vue.
    view.getJDiagram().updatePoints(trks, DistanceUnit.getDefaultUnit());

    // mis a jour du mercator
    // -------------------------------------------------------
    ArrayList<IGeoPosition> listGeo = new ArrayList<IGeoPosition>();

    // recuperation des donnees
    if (trks != null) {
      for (DataRunTrk p : trks) {
        IGeoPosition gp = GeoUtil.makeFromGarmin(p.getLatitude(), p
            .getLongitude());
        if (gp != null) {
          listGeo.add(gp);
        }
      }
    }
    // mis a jour de la vue.
    view.getJPanelMap().getModelMap().updateData(listGeo);

    // Pour les boutons de navigation avec CDE/Motif
    // ----------------------------------------------
    if (SwingLookAndFeel.isLookAndFeelMotif()) {
      MainGui.getWindow().updateComponentTreeUI();
    }

    log.info("<<update");
  }

  /**
   * Effacement de la vue.
   */
  private void eraseGui(JPanelRun view) {
    view.getJLabelValDistTot().setText(null);
    view.getJLabelValTimeTot().setText(null);
    view.getJLabelValAllure().setText(null);
    view.getJLabelValSpeedMoy().setText(null);
    view.getJLabelValCalories().setText(null);
    view.getJLabelValHeart().setText(null);
    view.getJLabelValAlt().setText(null);
    view.getModelActivities().setSelectedItem("");
    view.getModelEquipements().setSelectedItem("");
    view.getJTextFieldNotes().setText("");
    view.getJButtonNext().setEnabled(false);
    view.getJButtonPrev().setEnabled(false);
    view.getJButtonSave().setEnabled(false);
    view.getJButtonGoogleEarth().setEnabled(false);
    if (view.getJButtonEmail() != null) {
      view.getJButtonEmail().setEnabled(false);
    }
    view.setEnableMenuRun(false);
    MainGui.getWindow().setEnableMenuRun(false);

    view.getTableModelLap().clear();
    view.getJDiagram().updatePoints(null, DistanceUnit.getDefaultUnit());
    view.getJPanelMap().getModelMap().updateData(null);
  }

  /**
   * 
   */
  private void updateSummary(JPanelRun view) throws SQLException {
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
    view.getJLabelValSpeedMoy().setText(SpeedPaceUnit
        .computeFormatSpeedWithUnit(dataRun.getComputeDistanceTot(), dataRun
            .computeTimeTot()));

    // allure moyenne
    view.getJLabelValAllure().setText(PaceUnit
        .computeFormatAllureWithUnit(dataRun.getComputeDistanceTot(), dataRun
            .computeTimeTot()));

    // calories.
    value = RunLapTableManager.getInstance().computeCalories(dataRun.getId());
    view.getJLabelValCalories().setText(Integer.toString(value));

    // frequence moyenne/max/min.
    int avg = RunLapTableManager.getInstance().heartAvg(dataRun.getId());
    int min = RunTrkTableManager.getInstance().heartMin(dataRun.getId());
    int max = RunLapTableManager.getInstance().heartMax(dataRun.getId());

    view.getJLabelValHeart().setText(Integer.toString(avg) + " / "
                                     + Integer.toString(min) + " / "
                                     + Integer.toString(max));

    // Altitude.
    int[] alt = RunTrkTableManager.getInstance().altitude(dataRun.getId());
    view.getJLabelValAlt().setText("+" + Integer.toString(alt[0]) + " / -"
                                   + Integer.toString(alt[1]));

    // Activites
    view.getModelActivities().setSelectedActivity(dataRun.getSportType());

    // Equipement
    if (dataRun.getEquipement() != null) {
      view.getModelEquipements().setSelectedItem(dataRun.getEquipement());
    }
    else {
      view.getModelEquipements().setSelectedItem("");
    }
    view.getJTextFieldNotes().requestFocus();

    // Commentaires
    view.getJTextFieldNotes().setText((dataRun.getComments() == null) ? ""
        : dataRun.getComments());

    updateViewButtons(view);

    view.setEnableMenuRun(true);
    MainGui.getWindow().setEnableMenuRun(true);

    log.info("<<updateSummary");
  }

  /**
   * Mis &agrave; jour des boutons.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateViewButtons(JPanelRun view) throws SQLException {
    if (dataRun != null) {
      // Bouton suivant
      view.getJButtonNext().setEnabled(RunTableManager.getInstance()
          .hasNext(MainGui.getWindow().getCurrentIdUser(), dataRun.getTime()));

      // Bouton precedent
      view.getJButtonPrev().setEnabled(RunTableManager.getInstance()
          .hasPrev(MainGui.getWindow().getCurrentIdUser(), dataRun.getTime()));
    }
  }

  /**
   * Sauvegarde.
   */
  public void save(JPanelRun view) throws SQLException {
    if (dataRun == null) {
      return;
    }

    String comments = "";
    String newComments = "";
    if (dataRun.getComments() != null) {
      comments = dataRun.getComments();
    }
    if (view.getJTextFieldNotes() != null) {
      newComments = view.getJTextFieldNotes().getText();
    }

    String equipment = "";
    String newEquipment = "";
    if (dataRun.getEquipement() != null) {
      equipment = dataRun.getComments();
    }
    newEquipment = (String) view.getModelEquipements().getSelectedItem();

    int sportType = dataRun.getSportType();
    int newSportType = view.getModelActivities().getSportType();
    if (!newComments.equals(comments) || !newEquipment.equals(equipment)
        || (sportType != newSportType)) {
      RunTableManager.getInstance().update(dataRun.getId(),
                                           newComments,
                                           newEquipment,
                                           newSportType);
    }

  }

  /**
   * Suppression de la course.
   * 
   * @param view
   * @throws SQLException
   */
  public void delete(JPanelRun view) throws SQLException {
    if (dataRun == null) {
      return;
    }

    RunTableManager.getInstance().delete(dataRun.getId());

    // suppression de la date du calendrier si besoin
    JPanelCalendar jPanelCalendar = MainGui.getWindow().getJPanelCalendar();
    if (jPanelCalendar != null) {
      jPanelCalendar.fireDateDeleted(dataRun.getTime());
    }

    if (view.getJButtonNext().isEnabled()) {
      updateViewNext(view);
    }
    else if (view.getJButtonPrev().isEnabled()) {
      updateViewPrev(view);
    }
    else {
      eraseGui(view);
    }
  }

  /**
   * Mis &agrave; jour des boutons..
   * 
   * @param view
   * @throws SQLException
   */
  public void updateButtons(JPanelRun view) throws SQLException {
    if (dataRun != null) {

      updateViewButtons(view);
    }
  }

}
