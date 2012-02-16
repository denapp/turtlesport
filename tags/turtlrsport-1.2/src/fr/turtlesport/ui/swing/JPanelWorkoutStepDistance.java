package fr.turtlesport.ui.swing;

import java.awt.FlowLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPanelWorkoutStepDistance extends JPanel {

  public JPanelWorkoutStepDistance() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel jLabelDistance = new JLabel("Combien de temps ?");
    jLabelDistance.setFont(GuiFont.FONT_PLAIN);

    JFormattedTextField jFormattedTextFieldDistance = new JFormattedTextField();
    jFormattedTextFieldDistance.setFont(GuiFont.FONT_PLAIN);

    JLabel jLabelUnit = new JLabel("km");
    jLabelUnit.setFont(GuiFont.FONT_PLAIN);

    add(jLabelDistance);
    add(jFormattedTextFieldDistance);
    add(jLabelUnit);
  }

}
