package fr.turtlesport.ui.swing.component.workout;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.turtlesport.ui.swing.GuiFont;

public class JPanelWorkoutTargetHeart extends JPanel {
  private JTextField jTextField1;
  private JTextField jTextField2;
  private JLabel jLabelPlage;
  private JComboBox comboBox;

  /**
   * Create the panel.
   */
  public JPanelWorkoutTargetHeart() {
    FlowLayout flowLayout = (FlowLayout) getLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    
    jLabelPlage = new JLabel("Plage");
    jLabelPlage.setFont(GuiFont.FONT_PLAIN);
    add(jLabelPlage);
    
    comboBox = new JComboBox();
    comboBox.setFont(GuiFont.FONT_PLAIN);
    add(comboBox);
    
    jTextField1 = new JTextField();
    jTextField1.setFont(GuiFont.FONT_PLAIN);
    add(jTextField1);
    jTextField1.setColumns(10);
    
    JLabel jLabelTo = new JLabel("\u00E0");
    jLabelTo.setFont(GuiFont.FONT_PLAIN);
    add(jLabelTo);
    
    jTextField2 = new JTextField();
    jTextField2.setFont(GuiFont.FONT_PLAIN);
    add(jTextField2);
    jTextField2.setColumns(10);
    
    JLabel jLabelCadence = new JLabel("ppm");
    jLabelCadence.setFont(GuiFont.FONT_PLAIN);
    add(jLabelCadence);

  }

}
