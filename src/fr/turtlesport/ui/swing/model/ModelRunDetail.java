package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogRunDetail;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelRunDetail {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelRunDetail.class);
  }

  private DataRun             dataRun;

  /**
   * 
   */
  public ModelRunDetail(DataRun dataRun) {
    super();
    this.dataRun = dataRun;
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
  public void updateView(JDialogRunDetail view) throws SQLException {
    log.debug(">>updateView");

    // Titre
    String value = LanguageManager.getManager().getCurrentLang()
        .getDateFormatter().format(dataRun.getTime())
                   + "   "
                   + new SimpleDateFormat("kk:mm:ss").format(dataRun.getTime());

    view.getJLabelTitle().setText(value);

    // Distance tot
    view.getJLabelValDistanceTot().setText(DistanceUnit.formatWithUnit(dataRun
        .getComputeDistanceTot()));

    // Temps tot
    view.getJLabelValTimeTot().setText(TimeUnit
        .formatHundredSecondeTime(dataRun.computeTimeTot()));

    // recuperation des donnees
    DataRunTrk[] trks = RunTrkTableManager.getInstance().getTrks(dataRun
        .getId());

    view.getTableModel().updateData(trks);

    log.debug("<<updateView");
  }

}