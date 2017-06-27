package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataHeartZone;
import fr.turtlesport.db.DataSpeedZone;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JTextFieldLength;
import fr.turtlesport.ui.swing.component.TextFormatterFactory;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;
import fr.turtlesport.ui.swing.model.GenericModelDocListener;
import fr.turtlesport.ui.swing.model.ModelActivity;
import fr.turtlesport.ui.swing.model.SpeedModelDocListener;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis
 * 
 */
public class JPanelUserActivity extends JPanel implements LanguageListener,
                                              UnitListener {
  private static TurtleLogger                 log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelUserActivity.class);
  }

  private static final String[]               ZONES_HEART             = { "1",
      "2",
      "3",
      "4",
      "5"                                                            };

  private static final String[]               ZONES_SPEED             = { "1",
      "2",
      "3",
      "4",
      "5",
      "6",
      "7",
      "8",
      "9",
      "10"                                                           };

  private JPanel                              jPanelZoneHeart;

  private JLabel                              jLabelLibZoneHeart;

  private JComboBox                           jComboBoxZoneHeart;

  private JLabel                              jLabelLibZoneHeartMin;

  private JLabel                              jLabelLibZoneHeartMax;

  private JFormattedTextField                 jTextFieldZoneHeartMin;

  private JFormattedTextField                 jTextFieldZoneHeartMax;

  private JPanel                              jPanelZoneSpeed;

  private JLabel                              jLabelLibZoneSpeed;

  private JComboBox                           jComboBoxZoneSpeed;

  private JLabel                              jLabelLibZoneSpeedName;

  private JTextFieldLength                    jTextFieldZoneSpeedName;

  private JLabel                              jLabelLibSpeedMin;

  private JLabel                              jLabelLibSpeedMax;

  private JFormattedTextField                 jTextFieldZoneSpeedMin;

  private JFormattedTextField                 jTextFieldZoneSpeedMax;

  private JComboBox                           jComboBoxSpeedAndPaceUnits;

  private JRadioButton                        jRadioButtonBpm;

  private JRadioButton                        jRadioButtonPourFcMax;

  private JLabel                              jLabelLibBpm;

  private ButtonGroup                         heartButtonGroup;

  private JLabel                              jLabelMaxHeartRate;

  private JFormattedTextField                 jTextFieldMaxHeartRate;

  private TitledBorder                        borderPanelZoneHeart;

  private TitledBorder                        borderPanelZoneSpeed;

  // model
  private ModelActivity                       model;

  // Listener
  private GenericModelDocListener             docLstMaxHeartRate;

  private GenericModelDocListener             docLstZoneHeartMax;

  private GenericModelDocListener             docLstZoneHeartMin;

  private GenericModelDocListener             docLstZoneSpeedName;

  private SpeedModelDocListener               docLstZoneSpeedMax;

  private SpeedModelDocListener               docLstZoneSpeedMin;

  private ItemListener                        comboBoxItemListenerZoneHeart;

  private ItemListener                        comboBoxItemListenerZoneSpeed;

  private ActionListener                      actionListenerHeart;

  private JPanel                              jPanelNorth;

  private JPanel                              jPanelCenter;

  private JPanelUserProfile                   owner;

  private JCheckBox                           jCheckBoxDefaultActivity;

  private JComboBoxIconActivity               jComboBoxIconActivity;

  private JButton                             jButtonCalculate;

  private ActionListener                      actionListenerIconActivity;

  // Formatter
  public static final DefaultFormatterFactory TIME_FORMATTER_FACTORY  = TextFormatterFactory
                                                                          .createTime();

  public static final DefaultFormatterFactory SPEED_FORMATTER_FACTORY = TextFormatterFactory
                                                                          .createNumber(3,
                                                                                        2);

  private static ResourceBundle               rb;

  public JPanelUserActivity(JPanelUserProfile owner) {
    super();
    this.owner = owner;
    initialize();
  }

  public void setModel(ModelActivity model) {
    this.model = model;
  }

  public JPanelUserProfile getOwner() {
    return owner;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent event) {
    model.performedUnit(this, event);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
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

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, JPanelUserActivity.class);

    jLabelMaxHeartRate.setText(rb.getString("jLabelMaxHeartRate"));
    jCheckBoxDefaultActivity.setText(rb.getString("jCheckBoxDefaultActivity"));
    jLabelLibBpm.setText(rb.getString("jLabelLibBpm"));
    jLabelLibZoneHeart.setText(rb.getString("jLabelLibZoneHeart"));
    jLabelLibZoneHeartMin.setText(rb.getString("jLabelLibZoneHeartMin"));
    jLabelLibZoneHeartMax.setText(rb.getString("jLabelLibZoneHeartMax"));
    borderPanelZoneHeart.setTitle(rb.getString("borderPanelZoneHeart"));
    jLabelLibZoneSpeed.setText(rb.getString("jLabelLibZoneSpeed"));
    jLabelLibZoneSpeedName.setText(rb.getString("jLabelLibZoneSpeedName"));
    jLabelLibSpeedMin.setText(rb.getString("jLabelLibSpeedMin"));
    jLabelLibSpeedMax.setText(rb.getString("jLabelLibSpeedMax"));
    borderPanelZoneSpeed.setTitle(rb.getString("borderPanelZoneSpeed"));
    jRadioButtonBpm.setText(rb.getString("jRadioButtonBpm"));
    jRadioButtonPourFcMax.setText(rb.getString("jRadioButtonPourFcMax"));
    jButtonCalculate.setText(rb.getString("jButtonCalculate")==null?"calculate":rb.getString("jButtonCalculate"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  /**
   * @return the model
   */
  public ModelActivity getModel() {
    return model;
  }

  /**
   * Ajout des listeners de l'activit&eacute;.
   */
  public void addEvents(final AbstractDataActivity data) {
    try {
      removeEvents();

      actionListenerIconActivity = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          data.setIconName(jComboBoxIconActivity.getSelectedIconName());
        }
      };
      jComboBoxIconActivity.addActionListener(actionListenerIconActivity);

      actionListenerHeart = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          model.setUnitHeart(JPanelUserActivity.this);
        }
      };
      jRadioButtonBpm.addActionListener(actionListenerHeart);
      jRadioButtonPourFcMax.addActionListener(actionListenerHeart);

      docLstMaxHeartRate = new GenericModelDocListener(jTextFieldMaxHeartRate,
                                                       data,
                                                       "setMaxHeartRate",
                                                       Integer.TYPE);
      jTextFieldMaxHeartRate.getDocument()
          .addDocumentListener(docLstMaxHeartRate);

      comboBoxItemListenerZoneHeart = new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            firePerformedZoneHeart(data.getHeartZones()[jComboBoxZoneHeart
                .getSelectedIndex()]);
          }
        }
      };
      jComboBoxZoneHeart.addItemListener(comboBoxItemListenerZoneHeart);

      comboBoxItemListenerZoneSpeed = new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            firePerformedZoneSpeed(data.getSpeedZones()[jComboBoxZoneSpeed
                .getSelectedIndex()]);
          }
        }
      };
      jComboBoxZoneSpeed.addItemListener(comboBoxItemListenerZoneSpeed);

      jTextFieldMaxHeartRate.setValue(data.getMaxHeartRate());
      firePerformedZoneHeart(data.getHeartZones()[0]);
      firePerformedZoneSpeed(data.getSpeedZones()[0]);
      jComboBoxZoneHeart.setSelectedIndex(0);
      jComboBoxZoneSpeed.setSelectedIndex(0);
      jComboBoxIconActivity.setSelectedIcon(data.getIconName());
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
  }

  /**
   * Suppression des listeners de l'activit&eacute;.
   */
  public void removeEvents() {
    if (docLstMaxHeartRate == null) {
      return;
    }

    jTextFieldMaxHeartRate.getDocument()
        .removeDocumentListener(docLstMaxHeartRate);
    jComboBoxZoneHeart.removeItemListener(comboBoxItemListenerZoneHeart);
    jComboBoxZoneSpeed.removeItemListener(comboBoxItemListenerZoneSpeed);
    jRadioButtonBpm.removeActionListener(actionListenerHeart);
    jRadioButtonPourFcMax.removeActionListener(actionListenerHeart);
    jComboBoxIconActivity.removeActionListener(actionListenerIconActivity);

    docLstMaxHeartRate = null;
    comboBoxItemListenerZoneHeart = null;
    comboBoxItemListenerZoneSpeed = null;
    actionListenerHeart = null;
    actionListenerIconActivity = null;
  }

  /**
   * Ajout des listeners de la zone cardiaque.
   */
  private void firePerformedZoneHeart(final DataHeartZone data) {
    try {
      removeEventsZoneHeart();

      jTextFieldZoneHeartMax.setValue(data.getHighHeartRate());
      jTextFieldZoneHeartMin.setValue(data.getLowHeartRate());

      docLstZoneHeartMax = new GenericModelDocListener(jTextFieldZoneHeartMax,
                                                       data,
                                                       "setHighHeartRate",
                                                       Integer.TYPE);
      jTextFieldZoneHeartMax.getDocument()
          .addDocumentListener(docLstZoneHeartMax);

      docLstZoneHeartMin = new GenericModelDocListener(jTextFieldZoneHeartMin,
                                                       data,
                                                       "setLowHeartRate",
                                                       Integer.TYPE);
      jTextFieldZoneHeartMin.getDocument()
          .addDocumentListener(docLstZoneHeartMin);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
  }

  /**
   * Suppression des listeners de la zone cardiaque.
   */
  private void removeEventsZoneHeart() {
    if (docLstZoneHeartMax == null) {
      return;
    }

    jTextFieldZoneHeartMax.getDocument()
        .removeDocumentListener(docLstZoneHeartMax);
    jTextFieldZoneHeartMin.getDocument()
        .removeDocumentListener(docLstZoneHeartMin);
  }

  /**
   * Ajout des listeners de la zone de vitesse.
   */
  private void firePerformedZoneSpeed(final DataSpeedZone data) {
    try {
      removeEventsZoneSpeed();

      jTextFieldZoneSpeedName.setText(data.getName());
      if (TIME_FORMATTER_FACTORY.equals(jTextFieldZoneSpeedMax
          .getFormatterFactory())) {
        jTextFieldZoneSpeedMax.setText(SpeedPaceUnit.convertToTime(data
            .getHighSpeed()));
        jTextFieldZoneSpeedMin.setText(SpeedPaceUnit.convertToTime(data
            .getLowSpeed()));
      }
      else {
        jTextFieldZoneSpeedMax.setValue(data.getHighSpeed());
        jTextFieldZoneSpeedMin.setValue(data.getLowSpeed());
      }

      docLstZoneSpeedName = new GenericModelDocListener(jTextFieldZoneSpeedName,
                                                        data,
                                                        "setName");
      jTextFieldZoneSpeedName.getDocument()
          .addDocumentListener(docLstZoneSpeedName);

      docLstZoneSpeedMax = new SpeedModelDocListener(jTextFieldZoneSpeedMax,
                                                     data,
                                                     "setHighSpeed",
                                                     Float.TYPE);
      jTextFieldZoneSpeedMax.getDocument()
          .addDocumentListener(docLstZoneSpeedMax);

      docLstZoneSpeedMin = new SpeedModelDocListener(jTextFieldZoneSpeedMin,
                                                     data,
                                                     "setLowSpeed",
                                                     Float.TYPE);
      jTextFieldZoneSpeedMin.getDocument()
          .addDocumentListener(docLstZoneSpeedMin);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
  }

  /**
   * Suppression des listeners de la zone cardiaque.
   */
  private void removeEventsZoneSpeed() {
    if (docLstZoneSpeedName == null) {
      return;
    }
    jTextFieldZoneSpeedName.getDocument()
        .removeDocumentListener(docLstZoneSpeedName);
    jTextFieldZoneSpeedMax.getDocument()
        .removeDocumentListener(docLstZoneSpeedMax);
    jTextFieldZoneSpeedMin.getDocument()
        .removeDocumentListener(docLstZoneSpeedMin);

    docLstZoneSpeedName = null;
    docLstZoneSpeedMax = null;
    docLstZoneSpeedMin = null;
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setLayout(new BorderLayout());
    this.setBorder(BorderFactory
        .createTitledBorder(null,
                            "   ",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            GuiFont.FONT_BOLD,
                            null));
    this.add(getJPanelNorth(), BorderLayout.NORTH);
    this.add(getJPanelCenter(), BorderLayout.CENTER);

    // evenement
    jCheckBoxDefaultActivity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        owner.setDefaultActivity(model.getDataActivity(),
                                 jCheckBoxDefaultActivity.isSelected());
      }
    });

    jComboBoxSpeedAndPaceUnits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        model.setUnitSpeedAndSpace(JPanelUserActivity.this,
                                   (String) cb.getSelectedItem());
      }
    });

    jButtonCalculate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        model.calculateHeartZones(JPanelUserActivity.this);
      }
    });

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  private JPanel getJPanelNorth() {
    if (jPanelNorth == null) {
      jLabelMaxHeartRate = new JLabel("Frequence cardiaque maximale :");
      jLabelMaxHeartRate.setFont(GuiFont.FONT_PLAIN);

      jPanelNorth = new JPanel();
      jPanelNorth.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
      jPanelNorth.add(jLabelMaxHeartRate);
      jPanelNorth.add(getJTextFieldMaxHeartRate());
      jPanelNorth.add(getJCheckBoxDefaultActivity());
      jPanelNorth.add(getJComboBoxIconActivity());
    }
    return jPanelNorth;
  }

  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(new GridBagLayout());

      GridBagConstraints g = new GridBagConstraints();
      g.anchor = GridBagConstraints.EAST;
      jPanelCenter.add(getJPanelZoneHeart(), g);

      g = new GridBagConstraints();
      g.weightx = 0.5;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      jPanelCenter.add(getJPanelZoneSpeed(), g);

    }
    return jPanelCenter;
  }

  /**
   * @return
   */
  public JCheckBox getJCheckBoxDefaultActivity() {
    if (jCheckBoxDefaultActivity == null) {
      jCheckBoxDefaultActivity = new JCheckBox();
      jCheckBoxDefaultActivity.setFont(GuiFont.FONT_PLAIN);
    }
    return jCheckBoxDefaultActivity;
  }

  public JComboBoxIconActivity getJComboBoxIconActivity() {
    if (jComboBoxIconActivity == null) {
      jComboBoxIconActivity = new JComboBoxIconActivity();
      jComboBoxIconActivity.setFont(GuiFont.FONT_PLAIN);
    }
    return jComboBoxIconActivity;
  }

  /**
   * This method initializes jPanelHeart
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelZoneHeart() {
    if (jPanelZoneHeart == null) {
      jPanelZoneHeart = new JPanel();
      jPanelZoneHeart.setLayout(new GridBagLayout());
      borderPanelZoneHeart = BorderFactory
          .createTitledBorder(null,
                              "Zones de frequence cardiaque",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelZoneHeart.setBorder(borderPanelZoneHeart);

      Insets insets1 = new Insets(0, 0, 2, 10);

      jLabelLibZoneHeart = new JLabel("Zone :");
      jLabelLibZoneHeart.setFont(GuiFont.FONT_PLAIN);
      jLabelLibZoneHeart.setHorizontalAlignment(SwingConstants.RIGHT);
      GridBagConstraints g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      jPanelZoneHeart.add(jLabelLibZoneHeart, g);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.weightx = 1.0;
      jPanelZoneHeart.add(getJComboBoxZoneHeart(), g);
      jLabelLibZoneHeart.setLabelFor(getJComboBoxZoneHeart());

      jLabelLibZoneHeartMin = new JLabel("min. (bpm) :");
      jLabelLibZoneHeartMin.setFont(GuiFont.FONT_PLAIN);
      jLabelLibZoneHeartMin.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibZoneHeartMin.setHorizontalAlignment(SwingConstants.RIGHT);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      jPanelZoneHeart.add(jLabelLibZoneHeartMin, g);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      jPanelZoneHeart.add(getJTextFieldZoneHeartMin(), g);
      jLabelLibZoneHeartMin.setLabelFor(getJTextFieldZoneHeartMin());

      jLabelLibZoneHeartMax = new JLabel("max. (bpm) :");
      jLabelLibZoneHeartMax.setFont(GuiFont.FONT_PLAIN);
      jLabelLibZoneHeartMax.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibZoneHeartMax.setHorizontalAlignment(SwingConstants.RIGHT);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      jPanelZoneHeart.add(jLabelLibZoneHeartMax, g);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      jPanelZoneHeart.add(getJTextFieldZoneHeartMax(), g);
      jLabelLibZoneHeartMax.setLabelFor(getJTextFieldZoneHeartMax());

      jLabelLibBpm = new JLabel("Unite :");
      jLabelLibBpm.setFont(GuiFont.FONT_PLAIN);
      jLabelLibBpm.setHorizontalAlignment(SwingConstants.RIGHT);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      jPanelZoneHeart.add(jLabelLibBpm, g);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.WEST;
      jPanelZoneHeart.add(getJRadioButtonBpm(), g);
      jLabelLibBpm.setLabelFor(getJRadioButtonBpm());
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      jPanelZoneHeart.add(getJRadioButtonPourFcMax(), g);

      jButtonCalculate = new JButton();
      jButtonCalculate.setFont(GuiFont.FONT_PLAIN);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      jPanelZoneHeart.add(jButtonCalculate, g);
    }
    return jPanelZoneHeart;
  }

  /**
   * This method initializes jComboBox.
   * 
   * @return javax.swing.JComboBox
   */
  public JComboBox getJComboBoxZoneHeart() {
    if (jComboBoxZoneHeart == null) {
      jComboBoxZoneHeart = new JComboBox();
      jComboBoxZoneHeart.setModel(new DefaultComboBoxModel(ZONES_HEART));
      jComboBoxZoneHeart.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(70, 23);
      jComboBoxZoneHeart.setPreferredSize(dim);
      jComboBoxZoneHeart.setMaximumSize(dim);
    }

    return jComboBoxZoneHeart;
  }

  /**
   * This method initializes jTextFieldHeartMin.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldZoneHeartMin() {
    if (jTextFieldZoneHeartMin == null) {
      jTextFieldZoneHeartMin = new JFormattedTextField();
      Dimension dim = new Dimension(45, 23);
      jTextFieldZoneHeartMin.setPreferredSize(dim);
      jTextFieldZoneHeartMin.setMinimumSize(dim);
      jTextFieldZoneHeartMin.setValue(0);
      jTextFieldZoneHeartMin.setFormatterFactory(TextFormatterFactory
          .createNumber(3, 0));
      jTextFieldZoneHeartMin.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldZoneHeartMin;
  }

  /**
   * This method initializes getJTextFieldHeartMax.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldZoneHeartMax() {
    if (jTextFieldZoneHeartMax == null) {
      jTextFieldZoneHeartMax = new JFormattedTextField();
      Dimension dim = new Dimension(45, 23);
      jTextFieldZoneHeartMax.setPreferredSize(dim);
      jTextFieldZoneHeartMax.setMinimumSize(dim);
      jTextFieldZoneHeartMax.setValue(0);
      jTextFieldZoneHeartMax.setFormatterFactory(TextFormatterFactory
          .createNumber(3, 0));
      jTextFieldZoneHeartMax.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldZoneHeartMax;
  }

  /**
   * This method initializes jPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelZoneSpeed() {
    if (jPanelZoneSpeed == null) {
      jPanelZoneSpeed = new JPanel();
      GridBagLayout gridbagLayout = new GridBagLayout();
      jPanelZoneSpeed.setLayout(gridbagLayout);

      Insets insets1 = new Insets(0, 0, 2, 10);
      Insets insets2 = new Insets(0, 0, 2, 0);

      jLabelLibZoneSpeed = new JLabel("Zones :");
      jLabelLibZoneSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabelLibZoneSpeed.setFont(GuiFont.FONT_PLAIN);
      GridBagConstraints g = new GridBagConstraints();
      g.insets = insets1;
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.gridx = 0;
      g.gridy = 0;
      jPanelZoneSpeed.add(jLabelLibZoneSpeed, g);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.weightx = 1.0;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.anchor = GridBagConstraints.WEST;
      g.gridx = 1;
      g.gridy = 0;
      jPanelZoneSpeed.add(getJComboBoxZoneSpeed(), g);
      jLabelLibZoneSpeed.setLabelFor(getJComboBoxZoneSpeed());

      jLabelLibZoneSpeedName = new JLabel("Nom :");
      jLabelLibZoneSpeedName.setFont(GuiFont.FONT_PLAIN);
      jLabelLibZoneSpeedName.setHorizontalAlignment(SwingConstants.RIGHT);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      g.weightx = 1.0;
      g.gridx = 0;
      g.gridy = 1;
      gridbagLayout.setConstraints(jLabelLibZoneSpeedName, g);
      jPanelZoneSpeed.add(jLabelLibZoneSpeedName);
      g = new GridBagConstraints();
      g.insets = insets2;
      g.weightx = 1.0;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.anchor = GridBagConstraints.WEST;
      g.gridx = 1;
      g.gridy = 1;
      gridbagLayout.setConstraints(getJTextFieldSpeedName(), g);
      jPanelZoneSpeed.add(getJTextFieldSpeedName());
      jLabelLibZoneSpeedName.setLabelFor(getJTextFieldSpeedName());

      jLabelLibSpeedMin = new JLabel("Vitesse min. :");
      jLabelLibSpeedMin.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMin.setHorizontalAlignment(SwingConstants.RIGHT);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      g.weightx = 1.0;
      g.ipadx = 10;
      g.gridx = 0;
      g.gridy = 2;
      gridbagLayout.setConstraints(jLabelLibSpeedMin, g);
      jPanelZoneSpeed.add(jLabelLibSpeedMin);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.gridx = 1;
      g.gridy = 2;
      gridbagLayout.setConstraints(getJTextFieldZoneSpeedMin(), g);
      jPanelZoneSpeed.add(getJTextFieldZoneSpeedMin());
      g = new GridBagConstraints();
      g.insets = insets2;
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.gridx = 2;
      g.gridy = 2;
      gridbagLayout.setConstraints(getJComboBoxSpeedAndPaceUnit(), g);
      jPanelZoneSpeed.add(getJComboBoxSpeedAndPaceUnit());

      jLabelLibSpeedMax = new JLabel("Vitesse max. :");
      jLabelLibSpeedMax.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMax.setHorizontalAlignment(SwingConstants.RIGHT);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.anchor = GridBagConstraints.EAST;
      g.weightx = 1.0;
      g.ipadx = 10;
      g.gridx = 0;
      g.gridy = 3;
      gridbagLayout.setConstraints(jLabelLibSpeedMax, g);
      jPanelZoneSpeed.add(jLabelLibSpeedMax);
      g = new GridBagConstraints();
      g.insets = insets1;
      g.weightx = 1.0;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.anchor = GridBagConstraints.WEST;
      g.gridx = 1;
      g.gridy = 3;
      gridbagLayout.setConstraints(getJTextFieldZoneSpeedMax(), g);
      jPanelZoneSpeed.add(getJTextFieldZoneSpeedMax());
      jLabelLibSpeedMax.setLabelFor(getJTextFieldZoneSpeedMax());

      borderPanelZoneSpeed = BorderFactory
          .createTitledBorder(null,
                              "Zones de vitesse",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelZoneSpeed.setBorder(borderPanelZoneSpeed);
    }
    return jPanelZoneSpeed;
  }

  /**
   * This method initializes jComboBoxZoneSpeed.
   * 
   * @return javax.swing.JComboBox
   */
  public JComboBox getJComboBoxZoneSpeed() {
    if (jComboBoxZoneSpeed == null) {
      jComboBoxZoneSpeed = new JComboBox();
      jComboBoxZoneSpeed.setModel(new DefaultComboBoxModel(ZONES_SPEED));
      jComboBoxZoneSpeed.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(70, 23);
      jComboBoxZoneSpeed.setPreferredSize(dim);
      jComboBoxZoneSpeed.setMinimumSize(dim);
    }
    return jComboBoxZoneSpeed;
  }

  /**
   * This method initializes jTextField.
   * 
   * @return javax.swing.JTextField
   */
  public JTextFieldLength getJTextFieldSpeedName() {
    if (jTextFieldZoneSpeedName == null) {
      jTextFieldZoneSpeedName = new JTextFieldLength();
      Dimension dim = new Dimension(290, 23);
      jTextFieldZoneSpeedName.setPreferredSize(dim);
      jTextFieldZoneSpeedName.setMinimumSize(dim);

      jTextFieldZoneSpeedName.setMaxCharacters(15);
      jTextFieldZoneSpeedName.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldZoneSpeedName;
  }

  /**
   * This method initializes jTextFieldZoneSpeedMin.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldZoneSpeedMin() {
    if (jTextFieldZoneSpeedMin == null) {
      jTextFieldZoneSpeedMin = new JFormattedTextField();
      Dimension dim = new Dimension(70, 23);
      jTextFieldZoneSpeedMin.setPreferredSize(dim);
      jTextFieldZoneSpeedMin.setMinimumSize(dim);
      jTextFieldZoneSpeedMin.setFormatterFactory(SPEED_FORMATTER_FACTORY);
      jTextFieldZoneSpeedMin.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldZoneSpeedMin;
  }

  /**
   * This method initializes jTextFieldZoneSpeedMax.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldZoneSpeedMax() {
    if (jTextFieldZoneSpeedMax == null) {
      jTextFieldZoneSpeedMax = new JFormattedTextField();
      Dimension dim = new Dimension(70, 23);
      jTextFieldZoneSpeedMax.setPreferredSize(dim);
      jTextFieldZoneSpeedMax.setMinimumSize(dim);
      jTextFieldZoneSpeedMax.setFormatterFactory(SPEED_FORMATTER_FACTORY);
      jTextFieldZoneSpeedMax.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldZoneSpeedMax;
  }

  /**
   * This method initializes unitSpeed.
   * 
   * @return javax.swing.JComboBox
   */
  public JComboBox getJComboBoxSpeedAndPaceUnit() {
    if (jComboBoxSpeedAndPaceUnits == null) {
      jComboBoxSpeedAndPaceUnits = new JComboBox();
      jComboBoxSpeedAndPaceUnits.setFont(GuiFont.FONT_PLAIN);
      jComboBoxSpeedAndPaceUnits
          .setModel(new DefaultComboBoxModel(SpeedPaceUnit.units()));
      jComboBoxSpeedAndPaceUnits
          .setSelectedItem(SpeedPaceUnit.getDefaultUnit());
    }
    return jComboBoxSpeedAndPaceUnits;
  }

  /**
   * This method initializes jRadioButtonBpm.
   * 
   * @return javax.swing.JRadioButton
   */
  public JRadioButton getJRadioButtonBpm() {
    if (jRadioButtonBpm == null) {
      jRadioButtonBpm = new JRadioButton("bpm");
      jRadioButtonBpm.setFont(GuiFont.FONT_PLAIN);
      if (heartButtonGroup == null) {
        heartButtonGroup = new ButtonGroup();
      }
      heartButtonGroup.add(jRadioButtonBpm);
      jRadioButtonBpm.setSelected(true);
    }
    return jRadioButtonBpm;
  }

  /**
   * This method initializes jRadioButtonBpm.
   * 
   * @return javax.swing.JRadioButton
   */
  public JRadioButton getJRadioButtonPourFcMax() {
    if (jRadioButtonPourFcMax == null) {
      jRadioButtonPourFcMax = new JRadioButton("% FC max.");
      jRadioButtonPourFcMax.setFont(GuiFont.FONT_PLAIN);
      if (heartButtonGroup == null) {
        heartButtonGroup = new ButtonGroup();
      }
      heartButtonGroup.add(jRadioButtonPourFcMax);
    }
    return jRadioButtonPourFcMax;
  }

  /**
   * This method initializes jTextFieldMaxHeartRate.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldMaxHeartRate() {
    if (jTextFieldMaxHeartRate == null) {
      jTextFieldMaxHeartRate = new JFormattedTextField();
      Dimension dim = new Dimension(45, 23);
      jTextFieldMaxHeartRate.setPreferredSize(dim);
      jTextFieldMaxHeartRate.setMinimumSize(dim);
      jTextFieldMaxHeartRate.setValue(0);
      jTextFieldMaxHeartRate.setFormatterFactory(TextFormatterFactory
          .createNumber(3, 0));
      jTextFieldMaxHeartRate.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldMaxHeartRate;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class JComboBoxIconActivity extends JComboBox {

    public JComboBoxIconActivity() {
      super(ImagesActivityRepository.getImageIcons());
      setRenderer(new ComboBoxRenderer());
    }

    /**
     * @return Restitue le nom de l'icone selection&eacute;e.
     */
    public String getSelectedIconName() {
      return (getSelectedIndex() == -1) ? null : ImagesActivityRepository
          .getImageName(getSelectedIndex());
    }

    private class ComboBoxRenderer extends DefaultListCellRenderer {

      @Override
      public Component getListCellRendererComponent(JList list,
                                                    Object value,
                                                    int index,
                                                    boolean isSelected,
                                                    boolean cellHasFocus) {

        ImageIcon icon = (ImageIcon) value;
        setIcon(icon);

        return this;
      }

    }

    public void setSelectedIcon(String iconName) {
      setSelectedIndex(ImagesActivityRepository.getImageIndex(iconName));
    }
  }
} // @jve:decl-index=0:visual-constraint="10,10"
