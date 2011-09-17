package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.calendar.JPanelTreeRun;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelRunTreeTable {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelRunTreeTable.class);
  }

  private int                 idUser;

  /**
   * 
   */
  public ModelRunTreeTable() {
    super();
  }

  public int getIdUser() {
    return idUser;
  }

  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  /**
   * Mise a jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JPanelTreeRun view) throws SQLException {
    log.debug(">>updateView");

    List<DataRun> listRun = RunTableManager.getInstance().retreiveDesc(MainGui
        .getWindow().getCurrentIdUser());

    for (DataRun d : listRun) {
      d.getComputeDistanceTot();
    }

    view.fireCurrentRun(listRun);

    log.debug("<<updateView");
  }

  /**
   * Mise a jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateViewDateChanged(JPanelTreeRun view, Date date) throws SQLException {
    log.debug(">>updateViewDateChanged");

    view.fireCalendarSelectActiveDayPerformed(date);

    log.debug("<<updateViewDateChanged");
  }

  /**
   * Suppression d'une date.
   * 
   * @param panel
   * @param date
   */
  public void retreiveDate(JPanelTreeRun view, Date date) {
    if (log.isDebugEnabled()) {
      log.debug(">>retreiveDate date=" + date);
    }

    if (date == null) {
      return;
    }
    view.removeDate(date);

    log.debug("<<retreiveDate");
  }

}
