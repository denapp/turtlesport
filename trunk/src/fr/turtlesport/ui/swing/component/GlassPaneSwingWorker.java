package fr.turtlesport.ui.swing.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * startButton.addActionListener(new ActionListener() { 
 *    public void actionPerformed(ActionEvent e) { 
 *       GlassPaneWorker worker = new GlassPaneWorker() {
 *           @Override 
 *           protected Object doInBackground() throws Exception {
 *              Thread.sleep(5000); return null; 
 *           }
 *        }; 
 *        worker.execute(frame); 
 *     } 
 * });
 * 
 * @author Denis Apparicio
 * 
 */
public abstract class GlassPaneSwingWorker extends SwingWorker<Object, Object> {

  /**
   * @param frame
   */
  public void execute(final JFrame frame) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        final WaitingGlassPane glassPane = new WaitingGlassPane(frame);
        addPropertyChangeListener(new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            if ("state".equals(evt.getPropertyName())) {
              if (StateValue.DONE.equals(evt.getNewValue())) {
                glassPane.setBusy(false);
              }
              else if (StateValue.STARTED.equals(evt.getNewValue())) {
                glassPane.setBusy(true);
              }
            }
          }
        });
      }
    });
    super.execute();
  }

}