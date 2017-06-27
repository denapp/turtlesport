package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.lang.CommonLang;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.GeoUtil;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogRunPointsDetail extends JDialog {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogRunPointsDetail.class);
  }

  private JPanel              jPanelDistance;

  private JLabel              jLabelValDistanceTot;

  private JLabel              jLabelLibDistanceTot;

  private JLabel              jLabelLibTimeTot;

  private JLabel              jLabelValTimeTot;

  private JPanel              jContentPane;

  private JTableCustom        jTable;

  private JScrollPane         jPanelTable;

  private TableModelPoints    tableModel;

  private ResourceBundle      rb;

  private JLabel              jLabelTitle;

  private JButton             jButtonOK;

  private JPanel              jPanelButton;

  /**
   * @param owner
   * @param modal
   */
  public JDialogRunPointsDetail(Frame owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  public static void prompt(DataRun dataRun, List<DataRunTrk> listTrks) {

    // mis a jour du model et affichage de l'IHM
    JDialogRunPointsDetail view = new JDialogRunPointsDetail(MainGui.getWindow(),
                                                             true);

    // model
    String value = LanguageManager.getManager().getCurrentLang()
        .getDateFormatter().format(dataRun.getTime())
                   + "   "
                   + new SimpleDateFormat("kk:mm:ss").format(dataRun.getTime());

    view.getJLabelTitle().setText(value);

    try {
      // Distance tot
      view.getJLabelValDistanceTot()
          .setText(DistanceUnit.formatWithUnit(dataRun.getComputeDistanceTot()));

      // Temps tot
      view.getJLabelValTimeTot()
          .setText(TimeUnit.formatHundredSecondeTime(dataRun.computeTimeTot()));
    }
    catch (SQLException e) {
      log.error("", e);
    }
    view.tableModel.updateData(listTrks);

    // show
    view.pack();
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
  }

  public TableModelPoints getTableModel() {
    return tableModel;
  }

  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());
    setTitle(rb.getString("title"));

    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), CommonLang.class);
    this.setContentPane(getJContentPane());
    jLabelLibDistanceTot.setText(rb.getString("Distance") + " :");
    jLabelLibTimeTot.setText(rb.getString("Time") + " :");
    jButtonOK.setText(LanguageManager.getManager().getCurrentLang().ok());
    // this.setPreferredSize(new Dimension(850, 470));

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

      jLabelLibDistanceTot = new JLabel();
      jLabelLibDistanceTot.setFont(GuiFont.FONT_PLAIN);

      jLabelLibTimeTot = new JLabel();
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);

      jPanelDistance = new JPanel();
      jPanelDistance.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));

      jPanelDistance.add(getJLabelTitle(), null);
      jPanelDistance.add(new JLabel("  "), null);
      jPanelDistance.add(jLabelLibDistanceTot, null);
      jPanelDistance.add(getJLabelValDistanceTot(), null);
      jPanelDistance.add(jLabelLibTimeTot, null);
      jPanelDistance.add(getJLabelValTimeTot(), null);
    }
    return jPanelDistance;
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

  public JLabel getJLabelTitle() {
    if (jLabelTitle == null) {
      jLabelTitle = new JLabel();
      jLabelTitle.setFont(GuiFont.FONT_PLAIN);
    }
    return jLabelTitle;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  public JScrollPane getJPanelTable() {
    if (jPanelTable == null) {
      jPanelTable = new JScrollPane();
      jPanelTable.setViewportView(getJTable());
      jPanelTable.setFont(GuiFont.FONT_PLAIN);
      jPanelTable.setBounds(new Rectangle(10, 200, 500, 130));
    }
    return jPanelTable;
  }

  public JTable getJTable() {
    if (jTable == null) {
      jTable = new JTableCustom();
      tableModel = new TableModelPoints();
      jTable.setModel(tableModel);
      jTable.setFont(GuiFont.FONT_PLAIN);
      jTable.setShowGrid(false);
      jTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      jTable.setSortable(false);

      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      jTable.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTable.setSortable(true);
      jTable.packAll();
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
  public class TableModelPoints extends AbstractTableModel {

    private String[]        columnNames = { " ",
                                            "Temps",
                                            "Distance totale",
                                            "Distance",
                                            "bmp",
                                            "vitesse",
                                            "allure",
                                            "cadence",
                                            "temperature",
                                            "Longitude",
                                            "Latitude" };

    public List<DataRunTrk> listTrks;

    public TableModelPoints() {

      for (int i = 0; i < columnNames.length; i++) {
        switch (i) {
          case 1:
            columnNames[i] = rb.getString("Time");
            break;

          case 2:
            columnNames[i] = DistanceUnit.getDefaultUnit();
            break;

          case 3:
            columnNames[i] = DistanceUnit.getDefaultLowUnit();
            break;

          case 4:
            columnNames[i] = "<html><body><font color=\"red\">\u2665</font></body></html>";
            break;

          case 5:
            columnNames[i] = SpeedUnit.getDefaultUnit();
            break;

          case 6:
            columnNames[i] = PaceUnit.getDefaultUnit();
            break;

          case 7:
            columnNames[i] = rb.getString("Cadence");
            break;

          case 8:
            columnNames[i] = rb.getString("Temperature");
            break;

          case 9:
            columnNames[i] = rb.getString("Longitude");
            break;

          case 10:
            columnNames[i] = rb.getString("Latitude");
            break;

          default:
            columnNames[i] = "";
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
      return false;
    }

    /**
     * @param column
     * @return
     */
    // public int getPreferredWidth(int column) {
    // return columWidth[column] * 2;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int c) {
      return String.class;
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
      return (listTrks == null) ? 0 : listTrks.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     * int, int)
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {

      DataRunTrk data = listTrks.get(rowIndex);

      switch (columnIndex) {
        case 0:
          return rowIndex + 1;

        case 1: // Temps
          return TimeUnit.formatMilliSecondeTime(data.getTime().getTime()
                                                 - listTrks.get(0).getTime()
                                                     .getTime());

        case 2: // Distance Totale
          return DistanceUnit.formatMetersInKm(data.getDistance());

        case 3: // Distance
          if (rowIndex != 0) {
            return (int) (data.getDistance() - listTrks.get(rowIndex - 1)
                .getDistance());
          }
          return " ";

        case 4: // bmp
          return (data.getHeartRate() == 0) ? "-" : data.getHeartRate();

        case 5: // km/h
          if (rowIndex != 0) {
            if (DistanceUnit.isDefaultUnitKm()) {
              return SpeedUnit.format(data.getSpeed());
            }
            return SpeedUnit.format(DistanceUnit.convertKmToMile(data
                .getSpeed()));
          }
          return " ";

        case 6: // mn/km/h
          // if (data.getPace() == 0) {
          // return "-";
          // }
          if (rowIndex != 0) {
            if (DistanceUnit.isDefaultUnitKm()) {
              return PaceUnit.format(data.getSpeed());
            }
            return PaceUnit.convertMnperkmToMnpermile(data.getSpeed());
          }
          return " ";

        case 7: // Cadence
          return data.isValidCadence() ? data.getCadence() : "-";

        case 8: // Temperature.
          return data.isValidTemperature() ? data.getTemperature() : "-";

        case 9: // Longitude
          return data.isValidGps() ? GeoUtil.longititude(GeoUtil
              .makeLatitudeFromGarmin(data.getLongitude())) : "";

        case 10: // Latitude.
          return data.isValidGps() ? GeoUtil.latitude(GeoUtil
              .makeLatitudeFromGarmin(data.getLatitude())) : "";

        default:
          return "";
      }
    }

    /**
     * Mis a jour des donnees de la table.
     * 
     * @param runLaps
     */
    public void updateData(List<DataRunTrk> listTrks) {
      this.listTrks = listTrks;
      jTable.packAll();
    }
  }
}
