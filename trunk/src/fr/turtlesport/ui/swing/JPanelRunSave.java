package fr.turtlesport.ui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import fr.turtlesport.db.DataEquipement;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JTableCustom;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.ImageUtil;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelRunSave extends JPanel {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelRunSave.class);
  }

  private JLabel               jLabelSave;

  private JScrollPane          jScrollPaneEquipment;

  private JPanel               jPanelPhotoEquipment;

  private JLabelPhoto          jLabelPhoto;

  private JTableCustom         jTableEquipment;

  private JPanel               jPanelMsg;

  // model
  private TableModelEquipement tableModel;

  private ResourceBundle       rb;

  /**
   * 
   */
  protected JPanelRunSave() {
    super();
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());
    tableModel = new TableModelEquipement();
    initialize();
  }

  /**
   * @param listEquipement
   */
  protected JPanelRunSave(ArrayList<String> listEquipement) {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    tableModel = new TableModelEquipement(listEquipement);
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(300, 200);
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.add(getJPanelMsg(), null);
    this.add(Box.createRigidArea(new Dimension(0, 5)));
    this.add(getJScrollPaneEquipment(), null);
    this.add(Box.createRigidArea(new Dimension(0, 5)));
    this.add(getJPanelPhotoEquipment(), null);
  }

  /**
   * 
   */
  public void selectFirstRow() {
    // on se positionne sur la 1ere ligne
    if (tableModel.getRowCount() > 0) {
      jTableEquipment.setRowSelectionInterval(0, 0);
      jTableEquipment.clearSelection();
      jTableEquipment.setRowSelectionInterval(0, 0);
    }
  }

  /**
   * This method initializes jPanelCenter
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelMsg() {
    if (jPanelMsg == null) {
      jLabelSave = new JLabel();
      jLabelSave.setFont(GuiFont.FONT_BOLD);
      jLabelSave.setText(rb.getString("jLabelSave"));
      jLabelSave.setAlignmentX(Component.LEFT_ALIGNMENT);

      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.LEFT);

      jPanelMsg = new JPanel();
      jPanelMsg.setLayout(flowLayout);

      jPanelMsg.add(jLabelSave, null);
    }
    return jPanelMsg;
  }

  /**
   * This method initializes jScrollPaneTableEquipment
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getJScrollPaneEquipment() {
    if (jScrollPaneEquipment == null) {
      jScrollPaneEquipment = new JScrollPane();
      jScrollPaneEquipment.setBorder(BorderFactory
          .createTitledBorder(null,
                              rb.getString("borderScrollPaneEquipment"),
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));
      jScrollPaneEquipment.setViewportView(getJTableEquipment());
    }
    return jScrollPaneEquipment;
  }

  /**
   * This method initializes jTable
   * 
   * @return javax.swing.JTable
   */
  private JTableCustom getJTableEquipment() {
    if (jTableEquipment == null) {
      jTableEquipment = new JTableCustom();
      jTableEquipment.setModel(tableModel);
      jTableEquipment.setFont(GuiFont.FONT_PLAIN);
      jTableEquipment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jTableEquipment.setColumnSelectionAllowed(false);
      jTableEquipment.setRowSelectionAllowed(true);
      jTableEquipment.setShowGrid(false);

      // Render
      for (int i = 0; i < tableModel.getRowCount(); i++) {
        jTableEquipment.setCellRenderer(tableModel.getCellRenderer(i), i, 0);
      }
      jTableEquipment.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      jTableEquipment.getTableHeader().setFont(GuiFont.FONT_PLAIN);
      jTableEquipment.packAll();
      
      // Evenement
      SelectionListener listener = new SelectionListener();
      jTableEquipment.getSelectionModel().addListSelectionListener(listener);
    }
    return jTableEquipment;
  }

  /**
   * This method initializes jPanelPhotoEquipment
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelPhotoEquipment() {
    if (jPanelPhotoEquipment == null) {
      jLabelPhoto = new JLabelPhoto();
      jLabelPhoto.setFont(GuiFont.FONT_PLAIN);
      Dimension dimPhoto = new Dimension(180, 140);
      jLabelPhoto.setPreferredSize(dimPhoto);
      jLabelPhoto.setMinimumSize(dimPhoto);

      jPanelPhotoEquipment = new JPanel();
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.LEFT);
      jPanelPhotoEquipment.setLayout(flowLayout);
      jPanelPhotoEquipment.add(jLabelPhoto, null);

    }
    return jPanelPhotoEquipment;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TableModelEquipement extends AbstractTableModel {
    private final String[]            columnNames = new String[6];

    private final int[]               columWidth  = { 40,
                                                      200,
                                                      100,
                                                      140,
                                                      140,
                                                      80 };

    private WarnRenderer              warnOK      = new WarnRenderer(false);

    private WarnRenderer              warnKO      = new WarnRenderer(true);

    private ArrayList<TableRowObject> listEqt     = new ArrayList<TableRowObject>();

    public TableModelEquipement() {
      this(null);
      performedLanguage();
    }

    public TableCellRenderer getCellRenderer(int i) {
      return listEqt.get(i).isAlert() ? warnKO : warnOK;
    }

    public TableModelEquipement(ArrayList<String> list) {
      performedLanguage();
      if (list != null) {
        for (String name : list) {
          listEqt.add(new TableRowObject(name));
        }
      }
    }

    private void performedLanguage() {
      // Mis a jour des colonnes
      columnNames[0] = rb.getString("columnNames0");
      columnNames[1] = rb.getString("columnNames1");
      columnNames[2] = rb.getString("columnNames2");
      columnNames[3] = MessageFormat.format(rb.getString("columnNames3"),
                                            DistanceUnit.getDefaultUnit());
      columnNames[4] = MessageFormat.format(rb.getString("columnNames4"),
                                            DistanceUnit.getDefaultUnit());
      columnNames[5] = rb.getString("columnNames5");
    }

    /**
     * @param index
     * @return
     */
    public TableRowObject getRowObject(int index) {
      return listEqt.get(index);
    }

    /**
     * @param column
     * @return
     */
    public int getPreferredWidth(int column) {
      return columWidth[column];
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
    @SuppressWarnings("unchecked")
    public Class getColumnClass(int c) {
      return String.class;
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
      return listEqt.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
      switch (column) {
        case 0:
          return "";
        case 1:
          return listEqt.get(row).getName();
        case 2:
          return listEqt.get(row).getFirstUsed();
        case 3:
          return listEqt.get(row).getDistanceMax();
        case 4:
          return listEqt.get(row).getDistanceRun();
        case 5:
          return listEqt.get(row).getPourcent();
        default:
          return "";
      }

    }
  }

  private class TableRowObject {
    private boolean isAlert     = false;

    private String  name;

    private String  distanceMax = "0";

    private String  distanceRun = "100";

    private String  pathImg;

    private String  firstUsed   = "";

    private String  pourcent    = "0%";

    public TableRowObject(String name) {
      this.name = name;
      try {
        DataEquipement data = EquipementTableManager.getInstance()
            .retreive(name);
        if (data != null) {
          distanceRun = DistanceUnit.format(DistanceUnit.convert(DistanceUnit
              .getDefaultUnit(), DistanceUnit.unitKm(), data.getDistanceAll()));
          distanceMax = DistanceUnit.format(DistanceUnit.convert(DistanceUnit
              .getDefaultUnit(), DistanceUnit.unitKm(), data.getDistanceMax()));
          isAlert = (data.getDistance() >= data.getDistanceMax());
          pathImg = data.getPath();
          if (data.getFirstUsed() != null) {
            firstUsed = LanguageManager.getManager().getCurrentLang()
                .getDateFormatter().format(data.getFirstUsed());
          }

          double p = 0;
          if (data.getDistance() != 0) {
            p = data.getDistance() / data.getDistanceMax();
          }
          NumberFormat nb = NumberFormat.getPercentInstance(LanguageManager
              .getManager().getCurrentLang().getLocale());
          nb.setMaximumFractionDigits(2);
          pourcent = nb.format(p);
        }
      }
      catch (SQLException e) {
        log.error("", e);
        distanceRun = rb.getString("error");
      }
    }

    public String getFirstUsed() {
      return firstUsed;
    }

    public boolean isAlert() {
      return isAlert;
    }

    public String getPathImg() {
      return pathImg;
    }

    public String getDistanceMax() {
      return distanceMax;
    }

    public String getDistanceRun() {
      return distanceRun;
    }

    public String getName() {
      return name;
    }

    public String getPourcent() {
      return pourcent;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class WarnRenderer extends JLabel implements TableCellRenderer {

    private boolean isWarn;

    public WarnRenderer(boolean isWarn) {
      this.isWarn = isWarn;
      setOpaque(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table,
                                                   Object color,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
      setIcon(ImagesRepository.getImageIcon((isWarn) ? "redcross.png"
          : "checkmark.png"));
      return this;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class SelectionListener implements ListSelectionListener {
    private int selectedRow = -1;

    /**
     * 
     */
    public SelectionListener() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel) e.getSource();
      if (!lsm.isSelectionEmpty() && selectedRow != lsm.getMinSelectionIndex()) {
        TableRowObject row = tableModel
            .getRowObject(lsm.getMinSelectionIndex());

        File file = null;
        if (row != null && row.getPathImg() != null) {
          file = new File(row.getPathImg());
          if (!file.isFile()) {
            file = null;
          }
        }

        if (file == null) {
          jLabelPhoto.setIcon(null);
          jLabelPhoto.setText(rb.getString("jLabelPhoto"));
        }
        else {
          try {
            ImageIcon icon = ImageUtil.makeImage(file,
                                                 jLabelPhoto.getWidth(),
                                                 jLabelPhoto.getHeight());
            jLabelPhoto.setIcon(icon);
          }
          catch (IOException ioe) {
            jLabelPhoto.setIcon(new ImageIcon());
          }
        }
      }
    }
  }

  private class JLabelPhoto extends JLabel {

    public JLabelPhoto() {
      super();
    }

    @Override
    public int getHeight() {
      int h = super.getHeight();
      if (h < 10) {
        return getPreferredSize().height;
      }
      return h;
    }

    @Override
    public int getWidth() {
      int w = super.getWidth();
      if (w < 10) {
        return getPreferredSize().width;
      }
      return w;
    }

  }

}
