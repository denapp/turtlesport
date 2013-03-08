package fr.turtlesport.ui.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.activation.ActivationDataFlavor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.ui.swing.component.JTurtleMapKitCompare;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelCompareRun extends JPanel {

  private TurtleLogger         log = (TurtleLogger) TurtleLogger
                                       .getLogger(JPanelCompareRun.class);

  private TableModelDistance   tableModel;

  private JTurtleMapKitCompare jPanelMap;

  private JScrollPane          jPanelRun;

  private JTableCustom         jTable;

  private TitledBorder         borderPanelRunLap;

  private ResourceBundle       rb  = ResourceBundleUtility
                                       .getBundle(LanguageManager.getManager()
                                                      .getCurrentLang(),
                                                  JDialogRunDetail.class);

  /**
   * Create the panel.
   */
  public JPanelCompareRun() {
    super();
    this.setSize(660, 597);
    initialize();
  }

  private void initialize() {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    getJPanelMap();

    add(getJPanelMap());
    add(Box.createRigidArea(new Dimension(5, 0)));

    JPanel panelRight = new JPanel();
    panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
    panelRight.add(getJPanelRunLap());
    add(panelRight);
  }

  public JTurtleMapKitCompare getJPanelMap() {
    if (jPanelMap == null) {
      jPanelMap = new JTurtleMapKitCompare();
      jPanelMap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      // mapKit.setBorder(BorderFactory
      // .createTitledBorder(null,
      // "",
      // TitledBorder.DEFAULT_JUSTIFICATION,
      // TitledBorder.DEFAULT_POSITION,
      // GuiFont.FONT_PLAIN,
      // null));
      Dimension dim = new Dimension(600, 597);
      jPanelMap.setPreferredSize(dim);
      jPanelMap.setGeoPositionVisible(true);
      jPanelMap.setTimeVisible(true);
    }
    return jPanelMap;
  }

  /**
   * This method initializes jPanelRun.
   * 
   * @return javax.swing.JPanel
   */
  public JScrollPane getJPanelRunLap() {
    if (jPanelRun == null) {
      jPanelRun = new JScrollPane();
      Dimension dim = new Dimension(280, 600);
      jPanelRun.setPreferredSize(dim);
      jPanelRun.setMinimumSize(dim);
      jPanelRun.setOpaque(true);

      borderPanelRunLap = BorderFactory
          .createTitledBorder(null,
                              "Courses",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelRun.setBorder(borderPanelRunLap);
      jPanelRun.setViewportView(getJTableRun());
    }
    return jPanelRun;
  }

  /**
   * This method initializes jTableLap
   * 
   * @return javax.swing.JTable
   */
  private JTable getJTableRun() {
    if (jTable == null) {
      jTable = new JTableCustom();
      jTable.setDragEnabled(true);
      jTable.setDropMode(DropMode.INSERT);
      jTable.setTransferHandler(new MyTransferHandler());
      tableModel = new TableModelDistance();
      jTable.setModel(tableModel);
      jTable.setFont(GuiFont.FONT_PLAIN);
      jTable.setShowGrid(false);
      jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      // colum size
      TableColumn column;
      for (int i = 0; i < tableModel.getColumnCount(); i++) {
        column = jTable.getColumnModel().getColumn(i);
        column.setPreferredWidth(tableModel.getPreferredWidth(i));
      }
      jTable.getColumn(0).setHeaderRenderer(new MyRenderer());

      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      jTable.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTable.getTableHeader().resizeAndRepaint();
      jTable.setRowSelectionAllowed(true);
    }
    return jTable;
  }

  /**
   * @author denis
   * 
   */
  public class TableModelDistance extends AbstractTableModel {

    private String[]          columnNames   = { "",
                                                "Distance (km)",
                                                "Temps total",
                                                "Temps",
                                                "Allure Moy. (mn/km)",
                                                "Vitesse Moy. (km/h)",
                                                "moy.",
                                                "max." };

    private final Class<?>[]  columnClasses = { Boolean.class,
                                                String.class,
                                                String.class,
                                                String.class,
                                                String.class,
                                                String.class,
                                                String.class,
                                                String.class };

    private final int[]       columWidth    = { 20, 50, 50, 30, 70, 70, 35, 30 };

    private List<DataRunShow> runs          = new ArrayList<DataRunShow>();

    public TableModelDistance() {

      for (int i = 0; i < columnNames.length; i++) {
        switch (i) {
          case 0:
            break;

          case 1:
            // Distance
            columnNames[i] = initColumn(DistanceUnit.getDefaultUnit(), i - 1);
            break;

          case 4:
            // Allure
            columnNames[i] = initColumn(PaceUnit.getDefaultUnit(), i - 1);
            break;

          case 5:
            // Vitesse moyenne
            columnNames[i] = initColumn(SpeedUnit.getDefaultUnit(), i - 1);
            break;

          default:
            columnNames[i] = rb.getString("TableModel_header" + (i - 1));
        }
      }

    }

    private String initColumn(String unit, int index) {
      return MessageFormat.format(rb.getString("TableModel_header" + index),
                                  unit);
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
    @Override
    public String getColumnName(int i) {
      return columnNames[i];
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
      return (runs == null) ? 0 : runs.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {

      DataRun dataRun = runs.get(rowIndex).run;

      try {
        int timeActif = dataRun.computeTimeTot()
                        - dataRun.computeTimePauseTot();
        switch (columnIndex) {
          case 0: // Afficher
            return runs.get(rowIndex).isShow;

          case 1: // Distance
            return DistanceUnit
                .format(dataRun.getComputeDistanceTot() / 1000.0);

          case 2: // Temps Total
            return TimeUnit.formatHundredSecondeTime(dataRun.computeTimeTot());

          case 3: // Temps actif
            return TimeUnit.formatHundredSecondeTime(timeActif);

          case 4: // Allure Moy.
            return PaceUnit.computeAllure(dataRun.getComputeDistanceTot(),
                                          timeActif);

          case 5: // Vitesse Moy.
            return SpeedPaceUnit.computeFormatSpeed(dataRun
                .getComputeDistanceTot(), timeActif);

          case 6: // Frequence cardiaque moy.
            // frequence moyenne/max/min.
            int avg = RunLapTableManager.getInstance()
                .heartAvg(dataRun.getId());
            int min = RunTrkTableManager.getInstance()
                .heartMin(dataRun.getId());
            int max = RunLapTableManager.getInstance()
                .heartMax(dataRun.getId());

            return Integer.toString(avg) + " / " + Integer.toString(min)
                   + " / " + Integer.toString(max);
          case 7: // Altitude
            int[] alt = RunTrkTableManager.getInstance()
                .altitude(dataRun.getId());
            return "+" + Integer.toString(alt[0]) + " / -"
                   + Integer.toString(alt[1]);
          default:
            return "";
        }
      }
      catch (SQLException e) {
        return "N/A";
      }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     * int, int)
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
      if (col == 0) {
        runs.get(row).isShow = (Boolean) value;
        fireTableCellUpdated(jTable.convertRowIndexToView(row), col);

        if (runs.get(row).isShow) {
          jPanelMap.getMapListener().addRun(runs.get(row).run);

        }
        else {
          jPanelMap.getMapListener().removeRun(runs.get(row).run);
        }
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex == 0;
    }

    /**
     * Mis a jour des donnees de la table.
     * 
     * @param runLaps
     */
    public void updateData(DataRun dataRun) {
      if (dataRun != null && !contains(dataRun)) {
        runs.add(new DataRunShow(dataRun));
        fireTableDataChanged();
        jPanelMap.getMapListener().addRun(dataRun);
      }
    }

    private boolean contains(DataRun dataRun) {
      for (DataRunShow drs : runs) {
        if (drs.equals(dataRun)) {
          return true;
        }
      }
      return false;
    }
  }

  private class MyRenderer implements TableCellRenderer {

    private final ImageIcon icon = ImagesRepository.getImageIcon("eye.png");

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
      // Extract the original header renderer for this column.
      TableCellRenderer tcr = table.getTableHeader().getDefaultRenderer();

      // Extract the component used to render the column header.
      Component c = tcr.getTableCellRendererComponent(table,
                                                      value,
                                                      isSelected,
                                                      hasFocus,
                                                      row,
                                                      column);

      if (c instanceof JLabel) {
        ((JLabel) c).setIcon(icon);
      }

      return c;
    }
  }

  private class DataRunShow {
    public boolean isShow = true;

    public DataRun run;

    public DataRunShow(DataRun run) {
      this.run = run;
    }

    @Override
    public boolean equals(Object obj) {
      return run.equals(obj);
    }

    @Override
    public int hashCode() {
      return run.hashCode();
    }
  }

  private class MyTransferHandler extends TransferHandler {
    private DataFlavor localObjectFlavor = new ActivationDataFlavor(DataRun.class,
                                                                    DataFlavor.javaJVMLocalObjectMimeType,
                                                                    "datarun");

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
      return true;
    }

    @Override
    public boolean importData(TransferSupport support) {
      try {
        DataRun run = (DataRun) support.getTransferable()
            .getTransferData(localObjectFlavor);
        if (run != null) {
          tableModel.updateData(run);
        }
      }
      catch (Throwable e) {
        log.error("", e);
      }
      return true;
    }
  }

}
