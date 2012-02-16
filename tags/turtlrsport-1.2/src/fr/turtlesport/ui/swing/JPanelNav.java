package fr.turtlesport.ui.swing;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelNav extends JPanel {

  private static final Dimension DIM_BUTTON = new Dimension(16, 16);

  private JButton                jButtonNext;

  private JButton                jButtonPrev;
  
  private Dimension dim = new Dimension(5,1);

  /**
   * 
   */
  public JPanelNav() {
    super();
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JPanel#updateUI()
   */
  @Override
  public void updateUI() {
    if (jButtonPrev == null) {
      return;
    }
    // Probleme avec les boutons prev et next en CDE/Motif.
    if (SwingLookAndFeel.isLookAndFeelMotif()) {
      jButtonPrev.setPreferredSize(null);
      jButtonNext.setPreferredSize(null);
      dim.setSize(0, 0);
    }
    else {
      jButtonPrev.setPreferredSize(DIM_BUTTON);
      jButtonNext.setPreferredSize(DIM_BUTTON);
      dim.setSize(5, 0);
    }
    super.updateUI();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {        
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
    setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
    add(Box.createHorizontalGlue());
    add(getJButtonPrev(), null);
    add(Box.createRigidArea(dim));
    add(getJButtonNext(), null);
  }

  /**
   * This method initializes jButtonPrev.
   * 
   * @return javax.swing.JButton
   */
  public JButton getJButtonPrev() {
    if (jButtonPrev == null) {
      jButtonPrev = new JButton(ImagesRepository.getImageIcon("prev.gif"));
      jButtonPrev.setEnabled(false);
      jButtonPrev.setPreferredSize(DIM_BUTTON);
    }
    return jButtonPrev;
  }

  /**
   * This method initializes jButtonNext.
   * 
   * @return javax.swing.JButton
   */
  public JButton getJButtonNext() {
    if (jButtonNext == null) {
      jButtonNext = new JButton(ImagesRepository.getImageIcon("next.gif"));
      jButtonNext.setEnabled(false);
      jButtonNext.setPreferredSize(DIM_BUTTON);
    }
    return jButtonNext;
  }

}
