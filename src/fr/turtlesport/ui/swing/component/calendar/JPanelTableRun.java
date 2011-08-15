package fr.turtlesport.ui.swing.component.calendar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.ui.swing.component.jtable.DateShortDayCellRenderer;
import fr.turtlesport.ui.swing.component.jtable.DateTimeShortCellRenderer;
import fr.turtlesport.ui.swing.model.ModelRun;
import fr.turtlesport.ui.swing.model.ModelRunTable;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
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

  private JTableCustom             jTable;

  private DateShortDayCellRenderer dateShortDayCellRenderer = new DateShortDayCellRenderer();

  // model
  private ModelRunTable            model;

  private TableModelRun            tableModel;

  private JLabel                   jlabelRun;

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
    dateShortDayCellRenderer.setLocale(lang.getLocale());

    ResourceBundle rb = ResourceBundleUtility.getBundle(lang,
                                                        JPanelCalendar.class);
    tableModel.columnNames[1] = rb.getString("Date");
    jTable.getColumnModel().getColumn(1)
        .setHeaderValue(tableModel.columnNames[1]);
    jTable.packAll();
  }

  private void initialize() {
    tableModel = new TableModelRun();

    setFont(GuiFont.FONT_PLAIN);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(getJTable());

    setLayout(new BorderLayout(0, 0));
    add(scrollPane, BorderLayout.CENTER);
    add(getJlabelRun(), BorderLayout.SOUTH);

    jTable.getSelectionModel()
        .addListSelectionListener(new JTableListSelectionListener());

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  private JLabel getJlabelRun() {
    if (jlabelRun == null) {
      jlabelRun = new JLabel();
      jlabelRun.setAlignmentX(Component.LEFT_ALIGNMENT);
      jlabelRun.setFont(GuiFont.FONT_PLAIN_SMALL);
    }
    return jlabelRun;
  }

  private JTableCustom getJTable() {
    if (jTable == null) {
      jTable = new JTableCustom();
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
      jTable.packAll();
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
              setNumCourse(tableModel.listRows.size());
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
    setNumCourse(0);
    tableModel.listRows = listRun;
    if (listRun != null) {
      setNumCourse(listRun.size());
      tableModel.fireTableDataChanged();
      jTable.packAll();
    }
  }

  public void removeDate(Date date) {
  }

  private void setNumCourse(int nb) {
    switch (nb) {
      case 0:
        jlabelRun.setText("");
        break;
      case 1:
        jlabelRun.setText(nb + " course");
        break;
      default:
        jlabelRun.setText(nb + " courses");
        break;
    }
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

    public void performedUnitChanged(String unit) {
      columnNames[2] = unit;
      jTable.getColumnModel().getColumn(2).setHeaderValue(unit);

      if (listRows != null) {
        for (DataRun run : listRows) {
          run.setUnit(unit);
        }
        tableModel.fireTableDataChanged();
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
        }
      }
    }
  }

}
