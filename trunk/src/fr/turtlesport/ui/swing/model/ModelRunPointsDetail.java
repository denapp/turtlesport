package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogRunPointsDetail;
import fr.turtlesport.ui.swing.JDialogRunPointsDetail.TableModelPoints;
import fr.turtlesport.ui.swing.JDialogRunPointsDetail.TableRowObject;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelRunPointsDetail {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelRunPointsDetail.class);
  }

  private DataRun             dataRun;

  private List<DataRunTrk>    listTrks;

  /**
   * 
   */
  public ModelRunPointsDetail(DataRun dataRun, List<DataRunTrk> listTrks) {
    super();
    this.dataRun = dataRun;
    this.listTrks = listTrks;
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
  public void updateView(JDialogRunPointsDetail view) throws SQLException {
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
    view.getJLabelValTimeTot()
        .setText(TimeUnit.formatHundredSecondeTime(dataRun.computeTimeTot()));

    // recuperation des donnees
    view.getTableModel().updateData(listTrks);

    log.debug("<<updateView");
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void deletePoints(JDialogRunPointsDetail view) {
    log.debug(">>deletePoints");

    TableModelPoints tableModel = view.getTableModel();
    DataRunTrk first = null;
    DataRunTrk last = null;
    for (TableRowObject r : tableModel.listRowObject) {
      if (r.trk != null && r.trk.getTime() != null) {
        first = r.trk;
        break;
      }
    }

    for (int i = tableModel.listRowObject.size() - 1; i > 0; i--) {
      DataRunTrk trk = tableModel.listRowObject.get(i).trk;
      if (trk != null && trk.getTime() != null) {
        last = tableModel.listRowObject.get(i).trk;
        break;
      }
    }
    long elapsed = 0;
    if (first != null && last != null) {
      elapsed = last.getTime().getTime() - first.getTime().getTime();
    }

    // Titre
    if (first != null) {
      String value = LanguageManager.getManager().getCurrentLang()
          .getDateFormatter().format(dataRun.getTime())
                     + "   "
                     + new SimpleDateFormat("kk:mm:ss").format(first.getTime());
      view.getJLabelTitle().setText(value);

      view.getJLabelValTimeTot()
          .setText(TimeUnit.formatMilliSecondeTime(elapsed));
    }

    // Distance tot
    // view.getJLabelValDistanceTot().setText(DistanceUnit.formatWithUnit(dataRun
    // .getComputeDistanceTot()));

    // Temps tot
    view.getJLabelValTimeTot()
        .setText(TimeUnit.formatMilliSecondeTime(elapsed));

    log.debug("<<deletePoints");
  }

  public void savePoints(JDialogRunPointsDetail view) throws SQLException {
    TableModelPoints tableModel = view.getTableModel();
    if (tableModel.listRowObject.size() != listTrks.size()) {
      listTrks = new ArrayList<DataRunTrk>();
      Iterator<TableRowObject> it = tableModel.listRowObject.iterator();
      while (it.hasNext()) {
        listTrks.add(it.next().trk);
      }
      
      RunTableManager.getInstance().store(dataRun, listTrks);
    }
    view.dispose();
  }
}
