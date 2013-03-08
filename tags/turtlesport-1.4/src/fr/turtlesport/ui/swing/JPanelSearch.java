package fr.turtlesport.ui.swing;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import fr.turtlesport.CommonLang;
import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataSearchRun;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.meteo.DataMeteo;
import fr.turtlesport.ui.swing.component.JTextFieldTime;
import fr.turtlesport.ui.swing.component.JXDatePickerLocale;
import fr.turtlesport.ui.swing.component.JXSplitButton;
import fr.turtlesport.ui.swing.component.TextFormatterFactory;
import fr.turtlesport.ui.swing.model.ActivityComboBoxModel;
import fr.turtlesport.ui.swing.model.DatePickerPropertyChangeListener;
import fr.turtlesport.ui.swing.model.EquipementComboBoxModel;
import fr.turtlesport.ui.swing.model.GenericPropertyChangeListener;
import fr.turtlesport.ui.swing.model.LocationComboBoxModel;
import fr.turtlesport.ui.swing.model.TimePickerPropertyChangeListener;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TemperatureUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelSearch extends JPanel implements LanguageListener,
                                        UnitListener {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelSearch.class);
  }

  private JLabel                  jLabelDistance;

  private JFormattedTextField     jTextFielDistanceMin;

  private JFormattedTextField     jTextFielDistanceMax;

  private JLabel                  jLabelDistanceTo;

  private JLabel                  jLabelDuration;

  private JTextFieldTime          jTextFielDurationMin;

  private JLabel                  jLabelDurationTo;

  private JTextFieldTime          jTextFielDurationMax;

  private JLabel                  jLabelLibEquipment;

  private JComboBox               jComboBoxEquipment;

  private JLabel                  jLabelLibLocation;

  private JComboBox               jComboBoxLocation;

  private JLabel                  jLabelLibPeriod;

  private JXDatePickerLocale      jXDatePickerMin;

  private JLabel                  jLabelPeriodTo;

  private JXDatePickerLocale      jXDatePickerMax;

  private JComboBox               jComboBoxDistanceUnits;

  private JLabel                  jLabelLibActivity;

  private JComboBox               jComboBoxActivity;

  private JXSplitButton           jxSplitButtonImgMeteo;

  private JLabel                  jLabelLibMeteo;

  private JLabel                  jLabelLibTemperature;

  private JLabel                  jLabelTemperatureTo;

  private JFormattedTextField     jTextFieldTemperatureMin;

  private JFormattedTextField     jTextFieldTemperatureMax;

  private JLabel                  jLabelTemperatureUnit;

  private JButton                 jButtonClear;

  // Model
  private EquipementComboBoxModel modelEquipements;

  private LocationComboBoxModel   modelLocations;

  private ActivityComboBoxModel   modelActivities;

  private DataSearchRun           dataSearch;

  private ButtonGroup             jButtonGroupMeteo;

  private JMenuItemMeteo          nullItemCondition;

  /**
   * Create the panel.
   * 
   * @throws SQLException
   */
  public JPanelSearch() throws SQLException {
    super();
    initialize();

    // Evenements
    addEvents();
    clear();

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  /**
   * Mis &agrave; jour de l'unit&eacute; de distance.
   */
  public void setUnitDistance(String newUnit) {
    if (newUnit == null || newUnit.equals(dataSearch.getUnitDistance())) {
      return;
    }

    // Distance
    if (dataSearch.isDistanceMaxValid()) {
      dataSearch.setDistanceMax((int) DistanceUnit
          .convert(dataSearch.getUnitDistance(), newUnit, dataSearch.getDistanceMax()));
    }
    if (dataSearch.isDistanceMinValid()) {
      dataSearch.setDistanceMin((int) DistanceUnit
          .convert(dataSearch.getUnitDistance(), newUnit, dataSearch.getDistanceMin()));
    }

    dataSearch.setUnitDistance(newUnit);
    jComboBoxDistanceUnits.setSelectedItem(newUnit);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent e) {
    if (e.isEventDistance()) {
      setUnitDistance(e.getUnit());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
  }

  /**
   * Valorise l'action ENTER.
   * 
   * @param action
   *          l'action a d&eacute;clench&eacute;e;.
   */
  public void setFindAction(ActionListener action) {
    jTextFielDistanceMin.addActionListener(action);
    jTextFielDistanceMax.addActionListener(action);
    jTextFieldTemperatureMin.addActionListener(action);
    jTextFieldTemperatureMax.addActionListener(action);
  }

  public DataSearchRun getDataSearch() {
    return dataSearch;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang
   * .LanguageEvent)
   */
  public void languageChanged(final LanguageEvent event) {
    if (SwingUtilities.isEventDispatchThread()) {
      performedLanguage(event.getLang());
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          performedLanguage(event.getLang());
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  @Override
  public void completedRemoveLanguageListener() {
  };

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(lang, CommonLang.class);

    jButtonClear.setText(rb.getString("Clear"));
    jLabelDistance.setText(rb.getString("Distance"));
    jLabelDuration.setText(rb.getString("Duration"));
    jLabelLibEquipment.setText(rb.getString("Equipment"));
    jLabelLibLocation.setText(rb.getString("Location"));
    jLabelLibPeriod.setText(rb.getString("Period"));
    jLabelLibActivity.setText(rb.getString("Activity"));
    jLabelLibMeteo.setText(rb.getString("Meteo"));
    jLabelLibTemperature.setText(rb.getString("Temperature"));
    // on remet a jour le date formatter.
    jXDatePickerMax.setLanguage(lang);
    jXDatePickerMin.setLanguage(lang);
  }

  private void initialize() {
    modelEquipements = new EquipementComboBoxModel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 100,
        180,
        100,
        100,
        10,
        100,
        60,
        120 };
    gridBagLayout.rowHeights = new int[] { 30, 30, 30 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0 };
    setLayout(gridBagLayout);

    Insets insets = new Insets(0, 0, 5, 10);

    // Row 0
    // ----------
    // Meteo
    jLabelLibMeteo = new JLabel("Meteo :");
    jLabelLibMeteo.setFont(GuiFont.FONT_PLAIN);
    jLabelLibMeteo.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelLibMeteo = new GridBagConstraints();
    gbc_jLabelLibMeteo.insets = insets;
    gbc_jLabelLibMeteo.gridx = 0;
    gbc_jLabelLibMeteo.gridy = 0;
    gbc_jLabelLibMeteo.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelLibMeteo, gbc_jLabelLibMeteo);

    List<ImageIcon> listIcon = DataMeteo.getIconsSmall();
    HashMap<String, ImageIcon> map = new HashMap<String, ImageIcon>();
    String[] values = new String[listIcon.size()];

    JPopupMenu popupMenu = new JPopupMenu();
    jButtonGroupMeteo = new ButtonGroup();
    for (int i = 0; i < listIcon.size() - 1; i++) {
      values[i] = Integer.toString(i);
      map.put(values[i], listIcon.get(i));
      JMenuItemMeteo mi = new JMenuItemMeteo(listIcon.get(i), i);
      jButtonGroupMeteo.add(mi);
      popupMenu.add(mi);
    }
    nullItemCondition = new JMenuItemMeteo(listIcon.get(listIcon.size() - 1),
                                           listIcon.size() - 1,
                                           -1);
    jButtonGroupMeteo.add(nullItemCondition);
    popupMenu.add(nullItemCondition);

    jxSplitButtonImgMeteo = new JXSplitButton(null, null, popupMenu);
    jxSplitButtonImgMeteo.setFont(new Font("SansSerif", Font.PLAIN, 0));
    jxSplitButtonImgMeteo.setSelectedIndex(listIcon.size() - 1);
    GridBagConstraints gbc_jLabelImgMeteo = new GridBagConstraints();
    gbc_jLabelImgMeteo.fill = GridBagConstraints.VERTICAL;
    gbc_jLabelImgMeteo.insets = insets;
    gbc_jLabelImgMeteo.gridx = 1;
    gbc_jLabelImgMeteo.gridy = 0;
    add(jxSplitButtonImgMeteo, gbc_jLabelImgMeteo);

    // Temperature
    jLabelLibTemperature = new JLabel("Temperature :");
    jLabelLibTemperature.setFont(GuiFont.FONT_PLAIN);
    jLabelLibTemperature.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_Temperature = new GridBagConstraints();
    gbc_Temperature.insets = insets;
    gbc_Temperature.gridx = 2;
    gbc_Temperature.gridy = 0;
    gbc_Temperature.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelLibTemperature, gbc_Temperature);

    jTextFieldTemperatureMin = new JFormattedTextField();
    jTextFieldTemperatureMin.setFont(GuiFont.FONT_PLAIN);
    jTextFieldTemperatureMin.setFormatterFactory(TextFormatterFactory
        .createNumberBlankAllowed(2));
    GridBagConstraints gbc_jTextFielTemperatureMin = new GridBagConstraints();
    gbc_jTextFielTemperatureMin.insets = insets;
    gbc_jTextFielTemperatureMin.gridx = 3;
    gbc_jTextFielTemperatureMin.gridy = 0;
    gbc_jTextFielTemperatureMin.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFieldTemperatureMin, gbc_jTextFielTemperatureMin);

    jLabelTemperatureTo = new JLabel("-");
    jLabelTemperatureTo.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelTemperatureTo = new GridBagConstraints();
    gbc_jLabelTemperatureTo.insets = insets;
    gbc_jLabelTemperatureTo.gridx = 4;
    gbc_jLabelTemperatureTo.gridy = 0;
    gbc_jLabelTemperatureTo.fill = GridBagConstraints.CENTER;
    add(jLabelTemperatureTo, gbc_jLabelTemperatureTo);

    jTextFieldTemperatureMax = new JFormattedTextField();
    jTextFieldTemperatureMax.setFont(GuiFont.FONT_PLAIN);
    jTextFieldTemperatureMax.setFormatterFactory(TextFormatterFactory
        .createNumberBlankAllowed(2));
    GridBagConstraints gbc_jTextFielTemperatureMax = new GridBagConstraints();
    gbc_jTextFielTemperatureMax.insets = insets;
    gbc_jTextFielTemperatureMax.gridx = 5;
    gbc_jTextFielTemperatureMax.gridy = 0;
    gbc_jTextFielTemperatureMax.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFieldTemperatureMax, gbc_jTextFielTemperatureMax);

    jLabelTemperatureUnit = new JLabel(TemperatureUnit.getDefaultUnit());
    jLabelTemperatureUnit.setFont(GuiFont.FONT_PLAIN);
    jLabelLibTemperature.setHorizontalAlignment(SwingConstants.LEFT);
    GridBagConstraints gbc_jLabelTemperatureUnit = new GridBagConstraints();
    gbc_jLabelTemperatureUnit.insets = insets;
    gbc_jLabelTemperatureUnit.gridx = 6;
    gbc_jLabelTemperatureUnit.gridy = 0;
    gbc_jLabelTemperatureUnit.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelTemperatureUnit, gbc_jLabelTemperatureUnit);

    jButtonClear = new JButton();
    jButtonClear.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jButtonClear = new GridBagConstraints();
    gbc_jButtonClear.insets = insets;
    gbc_jButtonClear.gridx = 7;
    gbc_jButtonClear.gridy = 0;
    gbc_jButtonClear.fill = GridBagConstraints.CENTER;
    add(jButtonClear, gbc_jButtonClear);

    // Row 1
    // ---------------
    // Equipement
    jLabelLibEquipment = new JLabel("Equipement :");
    jLabelLibEquipment.setFont(GuiFont.FONT_PLAIN);
    jLabelLibEquipment.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelLibEquipment = new GridBagConstraints();
    gbc_jLabelLibEquipment.insets = insets;
    gbc_jLabelLibEquipment.gridx = 0;
    gbc_jLabelLibEquipment.gridy = 1;
    gbc_jLabelLibEquipment.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelLibEquipment, gbc_jLabelLibEquipment);

    jComboBoxEquipment = new JComboBox(modelEquipements);
    jComboBoxEquipment.setFont(GuiFont.FONT_PLAIN);
    jLabelLibEquipment.setLabelFor(jComboBoxEquipment);
    GridBagConstraints gbc_jComboBoxEquipment = new GridBagConstraints();
    gbc_jComboBoxEquipment.insets = insets;
    gbc_jComboBoxEquipment.gridx = 1;
    gbc_jComboBoxEquipment.gridy = 1;
    gbc_jComboBoxEquipment.anchor = GridBagConstraints.WEST;
    gbc_jComboBoxEquipment.fill = GridBagConstraints.BOTH;
    add(jComboBoxEquipment, gbc_jComboBoxEquipment);

    // Distance
    jLabelDistance = new JLabel("Distance :");
    jLabelDistance.setFont(GuiFont.FONT_PLAIN);
    jLabelDistance.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelDistance = new GridBagConstraints();
    gbc_jLabelDistance.insets = insets;
    gbc_jLabelDistance.gridx = 2;
    gbc_jLabelDistance.gridy = 1;
    gbc_jLabelDistance.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelDistance, gbc_jLabelDistance);

    jTextFielDistanceMin = new JFormattedTextField();
    jTextFielDistanceMin.setFormatterFactory(TextFormatterFactory
        .createNumberBlankAllowed(5));
    jTextFielDistanceMin.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDistanceMin = new GridBagConstraints();
    gbc_jTextFielDistanceMin.insets = insets;
    gbc_jTextFielDistanceMin.gridx = 3;
    gbc_jTextFielDistanceMin.gridy = 1;
    gbc_jTextFielDistanceMin.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDistanceMin, gbc_jTextFielDistanceMin);

    jLabelDistanceTo = new JLabel("-");
    jLabelDistanceTo.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelDistanceTo = new GridBagConstraints();
    gbc_jLabelDistanceTo.insets = insets;
    gbc_jLabelDistanceTo.gridx = 4;
    gbc_jLabelDistanceTo.gridy = 1;
    gbc_jLabelDistanceTo.fill = GridBagConstraints.CENTER;
    add(jLabelDistanceTo, gbc_jLabelDistanceTo);

    jTextFielDistanceMax = new JFormattedTextField();
    jTextFielDistanceMax.setFormatterFactory(TextFormatterFactory
        .createNumberBlankAllowed(5));
    jTextFielDistanceMax.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDistanceMax = new GridBagConstraints();
    gbc_jTextFielDistanceMax.insets = insets;
    gbc_jTextFielDistanceMax.gridx = 5;
    gbc_jTextFielDistanceMax.gridy = 1;
    gbc_jTextFielDistanceMax.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDistanceMax, gbc_jTextFielDistanceMax);

    jComboBoxDistanceUnits = new JComboBox();
    jComboBoxDistanceUnits.setFont(GuiFont.FONT_PLAIN);
    jComboBoxDistanceUnits.setModel(new DefaultComboBoxModel(DistanceUnit
        .units()));
    jComboBoxDistanceUnits.setSelectedItem(DistanceUnit.getDefaultUnit());
    GridBagConstraints gbc_jComboBoxDistanceUnits = new GridBagConstraints();
    gbc_jComboBoxDistanceUnits.insets = insets;
    gbc_jComboBoxDistanceUnits.gridx = 6;
    gbc_jComboBoxDistanceUnits.gridy = 1;
    gbc_jComboBoxDistanceUnits.fill = GridBagConstraints.HORIZONTAL;
    add(jComboBoxDistanceUnits, gbc_jComboBoxDistanceUnits);

    // Row 2
    // -----------------
    // Location
    jLabelLibLocation = new JLabel("Location :");
    jLabelLibLocation.setFont(GuiFont.FONT_PLAIN);
    jLabelLibLocation.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelLibLocation = new GridBagConstraints();
    gbc_jLabelLibLocation.insets = insets;
    gbc_jLabelLibLocation.gridx = 0;
    gbc_jLabelLibLocation.gridy = 2;
    gbc_jLabelLibLocation.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelLibLocation, gbc_jLabelLibLocation);

    modelLocations = new LocationComboBoxModel();
    jComboBoxLocation = new JComboBox(modelLocations);
    jComboBoxLocation.setFont(GuiFont.FONT_PLAIN);
    jLabelLibLocation.setLabelFor(jComboBoxLocation);
    GridBagConstraints gbc_jComboBoxLocation = new GridBagConstraints();
    gbc_jComboBoxLocation.insets = insets;
    gbc_jComboBoxLocation.gridx = 1;
    gbc_jComboBoxLocation.gridy = 2;
    gbc_jComboBoxLocation.anchor = GridBagConstraints.WEST;
    gbc_jComboBoxLocation.fill = GridBagConstraints.BOTH;
    add(jComboBoxLocation, gbc_jComboBoxLocation);

    // Duree
    jLabelDuration = new JLabel("Durée :");
    jLabelDuration.setFont(GuiFont.FONT_PLAIN);
    jLabelDuration.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelDuration = new GridBagConstraints();
    gbc_jLabelDuration.insets = insets;
    gbc_jLabelDuration.gridx = 2;
    gbc_jLabelDuration.gridy = 2;
    gbc_jLabelDuration.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelDuration, gbc_jLabelDuration);

    jTextFielDurationMin = new JTextFieldTime(true);
    jTextFielDurationMin.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDurationMin = new GridBagConstraints();
    gbc_jTextFielDurationMin.insets = insets;
    gbc_jTextFielDurationMin.gridx = 3;
    gbc_jTextFielDurationMin.gridy = 2;
    gbc_jTextFielDurationMin.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDurationMin, gbc_jTextFielDurationMin);

    jLabelDurationTo = new JLabel("-");
    jLabelDurationTo.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelDurationTo = new GridBagConstraints();
    gbc_jLabelDurationTo.insets = insets;
    gbc_jLabelDurationTo.gridx = 4;
    gbc_jLabelDurationTo.gridy = 2;
    gbc_jLabelDistanceTo.fill = GridBagConstraints.CENTER;
    add(jLabelDurationTo, gbc_jLabelDurationTo);

    jTextFielDurationMax = new JTextFieldTime(true);
    jTextFielDurationMax.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDurationMax = new GridBagConstraints();
    gbc_jTextFielDurationMax.insets = insets;
    gbc_jTextFielDurationMax.gridx = 5;
    gbc_jTextFielDurationMax.gridy = 2;
    gbc_jTextFielDurationMax.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDurationMax, gbc_jTextFielDurationMax);

    JLabel jLabelFormatDiration = new JLabel("(hh:mm:ss)");
    jLabelFormatDiration.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelFormatDiration = new GridBagConstraints();
    gbc_jLabelFormatDiration.insets = insets;
    gbc_jLabelFormatDiration.gridx = 6;
    gbc_jLabelFormatDiration.gridy = 2;
    gbc_jLabelFormatDiration.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelFormatDiration, gbc_jLabelFormatDiration);

    // Row 3
    // -----------------------
    // Activite
    jLabelLibActivity = new JLabel("Activity :");
    jLabelLibActivity.setFont(GuiFont.FONT_PLAIN);
    jLabelLibActivity.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelLibActivity = new GridBagConstraints();
    gbc_jLabelLibActivity.insets = insets;
    gbc_jLabelLibActivity.gridx = 0;
    gbc_jLabelLibActivity.gridy = 3;
    gbc_jLabelLibActivity.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelLibActivity, gbc_jLabelLibActivity);

    modelActivities = new ActivityComboBoxModel();
    modelActivities.insertElementAt("", 0);
    jComboBoxActivity = new JComboBox(modelActivities);
    modelActivities.setSelectedItem("");
    jComboBoxActivity.setFont(GuiFont.FONT_PLAIN);
    jLabelLibActivity.setLabelFor(jComboBoxActivity);
    GridBagConstraints gbc_jComboBoxActivity = new GridBagConstraints();
    gbc_jComboBoxActivity.insets = insets;
    gbc_jComboBoxActivity.gridx = 1;
    gbc_jComboBoxActivity.gridy = 3;
    gbc_jComboBoxActivity.fill = GridBagConstraints.HORIZONTAL;
    add(jComboBoxActivity, gbc_jComboBoxActivity);

    // Periode
    jLabelLibPeriod = new JLabel("Periode :");
    jLabelLibPeriod.setFont(GuiFont.FONT_PLAIN);
    jLabelLibPeriod.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelLibPeriod = new GridBagConstraints();
    gbc_jLabelLibPeriod.insets = insets;
    gbc_jLabelLibPeriod.gridx = 2;
    gbc_jLabelLibPeriod.gridy = 3;
    gbc_jLabelLibPeriod.fill = GridBagConstraints.HORIZONTAL;
    add(jLabelLibPeriod, gbc_jLabelLibPeriod);

    jXDatePickerMin = new JXDatePickerLocale();
    jXDatePickerMin.setFont(GuiFont.FONT_PLAIN);

    GridBagConstraints gbc_jXDatePickerMin = new GridBagConstraints();
    gbc_jXDatePickerMin.insets = insets;
    gbc_jXDatePickerMin.gridx = 3;
    gbc_jXDatePickerMin.gridy = 3;
    gbc_jXDatePickerMin.fill = GridBagConstraints.EAST;
    add(jXDatePickerMin, gbc_jXDatePickerMin);

    jLabelPeriodTo = new JLabel("-");
    jLabelPeriodTo.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelPeriodTo = new GridBagConstraints();
    gbc_jLabelPeriodTo.insets = insets;
    gbc_jLabelPeriodTo.gridx = 4;
    gbc_jLabelPeriodTo.gridy = 3;
    gbc_jLabelPeriodTo.fill = GridBagConstraints.CENTER;
    add(jLabelPeriodTo, gbc_jLabelPeriodTo);

    jXDatePickerMax = new JXDatePickerLocale();
    jXDatePickerMax.setFont(GuiFont.FONT_PLAIN);

    GridBagConstraints gbc_jXDatePickerMax = new GridBagConstraints();
    gbc_jXDatePickerMax.insets = insets;
    gbc_jXDatePickerMax.gridx = 5;
    gbc_jXDatePickerMax.gridy = 3;
    gbc_jXDatePickerMax.fill = GridBagConstraints.EAST;
    add(jXDatePickerMax, gbc_jXDatePickerMax);
  }

  private void clear() {
    nullItemCondition.doClick();
    jComboBoxEquipment.setSelectedIndex(0);
    jComboBoxLocation.setSelectedIndex(0);
    jComboBoxActivity.setSelectedIndex(0);
    jTextFieldTemperatureMin.setText(null);
    jTextFieldTemperatureMax.setText(null);
    jTextFielDistanceMin.setValue(null);
    jTextFielDistanceMax.setValue(null);
    jTextFielDurationMin.setValue(null);
    jTextFielDurationMax.setValue(null);
    jXDatePickerMin.setDate(null);
    jXDatePickerMax.setDate(null);
  }

  private void addEvents() {
    dataSearch = new DataSearchRun();
    try {
      jButtonClear.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          clear();
        }
      });

      // Meteo
      // Deja fait das JmenuItemMeto

      // Equipement
      jComboBoxEquipment.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          dataSearch.setEquipment((String) jComboBoxEquipment.getSelectedItem());
        }
      });

      // Location
      jComboBoxLocation.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          dataSearch.setLocation((String) jComboBoxLocation.getSelectedItem());
        }
      });

      // Activity
      jComboBoxActivity.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          Object obj = jComboBoxActivity.getSelectedItem();
          if (obj instanceof AbstractDataActivity) {
            dataSearch.setSportType(((AbstractDataActivity) obj).getSportType());
          }
          else {
            dataSearch.setSportType(-1);
          }
        }
      });

      // distance
      GenericPropertyChangeListener lstDistanceMin = new GenericPropertyChangeListener(jTextFielDistanceMin,
                                                                                       dataSearch,
                                                                                       "setDistanceMin",
                                                                                       Integer.TYPE);
      jTextFielDistanceMin.addPropertyChangeListener("value", lstDistanceMin);
      GenericPropertyChangeListener lstDistanceMax = new GenericPropertyChangeListener(jTextFielDistanceMax,
                                                                                       dataSearch,
                                                                                       "setDistanceMax",
                                                                                       Integer.TYPE);
      jTextFielDistanceMax.addPropertyChangeListener("value", lstDistanceMax);

      // Temperature
      GenericPropertyChangeListener lstTemperatureMin = new GenericPropertyChangeListener(jTextFieldTemperatureMin,
                                                                                          dataSearch,
                                                                                          "setTempMin",
                                                                                          Integer.TYPE,
                                                                                          Integer.MIN_VALUE);
      jTextFieldTemperatureMin.addPropertyChangeListener("value",
                                                         lstTemperatureMin);

      GenericPropertyChangeListener lstTemperatureMax = new GenericPropertyChangeListener(jTextFieldTemperatureMax,
                                                                                          dataSearch,
                                                                                          "setTempMax",
                                                                                          Integer.TYPE,
                                                                                          Integer.MAX_VALUE);
      jTextFieldTemperatureMax.addPropertyChangeListener("value",
                                                         lstTemperatureMax);

      // Duree
      TimePickerPropertyChangeListener lstTimeMin = new TimePickerPropertyChangeListener(jTextFielDurationMin,
                                                                                         dataSearch,
                                                                                         "setDurationMin");
      jTextFielDurationMin.addPropertyChangeListener(lstTimeMin);

      TimePickerPropertyChangeListener lstTimeMax = new TimePickerPropertyChangeListener(jTextFielDurationMax,
                                                                                         dataSearch,
                                                                                         "setDurationMax");
      jTextFielDurationMax.addPropertyChangeListener(lstTimeMax);

      // Periode
      DatePickerPropertyChangeListener lstDateMin = new DatePickerPropertyChangeListener(jXDatePickerMin,
                                                                                         dataSearch,
                                                                                         "setDateMin");
      jXDatePickerMin.addPropertyChangeListener(lstDateMin);

      DatePickerPropertyChangeListener lstDateMax = new DatePickerPropertyChangeListener(jXDatePickerMax,
                                                                                         dataSearch,
                                                                                         "setDateMax");
      jXDatePickerMax.addPropertyChangeListener(lstDateMax);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class JMenuItemMeteo extends JMenuItem implements ActionListener {
    int index;

    int condition;

    public JMenuItemMeteo(ImageIcon imageIcon, int index) {
      super(imageIcon);
      this.index = index;
      this.condition = index;
      addActionListener(JMenuItemMeteo.this);
    }

    public JMenuItemMeteo(ImageIcon imageIcon, int index, int condition) {
      super(imageIcon);
      this.index = index;
      this.condition = condition;
      addActionListener(JMenuItemMeteo.this);
    }

    public void actionPerformed(ActionEvent e) {
      jxSplitButtonImgMeteo.setSelectedIndex(index);
      dataSearch.setCondition(condition);
    }
  }

}
