package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.geo.FactoryGeoConvertRun;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.googleearth.GoogleEarthException;
import fr.turtlesport.googleearth.GoogleEarthFactory;
import fr.turtlesport.googleearth.IGoogleEarth;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.ui.swing.component.JButtonCustom;
import fr.turtlesport.ui.swing.component.JFileSaver;
import fr.turtlesport.ui.swing.component.JMenuItemTurtle;
import fr.turtlesport.ui.swing.component.JPanelGraph;
import fr.turtlesport.ui.swing.component.JPanelMap;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTextAreaLength;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.model.ModelRun;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.GeoUtil;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelRun extends JPanel implements LanguageListener, UnitListener {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelRun.class);
  }

  private JLabel                  jLabelLibDistTot;

  private JLabel                  jLabelLibAllure;

  private JLabel                  jLabelLibSpeedMoy;

  private JLabel                  jLabelLibCalories;

  private JLabel                  jLabelLibTimeTot;

  private JLabel                  jLabelValDistTot;

  private JLabel                  jLabelValTimeTot;

  private JLabel                  jLabelValAllure;

  private JLabel                  jLabelValSpeedMoy;

  private JLabel                  jLabelValCalories;

  private JLabel                  jLabelLibHeart;

  private JLabel                  jLabelValHeartAverage;

  private JPanel                  jPanelRunSummary;

  private JScrollPane             jPanelRunLap;

  private JXTable                 jTableLap;

  private TableModelLap           tableModelLap;

  private JScrollPane             jPanelGraph;

  private JPanelGraph             jDiagram;

  private JLabel                  jLabelLibAlt;

  private JLabel                  jLabelValAlt;

  private JLabel                  jLabelLibActivity;

  private JComboBox               jComboBoxActivity;

  private JLabel                  jLabelLibEquipment;

  private JComboBox               jComboBoxEquipment;

  private JLabel                  jLabelLibNotes;

  private JTextAreaLength         jTextFieldNotes;

  private TitledBorder            borderPanelRunSummary;

  private TitledBorder            borderPanelRunLap;

  private JPanelNav               jPanelNav;

  private JPopupMenu              jPopupMenu;

  private JMenuItemTurtle         jMenuItemRunDetail;

  private JMenuItemTurtle         jMenuItemRunMap;

  private JMenuItemTurtle         jMenuItemRunGoogleEarth;

  private JMenuItemTurtle         jMenuItemRunEmail;

  private JMenuItemTurtle         jMenuItemRunDelete;

  private JMenuItemTurtle         jMenuItemRunSave;

  private JMenuItemTurtle         jMenuItemRunExportGoogleEarth;

  private JMenu                   jMenuRunExport;

  private JMenuItemTurtle         jMenuItemRunExportGpx;

  private JMenuItemTurtle         jMenuItemRunExportTcx;

  private JMenuItemTurtle         jMenuItemRunExportHst;

  private JPanel                  jPanelCenter;

  private JPanel                  jPanelEast;

  private JPanelMap               jPanelMap;

  private JPanel                  jPanelEastCenter;

  private JScrollPane             jScrollPaneTextArea;

  private JButtonCustom           jButtonSave;

  private JButtonCustom           jButtonDelete;

  private JButtonCustom           jButtonGoogleEarth;

  private JButtonCustom           jButtonDetails;

  private JButtonCustom           jButtonEmail;

  private JPanel                  jPanelButtons;

  // model
  private ModelRun                model;

  private ActivityComboBoxModel   modelActivities;

  private EquipementComboBoxModel modelEquipements;

  private static ResourceBundle   rb;

  /**
   * This is the default constructor.
   */
  public JPanelRun() {
    super();
    initialize();
    model = new ModelRun();
  }

  /**
   * @return the rb
   */
  public static ResourceBundle getResourceBundle() {
    return rb;
  }

  /**
   * Si les dates changesnt, les boutons suivant et pr&eacute;c&eacute;dent
   * peuvent changer.
   * 
   * @throws SQLException
   */
  public void fireHistoric() throws SQLException {
    model.updateButtons(this);
  }

  public ModelRun getModel() {
    return model;
  }

  public TableModelLap getTableModelLap() {
    return tableModelLap;
  }

  public JLabel getJLabelValAllure() {
    return jLabelValAllure;
  }

  public JLabel getJLabelValCalories() {
    return jLabelValCalories;
  }

  public JLabel getJLabelValDistTot() {
    return jLabelValDistTot;
  }

  public JLabel getJLabelValHeart() {
    return jLabelValHeartAverage;
  }

  public JLabel getJLabelValSpeedMoy() {
    return jLabelValSpeedMoy;
  }

  public JLabel getJLabelValTimeTot() {
    return jLabelValTimeTot;
  }

  public JLabel getJLabelValAlt() {
    return jLabelValAlt;
  }

  public ActivityComboBoxModel getModelActivities() {
    return modelActivities;
  }

  public EquipementComboBoxModel getModelEquipements() {
    return modelEquipements;
  }

  public JTextArea getJTextFieldNotes() {
    return jTextFieldNotes;
  }

  /**
   * Rend les menus de courses activable ou non.
   * 
   * @param b
   *          <code>true</code> pour activer les menus de course.
   */
  public void setEnableMenuRun(boolean b) {
    getJMenuItemRunMap().setEnabled(b);
    getJMenuItemRunDetail().setEnabled(b);
    getJMenuItemRunGoogleEarth().setEnabled(b);
    if (jMenuItemRunEmail != null) {
      jMenuItemRunEmail.setEnabled(b);
    }
    getJMenuRunExport().setEnabled(b);
    getJMenuItemRunExportGpx().setEnabled(b);
    getJMenuItemRunExportGoogleEarth().setEnabled(b);
    getJMenuItemRunExportTcx().setEnabled(b);
    getJMenuItemRunExportHst().setEnabled(b);
    getJMenuItemRunSave().setEnabled(b);
    getJMenuItemRunDelete().setEnabled(b);
    getJButtonDelete().setEnabled(b);
    getJButtonSave().setEnabled(b);
    if (jButtonEmail != null) {
      jButtonEmail.setEnabled(b);
    }
    getJButtonGoogleEarth().setEnabled(b);
    getJButtonDetails().setEnabled(b);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent event) {
    // run summary
    model.performedUnit(this, event);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
    UnitManager.getManager().removeUnitListener(jDiagram.getJDiagram());
    UnitManager.getManager().removeUnitListener(jPanelMap);
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
  public void completedRemoveLanguageListener() {
    LanguageManager.getManager().removeLanguageListener(jDiagram.getJDiagram());
    LanguageManager.getManager().removeLanguageListener(jPanelMap);
  }

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, getClass());
    jButtonSave.setToolTipText(rb.getString("jButtonSaveToolTipText"));
    jButtonDelete.setToolTipText(rb.getString("jButtonDeleteToolTipText"));
    jButtonGoogleEarth.setToolTipText(rb
        .getString("jButtonGoogleEarthToolTipText"));
    jButtonDetails.setToolTipText(rb.getString("jButtonDetailsToolTipText"));
    if (jButtonEmail != null) {
      jButtonEmail.setToolTipText(rb.getString("jButtonEmailToolTipText"));
    }
    jLabelLibDistTot.setText(rb.getString("jLabelLibDistTot"));
    jLabelLibTimeTot.setText(rb.getString("jLabelLibTimeTot"));
    jLabelLibAllure.setText(rb.getString("jLabelLibAllure"));
    jLabelLibSpeedMoy.setText(rb.getString("jLabelLibSpeedMoy"));
    jLabelLibCalories.setText(rb.getString("jLabelLibCalories"));
    borderPanelRunSummary.setTitle(rb.getString("borderPanelRunSummary"));
    getJButtonNext().setToolTipText(rb.getString("jButtonNextToolTipText"));
    getJButtonPrev().setToolTipText(rb.getString("jButtonPrevToolTipText"));
    jLabelLibHeart.setText(rb.getString("jLabelLibHeart"));
    borderPanelRunLap.setTitle(rb.getString("borderPanelRunLap"));
    jLabelLibAlt.setText(rb.getString("jLabelLibAlt"));
    jLabelLibEquipment.setText(rb.getString("jLabelLibEquipment"));
    jLabelLibNotes.setText(rb.getString("jLabelLibNotes"));
    jLabelLibActivity.setText(rb.getString("jLabelLibActivity"));
    if (model != null && model.getDataRun() != null) {
      // jLabelValActivity.setText(model.getDataRun().getLibelleSportType());
    }
    jMenuItemRunDetail.setText(rb.getString("jMenuItemRunDetail"));
    jMenuItemRunMap.setText(rb.getString("jMenuItemRunMap"));
    jMenuItemRunGoogleEarth.setText(rb.getString("jMenuItemRunGoogleEarth"));
    if (jMenuItemRunEmail != null) {
      jMenuItemRunEmail.setText(rb.getString("jMenuItemRunEmail"));
    }
    jMenuRunExport.setText(rb.getString("jMenuRunExport"));
    jMenuItemRunExportGpx.setText(rb.getString("jMenuItemRunExportGpx"));
    jMenuItemRunExportGoogleEarth.setText(rb
        .getString("jMenuItemRunExportGoogleEarth"));
    jMenuItemRunExportTcx.setText(rb.getString("jMenuItemRunExportTcx"));
    jMenuItemRunExportHst.setText(rb.getString("jMenuItemRunExportHst"));
    jMenuItemRunSave.setText(rb.getString("jMenuItemRunSave"));
    jMenuItemRunDelete.setText(rb.getString("jMenuItemRunDelete"));

    // mis a jour nom de colonnes
    tableModelLap.performedLanguage();
    repaint();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(650, 650);
    this.setLayout(new BorderLayout(10, 10));
    this.setOpaque(true);
    this.add(getJPopupMenu());
    this.add(getJPanelCenter(), BorderLayout.CENTER);
    this.add(getJPanelEast(), BorderLayout.EAST);

    ActionListener action;
    // evenement
    PopupListener popupListener = new PopupListener(getJPopupMenu());
    this.addMouseListener(popupListener);

    getJButtonNext().addActionListener(new NextActionListener());
    getJButtonPrev().addActionListener(new PrevActionListener());

    action = new ExportActionListener(FactoryGeoConvertRun.GPX);
    getJMenuItemRunExportGpx().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunExportGpx().addActionListener(action);

    action = new ExportActionListener(FactoryGeoConvertRun.KML);
    getJMenuItemRunExportGoogleEarth().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunExportGoogleEarth()
        .addActionListener(action);

    action = new ExportActionListener(FactoryGeoConvertRun.TCX);
    getJMenuItemRunExportTcx().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunExportTcx().addActionListener(action);

    action = new ExportActionListener(FactoryGeoConvertRun.HST);
    getJMenuItemRunExportHst().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunExportHst().addActionListener(action);

    action = new GoogleEarthShowActionListener();
    getJMenuItemRunGoogleEarth().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunGoogleEarth().addActionListener(action);
    getJButtonGoogleEarth().addActionListener(action);

    if (jMenuItemRunEmail != null) {
      action = new EmailActionListener();
      jMenuItemRunEmail.addActionListener(action);
      jButtonEmail.addActionListener(action);
      MainGui.getWindow().getJMenuItemRunEmail().addActionListener(action);
    }

    action = new SaveActionListener();
    getJMenuItemRunSave().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunSave().addActionListener(action);
    getJButtonSave().addActionListener(action);

    action = new DeleteActionListener();
    getJMenuItemRunDelete().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunDelete().addActionListener(action);
    getJButtonDelete().addActionListener(action);

    action = new DetailActionListener();
    getJMenuItemRunDetail().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunDetail().addActionListener(action);
    getJButtonDetails().addActionListener(action);

    action = new MapMercatorActionListener();
    getJMenuItemRunMap().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunMap().addActionListener(action);

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  /**
   * This method initializes jPanelRunSummary.
   * 
   * @return javax.swing.JPanel
   */
  private JPopupMenu getJPopupMenu() {
    if (jPopupMenu == null) {
      jPopupMenu = new JPopupMenu();
      jPopupMenu.add(getJMenuItemRunDetail());
      jPopupMenu.add(getJMenuItemRunMap());
      jPopupMenu.add(getJMenuItemRunGoogleEarth());
      if (Mail.isSupported()) {
        jMenuItemRunEmail = new JMenuItemTurtle();
        jMenuItemRunEmail.setFont(GuiFont.FONT_PLAIN);
        jMenuItemRunEmail.setAccelerator(MainGui.getWindow()
            .getMenuProperties(), "jMenuItemRunEmail");
        jMenuItemRunEmail.setEnabled(false);
        jPopupMenu.add(jMenuItemRunEmail);
      }
      jPopupMenu.add(getJMenuRunExport());
      jPopupMenu.add(getJMenuItemRunSave());
      jPopupMenu.add(getJMenuItemRunDelete());
    }
    return jPopupMenu;
  }

  /**
   * This method initializes jMenuItemRunDetail.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunDetail() {
    if (jMenuItemRunDetail == null) {
      jMenuItemRunDetail = new JMenuItemTurtle();
      jMenuItemRunDetail.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunDetail
          .setAccelerator(MainGui.getWindow().getMenuProperties(),
                          "jMenuItemRunDetail");
      jMenuItemRunDetail.setEnabled(false);
    }
    return jMenuItemRunDetail;
  }

  /**
   * This method initializes jMenuItemRunDetail.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunMap() {
    if (jMenuItemRunMap == null) {
      jMenuItemRunMap = new JMenuItemTurtle();
      jMenuItemRunMap.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunMap.setAccelerator(MainGui.getWindow().getMenuProperties(),
                                     "jMenuItemRunMap");
      jMenuItemRunMap.setEnabled(false);
    }
    return jMenuItemRunMap;
  }

  /**
   * This method initializes jMenuItemRunGoogleEarth.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunGoogleEarth() {
    if (jMenuItemRunGoogleEarth == null) {
      jMenuItemRunGoogleEarth = new JMenuItemTurtle();
      jMenuItemRunGoogleEarth.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunGoogleEarth.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunGoogleEarth");
      jMenuItemRunGoogleEarth.setEnabled(false);
    }
    return jMenuItemRunGoogleEarth;
  }

  /**
   * This method initializes jMenuItemRunEmail.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunEmail() {
    return jMenuItemRunEmail;
  }

  /**
   * This method initializes jMenuRunExport.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenu getJMenuRunExport() {
    if (jMenuRunExport == null) {
      jMenuRunExport = new JMenu();
      jMenuRunExport.setFont(GuiFont.FONT_PLAIN);
      jMenuRunExport.add(getJMenuItemRunExportGpx());
      jMenuRunExport.add(getJMenuItemRunExportGoogleEarth());
      jMenuRunExport.add(getJMenuItemRunExportTcx());
      jMenuRunExport.add(getJMenuItemRunExportHst());
      jMenuRunExport.setEnabled(false);
    }
    return jMenuRunExport;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportGpx() {
    if (jMenuItemRunExportGpx == null) {
      jMenuItemRunExportGpx = new JMenuItemTurtle();
      jMenuItemRunExportGpx.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportGpx.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunExportGpx");
      jMenuItemRunExportGpx.setEnabled(false);
    }
    return jMenuItemRunExportGpx;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportGoogleEarth() {
    if (jMenuItemRunExportGoogleEarth == null) {
      jMenuItemRunExportGoogleEarth = new JMenuItemTurtle();
      jMenuItemRunExportGoogleEarth.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportGoogleEarth.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunExportGoogleEarth");
      jMenuItemRunExportGoogleEarth.setEnabled(false);
    }
    return jMenuItemRunExportGoogleEarth;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportTcx() {
    if (jMenuItemRunExportTcx == null) {
      jMenuItemRunExportTcx = new JMenuItemTurtle();
      jMenuItemRunExportTcx.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportTcx.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunExportTcx");
      jMenuItemRunExportTcx.setEnabled(false);
    }
    return jMenuItemRunExportTcx;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportHst() {
    if (jMenuItemRunExportHst == null) {
      jMenuItemRunExportHst = new JMenuItemTurtle();
      jMenuItemRunExportHst.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportHst.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunExportHst");
      jMenuItemRunExportHst.setEnabled(false);
    }
    return jMenuItemRunExportHst;
  }

  /**
   * This method initializes jMenuItemRunGoogleEarth.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunDelete() {
    if (jMenuItemRunDelete == null) {
      jMenuItemRunDelete = new JMenuItemTurtle();
      jMenuItemRunDelete.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunDelete
          .setAccelerator(MainGui.getWindow().getMenuProperties(),
                          "jMenuItemRunDelete");
      jMenuItemRunDelete.setEnabled(false);
    }
    return jMenuItemRunDelete;
  }

  /**
   * This method initializes jMenuItemRunSave.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunSave() {
    if (jMenuItemRunSave == null) {
      jMenuItemRunSave = new JMenuItemTurtle();
      jMenuItemRunSave.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunSave.setAccelerator(MainGui.getWindow().getMenuProperties(),
                                      "jMenuItemRunSave");
      jMenuItemRunSave.setEnabled(false);
    }
    return jMenuItemRunSave;
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
                              "Course",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelRunSummary.setBorder(borderPanelRunSummary);
      jPanelRunSummary.setPreferredSize(new Dimension(300, 400));

      Insets insets = new Insets(0, 0, 5, 10);
      GridBagConstraints g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDistTot = new JLabel("Distance totale :");
      jLabelLibDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibDistTot, g);
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelValDistTot = new JLabel();
      jLabelValDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setLabelFor(jLabelValDistTot);
      jPanelRunSummary.add(jLabelValDistTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.NORTHEAST;
      g.fill = GridBagConstraints.HORIZONTAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = new Insets(0, 0, 0, 5);
      jPanelRunSummary.add(getJPanelNav(), g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeTot = new JLabel("Temps total :");
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibTimeTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimeTot = new JLabel();
      jLabelValTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setLabelFor(jLabelValTimeTot);
      jPanelRunSummary.add(jLabelValTimeTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAllure = new JLabel("Allure moyenne :");
      jLabelLibAllure.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllure.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAllure, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAllure = new JLabel();
      jLabelValAllure.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllure.setLabelFor(jLabelValAllure);
      jPanelRunSummary.add(jLabelValAllure, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibSpeedMoy = new JLabel("Vitesse moyenne :");
      jLabelLibSpeedMoy.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoy.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibSpeedMoy, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValSpeedMoy = new JLabel();
      jLabelValSpeedMoy.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoy.setLabelFor(jLabelValSpeedMoy);
      jPanelRunSummary.add(jLabelValSpeedMoy, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibCalories = new JLabel("Calories :");
      jLabelLibCalories.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCalories.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibCalories, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValCalories = new JLabel();
      jLabelValCalories.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCalories.setLabelFor(jLabelValCalories);
      jPanelRunSummary.add(jLabelValCalories, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHeart = new JLabel("Moyenne :");
      jLabelLibHeart.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibHeart.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeart.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibHeart, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHeartAverage = new JLabel();
      jLabelValHeartAverage.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeart.setLabelFor(jLabelValHeartAverage);
      jPanelRunSummary.add(jLabelValHeartAverage, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAlt = new JLabel();
      jLabelLibAlt.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAlt.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAlt, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAlt = new JLabel();
      jLabelValAlt.setFont(GuiFont.FONT_PLAIN);
      jLabelValAlt.setLabelFor(jLabelLibAlt);
      jPanelRunSummary.add(jLabelValAlt, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibActivity = new JLabel();
      jLabelLibActivity.setFont(GuiFont.FONT_PLAIN);
      jLabelLibActivity.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibActivity, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      modelActivities = new ActivityComboBoxModel();
      jComboBoxActivity = new JComboBox(modelActivities);
      jComboBoxActivity.setFont(GuiFont.FONT_PLAIN);
      jLabelLibActivity.setLabelFor(jComboBoxActivity);
      jPanelRunSummary.add(jComboBoxActivity, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibEquipment = new JLabel("Equipement :");
      jLabelLibEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibEquipment, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      modelEquipements = new EquipementComboBoxModel();
      jComboBoxEquipment = new JComboBox(modelEquipements);
      jComboBoxEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setLabelFor(jComboBoxEquipment);
      jPanelRunSummary.add(jComboBoxEquipment, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibNotes = new JLabel("Notes :");
      jLabelLibNotes.setFont(GuiFont.FONT_PLAIN);
      jLabelLibNotes.setHorizontalAlignment(SwingConstants.TRAILING);
      jLabelLibNotes.setVerticalAlignment(SwingConstants.TOP);
      jPanelRunSummary.add(jLabelLibNotes, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.gridheight = 3;
      g.anchor = GridBagConstraints.NORTHWEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibNotes.setLabelFor(getJScrollPaneTextArea());
      jPanelRunSummary.add(getJScrollPaneTextArea(), g);

      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.NORTHEAST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      jPanelRunSummary.add(getJPanelButtons(), g);
    }
    return jPanelRunSummary;
  }

  private JPanel getJPanelButtons() {
    if (jPanelButtons == null) {
      jPanelButtons = new JPanel();
      jPanelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelButtons.add(getJButtonDelete());
      jPanelButtons.add(getJButtonSave());
      jPanelButtons.add(getJButtonDetails());
      if (Mail.isSupported()) {
        jButtonEmail = new JButtonCustom(ImagesRepository
            .getImageIcon("email.png"));
        Dimension dim = new Dimension(20, 20);
        jButtonEmail.setPreferredSize(dim);
        jButtonEmail.setMaximumSize(dim);
        jButtonEmail.setEnabled(false);
        jButtonEmail.setOpaque(false);
        jPanelButtons.add(jButtonEmail);
      }
      jPanelButtons.add(getJButtonGoogleEarth());
    }
    return jPanelButtons;
  }

  public JButton getJButtonSave() {
    if (jButtonSave == null) {
      jButtonSave = new JButtonCustom(ImagesRepository
          .getImageIcon("media-floppy.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonSave.setPreferredSize(dim);
      jButtonSave.setMaximumSize(dim);
      jButtonSave.setEnabled(false);
      jButtonSave.setOpaque(false);

    }
    return jButtonSave;
  }

  public JButton getJButtonGoogleEarth() {
    if (jButtonGoogleEarth == null) {
      jButtonGoogleEarth = new JButtonCustom(ImagesRepository
          .getImageIcon("googleearth.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonGoogleEarth.setPreferredSize(dim);
      jButtonGoogleEarth.setMaximumSize(dim);
      jButtonGoogleEarth.setEnabled(false);
      jButtonGoogleEarth.setOpaque(false);

    }
    return jButtonGoogleEarth;
  }

  public JButton getJButtonEmail() {
    return jButtonEmail;
  }

  public JButton getJButtonDelete() {
    if (jButtonDelete == null) {
      jButtonDelete = new JButtonCustom(ImagesRepository
          .getImageIcon("delete.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonDelete.setPreferredSize(dim);
      jButtonDelete.setMaximumSize(dim);
      jButtonDelete.setEnabled(false);
      jButtonDelete.setOpaque(false);

    }
    return jButtonDelete;
  }

  public JButton getJButtonDetails() {
    if (jButtonDetails == null) {
      jButtonDetails = new JButtonCustom(ImagesRepository
          .getImageIcon("loupe.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonDetails.setPreferredSize(dim);
      jButtonDetails.setMaximumSize(dim);
      jButtonDetails.setEnabled(false);
      jButtonDetails.setOpaque(false);

    }
    return jButtonDetails;
  }

  /**
   * This method initializes jPanelNav
   * 
   * @return javax.swing.JPanel
   */
  private JPanelNav getJPanelNav() {
    if (jPanelNav == null) {
      jPanelNav = new JPanelNav();
    }
    return jPanelNav;
  }

  /**
   * This method initializes jButtonNext.
   * 
   * @return javax.swing.JButton
   */
  public JButton getJButtonNext() {
    return getJPanelNav().getJButtonNext();
  }

  /**
   * This method initializes jButtonPrev.
   * 
   * @return javax.swing.JButton
   */
  public JButton getJButtonPrev() {
    return getJPanelNav().getJButtonPrev();
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jPanelCenter = new JPanel();
      jPanelCenter.setOpaque(true);
      jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.Y_AXIS));
      jPanelCenter.add(getJPanelRunLap());
      jPanelCenter.add(Box.createRigidArea(new Dimension(0, 10)));
      jPanelCenter.add(getJPanelGraph());
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelEast() {
    if (jPanelEast == null) {
      jPanelEastCenter = new JPanel();
      jPanelEastCenter.setLayout(new BoxLayout(jPanelEastCenter,
                                               BoxLayout.Y_AXIS));
      jPanelEastCenter.add(getJPanelRunSummary());
      jPanelEastCenter.add(Box.createRigidArea(new Dimension(0, 10)));
      jPanelEastCenter.add(getJPanelMap());
      jPanelEast = new JPanel();
      jPanelEast.setLayout(new BorderLayout());
      jPanelEast.add(jPanelEastCenter, BorderLayout.CENTER);
    }
    return jPanelEast;
  }

  /**
   * This method initializes jPanelMap.
   * 
   * @return javax.swing.JPanel
   */
  public JPanelMap getJPanelMap() {
    if (jPanelMap == null) {
      jPanelMap = new JPanelMap(true);
      jPanelMap.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));

      Dimension dim = new Dimension(270, 270);
      jPanelMap.setPreferredSize(dim);
      jPanelMap.setMinimumSize(dim);
    }
    return jPanelMap;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  public JScrollPane getJPanelRunLap() {
    if (jPanelRunLap == null) {
      jPanelRunLap = new JScrollPane();
      jPanelRunLap.setOpaque(true);

      borderPanelRunLap = BorderFactory
          .createTitledBorder(null,
                              "Temps intermediaire",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelRunLap.setBorder(borderPanelRunLap);
      jPanelRunLap.setViewportView(getJTableLap());
      jPanelRunLap.setPreferredSize(new Dimension(600, 300));
    }
    return jPanelRunLap;
  }

  /**
   * This method initializes jTableLap
   * 
   * @return javax.swing.JTable
   */
  private JTable getJTableLap() {
    if (jTableLap == null) {
      jTableLap = new JXTable();
      jTableLap.setOpaque(true);
      tableModelLap = new TableModelLap();
      jTableLap.setModel(tableModelLap);

      jTableLap.setFont(GuiFont.FONT_PLAIN);
      jTableLap.setShowGrid(false);
      jTableLap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      // jTableLap.setSelectionForeground(Color.white);
      jTableLap.setFont(GuiFont.FONT_PLAIN);

      jTableLap.setRowSelectionAllowed(true);
      jTableLap.setColumnSelectionAllowed(false);
      jTableLap.setShowGrid(false);

      // colum size
      TableColumn column;
      for (int i = 0; i < tableModelLap.getColumnCount(); i++) {
        column = jTableLap.getColumnModel().getColumn(i);
        column.setPreferredWidth(tableModelLap.getPreferredWidth(i));
        column.setWidth(tableModelLap.getPreferredWidth(i));
        column.setMinWidth(tableModelLap.getPreferredWidth(i));
      }

      jTableLap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      jTableLap.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTableLap.getSelectionModel()
          .addListSelectionListener(new TableListSelectionListener());
      // jTableLap.packAll();
      jTableLap.setHorizontalScrollEnabled(true);
      jTableLap.setSortable(false);
    }
    return jTableLap;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  public JScrollPane getJPanelGraph() {
    if (jPanelGraph == null) {
      jPanelGraph = new JScrollPane();
      jPanelGraph.setViewportView(getJDiagram());
      jPanelGraph.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));
      jPanelGraph.setFont(GuiFont.FONT_PLAIN);
      jPanelGraph.setPreferredSize(new Dimension(600, 350));
    }
    return jPanelGraph;
  }

  /**
   * @return
   */
  public JPanelGraph getJDiagram() {
    if (jDiagram == null) {
      jDiagram = new JPanelGraph();
    }
    return jDiagram;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TableModelLap extends AbstractTableModel {

    private String                 unitDistance = DistanceUnit.unitKm();

    private String[]               columnNames  = { "Jour",
                                                    "Heure",
                                                    "Distance (km)",
                                                    "Temps",
                                                    "Allure Moy. (mn/km)",
                                                    "Vitesse Moy. (km/h)",
                                                    "moy.",
                                                    "max.",
                                                    "Calories",
                                                    "D�nivel� +",
                                                    "D�nivel� -" };

    private final int[]            columWidth   = { 40,
                                                    40,
                                                    50,
                                                    30,
                                                    70,
                                                    70,
                                                    25,
                                                    25,
                                                    30,
                                                    35,
                                                    35 };

    private DataRunLap[]           runLaps;

    private final SimpleDateFormat dfTime       = new SimpleDateFormat("kk:mm:ss");

    public double[] computeInterval(int index) {
      log.debug(">>computeInterval index=" + index);
      double[] inter = null;
      if (runLaps != null && (index >= 0) && (index < getRowCount())) {
        inter = new double[2];

        inter[0] = 0;
        for (int i = 0; i < index; i++) {
          inter[0] += runLaps[i].getTotalDist();
        }
        inter[1] = inter[0] + runLaps[index].getTotalDist();
      }
      return inter;
    }

    public IGeoPosition[] lapTrkBeginEnd(int index) {
      IGeoPosition[] res = null;
      if (runLaps != null && (index >= 0) && (index < getRowCount())) {
        DataRunTrk deb = null;
        DataRunTrk end = null;

        try {
          // Map
          deb = RunLapTableManager
              .getInstance()
              .lapTrkBegin(runLaps[index].getId(), runLaps[index].getLapIndex());
          if (deb != null) {
            end = RunLapTableManager
                .getInstance()
                .lapTrkEnd(runLaps[index].getId(), runLaps[index].getLapIndex());
          }

          if (deb != null && end != null) {
            res = new IGeoPosition[2];
            res[0] = GeoUtil.makeFromGarmin(deb.getLatitude(), deb
                .getLongitude());
            res[1] = GeoUtil.makeFromGarmin(end.getLatitude(), end
                .getLongitude());
          }
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }
      return res;
    }

    /**
     * Valorise le nom des colonnes.
     * 
     * @param columnNames
     *          les nouveaux noms.
     */
    public void performedLanguage() {
      for (int i = 0; i < columnNames.length; i++) {
        switch (i) {
          case 2:
            // Distance
            performedHeader(DistanceUnit.getDefaultUnit(), 2);
            break;

          case 4:
            // Allure
            performedHeader(PaceUnit.getDefaultUnit(), 4);
            break;

          case 5:
            // Vitesse moyenne
            performedHeader(SpeedUnit.getDefaultUnit(), 5);
            break;

          default:
            columnNames[i] = rb.getString("TableModel_header" + i);
            jTableLap.getColumnModel().getColumn(i)
                .setHeaderValue(columnNames[i]);
        }
      }

      // jTableLap.packAll();
      // jTableLap.getTableHeader().resizeAndRepaint();
    }

    public void performedUnit(UnitEvent e) {
      if (!e.isEventDistance()) {
        return;
      }
      performedUnit(e.getUnit());
      jTableLap.getTableHeader().resizeAndRepaint();
    }

    private void performedUnit(String unit) {
      // unite
      performedHeader(unit, 2);
      performedHeader("mn/" + unit, 4);
      performedHeader(unit + "/h", 5);

      // mis a jour des valeurs
      if (!unit.equals(unitDistance) && runLaps != null) {
        if (runLaps != null) {
          double value;
          for (int i = 0; i < runLaps.length; i++) {
            value = DistanceUnit.convert(unitDistance, unit, runLaps[i]
                .getTotalDist());
            runLaps[i].setTotalDist((float) value);
            fireTableCellUpdated(i, 2);
            fireTableCellUpdated(i, 4);
            fireTableCellUpdated(i, 5);
          }
        }
        unitDistance = unit;
      }
    }

    public void performedHeader(String unit, int index) {
      columnNames[index] = MessageFormat.format(rb
          .getString("TableModel_header" + index), unit);
      jTableLap.getColumnModel().getColumn(index)
          .setHeaderValue(columnNames[index]);
    }

    /**
     * @param column
     * @return
     */
    public int getPreferredWidth(int column) {
      return columWidth[column] * 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
      return columnNames[column];
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
      return columnNames.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
      return (runLaps == null) ? 0 : runLaps.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      // "Date"
      // "Heure",
      // "Distance",
      // "Temps",
      // "Allure Moy.",
      // "Vitesse Moy.",
      // "Frequence cardiaque moy.",
      // "Frequence cardiaque max.",
      // "Calories",
      // "Denivele +",
      // "Denivele -" };

      switch (columnIndex) {
        case 0: // Date
          return LanguageManager.getManager().getCurrentLang()
              .getDateFormatter().format(runLaps[rowIndex].getStartTime());

        case 1: // Heure
          return dfTime.format(runLaps[rowIndex].getStartTime());

        case 2: // Distance
          return DistanceUnit
              .formatMetersInKm(runLaps[rowIndex].getTotalDist());

        case 3: // Temps
          return TimeUnit.formatHundredSecondeTime(runLaps[rowIndex]
              .getTotalTime());

        case 4: // Allure Moy.
          return PaceUnit.computeAllure(runLaps[rowIndex].getTotalDist(),
                                        runLaps[rowIndex].getTotalTime());

        case 5: // Vitesse Moy.
          return SpeedPaceUnit.computeFormatSpeed(runLaps[rowIndex]
              .getTotalDist(), runLaps[rowIndex].getTotalTime());

        case 6: // Frequence cardiaque moy.
          return runLaps[rowIndex].getAvgHeartRate();

        case 7: // Frequence cardiaque max.
          return runLaps[rowIndex].getMaxHeartRate();

        case 8: // Calories
          return runLaps[rowIndex].getCalories();

        case 9: // Denivele +
          try {
            return Integer.toString(runLaps[rowIndex].computeDenivelePos());
          }
          catch (SQLException e) {
            log.error("", e);
          }
          return "";

        case 10: // Denivele -
          try {
            return Integer.toString(runLaps[rowIndex].computeDeniveleNeg());
          }
          catch (SQLException e) {
            log.error("", e);
          }
          return "";

        default:
          return "";
      }
    }

    /**
     * Mis a jour des donnees de la table.
     * 
     * @param runLaps
     */
    public void updateData(DataRunLap[] runLaps) {
      log.info(">>updateData");

      this.runLaps = runLaps;
      unitDistance = DistanceUnit.unitKm();
      if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
        performedUnit(DistanceUnit.getDefaultUnit());
      }
      fireTableDataChanged();
      // jTableLap.packAll();

      log.info("<<updateData");
    }

    /**
     * Efface les donnees de la table.
     * 
     * @param runLaps
     */
    public void clear() {
      updateData(null);
    }

  }

  private class TableListSelectionListener implements ListSelectionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
     * .ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
      if (jTableLap.getRowCount() > 1) {
        // Diagram
        double[] inter = tableModelLap.computeInterval(jTableLap
            .getSelectedRow());
        if (inter != null) {
          jDiagram.getJDiagram().getModel().updateInt(inter[0], inter[1]);
        }
        // Map
        IGeoPosition[] pos = tableModelLap.lapTrkBeginEnd(jTableLap
            .getSelectedRow());
        if (pos != null) {
          jPanelMap.getModelMap().updateInt(pos[0], pos[1]);
        }
        else {
          jPanelMap.getModelMap().updateInt(null, null);
        }
      }
    }

  }

  private class NextActionListener implements ActionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            JPanelRun.this.model.updateViewNext(JPanelRun.this);
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorDatabase"));
          }
          MainGui.getWindow().afterRunnableSwing();
        }
      });

    }
  }

  private class PrevActionListener implements ActionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            JPanelRun.this.model.updateViewPrev(JPanelRun.this);
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorDatabase"));
          }
          MainGui.getWindow().afterRunnableSwing();
        }
      });

    }
  }

  private class ExportActionListener implements ActionListener {

    private String ext;

    public ExportActionListener(String ext) {
      this.ext = ext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
      String name = LanguageManager.getManager().getCurrentLang()
          .getDateTimeFormatterWithoutSep()
          .format(model.getDataRun().getTime());

      final IGeoConvertRun cv = FactoryGeoConvertRun.getInstance(ext);
      final File out = JFileSaver.showSaveDialog(MainGui.getWindow(), name, cv
          .extension()[0], cv.description());

      if (out != null) {
        MainGui.getWindow().beforeRunnableSwing();

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            try {
              cv.convert(model.getDataRun(), out);
              JShowMessage.ok(rb.getString("exportOK"), rb
                  .getString("exportTitle"));
            }
            catch (GeoConvertException e) {
              log.error("", e);
              JShowMessage.error(e.getMessage());
            }
            catch (SQLException e) {
              log.error("", e);
              JShowMessage.error(e.getMessage());
            }
            MainGui.getWindow().afterRunnableSwing();
          }
        });

      }
    }
  }

  private class GoogleEarthShowActionListener implements ActionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            IGoogleEarth ge = GoogleEarthFactory.getDefault();

            // Determine si google earth est installe
            if (!ge.isInstalled()) {
              JShowMessage.error(rb.getString("installGoogleEarth"));
            }
            else {
              // recuperation des pistes
              DataRun dataRun = JPanelRun.this.model.getDataRun();
              if (dataRun != null) {
                File kmlFile = FactoryGeoConvertRun
                    .getInstance(FactoryGeoConvertRun.KML).convert(dataRun);
                if (kmlFile != null) {
                  ge.open(kmlFile);
                }
              }
            }
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorDatabase"));
          }
          catch (GeoConvertException e) {
            log.error("", e);
            JShowMessage.error(e.getMessage());
          }
          catch (GoogleEarthException e) {
            log.error("", e);
            JShowMessage.error(e.getMessage());
          }
          MainGui.getWindow().afterRunnableSwing();
        }
      });

    }

  }

  private class EmailActionListener implements ActionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      JDialogRunSendEmail.prompt(getModel().getDataRun());
    }
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
   * @author Denis Apparicio
   * 
   */
  private class SaveActionListener implements ActionListener {

    public void actionPerformed(ActionEvent actionevent) {
      MainGui.getWindow().beforeRunnableSwing();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            model.save(JPanelRun.this);
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorSaveRun"));
          }
          MainGui.getWindow().afterRunnableSwing();
        }
      });

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DeleteActionListener implements ActionListener {

    public void actionPerformed(ActionEvent actionevent) {
      if (!JShowMessage.question(rb.getString("questionDeleteRace"), rb
          .getString("delete"))) {
        return;
      }
      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            model.delete(JPanelRun.this);
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorDeleteRace"));
          }
          MainGui.getWindow().afterRunnableSwing();
        }
      });

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DetailActionListener implements ActionListener {

    public void actionPerformed(ActionEvent actionevent) {
      JDialogRunDetail.prompt(getModel().getDataRun());
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MapMercatorActionListener implements ActionListener {

    public void actionPerformed(ActionEvent actionevent) {
      // JDialogMapMercator.prompt(getModel().getDataRun());
    }
  }

  
  /**
   * @author Denis apparicio
   *
   */
  private class PopupListener extends MouseAdapter {
    private JPopupMenu popup;

    public PopupListener(JPopupMenu popupMenu) {
      popup = popupMenu;
    }

    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  /**
   * @author Denis Apparicio
   *
   */
  public class ActivityComboBoxModel extends DefaultComboBoxModel {
    public ActivityComboBoxModel() {
      super();

      addElement("");
      try {
        List<AbstractDataActivity> list = UserActivityTableManager
            .getInstance().retreive();
        for (AbstractDataActivity d : list) {
          addElement(d);
        }
      }
      catch (SQLException e) {
        log.error("", e);
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
    public EquipementComboBoxModel() {
      super();
      addElement("");
      try {
        List<String> list = EquipementTableManager.getInstance()
            .retreiveNames();
        for (String d : list) {
          addElement(d);
        }
      }
      catch (SQLException e) {
        log.error("", e);
      }
    }
  }

}
