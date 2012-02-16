package fr.turtlesport.ui.swing.component;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import fr.turtlesport.ui.swing.SwingLookAndFeel;

/**
 * @author Denis Apparicio
 * 
 */
public class JButtonCustom extends JButton {

  private Dimension preferredSize;

  public JButtonCustom() {
    super();
  }

  public JButtonCustom(Action a) {
    super(a);
  }

  public JButtonCustom(Icon icon) {
    super(icon);
  }

  public JButtonCustom(String text, Icon icon) {
    super(text, icon);
  }

  public JButtonCustom(String text) {
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
    super.setPreferredSize(preferredSize);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JPanel#updateUI()
   */
  @Override
  public void updateUI() {
    // Probleme avec les boutons prev et next en CDE/Motif.
    if (SwingLookAndFeel.isLookAndFeelMotif()) {
      super.setPreferredSize(null);
    }
    else {
      if (preferredSize != null) {
        super.setPreferredSize(preferredSize);
      }
    }
    super.updateUI();
  }

}
