package fr.turtlesport.ui.swing;

import java.awt.FlowLayout;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField;

public class JPanelWorkoutGoalHeart extends JPanel {

  public JPanelWorkoutGoalHeart() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel jLabel = new JLabel("Quelle plage ?");
    jLabel.setFont(GuiFont.FONT_PLAIN);
    
    JList jListInterval = new JList();
    jListInterval.setFont(GuiFont.FONT_PLAIN);
    jListInterval.setModel(new AbstractListModel() {
      String[] values = new String[] { "Personnalisé", "1", "2" , "3"};

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });

    
    JFormattedTextField JFormattedTextFieldHeartMin = new JFormattedTextField();
    JFormattedTextFieldHeartMin.setFont(GuiFont.FONT_PLAIN);
    JLabel jLabelFrom = new JLabel("vers");
    jLabelFrom.setFont(GuiFont.FONT_PLAIN);
    JFormattedTextField JFormattedTextFieldHeartMax = new JFormattedTextField();
    JFormattedTextFieldHeartMax.setFont(GuiFont.FONT_PLAIN);

    JList jListHeart= new JList();
    jListHeart.setFont(GuiFont.FONT_PLAIN);
    jListHeart.setModel(new AbstractListModel() {
      String[] values = new String[] { "% FC maxi", "bmp"};

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });

    add(jLabel);
    add(jListInterval);
    add(JFormattedTextFieldHeartMin);
    add(jLabelFrom);
    add(JFormattedTextFieldHeartMax);
    add(jListHeart);
  }

}
