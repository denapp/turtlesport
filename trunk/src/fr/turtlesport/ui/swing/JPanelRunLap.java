package fr.turtlesport.ui.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelRunLap extends JPanel {
  private ResourceBundle rb;

  private JPanel         jPanelRunLap;

  private JLabel         jLabelLibDayLap;

  private JLabel         jLabelValDayLap;

  private JLabel         jLabelLibHourLap;

  private JLabel         jLabelValHourLap;

  private JLabel         jLabelLibDistanceLap;

  private JLabel         jLabelValDistanceLap;

  private JLabel         jLabelValTimeTotLap;

  private JLabel         jLabelLibTimeTotLap;

  private JLabel         jLabelLibTimePauseLap;

  private JLabel         jLabelValTimePauseLap;

  private JLabel         jLabelLibTimeMovingLap;

  private JLabel         jLabelValTimeMovingLap;

  private JLabel         jLabelLibPaceLap;

  private JLabel         jLabelValPaceLap;

  private JLabel         jLabelLibSpeedLap;

  private JLabel         jLabelValSpeedLap;

  private JLabel         jLabelLibCaloriesLap;

  private JLabel         jLabelValCaloriesLap;

  private JLabel         jLabelValAltitudeLap;

  private JLabel         jLabelLibHeartLap;

  private JLabel         jLabelValHeartLap;

  private JPanel         jPanelRunSummary;

  private TitledBorder   borderPanelRunSummary;

  private JLabel         jLabelValDateTime;

  private JLabel         jLabelLibDistTot;

  private JLabel         jLabelValDistTot;

  private JLabel         jLabelLibTimeTot;

  private JLabel         jLabelValTimeTot;

  private JLabel         jLabelLibTimePauseTot;

  private JLabel         jLabelValTimePauseTot;

  private JLabel         jLabelLibTimeMovingTot;

  private JLabel         jLabelValTimeMovingTot;

  private JLabel         jLabelLibAllureTot;

  private JLabel         jLabelValAllureTot;

  private JLabel         jLabelLibSpeedMoyTot;

  private JLabel         jLabelValSpeedMoyTot;

  private JLabel         jLabelLibCaloriesTot;

  private JLabel         jLabelValCaloriesTot;

  private JLabel         jLabelLibHeartTot;

  private JLabel         jLabelValHeartTot;

  private JLabel         jLabelLibActivity;

  private JLabel         jLabelValActivity;

  private JLabel         jLabelLibEquipment;

  private JLabel         jLabelValEquipment;

  private JLabel         jLabelLibAltitudeTot;

  private JLabel         jLabelValAltitudeTot;

  private JLabel         jLabelLibAltitudeLap;

  private JLabel         jLabelLibLap;

  private JLabel         jLabelLibLocation;

  private JLabel         jLabelValLocation;

  private JComboBox      jComboBoxLap;

  public JPanelRunLap() {
    super();
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), JDialogMap.class);

    initialize();
  }

  private void initialize() {
    Dimension dim = new Dimension(260, 600);
    setPreferredSize(dim);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(getJPanelRunSummary());
    add(Box.createRigidArea(new Dimension(5, 0)));
    add(getJPanelRunLap());
  }

  public JLabel getjLabelValDateTime() {
    return jLabelValDateTime;
  }

  public JComboBox getJComboBoxLap() {
    return jComboBoxLap;
  }

  public JLabel getJLabelValDayLap() {
    return jLabelValDayLap;
  }

  public JLabel getJLabelValHourLap() {
    return jLabelValHourLap;
  }

  public JLabel getJLabelValDistanceLap() {
    return jLabelValDistanceLap;
  }

  public JLabel getJLabelValTimeTotLap() {
    return jLabelValTimeTotLap;
  }

  public JLabel getjLabelValTimePauseLap() {
    return jLabelValTimePauseLap;
  }

  public JLabel getjLabelValTimeMovingLap() {
    return jLabelValTimeMovingLap;
  }

  public JLabel getJLabelValPaceLap() {
    return jLabelValPaceLap;
  }

  public JLabel getJLabelValSpeedLap() {
    return jLabelValSpeedLap;
  }

  public JLabel getJLabelValCaloriesLap() {
    return jLabelValCaloriesLap;
  }

  public JLabel getJLabelValAltitudeLap() {
    return jLabelValAltitudeLap;
  }

  public JLabel getJLabelValHeartLap() {
    return jLabelValHeartLap;
  }

  public JLabel getJLabelValDistTot() {
    return jLabelValDistTot;
  }

  public JLabel getJLabelValTimeTot() {
    return jLabelValTimeTot;
  }

  public JLabel getjLabelValTimePauseTot() {
    return jLabelValTimePauseTot;
  }

  public JLabel getjLabelValTimeMovingTot() {
    return jLabelValTimeMovingTot;
  }

  public JLabel getJLabelValAllureTot() {
    return jLabelValAllureTot;
  }

  public JLabel getJLabelValSpeedMoyTot() {
    return jLabelValSpeedMoyTot;
  }

  public JLabel getJLabelValCaloriesTot() {
    return jLabelValCaloriesTot;
  }

  public JLabel getJLabelValHeartTot() {
    return jLabelValHeartTot;
  }

  public JLabel getJLabelValActivity() {
    return jLabelValActivity;
  }

  public JLabel getJLabelValEquipment() {
    return jLabelValEquipment;
  }

  public JLabel getJLabelValAltitudeTot() {
    return jLabelValAltitudeTot;
  }

  public JLabel getjLabelValLocation() {
    return jLabelValLocation;
  }

  /**
   * This method initializes jPanelRunSummary.
   * 
   * 
   * @return javax.swing.JPanel
   */
  public JPanel getJPanelRunSummary() {
    if (jPanelRunSummary == null) {
      jPanelRunSummary = new JPanel();
      jPanelRunSummary.setLayout(new GridBagLayout());
      borderPanelRunSummary = BorderFactory
          .createTitledBorder(null,
                              rb.getString("jPanelRunSummary"),
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelRunSummary.setBorder(borderPanelRunSummary);
      jPanelRunSummary.setPreferredSize(new Dimension(260, 290));

      Insets insets = new Insets(0, 0, 5, 10);
      GridBagConstraints g = new GridBagConstraints();

      // Ligne 0
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.VERTICAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDateTime = new JLabel("");
      jLabelValDateTime.setFont(GuiFont.FONT_ITALIC);
      jLabelValDateTime.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelValDateTime, g);

      // Ligne 1
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDistTot = new JLabel(rb.getString("jLabelLibDistTot"));
      jLabelLibDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibDistTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDistTot = new JLabel();
      jLabelValDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setLabelFor(jLabelValDistTot);
      jPanelRunSummary.add(jLabelValDistTot, g);

      // Ligne 2
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeTot = new JLabel(rb.getString("jLabelLibTimeTot"));
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibTimeTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimeTot = new JLabel();
      jLabelValTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setLabelFor(jLabelValTimeTot);
      jPanelRunSummary.add(jLabelValTimeTot, g);

      // Ligne 2
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeMovingTot = new JLabel(rb.getString("jLabelLibTimeMovingTot"));
      jLabelLibTimeMovingTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeMovingTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibTimeMovingTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimeMovingTot = new JLabel();
      jLabelValTimeMovingTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeMovingTot.setLabelFor(jLabelLibTimeMovingTot);
      jPanelRunSummary.add(jLabelValTimeMovingTot, g);

      // Ligne 3
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimePauseTot = new JLabel(rb.getString("jLabelLibTimePauseTot"));
      jLabelLibTimePauseTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimePauseTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibTimePauseTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimePauseTot = new JLabel();
      jLabelValTimePauseTot.setFont(GuiFont.FONT_PLAIN);
      jLabelValTimePauseTot.setLabelFor(jLabelLibTimePauseTot);
      jPanelRunSummary.add(jLabelValTimePauseTot, g);

      // Ligne 3
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAllureTot = new JLabel(rb.getString("jLabelLibAllure"));
      jLabelLibAllureTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllureTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAllureTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAllureTot = new JLabel();
      jLabelValAllureTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllureTot.setLabelFor(jLabelValAllureTot);
      jPanelRunSummary.add(jLabelValAllureTot, g);

      // Ligne 4
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibSpeedMoyTot = new JLabel(rb.getString("jLabelLibSpeedMoy"));
      jLabelLibSpeedMoyTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoyTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibSpeedMoyTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValSpeedMoyTot = new JLabel();
      jLabelValSpeedMoyTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoyTot.setLabelFor(jLabelValSpeedMoyTot);
      jPanelRunSummary.add(jLabelValSpeedMoyTot, g);

      // Ligne 5
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibCaloriesTot = new JLabel(rb.getString("jLabelLibCaloriesTot"));
      jLabelLibCaloriesTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibCaloriesTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValCaloriesTot = new JLabel();
      jLabelValCaloriesTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesTot.setLabelFor(jLabelValCaloriesTot);
      jPanelRunSummary.add(jLabelValCaloriesTot, g);

      // Ligne 6
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHeartTot = new JLabel(rb.getString("jLabelLibHeartTot"));
      jLabelLibHeartTot.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibHeartTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibHeartTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHeartTot = new JLabel();
      jLabelValHeartTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartTot.setLabelFor(jLabelValHeartTot);
      jPanelRunSummary.add(jLabelValHeartTot, g);

      // Ligne 7
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAltitudeTot = new JLabel(rb.getString("jLabelLibAltitudeTot"));
      jLabelLibAltitudeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAltitudeTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAltitudeTot = new JLabel();
      jLabelValAltitudeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeTot.setLabelFor(jLabelValAltitudeTot);
      jPanelRunSummary.add(jLabelValAltitudeTot, g);

      // Ligne 8
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibActivity = new JLabel(rb.getString("jLabelLibCategory"));
      jLabelLibActivity.setFont(GuiFont.FONT_PLAIN);
      jLabelLibActivity.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibActivity, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValActivity = new JLabel();
      jLabelValActivity.setFont(GuiFont.FONT_PLAIN);
      g.anchor = GridBagConstraints.WEST;
      jLabelLibActivity.setLabelFor(jLabelValActivity);
      jPanelRunSummary.add(jLabelValActivity, g);

      // Ligne 9
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibEquipment = new JLabel(rb.getString("jLabelLibEquipment"));
      jLabelLibEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibEquipment, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      g.anchor = GridBagConstraints.WEST;
      jLabelValEquipment = new JLabel();
      jLabelValEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setLabelFor(jLabelValEquipment);
      jPanelRunSummary.add(jLabelValEquipment, g);

      // Ligne 10
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibLocation = new JLabel(rb.getString("jLabelLibLocation"));
      jLabelLibLocation.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLocation.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibLocation, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      g.anchor = GridBagConstraints.WEST;
      jLabelValLocation = new JLabel();
      jLabelValLocation.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLocation.setLabelFor(jLabelValLocation);
      jPanelRunSummary.add(jLabelValLocation, g);
    }
    return jPanelRunSummary;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  public JPanel getJPanelRunLap() {
    if (jPanelRunLap == null) {
      jPanelRunLap = new JPanel();
      jPanelRunLap.setOpaque(true);

      TitledBorder borderPanelRunLap = BorderFactory
          .createTitledBorder(null,
                              rb.getString("borderPanelRunLap"),
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelRunLap.setBorder(borderPanelRunLap);
      jPanelRunLap.setPreferredSize(new Dimension(260, 310));
      jPanelRunLap.setLayout(new GridBagLayout());

      Insets insets = new Insets(0, 0, 5, 10);
      GridBagConstraints g = new GridBagConstraints();

      // Ligne1
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibLap = new JLabel();
      jLabelLibLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLap.setText(rb.getString("jLabelLibLap"));
      jLabelLibLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jComboBoxLap = new JComboBox();
      jComboBoxLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLap.setLabelFor(jComboBoxLap);
      jPanelRunLap.add(jComboBoxLap, g);

      // Ligne 2
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDayLap = new JLabel();
      jLabelLibDayLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDayLap.setText(rb.getString("JLabelLibDayLap"));
      jLabelLibDayLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibDayLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDayLap = new JLabel();
      jLabelValDayLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDayLap.setLabelFor(jLabelValDayLap);
      jPanelRunLap.add(jLabelValDayLap, g);

      // Ligne 3
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHourLap = new JLabel();
      jLabelLibHourLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHourLap.setText(rb.getString("jLabelLibHourLap"));
      jLabelLibHourLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibHourLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHourLap = new JLabel();
      jLabelValHourLap.setFont(GuiFont.FONT_PLAIN);
      jLabelValHourLap.setLabelFor(jLabelLibDayLap);
      jLabelLibHourLap.setLabelFor(jLabelValHourLap);
      jPanelRunLap.add(jLabelValHourLap, g);

      // Ligne 4
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDistanceLap = new JLabel();
      jLabelLibDistanceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistanceLap.setText(rb.getString("jLabelLibDistanceLap"));
      jLabelLibDistanceLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibDistanceLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDistanceLap = new JLabel();
      jLabelValDistanceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistanceLap.setLabelFor(jLabelValDistanceLap);
      jPanelRunLap.add(jLabelValDistanceLap, g);

      // Ligne 5
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeTotLap = new JLabel();
      jLabelLibTimeTotLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTotLap.setText(rb.getString("jLabelLibTimeTot"));
      jLabelLibTimeTotLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibTimeTotLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimeTotLap = new JLabel();
      jLabelValTimeTotLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTotLap.setLabelFor(jLabelValTimeTotLap);
      jPanelRunLap.add(jLabelValTimeTotLap, g);

      // Ligne 5
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimePauseLap = new JLabel();
      jLabelLibTimePauseLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimePauseLap.setText(rb.getString("jLabelLibTimePauseTot"));
      jLabelLibTimePauseLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibTimePauseLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimePauseLap = new JLabel();
      jLabelValTimePauseLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimePauseLap.setLabelFor(jLabelValTimePauseLap);
      jPanelRunLap.add(jLabelValTimePauseLap, g);

      // Ligne 5
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeMovingLap = new JLabel();
      jLabelLibTimeMovingLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeMovingLap.setText(rb.getString("jLabelLibTimeMovingTot"));
      jLabelLibTimeMovingLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibTimeMovingLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimeMovingLap = new JLabel();
      jLabelValTimeMovingLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeMovingLap.setLabelFor(jLabelValTimeMovingLap);
      jPanelRunLap.add(jLabelValTimeMovingLap, g);

      // Ligne 6
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibPaceLap = new JLabel();
      jLabelLibPaceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibPaceLap.setText(rb.getString("jLabelLibPaceLap"));
      jLabelLibPaceLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibPaceLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValPaceLap = new JLabel();
      jLabelValPaceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibPaceLap.setLabelFor(jLabelValPaceLap);
      jPanelRunLap.add(jLabelValPaceLap, g);

      // Ligne 7
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibSpeedLap = new JLabel();
      jLabelLibSpeedLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedLap.setText(rb.getString("jLabelLibSpeedLap"));
      jLabelLibSpeedLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibSpeedLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValSpeedLap = new JLabel();
      jLabelValSpeedLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedLap.setLabelFor(jLabelValSpeedLap);
      jPanelRunLap.add(jLabelValSpeedLap, g);

      // Ligne 8
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHeartLap = new JLabel();
      jLabelLibHeartLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartLap.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibHeartLap.setText(rb.getString("jLabelLibHeartLap"));
      jLabelLibHeartLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibHeartLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHeartLap = new JLabel();
      jLabelValHeartLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartLap.setLabelFor(jLabelValHeartLap);
      jPanelRunLap.add(jLabelValHeartLap, g);

      // Ligne 9
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibCaloriesLap = new JLabel();
      jLabelLibCaloriesLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesLap.setText(rb.getString("jLabelLibCaloriesLap"));
      jLabelLibCaloriesLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibCaloriesLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValCaloriesLap = new JLabel();
      jLabelValCaloriesLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesLap.setLabelFor(jLabelValCaloriesLap);
      jPanelRunLap.add(jLabelValCaloriesLap, g);

      // Ligne 10
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAltitudeLap = new JLabel();
      jLabelLibAltitudeLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeLap.setText(rb.getString("jLabelLibAltitudeLap"));
      jLabelLibAltitudeLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibAltitudeLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAltitudeLap = new JLabel();
      jLabelValAltitudeLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeLap.setLabelFor(jLabelValAltitudeLap);
      jPanelRunLap.add(jLabelValAltitudeLap, g);
    }
    return jPanelRunLap;
  }

}
