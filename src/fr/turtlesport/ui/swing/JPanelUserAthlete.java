package fr.turtlesport.ui.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;

import org.jdesktop.swingx.JXDatePicker;

import fr.turtlesport.db.DataUser;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JButtonPhoto;
import fr.turtlesport.ui.swing.component.JXDatePickerLocale;
import fr.turtlesport.ui.swing.component.PhotoEvent;
import fr.turtlesport.ui.swing.component.PhotoListener;
import fr.turtlesport.ui.swing.model.GenericModelDocListener;
import fr.turtlesport.ui.swing.model.ModelAthlete;
import fr.turtlesport.unit.HeightUnit;
import fr.turtlesport.unit.WeightUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.Bmi;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis
 * 
 */
public class JPanelUserAthlete extends JPanel implements LanguageListener,
                                             UnitListener {
  private static TurtleLogger                 log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelUserAthlete.class);
  }

  private JLabel                              jLabelLibName;

  private JLabel                              jLabelLibFirstName;

  private JLabel                              jLabelLibBirthday;

  private JButtonPhoto                        jButtonPhoto;

  private JLabel                              jLabelLibWeight;

  private JLabel                              jLabelLastName;

  private JLabel                              jLabelFirstName;

  private JFormattedTextField                 jTextFieldWeight;

  private JComboBox                           jComboBoxWeightUnits;

  private JLabel                              jLabelLibHeight;

  private JFormattedTextField                 jTextFieldHeight;

  private JComboBox                           jComboBoxHeightUnits;

  private JLabel                              jLabelLibImc;

  private JLabel                              jLabelValImc;

  private JLabel                              jLabelMinHeartRate;

  private JFormattedTextField                 jTextFieldMinHeartRate;

  // model
  private ModelAthlete                        model;

  private JPanelUserProfile                   owner;

  private JLabel                              jLabelLibSexe;

  private JXDatePickerLocale                  jXDatePicker;

  private JRadioButton                        jRadioButtonFemale;

  private JRadioButton                        jRadioButtonMale;

  private ButtonGroup                         sexButtonGroup;

  // Listener
  private GenericModelDocListener             docLstUserWeight;

  private GenericModelDocListener             docLstUserHeight;

  private GenericModelDocListener             docLstUserMinHeartRate;

  private PhotoListener                       photoListener;

  private ActionListener                      actionListenerMale;

  private ActionListener                      actionListenerFemale;

  private FocusListener                       focusListenerWeight;

  private FocusListener                       focusListenerHeight;

  private FocusListener                       focusListenerMinHeartRate;

  private GenericModelDocListener             docLstUserBirthDate;

  // Formatter
  public static final DefaultFormatterFactory TIME_FORMATTER_FACTORY = TextFormatterFactory
                                                                         .createTime();

  private static ResourceBundle               rb;

  protected JPanelUserAthlete(JPanelUserProfile owner) {
    super();
    this.owner = owner;
    initialize();
  }

  public void setModel(ModelAthlete model) {
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
    if (model != null) {
      model.performedUnit(this, event);
    }
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

  /**
   * 
   */
  public void addEvents(final DataUser data) {
    try {
      removeEvents();

      docLstUserWeight = new GenericModelDocListener(jTextFieldWeight,
                                                     data,
                                                     "setWeight",
                                                     Float.TYPE);
      jTextFieldWeight.getDocument().addDocumentListener(docLstUserWeight);

      docLstUserHeight = new GenericModelDocListener(jTextFieldHeight,
                                                     data,
                                                     "setHeight",
                                                     Float.TYPE);
      jTextFieldHeight.getDocument().addDocumentListener(docLstUserHeight);

      docLstUserBirthDate = new GenericModelDocListener(jXDatePicker
          .getEditor(), data, "setBirthDate", Date.class);

      focusListenerWeight = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          fireImc(data);
        }
      };
      jTextFieldWeight.addFocusListener(focusListenerWeight);

      focusListenerHeight = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          fireImc(data);
        }
      };
      jTextFieldHeight.addFocusListener(focusListenerHeight);

      docLstUserMinHeartRate = new GenericModelDocListener(jTextFieldMinHeartRate,
                                                           data,
                                                           "setMinHeartRate",
                                                           Integer.TYPE);
      jTextFieldMinHeartRate.getDocument()
          .addDocumentListener(docLstUserMinHeartRate);
      focusListenerMinHeartRate = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          docLstUserMinHeartRate.fireUpdate();
        }
      };
      jTextFieldMinHeartRate.addFocusListener(focusListenerMinHeartRate);

      jXDatePicker.getEditor().getDocument()
          .addDocumentListener(docLstUserBirthDate);

      photoListener = new PhotoListener() {
        public void photoChanged(PhotoEvent event) {
          if (event.getPath() != null) {
            data.setPath(event.getPath());
          }
        }
      };
      jButtonPhoto.addPhotoListener(photoListener);

      actionListenerMale = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          data.setMale(true);
        }
      };
      jRadioButtonMale.addActionListener(actionListenerMale);

      actionListenerFemale = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          data.setMale(false);
        }
      };
      jRadioButtonFemale.addActionListener(actionListenerFemale);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }

    fireImc(data);
  }

  /**
   * Suppression des listeners.
   */
  public void removeEvents() {
    if (docLstUserWeight == null) {
      return;
    }

    jTextFieldWeight.getDocument().removeDocumentListener(docLstUserWeight);
    jTextFieldHeight.getDocument().removeDocumentListener(docLstUserHeight);
    jTextFieldMinHeartRate.getDocument()
        .removeDocumentListener(docLstUserMinHeartRate);

    // jTextFieldBirthDate.getDocument()
    // .removeDocumentListener(docLstUserBirthDate);
    jButtonPhoto.removePhotoListener(photoListener);
    jRadioButtonMale.removeActionListener(actionListenerMale);
    jRadioButtonFemale.removeActionListener(actionListenerFemale);
    jTextFieldWeight.removeFocusListener(focusListenerWeight);
    jTextFieldHeight.removeFocusListener(focusListenerHeight);
    jTextFieldMinHeartRate.removeFocusListener(focusListenerMinHeartRate);

    docLstUserWeight = null;
    docLstUserHeight = null;
    docLstUserMinHeartRate = null;
    photoListener = null;
    actionListenerMale = null;
    actionListenerFemale = null;
    focusListenerWeight = null;
    focusListenerHeight = null;
    focusListenerMinHeartRate = null;
  }

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, JPanelUserAthlete.class);

    // on remet a jour le date formatter.
    jXDatePicker.setLanguage(lang);

    jLabelLibName.setText(rb.getString("jLabelLibName"));
    jLabelLibFirstName.setText(rb.getString("jLabelLibFirstName"));
    jLabelLibSexe.setText(rb.getString("jLabelLibSexe"));
    jLabelLibBirthday.setText(rb.getString("jLabelLibBirthday"));
    jLabelLibWeight.setText(rb.getString("jLabelLibWeight"));
    jLabelLibHeight.setText(rb.getString("jLabelLibHeight"));
    jLabelLibImc.setText(rb.getString("jLabelLibImc"));
    jButtonPhoto.setText(rb.getString("jButtonPhoto"));
    jRadioButtonMale.setText(rb.getString("jRadioButtonMale"));
    jRadioButtonFemale.setText(rb.getString("jRadioButtonFemale"));
    jLabelMinHeartRate.setText(rb.getString("jLabelMinHeartRate"));
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
  public ModelAthlete getModel() {
    return model;
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {

    GridBagLayout gridbagLayout = new GridBagLayout();
    this.setLayout(gridbagLayout);
    this.setBorder(BorderFactory
        .createTitledBorder(null,
                            "",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            GuiFont.FONT_PLAIN,
                            null));

    Insets insets1 = new Insets(0, 0, 2, 10);

    GridBagConstraints g = new GridBagConstraints();
    g.fill = GridBagConstraints.HORIZONTAL;
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    g.gridheight = 6;
    g.weighty = 0.0;
    g.weightx = 0.0;
    this.add(getJButtonPhoto(), g);

    // Nom
    jLabelLibName = new JLabel("Nom :");
    jLabelLibName.setFont(GuiFont.FONT_PLAIN);
    jLabelLibName.setHorizontalAlignment(SwingConstants.RIGHT);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.EAST;
    this.add(jLabelLibName, g);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    this.add(getJLabelLastName(), g);
    jLabelLibName.setLabelFor(getJLabelLastName());

    // Prenom
    jLabelLibFirstName = new JLabel("Prenom :");
    jLabelLibFirstName.setFont(GuiFont.FONT_PLAIN);
    jLabelLibFirstName.setHorizontalAlignment(SwingConstants.RIGHT);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.EAST;
    this.add(jLabelLibFirstName, g);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    this.add(getJLabelFirstName(), g);
    jLabelLibFirstName.setLabelFor(getJLabelFirstName());

    // Date de naissance
    jLabelLibBirthday = new JLabel("Date de naissance :");
    jLabelLibBirthday.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabelLibBirthday.setFont(GuiFont.FONT_PLAIN);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.EAST;
    this.add(jLabelLibBirthday, g);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    this.add(getJDatePicker(), g);
    jLabelLibBirthday.setLabelFor(getJDatePicker());
    jLabelLibSexe = new JLabel("Sexe :");
    jLabelLibSexe.setHorizontalAlignment(SwingConstants.LEFT);
    jLabelLibSexe.setFont(GuiFont.FONT_PLAIN);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    this.add(jLabelLibSexe, g);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.RELATIVE;
    this.add(getJRadioButtonMale(), g);
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    this.add(getJRadioButtonFemale(), g);

    // Poids
    jLabelLibWeight = new JLabel("Poids :");
    jLabelLibWeight.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabelLibWeight.setFont(GuiFont.FONT_PLAIN);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.anchor = GridBagConstraints.EAST;
    this.add(jLabelLibWeight, g);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    this.add(getJTextFieldWeight(), g);
    jLabelLibWeight.setLabelFor(getJTextFieldWeight());
    g = new GridBagConstraints();
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    this.add(getJComboBoxWeightUnits(), g);
    jLabelLibImc = new JLabel("IMC :");
    jLabelLibImc.setHorizontalAlignment(SwingConstants.LEFT);
    jLabelLibImc.setFont(GuiFont.FONT_PLAIN);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    this.add(jLabelLibImc, g);
    jLabelValImc = new JLabel();
    jLabelValImc.setHorizontalAlignment(SwingConstants.LEFT);
    jLabelValImc.setFont(GuiFont.FONT_PLAIN);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    this.add(jLabelValImc, g);
    jLabelLibImc.setLabelFor(jLabelValImc);

    // Taille
    jLabelLibHeight = new JLabel("Taille :");
    jLabelLibHeight.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabelLibHeight.setFont(GuiFont.FONT_PLAIN);
    g = new GridBagConstraints();
    g.fill = GridBagConstraints.HORIZONTAL;
    g.insets = insets1;
    g.anchor = GridBagConstraints.EAST;
    this.add(jLabelLibHeight, g);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    this.add(getJTextFieldHeight(), g);
    jLabelLibWeight.setLabelFor(getJTextFieldHeight());
    g = new GridBagConstraints();
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    this.add(getJComboBoxHeightUnits(), g);

    // Frequence cardiaque
    jLabelMinHeartRate = new JLabel();
    jLabelMinHeartRate.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabelMinHeartRate.setFont(GuiFont.FONT_PLAIN);
    g = new GridBagConstraints();
    g.fill = GridBagConstraints.HORIZONTAL;
    g.insets = insets1;
    g.anchor = GridBagConstraints.EAST;
    g.gridwidth = 2;
    this.add(jLabelMinHeartRate, g);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    jLabelMinHeartRate.setLabelFor(getJTextFieldMinHeartRate());
    this.add(getJTextFieldMinHeartRate(), g);

    // Evenements
    jComboBoxWeightUnits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
        model.setUnitWeight(JPanelUserAthlete.this, value);
      }
    });

    jComboBoxHeightUnits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
        model.setUnitHeight(JPanelUserAthlete.this, value);
      }
    });

    jButtonPhoto.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Object res = JDialogChooseImage.prompt();
        if (res != null) {
          if (res instanceof ImageDesc) {
            jButtonPhoto.setFile((ImageDesc) res);
          }
          else {
            jButtonPhoto.setFile((File) res);
          }
        }
      }
    });

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  /**
   * This method initializes jButton.
   * 
   * @return javax.swing.JButton
   */
  public JButtonPhoto getJButtonPhoto() {
    if (jButtonPhoto == null) {
      jButtonPhoto = new JButtonPhoto();
      jButtonPhoto.setFont(GuiFont.FONT_PLAIN);
      jButtonPhoto.setText("Photo");
      jButtonPhoto.setVerticalAlignment(SwingConstants.CENTER);
      jButtonPhoto.setHorizontalAlignment(SwingConstants.CENTER);
      Dimension dim = new Dimension(160, 110);
      jButtonPhoto.setPreferredSize(dim);
      jButtonPhoto.setMinimumSize(dim);
    }
    return jButtonPhoto;
  }

  /**
   * This method initializes jTextField.
   * 
   * @return javax.swing.JTextField
   */
  public JLabel getJLabelLastName() {
    if (jLabelLastName == null) {
      jLabelLastName = new JLabel();
      Dimension dim = new Dimension(300, 23);
      jLabelLastName.setPreferredSize(dim);
      jLabelLastName.setMinimumSize(dim);
      jLabelLastName.setFont(GuiFont.FONT_PLAIN);
    }
    return jLabelLastName;
  }

  /**
   * This method initializes jTextField.
   * 
   * @return javax.swing.JTextField
   */
  public JLabel getJLabelFirstName() {
    if (jLabelFirstName == null) {
      jLabelFirstName = new JLabel();
      Dimension dim = new Dimension(300, 23);
      jLabelFirstName.setPreferredSize(dim);
      jLabelFirstName.setMinimumSize(dim);
      jLabelFirstName.setFont(GuiFont.FONT_PLAIN);
    }
    return jLabelFirstName;
  }

  /**
   * This method initializes jRadioButtonMale.
   * 
   * @return javax.swing.JRadioButton
   */
  public JRadioButton getJRadioButtonMale() {
    if (jRadioButtonMale == null) {
      jRadioButtonMale = new JRadioButton();
      jRadioButtonMale.setHorizontalAlignment(SwingConstants.LEFT);
      jRadioButtonMale.setFont(GuiFont.FONT_PLAIN);
      if (sexButtonGroup == null) {
        sexButtonGroup = new ButtonGroup();
      }
      sexButtonGroup.add(jRadioButtonMale);
      jLabelLibSexe.setLabelFor(jRadioButtonMale);
    }
    return jRadioButtonMale;
  }

  /**
   * This method initializes jRadioButtonFemale.
   * 
   * @return javax.swing.JRadioButton
   */
  public JRadioButton getJRadioButtonFemale() {
    if (jRadioButtonFemale == null) {
      jRadioButtonFemale = new JRadioButton();
      jRadioButtonFemale.setHorizontalAlignment(SwingConstants.LEFT);
      jRadioButtonFemale.setFont(GuiFont.FONT_PLAIN);
      if (sexButtonGroup == null) {
        sexButtonGroup = new ButtonGroup();
      }
      sexButtonGroup.add(jRadioButtonFemale);
      jRadioButtonFemale.setSelected(true);
    }
    return jRadioButtonFemale;
  }

  /**
   * This method initializes jTextField.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldWeight() {
    if (jTextFieldWeight == null) {
      jTextFieldWeight = new JFormattedTextField();
      jTextFieldWeight
          .setFormatterFactory(TextFormatterFactory.createNumber(3, 1));
      jTextFieldWeight.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(60, 23);
      jTextFieldWeight.setPreferredSize(dim);
      jTextFieldWeight.setMinimumSize(dim);
    }
    return jTextFieldWeight;
  }

  /**
   * This method initializes jTextField.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldHeight() {
    if (jTextFieldHeight == null) {
      jTextFieldHeight = new JFormattedTextField();
      jTextFieldHeight
          .setFormatterFactory(TextFormatterFactory.createNumber(3, 2));
      jTextFieldHeight.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(60, 23);
      jTextFieldHeight.setPreferredSize(dim);
      jTextFieldHeight.setMinimumSize(dim);
    }
    return jTextFieldHeight;
  }

  /**
   * This method initializes jTextFieldMinHeartRate.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldMinHeartRate() {
    if (jTextFieldMinHeartRate == null) {
      jTextFieldMinHeartRate = new JFormattedTextField();
      jTextFieldMinHeartRate.setFormatterFactory(TextFormatterFactory
          .createNumber(2, 0));
      jTextFieldMinHeartRate.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(45, 23);
      jTextFieldMinHeartRate.setPreferredSize(dim);
      jTextFieldMinHeartRate.setMinimumSize(dim);
    }
    return jTextFieldMinHeartRate;
  }

  /**
   * This method initializes jComboBoxWeightUnits.
   * 
   * @return javax.swing.JComboBox
   */
  public JComboBox getJComboBoxWeightUnits() {
    if (jComboBoxWeightUnits == null) {
      jComboBoxWeightUnits = new JComboBox();
      jComboBoxWeightUnits
          .setModel(new DefaultComboBoxModel(WeightUnit.units()));
      jComboBoxWeightUnits.setSelectedItem(WeightUnit.getDefaultUnit());
      jComboBoxWeightUnits.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(90, 23);
      jComboBoxWeightUnits.setPreferredSize(dim);
      jComboBoxWeightUnits.setMinimumSize(dim);
    }
    return jComboBoxWeightUnits;
  }

  /**
   * This method initializes jComboBoxHeightUnits.
   * 
   * @return javax.swing.JComboBox
   */
  public JComboBox getJComboBoxHeightUnits() {
    if (jComboBoxHeightUnits == null) {
      jComboBoxHeightUnits = new JComboBox();
      jComboBoxHeightUnits
          .setModel(new DefaultComboBoxModel(HeightUnit.units()));
      jComboBoxHeightUnits.setSelectedItem(HeightUnit.getDefaultUnit());
      jComboBoxHeightUnits.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(90, 23);
      jComboBoxHeightUnits.setPreferredSize(dim);
      jComboBoxHeightUnits.setMinimumSize(dim);
    }
    return jComboBoxHeightUnits;
  }

  /**
   * This method initializes jTextFieldBirthDate.
   * 
   * @return javax.swing.JTextField
   */
  public JXDatePicker getJDatePicker() {
    if (jXDatePicker == null) {
      jXDatePicker = new JXDatePickerLocale();
      jXDatePicker.setFont(GuiFont.FONT_PLAIN);
    }
    return jXDatePicker;
  }

  /**
   * Mis &agrave jour de l'IMC.
   */
  private void fireImc(DataUser data) {
    docLstUserWeight.fireUpdate();
    docLstUserHeight.fireUpdate();

    if (data.getHeight() > 0 && data.getWeight() > 0) {

      StringBuilder st = new StringBuilder();
      st.append(Bmi.computeFormat((String) jComboBoxWeightUnits
                                      .getSelectedItem(),
                                  (String) jComboBoxHeightUnits
                                      .getSelectedItem(),
                                  data.getWeight(),
                                  data.getHeight()));
      st.append(' ');
      st.append(Bmi.getLibelle((String) jComboBoxWeightUnits.getSelectedItem(),
                               (String) jComboBoxHeightUnits.getSelectedItem(),
                               data.getWeight(),
                               data.getHeight()));
      jLabelValImc.setText(st.toString());
    }
    else {
      jLabelValImc.setText(null);
    }
  }

} // @jve:decl-index=0:visual-constraint="10,10"
