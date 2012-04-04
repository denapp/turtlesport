package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.decorator.SortOrder;
import org.xml.sax.SAXParseException;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.DataEquipement;
import fr.turtlesport.db.DataRunExtra;
import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.db.UserTableManager;
import fr.turtlesport.db.progress.IGeoRouteStoreProgress;
import fr.turtlesport.geo.FactoryGeoLoad;
import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.IGeoRoute;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JFileChooserOS;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.ui.swing.component.JTextFieldLength;
import fr.turtlesport.ui.swing.component.jtable.ComboBoxCellRenderer;
import fr.turtlesport.ui.swing.component.jtable.DateCellEditor;
import fr.turtlesport.ui.swing.component.jtable.DateCellRenderer;
import fr.turtlesport.ui.swing.component.jtable.ProgressBarCellRenderer;
import fr.turtlesport.ui.swing.component.jtable.TimeCellEditor;
import fr.turtlesport.ui.swing.component.jtable.TimeCellRenderer;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public final class JDialogImport extends JDialog implements
                                                IGeoRouteStoreProgress {
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogImport.class);
  }

  private JPanel                      jContentPane;

  private JScrollPane                 jScrollPaneTrack;

  private JTableCustom                jTable;

  private JPanel                      jPanelButton;

  private JButton                     jButtonCancel;

  private JButton                     jButtonSave;

  private JButton                     jButtonDelete;

  private JButton                     jButtonImport;

  private JButton                     jButtonSelect;

  private JButton                     jButtonUnselect;

  private JPanel                      jPanelSouth;

  private JPanel                      jPanelStatus;

  private JProgressBar                jProgressBar;

  private JLabel                      jLabelNorth;

  private JLabel                      jLabelProgress;

  // Athlete
  private User[]                      users;

  private User                        defaultUser;

  // Equipements
  private String[]                    equipements;

  private String                      defaultEquipement;

  // Activites
  private AbstractDataActivity[]      activities;

  private AbstractDataActivity        defaultActivity;

  // model
  private TableModelImport            tableModel;

  private SaveActionListener          saveActionListener;

  private CancelActionListener        cancelActionListener;

  private AddActionListener           addActionListener;

  private JTableListSelectionListener jTableListSelectionListener;

  // Equipements
  private ResourceBundle              rb;

  private JPanel                      jPanelCenter;

  private JLabel                      jLabelTextTable;

  /**
   * @param owner
   * @param modal
   * @throws SQLException
   */
  private JDialogImport(Frame owner) throws SQLException {
    super(owner, true);

    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    tableModel = new TableModelImport();

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

    initialize();
  }

  /**
   * @param files
   */
  public static void prompt() {
    File[] files = getSelectedFiles();
    if (files == null) {
      return;
    }

    JDialogImport dlg;
    try {
      dlg = new JDialogImport(MainGui.getWindow());
      dlg.setLocationRelativeTo(MainGui.getWindow());
      dlg.addActionListener.fireFiles(files);
      dlg.setVisible(true);
    }
    catch (SQLException e) {
      log.error("", e);
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), JDialogImport.class);
      JShowMessage.error(MessageFormat.format(rb.getString("errorSql"),
                                              e.getErrorCode()));
    }
  }

  /**
   * Recuperation des fichiers &agrave; importer.
   */
  private static File[] getSelectedFiles() {
    JFileChooserOS fc = new JFileChooserOS();
    fc.setMultiSelectionEnabled(true);

    // Recuperation du dernier repertoire dans le .ini
    File dir = null;
    String path = Configuration.getConfig().getProperty("Import", "lastDir");
    if (path != null) {
      dir = new File(path);
      if (!dir.isDirectory()) {
        dir = null;
      }
    }

    // Ajout des filtres
    for (FileFilter ff : ImportFileFilter.filefilters()) {
      fc.addChoosableFileFilter(ff);
    }

    // Ouverture dialog
    int ret = fc.showOpenDialog(MainGui.getWindow(), dir);
    if (ret != JFileChooser.APPROVE_OPTION) {
      return null;
    }
    File[] files = fc.getSelectedFiles();

    // Sauvegarde du dernier repertoire dans le .ini
    Configuration.getConfig().addProperty("Import",
                                          "lastDir",
                                          files[0].getParent());

    return files;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#beginStore(int)
   */
  public void beginStore(int maxPoint) {
    jLabelProgress.setText(rb.getString("jLabelProgressBeginStore"));
    jProgressBar.setIndeterminate(false);
    jProgressBar.setStringPainted(true);
    jProgressBar.setMaximum(maxPoint);
    jProgressBar.setValue(0);
    for (TableRowObject row : tableModel.listRows) {
      row.setProgressValue(0);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#store(int, int)
   */
  public void store(int current, int maxPoint) {
    jProgressBar.setValue(current);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.db.progress.IGeoRouteStoreProgress#beginStore(fr.turtlesport
   * .geo.IGeoRoute)
   */
  public void beginStore(IGeoRoute route) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.db.progress.IGeoRouteStoreProgress#endStore(fr.turtlesport
   * .geo.IGeoRoute)
   */
  public void endStore(IGeoRoute route) {
    TableRowObject row = tableModel.getTableRowObject(route);
    if (row != null) {
      row.endProgress();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IGeoRouteStoreProgress#endStore()
   */
  public void endStore() {
    jLabelProgress.setText(rb.getString("jLabelProgressEndStore"));
    jProgressBar.setValue(jProgressBar.getMaximum());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.db.progress.IGeoRouteStoreProgress#storePoint(fr.turtlesport
   * .geo.IGeoRoute, int, int)
   */
  public void storePoint(IGeoRoute route, int currentPoint, int maxPoint) {
    TableRowObject row = tableModel.getTableRowObject(route);
    if (row != null) {
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
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // evenements
    cancelActionListener = new CancelActionListener();
    jButtonCancel.addActionListener(new CancelActionListener());
    jButtonSelect.addActionListener(new SelectActionListener(true));
    jButtonUnselect.addActionListener(new SelectActionListener(false));

    saveActionListener = new SaveActionListener();
    jButtonSave.addActionListener(saveActionListener);

    addActionListener = new AddActionListener();
    jButtonImport.addActionListener(addActionListener);
    jButtonDelete.addActionListener(new DeleteActionListener());

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
      jLabelNorth = new JLabel();
      jLabelNorth.setText("  ");
      BorderLayout borderLayout = new BorderLayout();
      borderLayout.setVgap(5);
      jContentPane = new JPanel();
      jContentPane.setLayout(borderLayout);
      jContentPane.add(getJPanelCenter(), BorderLayout.CENTER);
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
  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jLabelTextTable = new JLabel(" ");
      jLabelTextTable.setForeground(Color.red);
      jLabelTextTable.setFont(GuiFont.FONT_PLAIN);
      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(new BorderLayout());
      jPanelCenter.add(getJScrollPaneTrack(), BorderLayout.CENTER);
      jPanelCenter.add(jLabelTextTable, BorderLayout.SOUTH);
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getJScrollPaneTrack() {
    if (jScrollPaneTrack == null) {
      jScrollPaneTrack = new JScrollPane();
      jScrollPaneTrack.setViewportView(getJTable());
    }
    return jScrollPaneTrack;
  }

  /**
   * This method initializes jTable
   * 
   * @return javax.swing.JTable
   */
  private JTableCustom getJTable() {
    if (jTable == null) {
      jTable = new JTableCustom();
      jTable.setModel(tableModel);
      jTable.setFont(GuiFont.FONT_PLAIN);
      jTable.setShowGrid(false);
      jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      // colum size
      TableColumn column;
      jTable.setRowHeight(22);
      for (int i = 0; i < tableModel.getColumnCount(); i++) {
        column = jTable.getColumnModel().getColumn(i);
        // column.setPreferredWidth(tableModel.getPreferredWidth(i));
        if (tableModel.hasCellEditor(i)) {
          column.setCellEditor(tableModel.getCellEditor(i));
        }
        if (tableModel.hasRenderer(i)) {
          column.setCellRenderer(tableModel.getCellRenderer(i));
        }
      }
      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      jTable.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTable.setSortable(true);
      jTable.packAll();
    }
    return jTable;
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
      jPanelButton.add(getJButtonImport(), null);
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
   * This method initializes jButtonImport
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonImport() {
    if (jButtonImport == null) {
      jButtonImport = new JButton();
      jButtonImport.setFont(GuiFont.FONT_PLAIN);
      jButtonImport.setText(rb.getString("jButtonImport"));
    }
    return jButtonImport;
  }

  /**
   * This method initializes jButtonDelete
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonDelete() {
    if (jButtonDelete == null) {
      jButtonDelete = new JButton();
      jButtonDelete.setFont(GuiFont.FONT_PLAIN);
      jButtonDelete.setText(rb.getString("jButtonDelete"));
      jButtonDelete.setEnabled(false);
    }
    return jButtonDelete;
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
      jProgressBar.setIndeterminate(false);
      jProgressBar.setFont(GuiFont.FONT_PLAIN);
    }
    return jProgressBar;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TableModelImport extends AbstractTableModel {

    private String[]                  columnNames   = { "",
                                                        "Progression",
                                                        "Date",
                                                        "Heure",
                                                        "Distance",
                                                        "Temps",
                                                        "Athlete",
                                                        "Categorie",
                                                        "Equipement",
                                                        "Sauvegarder",
                                                        "Commentaires" };

    private final int[]               columWidth    = { 30,
                                                        110,
                                                        90,
                                                        90,
                                                        110,
                                                        110,
                                                        170,
                                                        170,
                                                        140,
                                                        100,
                                                        300 };

    private final Class<?>[]          columnClasses = { ImageIcon.class,
                                                        JProgressBar.class,
                                                        Date.class,
                                                        Date.class,
                                                        String.class,
                                                        String.class,
                                                        JComboBox.class,
                                                        JComboBox.class,
                                                        JComboBox.class,
                                                        Boolean.class,
                                                        String.class };

    private ArrayList<TableRowObject> listRows      = new ArrayList<TableRowObject>();

    private ImageIcon                 imageOK       = ImagesRepository
                                                        .getImageIcon("checkmark.png");

    private ImageIcon                 imageKO       = ImagesRepository
                                                        .getImageIcon("redcross.png");

    private ImageIcon                 imageWarning  = ImagesRepository
                                                        .getImageIcon("warning.png");

    private Date                      currentDate   = Calendar.getInstance()
                                                        .getTime();

    /**
     * @param files
     */
    public TableModelImport() {
      super();

      performedLanguage();
    }

    private void performedLanguage() {
      // Mis a jour des colonnes
      for (int i = 1; i < columnNames.length; i++) {
        if (i != 4) {
          columnNames[i] = rb.getString("columnNames" + i);
        }
        else {
          // distance
          columnNames[4] = MessageFormat.format(rb.getString("columnNames4"),
                                                DistanceUnit.getDefaultUnit());
        }
      }
    }

    /**
     * Ajout d'une course.
     */
    public boolean addImportCourse(File file) throws FileNotFoundException,
                                             GeoLoadException {
      boolean isAdd = false;

      // Recuperation des routes
      IGeoRoute[] routes = FactoryGeoLoad.getRoutes(file);
      if (routes != null) {
        for (IGeoRoute r : routes) {
          int size = listRows.size();
            TableRowObject rowObj = new TableRowObject(size, r, file);
            listRows.add(rowObj);
            switch (r.getSportType()) {
              case IGeoRoute.SPORT_TYPE_RUNNING:
              case IGeoRoute.SPORT_TYPE_BIKE:
                rowObj.setActivity(r.getSportType());
                break;
              default:
                rowObj.setActivity(IGeoRoute.SPORT_TYPE_OTHER);
                break;
            }
            fireTableRowsInserted(size, size);
            isAdd = true;
        }
      }

      return isAdd;
    }

    /**
     * Determine si la liste conient deja cette import.
     * 
     * @param file
     * @return
     */
    public boolean contains(File file) {
      if (listRows != null) {
        for (TableRowObject r : listRows) {
          if (r.getFile().equals(file)) {
            return true;
          }
        }
      }
      return false;
    }

    public int getPreferredWidth(int column) {
      return columWidth[column];
    }

    public TableCellRenderer getCellRenderer(int column) {
      switch (column) {
        case 1: // Progression
          return new ProgressBarCellRenderer();
        case 2: // Date
          return new DateCellRenderer();
        case 3: // Heure
          return new TimeCellRenderer();
        case 5: // Temps
          return new TimeCellRenderer();
        case 6: // Activites
          return new ComboBoxCellRenderer(users);
        case 7: // Activites
          return new ComboBoxCellRenderer(activities);
        case 8: // Equipement
          return new ComboBoxCellRenderer(equipements);
        default:
          return null;
      }
    }

    public boolean hasRenderer(int column) {
      switch (column) {
        case 1: // Progression
        case 2: // Date
        case 3: // Heure
        case 5: // Temps
        case 6: // Athlete
        case 7: // Categorie
        case 8: // Equipement
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
      return listRows.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
      switch (column) {
        case 0: // Peut etre sauvegarder ?
          if (!listRows.get(row).isValid()) {
            return imageKO;
          }
          if (listRows.get(row).isInDatabase()) {
            return imageWarning;
          }
          return imageOK;

        case 1: // Progression
          return listRows.get(row).getProgressValue();

        case 2: // Date
          return listRows.get(row).getDateDay();

        case 3: // Heure
          return listRows.get(row).getTime();

        case 4: // Distance
          return listRows.get(row).getDistance();

        case 5: // Temps
          return listRows.get(row).getTimeTot();

        case 6: // Athlete
          return listRows.get(row).getUser();

        case 7: // Activite
          return listRows.get(row).getActivity();

        case 8: // Equipement
          return listRows.get(row).getEquipement();

        case 9: // Sauvegarder
          return listRows.get(row).isSave();

        case 10: // Commentaires
          return listRows.get(row).getComments();

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
        case 2: // Date
          listRows.get(row).setDateDay((Date) value);
          break;

        case 3: // Heure
          listRows.get(row).setTime((Date) value);
          break;

        case 5: // Distance
          listRows.get(row).setTimeTot((Date) value);
          break;

        case 6: // Athlete
          listRows.get(row).setUser((User) value);
          fireTableCellUpdated(jTable.convertRowIndexToView(row), col);
          break;

        case 7: // Activite
          listRows.get(row).setActivity((AbstractDataActivity) value);
          fireTableCellUpdated(jTable.convertRowIndexToView(row), col);
          break;

        case 8: // Equipement
          listRows.get(row).setEquipement((String) value);
          fireTableCellUpdated(jTable.convertRowIndexToView(row), col);
          break;

        case 9: // Sauvegarder
          listRows.get(row).setSave((Boolean) value);
          fireTableCellUpdated(jTable.convertRowIndexToView(row), col);
          break;

        case 10: // Commentaires
          listRows.get(row).setComments((String) value);
          fireTableCellUpdated(jTable.convertRowIndexToView(row), col);
          break;

        default:
          break;
      }
    }

    protected TableRowObject getTableRowObject(IGeoRoute route) {
      for (TableRowObject r : listRows) {
        if (r.route.equals(route)) {
          return r;
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
        case 2: // Date
        case 3: // Heure
        case 5: // Temps
        case 6: // Athlete
        case 7: // Activite
        case 8: // Equipement
        case 10: // Commentaires
          return true;
        case 9: // Sauvegarder
          return listRows.get(rowIndex).isValid();
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
        case 2: // Date
        case 3: // Heure
        case 5: // Time
        case 6: // Athlete
        case 7: // Activite
        case 8: // Equipement
        case 10: // Commentaires
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
      switch (column) {
        case 2: // Date
          DateCellEditor dce = new DateCellEditor();
          dce.getDatePicker().getMonthView().setUpperBound(currentDate);
          return dce;
        case 3: // Heure
          return new TimeCellEditor();
        case 5: // Time
          return new TimeCellEditor();
        case 6: // Athlete
          final JComboBox comboUsers = new JComboBox(users);
          comboUsers.setFont(GuiFont.FONT_PLAIN);
          return new DefaultCellEditor(comboUsers);
        case 7: // Activite
          final JComboBox comboAct = new JComboBox(activities);
          comboAct.setFont(GuiFont.FONT_PLAIN);
          return new DefaultCellEditor(comboAct);
        case 8: // Equipement
          final JComboBox comboEqu = new JComboBox(equipements);
          comboEqu.setFont(GuiFont.FONT_PLAIN);
          return new DefaultCellEditor(comboEqu);
        case 10: // Commentaires
          final JTextFieldLength jTextField = new JTextFieldLength();
          jTextField.setMaxCharacters(100);
          jTextField.setFont(GuiFont.FONT_PLAIN);
          return new DefaultCellEditor(jTextField);
        default:
          return null;
      }
    }

    protected void fireRowsDateChanged() {
      if (listRows != null) {
        for (int i = 0; i < listRows.size(); i++) {
          try {
            Date date = listRows.get(i).getFullDate();
            boolean isValid = false;
            boolean isInDataBase = false;
            if (date != null && date.before(currentDate)) {
              if (log.isDebugEnabled()) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
                log.debug("date=" + df.format(date));
              }
              isValid = true;
              isInDataBase = (RunTableManager.getInstance()
                  .find(DataUser.getAllUser().getId(), date) != -1);
              if (isInDataBase) {
                log.debug("isInDataBase=" + date);
              }
            }
            listRows.get(i).setValidDateTime(isValid);
            listRows.get(i).setInDatabase(isInDataBase);
          }
          catch (SQLException e) {
            log.error("", e);
          }
        }
        for (int i = 0; i < listRows.size() - 1; i++) {
          for (int j = i + 1; j < listRows.size(); j++) {
            Date date1 = listRows.get(i).getFullDate();
            Date date2 = listRows.get(j).getFullDate();
            if (date1 != null && date1.equals(date2)) {
              listRows.get(i).setValidDateTime(false);
              listRows.get(j).setValidDateTime(false);
            }
          }
        }

        if (log.isDebugEnabled()) {
          SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
          for (int i = 0; i < listRows.size(); i++) {
            Date date = listRows.get(i).getFullDate();
            log.debug(i + ":" + ((date == null) ? "null" : df.format(date)));
          }
        }
      }

      // notification pour libelles icones
      jTableListSelectionListener.valueChanged(null);
    }

    public void removeRows(int[] tabIndex) {
      if (tabIndex == null || tabIndex.length == 0) {
        return;
      }

      if (log.isDebugEnabled()) {
        StringBuilder st = new StringBuilder();
        for (int index : tabIndex) {
          st.append(index);
          st.append(' ');
        }
        log.debug(">>removeRows " + st.toString());
        log.debug("RowCount=" + getRowCount());
      }

      try {
        // recuperation des objets
        List<TableRowObject> listTmp = new ArrayList<TableRowObject>();

        for (int index : tabIndex) {
          int rowIndex = jTable.convertRowIndexToModel(index);
          listTmp.add(listRows.get(rowIndex));
        }
        for (TableRowObject r : listTmp) {
          listRows.remove(r);
        }
        for (int index : tabIndex) {
          tableModel.fireTableRowsDeleted(index, index);
        }
        fireRowsDateChanged();

        // on se positionne dans la liste
        int index = tabIndex[0];
        if (index > 0) {
          index--;
        }
        if (index < getRowCount()) {
          jTable.setRowSelectionInterval(index, index);
        }
      }
      catch (IndexOutOfBoundsException e) {
        log.error("", e);
      }

      log.debug("<<removeRows");
    }
  }

  /**
   * @author denis
   * 
   */
  private class TableRowObject {
    private IGeoRoute            route;

    private int                  row;

    private String               distance;

    private Date                 dateDay;

    private Date                 dateTime;

    private Date                 timeTot;

    private boolean              isSave;

    private int                  progressValue = 0;

    private String               comments;

    private String               equipement;

    private File                 file;

    private boolean              isValidDateTime;

    private boolean              isValidTimeTot;

    private boolean              isInDatabase;

    private Calendar             calendar;

    private AbstractDataActivity activity;

    private User                 user;

    /**
     * 
     * @param row
     * @param index
     */
    public TableRowObject(int row, IGeoRoute route, File file) {
      super();

      this.row = row;
      this.route = route;
      this.route.setExtra(new DataRunExtra());
      this.file = file;
      isSave = false;
      setDistance(route.distanceTot());

      calendar = Calendar.getInstance();
      if (route.hasPointsDate()) {
        dateDay = route.getStartTime();
        dateTime = route.getStartTime();
        calendar.setTime(route.getStartTime());
      }
      else {
        isValidTimeTot = false;
        dateTime = calendar.getTime();
      }
      setTimeTot(route.totalTime());

      if (log.isDebugEnabled()) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
        log.debug("TableRowObject date=" + df.format(calendar.getTime()));
      }
      if (defaultEquipement != null) {
        setEquipement(defaultEquipement);
      }
      if (defaultActivity != null) {
        setActivity(defaultActivity);
      }
      if (defaultUser != null) {
        setUser(defaultUser);
      }
    }

    /**
     * @return the calendar
     */
    public Date getFullDate() {
      if (dateDay != null && dateTime != null) {
        return calendar.getTime();
      }
      return null;
    }

    /**
     * Valorise le temps de la course.
     * 
     * @param totalTime
     *          le temps de la course.
     */
    public void setTimeTot(long totalTime) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      if (totalTime > 0) {
        isValidTimeTot = true;
        cal.set(Calendar.MILLISECOND, (int) totalTime);
      }
      else {
        isValidTimeTot = false;
        setSave(false);
      }
      timeTot = cal.getTime();
      tableModel.fireTableCellUpdated(row, 5);
    }

    /**
     * Valorise le temps de la course.
     * 
     * @param totalTime
     *          le temps de la course.
     */
    public void setTimeTot(Date timeTot) {
      log.debug(">>setTimeTot");
      if (this.timeTot.equals(timeTot)) {
        return;
      }

      this.timeTot = timeTot;
      Calendar cal = Calendar.getInstance();
      cal.setTime(timeTot);
      if (cal.get(Calendar.HOUR_OF_DAY) > 0 || cal.get(Calendar.MINUTE) > 0
          || cal.get(Calendar.SECOND) > 0) {
        isValidTimeTot = true;
      }
      else {
        isValidTimeTot = false;
        setSave(false);
      }

      tableModel.fireTableCellUpdated(row, 5);
      log.debug("<<setTimeTot isValidTimeTot=" + isValidTimeTot);
    }

    public boolean isValid() {
      return isValidDateTime && isValidTimeTot;
    }

    public boolean isValidDateTime() {
      return isValidDateTime;
    }

    public boolean isValidTimeTot() {
      return isValidTimeTot;
    }

    public void setValidDateTime(boolean isValidDateTime) {
      this.isValidDateTime = isValidDateTime;
      if (!isValidDateTime) {
        setSave(false);
      }
      tableModel.fireTableCellUpdated(row, 0);
    }

    public Date getTimeTot() {
      return timeTot;
    }

    public long getTimeTotLong() {
      return TimeUnit.computeTimeMilliSeconde(timeTot);
    }

    public void setEquipement(String equipement) {
      this.equipement = equipement;
      ((DataRunExtra) route.getExtra()).setEquipement(equipement);
    }

    public String getEquipement() {
      return equipement;
    }

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
      if (user != null) {
        ((DataRunExtra) route.getExtra()).setIdUser(user.getData().getId());
      }
    }

    public String getComments() {
      return comments;
    }

    public void setComments(String comments) {
      this.comments = comments;
      if (route.getExtra() == null) {
        route.setExtra(new DataRunExtra());
      }
      if (comments == null || "".equals(comments)) {
        route.setExtra(null);
      }
      else {
        ((DataRunExtra) route.getExtra()).setComments(comments);
      }
    }

    public AbstractDataActivity getActivity() {
      return activity;
    }

    public void setActivity(AbstractDataActivity activity) {
      if (activity == null) {
        return;
      }
      if (this.activity != null && this.activity.equals(activity)) {
        return;
      }
      this.activity = activity;

      int viewRow = jTable.convertRowIndexToView(row);

      route.setSportType(activity.getSportType());
      tableModel.fireTableCellUpdated(viewRow, 6);
    }

    public void setActivity(int sportType) {
      activity = find(sportType);
      int viewRow = jTable.convertRowIndexToView(row);
      tableModel.fireTableCellUpdated(viewRow, 6);
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

    public String getDistance() {
      return distance;
    }

    public void setDistance(double distance) {
      this.distance = DistanceUnit.formatMetersInKm(distance);
      tableModel.fireTableCellUpdated(row, 4);
    }

    public boolean isSave() {
      return isSave;
    }

    public void setSave(boolean isSave) {
      if (this.isSave == isSave) {
        return;
      }

      this.isSave = isSave;
      // rend le bouton sauvegarder unable si fin transfert.
      if (isSave) {
        jButtonSave.setEnabled(true);
      }
      else {
        boolean hasRuntoSave = false;
        for (TableRowObject row : tableModel.listRows) {
          if (row.isSave) {
            hasRuntoSave = true;
            break;
          }
        }
        jButtonSave.setEnabled(hasRuntoSave);
      }

      tableModel.fireTableCellUpdated(row, 8);
    }

    /**
     * Valorise la date du tour.
     * 
     * @param date
     *          la nouvelle valeur.
     */
    public void setDateDay(Date dateDay) {
      this.dateDay = dateDay;
      if (dateDay != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateDay);
        calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
      }
      tableModel.fireTableCellUpdated(row, 2);
      tableModel.fireRowsDateChanged();
    }

    /**
     * Restitue la date du premier tour.
     * 
     * @return la date du premier tour.
     */
    public Date getDateDay() {
      return dateDay;
    }

    /**
     * Valorise la date du tour.
     * 
     * @param date
     *          la nouvelle valeur.
     */
    public void setTime(Date dateTime) {
      this.dateTime = dateTime;
      if (dateTime != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, cal.get(Calendar.SECOND));
      }
      tableModel.fireTableCellUpdated(row, 3);
      tableModel.fireRowsDateChanged();
    }

    /**
     * Restitue l'heure du premier tour.
     * 
     * @return l'heure du premier tour.
     */
    public Date getTime() {
      return dateTime;
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
      tableModel.fireTableCellUpdated(row, 1);
    }

    /**
     * @return the progressValue
     */
    public void addProgressValue() {
      progressValue += 3;
      if (progressValue >= 100) {
        progressValue = 3;
      }
      tableModel.fireTableCellUpdated(row, 1);
    }

    /**
     * 
     */
    public void endProgress() {
      if (progressValue != 100) {
        progressValue = 100;
        tableModel.fireTableCellUpdated(row, 1);
      }
    }

    /**
     * @return the file
     */
    public File getFile() {
      return file;
    }

    public boolean isInDatabase() {
      return isInDatabase;
    }

    /**
     * @param isInDatabase
     *          the isInDatabase to set
     */
    public void setInDatabase(boolean isInDatabase) {
      this.isInDatabase = isInDatabase;
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
      for (TableRowObject row : tableModel.listRows) {
        if (row.isValid()) {
          row.setSave(isSelect);
        }
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
      JDialogImport.this.setCursor(Cursor
          .getPredefinedCursor(Cursor.WAIT_CURSOR));
      jButtonCancel.setEnabled(false);
      new SwingWorker() {

        @Override
        public Object construct() {
          ArrayList<String> listEquipement = new ArrayList<String>();

          try {
            // On ne garde que les run selectionne et on recupere les
            // equipements.
            List<IGeoRoute> listGeoRoute = new ArrayList<IGeoRoute>();
            for (TableRowObject row : tableModel.listRows) {
              if (row.isSave()) {
                listGeoRoute.add(row.route);
                // mis a jour date/heures et duree
                row.route.update(row.getFullDate(), row.getTimeTotLong());
                if (row.getEquipement() != null
                    && !listEquipement.contains(row.getEquipement())) {
                  listEquipement.add(row.getEquipement());
                }
              }
            }

            // Sauvegarde des run
            long deb = System.currentTimeMillis();
            RunTableManager.getInstance().store(listGeoRoute,
                                                JDialogImport.this);
            log.warn("Temps pour sauvegarder " + listGeoRoute.size()
                     + " run (ms) --> " + (System.currentTimeMillis() - deb));
          }
          catch (SQLException sqle) {
            log.error("", sqle);
            jProgressBar.setIndeterminate(false);
            jProgressBar.setValue(0);
            JShowMessage.error(MessageFormat.format(rb
                .getString("errorSaveSql"), sqle.getErrorCode()));
            return null;
          }
          catch (Throwable th) {
            log.error("", th);
            jProgressBar.setIndeterminate(false);
            jProgressBar.setValue(0);
            JShowMessage.error(rb.getString("errorSave"));
            return null;
          }

          // Recuperation des equipements en alertes
          jProgressBar.setIndeterminate(false);
          JPanelRunSave panel = new JPanelRunSave(listEquipement);

          getJContentPane().remove(getJPanelCenter());
          getJContentPane().add(panel, BorderLayout.CENTER);
          getJPanelButton().remove(getJButtonImport());
          getJPanelButton().remove(getJButtonSelect());
          getJPanelButton().remove(getJButtonUnselect());
          getJPanelButton().remove(getJButtonSave());
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
          jButtonCancel.setEnabled(true);
          JDialogImport.this.setCursor(Cursor.getDefaultCursor());
          MainGui.getWindow().afterRunnableSwing();
        }

      }.start();

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class AddActionListener implements ActionListener {

    public AddActionListener() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {

      // recuperation des fichiers
      final File[] files = getSelectedFiles();
      if (files == null) {
        return;
      }

      // Traitement
      fireFiles(files);
    }

    public void fireFiles(final File[] files) {
      MainGui.getWindow().beforeRunnableSwing();
      JDialogImport.this.setCursor(Cursor
          .getPredefinedCursor(Cursor.WAIT_CURSOR));
      jProgressBar.setIndeterminate(true);

      new SwingWorker() {
        private StringBuilder error   = new StringBuilder();

        private int           nbError = 0;

        @Override
        public Object construct() {
          // Ajout du model
          jProgressBar.setIndeterminate(true);
          boolean isAdd = false;
          for (File f : files) {
            if (!tableModel.contains(f)) {
              try {
                isAdd |= tableModel.addImportCourse(f);
              }
              catch (FileNotFoundException e) {
                log.error("", e);
                if (nbError < 3) {
                  error.append(MessageFormat.format(rb
                      .getString("errorDialogImportDetMsg1"), f.getName()));
                }
                nbError++;
              }
              catch (GeoLoadException e) {
                log.error("", e);
                if (nbError < 3) {
                  error.append(MessageFormat.format(rb
                      .getString("errorDialogImportDetMsg2"), f.getName()));
                  if (e.getCause() != null
                      && (e.getCause() instanceof SAXParseException)) {
                    SAXParseException se = (SAXParseException) e.getCause();
                    if (se.getLineNumber() != -1) {
                      error.append(" (");
                      error.append(se.getLineNumber());
                      error.append(',');
                      error.append(se.getColumnNumber());
                      error.append(')');
                    }
                  }
                }
                nbError++;
              }
            }
          }
          if (isAdd) {
            tableModel.fireRowsDateChanged();
            jTable.packAll();
          }
          jTable.setSortOrder(2, SortOrder.DESCENDING);
          return null;
        }

        @Override
        public void finished() {
          JDialogImport.this.setCursor(Cursor.getDefaultCursor());
          MainGui.getWindow().afterRunnableSwing();
          jProgressBar.setIndeterminate(false);
          jProgressBar.setValue(0);
          if (error.length() > 0) {
            error.insert(0,
                         "<html><body><b>"
                             + rb.getString("errorDialogImportMsg")
                             + "</b><br>");
            if (nbError > 3) {
              error.append("...");
            }
            error.append("</body></html>");
            JShowMessage.error(error.toString());
          }
        }

      }.start();
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DeleteActionListener implements ActionListener {

    public DeleteActionListener() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      tableModel.removeRows(jTable.getSelectedRows());
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

      switch (tabIndex.length) {
        case 0:// pas de selection
          jLabelTextTable.setText(" ");
          jButtonDelete.setEnabled(false);
          break;

        case 1:// une selection
          int viewRow = jTable.getSelectedRow();

          if (viewRow < 0) {
            jLabelTextTable.setText(" ");
          }
          else {
            int modelRow = jTable.convertRowIndexToModel(viewRow);
            jButtonDelete.setEnabled(true);

            TableRowObject row = tableModel.listRows.get(modelRow);
            if (row.isValid()) {
              if (row.isInDatabase()) {
                jLabelTextTable.setText(rb.getString("rowError2"));
              }
              else {
                jLabelTextTable.setText(" ");
              }
              break;
            }

            if (!row.isValidDateTime()) {
              // Date/Time invalide
              if (row.getFullDate() == null) {
                // Date incorrecte
                jLabelTextTable.setText(rb.getString("rowError1"));
              }
              else if (row.getFullDate().after(tableModel.currentDate)) {
                // date apres la date/heure du jour
                jLabelTextTable.setText(rb.getString("rowError1"));
              }
              else {
                // 2 import avec la meme date
                jLabelTextTable.setText(rb.getString("rowError3"));
              }
            }
            else {
              // Temps de course invalide
              jLabelTextTable.setText(rb.getString("rowError4"));
            }
          }
          break;

        default: // multi-selection
          jLabelTextTable.setText(" ");
          jButtonDelete.setEnabled(true);
          break;
      }

    }
  }

}
