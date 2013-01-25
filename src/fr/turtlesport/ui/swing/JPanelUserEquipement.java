package fr.turtlesport.ui.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;

import fr.turtlesport.db.DataEquipement;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JButtonPhoto;
import fr.turtlesport.ui.swing.component.PhotoEvent;
import fr.turtlesport.ui.swing.component.PhotoListener;
import fr.turtlesport.ui.swing.component.TextFormatterFactory;
import fr.turtlesport.ui.swing.model.GenericModelCheckBoxListener;
import fr.turtlesport.ui.swing.model.GenericModelDocListener;
import fr.turtlesport.ui.swing.model.ModelEquipement;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.WeightUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis
 * 
 */
public class JPanelUserEquipement extends JPanel implements LanguageListener,
                                                UnitListener {
  private static TurtleLogger                 log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelUserEquipement.class);
  }

  private JLabel                              jLabelLibWeight;

  private JFormattedTextField                 jTextFieldWeight;

  private JComboBox                           jComboBoxWeightUnits;

  private JButtonPhoto                        jButtonPhoto;

  private JLabel                              jLabelLibDistanceRun;

  private JLabel                              jLabelValDistanceRun;

  private JCheckBox                           jCheckBoxWarning;

  private JLabel                              jLabelLibLastUsed;

  private JLabel                              jLabelValLastUsed;

  private JLabel                              jLabelLibFirstUsed;

  private JLabel                              jLabelValFirstUsed;

  private JLabel                              jLabelLibDistanceMax;

  private JFormattedTextField                 jTextFieldDistanceMax;

  private JComboBox                           jComboBoxDistanceUnits;

  private JCheckBox                           jCheckBoxDefaultEquipment;

  // model
  private ModelEquipement                     model;

  // Listener
  private GenericModelDocListener             docLstEquimentWeight;

  private GenericModelDocListener             docLstEquimentDistanceMax;

  private GenericModelCheckBoxListener        checkLstEquimentWarning;

  private PhotoListener                       photoListener;

  private JPanelUserProfile                   owner;

  // Formatter
  public static final DefaultFormatterFactory TIME_FORMATTER_FACTORY = TextFormatterFactory
                                                                         .createTime();

  private static ResourceBundle               rb;

  protected JPanelUserEquipement(JPanelUserProfile owner) {
    super();
    this.owner = owner;
    initialize();
  }

  public void setModel(ModelEquipement model) {
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

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, JPanelUserEquipement.class);

    jLabelLibWeight.setText(rb.getString("jLabelLibWeight"));
    jLabelLibDistanceMax.setText(rb.getString("jLabelLibDistanceMax"));
    jLabelLibDistanceRun.setText(rb.getString("jLabelLibDistanceRun"));
    jLabelLibFirstUsed.setText(rb.getString("jLabelLibFirstUsed"));
    jLabelLibLastUsed.setText(rb.getString("jLabelLibLastUsed"));
    jButtonPhoto.setText(rb.getString("jButtonPhoto"));
    jCheckBoxWarning.setText(rb.getString("jCheckBoxWarning"));
    jCheckBoxDefaultEquipment
        .setText(rb.getString("jCheckBoxDefaultEquipment"));
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
  public ModelEquipement getModel() {
    return model;
  }

  /**
   * @return
   */
  public JLabel getJLabelValDistanceRun() {
    return jLabelValDistanceRun;
  }

  /**
   * @return the jLabelValFirstUsed
   */
  public JLabel getJLabelValFirstUsed() {
    return jLabelValFirstUsed;
  }

  /**
   * @return the jLabelValLastUsed
   */
  public JLabel getJLabelValLastUsed() {
    return jLabelValLastUsed;
  }

  /**
   * Ajout des listeners de l'&eacute;quipement.
   */
  public void addEvents(final DataEquipement data) {
    try {
      removeEvents();

      docLstEquimentWeight = new GenericModelDocListener(jTextFieldWeight,
                                                         data,
                                                         "setWeight",
                                                         Float.TYPE);
      jTextFieldWeight.getDocument().addDocumentListener(docLstEquimentWeight);

      docLstEquimentDistanceMax = new GenericModelDocListener(jTextFieldDistanceMax,
                                                              data,
                                                              "setDistanceMax",
                                                              Float.TYPE);
      jTextFieldDistanceMax.getDocument()
          .addDocumentListener(docLstEquimentDistanceMax);

      checkLstEquimentWarning = new GenericModelCheckBoxListener(jCheckBoxWarning,
                                                                 data,
                                                                 "setAlert");
      jCheckBoxWarning.addActionListener(checkLstEquimentWarning);

      photoListener = new PhotoListener() {
        public void photoChanged(PhotoEvent event) {
          data.setPath(event.getPath());
        }
      };
      jButtonPhoto.addPhotoListener(photoListener);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
  }

  /**
   * Suppression des listeners de l'&eacute;quipement.
   */
  public void removeEvents() {
    if (docLstEquimentWeight == null) {
      return;
    }

    jTextFieldWeight.getDocument().removeDocumentListener(docLstEquimentWeight);
    jTextFieldDistanceMax.getDocument()
        .removeDocumentListener(docLstEquimentDistanceMax);
    jCheckBoxWarning.removeActionListener(checkLstEquimentWarning);
    jButtonPhoto.removePhotoListener(photoListener);

    docLstEquimentWeight = null;
    docLstEquimentDistanceMax = null;
    checkLstEquimentWarning = null;
    photoListener = null;
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setBorder(BorderFactory
        .createTitledBorder(null,
                            " ",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            GuiFont.FONT_BOLD,
                            null));

    GridBagLayout gridbagLayout = new GridBagLayout();
    this.setLayout(gridbagLayout);

    Insets insets1 = new Insets(0, 0, 5, 5);

    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(0, 10, 0, 50);
    g.anchor = GridBagConstraints.CENTER;
    g.gridheight = 5;
    g.weighty = 1.0;
    gridbagLayout.setConstraints(getJButtonPhoto(), g);
    this.add(getJButtonPhoto());

    // Poids
    jLabelLibWeight = new JLabel("Poids :");
    jLabelLibWeight.setFont(GuiFont.FONT_PLAIN);
    jLabelLibWeight.setHorizontalAlignment(SwingConstants.RIGHT);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.EAST;
    gridbagLayout.setConstraints(jLabelLibWeight, g);
    this.add(jLabelLibWeight);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(getJTextFieldWeight(), g);
    jLabelLibWeight.setLabelFor(getJTextFieldWeight());
    this.add(getJTextFieldWeight());
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.gridwidth = GridBagConstraints.REMAINDER;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(getJComboBoxWeightUnits(), g);
    this.add(getJComboBoxWeightUnits());

    // Distances
    jLabelLibDistanceMax = new JLabel("Distance max. :");
    jLabelLibDistanceMax.setFont(GuiFont.FONT_PLAIN);
    jLabelLibDistanceMax.setHorizontalAlignment(SwingConstants.RIGHT);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.EAST;
    gridbagLayout.setConstraints(jLabelLibDistanceMax, g);
    this.add(jLabelLibDistanceMax);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(getJTextFieldDistanceMax(), g);
    jLabelLibDistanceMax.setLabelFor(getJTextFieldDistanceMax());
    this.add(getJTextFieldDistanceMax());
    jLabelLibDistanceRun = new JLabel("Distance parcouru :");
    jLabelLibDistanceRun.setFont(GuiFont.FONT_PLAIN);
    jLabelLibDistanceRun.setHorizontalAlignment(SwingConstants.RIGHT);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(jLabelLibDistanceRun, g);
    this.add(jLabelLibDistanceRun);
    jLabelValDistanceRun = new JLabel();
    jLabelValDistanceRun.setFont(GuiFont.FONT_PLAIN);
    jLabelValDistanceRun.setHorizontalAlignment(SwingConstants.LEFT);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(jLabelValDistanceRun, g);
    jLabelValDistanceRun.setLabelFor(jLabelValDistanceRun);
    this.add(jLabelValDistanceRun);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    gridbagLayout.setConstraints(getJComboBoxDistanceUnits(), g);
    this.add(getJComboBoxDistanceUnits());

    // Premiere utilisation
    jLabelLibFirstUsed = new JLabel("Premiere utilisation :");
    jLabelLibFirstUsed.setFont(GuiFont.FONT_PLAIN);
    jLabelLibFirstUsed.setHorizontalAlignment(SwingConstants.RIGHT);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.EAST;
    gridbagLayout.setConstraints(jLabelLibFirstUsed, g);
    this.add(jLabelLibFirstUsed);
    jLabelValFirstUsed = new JLabel();
    jLabelValFirstUsed.setFont(GuiFont.FONT_PLAIN);
    jLabelValFirstUsed.setHorizontalAlignment(SwingConstants.LEFT);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(jLabelValFirstUsed, g);
    this.add(jLabelValFirstUsed);
    jLabelLibFirstUsed.setLabelFor(jLabelValFirstUsed);
    getJCheckBoxDefaultEquipment().setHorizontalAlignment(SwingConstants.LEFT);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    gridbagLayout.setConstraints(getJCheckBoxDefaultEquipment(), g);
    this.add(getJCheckBoxDefaultEquipment());

    // Derniere utilisation
    jLabelLibLastUsed = new JLabel("Derniere utilisation :");
    jLabelLibLastUsed.setFont(GuiFont.FONT_PLAIN);
    jLabelLibLastUsed.setHorizontalAlignment(SwingConstants.RIGHT);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.fill = GridBagConstraints.HORIZONTAL;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(jLabelLibLastUsed, g);
    this.add(jLabelLibLastUsed);
    jLabelValLastUsed = new JLabel();
    jLabelValLastUsed.setFont(GuiFont.FONT_PLAIN);
    jLabelValLastUsed.setHorizontalAlignment(SwingConstants.LEFT);
    g = new GridBagConstraints();
    g.weightx = 0.0;
    g.insets = insets1;
    g.anchor = GridBagConstraints.WEST;
    gridbagLayout.setConstraints(jLabelValLastUsed, g);
    this.add(jLabelValLastUsed);
    jLabelLibFirstUsed.setLabelFor(jLabelValLastUsed);
    getJCheckBoxWarning().setHorizontalAlignment(SwingConstants.LEFT);
    g = new GridBagConstraints();
    g.insets = insets1;
    g.weightx = 0.0;
    g.anchor = GridBagConstraints.WEST;
    g.gridwidth = GridBagConstraints.REMAINDER;
    gridbagLayout.setConstraints(getJCheckBoxWarning(), g);
    this.add(getJCheckBoxWarning());

    // Evenement
    jComboBoxDistanceUnits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        model.setUnitDistance(JPanelUserEquipement.this, (String) cb
            .getSelectedItem());
      }
    });

    jCheckBoxDefaultEquipment.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        owner.setDefaultEquipement(model.getData(), jCheckBoxDefaultEquipment
            .isSelected());
      }
    });

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  /**
   * This method initializes jTextField.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldWeight() {
    if (jTextFieldWeight == null) {
      jTextFieldWeight = new JFormattedTextField();
      jTextFieldWeight.setValue(0);
      Dimension dim = new Dimension(80, 23);
      jTextFieldWeight.setPreferredSize(dim);
      jTextFieldWeight.setMinimumSize(dim);
      jTextFieldWeight
          .setFormatterFactory(TextFormatterFactory.createNumber(3, 3));
      jTextFieldWeight.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldWeight;
  }

  /**
   * This method initializes jTextField.
   * 
   * @return javax.swing.JTextField
   */
  public JFormattedTextField getJTextFieldDistanceMax() {
    if (jTextFieldDistanceMax == null) {
      jTextFieldDistanceMax = new JFormattedTextField();
      Dimension dim = new Dimension(80, 23);
      jTextFieldDistanceMax.setPreferredSize(dim);
      jTextFieldDistanceMax.setMinimumSize(dim);
      jTextFieldDistanceMax.setValue(0);
      jTextFieldDistanceMax.setFormatterFactory(TextFormatterFactory
          .createNumber(5, 0));
      jTextFieldDistanceMax.setFont(GuiFont.FONT_PLAIN);
    }
    return jTextFieldDistanceMax;
  }

  /**
   * This method initializes jComboBox.
   * 
   * @return javax.swing.JComboBox
   */
  public JComboBox getJComboBoxWeightUnits() {
    if (jComboBoxWeightUnits == null) {
      jComboBoxWeightUnits = new JComboBox();
      jComboBoxWeightUnits.setFont(GuiFont.FONT_PLAIN);
      jComboBoxWeightUnits
          .setModel(new DefaultComboBoxModel(WeightUnit.units()));
      jComboBoxWeightUnits.setSelectedItem(WeightUnit.getDefaultUnit());

    }
    return jComboBoxWeightUnits;
  }

  /**
   * This method initializes jButton.
   * 
   * @return javax.swing.JButton
   */
  public JButtonPhoto getJButtonPhoto() {
    if (jButtonPhoto == null) {
      jButtonPhoto = new JButtonPhoto("Photo");
      jButtonPhoto.setFont(GuiFont.FONT_PLAIN);
      Dimension dim = new Dimension(160, 110);
      jButtonPhoto.setPreferredSize(dim);
      jButtonPhoto.setMinimumSize(dim);
      jButtonPhoto.setVerticalAlignment(SwingConstants.CENTER);
      jButtonPhoto.setHorizontalAlignment(SwingConstants.CENTER);
      jButtonPhoto.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonPhoto;
  }

  /**
   * This method initializes jCheckBoxWarning.
   * 
   * @return javax.swing.JCheckBox
   */
  public JCheckBox getJCheckBoxWarning() {
    if (jCheckBoxWarning == null) {
      jCheckBoxWarning = new JCheckBox();
      jCheckBoxWarning.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxWarning.setText("Alerte kilometrage depasse");
    }
    return jCheckBoxWarning;
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
   * This method initializes jCheckBox.
   * 
   * @return javax.swing.JCheckBox
   */
  public JCheckBox getJCheckBoxDefaultEquipment() {
    if (jCheckBoxDefaultEquipment == null) {
      jCheckBoxDefaultEquipment = new JCheckBox();
      jCheckBoxDefaultEquipment.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxDefaultEquipment.setText("Equipement principal");
    }
    return jCheckBoxDefaultEquipment;
  }

} // @jve:decl-index=0:visual-constraint="10,10"
