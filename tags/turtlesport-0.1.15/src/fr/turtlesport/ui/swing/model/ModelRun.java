package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.Date;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.db.RunTrkTableManager;
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
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JPanelRun view, Date date) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>updateView " + date);
    }

    if (date == null) {
      eraseGui(view);
      dataRun = null;
      return;
    }

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().findNext(MainGui.getWindow()
                                                         .getCurrentIdUser(),
                                                     date);

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

    // mis a jour du graph et de la vue
    // --------------------------------------------------
    // recuperation des donnees
    ModelPointsManager.getInstance().setDataRun(dataRun);

    // mis a jour de la vue.
    // view.getJPanelMap().getModel().updateData(dataRun, listGeo);

    // Mis a jour des boutons et menu
    // --------------------------------------------------------------------------------
    boolean hasPoint = ModelPointsManager.getInstance().hasPoints();
    view.setEnableMenuRun(hasPoint);
    view.getJMenuItemRunSave().setEnabled(true);
    view.getJMenuItemRunDelete().setEnabled(true);
    view.getJButtonSave().setEnabled(true);
    view.getJButtonDelete().setEnabled(true);
    MainGui.getWindow().setEnableMenuRun(hasPoint);
    MainGui.getWindow().getJMenuItemRunSave().setEnabled(true);
    MainGui.getWindow().getJMenuItemRunDelete().setEnabled(true);

    // Pour les boutons de navigation avec CDE/Motif
    // --------------------------------------------------------------------------------
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
    view.getJButtonGoogleMap().setEnabled(false);
    if (view.getJButtonEmail() != null) {
      view.getJButtonEmail().setEnabled(false);
    }
    view.setEnableMenuRun(false);
    MainGui.getWindow().setEnableMenuRun(false);

    try {
      ModelPointsManager.getInstance().setDataRun(null);
    }
    catch (SQLException e) {
      log.error("", e);
    }
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
    DataRun dataRun = ModelPointsManager.getInstance().getDataRun();
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
