package fr.turtlesport.ui.swing.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;

import fr.turtlesport.ui.swing.MainGui;

/**
 * startButton.addActionListener(new ActionListener() { public void
 * actionPerformed(ActionEvent e) { GlassPaneWorker worker = new
 * GlassPaneWorker() {
 * 
 * @Override protected Object doInBackground() throws Exception {
 *           Thread.sleep(5000); return null; } }; worker.execute(frame); } });
 * 
 * @author Denis Apparicio
 * 
 */
public abstract class CursorSwingWorker extends SwingWorker<Object, Object>
                                                                           implements
                                                                           PropertyChangeListener {

  public CursorSwingWorker() {
    addPropertyChangeListener(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent
   * )
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if ("state".equals(evt.getPropertyName())) {
      if (StateValue.DONE.equals(evt.getNewValue())) {
        MainGui.getWindow().afterRunnableSwing();
      }
      else if (StateValue.STARTED.equals(evt.getNewValue())) {
        MainGui.getWindow().beforeRunnableSwing();
      }
    }
  }

}