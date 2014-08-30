package fr.turtlesport.ui.swing;

import java.awt.FlowLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPanelWorkoutGoalCadence extends JPanel {

  public JPanelWorkoutGoalCadence() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel jLabelCadence = new JLabel(" Cadence :");
    jLabelCadence.setFont(GuiFont.FONT_PLAIN);

    
    JFormattedTextField JFormattedTextFieldMin = new JFormattedTextField();
    JFormattedTextFieldMin.setFont(GuiFont.FONT_PLAIN);
    JLabel jLabelFrom = new JLabel("vers");
    jLabelFrom.setFont(GuiFont.FONT_PLAIN);
    JFormattedTextField JFormattedTextFieldMax = new JFormattedTextField();
    JFormattedTextFieldMax.setFont(GuiFont.FONT_PLAIN);
    JLabel jLabelFreq = new JLabel("tr/mn");
    jLabelFreq.setFont(GuiFont.FONT_PLAIN);

    add(jLabelCadence);
    add(JFormattedTextFieldMin);
    add(jLabelFrom);
    add(JFormattedTextFieldMax);   
    add(jLabelFreq);
  }

}
