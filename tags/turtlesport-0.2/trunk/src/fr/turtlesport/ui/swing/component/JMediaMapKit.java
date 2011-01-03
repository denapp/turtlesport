package fr.turtlesport.ui.swing.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.Timer;
import javax.swing.plaf.ProgressBarUI;

import org.jdesktop.swingx.JXPanel;

import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class JMediaMapKit extends JXPanel {

  private JButtonCustom       jButtonPlay;

  private JLabel              JLabelGeoPosition;

  private Timer               timer;

  private JProgressBar        jProgressBarPlay;

  private JProgressBar        jProgressBarSpeed;

  private JLabel              jLabelTime;

  private JLabel              jLabelExtra;

  private TimerActionListener timerActionListener;

  private JTurtleMapKit       mapkit;

  private JLabel              jLabelSpeed1;

  private JLabel              jLabelSpeed2;

  public static ImageIcon     ICON_PLAY           = ImagesDiagramRepository
                                                      .getImageIcon("player_play.png");

  public static ImageIcon     ICON_PLAY_ROLLOVER  = ImagesDiagramRepository
                                                      .getImageIcon("player_play_rollover.png");

  public static ImageIcon     ICON_PAUSE          = ImagesDiagramRepository
                                                      .getImageIcon("player_pause.png");

  public static ImageIcon     ICON_PAUSE_ROLLOVER = ImagesDiagramRepository
                                                      .getImageIcon("player_pause_rollover.png");

  // Model
  private MediaMapKitModel    model;

  /**
   * @param mapkit
   */
  public JMediaMapKit(JTurtleMapKit mapkit) {
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

    setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    setOpaque(false);
    add(jButtonPlay);
    JSeparator separator = new JSeparator(JSeparator.VERTICAL);
    separator.setPreferredSize(new Dimension(2, 22));
    add(separator);
    add(getJProgressBarPlay());
    add(jLabelTime);
    add(jLabelSpeed1);
    add(getJProgressBarSpeed());
    add(jLabelSpeed2);
    add(jLabelExtra);
    add(Box.createRigidArea(new Dimension(50, 10)));
    add(getJLabelGeoPosition());

    // Evenements
    model = new MediaMapKitModel();

    jButtonPlay.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (jButtonPlay.getIcon().equals(ICON_PAUSE)) {
          ModelMapkitManager.getInstance().pause();
        }
        else {
          ModelMapkitManager.getInstance().play();
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
        ModelMapkitManager.getInstance()
            .setMapCurrentPoint(this, value);
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
    if (JLabelGeoPosition == null) {
      JLabelGeoPosition = new JLabel("  ");
      JLabelGeoPosition.setAlignmentX(Component.RIGHT_ALIGNMENT);
      JLabelGeoPosition.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      JLabelGeoPosition.setOpaque(false);
      JLabelGeoPosition.setVisible(false);
    }
    return JLabelGeoPosition;
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

      // geo position
      model.geoPosition(p);

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
      if (jLabelTime.isVisible() && p != null && p.getHeartRate() > 50) {
        st.append("&nbsp;&nbsp;<font color=red>\u2665</font>&nbsp;");
        st.append(p.getHeartRate());
      }
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

    protected void geoPosition(GeoPositionMapKit p) {
      if (mapkit.hasGeoPositionVisible()) {
        getJLabelGeoPosition().setText(GeoUtil.geoPosition(p.getLatitude(),
                                                           p.getLongitude()));
      }
    }

  }

}
