package fr.turtlesport.ui.swing.component.workout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fr.turtlesport.ui.swing.GuiFont;

public class JPanelWorkoutTitle extends JPanel {
  private JTextField jTextFieldName;

  private JTextArea  jTextFieldNotes;

  private JComboBox  jComboBoxSportType;

  /**
   * Create the panel.
   */
  public JPanelWorkoutTitle() {
    setBorder(new TitledBorder(null,
                               "",
                               TitledBorder.LEADING,
                               TitledBorder.TOP,
                               null,
                               null));
    setLayout(new BorderLayout(0, 0));

    JPanel jPanelNorth = new JPanel();
    FlowLayout fl_jPanelNorth = (FlowLayout) jPanelNorth.getLayout();
    fl_jPanelNorth.setAlignment(FlowLayout.LEFT);
    add(jPanelNorth, BorderLayout.NORTH);

    JLabel jLabelName = new JLabel("Nom :");
    jLabelName.setFont(GuiFont.FONT_PLAIN);
    jPanelNorth.add(jLabelName);

    jTextFieldName = new JTextField();
    jTextFieldName.setFont(GuiFont.FONT_PLAIN);
    jLabelName.setLabelFor(jTextFieldName);
    jPanelNorth.add(jTextFieldName);
    jTextFieldName.setColumns(10);

    JLabel jLabelSportType = new JLabel("Sport :");
    jLabelSportType.setFont(GuiFont.FONT_PLAIN);
    jPanelNorth.add(jLabelSportType);

    String[] values = { "Course a pied", "Velo", "Autre Sport" };
    jComboBoxSportType = new JComboBox(values);
    jComboBoxSportType.setFont(GuiFont.FONT_PLAIN);
    jPanelNorth.add(jComboBoxSportType);

    JPanel jPanelCenter = new JPanel();
    FlowLayout flowLayout = (FlowLayout) jPanelCenter.getLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    add(jPanelCenter, BorderLayout.CENTER);

    JLabel jLabelNotes = new JLabel("Remarques :");
    jLabelNotes.setFont(GuiFont.FONT_PLAIN);
    jLabelNotes.setVerticalAlignment(SwingConstants.TOP);
    jPanelCenter.add(jLabelNotes);

    jTextFieldNotes = new JTextArea();
    jTextFieldNotes.setLineWrap(true);
    jTextFieldNotes.setWrapStyleWord(true);
    jTextFieldNotes.setFont(GuiFont.FONT_PLAIN);
    jTextFieldNotes.setColumns(50);
    jTextFieldNotes.setRows(5);
    jLabelNotes.setLabelFor(jTextFieldNotes);
    JScrollPane jScrollPaneTextArea = new JScrollPane(jTextFieldNotes);
    jScrollPaneTextArea
        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollPaneTextArea
        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    jPanelCenter.add(jScrollPaneTextArea);
  }

  public JTextField getjTextFieldName() {
    return jTextFieldName;
  }

  public JTextArea getjTextFieldNotes() {
    return jTextFieldNotes;
  }

  public JComboBox getjComboBoxSportType() {
    return jComboBoxSportType;
  }

}
