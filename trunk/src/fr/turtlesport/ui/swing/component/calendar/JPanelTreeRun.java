package fr.turtlesport.ui.swing.component.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

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
import fr.turtlesport.ui.swing.component.jtable.DateShortDayCellRenderer;
import fr.turtlesport.ui.swing.model.ModelRun;
import fr.turtlesport.ui.swing.model.ModelRunTreeTable;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
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
    dateShortDayCellRenderer.setLocale(lang.getLocale());

    ResourceBundle rb = ResourceBundleUtility.getBundle(lang,
                                                        JPanelCalendar.class);
    tableModel.columnNames[0] = rb.getString("Date");
    jTreeTable.getColumnModel().getColumn(0)
        .setHeaderValue(tableModel.columnNames[0]);
    formatMonth = DateFormatSymbols.getInstance(lang.getLocale());

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

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
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

      // jTreeTable.addHighlighter(HighlighterFactory.createSimpleStriping());
      jTreeTable
          .addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                                               null,
                                               Color.RED));
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
    if (tableModel.listRun != null) {      
      // for (DataRun run : tableModel.listRun) {
      // if (run.getTime().equals(date)) {
      // run.setSportType(sportType);
      // packAll();
      // break;
      // }
      // }
      /*
       * DefaultMutableTreeTableNode deletedNode = null; for (int iyear = 0;
       * iyear < tableModel.getRoot().getChildCount(); iyear++) { // annee
       * DefaultMutableTreeTableNode year = (DefaultMutableTreeTableNode) root
       * .getChildAt(iyear); // month for (int imonth = 0; imonth <
       * year.getChildCount(); imonth++) { DefaultMutableTreeTableNode month =
       * (DefaultMutableTreeTableNode) year .getChildAt(imonth); // runs for
       * (int index = 0; index < month.getChildCount(); index++) {
       * DefaultMutableTreeTableNode nodeRun = (DefaultMutableTreeTableNode)
       * month .getChildAt(index); DataRun run = (DataRun)
       * nodeRun.getUserObject(); run.setSportType(sportType) break; } }
       */
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
    if (date != null && tableModel != null && tableModel.getRoot() != null) {
      return;
    }

    DataRun runDeleted = null;
    // Recuperation du noeud
    TreeTableNode root = tableModel.getRoot();
    // int index;
    DefaultMutableTreeTableNode deletedNode = null;
    for (int iyear = 0; iyear < root.getChildCount(); iyear++) {
      if (runDeleted != null) {
        break;
      }
      // annee
      DefaultMutableTreeTableNode year = (DefaultMutableTreeTableNode) root
          .getChildAt(iyear);
      // month
      for (int imonth = 0; imonth < year.getChildCount(); imonth++) {
        if (runDeleted != null) {
          break;
        }
        DefaultMutableTreeTableNode month = (DefaultMutableTreeTableNode) year
            .getChildAt(imonth);
        // runs
        for (int index = 0; index < month.getChildCount(); index++) {
          DefaultMutableTreeTableNode nodeRun = (DefaultMutableTreeTableNode) month
              .getChildAt(index);
          DataRun run = (DataRun) nodeRun.getUserObject();
          if (run.getTime().equals(date)) {
            runDeleted = run;
            deletedNode = nodeRun;
            break;
          }
        }
      }
    }

    if (deletedNode != null) {
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.IListDateRun#
   * fireCalendarSelectActiveDayPerformed(java.util.Date)
   */
  public void fireCalendarSelectActiveDayPerformed(Date date) {

    if (date != null && tableModel != null && tableModel.getRoot() != null) {
      TreeTableNode root = tableModel.getRoot();
      int row = -1;
      for (int iyear = 0; iyear < root.getChildCount(); iyear++) {
        row++;
        // annee
        DefaultMutableTreeTableNode year = (DefaultMutableTreeTableNode) root
            .getChildAt(iyear);
        // month
        for (int imonth = 0; imonth < year.getChildCount(); imonth++) {
          row++;
          DefaultMutableTreeTableNode month = (DefaultMutableTreeTableNode) year
              .getChildAt(imonth);
          // runs
          for (int index = 0; index < month.getChildCount(); index++) {
            row++;
            DefaultMutableTreeTableNode nodeRun = (DefaultMutableTreeTableNode) month
                .getChildAt(index);
            DataRun run = (DataRun) nodeRun.getUserObject();
            if (run.getTime().equals(date)) {
              TreePath path = jTreeTable.getPathForRow(row);
              jTreeTable.getTreeSelectionModel().setSelectionPath(path);
              return;
            }
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

    protected int runRow = -1;

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
      int[] tabIndex = jTreeTable.getSelectedRows();
      if (tabIndex != null && tabIndex.length == 1) {
        // une selection
        int viewRow = jTreeTable.getSelectedRow();
        if (viewRow >= 0) {
          TreePath treePath = jTreeTable.getPathForRow(viewRow);
          DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) treePath
              .getLastPathComponent();
          if (node.getUserObject() != null
              && node.getUserObject() instanceof DataRun) {
            runRow = viewRow;
            final DataRun dataRun = (DataRun) node.getUserObject();
            final int index = tableModel.listRun.indexOf(dataRun);
            if (index != -1) {
              updateNumCourse(index + 1);
            }
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

}
