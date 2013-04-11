package fr.turtlesport.ui.swing;

import java.awt.FlowLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPanelWorkoutStepCalories extends JPanel {

  public JPanelWorkoutStepCalories() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel jLabelCalories = new JLabel("Combien de calories ?");
    jLabelCalories.setFont(GuiFont.FONT_PLAIN);

    JFormattedTextField jFormattedTextFieldCalories = new JFormattedTextField();
    jFormattedTextFieldCalories.setFont(GuiFont.FONT_PLAIN);

    add(jLabelCalories);
    add(jFormattedTextFieldCalories);
  }

}
