package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.ui.swing.model.ModelRunDetail;
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
public class JDialogRunDetail extends JDialog {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogRunDetail.class);
  }

  private JLabel               jLabelLibDistance;

  private JPanel               jPanelDistance;

  private JComboBox            jComboBoxDistance;

  private JPanel               jContentPane;

  private JLabel               jLabelLibUnit;

  private JTableCustom               jTable;

  private JScrollPane          jPanelTable;

  private TableModelDistance   tableModel;

  private ResourceBundle       rb;

  private JLabel               jLabelTitle;

  private DefaultComboBoxModel comboBoxModel;

  private JButton              jButtonOK;

  private JPanel               jPanelButton;

  private JLabel               jLabelValDistanceTot;

  private JLabel               jLabelLibDistanceTot;

  private JLabel               jLabelLibTimeTot;

  private JLabel               jLabelValTimeTot;

  /**
   * @param owner
   * @param modal
   */
  public JDialogRunDetail(Frame owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  public static void prompt(DataRun dataRun, List<DataRunTrk> listTrks) {

    // mis a jour du model et affichage de l'IHM
    ModelRunDetail model = new ModelRunDetail(dataRun, listTrks);
    JDialogRunDetail view = new JDialogRunDetail(MainGui.getWindow(), true);
    try {
      model.updateView(view);
      view.pack();
      view.setLocationRelativeTo(MainGui.getWindow());
      view.setVisible(true);
    }
    catch (SQLException e) {
      log.error("", e);
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), JDialogRunDetail.class);
      JShowMessage.error(rb.getString("errorSQL"));
    }

  }

  public TableModelDistance getTableModel() {
    return tableModel;
  }

  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    this.setContentPane(getJContentPane());
    jLabelLibDistance.setText(rb.getString("jLabelLibDistance"));
    jLabelLibDistanceTot.setText(rb.getString("jLabelLibDistanceTot"));
    jLabelLibTimeTot.setText(rb.getString("jLabelLibTimeTot"));
    jButtonOK.setText(LanguageManager.getManager().getCurrentLang().ok());
    this.setPreferredSize(new Dimension(696, 420));
    this.setTitle(rb.getString("title"));

    // Evenement
    getRootPane().setDefaultButton(jButtonOK);
    jButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout(5, 0));
      jContentPane.add(getJPanelDistance(), BorderLayout.NORTH);
      jContentPane.add(getJPanelTable(), BorderLayout.CENTER);
      jContentPane.add(getJPanelButton(), BorderLayout.SOUTH);

    }
    return jContentPane;
  }

  private JPanel getJPanelDistance() {
    if (jPanelDistance == null) {
      jLabelLibDistance = new JLabel();
      jLabelLibDistance.setFont(GuiFont.FONT_PLAIN);
      jLabelLibUnit = new JLabel(DistanceUnit.getDefaultUnit());
      jLabelLibUnit.setFont(GuiFont.FONT_PLAIN);

      jLabelLibDistanceTot = new JLabel();
      jLabelLibDistanceTot.setFont(GuiFont.FONT_PLAIN);

      jLabelLibTimeTot = new JLabel();
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);

      jPanelDistance = new JPanel();
      jPanelDistance.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));

      jPanelDistance.add(jLabelLibDistance, null);
      jPanelDistance.add(getJComboBoxDistance(), null);
      jPanelDistance.add(jLabelLibUnit, null);

      jPanelDistance.add(new JLabel("  "), null);
      jPanelDistance.add(getJLabelTitle(), null);
      jPanelDistance.add(new JLabel("  "), null);
      jPanelDistance.add(jLabelLibDistanceTot, null);
      jPanelDistance.add(getJLabelValDistanceTot(), null);
      jPanelDistance.add(jLabelLibTimeTot, null);
      jPanelDistance.add(getJLabelValTimeTot(), null);
    }
    return jPanelDistance;
  }

  public JLabel getJLabelTitle() {
    if (jLabelTitle == null) {
      jLabelTitle = new JLabel();
      jLabelTitle.setFont(GuiFont.FONT_PLAIN);
    }
    return jLabelTitle;
  }

  public JLabel getJLabelValDistanceTot() {
    if (jLabelValDistanceTot == null) {
      jLabelValDistanceTot = new JLabel();
      jLabelValDistanceTot.setFont(GuiFont.FONT_PLAIN);
    }
    return jLabelValDistanceTot;
  }

  public JLabel getJLabelValTimeTot() {
    if (jLabelValTimeTot == null) {
      jLabelValTimeTot = new JLabel();
      jLabelValTimeTot.setFont(GuiFont.FONT_PLAIN);
    }
    return jLabelValTimeTot;
  }

  private JComboBox getJComboBoxDistance() {
    if (jComboBoxDistance == null) {
      jComboBoxDistance = new JComboBox();
      jComboBoxDistance.setFont(GuiFont.FONT_PLAIN);
      comboBoxModel = new DefaultComboBoxModel();
      jComboBoxDistance.setModel(comboBoxModel);
      jComboBoxDistance.addActionListener(new DistanceComboActionListener());
    }
    return jComboBoxDistance;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  public JScrollPane getJPanelTable() {
    if (jPanelTable == null) {
      jPanelTable = new JScrollPane();
      jPanelTable.setViewportView(getJTableLap());
      jPanelTable.setFont(GuiFont.FONT_PLAIN);
      jPanelTable.setBounds(new Rectangle(10, 200, 685, 130));
    }
    return jPanelTable;
  }

  /**
   * This method initializes jTableLap
   * 
   * @return javax.swing.JTable
   */
  private JTable getJTableLap() {
    if (jTable == null) {
      jTable = new JTableCustom();
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
      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      jTable.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTable.getTableHeader().resizeAndRepaint();
      jTable.setRowSelectionAllowed(true);
    }
    return jTable;
  }

  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      jPanelButton = new JPanel();
      jPanelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelButton.add(getJButtonOK(), null);
    }
    return jPanelButton;
  }

  private JButton getJButtonOK() {
    if (jButtonOK == null) {
      jButtonOK = new JButton();
      jButtonOK.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonOK;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TableModelDistance extends AbstractTableModel {

    private String[]             columnNames = { "Distance (km)",
                                                 "Temps total",
                                                 "Temps",
                                                 "Allure Moy. (mn/km)",
                                                 "Vitesse Moy. (km/h)",
                                                 "moy.",
                                                 "max." };

    private final int[]          columWidth  = { 50, 50, 30, 70, 70, 35, 30 };

    private List<DataRunTrk>     trks;

    private ArrayList<TableData> listData;

    public TableModelDistance() {

      for (int i = 0; i < columnNames.length; i++) {
        switch (i) {
          case 0:
            // Distance
            initColumn(DistanceUnit.getDefaultUnit(), i);
            break;

          case 3:
            // Allure
            initColumn(PaceUnit.getDefaultUnit(), i);
            break;

          case 4:
            // Vitesse moyenne
            initColumn(SpeedUnit.getDefaultUnit(), i);
            break;

          default:
            columnNames[i] = rb.getString("TableModel_header" + i);
        }
      }

    }

    private void initColumn(String unit, int index) {
      columnNames[index] = MessageFormat.format(rb
          .getString("TableModel_header" + index), unit);
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
      return (listData == null) ? 0 : listData.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {

      TableData data = listData.get(rowIndex);
      log.debug("rowIndex=" + rowIndex + " : " + data.toString());

      switch (columnIndex) {
        case 0: // Distance
          return DistanceUnit.formatMetersInKm(data.getBeginDistance());

        case 1: // Temps cumule
          long time = 0;
          if (rowIndex != 0) {
            // time = listData.get(rowIndex - 1).getTimeEnd().getTime()
            // - listData.get(0).getTimeBegin().getTime();
            time = listData.get(rowIndex).getTimeBegin().getTime()
                   - listData.get(0).getTimeBegin().getTime();
          }
          return TimeUnit.formatHundredSecondeTime(time / 10);

        case 2: // Temps
          return TimeUnit.formatHundredSecondeTime(data.getTotalTime());

        case 3: // Allure Moy.
          return PaceUnit.computeAllure(data.getTotalDist(),
                                        data.getTotalTime());

        case 4: // Vitesse Moy.
          return SpeedPaceUnit.computeFormatSpeed(data.getTotalDist(),
                                                  data.getTotalTime());

        case 5: // Frequence cardiaque moy.
          return data.getAvgHeartRate();

        case 6: // Frequence cardiaque max.
          return data.getMaxHeartRate();

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
    public void updateData(List<DataRunTrk> listTrks) {
      this.trks = listTrks;
      if (listTrks != null && listTrks.size() > 0) {

        listData = new ArrayList<TableData>();

        // recuperation de la distance totale
        float distance = listTrks.get(listTrks.size() - 1).getDistance() / 1000;

        // mis a jour des valeurs miles
        if (!DistanceUnit.isDefaultUnitKm()) {
          int size = listTrks.size();
          for (int i = 0; i < size; i++) {
            listTrks.get(i)
                .setDistance((float) (listTrks.get(i).getDistance() / 1.609));
          }
        }

        // mis a jour liste de la combo
        int[] valCombo = { 1, 2, 5, 10, 20, 25, 50, 100 };
        comboBoxModel.addElement(new Integer(valCombo[0]));
        for (int i = 1; i < valCombo.length; i++) {
          if (distance > valCombo[i] * 2) {
            comboBoxModel.addElement(new Integer(valCombo[i]));
          }
          else {
            break;
          }
        }

      }
    }

    protected void updateData() {
      if (trks == null) {
        return;
      }

      getTableModel().clear();

      int dist = (Integer) jComboBoxDistance.getSelectedItem();
      dist *= 1000;
      int currentDist = dist;

      TableData data = new TableData(trks.get(0).getTime(), trks.get(0)
          .getDistance());
      listData.add(data);

      for (int i = 0; i < trks.size(); i++) {
        if (trks.get(i).getDistance() <= currentDist) {
          data.setTimeEnd(trks.get(i).getTime());
          data.setEndDistance(trks.get(i).getDistance());
          data.addHeartRate(trks.get(i).getHeartRate());
        }
        else {
          currentDist += dist;
          if ((i + 1) < trks.size()) {
            data = new TableData(trks.get(i - 1).getTime(), trks.get(i - 1)
                .getDistance());
            data.addHeartRate(trks.get(i - 1).getHeartRate());
            listData.add(data);
          }
        }
      }
      fireTableDataChanged();
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TableData {
    private float     beginDistance;

    private Timestamp timeBegin;

    private Timestamp timeEnd;

    private float     endDistance;

    private int       heartRate    = 0;

    private int       nbPoints     = 0;

    private int       maxHeartRate = 0;

    public TableData(Timestamp timeBegin, float beginDistance) {
      this.timeBegin = timeBegin;
      this.beginDistance = beginDistance;
      this.timeEnd = timeBegin;
      this.endDistance = beginDistance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "nbPoints=" + nbPoints + " timeBegin=" + timeBegin + " TotalTime="
             + getTotalTime() + " TotalDist=" + getTotalDist();
    }

    public void setEndDistance(float endDistance) {
      this.endDistance = endDistance;
    }

    public Timestamp getTimeBegin() {
      return timeBegin;
    }

    public Timestamp getTimeEnd() {
      return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
      this.timeEnd = timeEnd;
    }

    public void addHeartRate(int val) {
      if (val > maxHeartRate) {
        maxHeartRate = val;
      }
      heartRate += val;
      nbPoints++;
    }

    public int getMaxHeartRate() {
      return maxHeartRate;
    }

    public int getAvgHeartRate() {
      return heartRate / nbPoints;
    }

    public long getTotalTime() {
      return (timeEnd.getTime() - timeBegin.getTime()) / 10;
    }

    public float getBeginDistance() {
      return beginDistance;
    }

    public void setBeginDistance(float beginDistance) {
      this.beginDistance = beginDistance;
    }

    public double getTotalDist() {
      double totalDist = endDistance - beginDistance;
      return totalDist;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DistanceComboActionListener implements ActionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      getTableModel().updateData();
    }

  }

}
