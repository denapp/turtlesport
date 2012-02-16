package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.Calendar;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataRunWithoutPoints;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogAddRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.unit.DistanceUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelAddRun {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelAddRun.class);
  }

  private DataRunWithoutPoints data;

  public ModelAddRun() {
    super();
    data = new DataRunWithoutPoints();
    data.setIdUser(MainGui.getWindow().getCurrentIdUser());
  }

  /**
   * Restitue les data.
   * 
   * @return les data.
   */
  public DataRunWithoutPoints getData() {
    return data;
  }

  /**
   * Sauvegarde.
   * 
   * @throws SQLException
   */
  public void save(JDialogAddRun view) throws SQLException {
    log.info(">>save");

    // conversion unites
    String unit = data.getUnit();
    if (!DistanceUnit.isUnitKm(unit)) {
      // Distance
      data.setDistanceTot(DistanceUnit.convert(unit,
                                               DistanceUnit.unitKm(),
                                               data.getDistanceTot()));
    }
    data.setIdUser(MainGui.getWindow().getCurrentIdUser());
    data.setDistanceTot(data.getDistanceTot() * 1000);
    Calendar cal = Calendar.getInstance();
    cal.setTime(view.getJDatePicker().getDate());
    cal.set(Calendar.HOUR_OF_DAY, view.getJTextFieldTime().getHour());
    cal.set(Calendar.MINUTE, view.getJTextFieldTime().getMinute());
    cal.set(Calendar.SECOND, view.getJTextFieldTime().getSecond());
    data.setStartTime(cal.getTime());
    data.setTimeTot((int) view.getJTextFieldTimeTot().getTime() * 100);
    data.setSportType(((AbstractDataActivity) view.getModelActivities()
        .getSelectedItem()).getSportType());
    data.setEquipement((String) view.getModelEquipements().getSelectedItem());
    data.setComments(view.getJTextFieldNotes().getText());

    // log.error("data.getIdUser()=" + data.getIdUser());
    // log.error("data.getStartTime()=" + data.getStartTime());
    // log.error("data.getDistanceTot()=" + data.getDistanceTot());
    // log.error("data.getTimeTot()=" + data.getTimeTot());
    // log.error("data.getSportType()=" + data.getSportType());
    // log.error("data.getEquipement()=" + data.getEquipement());
    // log.error("data.getComments()=" + data.getComments());

    // sauvegarde des equipements
    RunTableManager.getInstance().store(data);
   
    log.info("<<save");
  }

  /**
   * Mis &agrave; jour de l'unit&eacute; de poids.
   * 
   * @param view
   *          la vue.
   * @param newUnit
   *          la nouvelle unit&eacute;
   */
  public void setUnitDistance(JDialogAddRun view, String newUnit) {
    if (data == null || newUnit == null || newUnit.equals(data.getUnit())) {
      return;
    }

    // Distance
    data.setDistanceTot(DistanceUnit.convert(data.getUnit(), newUnit, data
        .getDistanceTot()));
    data.setUnit(newUnit);
    view.getJTextFieldDistTot().setValue(data.getDistanceTot());
    view.getJComboBoxDistanceUnits().setSelectedItem(data.getUnit());
  }
}
