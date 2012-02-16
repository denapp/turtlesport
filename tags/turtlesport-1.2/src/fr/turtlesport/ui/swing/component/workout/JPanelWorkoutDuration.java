package fr.turtlesport.ui.swing.component.workout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import fr.turtlesport.ui.swing.GuiFont;

public class JPanelWorkoutDuration extends JPanel {

  private JPanel[] panels;

  /**
   * Create the panel.
   */
  public JPanelWorkoutDuration() {

    setLayout(new BorderLayout());

    JPanel jpanelNorth = new JPanel();
    jpanelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel jLabel = new JLabel("Dur\u00E9e :");
    jLabel.setFont(GuiFont.FONT_PLAIN);
    jLabel.setHorizontalAlignment(SwingConstants.CENTER);
    jpanelNorth.add(jLabel);

    String[] values = { "Appui sur la touche Lap",
        "Temps",
        "Distance",
        "Calories",
        "Frequence cardiaque" };
    panels = new JPanel[values.length];

    final JComboBox jComboBox = new JComboBox(values);
    jComboBox.setFont(GuiFont.FONT_PLAIN);
    jLabel.setLabelFor(jComboBox);
    jpanelNorth.add(jComboBox);

    add(jpanelNorth, BorderLayout.NORTH);
    add(new JPanelLap(), BorderLayout.CENTER);

    // Evenements
    jComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = jComboBox.getSelectedIndex();
        JPanelWorkoutDuration.this.remove(1);
        JPanelWorkoutDuration.this.add(getPanel(index), BorderLayout.CENTER);
        JPanelWorkoutDuration.this.validate();
      }
    });
  }

  private JPanel getPanel(int index) {
    if (panels[index] == null) {
      switch (index) {
        case 0:
          panels[index] = new JPanelLap();
          break;
        case 1:
          panels[index] = new JPanelWorkoutDurationTime();
          break;
        case 2:
          panels[index] = new JPanelWorkoutDurationDist();
          break;
        case 3:
          panels[index] = new JPanelWorkoutDurationCalorie();
          break;
        case 4:
          panels[index] = new JPanelWorkoutDurationHeart();
          break;
      }
    }
    return panels[index];
  }

  private static class JPanelLap extends JPanel {
    public JPanelLap() {
      super();
      setLayout(new FlowLayout());
      add(new JLabel("  "));
    }
  }

}
