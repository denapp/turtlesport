package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXPanel;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.JDialogRunDetail;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JMediaMapKitCompare extends JXPanel {

  private JButtonCustom        jButtonPlay;

  private JLabel               jLabelGeoPosition;

  private Timer                timer;

  private JProgressBar         jProgressBarPlay;

  private JProgressBar         jProgressBarSpeed;

  private JLabel               jLabelTime;

  private JLabel               jLabelExtra;

  private TimerActionListener  timerActionListener;

  private JTurtleMapKitCompare mapkit;

  private JLabel               jLabelSpeed1;

  private JLabel               jLabelSpeed2;

  public static ImageIcon      ICON_PLAY           = ImagesDiagramRepository
                                                       .getImageIcon("player_play.png");

  public static ImageIcon      ICON_PLAY_ROLLOVER  = ImagesDiagramRepository
                                                       .getImageIcon("player_play_rollover.png");

  public static ImageIcon      ICON_PAUSE          = ImagesDiagramRepository
                                                       .getImageIcon("player_pause.png");

  public static ImageIcon      ICON_PAUSE_ROLLOVER = ImagesDiagramRepository
                                                       .getImageIcon("player_pause_rollover.png");

  // Model
  private MediaMapKitModel     model;

  private JTableCustom         jTable;

  private JScrollPane          jPanelTable;

  private TableModelCompare tableModel;

  /**
   * @param mapkit
   */
  public JMediaMapKitCompare(JTurtleMapKitCompare mapkit) {
    super();
    this.mapkit = mapkit;
    initialize();
  }

  /**
   * Restitue le model
   * 
   * @return le model
   */
  public MediaMapKitModel getModel() {
    return model;
  }

  private void initialize() {
    setLayout(new BorderLayout(5, 5));

    JPanel panelNorth = new JXPanel();
    panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    jButtonPlay = new JButtonCustom();
    jButtonPlay.setIcon(ICON_PLAY);
    jButtonPlay.setRolloverIcon(ICON_PLAY_ROLLOVER);

    Dimension dimButton = new Dimension(24, 24);
    jButtonPlay.setMaximumSize(dimButton);
    jButtonPlay.setMinimumSize(dimButton);
    jButtonPlay.setPreferredSize(dimButton);
    jButtonPlay.setBorderPainted(false);
    jButtonPlay.setContentAreaFilled(false);
    jButtonPlay.setOpaque(false);

    jLabelSpeed1 = new JLabel(ImagesDiagramRepository.getImageIcon("turtle.png"));
    jLabelSpeed1.setVisible(true);
    jLabelSpeed2 = new JLabel(ImagesDiagramRepository.getImageIcon("rabbit.png"));
    jLabelSpeed2.setVisible(true);

    jLabelTime = new JLabel(" ");
    jLabelTime.setVisible(false);
    jLabelTime.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
    jLabelTime.setAlignmentX(Component.LEFT_ALIGNMENT);
    jLabelExtra = new JLabel(" ");
    jLabelExtra.setAlignmentX(Component.LEFT_ALIGNMENT);
    jLabelExtra.setVisible(true);
    jLabelExtra.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);

    setOpaque(false);

    panelNorth.add(jButtonPlay);
    JSeparator separator = new JSeparator(JSeparator.VERTICAL);
    separator.setPreferredSize(new Dimension(2, 22));
    panelNorth.add(separator);
    panelNorth.add(getJProgressBarPlay());
    panelNorth.add(jLabelTime);
    panelNorth.add(jLabelSpeed1);
    panelNorth.add(getJProgressBarSpeed());
    panelNorth.add(jLabelSpeed2);
    panelNorth.add(jLabelExtra);
    panelNorth.add(Box.createRigidArea(new Dimension(50, 10)));
    panelNorth.add(getJLabelGeoPosition());

    add(panelNorth, BorderLayout.NORTH);
    add(getJPanelTable(), BorderLayout.CENTER);

    // Evenements
    model = new MediaMapKitModel();

    jButtonPlay.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (jButtonPlay.getIcon().equals(ICON_PAUSE)) {
          stopTimer();
        }
        else {
          startTimer();
        }
      }

    });

    jProgressBarPlay.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        doWork(e);
      }

      public void mouseReleased(MouseEvent e) {
        doWork(e);
      }

      private void doWork(MouseEvent e) {
        int x = e.getPoint().x;
        int width = jProgressBarPlay.getSize().width;

        int value = (int) (((1.0 * x) / width) * jProgressBarPlay.getMaximum());
        ModelMapkitManager.getInstance().setMapCurrentPoint(this, value);
      }

    });

    jProgressBarSpeed.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        doWork(e);
      }

      public void mouseReleased(MouseEvent e) {
        doWork(e);
      }

      private void doWork(MouseEvent e) {
        int x = e.getPoint().x;
        int width = jProgressBarSpeed.getSize().width;

        int value = (int) (((1.0 * x) / width) * jProgressBarSpeed.getMaximum());
        ModelMapkitManager.getInstance().setSpeed(value);
      }

    });

    jLabelSpeed1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        ModelMapkitManager.getInstance().setSpeed(ModelMapkitManager
            .getInstance().getSpeed() - 5);
      }
    });
    jLabelSpeed2.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if ((ModelMapkitManager.getInstance().getSpeed() + 5) <= jProgressBarSpeed
            .getMaximum()) {
          ModelMapkitManager.getInstance().setSpeed(ModelMapkitManager
              .getInstance().getSpeed() + 5);
        }
      }
    });

    // ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    // executor.scheduleAtFixedRate(timerActionListener,
    // 0,
    // 200,
    // TimeUnit.MILLISECONDS);

    timerActionListener = new TimerActionListener();
    timer = new Timer(300, timerActionListener);
  }

  private JScrollPane getJPanelTable() {
    if (jPanelTable == null) {
      jPanelTable = new JScrollPane();
      Dimension dim = new Dimension(280, 100);
      jPanelTable.setPreferredSize(dim);
      jPanelTable.setMinimumSize(dim);
      jPanelTable.setOpaque(true);
      jPanelTable.setViewportView(getJTable());
    }
    return jPanelTable;
  }

  /**
   * This method initializes jTableLap
   * 
   * @return javax.swing.JTable
   */
  private JTable getJTable() {
    if (jTable == null) {
      jTable = new JTableCustom();
      jTable.setDragEnabled(true);
      tableModel = new TableModelCompare();
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

  /**
   * Mise &agrave; jour de la barre de progression.
   * 
   * @param value
   *          l'index du point courant.
   * @param p
   *          le point.
   */
  protected void firePogressBarPlayUpdate(int value, GeoPositionMapKit p) {
    timerActionListener.firePogressBarPlayUpdate(value, p);
  }

  private JProgressBar getJProgressBarPlay() {
    if (jProgressBarPlay == null) {
      jProgressBarPlay = new JProgressBarTurtle(JProgressBar.HORIZONTAL, 0, 50);
      jProgressBarPlay.setUI(new ProgressBarTurtleUI());
      jProgressBarPlay.setOpaque(true);
      jProgressBarPlay.setForeground(Color.BLUE);
      jProgressBarPlay.setBackground(Color.WHITE);
      jProgressBarPlay.setBorder(BorderFactory
          .createLineBorder(Color.lightGray, 2));
      Dimension d = new Dimension(75, 12);
      jProgressBarPlay.setPreferredSize(d);
      jProgressBarPlay.setMaximumSize(d);
    }
    return jProgressBarPlay;
  }

  public JProgressBar getJProgressBarSpeed() {
    if (jProgressBarSpeed == null) {
      jProgressBarSpeed = new JProgressBarTurtle(JProgressBar.HORIZONTAL,
                                                 0,
                                                 100);
      ProgressBarCellTurtleUI ui = new ProgressBarCellTurtleUI();
      jProgressBarSpeed.setUI(ui);
      jProgressBarSpeed.setOpaque(true);
      jProgressBarSpeed.setForeground(Color.BLUE);
      jProgressBarSpeed.setBackground(Color.WHITE);
      jProgressBarSpeed.setBorder(BorderFactory
          .createLineBorder(Color.lightGray, 2));
      Dimension d = new Dimension(50, 12);
      jProgressBarSpeed.setPreferredSize(d);
      jProgressBarSpeed.setMaximumSize(d);
    }
    return jProgressBarSpeed;
  }

  /**
   * @return the jLabelGeoPosition
   */
  protected JLabel getJLabelGeoPosition() {
    if (jLabelGeoPosition == null) {
      jLabelGeoPosition = new JLabel("  ");
      jLabelGeoPosition.setAlignmentX(Component.RIGHT_ALIGNMENT);
      jLabelGeoPosition.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      jLabelGeoPosition.setOpaque(false);
      jLabelGeoPosition.setVisible(false);
    }
    return jLabelGeoPosition;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class JProgressBarTurtle extends JProgressBar {
    public JProgressBarTurtle(int orient, int min, int max) {
      super(orient, min, max);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JProgressBar#updateUI()
     */
    @Override
    public void updateUI() {
      setUI((ProgressBarUI) getUI());
    }

  }

  public void startTimer() {
    if (jProgressBarPlay.getValue() == jProgressBarPlay.getMaximum()) {
      timerActionListener.init();
      ModelMapkitManager.getInstance().beginPoint(this);
    }
    timer.start();
    jButtonPlay.setIcon(ICON_PAUSE);
    jButtonPlay.setRolloverIcon(ICON_PAUSE_ROLLOVER);
  }

  public void stopTimer() {
    timer.stop();
    jButtonPlay.setIcon(ICON_PLAY);
    jButtonPlay.setRolloverIcon(ICON_PLAY_ROLLOVER);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TimerActionListener implements ActionListener {
    ModelMapkitManager modelMap = ModelMapkitManager.getInstance();

    public void actionPerformed(ActionEvent e) {
      modelMap.nextPoint(this);
    }

    public void init() {
      jButtonPlay.setIcon(ICON_PAUSE);
      jButtonPlay.setRolloverIcon(ICON_PAUSE_ROLLOVER);
      jProgressBarPlay.setValue(0);
    }

    /**
     * Update la barre de progression.
     * 
     * @param value
     * @param p
     */
    protected void firePogressBarPlayUpdate(int value, GeoPositionMapKit p) {
      if (p == null || value >= (jProgressBarPlay.getMaximum() - 1)) {
        value = jProgressBarPlay.getMaximum() - 1;

        // time
        if (jLabelTime.isVisible()) {
          model.timeEnd();
        }
      }
      else {
        // time
        if (jLabelTime.isVisible()) {
          model.time(p);
        }
      }
      jProgressBarPlay.setValue(value + 1);

      // time
      model.extra(p);
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  protected class MediaMapKitModel {
    private String timeTot;

    /**
     * @return the timeTot
     */
    public String getTimeTot() {
      return timeTot;
    }

    /**
     * @param timeTot
     *          the timeTot to set
     */
    public void setTimeTot(String timeTot) {
      this.timeTot = timeTot;
    }

    /**
     * Mise &agrave; jour du nombre de points.
     * 
     * @param size
     *          le nombre de points.
     */
    public void setMaximum(int size) {
      getJProgressBarPlay().setMaximum(size);
      getJProgressBarPlay().setValue(0);
      if (timerActionListener != null) {
        timerActionListener.init();
      }
      stopTimer();
    }

    /**
     * Rend le temps visible.
     * 
     * @param b
     */
    public void setTimeVisible(boolean b) {
      jLabelTime.setVisible(b);
    }

    protected void extra(GeoPositionMapKit p) {

      StringBuilder st = new StringBuilder();

      // geoposition
      st = new StringBuilder();
      st.append("<html><body>");
      double dist = (p == null) ? 0 : p.getDistance();
      if (p != null && !DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
        dist = DistanceUnit.convert(DistanceUnit.unitKm(),
                                    DistanceUnit.getDefaultUnit(),
                                    dist);
      }
      st.append(DistanceUnit.formatWithUnit(dist));
      jLabelExtra.setText(st.toString());
      st.append("</body></html>");
    }

    protected void time(GeoPositionMapKit p) {
      if (!jLabelTime.isVisible()) {
        return;
      }

      StringBuilder st = new StringBuilder();

      // time
      st.append("<html><body>");
      st.append((p == null) ? "00:00" : ModelMapkitManager.getInstance()
          .currentTime());
      st.append('/');
      st.append(model.getTimeTot());
      jLabelTime.setText(st.toString());
    }

    protected void timeEnd() {
      StringBuilder st = new StringBuilder();
      st.append("<html><body>");
      st.append(model.getTimeTot());
      st.append('/');
      st.append(model.getTimeTot());
      jLabelTime.setText(st.toString());
    }

  }

  private class TableModelCompare extends AbstractTableModel {

    private String[]         columnNames   = { "Distance (km)", "Temps", };

    private final Class<?>[] columnClasses = { String.class, String.class };

    private final int[]      columWidth    = { 70, 70 };

    private ResourceBundle   rb            = ResourceBundleUtility
                                               .getBundle(LanguageManager
                                                              .getManager()
                                                              .getCurrentLang(),
                                                          JDialogRunDetail.class);

    public TableModelCompare() {

      for (int i = 0; i < columnNames.length; i++) {
        if (i == 0) {
          // Distance
          columnNames[i] = initColumn(DistanceUnit.getDefaultUnit(), 0);

        }
        else {
          columnNames[i] = rb.getString("TableModel_header2");
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
      return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     * int, int)
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
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
  }

}
