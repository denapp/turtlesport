package fr.turtlesport.ui.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.geo.FactoryGeoConvertRun;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.googleearth.GoogleEarthException;
import fr.turtlesport.googleearth.GoogleEarthFactory;
import fr.turtlesport.googleearth.IGoogleEarth;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class GoogleEarthShowActionListener implements ActionListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(GoogleEarthShowActionListener.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    MainGui.getWindow().beforeRunnableSwing();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          IGoogleEarth ge = GoogleEarthFactory.getDefault();

          // Determine si google earth est installe
          if (!ge.isInstalled()) {
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), JPanelRun.class);
            JShowMessage.error(rb.getString("errorInstallGoogleEarth"));
          }
          else {
            // recuperation des pistes
            DataRun dataRun = ModelPointsManager.getInstance().getDataRun();
            if (dataRun != null) {
              File kmlFile = FactoryGeoConvertRun
                  .getInstance(FactoryGeoConvertRun.KML).convert(dataRun);
              if (kmlFile != null) {
                ge.open(kmlFile);
              }
            }
          }
        }
        catch (SQLException e) {
          log.error("", e);
          ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
              .getManager().getCurrentLang(), JPanelRun.class);
          JShowMessage.error(rb.getString("errorDatabase"));
        }
        catch (GeoConvertException e) {
          log.error("", e);
          JShowMessage.error(e.getMessage());
        }
        catch (GoogleEarthException e) {
          log.error("", e);
          JShowMessage.error(e.getMessage());
        }
        MainGui.getWindow().afterRunnableSwing();
      }
    });

  }

}
