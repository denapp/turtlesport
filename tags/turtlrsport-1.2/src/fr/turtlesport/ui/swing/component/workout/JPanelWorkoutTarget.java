package fr.turtlesport.ui.swing.component.workout;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import fr.turtlesport.ui.swing.GuiFont;

public class JPanelWorkoutTarget extends JPanel {

  /**
   * Create the panel.
   */
  public JPanelWorkoutTarget() {
    FlowLayout flowLayout = (FlowLayout) getLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    
    JLabel jLabel = new JLabel("Cible :");
    jLabel.setFont(GuiFont.FONT_PLAIN);
    jLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(jLabel);
    
    String[] values ={"Pas de cible", "Zone de fr\u00E9equence cardiaque", "Vitesse", "Allure", "Cadence"};
    final JComboBox jComboBox = new JComboBox(values);
    jComboBox.setFont(GuiFont.FONT_PLAIN);
    jLabel.setLabelFor(jComboBox);
    add(jComboBox);
    
    //Evenements
    jComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {        
        int index = jComboBox.getSelectedIndex();
      }
    });
  }

}
