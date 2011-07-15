package fr.turtlesport.ui.swing;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;

public class JPanelWorkoutStepTime extends JPanel {

  /**
   * Create the panel.
   */
  public JPanelWorkoutStepTime() {
    JLabel jLabelTime = new JLabel("Combien de temps ?");
    jLabelTime.setFont(GuiFont.FONT_PLAIN);
    add(jLabelTime);
    setLayout(new FlowLayout(FlowLayout.LEFT));
    
    JSpinner jSpinnerTime = new JSpinner();
    jSpinnerTime.setFont(GuiFont.FONT_PLAIN);

    add(jSpinnerTime);
  }

}
