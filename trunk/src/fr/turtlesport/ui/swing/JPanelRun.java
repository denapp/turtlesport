package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jfree.chart.ChartPanel;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.geo.FactoryGeoConvertRun;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.ui.swing.action.DeleteActionListener;
import fr.turtlesport.ui.swing.action.DetailActionListener;
import fr.turtlesport.ui.swing.action.DetailPointsActionListener;
import fr.turtlesport.ui.swing.action.EmailActionListener;
import fr.turtlesport.ui.swing.action.ExportActionListener;
import fr.turtlesport.ui.swing.action.GoogleEarthShowActionListener;
import fr.turtlesport.ui.swing.action.GoogleMapsShowActionListener;
import fr.turtlesport.ui.swing.action.MapMercatorActionListener;
import fr.turtlesport.ui.swing.component.JButtonCustom;
import fr.turtlesport.ui.swing.component.JMenuItemTurtle;
import fr.turtlesport.ui.swing.component.JPanelGraph;
import fr.turtlesport.ui.swing.component.JPanelMap;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTextAreaLength;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.model.ChangePointsEvent;
import fr.turtlesport.ui.swing.model.ChangePointsListener;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.ui.swing.model.ModelRun;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.OperatingSystem;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelRun extends JPanel implements LanguageListener,
                                     UnitListener, UserListener {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelRun.class);
  }

  private JLabel                  jLabelLibDistTot;

  private JLabel                  jLabelLibAllure;

  private JLabel                  jLabelLibSpeedMoy;

  private JLabel                  jLabelLibCalories;

  private JLabel                  jLabelLibTimeTot;

  private JLabel                  jLabelValTimeTot;

  private JLabel                  jLabelValDistTot;

  private JLabel                  jLabelLibTimePauseTot;

  private JLabel                  jLabelValTimePauseTot;

  private JLabel                  jLabelLibTimeMovingTot;

  private JLabel                  jLabelValTimeMovingTot;

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

  private JLabel                  jLabelLibLocation;

  private JComboBox               jComboBoxLocation;

  private JTextAreaLength         jTextFieldNotes;

  private TitledBorder            borderPanelRunLap;

  private JPanelNav               jPanelNav;

  private JPopupMenu              jPopupMenu;

  private JMenuItemTurtle         jMenuItemRunDetail;

  private JMenuItemTurtle         jMenuItemRunDetailGps;

  private JMenuItemTurtle         jMenuItemRunMap;

  private JMenuItemTurtle         jMenuItemRunGoogleEarth;

  private JMenuItemTurtle         jMenuItemRunEmail;

  private JMenuItemTurtle         jMenuItemRunDelete;

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

  private JButtonCustom           jButtonDelete;

  private JButtonCustom           jButtonGoogleEarth;

  private JButtonCustom           jButtonGoogleMap;

  private JButtonCustom           jButtonDetails;

  private JButtonCustom           jButtonDetailsGps;

  private JButtonCustom           jButtonEmail;

  private JPanel                  jPanelButtons;

  private JMenuItemTurtle         jMenuItemRunGoogleMap;

  private JTabbedPane             jTabbedPaneRace;

  private ChartPanel              chartPanelHeartZone;

  private JPanel                  jPanelSpeed;

  private JPanel                  jPanelHeart;

  private JPanel                  jPanelTextHeart;

  private JLabel[]                jLabelLibHearts;

  private JLabel[]                jLabelValHearts;

  private JLabel[]                jLabelLibSpeeds;

  private JLabel[]                jLabelValSpeeds;

  private JButton                 jButtonChartSpeed;

  private JButton                 jButtonTextSpeed;

  private JButton                 jButtonChartHeart;

  private JButton                 jButtonTextHeart;

  private ChartPanel              chartPanelSpeedZone;

  // model
  private ModelRun                model;

  private ActivityComboBoxModel   modelActivities;

  private EquipementComboBoxModel modelEquipements;

  private ResourceBundle          rb;

  private JPanel                  jPanelTextSpeed;

  private JPanelMeteo             jPanelMeteo;

  private JLabel                  jLabelValDateTime;

  private LocationComboBoxModel   modelLocations;

  /**
   * This is the default constructor.
   */
  public JPanelRun() {
    super();
    initialize();
    model = new ModelRun();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.UserListener#userSelect(int)
   */
  public void userSelect(int idUser) throws SQLException {
    if (!DataUser.isAllUser(idUser)) {
      model.updateView(this, (Date) null);
    }
    else {
      model.updateViewButtons(this);
    }
  }

  /**
   * @return the rb
   */
  public ResourceBundle getResourceBundle() {
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

  public JLabel getjLabelValDateTime() {
    return jLabelValDateTime;
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

  public JLabel getJLabelValTimePauseTot() {
    return jLabelValTimePauseTot;
  }

  public JLabel getJLabelValTimeMovingTot() {
    return jLabelValTimeMovingTot;
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

  public LocationComboBoxModel getModelLocation() {
    return modelLocations;
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
    getJMenuItemRunDetailGps().setEnabled(b);
    getJMenuItemRunGoogleEarth().setEnabled(b);
    getJMenuItemRunGoogleMap().setEnabled(b);
    if (jMenuItemRunEmail != null) {
      jMenuItemRunEmail.setEnabled(b);
    }
    getJMenuRunExport().setEnabled(b);
    getJMenuItemRunExportGpx().setEnabled(b);
    getJMenuItemRunExportGoogleEarth().setEnabled(b);
    getJMenuItemRunExportTcx().setEnabled(b);
    getJMenuItemRunExportHst().setEnabled(b);
    getJMenuItemRunDelete().setEnabled(b);
    getJButtonDelete().setEnabled(b);
    if (jButtonEmail != null) {
      jButtonEmail.setEnabled(b);
    }
    getJButtonGoogleEarth().setEnabled(b);
    getJButtonGoogleMap().setEnabled(b);
    getJButtonDetails().setEnabled(b);
    getJButtonDetailsGps().setEnabled(b);
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
    UnitManager.getManager().removeUnitListener(jDiagram);
    UnitManager.getManager().removeUnitListener(jPanelMap);
    UnitManager.getManager().removeUnitListener(jPanelMeteo);
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
    LanguageManager.getManager().removeLanguageListener(jDiagram);
    LanguageManager.getManager().removeLanguageListener(getJPanelMeteo());
  }

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, getClass());
    jButtonDelete.setToolTipText(rb.getString("jButtonDeleteToolTipText"));
    jButtonGoogleEarth.setToolTipText(rb
        .getString("jButtonGoogleEarthToolTipText"));
    jButtonGoogleMap
        .setToolTipText(rb.getString("jButtonGoogleMapToolTipText"));
    jButtonDetails.setToolTipText(rb.getString("jButtonDetailsToolTipText"));
    jButtonDetailsGps.setToolTipText(rb
        .getString("jButtonDetailsGpsToolTipText"));
    if (jButtonEmail != null) {
      jButtonEmail.setToolTipText(rb.getString("jButtonEmailToolTipText"));
    }
    jLabelLibDistTot.setText(rb.getString("jLabelLibDistTot"));
    jLabelLibTimeMovingTot.setText(rb.getString("jLabelLibTimeMovingTot"));
    jLabelLibTimeTot.setText(rb.getString("jLabelLibTimeTot"));
    jLabelLibTimePauseTot.setText(rb.getString("jLabelLibTimePauseTot"));
    jLabelLibAllure.setText(rb.getString("jLabelLibAllure"));
    jLabelLibSpeedMoy.setText(rb.getString("jLabelLibSpeedMoy"));
    jLabelLibCalories.setText(rb.getString("jLabelLibCalories"));
    getJButtonNext().setToolTipText(rb.getString("jButtonNextToolTipText"));
    getJButtonPrev().setToolTipText(rb.getString("jButtonPrevToolTipText"));
    jLabelLibHeart.setText(rb.getString("jLabelLibHeart"));
    borderPanelRunLap.setTitle(rb.getString("borderPanelRunLap"));
    jLabelLibAlt.setText(rb.getString("jLabelLibAlt"));
    jLabelLibEquipment.setText(rb.getString("jLabelLibEquipment"));
    jLabelLibLocation.setText(rb.getString("jLabelLibLocation"));
    jLabelLibActivity.setText(rb.getString("jLabelLibActivity"));
    jMenuItemRunDetail.setText(rb.getString("jMenuItemRunDetail"));
    jMenuItemRunMap.setText(rb.getString("jMenuItemRunMap"));
    jMenuItemRunGoogleEarth.setText(rb.getString("jMenuItemRunGoogleEarth"));
    jMenuItemRunGoogleMap.setText(rb.getString("jMenuItemRunGoogleMap"));
    if (jMenuItemRunEmail != null) {
      jMenuItemRunEmail.setText(rb.getString("jMenuItemRunEmail"));
    }
    jMenuRunExport.setText(rb.getString("jMenuRunExport"));
    jMenuItemRunExportGpx.setText(rb.getString("jMenuItemRunExportGpx"));
    jMenuItemRunExportGoogleEarth.setText(rb
        .getString("jMenuItemRunExportGoogleEarth"));
    jMenuItemRunExportTcx.setText(rb.getString("jMenuItemRunExportTcx"));
    jMenuItemRunExportHst.setText(rb.getString("jMenuItemRunExportHst"));
    jMenuItemRunDelete.setText(rb.getString("jMenuItemRunDelete"));
    jMenuItemRunDetailGps.setText(rb.getString("jMenuItemRunDetailGps"));

    // tabepane
    jTabbedPaneRace.setTitleAt(0, rb.getString("tabPane0"));
    jTabbedPaneRace.setTitleAt(3, rb.getString("tabPane3"));
    jTabbedPaneRace.setTitleAt(4, rb.getString("tabPane4"));

    // mis a jour nom de colonnes
    tableModelLap.performedLanguage();

    if (!chartPanelHeartZone.getLocale().equals(lang.getLocale())
        && model != null) {
      try {
        model.updateHeartZone(this);
      }
      catch (SQLException e) {
      }
    }

    for (int i = 0; i < jLabelLibSpeeds.length; i++) {
      jLabelLibSpeeds[i].setText(rb.getString("zone") + " " + (i + 1) + " : ");
    }
    repaint();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(660, 597);
    this.setLayout(new BorderLayout(5, 0));
    this.setOpaque(true);
    this.add(getJPopupMenu());
    this.add(getJPanelCenter(), BorderLayout.CENTER);
    this.add(getJPanelEast(), BorderLayout.EAST);

    setBorder(BorderFactory.createTitledBorder(""));

    ActionListener action;
    // evenement
    PopupListener popupListener = new PopupListener(getJPopupMenu());
    this.addMouseListener(popupListener);
    for (int i = 0; i < jTabbedPaneRace.getTabCount(); i++) {
      jTabbedPaneRace.getComponentAt(i).addMouseListener(popupListener);
    }
    jDiagram.addMouseListener(popupListener);

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

    action = new GoogleMapsShowActionListener();
    getJMenuItemRunGoogleMap().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunGoogleMap().addActionListener(action);
    getJButtonGoogleMap().addActionListener(action);

    MainGui.getWindow().getJMenuItemRunGoogleMap().addActionListener(action);
    getJButtonGoogleMap().addActionListener(action);

    if (jMenuItemRunEmail != null) {
      action = new EmailActionListener();
      jMenuItemRunEmail.addActionListener(action);
      jButtonEmail.addActionListener(action);
      MainGui.getWindow().getJMenuItemRunEmail().addActionListener(action);
    }

    action = new DeleteActionListener();
    getJMenuItemRunDelete().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunDelete().addActionListener(action);
    getJButtonDelete().addActionListener(action);

    action = new DetailActionListener();
    getJMenuItemRunDetail().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunDetail().addActionListener(action);
    getJButtonDetails().addActionListener(action);

    action = new DetailPointsActionListener();
    getJMenuItemRunDetailGps().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunDetailGps().addActionListener(action);
    getJButtonDetailsGps().addActionListener(action);

    action = new MapMercatorActionListener();
    getJMenuItemRunMap().addActionListener(action);
    MainGui.getWindow().getJMenuItemRunMap().addActionListener(action);

    jTabbedPaneRace.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (jTabbedPaneRace.getSelectedComponent() instanceof ChartPanel) {
          try {
            model.updateHeartZone(JPanelRun.this);
          }
          catch (SQLException sqle) {
          }
        }
      }
    });

    jButtonChartHeart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!(jPanelHeart.getComponent(1) instanceof ChartPanel)) {
          jPanelHeart.remove(1);
          jPanelHeart.add(chartPanelHeartZone, BorderLayout.CENTER);
          Configuration.getConfig().addProperty("Run",
                                                "heartchart",
                                                Boolean.toString(true));
          repaint();
        }
      }
    });
    jButtonTextHeart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (jPanelHeart.getComponent(1) instanceof ChartPanel) {
          jPanelHeart.remove(1);
          jPanelHeart.add(jPanelTextHeart, BorderLayout.CENTER);
          Configuration.getConfig().addProperty("Run",
                                                "heartchart",
                                                Boolean.toString(false));
          repaint();
        }
      }
    });

    jButtonChartSpeed.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!(jPanelSpeed.getComponent(1) instanceof ChartPanel)) {
          jPanelSpeed.remove(1);
          jPanelSpeed.add(chartPanelSpeedZone, BorderLayout.CENTER);
          Configuration.getConfig().addProperty("Run",
                                                "speedchart",
                                                Boolean.toString(true));
          repaint();
        }
      }
    });
    jButtonTextSpeed.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (jPanelSpeed.getComponent(1) instanceof ChartPanel) {
          jPanelSpeed.remove(1);
          jPanelSpeed.add(jPanelTextSpeed, BorderLayout.CENTER);
          Configuration.getConfig().addProperty("Run",
                                                "speedchart",
                                                Boolean.toString(false));
          repaint();
        }
      }
    });

    jComboBoxActivity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          model.updateHeartZone(JPanelRun.this);
        }
        catch (SQLException sqle) {
          log.error("", sqle);
        }
      }
    });

    // sauvegarde du texte
    jTextFieldNotes.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent event) {
        try {
          model.saveComments(JPanelRun.this);
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }

      public void focusGained(FocusEvent event) {
      }
    });

    // Equipement
    jComboBoxEquipment.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          model.saveEquipment(JPanelRun.this);
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }
    });

    // Activity
    jComboBoxActivity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        MainGui.getWindow().beforeRunnableSwing();

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            try {
              model.saveSportType(JPanelRun.this);
            }
            catch (SQLException e) {
              log.error("", e);
            }
            MainGui.getWindow().afterRunnableSwing();
          }
        });

      }
    });
    
    // Localisation
    jComboBoxLocation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          model.saveLocation(JPanelRun.this);
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }
    });

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);

    ModelPointsManager.getInstance().addChangeListener(tableModelLap);
    ModelPointsManager.getInstance().addChangeListener(getJPanelMeteo());
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
      jPopupMenu.add(getJMenuItemRunDetailGps());
      jPopupMenu.add(getJMenuItemRunMap());
      jPopupMenu.add(getJMenuItemRunGoogleEarth());
      jPopupMenu.add(getJMenuItemRunGoogleMap());
      if (Mail.isSupported()) {
        jMenuItemRunEmail = new JMenuItemTurtle();
        jMenuItemRunEmail.setFont(GuiFont.FONT_PLAIN);
        jMenuItemRunEmail.setAccelerator(MainGui.getWindow()
            .getMenuProperties(), "jMenuItemRunEmail");
        jMenuItemRunEmail.setEnabled(false);
        jPopupMenu.add(jMenuItemRunEmail);
      }
      jPopupMenu.add(getJMenuRunExport());
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
  protected JMenuItemTurtle getJMenuItemRunDetailGps() {
    if (jMenuItemRunDetailGps == null) {
      jMenuItemRunDetailGps = new JMenuItemTurtle();
      jMenuItemRunDetailGps.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunDetailGps.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunDetailGps");
      jMenuItemRunDetailGps.setEnabled(false);
    }
    return jMenuItemRunDetailGps;
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
   * This method initializes jMenuItemRunGoogleEarth.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunGoogleMap() {
    if (jMenuItemRunGoogleMap == null) {
      jMenuItemRunGoogleMap = new JMenuItemTurtle();
      jMenuItemRunGoogleMap.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunGoogleMap.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunGoogleMap");
      jMenuItemRunGoogleMap.setEnabled(false);
    }
    return jMenuItemRunGoogleMap;
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
  public JMenuItemTurtle getJMenuItemRunDelete() {
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

  public JTabbedPane getJTabbedPaneRace() {
    if (jTabbedPaneRace == null) {
      jTabbedPaneRace = new JTabbedPane();

      jTabbedPaneRace.setBorder(BorderFactory.createTitledBorder(""));
      // jTabbedPaneRace.setBorder(BorderFactory
      // .createEtchedBorder(EtchedBorder.LOWERED));

      jTabbedPaneRace.setFont(GuiFont.FONT_PLAIN);
      jTabbedPaneRace.addTab("Course", getJPanelRunSummary());
      jTabbedPaneRace.addTab(null,
                             ImagesRepository.getImageIcon("book-open.png"),
                             getJScrollPaneTextArea(),
                             null);
      jTabbedPaneRace.addTab(null,
                             ImagesRepository.getImageIcon("heart.gif"),
                             getJPanelChartHeart(),
                             null);
      jTabbedPaneRace.addTab("Speed", getJPanelChartSpeed());
      jTabbedPaneRace.addTab("Meteo", getJPanelMeteo());
      jTabbedPaneRace.setPreferredSize(new Dimension(200, 370));
    }
    return jTabbedPaneRace;
  }

  public JPanelMeteo getJPanelMeteo() {
    if (jPanelMeteo == null) {
      jPanelMeteo = new JPanelMeteo();
    }
    return jPanelMeteo;
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

      Insets insets = new Insets(0, 0, 5, 10);
      Insets insets2 = new Insets(10, 0, 5, 10);
      GridBagConstraints g = new GridBagConstraints();

      // Ligne 0
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.VERTICAL;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDateTime = new JLabel();
      jLabelValDateTime.setFont(GuiFont.FONT_ITALIC);
      jLabelValDateTime.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelValDateTime, g);

      // Ligne 1
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets2;
      jLabelLibDistTot = new JLabel();
      jLabelLibDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibDistTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets2;
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
      jLabelLibTimeTot = new JLabel();
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
      jLabelLibTimeMovingTot = new JLabel();
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
      jLabelLibTimePauseTot = new JLabel();
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

      // Ligne 4
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAllure = new JLabel();
      jLabelLibAllure.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllure.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAllure, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAllure = new JLabel();
      jLabelValAllure.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllure.setLabelFor(jLabelValAllure);
      jPanelRunSummary.add(jLabelValAllure, g);

      // Ligne 5
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibSpeedMoy = new JLabel();
      jLabelLibSpeedMoy.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoy.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibSpeedMoy, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValSpeedMoy = new JLabel();
      jLabelValSpeedMoy.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoy.setLabelFor(jLabelValSpeedMoy);
      jPanelRunSummary.add(jLabelValSpeedMoy, g);

      // Ligne 6
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibCalories = new JLabel();
      jLabelLibCalories.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCalories.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibCalories, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValCalories = new JLabel();
      jLabelValCalories.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCalories.setLabelFor(jLabelValCalories);
      jPanelRunSummary.add(jLabelValCalories, g);

      // Ligne 7
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHeart = new JLabel();
      jLabelLibHeart.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibHeart.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeart.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibHeart, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHeartAverage = new JLabel();
      jLabelValHeartAverage.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeart.setLabelFor(jLabelValHeartAverage);
      jPanelRunSummary.add(jLabelValHeartAverage, g);

      // Ligne 8
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAlt = new JLabel();
      jLabelLibAlt.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAlt.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAlt, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAlt = new JLabel();
      jLabelValAlt.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAlt.setLabelFor(jLabelValAlt);
      jPanelRunSummary.add(jLabelValAlt, g);

      // Ligne 9
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

      // Ligne 10
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibEquipment = new JLabel();
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

      // Ligne 11
      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibLocation = new JLabel();
      jLabelLibLocation.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLocation.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibLocation, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      modelLocations = new LocationComboBoxModel();
      jComboBoxLocation = new JComboBox(modelLocations);
      jComboBoxLocation.setEditable(true);
      jComboBoxLocation.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLocation.setLabelFor(jComboBoxLocation);
      jPanelRunSummary.add(jComboBoxLocation, g);
    }
    return jPanelRunSummary;
  }

  public JPanel getJPanelChartHeart() {
    if (jPanelHeart == null) {
      jPanelHeart = new JPanel();
      jPanelHeart.setLayout(new BorderLayout());

      JPanel panelButton = new JPanel();
      panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.Y_AXIS));

      jButtonChartHeart = new JButton(ImagesRepository.getImageIcon("statSmall.png"));
      jButtonChartHeart.setPreferredSize(new Dimension(24, 24));
      jButtonTextHeart = new JButton(ImagesRepository.getImageIcon("list.png"));
      jButtonTextHeart.setPreferredSize(new Dimension(24, 24));
      panelButton.add(jButtonChartHeart);
      panelButton.add(jButtonTextHeart);

      jPanelHeart.add(panelButton, BorderLayout.EAST);
      getJPanelTextHeart();
      getChartHeartZone();
      if (Configuration.getConfig().getPropertyAsBoolean("Run",
                                                         "heartchart",
                                                         true)) {
        jPanelHeart.add(getChartHeartZone(), BorderLayout.CENTER);
      }
      else {
        jPanelHeart.add(getJPanelTextHeart(), BorderLayout.CENTER);

      }
    }
    return jPanelHeart;
  }

  public JPanel getJPanelTextHeart() {
    if (jPanelTextHeart == null) {
      jPanelTextHeart = new JPanel();
      // jPanelTextHeart
      // .setLayout(new BoxLayout(jPanelTextHeart, BoxLayout.Y_AXIS));
      jPanelTextHeart.setLayout(new GridLayout(10, 1));

      jLabelLibHearts = new JLabel[5];
      jLabelValHearts = new JLabel[5];
      for (int i = 0; i < 5; i++) {
        jLabelLibHearts[i] = new JLabel();
        jLabelLibHearts[i].setFont(GuiFont.FONT_PLAIN);
        jLabelValHearts[i] = new JLabel();
        jLabelValHearts[i].setFont(GuiFont.FONT_ITALIC);
        jPanelTextHeart.add(jLabelLibHearts[i]);
        jPanelTextHeart.add(jLabelValHearts[i]);
      }

    }
    return jPanelTextHeart;
  }

  public JLabel[] getjLabelLibHearts() {
    return jLabelLibHearts;
  }

  public JLabel[] getjLabelValHearts() {
    return jLabelValHearts;
  }

  public ChartPanel getChartHeartZone() {
    if (chartPanelHeartZone == null) {
      chartPanelHeartZone = new ChartPanel(null);
      chartPanelHeartZone.setFont(GuiFont.FONT_PLAIN);
      chartPanelHeartZone.setPreferredSize(new Dimension(280, 350));
    }
    return chartPanelHeartZone;
  }

  public JLabel[] getjLabelLibSpeeds() {
    return jLabelLibSpeeds;
  }

  public JLabel[] getjLabelValSpeeds() {
    return jLabelValSpeeds;
  }

  public JPanel getJPanelChartSpeed() {
    if (jPanelSpeed == null) {
      jPanelSpeed = new JPanel();
      jPanelSpeed.setLayout(new BorderLayout());

      JPanel panelButton = new JPanel();
      panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.Y_AXIS));

      jButtonChartSpeed = new JButton(ImagesRepository.getImageIcon("statSmall.png"));
      jButtonChartSpeed.setPreferredSize(new Dimension(24, 24));
      jButtonTextSpeed = new JButton(ImagesRepository.getImageIcon("list.png"));
      jButtonTextSpeed.setPreferredSize(new Dimension(24, 24));
      panelButton.add(jButtonChartSpeed);
      panelButton.add(jButtonTextSpeed);

      jPanelSpeed.add(panelButton, BorderLayout.EAST);
      getJPanelTextSpeed();
      getChartSpeedZone();
      if (Configuration.getConfig().getPropertyAsBoolean("Run",
                                                         "speedchart",
                                                         true)) {
        jPanelSpeed.add(getChartSpeedZone(), BorderLayout.CENTER);
      }
      else {
        jPanelSpeed.add(getJPanelTextSpeed(), BorderLayout.CENTER);
      }
    }
    return jPanelSpeed;
  }

  public JPanel getJPanelTextSpeed() {
    if (jPanelTextSpeed == null) {
      // jPanelTextSpeed = new JPanel();
      // jPanelTextSpeed.setLayout(new GridLayout(10, 2));
      // jLabelLibSpeeds = new JLabel[10];
      // jLabelValSpeeds = new JLabel[10];
      // for (int i = 0; i < 10; i++) {
      // jLabelLibSpeeds[i] = new JLabel();
      // jLabelLibSpeeds[i].setFont(GuiFont.FONT_PLAIN);
      // jLabelValSpeeds[i] = new JLabel();
      // jLabelValSpeeds[i].setFont(GuiFont.FONT_ITALIC);
      // jPanelTextSpeed.add(jLabelLibSpeeds[i]);
      // jPanelTextSpeed.add(jLabelValSpeeds[i]);
      // }

      jPanelTextSpeed = new JPanel();
      jPanelTextSpeed.setLayout(new GridBagLayout());
      jLabelLibSpeeds = new JLabel[10];
      jLabelValSpeeds = new JLabel[10];

      for (int i = 0; i < 10; i++) {
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = 0.0;
        g.weighty = 1.0;
        g.anchor = GridBagConstraints.EAST;
        g.fill = GridBagConstraints.BOTH;
        jLabelLibSpeeds[i] = new JLabel();
        jLabelLibSpeeds[i].setFont(GuiFont.FONT_PLAIN);
        jLabelLibSpeeds[i].setHorizontalAlignment(SwingConstants.TRAILING);
        jPanelTextSpeed.add(jLabelLibSpeeds[i], g);
        g = new GridBagConstraints();
        g.weightx = 1.0;
        g.weighty = 1.0;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.BOTH;
        g.gridwidth = GridBagConstraints.REMAINDER;
        jLabelValSpeeds[i] = new JLabel();
        jLabelValSpeeds[i].setFont(GuiFont.FONT_PLAIN);
        jLabelLibSpeeds[i].setLabelFor(jLabelValSpeeds[i]);
        jPanelTextSpeed.add(jLabelValSpeeds[i], g);
      }
    }
    return jPanelTextSpeed;
  }

  public ChartPanel getChartSpeedZone() {
    if (chartPanelSpeedZone == null) {
      chartPanelSpeedZone = new ChartPanel(null);
      chartPanelSpeedZone.setFont(GuiFont.FONT_PLAIN);
      chartPanelSpeedZone.setPreferredSize(new Dimension(280, 350));
    }
    return chartPanelSpeedZone;
  }

  private JPanel getJPanelButtons() {
    if (jPanelButtons == null) {
      jPanelButtons = new JPanel();
      jPanelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelButtons.add(getJPanelNav());

      jPanelButtons.add(new Box.Filler(new Dimension(200, 10),
                                       new Dimension(120, 10),
                                       new Dimension(200, 10)));
      jPanelButtons.add(getJButtonDelete());
      jPanelButtons.add(getJButtonDetails());
      jPanelButtons.add(getJButtonDetailsGps());
      if (Mail.isSupported()) {
        jButtonEmail = new JButtonCustom(ImagesRepository.getImageIcon("email.png"));
        Dimension dim = new Dimension(20, 20);
        jButtonEmail.setPreferredSize(dim);
        jButtonEmail.setMaximumSize(dim);
        jButtonEmail.setEnabled(false);
        jButtonEmail.setOpaque(false);
        jPanelButtons.add(jButtonEmail);
      }
      jPanelButtons.add(getJButtonGoogleEarth());
      jPanelButtons.add(getJButtonGoogleMap());
    }
    return jPanelButtons;
  }

  public JButton getJButtonGoogleEarth() {
    if (jButtonGoogleEarth == null) {
      jButtonGoogleEarth = new JButtonCustom(ImagesRepository.getImageIcon("googleearth.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonGoogleEarth.setPreferredSize(dim);
      jButtonGoogleEarth.setMaximumSize(dim);
      jButtonGoogleEarth.setEnabled(false);
      jButtonGoogleEarth.setOpaque(false);

    }
    return jButtonGoogleEarth;
  }

  public JButton getJButtonGoogleMap() {
    if (jButtonGoogleMap == null) {
      jButtonGoogleMap = new JButtonCustom(ImagesRepository.getImageIcon("googlemaps.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonGoogleMap.setPreferredSize(dim);
      jButtonGoogleMap.setMaximumSize(dim);
      jButtonGoogleMap.setEnabled(false);
      jButtonGoogleMap.setOpaque(false);

    }
    return jButtonGoogleMap;
  }

  public JButton getJButtonEmail() {
    return jButtonEmail;
  }

  public JButton getJButtonDelete() {
    if (jButtonDelete == null) {
      jButtonDelete = new JButtonCustom(ImagesRepository.getImageIcon("trash.png"));
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
      jButtonDetails = new JButtonCustom(ImagesRepository.getImageIcon("loupe.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonDetails.setPreferredSize(dim);
      jButtonDetails.setMaximumSize(dim);
      jButtonDetails.setEnabled(false);
      jButtonDetails.setOpaque(false);

    }
    return jButtonDetails;
  }

  public JButton getJButtonDetailsGps() {
    if (jButtonDetailsGps == null) {
      jButtonDetailsGps = new JButtonCustom(ImagesRepository.getImageIcon("gps.png"));
      Dimension dim = new Dimension(20, 20);
      jButtonDetailsGps.setPreferredSize(dim);
      jButtonDetailsGps.setMaximumSize(dim);
      jButtonDetailsGps.setEnabled(false);
      jButtonDetailsGps.setOpaque(false);

    }
    return jButtonDetailsGps;
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
      // jPanelCenter.add(getJPanelRunLap());
      jPanelCenter.add(getJPanelMap());
      jPanelCenter.add(Box.createRigidArea(new Dimension(0, 10)));
      jPanelCenter.add(getJDiagram());
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
      jPanelEastCenter.add(getJPanelButtons());
      jPanelEastCenter.add(getJTabbedPaneRace());
      jPanelEastCenter.add(getJPanelRunLap());
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
      jPanelMap = new JPanelMap();
      /*
       * jPanelMap.setBorder(BorderFactory .createTitledBorder(null, "",
       * TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
       * GuiFont.FONT_PLAIN, null));
       */
      // Dimension dim = new Dimension(270, 270);
      Dimension dim = new Dimension(600, 400);
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
      jPanelRunLap.setPreferredSize(new Dimension(200, 300));
    }
    return jPanelRunLap;
  }

  /**
   * This method initializes jTableLap
   * 
   * @return javax.swing.JTable
   */
  private JXTable getJTableLap() {
    if (jTableLap == null) {
      synchronized (JPanelRun.class) {
        if (jTableLap == null) {
          jTableLap = new JXTable();
          if (OperatingSystem.isMacOSX()) {
            jTableLap.addHighlighter(HighlighterFactory
                .createAlternateStriping());
          }
          jTableLap
              .addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                                                   null,
                                                   Color.RED));

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
      }
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
      jPanelGraph.setFont(GuiFont.FONT_PLAIN);
      jPanelGraph.setPreferredSize(new Dimension(600, 360));
    }
    return jPanelGraph;
  }

  /**
   * @return
   */
  public JPanelGraph getJDiagram() {
    if (jDiagram == null) {
      jDiagram = new JPanelGraph();
      jDiagram.setPreferredSize(new Dimension(600, 360));
      jDiagram
          .setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }
    return jDiagram;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TableModelLap extends AbstractTableModel implements
                                                       ChangePointsListener {

    private String      unitDistance = DistanceUnit.unitKm();

    private String[]    columnNames  = { DistanceUnit.getDefaultUnit(),
                                         "Temps",
                                         "Allure Moy. (mn/km)",
                                         "Vitesse Moy. (km/h)",
                                         "moy.",
                                         "max.",
                                         "Calories",
                                         "Denivele +",
                                         "Denivele -" };

    private final int[] columWidth   = { 35, 30, 30, 35, 35, 28, 37, 45, 45 };

    public TableModelLap() {
      super();
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
          case 0:
            // Distance
            performedHeader(DistanceUnit.getDefaultUnit(), 0);
            break;

          case 2:
            // Allure
            performedHeader(PaceUnit.getDefaultUnit(), 2);
            break;

          case 3:
            // Vitesse moyenne
            performedHeader(SpeedUnit.getDefaultUnit(), 3);
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
      performedHeader(unit, 0);
      performedHeader("mn/" + unit, 2);
      performedHeader(unit + "/h", 3);

      // mis a jour des valeurs
      DataRunLap[] runLaps = ModelPointsManager.getInstance().getRunLaps();
      if (!unit.equals(unitDistance) && runLaps != null) {
        double value;
        for (int i = 0; i < runLaps.length; i++) {
          value = DistanceUnit.convert(unitDistance,
                                       unit,
                                       runLaps[i].getTotalDist());
          runLaps[i].setTotalDist((float) value);
          fireTableCellUpdated(i, 0);
          fireTableCellUpdated(i, 2);
          fireTableCellUpdated(i, 3);
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
      return ModelPointsManager.getInstance().runLapsSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
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

      DataRunLap[] runLaps = ModelPointsManager.getInstance().getRunLaps();
      switch (columnIndex) {
        case 0: // Distance
          return DistanceUnit
              .formatMetersInKm(runLaps[rowIndex].getTotalDist());

        case 1: // Temps
          try {
            return TimeUnit.formatHundredSecondeTime(runLaps[rowIndex]
                .getMovingTotalTime());
          }
          catch (SQLException e) {
            return TimeUnit.formatHundredSecondeTime(runLaps[rowIndex]
                .getTotalTime());
          }
        case 2: // Allure Moy.
          try {
            return PaceUnit.computeAllure(runLaps[rowIndex].getTotalDist(),
                                          runLaps[rowIndex]
                                              .getMovingTotalTime());
          }
          catch (SQLException e) {
            return PaceUnit.computeAllure(runLaps[rowIndex].getTotalDist(),
                                          runLaps[rowIndex].getTotalTime());
          }

        case 3: // Vitesse Moy.
          try {
            return SpeedPaceUnit.computeFormatSpeed(runLaps[rowIndex]
                .getTotalDist(), runLaps[rowIndex].getMovingTotalTime());
          }
          catch (SQLException e) {
            return SpeedPaceUnit.computeFormatSpeed(runLaps[rowIndex]
                .getTotalDist(), runLaps[rowIndex].getTotalTime());
          }

        case 4: // Frequence cardiaque moy.
          return runLaps[rowIndex].getAvgHeartRate();

        case 5: // Frequence cardiaque max.
          return runLaps[rowIndex].getMaxHeartRate();

        case 6: // Calories
          return runLaps[rowIndex].getCalories();

        case 7: // Denivele +
          try {
            return Integer.toString(runLaps[rowIndex].computeDenivelePos());
          }
          catch (SQLException e) {
            log.error("", e);
          }
          return "";

        case 8: // Denivele -
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedAllPoints
     * (fr.turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedAllPoints(ChangePointsEvent changeEvent) {
      unitDistance = DistanceUnit.unitKm();
      if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
        performedUnit(DistanceUnit.getDefaultUnit());
      }
      fireTableDataChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedLap(fr.
     * turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedLap(ChangePointsEvent e) {
      int index = ModelPointsManager.getInstance().getLapIndex();
      jTableLap.getSelectionModel().setSelectionInterval(index, index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedPoint(fr
     * .turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedPoint(ChangePointsEvent e) {
    }

  }

  private class TableListSelectionListener implements ListSelectionListener {

    public TableListSelectionListener() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
     * .ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
      if (jTableLap.getRowCount() > 1) {
        ModelPointsManager.getInstance().setLap(null,
                                                jTableLap.getSelectedRow());
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

  /**
   * This method initializes jScrollPaneTextArea.
   * 
   * @return javax.swing.JTextField
   */
  private JScrollPane getJScrollPaneTextArea() {
    if (jScrollPaneTextArea == null) {
      jTextFieldNotes = new JTextAreaLength(13, 25);
      jTextFieldNotes.setMaxiMumCharacters(500);
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
      for (int i = 0; i < getSize(); i++) {
        AbstractDataActivity d = (AbstractDataActivity) getElementAt(i);
        if (d.getSportType() == sportType) {
          setSelectedItem(d);
          return;
        }
      }

      for (int i = 0; i < getSize(); i++) {
        AbstractDataActivity d = (AbstractDataActivity) getElementAt(i);
        if (d.getSportType() == DataActivityOther.SPORT_TYPE) {
          setSelectedItem(d);
          return;
        }
      }
    }

    public int getSportType() {
      Object obj = getSelectedItem();
      if (obj instanceof String || obj == null) {
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

  /**
   * @author Denis Apparicio
   * 
   */
  public class LocationComboBoxModel extends DefaultComboBoxModel {
    public LocationComboBoxModel() {
      super();
      fill();
    }

    public void fill() {
      removeAllElements();
      addElement("");
      try {
        List<String> list = RunTableManager.getInstance()
            .retreiveLocations(MainGui.getWindow().getCurrentIdUser());
        for (String d : list) {
          if (d != null && d.trim().length() > 0) {
            addElement(d.trim());
          }
        }
      }
      catch (SQLException e) {
        log.error("", e);
      }
    }

    public void setSelectedLocation(String location) {
      setSelectedItem((location == null) ? "" : location);
    }

    public boolean contains(Object value) {
      for (int i = 0; i < getSize(); i++) {
        if (getElementAt(i).equals(value)) {
          return true;
        }
      }
      return false;
    }
  }
}
