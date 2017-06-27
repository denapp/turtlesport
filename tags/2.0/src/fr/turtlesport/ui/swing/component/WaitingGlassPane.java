package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXBusyLabel;

/**
 * @author Denis Apparicio
 * 
 */
public class WaitingGlassPane extends LockingGlassPane {

  private JXBusyLabel busyLabel;

  private Component   previousGlassPane;

  private JFrame      frame;

  public WaitingGlassPane(JFrame frame) {
    super(new Color(255, 255, 255, 180));

    previousGlassPane = frame.getGlassPane();

    busyLabel = new JXBusyLabel(new Dimension(104, 104));
    this.frame = frame;
    setLayout(new BorderLayout());

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    centerPanel.setOpaque(false);
    centerPanel.add(busyLabel);
    add(centerPanel, BorderLayout.CENTER);
  }

  public void setBusy(final boolean isBusy) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setBusy(isBusy);
          return;
        }
      });
    }

    if (!isBusy) {
      setVisible(false);
      frame.setGlassPane(previousGlassPane);
      busyLabel.setBusy(false);
    }
    else {
      busyLabel.setBusy(true);
      frame.setGlassPane(this);
      setVisible(true);
    }
  }
}