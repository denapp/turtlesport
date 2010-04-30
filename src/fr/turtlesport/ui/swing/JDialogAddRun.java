package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
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
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTextAreaLength;
import fr.turtlesport.ui.swing.component.JTextFieldTime;
import fr.turtlesport.ui.swing.component.JXDatePickerLocale;
import fr.turtlesport.ui.swing.model.GenericModelDocListener;
import fr.turtlesport.ui.swing.model.ModelAddRun;
import fr.turtlesport.unit.DistanceUnit;
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

  private JComboBox               jComboBoxActivity;

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

  /** Model */
  private ActivityComboBoxModel   modelActivities;

  private EquipementComboBoxModel modelEquipements;

  private ModelAddRun             model = new ModelAddRun();

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
    dlg.pack();
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

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(500, 400);
    this.setContentPane(getJContentPane());

    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());
    this.setTitle(rb.getString("title"));
    jLabelLibDate.setText(rb.getString("jLabelLibDate"));
    jLabelLibDistTot.setText(rb.getString("jLabelLibDistTot"));
    jLabelLibTimeTot.setText(rb.getString("jLabelLibTimeTot"));
    jLabelLibActivity.setText(rb.getString("jLabelLibActivity"));
    jLabelLibEquipment.setText(rb.getString("jLabelLibEquipment"));
    jLabelLibNotes.setText(rb.getString("jLabelLibNotes"));
    jButtonCancel.setText(rb.getString("jButtonCancel"));
    jButtonSave.setText(rb.getString("jButtonSave"));

    // evenements
    jButtonCancel.addActionListener(new CancelActionListener());
    getRootPane().setDefaultButton(jButtonSave);
    jButtonSave.addActionListener(new SaveActionListener());
    jComboBoxDistanceUnits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        model
            .setUnitDistance(JDialogAddRun.this, (String) cb.getSelectedItem());
      }
    });
    try {
      GenericModelDocListener doc = new GenericModelDocListener(jTextFieldDistTot,
                                                                model.getData(),
                                                                "setDistanceTot",
                                                                Double.TYPE);
      jTextFieldDistTot.getDocument().addDocumentListener(doc);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
    
    setDefaultCloseOperation(JDialogImport.DISPOSE_ON_CLOSE);

    // mis a jour des valeurs
    jTextFieldDistTot.setValue(10);
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

      GridBagConstraints g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDate = new JLabel();
      jLabelLibDate.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDate.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibDate, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDate.setLabelFor(getJDatePicker());
      jPanelSummary.add(getJDatePicker(), g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibDate.setLabelFor(getJDatePicker());
      jPanelSummary.add(getJTextFieldTime(), g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDistTot = new JLabel();
      jLabelLibDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibDistTot, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;

      jLabelLibDistTot.setLabelFor(getJTextFieldDistTot());
      jPanelSummary.add(getJTextFieldDistTot(), g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      g.anchor = GridBagConstraints.WEST;
      g.gridwidth = GridBagConstraints.REMAINDER;
      jPanelSummary.add(getJComboBoxDistanceUnits(), g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeTot = new JLabel();
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibTimeTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibTimeTot.setLabelFor(getJTextFieldTimeTot());
      jPanelSummary.add(getJTextFieldTimeTot(), g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibActivity = new JLabel();
      jLabelLibActivity.setFont(GuiFont.FONT_PLAIN);
      jLabelLibActivity.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibActivity, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jComboBoxActivity = new JComboBox(modelActivities);
      modelActivities.setDefaultSelectedItem();
      jComboBoxActivity.setFont(GuiFont.FONT_PLAIN);
      jLabelLibActivity.setLabelFor(jComboBoxActivity);
      jPanelSummary.add(jComboBoxActivity, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibEquipment = new JLabel();
      jLabelLibEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelSummary.add(jLabelLibEquipment, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;

      jComboBoxEquipment = new JComboBox(modelEquipements);
      modelEquipements.setDefaultSelectedItem();

      jComboBoxEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setLabelFor(jComboBoxEquipment);
      jPanelSummary.add(jComboBoxEquipment, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
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
    }
    return jTextFieldDistTot;
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
  public class ActivityComboBoxModel extends DefaultComboBoxModel {
    private AbstractDataActivity defaultActivity;

    public ActivityComboBoxModel() throws SQLException {
      super();

      List<AbstractDataActivity> list = UserActivityTableManager.getInstance()
          .retreive();
      for (AbstractDataActivity d : list) {
        if (d.isDefaultActivity()) {
          defaultActivity = d;
        }
        addElement(d);
      }
    }

    public void setDefaultSelectedItem() {
      if (defaultActivity != null) {
        setSelectedItem(defaultActivity);
      }
    }

    public void setSelectedActivity(int sportType) {
      for (int i = 1; i < getSize(); i++) {
        AbstractDataActivity d = (AbstractDataActivity) getElementAt(i);
        if (d.getSportType() == sportType) {
          setSelectedItem(d);
          return;
        }
      }
      setSelectedItem("");
    }

    public int getSportType() {
      Object obj = getSelectedItem();
      if (obj instanceof String) {
        return DataActivityOther.SPORT_TYPE;
      }
      return ((AbstractDataActivity) obj).getSportType();
    }
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
//          jButtonCancel.setEnabled(true);
//          JDialogAddRun.this.setCursor(Cursor.getDefaultCursor());
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
          .getManager().getCurrentLang(), getClass());
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
}
