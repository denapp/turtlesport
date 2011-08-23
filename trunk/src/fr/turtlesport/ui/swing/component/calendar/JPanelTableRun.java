package fr.turtlesport.ui.swing.component.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import fr.turtlesport.db.DataRun;
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
import fr.turtlesport.ui.swing.action.GoogleEarthShowActionListener;
import fr.turtlesport.ui.swing.action.GoogleMapsShowActionListener;
import fr.turtlesport.ui.swing.action.MapMercatorActionListener;
import fr.turtlesport.ui.swing.component.JMenuItemTurtle;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.jtable.DateShortDayCellRenderer;
import fr.turtlesport.ui.swing.component.jtable.DateTimeShortCellRenderer;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.ui.swing.model.ModelRun;
import fr.turtlesport.ui.swing.model.ModelRunTable;
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
public class JPanelTableRun extends JPanel implements IListDateRunFire,
                                          LanguageListener, UnitListener {

  private static TurtleLogger      log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelTableRun.class);
  }

  private JXTable                  jTable;

  private DateShortDayCellRenderer dateShortDayCellRenderer = new DateShortDayCellRenderer();

  // model
  private ModelRunTable            model;

  private TableModelRun            tableModel;

  private JLabel                   jLabelRun;

  private JPopupMenu               jPopupMenu;

  private JMenuItemTurtle          jMenuItemRunDetail;

  private JMenuItemTurtle          jMenuItemRunDetailGps;

  private JMenuItemTurtle          jMenuItemRunMap;

  private JMenuItemTurtle          jMenuItemRunGoogleEarth;

  private JMenuItemTurtle          jMenuItemRunEmail;

  private JMenuItemTurtle          jMenuItemRunDelete;

  private JMenuItemTurtle          jMenuItemRunExportGoogleEarth;

  private JMenu                    jMenuRunExport;

  private JMenuItemTurtle          jMenuItemRunExportGpx;

  private JMenuItemTurtle          jMenuItemRunExportTcx;

  private JMenuItemTurtle          jMenuItemRunExportHst;

  private JMenuItemTurtle          jMenuItemRunGoogleMap;

  // Ressource
  private ResourceBundle           rb;

  /**
   * Create the panel.
   */
  public JPanelTableRun() {
    super();
    initialize();
    setModel(new ModelRunTable());
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
    tableModel.performedUnitChanged(unit);
  }

  /**
   * @param lang
   */
  private void performedLanguage(ILanguage lang) {
    // table
    dateShortDayCellRenderer.setLocale(lang.getLocale());
    rb = ResourceBundleUtility.getBundle(lang, JPanelCalendar.class);

    tableModel.columnNames[1] = rb.getString("Date");
    jTable.getColumnModel().getColumn(1)
        .setHeaderValue(tableModel.columnNames[1]);

    tableModel.performedLanguage();

    // menu de la table
    rb = ResourceBundleUtility.getBundle(lang, JPanelRun.class);

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

  }

  private void initialize() {
    tableModel = new TableModelRun();

    setFont(GuiFont.FONT_PLAIN);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(getJTable());

    setLayout(new BorderLayout(0, 0));
    add(scrollPane, BorderLayout.CENTER);
    add(getJLabelRun(), BorderLayout.SOUTH);

    jTable.getSelectionModel()
        .addListSelectionListener(new JTableListSelectionListener());

    // Evenements
    jTable.addMouseListener(new PopupListener());

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

    final EmailActionListener actionMail = new EmailActionListener();
    getJMenuItemRunEmail().addActionListener(actionMail);

    final ExportActionListener actionKml = new ExportActionListener(FactoryGeoConvertRun.KML);
    getJMenuItemRunExportGoogleEarth().addActionListener(actionKml);

    final ExportActionListener actionHst = new ExportActionListener(FactoryGeoConvertRun.HST);
    getJMenuItemRunExportHst().addActionListener(actionHst);

    final ExportActionListener actionTcx = new ExportActionListener(FactoryGeoConvertRun.TCX);
    getJMenuItemRunExportTcx().addActionListener(actionTcx);

    final ExportActionListener actionGpx = new ExportActionListener(FactoryGeoConvertRun.GPX);
    getJMenuItemRunExportGpx().addActionListener(actionGpx);

    final DeleteActionListener actionDelete = new DeleteActionListener();
    getJMenuItemRunDelete().addActionListener(actionDelete);

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

  private void updateNumCourse() {
    int row = tableModel.getRowCount();
    if (row != 0) {
      int selectedRow = (jTable.getSelectedRow() == -1) ? 0 : (jTable
          .getSelectedRow() + 1);
      jLabelRun.setText("  " + selectedRow + "/" + row);
    }
    else {
      jLabelRun.setText(null);
    }
  }

  private JLabel getJLabelRun() {
    if (jLabelRun == null) {
      jLabelRun = new JLabel();
      jLabelRun.setAlignmentX(Component.LEFT_ALIGNMENT);
      jLabelRun.setFont(GuiFont.FONT_PLAIN_SMALL);
    }
    return jLabelRun;
  }

  private JXTable getJTable() {
    if (jTable == null) {
      getJPopupMenu();
      jTable = new JXTable();
      if (OperatingSystem.isMacOSX()) {
        jTable.addHighlighter(HighlighterFactory.createAlternateStriping());
      }
      jTable
          .addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                                               null,
                                               Color.RED));
      jTable.setModel(tableModel);
      jTable.setFont(GuiFont.FONT_PLAIN);
      jTable.setShowGrid(false);
      jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      // colum size
      TableColumn column;
      // jTable.setRowHeight(22);
      for (int i = 0; i < tableModel.getColumnCount(); i++) {
        column = jTable.getColumnModel().getColumn(i);
        // column.setPreferredWidth(tableModel.getPreferredWidth(i));
        if (tableModel.hasRenderer(i)) {
          column.setCellRenderer(tableModel.getCellRenderer(i));
        }
      }
      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      jTable.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTable.setSortable(false);
      jTable.packAll();
    }
    return jTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireDatesUnselect
   * ()
   */
  public void fireSportChanged(Date date, int sportType) {
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

    jTable.clearSelection();

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
    if (date != null && tableModel != null && tableModel.listRows != null) {
      for (int i = 0; i < tableModel.listRows.size(); i++) {
        DataRun run = tableModel.listRows.get(i);
        if (run.getTime().equals(date)) {
          int row = jTable.convertRowIndexToView(i);
          tableModel.listRows.remove(i);
          tableModel.fireTableRowsDeleted(row, row);
          if (tableModel.listRows.size() > 0) {
            if (row >= tableModel.listRows.size()) {
              row--;
            }
            try {
              jTable.setRowSelectionInterval(row, row);
              updateNumCourse();
            }
            catch (IllegalArgumentException e) {
            }
          }
          break;
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.IListDateRun#
   * fireCalendarSelectActiveDayPerformed(java.util.Date)
   */
  public void fireCalendarSelectActiveDayPerformed(Date date) {
    if (date != null && tableModel != null && tableModel.listRows != null) {
      for (int i = 0; i < tableModel.listRows.size(); i++) {
        DataRun run = tableModel.listRows.get(i);
        if (run.getTime().equals(date)) {
          int row = jTable.convertRowIndexToView(i);
          jTable.setRowSelectionInterval(row, row);
          break;
        }
      }
    }
  }

  public ModelRunTable getModel() {
    return model;
  }

  public void setModel(ModelRunTable model) {
    this.model = model;
  }

  public void fireCurrentRun(List<DataRun> listRun) {
    tableModel.listRows = listRun;
    if (listRun != null) {
      tableModel.fireTableDataChanged();
      jTable.packAll();
    }
    updateNumCourse();
  }

  public void removeDate(Date date) {
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TableModelRun extends AbstractTableModel {

    private String[]         columnNames   = { "",
                                               "Date",
                                               DistanceUnit.getDefaultUnit() };

    private final Class<?>[] columnClasses = { Date.class,
                                               Date.class,
                                               String.class };

    private List<DataRun>    listRows      = new ArrayList<DataRun>();

    /**
     * @param files
     */
    public TableModelRun() {
      super();
    }

    public void performedLanguage() {
      if (listRows != null) {
        int row = jTable.getSelectedRow();
        tableModel.fireTableDataChanged();
        if (row != -1) {
          jTable.getSelectionModel().setSelectionInterval(row, row);
        }
      }
      jTable.packAll();
    }

    public void performedUnitChanged(String unit) {
      columnNames[2] = unit;
      jTable.getColumnModel().getColumn(2).setHeaderValue(unit);

      if (listRows != null) {
        for (DataRun run : listRows) {
          run.setUnit(unit);
        }

        int row = jTable.getSelectedRow();
        tableModel.fireTableDataChanged();
        if (row != -1) {
          jTable.getSelectionModel().setSelectionInterval(row, row);
        }
      }

      jTable.packAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
      return columnNames[column];
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int c) {
      return columnClasses[c];
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
      return listRows.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
      switch (column) {
        case 0: // jour
        case 1: // Date
          return listRows.get(row).getTime();
        case 2: // Date
          try {
            return DistanceUnit.format(listRows.get(row)
                .getComputeDistanceTot() / 1000.0);
          }
          catch (SQLException e) {
            return "";
          }
        default:
          return "";
      }
    }

    public TableCellRenderer getCellRenderer(int column) {
      switch (column) {
        case 0: // Short days
          return dateShortDayCellRenderer;
        case 1: // Date heure
          return new DateTimeShortCellRenderer();
        default:
          return null;
      }
    }

    public boolean hasRenderer(int column) {
      switch (column) {
        case 0:
        case 1:
          return true;
        default:
          return false;
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class JTableListSelectionListener implements ListSelectionListener {

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

      int[] tabIndex = jTable.getSelectedRows();
      if (tabIndex != null && tabIndex.length == 1) {
        // une selection
        int viewRow = jTable.getSelectedRow();
        if (viewRow >= 0) {
          int modelRow = jTable.convertRowIndexToModel(viewRow);
          final DataRun dataRun = tableModel.listRows.get(modelRow);

          MainGui.getWindow().beforeRunnableSwing();
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
              try {
                ModelRun model = panelRun.getModel();
                model.updateView(panelRun, dataRun);
              }
              catch (SQLException e) {
                log.error("", e);
                ResourceBundle rb = ResourceBundleUtility
                    .getBundle(LanguageManager.getManager().getCurrentLang(),
                               JPanelCalendar.class);
                JShowMessage.error(rb.getString("errorSQL"));
              }
              MainGui.getWindow().afterRunnableSwing();
            }
          });

          // mis a jour libelle course
          updateNumCourse();
        }
      }
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

      int viewRow = jTable.getSelectedRow();
      int row = jTable.rowAtPoint(e.getPoint());
      if (viewRow != row) {
        jTable.getSelectionModel().setSelectionInterval(row, row);
        return;
      }

      boolean hasPoint = ModelPointsManager.getInstance().hasPoints();
      setEnableMenuRun(hasPoint);
      getJMenuItemRunDelete().setEnabled(true);

      getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
    }
  }

}
