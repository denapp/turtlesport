package fr.turtlesport.ui.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ProgressMonitor;
import javax.swing.UIManager;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.geo.FactoryGeoConvertRun;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.geo.IGeoConvertProgress;
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.JFileSaver;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class ExportAllActionListener implements ActionListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ExportAllActionListener.class);
  }

  private String              ext;

  /**
   * 
   * @param ext
   */
  public ExportAllActionListener(String ext) {
    this.ext = ext;
  }

  /**
   * Restitue l'anne et le mois des runs&agrave; exporter.
   * 
   * @param year
   * @param month
   */
  public YearMonth getDate() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent ae) {
    MainGui.getWindow().afterRunnableSwing();

    final List<DataRun> runs;
    StringBuilder name = new StringBuilder();
    YearMonth date = getDate();
    try {
      if (date == null) {
        runs = RunTableManager.getInstance().retreive(MainGui.getWindow()
            .getCurrentIdUser());
      }
      else {
        runs = RunTableManager.getInstance()
            .retreiveDesc(MainGui.getWindow().getCurrentIdUser(),
                          date.year,
                          date.month);
      }

      if (runs != null && runs.size() > 0) {
        if (date == null) {
          DateFormat df = LanguageManager.getManager().getCurrentLang()
              .getDateTimeFormatterWithoutSep();
          name.append(df.format(runs.get(0).getTime()));
          if (runs.size() > 1) {
            name.append("-");
            name.append(df.format(runs.get(runs.size() - 1).getTime()));
          }
        }
        else {
          name.append(date.getLibelle());
        }
      }
    }
    catch (SQLException e) {
      log.error("", e);
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), MainGui.class);
      JShowMessage.error(rb.getString("errorSQLExport"));
      return;
    }
    finally {
      MainGui.getWindow().afterRunnableSwing();
    }

    final IGeoConvertRun cv = FactoryGeoConvertRun.getInstance(ext);
    final File out = JFileSaver.showSaveDialog(MainGui.getWindow(),
                                               name.toString(),
                                               cv.extension()[0],
                                               cv.description());
    if (out != null) {
      final ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), JPanelRun.class);

      UIManager
          .put("ProgressMonitor.progressText", rb.getString("exportTitle"));

      final ProgressMonitor monitor = new ProgressMonitor(MainGui.getWindow(),
                                                          rb.getString("exportTitle"),
                                                          "message",
                                                          0,
                                                          100);

      monitor.setProgress(0);

      final IGeoConvertProgress progress = new IGeoConvertProgress() {

        public boolean cancel() {
          return monitor.isCanceled();
        }

        public void begin(int nbRuns) {
          monitor.setMaximum(nbRuns);
          monitor.setProgress(0);
        }

        public void convert(int index, int nbRuns) {
          setProgress(index);
        }

        public void end() {
          monitor.close();
        }

        private void setProgress(final int index) {
          monitor.setProgress(index + 1);
          monitor.setNote(index + "/" + monitor.getMaximum());
        }
      };

      new Thread() {
        public void run() {
          try {
            cv.convert(runs, progress, out);
            if (!progress.cancel()) {
              ResourceBundle rb = ResourceBundleUtility
                  .getBundle(LanguageManager.getManager().getCurrentLang(),
                             JPanelRun.class);

              JShowMessage.ok(rb.getString("exportOK"),
                              rb.getString("exportTitle"));
            }
          }
          catch (GeoConvertException e) {
            log.error("", e);
            JShowMessage.error(e.getMessage());
          }
          catch (SQLException e) {
            log.error("", e);
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), MainGui.class);
            JShowMessage.error(rb.getString("errorSQLExport"));
          }

          MainGui.getWindow().afterRunnableSwing();
        }
      }.start();
    }
  }

}
