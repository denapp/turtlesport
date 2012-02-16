package fr.turtlesport.ui.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;

/**
 * @author Denis Apparicio
 * 
 */
public class MapMercatorActionListener implements ActionListener {

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent actionevent) {
    if (MainGui.getWindow().getRightComponent() instanceof JPanelRun) {
      JPanelRun p = (JPanelRun) MainGui.getWindow().getRightComponent();
      p.getJPanelMap().fireActionGrow();
    }
  }

}
