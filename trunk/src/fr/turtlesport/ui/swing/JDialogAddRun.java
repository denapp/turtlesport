package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractSpinnerModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;

import fr.turtlesport.CommonLang;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.meteo.DataMeteo;
import fr.turtlesport.ui.swing.component.JComboBoxActivity;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTextAreaLength;
import fr.turtlesport.ui.swing.component.JTextFieldTime;
import fr.turtlesport.ui.swing.component.JXDatePickerLocale;
import fr.turtlesport.ui.swing.component.JXSplitButton;
import fr.turtlesport.ui.swing.component.TextFormatterFactory;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.model.ActivityComboBoxModel;
import fr.turtlesport.ui.swing.model.GenericPropertyChangeListener;
import fr.turtlesport.ui.swing.model.LocationComboBoxModel;
import fr.turtlesport.ui.swing.model.ModelAddRun;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TemperatureUnit;
import fr.turtlesport.util.ResourceBundleExt;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogAddRun extends JDialog {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogAddRun.class);
  }

  private JLabel                  jLabelLibDistTot;

  private JPanel                  jPanelSummary;

  private JFormattedTextField     jTextFieldDistTot;

  private JLabel                  jLabelLibTimeTot;

  private JLabel                  jLabelLibActivity;

  private JComboBoxActivity       jComboBoxActivity;

  private JLabel                  jLabelLibEquipment;

  private JComboBox               jComboBoxEquipment;

  private JLabel                  jLabelLibNotes;

  private JTextAreaLength         jTextFieldNotes;

  private JScrollPane             jScrollPaneTextArea;

  private JButton                 jButtonCancel;

  private JButton                 jButtonSave;

  private JComboBox               jComboBoxDistanceUnits;

  private JPanel                  jPanelButton;

  private JPanel                  jPanelContentPane;

  private JLabel                  jLabelLibDate;

  private JXDatePickerLocale      jXDatePicker;

  private JTextFieldTime          jTextFieldTime;

  private JTextFieldTime          jTextFieldTimeTot;

  private JLabel                  jLabelLibHeart;

  private JLabel                  jLabelLibCalories;

  private JFormattedTextField     jTextFieldCalories;

  private JFormattedTextField     jTextFieldHeartAvg;

  private JFormattedTextField     jTextFieldHeartMax;

  private JLabel                  jLabelLibLocation;

  private JComboBox               jComboBoxLocation;

  private JLabel                  jLabelLibMeteo;

  private JXSplitButton           jxSplitButtonImgMeteo;

  private JLabel                  jLabelTemperature;

  private JSpinner                spinner;

  /** Model */
  private ActivityComboBoxModel   modelActivities;

  private EquipementComboBoxModel modelEquipements;

  private LocationComboBoxModel   modelLocations;

  private ModelAddRun             model = new ModelAddRun();

  private TemperatureSpinnerModel spinnerModel;

  private JDialogAddRun(Frame owner) throws SQLException {
    super(owner, true);

    modelEquipements = new EquipementComboBoxModel();
    modelActivities = new ActivityComboBoxModel();

    initialize();
  }

  /**
   * @throws SQLException
   * 
   */
  public static void prompt() throws SQLException {
    JDialogAddRun dlg = new JDialogAddRun(MainGui.getWindow());
    dlg.setLocationRelativeTo(MainGui.getWindow());
    dlg.setVisible(true);
  }

  public JTextAreaLength getJTextFieldNotes() {
    return jTextFieldNotes;
  }

  public ActivityComboBoxModel getModelActivities() {
    return modelActivities;
  }

  public EquipementComboBoxModel getModelEquipements() {
    return modelEquipements;
  }

  public LocationComboBoxModel getModelLocation() {
    return modelLocations;
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(400, 520);
    this.setContentPane(getJContentPane());

    ResourceBundleExt rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());
    this.setTitle(rb.getString("title"));
    jLabelLibDate.setText(rb.getString("jLabelLibDate"));
    jButtonSave.setText(rb.getString("jButtonSave"));

    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), CommonLang.class);
    jLabelLibDistTot.setText(rb.getStringLib("Distance"));
    jLabelLibTimeTot.setText(rb.getStringLib("Time"));
    jLabelLibActivity.setText(rb.getStringLib("Activity"));
    jLabelLibEquipment.setText(rb.getStringLib("Equipment"));
    jLabelLibNotes.setText(rb.getStringLib("Notes"));
    jLabelLibCalories.setText(rb.getStringLib("Calories"));
    jLabelLibHeart.setText(rb.getStringLib("AverageMax"));
    jLabelLibLocation.setText(rb.getStringLib("Location"));
    jLabelLibMeteo.setText(rb.getStringLib("Meteo"));

    jButtonCancel.setText(LanguageManager.getManager().getCurrentLang()
        .cancel());

    // evenements
    jButtonCancel.addActionListener(new CancelActionListener());
    getRootPane().setDefaultButton(jButtonSave);
    jButtonSave.addActionListener(new SaveActionListener());
    jComboBoxDistanceUnits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        model.setUnitDistance(JDialogAddRun.this, (String) cb.getSelectedItem());
      }
    });
    jComboBoxLocation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          model.saveLocation(JDialogAddRun.this);
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }
    });

    try {
      // Distance
      GenericPropertyChangeListener lstDist = new GenericPropertyChangeListener(jTextFieldDistTot,
                                                                                model
                                                                                    .getData(),
                                                                                "setDistanceTot",
                                                                                Double.TYPE);
      jTextFieldDistTot.addPropertyChangeListener(lstDist);

      // Calories
      GenericPropertyChangeListener lstCalories = new GenericPropertyChangeListener(jTextFieldCalories,
                                                                                    model
                                                                                        .getData(),
                                                                                    "setCalories",
                                                                                    Integer.TYPE);
      jTextFieldCalories.addPropertyChangeListener("value", lstCalories);

      // Fc
      GenericPropertyChangeListener lstFcMax = new GenericPropertyChangeListener(jTextFieldHeartMax,
                                                                                 model
                                                                                     .getData(),
                                                                                 "setMaxRate",
                                                                                 Integer.TYPE);
      jTextFieldHeartMax.addPropertyChangeListener("value", lstFcMax);
      GenericPropertyChangeListener lstFcAvg = new GenericPropertyChangeListener(jTextFieldHeartAvg,
                                                                                 model
                                                                                     .getData(),
                                                                                 "setAvgRate",
                                                                                 Integer.TYPE);
      jTextFieldHeartAvg.addPropertyChangeListener("value", lstFcAvg);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }

    setDefaultCloseOperation(JDialogImport.DISPOSE_ON_CLOSE);

    // mis a jour des valeurs
    jTextFieldDistTot.setValue(10);
    jTextFieldCalories.setValue(800);
    jXDatePicker.getMonthView().setUpperBound(Calendar.getInstance().getTime());
    jXDatePicker.setDate(GregorianCalendar.getInstance().getTime());
  }

  /**
   * This method initializes jPanelSummary
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jPanelContentPane == null) {
      JLabel jLabelNorth = new JLabel("  ");
      jPanelContentPane = new JPanel();
      jPanelContentPane.setLayout(new BorderLayout(0, 5));
      jPanelContentPane.add(getJPanelSummary(), BorderLayout.CENTER);
      jPanelContentPane.add(getJPanelButton(), BorderLayout.SOUTH);
      jPanelContentPane.add(jLabelNorth, BorderLayout.NORTH);
    }
    return jPanelContentPane;
  }

  /**
   * This method initializes jPanelSummary
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelSummary() {
    if (jPanelSummary == null) {
      jPanelSummary = new JPanel();
      jPanelSummary.setLayout(new GridBagLayout());
      Insets insets = new Insets(0, 0, 5, 10);

      // Date
      GridBagConstraints g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.insets = insets;
      jLabelLibDate = new JLabel();
      jLabelLibDate.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDate.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibDate, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.WEST;
      g.insets = insets;
      jLabelLibDate.setLabelFor(getJDatePicker());
      jPanelSummary.add(getJDatePicker(), g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibDate.setLabelFor(getJDatePicker());
      jPanelSummary.add(getJTextFieldTime(), g);

      // Distance
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibDistTot = new JLabel();
      jLabelLibDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibDistTot, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      JPanel panelDist = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
      jLabelLibDistTot.setLabelFor(panelDist);
      panelDist.add(getJTextFieldDistTot());
      panelDist.add(getJComboBoxDistanceUnits());
      jPanelSummary.add(panelDist, g);

      // Temps
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibTimeTot = new JLabel();
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibTimeTot, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibTimeTot.setLabelFor(getJTextFieldTimeTot());
      jPanelSummary.add(getJTextFieldTimeTot(), g);

      // Calories
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibCalories = new JLabel();
      jLabelLibCalories.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCalories.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibCalories, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibCalories.setLabelFor(getJTextFieldCalories());
      jPanelSummary.add(jTextFieldCalories, g);

      // FC
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibHeart = new JLabel();
      jLabelLibHeart.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibHeart.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeart.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibHeart, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      JPanel panelHeart = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
      panelHeart.add(getJTextFieldHeartMoy());
      JLabel jLabelSpace = new JLabel("/");
      jLabelSpace.setFont(GuiFont.FONT_PLAIN);
      panelHeart.add(jLabelSpace);
      panelHeart.add(getJTextFieldHeartMax());
      jLabelLibHeart.setLabelFor(panelHeart);
      jPanelSummary.add(panelHeart, g);

      // Activity
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibActivity = new JLabel();
      jLabelLibActivity.setFont(GuiFont.FONT_PLAIN);
      jLabelLibActivity.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibActivity, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jComboBoxActivity = new JComboBoxActivity(modelActivities);
      modelActivities.setDefaultSelectedItem();
      jComboBoxActivity.setFont(GuiFont.FONT_PLAIN);
      jLabelLibActivity.setLabelFor(jComboBoxActivity);
      jPanelSummary.add(jComboBoxActivity, g);

      // Equipements
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibEquipment = new JLabel();
      jLabelLibEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibEquipment, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jComboBoxEquipment = new JComboBox(modelEquipements);
      modelEquipements.setDefaultSelectedItem();
      jComboBoxEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setLabelFor(jComboBoxEquipment);
      jPanelSummary.add(jComboBoxEquipment, g);

      // Location
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibLocation = new JLabel();
      jLabelLibLocation.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLocation.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibLocation, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      modelLocations = new LocationComboBoxModel();
      jComboBoxLocation = new JComboBox(modelLocations);
      jComboBoxLocation.setEditable(true);
      jComboBoxLocation.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLocation.setLabelFor(jComboBoxLocation);
      jPanelSummary.add(jComboBoxLocation, g);

      // Meteo
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibMeteo = new JLabel();
      jLabelLibMeteo.setFont(GuiFont.FONT_PLAIN);
      jLabelLibMeteo.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibMeteo, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      JPanel panelMeteo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panelMeteo.add(getJXSplitButtonImgMeteo());
      Dimension dimFiller = new Dimension(10, 5);
      panelMeteo.add(new Box.Filler(dimFiller, dimFiller, dimFiller));
      jLabelTemperature = new JLabel("15 " + TemperatureUnit.getDefaultUnit());
      jLabelTemperature.setHorizontalAlignment(SwingConstants.LEFT);
      jLabelTemperature.setFont(new Font("SansSerif", Font.BOLD, 16));
      jLabelTemperature.setBorder(BorderFactory.createTitledBorder(""));
      spinner = new JSpinner();
      spinner.setEditor(jLabelTemperature);
      spinnerModel = new TemperatureSpinnerModel();
      spinner.setModel(spinnerModel);
      Dimension dim = new Dimension(105, 35);
      spinner.setPreferredSize(dim);
      spinner.setMinimumSize(dim);
      panelMeteo.add(spinner);
      jPanelSummary.add(panelMeteo, g);

      // Notes
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.insets = insets;
      jLabelLibNotes = new JLabel("Notes :");
      jLabelLibNotes.setFont(GuiFont.FONT_PLAIN);
      jLabelLibNotes.setHorizontalAlignment(SwingConstants.TRAILING);
      jLabelLibNotes.setVerticalAlignment(SwingConstants.TOP);
      jPanelSummary.add(jLabelLibNotes, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.gridheight = 3;
      g.anchor = GridBagConstraints.NORTHWEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibNotes.setLabelFor(getJScrollPaneTextArea());
      jPanelSummary.add(getJScrollPaneTextArea(), g);
    }
    return jPanelSummary;
  }

  public JFormattedTextField getJTextFieldDistTot() {
    if (jTextFieldDistTot == null) {
      jTextFieldDistTot = new JFormattedTextField();
      jTextFieldDistTot.setFont(GuiFont.FONT_PLAIN);
      jTextFieldDistTot.setFormatterFactory(TextFormatterFactory
          .createNumber(5, 2));
      Dimension dim = new Dimension(63, 24);
      jTextFieldDistTot.setPreferredSize(dim);
      jTextFieldDistTot.setMaximumSize(dim);
    }
    return jTextFieldDistTot;
  }

  public JFormattedTextField getJTextFieldCalories() {
    if (jTextFieldCalories == null) {
      jTextFieldCalories = new JFormattedTextField();
      jTextFieldCalories.setFont(GuiFont.FONT_PLAIN);
      jTextFieldCalories.setFormatterFactory(TextFormatterFactory
          .createNumberBlankAllowed(4));
      Dimension dim = new Dimension(63, 24);
      jTextFieldCalories.setPreferredSize(dim);
      jTextFieldCalories.setMaximumSize(dim);
    }
    return jTextFieldCalories;
  }

  public JFormattedTextField getJTextFieldHeartMoy() {
    if (jTextFieldHeartAvg == null) {
      jTextFieldHeartAvg = new JFormattedTextField();
      jTextFieldHeartAvg.setFont(GuiFont.FONT_PLAIN);
      jTextFieldHeartAvg.setFormatterFactory(TextFormatterFactory
          .createNumberBlankAllowed(3));
      Dimension dim = new Dimension(50, 24);
      jTextFieldHeartAvg.setPreferredSize(dim);
      jTextFieldHeartAvg.setMaximumSize(dim);
    }
    return jTextFieldHeartAvg;
  }

  public JFormattedTextField getJTextFieldHeartMax() {
    if (jTextFieldHeartMax == null) {
      jTextFieldHeartMax = new JFormattedTextField();
      jTextFieldHeartMax.setFont(GuiFont.FONT_PLAIN);
      jTextFieldHeartMax.setFormatterFactory(TextFormatterFactory
          .createNumberBlankAllowed(3));
      Dimension dim = new Dimension(50, 24);
      jTextFieldHeartMax.setPreferredSize(dim);
      jTextFieldHeartMax.setMaximumSize(dim);
    }
    return jTextFieldHeartMax;
  }

  /**
   * This method initializes jTextFieldBirthDate.
   * 
   * @return javax.swing.JTextField
   */
  public JXDatePicker getJDatePicker() {
    if (jXDatePicker == null) {
      jXDatePicker = new JXDatePickerLocale();
      jXDatePicker.setLanguage(LanguageManager.getManager().getCurrentLang());
      jXDatePicker.setFont(GuiFont.FONT_PLAIN);
    }
    return jXDatePicker;
  }

  /**
   * This method initializes jTextFieldTime.
   * 
   * @return javax.swing.JTextField
   */
  public JTextFieldTime getJTextFieldTime() {
    if (jTextFieldTime == null) {
      jTextFieldTime = new JTextFieldTime();
      jTextFieldTime.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldTime;
  }

  /**
   * This method initializes jTextFieldTimeRace.
   * 
   * @return javax.swing.JTextField
   */
  public JTextFieldTime getJTextFieldTimeTot() {
    if (jTextFieldTimeTot == null) {
      jTextFieldTimeTot = new JTextFieldTime(1, 0, 0);
      jTextFieldTimeTot.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldTimeTot;
  }

  /**
   * This method initializes jComboBoxDistanceUnit.
   * 
   * @return javax.swing.JComboBox
   */
  public JComboBox getJComboBoxDistanceUnits() {
    if (jComboBoxDistanceUnits == null) {
      jComboBoxDistanceUnits = new JComboBox();
      jComboBoxDistanceUnits.setFont(GuiFont.FONT_PLAIN);
      jComboBoxDistanceUnits.setModel(new DefaultComboBoxModel(DistanceUnit
          .units()));
      jComboBoxDistanceUnits.setSelectedItem(DistanceUnit.getDefaultUnit());
    }
    return jComboBoxDistanceUnits;
  }

  /**
   * This method initializes jScrollPaneTextArea.
   * 
   * @return javax.swing.JTextField
   */
  private JScrollPane getJScrollPaneTextArea() {
    if (jScrollPaneTextArea == null) {
      jTextFieldNotes = new JTextAreaLength(5, 20);
      jTextFieldNotes.setMaxiMumCharacters(100);
      jTextFieldNotes.setFont(GuiFont.FONT_PLAIN);
      jTextFieldNotes.setWrapStyleWord(true);
      jTextFieldNotes.setLineWrap(true);
      jScrollPaneTextArea = new JScrollPane(jTextFieldNotes);
      jScrollPaneTextArea
          .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      jScrollPaneTextArea
          .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    }
    return jScrollPaneTextArea;
  }

  /**
   * This method initializes jPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      flowLayout.setVgap(0);
      jPanelButton = new JPanel();
      jPanelButton.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));
      jPanelButton.setLayout(flowLayout);
      jPanelButton.add(getJButtonSave(), null);
      jPanelButton.add(getJButtonCancel(), null);
    }
    return jPanelButton;
  }

  private JXSplitButton getJXSplitButtonImgMeteo() {
    if (jxSplitButtonImgMeteo == null) {
      List<ImageIcon> listIcon = DataMeteo.getIcons();
      HashMap<String, ImageIcon> map = new HashMap<String, ImageIcon>();
      String[] values = new String[listIcon.size()];

      JPopupMenu popupMenu = new JPopupMenu();
      ButtonGroup buttonGroupDropDown = new ButtonGroup();
      for (int i = 0; i < listIcon.size(); i++) {
        values[i] = Integer.toString(i);
        map.put(values[i], listIcon.get(i));

        JMenuItemMeteo mi = new JMenuItemMeteo(listIcon.get(i), i);
        buttonGroupDropDown.add(mi);
        popupMenu.add(mi);
      }

      jxSplitButtonImgMeteo = new JXSplitButton(null, null, popupMenu);
      jxSplitButtonImgMeteo.setSelectedIndex(0);
      model.getData().getMeteo().setImageIconIndex(0);
      jxSplitButtonImgMeteo.setFont(new Font("SansSerif", Font.PLAIN, 0));
    }
    return jxSplitButtonImgMeteo;
  }

  /**
   * This method initializes jButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonCancel() {
    if (jButtonCancel == null) {
      jButtonCancel = new JButton();
      jButtonCancel.setFont(GuiFont.FONT_PLAIN);
      jButtonCancel.setEnabled(true);
    }
    return jButtonCancel;
  }

  /**
   * This method initializes jButtonSave
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonSave() {
    if (jButtonSave == null) {
      jButtonSave = new JButton();
      jButtonSave.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonSave;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class EquipementComboBoxModel extends DefaultComboBoxModel {
    private String defaultEquipement;

    public EquipementComboBoxModel() throws SQLException {
      super();
      addElement("");

      List<String> list = EquipementTableManager.getInstance().retreiveNames();
      for (String d : list) {
        addElement(d);
      }
      defaultEquipement = EquipementTableManager.getInstance()
          .retreiveNameDefault();
    }

    public void setDefaultSelectedItem() {
      if (defaultEquipement != null) {
        setSelectedItem(defaultEquipement);
      }
    }

    public String getDefaultEquipement() {
      return defaultEquipement;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class SaveActionListener implements ActionListener {

    public SaveActionListener() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {
      if (!checkSave()) {
        return;
      }

      JDialogAddRun.this.setCursor(Cursor
          .getPredefinedCursor(Cursor.WAIT_CURSOR));
      jButtonCancel.setEnabled(false);
      new SwingWorker() {

        @Override
        public Object construct() {
          // sauvegarde
          try {
            model.save(JDialogAddRun.this);
          }
          catch (SQLException sqle) {
            log.error("", sqle);
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), getClass());
            JShowMessage.error(MessageFormat.format(rb
                .getString("errorSaveSql"), sqle.getErrorCode()));
          }

          // mise a jour des dates;
          MainGui.getWindow().fireHistoric();
          return null;
        }

        @Override
        public void finished() {
          // jButtonCancel.setEnabled(true);
          // JDialogAddRun.this.setCursor(Cursor.getDefaultCursor());
          MainGui.getWindow().afterRunnableSwing();
          dispose();
        }

      }.start();
    }

    private boolean checkSave() {
      if (model.getData().getDistanceTot() <= 0) {
        error("errorDistanceTot");
        return false;
      }
      return true;
    }

    private void error(String msg) {
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), JDialogAddRun.class);
      JShowMessage.error(JDialogAddRun.this, rb.getString(msg));
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class CancelActionListener implements ActionListener {
    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {
      dispose();
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class JMenuItemMeteo extends JMenuItem implements ActionListener {
    int index;

    public JMenuItemMeteo(ImageIcon imageIcon, int index) {
      super(imageIcon);
      this.index = index;
      addActionListener(JMenuItemMeteo.this);
    }

    public void actionPerformed(ActionEvent e) {
      jxSplitButtonImgMeteo.setSelectedIndex(index);
      JDialogAddRun.this.model.getData().getMeteo().setImageIconIndex(index);
    }
  };

  /**
   * @author Denis Apparicio
   * 
   */
  private class TemperatureSpinnerModel extends AbstractSpinnerModel {
    int    maxDegree     = 54;

    int    minDegree     = -49;

    int    maxFahrenheit = (int) TemperatureUnit.convertToFahrenheit(maxDegree);

    int    minFahrenheit = (int) TemperatureUnit.convertToFahrenheit(minDegree);

    int    value = 15;

    int    max           = maxDegree;

    int    min           = minDegree;

    String sValue;

    public TemperatureSpinnerModel() {
      if (TemperatureUnit.isFahrenheit(TemperatureUnit.getDefaultUnit())) {
        max = maxFahrenheit;
        min = minFahrenheit;
        value = (int) TemperatureUnit.convertToFahrenheit(15);
      }
      model.getData().getMeteo().setTemperature(value);  
    }

    public Object getValue() {
      return sValue;
    }

    public void setValue(Object value) {
      this.sValue = (String) value;
      retrieveIntValue();
    }

    public Object getNextValue() {
      if (value > max) {
        return null;
      }
      value++;
      sValue = Integer.toString(value) + " " + TemperatureUnit.getDefaultUnit();
      jLabelTemperature.setText(sValue);
      updateTemperature();
      return sValue;
    }

    public Object getPreviousValue() {
      if (value < min) {
        return null;
      }
      value--;
      sValue = Integer.toString(value) + " " + TemperatureUnit.getDefaultUnit();
      jLabelTemperature.setText(sValue);
      updateTemperature();
      return sValue;
    }

    private int retrieveIntValue() {
      String s = sValue.substring(0, sValue.indexOf(' '));
      value = (s.length() == 1 && s.charAt(0) == '-') ? 0 : Integer.parseInt(s);
      updateTemperature();
      return value;
    }

    private void updateTemperature() {
      model
          .getData()
          .getMeteo()
          .setTemperature((TemperatureUnit.isDefaultUnitDegree()) ? value
              : (int) Math.rint(TemperatureUnit.convertToDegree(value)));
    }

  }

}
