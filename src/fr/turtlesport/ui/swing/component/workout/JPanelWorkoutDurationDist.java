package fr.turtlesport.ui.swing.component.workout;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;

import fr.turtlesport.ui.swing.GuiFont;

public class JPanelWorkoutDurationDist extends JPanel {
  private JTextField jTextField;

  /**
   * Create the panel.
   */
  public JPanelWorkoutDurationDist() {
    FlowLayout flowLayout = (FlowLayout) getLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    
    jTextField = new JTextField();
    jTextField.setFont(GuiFont.FONT_PLAIN);
    add(jTextField);
    jTextField.setColumns(10);
    
    JLabel jLabelUnit = new JLabel("km");
    jLabelUnit.setFont(GuiFont.FONT_PLAIN);
    add(jLabelUnit);

  }

}
