package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataSearchRun;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.calendar.JPanelTableRun;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelRunTable {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelRunTable.class);
  }

  private int                 idUser;

  /**
   * 
   */
  public ModelRunTable() {
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
  public void updateView(JPanelTableRun view, DataSearchRun search) throws SQLException {
    log.debug(">>updateView");

    List<DataRun> listRun = RunTableManager.getInstance().retreiveDesc(MainGui
        .getWindow().getCurrentIdUser(), search);

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
  public void updateViewDateChanged(JPanelTableRun view, Date date) throws SQLException {
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
  public void retreiveDate(JPanelTableRun view, Date date) {
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
