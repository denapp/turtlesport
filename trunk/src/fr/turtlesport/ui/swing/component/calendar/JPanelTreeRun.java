package fr.turtlesport.ui.swing.component.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.geo.FactoryGeoConvertRun;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.action.DeleteActionListener;
import fr.turtlesport.ui.swing.action.DetailActionListener;
import fr.turtlesport.ui.swing.action.DetailPointsActionListener;
import fr.turtlesport.ui.swing.action.EmailActionListener;
import fr.turtlesport.ui.swing.action.ExportActionListener;
import fr.turtlesport.ui.swing.action.ExportAllActionListener;
import fr.turtlesport.ui.swing.action.GoogleEarthShowActionListener;
import fr.turtlesport.ui.swing.action.GoogleMapsShowActionListener;
import fr.turtlesport.ui.swing.action.MapMercatorActionListener;
import fr.turtlesport.ui.swing.action.YearMonth;
import fr.turtlesport.ui.swing.component.JMenuItemTurtle;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.jtable.DateShortDayCellRenderer;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.ui.swing.model.ModelRun;
import fr.turtlesport.ui.swing.model.ModelRunTreeTable;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.OperatingSystem;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelTreeRun extends JPanel implements IListDateRunFire,
                                         LanguageListener, UnitListener {

  private static TurtleLogger             log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelTreeRun.class);
  }

  private JXTreeTable                     jTreeTable;

  private DateShortDayCellRenderer        dateShortDayCellRenderer = new DateShortDayCellRenderer();

  private DateFormatSymbols               formatMonth              = DateFormatSymbols
                                                                       .getInstance(LanguageManager
                                                                           .getManager()
                                                                           .getLocale());

  // model
  private ModelRunTreeTable               model                    = new ModelRunTreeTable();

  private TableModelRun                   tableModel               = new TableModelRun();

  private JLabel                          jLabelRun;

  private JTreeTableListSelectionListener selectionListener;

  // menu
  private JPopupMenu                      jPopupMenuRun;

  private JPopupMenu                      jPopupMenuDate;

  private JMenuItemTurtle                 jMenuItemRunDetail;

  private JMenuItemTurtle                 jMenuItemRunDetailGps;

  private JMenuItemTurtle                 jMenuItemRunMap;

  private JMenuItemTurtle                 jMenuItemRunGoogleEarth;

  private JMenuItemTurtle                 jMenuItemRunEmail;

  private JMenuItemTurtle                 jMenuItemRunDelete;

  private JMenuItemTurtle                 jMenuItemRunExportGoogleEarth;

  private JMenu                           jMenuRunExport;

  private JMenuItemTurtle                 jMenuItemRunExportGpx;

  private JMenuItemTurtle                 jMenuItemRunExportTcx;

  private JMenuItemTurtle                 jMenuItemRunExportHst;

  private JMenuItemTurtle                 jMenuItemRunGoogleMap;

  private JMenu                           jMenuDateExport;

  private JMenuItemTurtle                 jMenuItemDateDelete;

  private JMenuItemTurtle                 jMenuItemDateExportGpx;

  private JMenuItemTurtle                 jMenuItemDateExportGoogleEarth;

  private JMenuItemTurtle                 jMenuItemDateExportTcx;

  // Ressource
  private ResourceBundle                  rb;

  /**
   * Create the panel.
   */
  public JPanelTreeRun() {
    super();
    initialize();
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(final UnitEvent event) {
    if (!event.isEventDistance()) {
      return;
    }

    if (SwingUtilities.isEventDispatchThread()) {
      performedUnitChanged(event.getUnit());
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          performedUnitChanged(event.getUnit());
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
  }

  private void performedUnitChanged(String unit) {
    tableModel.columnNames[1] = unit;
    jTreeTable.getColumnModel().getColumn(1)
        .setHeaderValue(tableModel.columnNames[1]);
    tableModel.performedUnitChanged(unit);
    packAll();
  }

  /**
   * @param lang
   */
  protected void performedLanguage(ILanguage lang) {

    // table
    dateShortDayCellRenderer.setLocale(lang.getLocale());

    ResourceBundle rbDate = ResourceBundleUtility
        .getBundle(lang, JPanelCalendar.class);
    tableModel.columnNames[0] = rbDate.getString("Date");
    jTreeTable.getColumnModel().getColumn(0)
        .setHeaderValue(tableModel.columnNames[0]);
    formatMonth = DateFormatSymbols.getInstance(lang.getLocale());

    // popup menu
    rb = ResourceBundleUtility.getBundle(lang, JPanelRun.class);
    jMenuItemRunDetail.setText(rb.getString("jMenuItemRunDetail"));
    jMenuItemRunMap.setText(rb.getString("jMenuItemRunMap"));
    jMenuItemRunGoogleEarth.setText(rb.getString("jMenuItemRunGoogleEarth"));
    jMenuItemRunGoogleMap.setText(rb.getString("jMenuItemRunGoogleMap"));
    if (jMenuItemRunEmail != null) {
      jMenuItemRunEmail.setText(rb.getString("jMenuItemRunEmail"));
    }
    jMenuRunExport.setText(rb.getString("jMenuRunExport"));
    jMenuDateExport.setText(rb.getString("jMenuRunExport"));
    jMenuItemRunExportGpx.setText(rb.getString("jMenuItemRunExportGpx"));
    jMenuItemDateExportGpx.setText(rb.getString("jMenuItemRunExportGpx"));
    jMenuItemRunExportGoogleEarth.setText(rb
        .getString("jMenuItemRunExportGoogleEarth"));
    jMenuItemRunExportTcx.setText(rb.getString("jMenuItemRunExportTcx"));
    jMenuItemDateExportTcx.setText(rb.getString("jMenuItemRunExportTcx"));
    jMenuItemRunExportHst.setText(rb.getString("jMenuItemRunExportHst"));
    jMenuItemRunDelete.setText(rb.getString("jMenuItemRunDelete"));
    jMenuItemDateDelete.setText(rb.getString("jMenuItemRunDelete"));
    jMenuItemRunDetailGps.setText(rb.getString("jMenuItemRunDetailGps"));

    packAll();
  }

  private void initialize() {
    setFont(GuiFont.FONT_PLAIN);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(getJTreeTable());

    setLayout(new BorderLayout(0, 0));
    add(scrollPane, BorderLayout.CENTER);
    add(getJLabelRun(), BorderLayout.SOUTH);

    selectionListener = new JTreeTableListSelectionListener();
    jTreeTable.getSelectionModel().addListSelectionListener(selectionListener);

    getJPopupMenuDate();
    getJPopupMenuRun();

    // evenements
    PopupListener popupListener = new PopupListener();
    jTreeTable.addMouseListener(popupListener);

    final DetailActionListener actionDetails = new DetailActionListener();
    getJMenuItemRunDetail().addActionListener(actionDetails);

    final DetailPointsActionListener actionDetailsPoints = new DetailPointsActionListener();
    getJMenuItemRunDetailGps().addActionListener(actionDetailsPoints);

    final MapMercatorActionListener actionMap = new MapMercatorActionListener();
    getJMenuItemRunMap().addActionListener(actionMap);

    final GoogleEarthShowActionListener actionGoogleEarth = new GoogleEarthShowActionListener();
    getJMenuItemRunGoogleEarth().addActionListener(actionGoogleEarth);

    final GoogleMapsShowActionListener actionGoogleMaps = new GoogleMapsShowActionListener();
    getJMenuItemRunGoogleMap().addActionListener(actionGoogleMaps);

    if (getJMenuItemRunEmail() != null) {
      final EmailActionListener actionMail = new EmailActionListener();
      getJMenuItemRunEmail().addActionListener(actionMail);
    }
    
    final ExportActionListener actionKml = new ExportActionListener(FactoryGeoConvertRun.KML);
    getJMenuItemRunExportGoogleEarth().addActionListener(actionKml);

    final ExportActionListener actionHst = new ExportActionListener(FactoryGeoConvertRun.HST);
    getJMenuItemRunExportHst().addActionListener(actionHst);

    final ExportActionListener actionTcx = new ExportActionListener(FactoryGeoConvertRun.TCX);
    getJMenuItemRunExportTcx().addActionListener(actionTcx);
    final TreeExportAllActionListener actionAllTcx = new TreeExportAllActionListener(FactoryGeoConvertRun.TCX);
    getJMenuItemDateExportTcx().addActionListener(actionAllTcx);

    final ExportActionListener actionGpx = new ExportActionListener(FactoryGeoConvertRun.GPX);
    getJMenuItemRunExportGpx().addActionListener(actionGpx);
    final TreeExportAllActionListener actionAllGpx = new TreeExportAllActionListener(FactoryGeoConvertRun.GPX);
    getJMenuItemDateExportGpx().addActionListener(actionAllGpx);

    final DeleteActionListener actionDelete = new DeleteActionListener();
    getJMenuItemRunDelete().addActionListener(actionDelete);
    final DeleteAllActionListener actionAllDelete = new DeleteAllActionListener();
    getJMenuItemDateDelete().addActionListener(actionAllDelete);

    // Langue / unit
    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
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
  }

  /**
   * This method initializes jPanelRunSummary.
   * 
   * @return javax.swing.JPanel
   */
  private JPopupMenu getJPopupMenuRun() {
    if (jPopupMenuRun == null) {
      jPopupMenuRun = new JPopupMenu();
      jPopupMenuRun.add(getJMenuItemRunDetail());
      jPopupMenuRun.add(getJMenuItemRunDetailGps());
      jPopupMenuRun.add(getJMenuItemRunMap());
      jPopupMenuRun.add(getJMenuItemRunGoogleEarth());
      jPopupMenuRun.add(getJMenuItemRunGoogleMap());
      if (Mail.isSupported()) {
        jMenuItemRunEmail = new JMenuItemTurtle();
        jMenuItemRunEmail.setFont(GuiFont.FONT_PLAIN);
        jMenuItemRunEmail.setAccelerator(MainGui.getWindow()
            .getMenuProperties(), "jMenuItemRunEmail");
        jMenuItemRunEmail.setEnabled(false);
        jPopupMenuRun.add(jMenuItemRunEmail);
      }
      jPopupMenuRun.add(getJMenuRunExport());
      jPopupMenuRun.add(getJMenuItemRunDelete());
    }
    return jPopupMenuRun;
  }

  /**
   * This method initializes jPanelRunSummary.
   * 
   * @return javax.swing.JPanel
   */
  private JPopupMenu getJPopupMenuDate() {
    if (jPopupMenuDate == null) {
      jPopupMenuDate = new JPopupMenu();
      jPopupMenuDate.add(getJMenuDateExport());
      jPopupMenuDate.add(getJMenuItemDateDelete());
    }
    return jPopupMenuDate;
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
   * This method initializes jMenuRunExport.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenu getJMenuDateExport() {
    if (jMenuDateExport == null) {
      jMenuDateExport = new JMenu();
      jMenuDateExport.setFont(GuiFont.FONT_PLAIN);

      jMenuDateExport.add(getJMenuItemDateExportGpx());
      // jMenuDateExport.add(getJMenuItemDateExportGoogleEarth());
      jMenuDateExport.add(getJMenuItemDateExportTcx());
    }
    return jMenuDateExport;
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

  protected JMenuItemTurtle getJMenuItemDateExportGpx() {
    if (jMenuItemDateExportGpx == null) {
      jMenuItemDateExportGpx = new JMenuItemTurtle();
      jMenuItemDateExportGpx.setFont(GuiFont.FONT_PLAIN);
    }
    return jMenuItemDateExportGpx;
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
      jMenuItemRunExportGoogleEarth.setEnabled(false);
    }
    return jMenuItemRunExportGoogleEarth;
  }

  protected JMenuItemTurtle getJMenuItemDateExportGoogleEarth() {
    if (jMenuItemDateExportGoogleEarth == null) {
      jMenuItemDateExportGoogleEarth = new JMenuItemTurtle();
      jMenuItemDateExportGoogleEarth.setFont(GuiFont.FONT_PLAIN);
      jMenuItemDateExportGoogleEarth.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunExportGoogleEarth");
      jMenuItemDateExportGoogleEarth.setEnabled(false);
    }
    return jMenuItemDateExportGoogleEarth;
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

  protected JMenuItemTurtle getJMenuItemDateExportTcx() {
    if (jMenuItemDateExportTcx == null) {
      jMenuItemDateExportTcx = new JMenuItemTurtle();
      jMenuItemDateExportTcx.setFont(GuiFont.FONT_PLAIN);
      jMenuItemDateExportTcx.setAccelerator(MainGui.getWindow()
          .getMenuProperties(), "jMenuItemRunExportTcx");
    }
    return jMenuItemDateExportTcx;
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

  /**
   * This method initializes jMenuItemDateDelete.
   * 
   * @return javax.swing.JMenuItem
   */
  public JMenuItemTurtle getJMenuItemDateDelete() {
    if (jMenuItemDateDelete == null) {
      jMenuItemDateDelete = new JMenuItemTurtle();
      jMenuItemDateDelete.setFont(GuiFont.FONT_PLAIN);
    }
    return jMenuItemDateDelete;
  }

  private JLabel getJLabelRun() {
    if (jLabelRun == null) {
      jLabelRun = new JLabel();
      jLabelRun.setAlignmentX(Component.LEFT_ALIGNMENT);
      jLabelRun.setFont(GuiFont.FONT_PLAIN_SMALL);
    }
    return jLabelRun;
  }

  private JXTreeTable getJTreeTable() {
    if (jTreeTable == null) {
      synchronized (JPanelTreeRun.class) {
        if (jTreeTable == null) {
          jTreeTable = new JXTreeTable();
          jTreeTable.setEditable(false);
          jTreeTable.setFont(GuiFont.FONT_PLAIN);
          jTreeTable.setShowGrid(false);
          jTreeTable.setSortable(false);
          jTreeTable.setRootVisible(false);
          jTreeTable.getTableHeader().setFont(GuiFont.FONT_PLAIN);

          jTreeTable.setTreeCellRenderer(new MyTreeCellRenderer());
          jTreeTable.setTreeTableModel(tableModel);
          jTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          jTreeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

          if (OperatingSystem.isMacOSX()) {
            jTreeTable.addHighlighter(HighlighterFactory
                .createAlternateStriping());
          }
          jTreeTable
              .addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                                                   null,
                                                   Color.RED));
        }
      }
    }
    return jTreeTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireDatesUnselect
   * ()
   */
  public void fireSportChanged(Date date, int sportType) {
    if (date == null || tableModel == null || tableModel.getRoot() == null) {
      return;
    }

    // recuperation de la row du run par la selection
    if (selectionListener.runNode != null) {
      DataRun dataRun = (DataRun) selectionListener.runNode.getUserObject();
      if (dataRun.getTime().equals(date)) {
        dataRun.setSportType(sportType);
        tableModel.setValueAt(dataRun, selectionListener.runNode, 0);
        return;
      }
    }

    // non trouvee par la selection recherche classique
    DefaultMutableTreeTableNode node = findNode(date);
    if (node != null) {
      DataRun dataRun = (DataRun) node.getUserObject();
      dataRun.setSportType(sportType);
      tableModel.setValueAt(dataRun, node, 0);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireDatesUnselect
   * ()
   */
  public void fireDatesUnselect() {
    if (log.isDebugEnabled()) {
      log.debug(">>fireDatesUnselect");
    }

    jTreeTable.clearSelection();

    if (log.isDebugEnabled()) {
      log.debug("<<fireDatesUnselect");
    }
  }

  public void fireDateChanged(Date date) {
    if (log.isDebugEnabled()) {
      log.debug(">>fireDateChanged");
    }

    if (date == null) {
      return;
    }

    try {
      model.updateViewDateChanged(this, date);
    }
    catch (SQLException e) {
      log.error("", e);
    }

    if (log.isDebugEnabled()) {
      log.debug("<<fireDateChanged");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRun#fireHistoric(int)
   */
  public void fireHistoric(int idUser) throws SQLException {
    model.setIdUser(idUser);
    model.updateView(this);
    // mis a jour des boutons date en cours
    if (MainGui.getWindow().getRightComponent() instanceof JPanelRun) {
      JPanelRun p = (JPanelRun) MainGui.getWindow().getRightComponent();
      p.fireHistoric();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRun#fireDateDeleted
   * (java.util.Date)
   */
  public void fireDateDeleted(Date date) {
    if (date == null || tableModel == null || tableModel.getRoot() == null) {
      return;
    }

    // Recuperation du noeud
    DefaultMutableTreeTableNode deletedNode = findNode(date);
    if (deletedNode == null) {
      return;
    }

    DataRun runDeleted = (DataRun) deletedNode.getUserObject();
    // Suppression de la vue
    int count = deletedNode.getParent().getChildCount();
    if (count == 1) {
      // month
      DefaultMutableTreeTableNode month = (DefaultMutableTreeTableNode) deletedNode
          .getParent();
      DefaultMutableTreeTableNode year = (DefaultMutableTreeTableNode) month
          .getParent();
      count = year.getChildCount();
      tableModel.removeNodeFromParent(deletedNode);
      tableModel.removeNodeFromParent(month);
      // year
      if (count == 1) {
        tableModel.removeNodeFromParent(year);
      }
    }
    else {
      tableModel.removeNodeFromParent(deletedNode);
    }
    // Suppression de la liste
    tableModel.listRun.remove(runDeleted);
    updateNumCourse(0);
  }

  private DefaultMutableTreeTableNode findNode(Date date) {
    // Recuperation du noeud
    TreeTableNode root = tableModel.getRoot();

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.get(Calendar.YEAR);
    int theYear = cal.get(Calendar.YEAR);

    for (int iyear = 0; iyear < root.getChildCount(); iyear++) {
      // annee
      DefaultMutableTreeTableNode year = (DefaultMutableTreeTableNode) root
          .getChildAt(iyear);
      if (theYear != Integer.parseInt(year.getUserObject().toString())) {
        continue;
      }

      // month
      for (int imonth = 0; imonth < year.getChildCount(); imonth++) {
        DefaultMutableTreeTableNode month = (DefaultMutableTreeTableNode) year
            .getChildAt(imonth);
        // runs
        for (int index = 0; index < month.getChildCount(); index++) {
          DefaultMutableTreeTableNode nodeRun = (DefaultMutableTreeTableNode) month
              .getChildAt(index);
          DataRun run = (DataRun) nodeRun.getUserObject();
          if (run.getTime().equals(date)) {
            return nodeRun;
          }
        }
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.IListDateRun#
   * fireCalendarSelectActiveDayPerformed(java.util.Date)
   */
  public void fireCalendarSelectActiveDayPerformed(Date date) {
    if (date == null || tableModel == null || tableModel.getRoot() == null) {
      return;
    }
    TreeTableNode root = tableModel.getRoot();
    for (int iyear = 0; iyear < root.getChildCount(); iyear++) {
      // annee
      DefaultMutableTreeTableNode year = (DefaultMutableTreeTableNode) root
          .getChildAt(iyear);
      // month
      for (int imonth = 0; imonth < year.getChildCount(); imonth++) {
        DefaultMutableTreeTableNode month = (DefaultMutableTreeTableNode) year
            .getChildAt(imonth);
        // runs
        for (int index = 0; index < month.getChildCount(); index++) {
          DefaultMutableTreeTableNode nodeRun = (DefaultMutableTreeTableNode) month
              .getChildAt(index);
          DataRun run = (DataRun) nodeRun.getUserObject();
          if (run.getTime().equals(date)) {
            TreeTableNode[] nodes = tableModel.getPathToRoot(nodeRun);
            TreePath path = new TreePath(nodes);
            jTreeTable.scrollPathToVisible(path);
            jTreeTable.getTreeSelectionModel().setSelectionPath(path);
            // recuperation du focus
            jTreeTable.grabFocus();
            return;
          }
        }
      }
    }
  }

  public ModelRunTreeTable getModel() {
    return model;
  }

  public void fireCurrentRun(final List<DataRun> listRun) {
    tableModel = new TableModelRun();
    tableModel.listRun = listRun;
    DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();

    if (listRun != null) {
      Hashtable<String, DefaultMutableTreeTableNode> table = new Hashtable<String, DefaultMutableTreeTableNode>();
      for (DataRun run : listRun) {
        run.setUnit(DistanceUnit.getDefaultUnit());
        Calendar cal = Calendar.getInstance();
        cal.setTime(run.getTime());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        DefaultMutableTreeTableNode nodeYear = table
            .get(Integer.toString(year));
        if (nodeYear == null) {
          nodeYear = new DefaultMutableTreeTableNode(Integer.toString(year));
          root.add(nodeYear);
          table.put(Integer.toString(year), nodeYear);
        }
        String value = Integer.toString(year) + Integer.toString(month);
        DefaultMutableTreeTableNode nodeMonth = table.get(value);
        if (nodeMonth == null) {
          nodeMonth = new DefaultMutableTreeTableNode(month);
          nodeYear.add(nodeMonth);
          table.put(value, nodeMonth);
        }
        nodeMonth.add(new DefaultMutableTreeTableNode(run, false));
      }
    }
    tableModel.setRoot(root);
    jTreeTable.setTreeTableModel(tableModel);
    packAll();

    updateNumCourse(0);
  }

  public void removeDate(Date date) {
  }

  private void updateNumCourse(int selectedRow) {
    int row = (tableModel.listRun == null) ? 0 : tableModel.listRun.size();
    if (row != 0) {
      jLabelRun.setText("  " + selectedRow + "/" + row);
    }
    else {
      jLabelRun.setText(null);
    }
  }

  private void packAll() {
    int row = jTreeTable.getSelectedRow();
    jTreeTable.collapseAll();
    jTreeTable.expandAll();
    jTreeTable.packAll();
    // reselection
    if (row != -1) {
      jTreeTable.getTreeSelectionModel()
          .setSelectionPath(jTreeTable.getPathForRow(row));
    }
  }

  private TreeYearMonth retreiveYearMonth() {
    int viewRow = jTreeTable.getSelectedRow();
    if (viewRow == -1) {
      return null;
    }
    TreePath treePath = jTreeTable.getPathForRow(viewRow);
    DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) treePath
        .getLastPathComponent();
    if (node == null || node.getUserObject() == null) {
      return null;
    }

    int year = -1;
    int month = -1;
    if (node.getUserObject() instanceof String) {
      // Annee
      year = Integer.parseInt((String) node.getUserObject());
    }
    else if (node.getUserObject() instanceof Integer) {
      // Mois
      month = (Integer) node.getUserObject();
      year = Integer.parseInt((String) node.getParent().getUserObject());
    }
    else {
      // ne peut arriver
      return null;
    }

    return new TreeYearMonth(year, (month == -1) ? month : (month + 1));
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TableModelRun extends DefaultTreeTableModel {
    private String[]      columnNames = { "Date", DistanceUnit.getDefaultUnit() };

    private List<DataRun> listRun;

    public TableModelRun() {
      super();
    }

    public void performedUnitChanged(String unit) {
      if (listRun != null) {
        for (DataRun run : listRun) {
          run.setUnit(unit);
        }
      }
    }

    @Override
    public String getColumnName(int column) {
      return columnNames[column];
    }

    @Override
    public Object getChild(Object parent, int index) {
      if (parent instanceof DefaultMutableTreeTableNode) {
        DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) parent;
        return node.getChildAt(index);
      }
      return null;
    }

    @Override
    public Object getValueAt(Object obj, int column) {
      if (column == 1 && obj instanceof DefaultMutableTreeTableNode) {
        DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) obj;
        if (node.isLeaf()) {
          DataRun run = (DataRun) node.getUserObject();
          if (run != null) {
            try {
              return DistanceUnit.format(run.getComputeDistanceTot() / 1000.0);
            }
            catch (SQLException e) {
            }
          }
        }
        else {
          double distTot = 0;
          if (node.getParent().equals(getRoot())) {
            // year
            for (int imonth = 0; imonth < node.getChildCount(); imonth++) {
              DefaultMutableTreeTableNode month = (DefaultMutableTreeTableNode) node
                  .getChildAt(imonth);
              for (int index = 0; index < month.getChildCount(); index++) {
                DefaultMutableTreeTableNode child = (DefaultMutableTreeTableNode) month
                    .getChildAt(index);
                DataRun run = (DataRun) child.getUserObject();
                try {
                  distTot += run.getComputeDistanceTot();
                }
                catch (SQLException e) {
                }
              }
            }
          }
          else {
            // month
            for (int index = 0; index < node.getChildCount(); index++) {
              DefaultMutableTreeTableNode child = (DefaultMutableTreeTableNode) node
                  .getChildAt(index);
              DataRun run = (DataRun) child.getUserObject();
              try {
                distTot += run.getComputeDistanceTot();
              }
              catch (SQLException e) {
              }
            }
          }
          return DistanceUnit.format(distTot / 1000.0);
        }
      }
      return obj;
    }

    @Override
    public int getChildCount(Object parent) {
      if (parent instanceof DefaultMutableTreeTableNode) {
        DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) parent;
        return node.getChildCount();
      }
      return super.getChildCount(parent);
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class JTreeTableListSelectionListener implements
                                               ListSelectionListener {

    protected DefaultMutableTreeTableNode runNode;

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
     * .ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
      if (e != null && e.getValueIsAdjusting()) {
        return;
      }

      runNode = null;
      jTreeTable.grabFocus();

      // recuperation de la selection
      int[] tabIndex = jTreeTable.getSelectedRows();
      if (tabIndex == null || tabIndex.length != 1) {
        // une seule selection autorisee
        return;
      }
      int viewRow = jTreeTable.getSelectedRow();
      if (viewRow < 0) {
        return;
      }
      TreePath treePath = jTreeTable.getPathForRow(viewRow);
      DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) treePath
          .getLastPathComponent();
      if (node.getUserObject() == null
          || !(node.getUserObject() instanceof DataRun)) {
        return;
      }

      runNode = node;
      final DataRun dataRun = (DataRun) node.getUserObject();
      final int index = tableModel.listRun.indexOf(dataRun);

      MainGui.getWindow().beforeRunnableSwing();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            if (index != -1) {
              updateNumCourse(index + 1);
            }

            JPanelRun panelRun = null;

            Object obj = MainGui.getWindow().getRightComponent();
            if (!(obj instanceof JPanelRun)) {
              panelRun = new JPanelRun();
              MainGui.getWindow().setRightComponent(panelRun);
            }
            else {
              panelRun = (JPanelRun) obj;
            }

            // Recuperation du run.
            ModelRun model = panelRun.getModel();
            model.updateView(panelRun, dataRun);
          }
          catch (SQLException e) {
            log.error("", e);
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), JPanelCalendar.class);
            JShowMessage.error(rb.getString("errorSQL"));
          }
          finally {
            MainGui.getWindow().afterRunnableSwing();
          }
        }
      });
    }

  }

  private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    // icon from
    // http://www.iconarchive.com/show/points-of-interest-icons-by-icons-land/Bicycle-Green-2-icon.html
    // http://www.google.fr/imgres?q=icon+run&hl=fr&client=firefox-a&hs=CD8&sa=X&rls=org.mozilla:fr:official&biw=1280&bih=630&tbm=isch&tbnid=v0kwS2eax6suMM:&imgrefurl=http://www.start-run-grow.com/&docid=QDAjNw5-PYBR-M&w=38&h=36&ei=MNxETtCIHMW88gPgq7mdBg&zoom=1&iact=hc&vpx=207&vpy=200&dur=271&hovh=36&hovw=38&tx=85&ty=24&page=8&tbnh=36&tbnw=38&start=132&ndsp=18&ved=1t:429,r:6,s:132
    private ImageIcon iconRun         = new ImageIcon(getClass()
                                          .getResource("run2.png"));

    private ImageIcon iconBicycle     = new ImageIcon(getClass()
                                          .getResource("bicycle2.png"));

    private ImageIcon iconTransparent = new ImageIcon(getClass()
                                          .getResource("16px-transparent.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
      super.getTreeCellRendererComponent(tree,
                                         value,
                                         sel,
                                         expanded,
                                         leaf,
                                         row,
                                         hasFocus);
      setFont(GuiFont.FONT_PLAIN);

      DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) value;
      Object obj = node.getUserObject();
      if (obj instanceof Integer) {
        setText(formatMonth.getMonths()[(Integer) obj]);
      }
      else if (obj instanceof DataRun) {
        DataRun run = (DataRun) node.getUserObject();
        if (run != null) {
          String date = LanguageManager.getManager().getCurrentLang()
              .getDateTimeShortWithoutYearFormatter().format(run.getTime());
          setText(date);
          if (run.isSportRunning()) {
            setIcon(iconRun);
          }
          else if (run.isSportBike()) {
            setIcon(iconBicycle);
          }
          else {
            setIcon(iconTransparent);
          }
        }
      }

      return this;
    }

  }

  /**
   * @author Denis apparicio
   * 
   */
  private class PopupListener extends MouseAdapter {

    public PopupListener() {
    }

    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (!e.isPopupTrigger()) {
        return;
      }

      int viewRow = jTreeTable.getSelectedRow();
      if (viewRow == -1) {
        TreePath selPath = jTreeTable.getPathForLocation(e.getX(), e.getY());
        jTreeTable.getTreeSelectionModel().setSelectionPath(selPath);
        return;
      }

      TreePath selPath = jTreeTable.getPathForLocation(e.getX(), e.getY());
      TreePath treePath = jTreeTable.getPathForRow(viewRow);
      if (!selPath.equals(treePath)) {
        jTreeTable.getTreeSelectionModel().setSelectionPath(selPath);
        return;
      }

      DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) treePath
          .getLastPathComponent();
      if (node == null || node.getUserObject() == null) {
        return;
      }

      if (node.getUserObject() instanceof DataRun) {
        boolean hasPoint = ModelPointsManager.getInstance().hasPoints();
        setEnableMenuRun(hasPoint);
        getJMenuItemRunDelete().setEnabled(true);
        getJPopupMenuRun().show(e.getComponent(), e.getX(), e.getY());
      }
      else {
        // Annee et Mois
        getJPopupMenuDate().show(e.getComponent(), e.getX(), e.getY());
      }

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DeleteAllActionListener implements ActionListener {

    public void actionPerformed(ActionEvent actionevent) {
      if (!JShowMessage.question(rb.getString("questionDeleteRace"),
                                 rb.getString("delete"))) {
        return;
      }

      final TreeYearMonth yearMonth = retreiveYearMonth();

      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            DataRun current = ModelPointsManager.getInstance().getDataRun();

            // suppression
            boolean isDel = RunTableManager.getInstance()
                .delete(MainGui.getWindow().getCurrentIdUser(),
                        yearMonth.year,
                        yearMonth.month);
            if (isDel) {
              model.updateView(JPanelTreeRun.this);
            }

            // test si existe encore
            if (current != null
                && !RunTableManager.getInstance().exist(current.getId())) {
              ModelPointsManager.getInstance().setDataRun(JPanelTreeRun.this,
                                                          current);
            }
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorDeleteRace"));
          }
          finally {
            MainGui.getWindow().afterRunnableSwing();
          }
        }
      });

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TreeExportAllActionListener extends ExportAllActionListener {

    public TreeExportAllActionListener(String ext) {
      super(ext);
    }

    @Override
    public YearMonth getDate() {
      return retreiveYearMonth();
    }

  }

  // private class ExportAllActionListener implements ActionListener {
  //
  // private String ext;
  //
  // public ExportAllActionListener(String ext) {
  // this.ext = ext;
  // }
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
  // */
  // public void actionPerformed(ActionEvent ae) {
  //
  // final YearMonth yearMonth = retreiveYearMonth();
  // if (yearMonth == null) {
  // return;
  // }
  //
  // final IGeoConvertRun cv = FactoryGeoConvertRun.getInstance(ext);
  // final File out = JFileSaver.showSaveDialog(MainGui.getWindow(),
  // yearMonth.toString(),
  // cv.extension()[0],
  // cv.description());
  // if (out != null) {
  // MainGui.getWindow().beforeRunnableSwing();
  //
  // SwingUtilities.invokeLater(new Runnable() {
  // public void run() {
  // try {
  // // recuperation des run
  // List<DataRun> runs = RunTableManager.getInstance()
  // .retreiveDesc(MainGui.getWindow().getCurrentIdUser(),
  // yearMonth.year,
  // yearMonth.month);
  // // conversion
  // cv.convert(runs, null, out);
  // JShowMessage.ok(rb.getString("exportOK"),
  // rb.getString("exportTitle"));
  // }
  // catch (GeoConvertException e) {
  // log.error("", e);
  // JShowMessage.error(e.getMessage());
  // }
  // catch (SQLException e) {
  // log.error("", e);
  // JShowMessage.error(e.getMessage());
  // }
  // MainGui.getWindow().afterRunnableSwing();
  // }
  // });
  //
  // }
  // }
  // }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TreeYearMonth extends YearMonth {

    public TreeYearMonth(int year, int month) {
      super(year, month);
      this.year = year;
      this.month = month;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
      return getLibelle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.action.ExportAllActionListener.YearMonth#getLibelle
     * ()
     */
    @Override
    public String getLibelle() {
      StringBuilder name = new StringBuilder(Integer.toString(year));
      if (getMonth() != -1) {
        name.append('-');
        name.append(formatMonth.getMonths()[getMonth() - 1]);
      }
      return name.toString();
    }
  }

}
