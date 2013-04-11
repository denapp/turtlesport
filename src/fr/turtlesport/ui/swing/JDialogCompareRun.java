package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.xml.DOMConfigurator;

import fr.turtlesport.Configuration;
import fr.turtlesport.Launcher;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.DatabaseManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogCompareRun extends JDialog {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogCompareRun.class);
  }

  private JComboBox           jComboxBoxFirstRun;

  private JComboBox           jComboxBoxSecondRun;

  private JPanel              jPanelRun;

  private JButton             jButtonCompare;

  private JPanel              jPanelCenter;

  private JScrollPane         jScrollPaneLap;

  private JTableCustom        jTable;

  private TableModelDistance  tableModel;

  public JDialogCompareRun() {
    super();
    initialize();
  }

  public JDialogCompareRun(Frame owner) {
    super(owner, true);
    initialize();
  }

  private void initialize() {
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(getJPanelRun(), BorderLayout.NORTH);
    setContentPane(contentPane);

    jPanelCenter = new JPanel();
    contentPane.add(jPanelCenter, BorderLayout.CENTER);
    jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.Y_AXIS));

    jScrollPaneLap = new JScrollPane();
    jScrollPaneLap.setFont(GuiFont.FONT_PLAIN);
    jScrollPaneLap.setViewportView(getJTable());
    jPanelCenter.add(jScrollPaneLap);

    jPanelCenter.add(jScrollPaneLap);
    this.setSize(880, 700);

    // Evenement
    jButtonCompare.addActionListener(new CompareActionListener());
  }

  public JTableCustom getJTable() {
    if (jTable == null) {
      jTable = new JTableCustom();
      tableModel = new TableModelDistance();
      jTable.setModel(tableModel);
      jTable.setFont(GuiFont.FONT_PLAIN);
      jTable.setShowGrid(false);
      jTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      jTable.setSortable(false);

      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      jTable.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTable.setSortable(true);
      jTable.packAll();
    }
    return jTable;
  }

  private JPanel getJPanelRun() {
    if (jPanelRun == null) {
      jPanelRun = new JPanel();
      jPanelRun.setLayout(new BoxLayout(jPanelRun, BoxLayout.X_AXIS));

      jComboxBoxFirstRun = new JComboBox();
      jComboxBoxFirstRun.setFont(GuiFont.FONT_PLAIN);

      jComboxBoxSecondRun = new JComboBox();
      jComboxBoxSecondRun.setFont(GuiFont.FONT_PLAIN);

      List<DataRun> list = null;
      try {
        list = RunTableManager.getInstance().retreive(-1);
      }
      catch (SQLException e) {
        e.printStackTrace();
      }

      MyDefautlCellRenderer renderer = new MyDefautlCellRenderer();
      jComboxBoxFirstRun.setRenderer(renderer);
      jComboxBoxSecondRun.setRenderer(renderer);

      jComboxBoxSecondRun.setModel(new MyDefaultComboBoxModel(list));
      jComboxBoxFirstRun.setModel(new MyDefaultComboBoxModel(list));

      jPanelRun.add(jComboxBoxFirstRun);
      jPanelRun.add(jComboxBoxSecondRun);

      jButtonCompare = new JButton("Comparer");
      jButtonCompare.setFont(GuiFont.FONT_PLAIN);
      jPanelRun.add(jButtonCompare);

    }
    return jPanelRun;
  }

  private class MyDefautlCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

      JLabel cmp = (JLabel) super.getListCellRendererComponent(list,
                                                               value,
                                                               index,
                                                               isSelected,
                                                               cellHasFocus);

      if (value != null && value instanceof DataRun) {
        DataRun dataRun = (DataRun) value;

        String text = LanguageManager.getManager().getCurrentLang()
            .getDateFormatter().format(dataRun.getTime())
                      + " "
                      + new SimpleDateFormat("kk:mm:ss").format(dataRun
                          .getTime()) + " ";
        try {
          text += DistanceUnit.formatWithUnit(dataRun.getComputeDistanceTot());
        }
        catch (SQLException e) {
        }

        cmp.setText(text);
      }

      return cmp;
    }
  }

  private class MyDefaultComboBoxModel extends DefaultComboBoxModel {
    private List<DataRun> list;

    public MyDefaultComboBoxModel(List<DataRun> list) {
      this.list = list;
      System.out.println(list.size());
    }

    @Override
    public Object getElementAt(int index) {
      return list.get(index);
    }

    @Override
    public int getSize() {
      return list.size();
    }
  }

  private class CompareActionListener implements ActionListener {

    public void actionPerformed(ActionEvent action) {
      try {
        DataRun run1 = (DataRun) jComboxBoxFirstRun.getSelectedItem();
        List<DataRunTrk> trks1;
        trks1 = RunTrkTableManager.getInstance().getValidTrks(run1.getId());

        DataRun run2 = (DataRun) jComboxBoxSecondRun.getSelectedItem();
        List<DataRunTrk> trks2 = RunTrkTableManager.getInstance()
            .getValidTrks(run2.getId());

        tableModel.updateData(trks1, trks2);
      }
      catch (SQLException e) {
        e.printStackTrace();
      }

    }

  }

  private class TableData {
    float distanceBegin;

    long  time1 = -1;

    long  time2 = -1;

    float distance1;

    float distance2;

    public TableData(float distanceBegin) {
      this.distanceBegin = distanceBegin;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TableModelDistance extends AbstractTableModel {

    private String[]         columnNames = { "Distance (km)", "Temps", "Temps" };

    private final int[]      columWidth  = { 70, 70, 70 };

    private List<TableData>  listData    = new ArrayList<TableData>();

    private List<DataRunTrk> listTrks1;

    private List<DataRunTrk> listTrks2;

    public TableModelDistance() {

      // for (int i = 0; i < columnNames.length; i++) {
      // switch (i) {
      // case 1:
      // case 2:
      // case 3:
      // // Distance
      // initColumn(DistanceUnit.getDefaultUnit(), i);
      // break;
      //
      // default:
      // columnNames[i] = rb.getString("TableModel_header" + i);
      // }
      // }

    }

    private void initColumn(String unit, int index) {
      // columnNames[index] = MessageFormat.format(rb
      // .getString("TableModel_header" + index), unit);
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
      return listData.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      TableData data = listData.get(rowIndex);
      if (log.isDebugEnabled()) {
        log.debug("rowIndex=" + rowIndex + " columnIndex=" + columnIndex + " "
                  + data.toString());
      }

      switch (columnIndex) {
        case 0: // Distance 1
          return DistanceUnit
              .formatMetersInKm(listData.get(rowIndex).distanceBegin);

        case 1: // Temps 1
          if (listData.get(rowIndex).time1 == -1) {
            return "-";
          }
          return TimeUnit.formatMilliSecondeTime(listData.get(rowIndex).time1);

        case 2: // Temps 2
          if (listData.get(rowIndex).time2 == -1) {
            return "-";
          }
          return TimeUnit.formatMilliSecondeTime(listData.get(rowIndex).time2);

        default:
          return "";
      }
    }

    /**
     * Efface les donnees de la table.
     * 
     * @param runLaps
     */
    public void clear() {
      if (listData != null) {
        listData.clear();
      }
    }

    /**
     * Mis a jour des donnees de la table.
     * 
     * @param runLaps
     */
    public void updateData(List<DataRunTrk> listTrks1,
                           List<DataRunTrk> listTrks2) {
      // recuperation de la distance totale
      this.listTrks1 = listTrks1;
      this.listTrks2 = listTrks2;

      // mis a jour des valeurs miles
      if (!DistanceUnit.isDefaultUnitKm()) {
        int size = listTrks1.size();
        for (int i = 0; i < size; i++) {
          listTrks1.get(i)
              .setDistance((float) (listTrks1.get(i).getDistance() / 1.609));
        }
        size = listTrks2.size();
        for (int i = 0; i < size; i++) {
          listTrks2.get(i)
              .setDistance((float) (listTrks2.get(i).getDistance() / 1.609));
        }
      }
      updateData();
      fireTableDataChanged();
    }

    protected void updateData() {
      clear();

      HashMap<Integer, TableData> map = new HashMap<Integer, JDialogCompareRun.TableData>();
      computeTrks(map, listTrks1, 1000, true);
      computeTrks(map, listTrks2, 1000, false);

      fireTableDataChanged();
    }

    private void computeTrks(HashMap<Integer, TableData> map,
                             List<DataRunTrk> listTrks,
                             int dist,
                             boolean isFirstTrks) {
      int currentDist = dist;

      int size = listTrks.size() - 1;
      TableData data = map.get(currentDist);
      if (data == null) {
        System.out.println("new data " + currentDist);
        data = new TableData(currentDist);
        map.put(new Integer(currentDist), data);
        listData.add(data);
      }
      for (int i = 0; i < size; i++) {
        if (listTrks.get(i).getDistance() <= currentDist) {
          setDist(data, listTrks.get(i).getDistance(), isFirstTrks);
          setTime(data, listTrks.get(i).getTime().getTime()
                        - listTrks.get(0).getTime().getTime(), isFirstTrks);
        }
        else {
          float distance = getDist(data, isFirstTrks);
          if (distance != currentDist) {
            float distDiff = listTrks.get(i).getDistance() - distance;
            long time = (listTrks.get(i).getTime().getTime() - listTrks.get(0)
                .getTime().getTime())
                        - getTime(data, isFirstTrks);
            double timeDiff = (currentDist - distance) * time / distDiff;

            setDist(data, currentDist, isFirstTrks);
            addTime(data, (long) timeDiff, isFirstTrks);
            // System.out.println(" distDiff =" + distDiff + " time=" + time
            // + " getTime=" + getTime(data, isFirstTrks)
            // + " timeDiff=" + timeDiff);
          }

          currentDist += dist;
          if ((i + 1) < listTrks.size()) {
            data = map.get(currentDist);
            if (data == null) {
              data = new TableData(currentDist);
              map.put(new Integer(currentDist), data);
              System.out.println("new data " + currentDist);

            }
            setDist(data, listTrks.get(i + 1).getDistance(), isFirstTrks);
            setTime(data, listTrks.get(i + 1).getTime().getTime()
                          - listTrks.get(0).getTime().getTime(), isFirstTrks);
            listData.add(data);
          }
        }
      }
    }

    private void setDist(TableData data, float dist, boolean isFirst) {
      if (isFirst) {
        data.distance1 = dist;
      }
      else {
        data.distance2 = dist;
      }
    }

    private float getDist(TableData data, boolean isFirst) {
      return (isFirst) ? data.distance1 : data.distance2;
    }

    private long getTime(TableData data, boolean isFirst) {
      return (isFirst) ? data.time1 : data.time2;
    }

    private void setTime(TableData data, long time, boolean isFirst) {
      if (isFirst) {
        data.time1 = time;
      }
      else {
        data.time2 = time;
      }
    }

    private void addTime(TableData data, long time, boolean isFirst) {
      if (isFirst) {
        data.time1 += time;
      }
      else {
        data.time2 += time;
      }
    }

    private long elapsed(List<DataRunTrk> trks, int index) {
      return trks.get(index).getTime().getTime()
             - trks.get(0).getTime().getTime();
    }
  }

  public static void main(String[] args) {
    try {
      Location.initialize();
      // positionne les traces
      String dirExe = Location.dirNameExecution(Launcher.class);
      File file = new File(dirExe, "log4J.xml");
      DOMConfigurator.configure(file.toURI().toURL());

      Configuration.initialize();

      DatabaseManager.initDatabase(false);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JDialogCompareRun dlg = new JDialogCompareRun();
    dlg.setVisible(true);
  }
}
