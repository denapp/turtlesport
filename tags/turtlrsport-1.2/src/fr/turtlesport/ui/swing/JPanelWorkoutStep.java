package fr.turtlesport.ui.swing;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class JPanelWorkoutStep extends JPanel {
  private JTextField   jTextFieldName;

  private JPanel       jPanelOption;

  private JLabel       jLabelName;

  private JCheckBox    jCheckBoxRest;

  private JPanel       jPanelStepGoal;

  private TitledBorder titledBorderStepTime;

  private JList        jListStep;

  private JPanel       jPanelStepGoalList;

  private JList        jListGoal;

  /**
   * Create the panel.
   */
  public JPanelWorkoutStep() {
    setLayout(new BorderLayout(0, 0));
    setFont(GuiFont.FONT_PLAIN);

    jPanelOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(jPanelOption, BorderLayout.NORTH);

    jLabelName = new JLabel("Nom :");
    jLabelName.setFont(GuiFont.FONT_PLAIN);
    jPanelOption.add(jLabelName);

    jTextFieldName = new JTextField();
    jTextFieldName.setFont(GuiFont.FONT_PLAIN);
    jPanelOption.add(jTextFieldName);
    jTextFieldName.setColumns(10);

    jCheckBoxRest = new JCheckBox("Phase de repos :");
    jCheckBoxRest.setFont(GuiFont.FONT_PLAIN);
    jPanelOption.add(jCheckBoxRest);

    JPanel jPanelCenter = new JPanel();
    add(jPanelCenter, BorderLayout.CENTER);
    jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.Y_AXIS));

    JPanel jPanelStepTime = new JPanel();
    titledBorderStepTime = new TitledBorder(null,
                                            "Dur\u00E9e de la phase",
                                            TitledBorder.LEADING,
                                            TitledBorder.TOP,
                                            GuiFont.FONT_PLAIN,
                                            null);
    jPanelStepTime.setBorder(titledBorderStepTime);
    jPanelCenter.add(jPanelStepTime);
    jPanelStepTime.add(new JPanelWorkoutStepDistance(), BorderLayout.CENTER);
    jPanelStepTime.setLayout(new BorderLayout(0, 0));

    JPanel jPanelStepTimeList = new JPanel(new FlowLayout(FlowLayout.LEFT));
    jPanelStepTime.add(jPanelStepTimeList, BorderLayout.NORTH);
    JLabel jLabelTimeStep = new JLabel("Quand cette phase doit elle prendre fin ?");
    jLabelTimeStep.setFont(GuiFont.FONT_PLAIN);
    jPanelStepTimeList.add(jLabelTimeStep);

    jListStep = new JList();
    jListStep.setFont(GuiFont.FONT_PLAIN);
    jListStep.setModel(new AbstractListModel() {
      String[] values = new String[] { "Lorsque j'aurai brulé un certain nombre de calories",
                          "Lorsque j'aurais parcouru une certaine distance" };

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });
    jPanelStepTimeList.add(jListStep);

    jPanelStepGoal = new JPanel();
    jPanelCenter.add(jPanelStepGoal);
    jPanelStepGoal.setLayout(new BorderLayout(0, 0));

    jPanelStepGoalList = new JPanel(new FlowLayout(FlowLayout.LEFT));
    jPanelStepGoal.add(jPanelStepGoalList, BorderLayout.NORTH);
    jPanelStepGoal.add(new JPanelWorkoutGoalSpeed(), BorderLayout.CENTER);
    JPanel jPanelStepGoalList = new JPanel(new FlowLayout(FlowLayout.LEFT));
    jPanelStepGoal.add(jPanelStepGoalList, BorderLayout.NORTH);
    JLabel jLabelGoal = new JLabel("Quelle est l'objectif de cette phase ?");
    jLabelGoal.setFont(GuiFont.FONT_PLAIN);
    jPanelStepGoalList.add(jLabelGoal);

    jListGoal = new JList();
    jListGoal.setFont(GuiFont.FONT_PLAIN);
    jListGoal.setModel(new AbstractListModel() {
      String[] values = new String[] { "Essayer de maintenir ma vitesse dans une certaine plage ?",
                          "Essayer de maintenir ma cadence à un certain niveau" };

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });

    jPanelStepGoalList.add(jListGoal);
  }

}
