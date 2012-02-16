package fr.turtlesport.ui.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import fr.turtlesport.geo.FactoryGeoConvertRun;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.JFileSaver;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class ExportActionListener implements ActionListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ExportActionListener.class);
  }

  private String              ext;

  public ExportActionListener(String ext) {
    this.ext = ext;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent ae) {
    String name = LanguageManager.getManager().getCurrentLang()
        .getDateTimeFormatterWithoutSep()
        .format(ModelPointsManager.getInstance().getDataRun().getTime());
    final IGeoConvertRun cv = FactoryGeoConvertRun.getInstance(ext);
    final File out = JFileSaver.showSaveDialog(MainGui.getWindow(),
                                               name,
                                               cv.extension()[0],
                                               cv.description());
    if (out != null) {
      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), JPanelRun.class);
            cv.convert(ModelPointsManager.getInstance().getDataRun(), out);
            JShowMessage.ok(rb.getString("exportOK"),
                            rb.getString("exportTitle"));
          }
          catch (GeoConvertException e) {
            log.error("", e);
            JShowMessage.error(e.getMessage());
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(e.getMessage());
          }
          MainGui.getWindow().afterRunnableSwing();
        }
      });

    }
  }

}
