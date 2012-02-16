package fr.turtlesport.ui.swing.component;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import fr.turtlesport.ui.swing.SwingLookAndFeel;

/**
 * Probleme de taille avec les boutons en CDE/Motif
 * 
 * @author Denis Apparicio
 * 
 */
public class JButtonDim extends JButton {
  private Dimension preferredSize;

  private Dimension minimumSize;

  public JButtonDim() {
    super();
  }

  public JButtonDim(Action a) {
    super(a);
  }

  public JButtonDim(Icon icon) {
    super(icon);
  }

  public JButtonDim(String text, Icon icon) {
    super(text, icon);
  }

  public JButtonDim(String text) {
    super(text);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
   */
  @Override
  public void setPreferredSize(Dimension preferredSize) {
    this.preferredSize = preferredSize;
    if (!SwingLookAndFeel.isLookAndFeelMotif()) {
      super.setPreferredSize(preferredSize);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#setMinimumSize(java.awt.Dimension)
   */
  @Override
  public void setMinimumSize(Dimension minimumSize) {
    this.minimumSize = minimumSize;
    if (!SwingLookAndFeel.isLookAndFeelMotif()) {
      super.setMinimumSize(minimumSize);
    }
  }

  @Override
  public void updateUI() {
    if (!SwingLookAndFeel.isLookAndFeelMotif()) {
      if (preferredSize != null) {
        super.setPreferredSize(preferredSize);
      }
      if (minimumSize != null) {
        super.setMinimumSize(minimumSize);
      }
    }
    else {
      if (preferredSize != null) {
        super.setPreferredSize(null);
      }
      if (minimumSize != null) {
        super.setMinimumSize(null);
      }
    }
    super.updateUI();
  }

}
