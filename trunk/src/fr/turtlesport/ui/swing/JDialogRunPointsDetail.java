package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.model.ModelRunPointsDetail;
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
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogRunPointsDetail.class);
  }

  private JButton                     jButtonCancel;

  private JButton                     jButtonDelete;

  private JPanel                      jPanelDistance;

  private JLabel                      jLabelValDistanceTot;

  private JLabel                      jLabelLibDistanceTot;

  private JLabel                      jLabelLibTimeTot;

  private JLabel                      jLabelValTimeTot;

  private JPanel                      jContentPane;

  private JTableCustom                jTable;

  private JScrollPane                 jPanelTable;

  private TableModelPoints            tableModel;

  private ResourceBundle              rb;

  private JLabel                      jLabelTitle;

  private JButton                     jButtonSave;

  private JButton                     jButtonSelect;

  private JButton                     jButtonUnselect;

  private JPanel                      jPanelButton;

  private JTableListSelectionListener jTableListSelectionListener;

  private ModelRunPointsDetail        model;

  /**
   * @param owner
   * @param modal
   */
  public JDialogRunPointsDetail(Frame owner,
                                boolean modal,
                                ModelRunPointsDetail model) {
    super(owner, modal);
    initialize();
    this.model = model;
  }

  public static void prompt(DataRun dataRun, List<DataRunTrk> listTrks) {

    // mis a jour du model et affichage de l'IHM
    ModelRunPointsDetail model = new ModelRunPointsDetail(dataRun, listTrks);
    JDialogRunPointsDetail view = new JDialogRunPointsDetail(MainGui.getWindow(),
                                                             true,
                                                             model);
    try {
      model.updateView(view);
    }
    catch (SQLException e) {
      log.error("", e);
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), JDialogRunPointsDetail.class);
      JShowMessage.error(rb.getString("errorSQL"));
    }
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

    this.setContentPane(getJContentPane());
    jLabelLibDistanceTot.setText(rb.getString("jLabelLibDistanceTot"));
    jLabelLibTimeTot.setText(rb.getString("jLabelLibTimeTot"));
    jButtonSave.setText(rb.getString("jButtonSave"));
    jButtonCancel.setText(LanguageManager.getManager().getCurrentLang()
        .cancel());
    jButtonDelete.setText(rb.getString("jButtonDelete"));
    jButtonSelect.setText(rb.getString("jButtonSelect"));
    jButtonUnselect.setText(rb.getString("jButtonUnselect"));

    this.setPreferredSize(new Dimension(795, 470));

    // Evenement
    getRootPane().setDefaultButton(jButtonCancel);
    jButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    jButtonSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          model.savePoints(JDialogRunPointsDetail.this);
        }
        catch (SQLException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });
    jButtonDelete.addActionListener(new DeleteActionListener());
    jButtonSelect.addActionListener(new SelectActionListener(true));
    jButtonUnselect.addActionListener(new SelectActionListener(false));

    jTableListSelectionListener = new JTableListSelectionListener();
    jTable.getSelectionModel()
        .addListSelectionListener(jTableListSelectionListener);
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

  public JTableCustom getJTable() {
    if (jTable == null) {
      jTable = new JTableCustom();
      tableModel = new TableModelPoints();
      jTable.setModel(tableModel);
      jTable.setFont(GuiFont.FONT_PLAIN);
      jTable.setShowGrid(false);
      jTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      jTable.setSortable(false);

      // header corbeille
      TableCellRenderer iconHeaderRenderer = new DefaultTableCellRenderer() {
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
          // Inherit the colors and font from the header component
          if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
              setForeground(header.getForeground());
              setBackground(header.getBackground());
              setFont(header.getFont());
            }
          }

          if (value instanceof HeaderIcon) {
            setIcon(((HeaderIcon) value).icon);
          }
          else {
            setText((value == null) ? "" : value.toString());
            setIcon(null);
          }
          setBorder(UIManager.getBorder("TableHeader.cellBorder"));
          setHorizontalAlignment(JLabel.CENTER);
          return this;
        }
      };

      HeaderIcon hi = new HeaderIcon(ImagesRepository.getImageIcon("trash.png"));
      TableColumnModel columnModel = jTable.getTableHeader().getColumnModel();
      columnModel.getColumn(0).setHeaderRenderer(iconHeaderRenderer);
      jTable.getColumnModel().getColumn(0).setHeaderValue(hi);

      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
      jPanelButton.add(getJButtonDelete(), null);
      jPanelButton.add(getJButtonUnselect(), null);
      jPanelButton.add(getJButtonSelect(), null);
      jPanelButton.add(getJButtonSave(), null);
      jPanelButton.add(getJButtonCancel(), null);
    }
    return jPanelButton;
  }

  /**
   * This method initializes jButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonSelect() {
    if (jButtonSelect == null) {
      jButtonSelect = new JButton();
      jButtonSelect.setFont(GuiFont.FONT_PLAIN);
      jButtonSelect.setText(rb.getString("jButtonSelect"));
    }
    return jButtonSelect;
  }

  /**
   * This method initializes jButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonUnselect() {
    if (jButtonUnselect == null) {
      jButtonUnselect = new JButton();
      jButtonUnselect.setFont(GuiFont.FONT_PLAIN);
      jButtonUnselect.setText(rb.getString("jButtonUnselect"));
    }
    return jButtonUnselect;
  }

  private JButton getJButtonSave() {
    if (jButtonSave == null) {
      jButtonSave = new JButton();
      jButtonSave.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonSave;
  }

  private JButton getJButtonCancel() {
    if (jButtonCancel == null) {
      jButtonCancel = new JButton();
      jButtonCancel.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonCancel;
  }

  private JButton getJButtonDelete() {
    if (jButtonDelete == null) {
      jButtonDelete = new JButton();
      jButtonDelete.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonDelete;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TableModelPoints extends AbstractTableModel {

    private String[]            columnNames = { " ",
                                                " ",
                                                "Temps",
                                                "Distance totale",
                                                "Distance",
                                                "bmp",
                                                "vitesse",
                                                "allure",
                                                "Longitude",
                                                "Latitude" };

    private final int[]         columWidth  = { 20,
                                                30,
                                                40,
                                                40,
                                                20,
                                                30,
                                                40,
                                                40,
                                                60,
                                                60 };

    public List<TableRowObject> listRowObject;

    public TableModelPoints() {

      for (int i = 0; i < columnNames.length; i++) {
        switch (i) {
          case 3:
            columnNames[i] = DistanceUnit.getDefaultUnit();
            break;

          case 4:
            columnNames[i] = DistanceUnit.getDefaultLowUnit();
            break;

          case 5:
            columnNames[i] = "<html><body><font color=\"red\">\u2665</font></body></html>";
            break;

          case 6:
            columnNames[i] = SpeedUnit.getDefaultUnit();
            break;

          case 7:
            columnNames[i] = PaceUnit.getDefaultUnit();
            break;

          case 2:
          case 8:
          case 9:
            columnNames[i] = rb.getString("TableModel_header" + i);
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
      return columnIndex == 0;
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
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int c) {
      return (c == 0) ? Boolean.class : String.class;
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
      return (listRowObject == null) ? 0 : listRowObject.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     * int, int)
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
      if (columnIndex == 0) {
        listRowObject.get(rowIndex).isDel = (Boolean) value;
        fireTableCellUpdated(jTable.convertRowIndexToView(rowIndex),
                             columnIndex);
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {

      DataRunTrk data = listRowObject.get(rowIndex).trk;

      switch (columnIndex) {
        case 0:
          return listRowObject.get(rowIndex).isDel;

        case 1:
          return rowIndex + 1;

        case 2: // Temps
          return TimeUnit.formatMilliSecondeTime(data.getTime().getTime()
                                                 - listRowObject.get(0).trk
                                                     .getTime().getTime());

        case 3: // Distance Totale
          return DistanceUnit.formatMetersInKm(data.getDistance());

        case 4: // Distance
          if (rowIndex != 0) {
            return (int) (data.getDistance() - listRowObject.get(rowIndex - 1).trk
                .getDistance());
          }
          return " ";

        case 5: // bmp
          return (data.getHeartRate() == 0) ? "-" : data.getHeartRate();

        case 6: // km/h
          if (rowIndex != 0) {
            if (DistanceUnit.isDefaultUnitKm()) {
              return SpeedUnit.format(data.getSpeed());
            }
            return SpeedUnit.format(DistanceUnit.convertKmToMile(data
                .getSpeed()));
          }
          return " ";

        case 7: // mn/km/h
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

        case 8: // Longitude
          return data.isValidGps() ? GeoUtil.longititude(GeoUtil
              .makeLatitudeFromGarmin(data.getLongitude())) : "";

        case 9: // Latitude.
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
      listRowObject = new ArrayList<JDialogRunPointsDetail.TableRowObject>();
      if (listTrks != null) {
        for (DataRunTrk t : listTrks) {
          listRowObject.add(new TableRowObject(t));
        }
      }
    }
  }

  class HeaderIcon {
    HeaderIcon(Icon icon) {
      this.icon = icon;
    }

    Icon icon;
  }

  public class TableRowObject {
    public DataRunTrk trk;

    public boolean    isDel = false;

    public TableRowObject(DataRunTrk trk) {
      this.trk = trk;
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
      ListSelectionModel lsm = (ListSelectionModel) e.getSource();
      int minIndex = lsm.getMinSelectionIndex();
      int maxIndex = lsm.getMaxSelectionIndex();
      if (minIndex >= 0 && maxIndex > minIndex) {
        TableModelPoints model = (TableModelPoints) jTable.getModel();
        for (int i = minIndex; i <= maxIndex; i++) {
          model.listRowObject.get(i).isDel = true;
        }
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class SelectActionListener implements ActionListener {
    private boolean isSelect;

    public SelectActionListener(boolean isSelect) {
      this.isSelect = isSelect;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {
      for (TableRowObject row : tableModel.listRowObject) {
        row.isDel = isSelect;
      }
      tableModel.fireTableDataChanged();
    }
  }

  private class DeleteActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int originalSize = tableModel.listRowObject.size();
      Iterator<TableRowObject> it = tableModel.listRowObject.iterator();
      while (it.hasNext()) {
        if (it.next().isDel) {
          it.remove();
        }
      }

      if (originalSize != tableModel.listRowObject.size()) {
        getJTable().tableChanged(new TableModelEvent(tableModel));
        model.deletePoints(JDialogRunPointsDetail.this);
      }
    }
  }
}
