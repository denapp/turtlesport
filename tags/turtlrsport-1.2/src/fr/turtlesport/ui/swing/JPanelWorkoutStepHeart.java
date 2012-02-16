package fr.turtlesport.ui.swing;

import java.awt.FlowLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class JPanelWorkoutStepHeart extends JPanel {

  public JPanelWorkoutStepHeart() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel jLabelHeart = new JLabel("Quelle fréquence cardiaque ?");
    jLabelHeart.setFont(GuiFont.FONT_PLAIN);

    JFormattedTextField jFormattedTextFieldDistance = new JFormattedTextField();
    jFormattedTextFieldDistance.setFont(GuiFont.FONT_PLAIN);

    JList jList = new JList();
    jList.setFont(GuiFont.FONT_PLAIN);
    jList.setModel(new AbstractListModel() {
      String[] values = new String[] { "% FC maxi", "bmp" };

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });

    add(jLabelHeart);
    add(jFormattedTextFieldDistance);
    add(jList);
  }

}
