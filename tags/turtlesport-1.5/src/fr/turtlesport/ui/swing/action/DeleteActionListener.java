package fr.turtlesport.ui.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class DeleteActionListener implements ActionListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(DeleteActionListener.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent actionevent) {
    final ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), JPanelRun.class);
    if (!JShowMessage.question(rb.getString("questionDeleteRace"),
                               rb.getString("delete"))) {
      return;
    }
    MainGui.getWindow().beforeRunnableSwing();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          if (MainGui.getWindow().getRightComponent() instanceof JPanelRun) {
            JPanelRun p = (JPanelRun) MainGui.getWindow().getRightComponent();
            p.getModel().delete(p);
          }
        }
        catch (SQLException e) {
          log.error("", e);
          JShowMessage.error(rb.getString("errorDeleteRace"));
        }
        MainGui.getWindow().afterRunnableSwing();
      }
    });
  }
}
