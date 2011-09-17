package fr.turtlesport.ui.swing;

import java.awt.FlowLayout;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public class JPanelWorkoutGoalSpeed extends JPanel {

  public JPanelWorkoutGoalSpeed() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel jLabel = new JLabel("Quelle plage ?");
    jLabel.setFont(GuiFont.FONT_PLAIN);
    
    JList jList = new JList();
    jList.setFont(GuiFont.FONT_PLAIN);
    jList.setModel(new AbstractListModel() {
      String[] values = new String[] { "Personnalisé", "1- MARCHE LENTE", "2- MARCHE" , "3- MARCHE RAPIDE"};

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });


    JSpinner jSpinnerSpeedMin = new JSpinner();
    jSpinnerSpeedMin.setFont(GuiFont.FONT_PLAIN);
    
    JLabel jLabelFrom = new JLabel("vers");
    jLabelFrom.setFont(GuiFont.FONT_PLAIN);

    JSpinner jSpinnerSpeedMax= new JSpinner();
    jSpinnerSpeedMax.setFont(GuiFont.FONT_PLAIN);

    JList jListUnit = new JList();
    jListUnit.setFont(GuiFont.FONT_PLAIN);
    jListUnit.setModel(new AbstractListModel() {
      String[] values = new String[] { "/km", "/mile"};

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });

    add(jLabel);
    add(jList);
    add(jSpinnerSpeedMin);
    add(jLabelFrom);
    add(jSpinnerSpeedMax);
    add(jListUnit);
  }

}
