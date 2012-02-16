package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.DataEquipement;
import fr.turtlesport.db.DataRunExtra;
import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.db.UserTableManager;
import fr.turtlesport.db.progress.IRunStoreProgress;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.A1000RunTransferProtocol;
import fr.turtlesport.protocol.data.AbstractLapType;
import fr.turtlesport.protocol.data.AbstractRunType;
import fr.turtlesport.protocol.progress.IRunTransfertProgress;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.ui.swing.component.JTextFieldLength;
import fr.turtlesport.ui.swing.component.jtable.ComboBoxCellRenderer;
import fr.turtlesport.ui.swing.component.jtable.ProgressBarCellRenderer;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogProgressRun extends JDialog implements
                                               IRunTransfertProgress,
                                               IRunStoreProgress {
  private static TurtleLogger      log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogProgressRun.class);
  }

  private JPanel                   jContentPane;

  private JScrollPane              jScrollPaneTrack;

  private JTableCustom             jTableRun;

  private JPanel                   jPanelButton;

  private JButton                  jButtonCancel;

  private JButton                  jButtonSave;

  private JButton                  jButtonSelect;

  private JButton                  jButtonUnselect;

  private JPanel                   jPanelSouth;

  private JPanel                   jPanelStatus;

  private JProgressBar             jProgressBar;

  private JLabel                   jLabelNorth;

  private JLabel                   jLabelProgress;

  // model
  private TableModelRun            tableModelRun;

  private A1000RunTransferProtocol a1000;

  private SaveActionListener       saveActionListener;

  private CancelActionListener     cancelActionListener;

  // Athlete
  private User[]                   users;

  private User                     defaultUser;

  // Equipements
  private String[]                 equipements;

  private String                   defaultEquipement;

  // Activites
  private AbstractDataActivity[]   activities;

  private AbstractDataActivity     defaultActivity;

  private boolean                  isEndTransfert   = false;

  private ResourceBundle           rb;

  private int                      pointNotify      = 15;

  private boolean                  isAbortTransfert = false;

  /**
   * @param owner
   * @param modal
   * @throws SQLException
   */
  public JDialogProgressRun(Frame owner, boolean modal) throws SQLException {
    super(owner, modal);

    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    // Utilisateurs
    List<DataUser> listDataUser = UserTableManager.getInstance().retreive();
    users = new User[listDataUser.size()];
    for (int i = 0; i < listDataUser.size(); i++) {
      users[i] = new User(listDataUser.get(i));
      if (listDataUser.get(i).getId() == MainGui.getWindow().getCurrentIdUser()) {
        defaultUser = users[i];
      }
    }
    if (defaultUser == null && users.length > 0) {
      defaultUser = users[0];
    }

    // Equipements
    List<DataEquipement> listEquipement = EquipementTableManager.getInstance()
        .retreive();
    equipements = new String[listEquipement.size()];
    if (listEquipement.size() > 0) {
      for (int i = 0; i < listEquipement.size(); i++) {
        if (listEquipement.get(i).isDefault()) {
          defaultEquipement = listEquipement.get(i).getName();
        }
        equipements[i] = listEquipement.get(i).getName();
      }
    }

    // Activites
    List<AbstractDataActivity> listActivities = UserActivityTableManager
        .getInstance().retreive();
    activities = new AbstractDataActivity[listActivities.size()];
    if (listActivities.size() > 0) {
      listActivities.toArray(activities);
      Arrays.sort(activities);
    }
    for (AbstractDataActivity d : listActivities) {
      if (d.isDefaultActivity()) {
        defaultActivity = d;
      }
    }

    tableModelRun = new TableModelRun();
    initialize();
  }

  /**
   * Recuperation des run.
   * 
   * @param a1000
   */
  public void retreive(A1000RunTransferProtocol a1000) {

    long deb;
    try {
      this.a1000 = a1000;
      // Recuperation des run.
      deb = System.currentTimeMillis();
      a1000.retrieve(this);
      log.warn("Temps pour recuperer les run (ms) --> "
               + (System.currentTimeMillis() - deb));
    }
    catch (Throwable th) {
      log.error("", th);
      dispose();
      JShowMessage.error(th.getMessage());
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Window#dispose()
   */
  @Override
  public void dispose() {
    super.dispose();
    a1000 = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfert(int)
   */
  public void beginTransfert(int nbPacket) {
    pointNotify = 3;
    jLabelProgress.setText(rb.getString("jLabelProgressBeginTransfer"));
    jProgressBar.setStringPainted(true);
    jProgressBar.setMaximum(nbPacket);
    jProgressBar.setValue(0);
    jProgressBar.setIndeterminate(false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#stopTransfert()
   */
  public boolean abortTransfert() {
    return isAbortTransfert;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#transfertRun()
   */
  public void transfert() {
    jProgressBar.setValue(jProgressBar.getValue() + intervalNotify());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#endTransfert()
   */
  public void endTransfert() {
    if (tableModelRun.getRowCount() > 0) {
      jButtonSave.setEnabled(true);
      jButtonSelect.setEnabled(true);
      jButtonUnselect.setEnabled(true);
    }

    for (TableRowObject ro : tableModelRun.listRun) {
      ro.endProgress();
    }
    if (tableModelRun.getRowCount() > 1) {
      jTableRun.packAll();
    }
    jButtonCancel.setEnabled(true);
    jLabelProgress.setText(rb.getString("jLabelProgressEndTransfer"));
    jProgressBar.setIndeterminate(false);
    jProgressBar.setValue(jProgressBar.getMaximum());

    isEndTransfert = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfertLap
   * (int)
   */
  public void beginTransfertLap(int nbPacket) {
    jProgressBar.setMaximum(nbPacket);
    jProgressBar.setValue(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfertCourse
   * (fr.turtlesport.protocol.data.AbstractRunType)
   */
  public void beginTransfertCourse(AbstractRunType rt) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.IRunTransfertProgress#endTransfertCourse
   * (fr.turtlesport.protocol.data.D1009RunType)
   */
  public void endTransfertCourse(AbstractRunType rt) {
    TableRowObject row = tableModelRun.getTableRowObject(rt);
    if (row != null) {
      float dist = rt.getComputeDistance();
      if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
        row.setDistance(DistanceUnit.convert(DistanceUnit.getDefaultUnit(),
                                             DistanceUnit.unitKm(),
                                             dist));
      }
      else {
        row.setDistance(dist);
      }
      row.setSave(true);
      row.endProgress();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.IRunTransfertProgress#transfertLap(fr.
   * turtlesport.protocol.data.D1009RunType,
   * fr.turtlesport.protocol.data.AbstractLapType)
   */
  public void transfertLap(AbstractRunType runType, AbstractLapType lapType) {
    if (runType.sizeLapType() == 1) {
      try {
        if (RunTableManager.getInstance().find(DataUser.getAllUser().getId(),
                                               runType.getComputeStartTime()) == -1) {
          tableModelRun.addTransfertCourse(runType);
        }
      }
      catch (SQLException e) {
        log.error("", e);
      }
    }
    TableRowObject row = tableModelRun.getTableRowObject(runType);
    if (row != null) {
      row.setDate(lapType.getStartTime());
      row.addProgressValue();
      if (tableModelRun.getRowCount() == 1) {
        jTableRun.packAll();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.IRunTransfertProgress#transfertPoint(fr
   * .turtlesport.protocol.data.D1009RunType)
   */
  public void transfertPoint(AbstractRunType runType) {
    TableRowObject row = tableModelRun.getTableRowObject(runType);
    if (row != null) {
      row.addProgressValue();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfertPoint
   * (int)
   */
  public void beginTransfertPoint(int nbPacket) {
    pointNotify = 15;
    jProgressBar.setMaximum(nbPacket);
    jProgressBar.setValue(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#pointNotify()
   */
  public int intervalNotify() {
    return pointNotify;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#beginStore(int)
   */
  public void beginStore(int maxLines) {
    jLabelProgress.setText(rb.getString("jLabelProgressBeginStore"));
    jProgressBar.setIndeterminate(false);
    jProgressBar.setStringPainted(true);
    jProgressBar.setMaximum(maxLines);
    for (TableRowObject row : tableModelRun.listRun) {
      row.setProgressValue(0);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#store(int, int)
   */
  public void store(int current, int maxPoint) {
    jProgressBar.setValue(current);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.db.progress.IRunStoreProgress#beginStore(fr.turtlesport.
   * protocol.data.D1009RunType)
   */
  public void beginStore(AbstractRunType run) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#beginStorePoint()
   */
  public void beginStorePoint() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.db.progress.IRunStoreProgress#endStore(fr.turtlesport.protocol
   * .data.D1009RunType)
   */
  public void endStore(AbstractRunType run) {
    jProgressBar.setValue(jProgressBar.getMaximum());
    TableRowObject row = tableModelRun.getTableRowObject(run);
    if (row != null) {
      row.endProgress();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#endStore()
   */
  public void endStore() {
    jLabelProgress.setText(rb.getString("jLabelProgressEndStore"));
    jProgressBar.setIndeterminate(false);
    jProgressBar.setValue(100);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.db.progress.IRunStoreProgress#storePoint(fr.turtlesport.
   * protocol.data.D1009RunType, int, int)
   */
  public void storePoint(AbstractRunType run, int currentPoint, int maxPoint) {
    TableRowObject row = tableModelRun.getTableRowObject(run);
    if (row != null) {
      row.endProgress();
      double pourc = (currentPoint * 100.0) / maxPoint;
      row.setProgressValue((int) pourc);
    }
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1100, 450);
    this.setTitle(rb.getString("title"));
    this.setContentPane(getJContentPane());
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    // evenements
    cancelActionListener = new CancelActionListener();
    jButtonCancel.addActionListener(cancelActionListener);
    jButtonSelect.addActionListener(new SelectActionListener(true));
    jButtonUnselect.addActionListener(new SelectActionListener(false));

    saveActionListener = new SaveActionListener();
    jButtonSave.addActionListener(saveActionListener);
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jLabelNorth = new JLabel();
      jLabelNorth.setText("  ");
      BorderLayout borderLayout = new BorderLayout();
      borderLayout.setVgap(5);
      jContentPane = new JPanel();
      jContentPane.setLayout(borderLayout);
      jContentPane.add(getJScrollPaneTrack(), BorderLayout.CENTER);
      jContentPane.add(getJPanelSouth(), BorderLayout.SOUTH);
      jContentPane.add(jLabelNorth, BorderLayout.NORTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes jScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getJScrollPaneTrack() {
    if (jScrollPaneTrack == null) {
      jScrollPaneTrack = new JScrollPane();
      jScrollPaneTrack.setViewportView(getJTableRun());
    }
    return jScrollPaneTrack;
  }

  /**
   * This method initializes jTable
   * 
   * @return javax.swing.JTable
   */
  private JTableCustom getJTableRun() {
    if (jTableRun == null) {
      jTableRun = new JTableCustom();
      jTableRun.setModel(tableModelRun);
      jTableRun.setFont(GuiFont.FONT_PLAIN);
      jTableRun.setShowGrid(false);
      jTableRun.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      // colum size
      TableColumn column;
      jTableRun.setRowHeight(22);
      for (int i = 0; i < tableModelRun.getColumnCount(); i++) {
        column = jTableRun.getColumnModel().getColumn(i);
        // column.setPreferredWidth(tableModelRun.getPreferredWidth(i));
        if (tableModelRun.hasCellEditor(i)) {
          column.setCellEditor(tableModelRun.getCellEditor(i));
        }
        if (tableModelRun.hasRenderer(i)) {
          column.setCellRenderer(tableModelRun.getCellRenderer(i));
        }
      }

      jTableRun.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      jTableRun.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTableRun.setSortable(true);
      jTableRun.packAll();
    }
    return jTableRun;
  }

  /**
   * This method initializes jPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      flowLayout.setVgap(0);
      jPanelButton = new JPanel();
      jPanelButton.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));
      jPanelButton.setLayout(flowLayout);
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
  private JButton getJButtonCancel() {
    if (jButtonCancel == null) {
      jButtonCancel = new JButton();
      jButtonCancel.setFont(GuiFont.FONT_PLAIN);
      jButtonCancel.setText(rb.getString("jButtonCancel"));
      jButtonCancel.setEnabled(true);
    }
    return jButtonCancel;
  }

  /**
   * This method initializes jButtonSave
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonSave() {
    if (jButtonSave == null) {
      jButtonSave = new JButton();
      jButtonSave.setFont(GuiFont.FONT_PLAIN);
      jButtonSave.setText(rb.getString("jButtonSave"));
      jButtonSave.setEnabled(false);
    }
    return jButtonSave;
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
      jButtonSelect.setEnabled(false);
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
      jButtonUnselect.setEnabled(false);
    }
    return jButtonUnselect;
  }

  /**
   * This method initializes jPanelSouth
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelSouth() {
    if (jPanelSouth == null) {
      jPanelSouth = new JPanel();
      jPanelSouth.setLayout(new BoxLayout(getJPanelSouth(), BoxLayout.Y_AXIS));
      jPanelSouth.add(getJPanelButton(), null);
      jPanelSouth.add(getJPanelStatus(), null);
    }
    return jPanelSouth;
  }

  /**
   * This method initializes jPanelSttaus
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelStatus() {
    if (jPanelStatus == null) {
      jLabelProgress = new JLabel();
      jLabelProgress.setPreferredSize(new Dimension(200, 20));
      jLabelProgress.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabelProgress.setVerticalAlignment(SwingConstants.TOP);
      jLabelProgress.setVerticalTextPosition(SwingConstants.TOP);
      jLabelProgress.setFont(GuiFont.FONT_PLAIN);
      FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT, 5, 3);
      jPanelStatus = new JPanel();
      jPanelStatus.setLayout(flowLayout);
      jPanelStatus.add(jLabelProgress, null);
      jPanelStatus.add(getJProgressBar(), null);
    }
    return jPanelStatus;
  }

  /**
   * This method initializes jProgressBar
   * 
   * @return javax.swing.JProgressBar
   */
  private JProgressBar getJProgressBar() {
    if (jProgressBar == null) {
      jProgressBar = new JProgressBar();
      jProgressBar.setPreferredSize(new Dimension(200, 18));
      jProgressBar.setMaximumSize(new Dimension(200, 18));
      jProgressBar.setMinimumSize(new Dimension(200, 18));
      jProgressBar.setIndeterminate(true);
      jProgressBar.setFont(GuiFont.FONT_PLAIN);
    }
    return jProgressBar;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TableModelRun extends AbstractTableModel {
    private String[]                  columnNames   = { "Progression",
                                                        "Date",
                                                        "Heure",
                                                        "Distance",
                                                        "Athlete",
                                                        "Activite",
                                                        "Equipement",
                                                        "Sauvegarder",
                                                        "Commentaires" };

    private final int[]               columWidth    = { 110,
                                                        90,
                                                        90,
                                                        110,
                                                        170,
                                                        170,
                                                        140,
                                                        100,
                                                        300 };

    private final Class<?>[]          columnClasses = { JProgressBar.class,
                                                        String.class,
                                                        String.class,
                                                        String.class,
                                                        JComboBox.class,
                                                        JComboBox.class,
                                                        JComboBox.class,
                                                        Boolean.class,
                                                        String.class };

    private ArrayList<TableRowObject> listRun       = new ArrayList<TableRowObject>();

    public TableModelRun() {
      super();
      performedLanguage();
    }

    private void performedLanguage() {
      // Mis a jour des colonnes
      for (int i = 0; i < columnNames.length; i++) {
        if (i != 3) {
          columnNames[i] = rb.getString("columnNames" + i);
        }
        else {
          // distance
          columnNames[3] = MessageFormat.format(rb.getString("columnNames3"),
                                                DistanceUnit.getDefaultUnit());
        }
      }
    }

    /**
     * @param column
     * @return
     */
    public int getPreferredWidth(int column) {
      return columWidth[column];
    }

    public TableCellRenderer getCellRenderer(int column) {
      if (column == 0) {
        return new ProgressBarCellRenderer();
      }
      if (column == 4) {
        return new ComboBoxCellRenderer(users);
      }
      if (column == 5) {
        return new ComboBoxCellRenderer(activities);
      }
      if (column == 6) {
        return new ComboBoxCellRenderer(equipements);
      }
      return null;
    }

    public boolean hasRenderer(int column) {
      switch (column) {
        case 0: // Progression
        case 4: // Athlete
        case 5: // Activite
        case 6: // Equipement
          return true;
        default:
          return false;
      }
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
      return listRun.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
      switch (column) {
        case 0: // Progression
          return listRun.get(row).getProgressValue();

        case 1: // Date
          return listRun.get(row).getDate();

        case 2: // Heure
          return listRun.get(row).getTime();

        case 3: // Distance
          return listRun.get(row).getDistance();

        case 4: // Athlete
          return listRun.get(row).getUser();

        case 5: // Activite
          return listRun.get(row).getActivity();

        case 6: // Equipement
          return listRun.get(row).getEquipement();

        case 7: // Sauvegarder
          return listRun.get(row).isSave();

        case 8: // Commentaires
          return listRun.get(row).getComments();

        default:
          return "";
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
      switch (col) {
        case 4: // Athlete
          listRun.get(row).setUser((User) value);
          break;

        case 5: // Activite
          listRun.get(row).setActivity((AbstractDataActivity) value);
          break;

        case 6: // Equipement
          listRun.get(row).setEquipement((String) value);
          fireTableCellUpdated(jTableRun.convertRowIndexToView(row), col);
          break;

        case 7: // Sauvegarder
          listRun.get(row).setSave((Boolean) value);
          fireTableCellUpdated(jTableRun.convertRowIndexToView(row), col);
          break;

        case 8: // Commentaires
          listRun.get(row).setComments((String) value);
          fireTableCellUpdated(jTableRun.convertRowIndexToView(row), col);
          break;

        default:
          break;
      }
    }

    /**
     * Ajout d'une course.
     */
    public void addTransfertCourse(AbstractRunType runType) {
      int size = listRun.size();

      TableRowObject rowObj = new TableRowObject(size, runType);
      listRun.add(rowObj);

      rowObj.setActivity(runType.getSportType());

      fireTableRowsInserted(size, size);
      if (size == 0) {
        jTableRun.packAll();
      }
    }

    /**
     * @param runType
     * @return
     */
    protected TableRowObject getTableRowObject(AbstractRunType runType) {
      for (TableRowObject res : listRun) {
        if (res.runType.equals(runType)) {
          return res;
        }
      }
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      switch (columnIndex) {
        case 4: // Athlete
        case 5: // Activite
        case 6: // Equipement
        case 7: // Sauvegarder
        case 8: // Commentaires
          return true;
        default:
          return false;
      }
    }

    /**
     * @param column
     * @return
     */
    protected boolean hasCellEditor(int column) {
      switch (column) {
        case 4: // Athlete
        case 5: // Activite
        case 6: // Equipement
        case 8: // Commentaires
          return true;
        default:
          return false;
      }
    }

    /**
     * @param column
     * @return
     */
    protected TableCellEditor getCellEditor(int column) {
      if (column == 4) { // Athlete
        final JComboBox combo = new JComboBox(users);
        combo.setFont(GuiFont.FONT_PLAIN);
        return new DefaultCellEditor(combo);
      }
      if (column == 5) { // Activite
        final JComboBox combo = new JComboBox(activities);
        combo.setFont(GuiFont.FONT_PLAIN);
        return new DefaultCellEditor(combo);
      }
      if (column == 6) { // Equipements
        final JComboBox combo = new JComboBox(equipements);
        combo.setFont(GuiFont.FONT_PLAIN);
        return new DefaultCellEditor(combo);
      }
      if (column == 7) { // Commentaires
        final JTextFieldLength jTextField = new JTextFieldLength();
        jTextField.setMaxCharacters(100);
        jTextField.setFont(GuiFont.FONT_PLAIN);
        return new DefaultCellEditor(jTextField);
      }
      return null;
    }

  }

  /**
   * @author denis
   * 
   */
  private class TableRowObject {
    private AbstractRunType           runType;

    private int                    row;

    private final SimpleDateFormat dfDate        = new SimpleDateFormat("dd/MM/yyyy");

    private final SimpleDateFormat dfTime        = new SimpleDateFormat("HH:mm:ss");

    private String                 distance;

    private String                 date;

    private String                 time;

    private boolean                isSave;

    private int                    progressValue = 0;

    private AbstractDataActivity   activity;

    private User                   user;

    private String                 comments;

    private String                 equipement;

    /**
     * 
     * @param row
     * @param index
     */
    public TableRowObject(int row, AbstractRunType runType) {
      super();
      this.row = row;
      this.runType = runType;
      this.runType.setExtra(new DataRunExtra());
      if (defaultEquipement != null) {
        setEquipement(defaultEquipement);
      }
      if (defaultActivity != null) {
        setActivity(defaultActivity);
      }
      if (defaultUser != null) {
        setUser(defaultUser);
      }
      isSave = false;

    }

    public void setEquipement(String equipement) {
      this.equipement = equipement;
      if (runType.getExtra() == null) {
        runType.setExtra(new DataRunExtra());
      }
      ((DataRunExtra) runType.getExtra()).setEquipement(equipement);
    }

    public String getEquipement() {
      return equipement;
    }

    /**
     * @return
     */
    public String getComments() {
      return comments;
    }

    /**
     * @return
     */
    public void setComments(String comments) {
      this.comments = comments;
      if (runType.getExtra() == null) {
        runType.setExtra(new DataRunExtra());
      }
      if (comments == null || "".equals(comments)) {
        ((DataRunExtra) runType.getExtra()).setComments(null);
      }
      else {
        ((DataRunExtra) runType.getExtra()).setComments(comments);
      }
    }

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
      ((DataRunExtra) runType.getExtra()).setIdUser(user.getData().getId());
    }

    public AbstractDataActivity getActivity() {
      return activity;
    }

    public void setActivity(AbstractDataActivity activity) {
      if (this.activity != null && this.activity.equals(activity)) {
        return;
      }
      this.activity = activity;

      int viewRow = jTableRun.convertRowIndexToView(row);
      runType.setSportType(activity.getSportType());

      tableModelRun.fireTableCellUpdated(viewRow, 4);
    }

    public void setActivity(int sportType) {
      activity = find(sportType);
      int viewRow = jTableRun.convertRowIndexToView(row);
      tableModelRun.fireTableCellUpdated(viewRow, 4);
    }

    private AbstractDataActivity find(int sportType) {
      for (AbstractDataActivity a : activities) {
        if (sportType == a.getSportType()) {
          return a;
        }
      }
      // autre sport
      for (AbstractDataActivity a : activities) {
        if (DataActivityOther.SPORT_TYPE == a.getSportType()) {
          return a;
        }
      }
      return null;
    }

    /**
     * @return the distance
     */
    public String getDistance() {
      return distance;
    }

    /**
     * Valorise la distance de la course.
     * 
     * @param distance
     *          la nouvelle valeur.
     */
    public void setDistance(double distance) {
      this.distance = DistanceUnit.formatMetersInKm(distance);
      tableModelRun.fireTableCellUpdated(row, 3);
    }

    /**
     * @return the isSave
     */
    public boolean isSave() {
      return isSave;
    }

    /**
     * @param isSave
     *          the isSave to set
     */
    public void setSave(boolean isSave) {
      this.isSave = isSave;
      // rend le bouton sauvegarder unable si fin transfert.
      if (isEndTransfert) {
        if (isSave) {
          jButtonSave.setEnabled(true);
        }
        else {
          boolean hasRuntoSave = false;
          for (TableRowObject row : tableModelRun.listRun) {
            if (row.isSave) {
              hasRuntoSave = true;
              break;
            }
          }
          jButtonSave.setEnabled(hasRuntoSave);
        }
      }
      tableModelRun.fireTableCellUpdated(row, 6);
    }

    /**
     * Restitue la date du premier tour.
     * 
     * @return la date du premier tour.
     */
    public String getDate() {
      return date;
    }

    /**
     * Restitue l'heure du premier tour.
     * 
     * @return l'heure du premier tour.
     */
    public String getTime() {
      return time;
    }

    /**
     * Valorise la date du tour.
     * 
     * @param date
     *          la nouvelle valeur.
     */
    public void setDate(Date date) {
      if (this.date != null) {
        return;
      }
      this.date = dfDate.format(date);
      this.time = dfTime.format(date);
      tableModelRun.fireTableCellUpdated(row, 1);
      tableModelRun.fireTableCellUpdated(row, 2);
    }

    /**
     * @return the progressValue
     */
    public int getProgressValue() {
      return progressValue;
    }

    /**
     * @return the progressValue
     */
    public void setProgressValue(int progressValue) {
      this.progressValue = progressValue;
      tableModelRun.fireTableCellUpdated(row, 0);
    }

    /**
     * @return the progressValue
     */
    public void addProgressValue() {
      progressValue += 3;
      if (progressValue >= 100) {
        progressValue = 3;
      }
      tableModelRun.fireTableCellUpdated(row, 0);
    }

    /**
     * 
     */
    public void endProgress() {
      if (progressValue != 100) {
        progressValue = 100;
        tableModelRun.fireTableCellUpdated(row, 0);
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class User {
    private DataUser data;

    public User(DataUser data) {
      super();
      this.data = data;
    }

    @Override
    public String toString() {
      return data.getFirstName() + " " + data.getLastName();
    }

    public DataUser getData() {
      return data;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class CancelActionListener implements ActionListener {
    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {
      isAbortTransfert = true;
      dispose();
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
      for (TableRowObject row : tableModelRun.listRun) {
        row.setSave(isSelect);
      }
      jButtonSave.setEnabled(isSelect);
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class SaveActionListener implements ActionListener {

    public SaveActionListener() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {
      JDialogProgressRun.this.setCursor(Cursor
          .getPredefinedCursor(Cursor.WAIT_CURSOR));
      jButtonCancel.setEnabled(false);
      new SwingWorker() {

        @Override
        public Object construct() {
          ArrayList<String> listEquipement = new ArrayList<String>();

          // On ne garde que les run selectionne et on recupere les equipements.
          for (TableRowObject row : tableModelRun.listRun) {
            if (!row.isSave()) {
              a1000.removeRunType(row.runType);
            }
            else if (row.getEquipement() != null
                     && !listEquipement.contains(row.getEquipement())) {
              listEquipement.add(row.getEquipement());
            }
          }

          // Sauvegarde des run
          try {
            long deb = System.currentTimeMillis();
            RunTableManager.getInstance().store(a1000, JDialogProgressRun.this);
            log.warn("Temps pour sauvegarder " + a1000.getListRunTypeSize()
                     + " run (ms) --> " + (System.currentTimeMillis() - deb));
          }
          catch (SQLException sqle) {
            log.error("", sqle);
            jProgressBar.setIndeterminate(false);
            JShowMessage.error("Impossible de sauvegarder les courses.");
            dispose();
            return null;
          }

          // Recuperation des equipements en alertes
          jProgressBar.setIndeterminate(false);
          JDialogProgressRun.this.setCursor(Cursor.getDefaultCursor());
          JPanelRunSave panel = new JPanelRunSave(listEquipement);

          getJContentPane().remove(getJScrollPaneTrack());
          getJContentPane().add(panel, BorderLayout.CENTER);
          getJPanelButton().remove(getJButtonSelect());
          getJPanelButton().remove(getJButtonUnselect());
          getJPanelButton().remove(getJButtonSave());
          getJButtonCancel().setEnabled(true);
          getJButtonCancel().setText(rb.getString("textEnd"));
          getJButtonCancel().addActionListener(cancelActionListener);
          jLabelProgress.setText("");
          jProgressBar.setValue(0);
          jProgressBar.setVisible(false);

          // On se positionne sur la 1ere ligne
          panel.selectFirstRow();

          // mise a jour des dates;
          MainGui.getWindow().fireHistoric();
          return null;
        }

        @Override
        public void finished() {
          MainGui.getWindow().afterRunnableSwing();
        }

      }.start();

    }
  }

} // @jve:decl-index=0:visual-constraint="10,10"
