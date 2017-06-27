package fr.turtlesport.ui.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.turtlesport.ui.swing.JDialogRunSendEmail;
import fr.turtlesport.ui.swing.model.ModelPointsManager;

/**
 * @author Denis Apparicio
 *
 */
public class EmailActionListener implements ActionListener {

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    JDialogRunSendEmail.prompt(ModelPointsManager.getInstance().getDataRun());
  }
}
