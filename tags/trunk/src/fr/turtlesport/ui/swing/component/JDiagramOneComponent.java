package fr.turtlesport.ui.swing.component;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.filter.SavitzkyGolay;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.model.ChangeMapEvent;
import fr.turtlesport.ui.swing.model.ChangeMapListener;
import fr.turtlesport.ui.swing.model.ChangePointsEvent;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDiagramOneComponent extends JPanel {
  private BufferedImage               bimg;

  private Toolkit                     toolkit;

  private int                         biw, bih;

  private boolean                     clearOnce;

  // Mouse position
  public int                          mouseX         = 0;

  private long                        currentTime;

  private double                      currentDistance;

  private double                      currentY1;

  private int                         tabMouseY;

  // gui
  private static final int            WIDTH_TITLE_1  = 60;

  protected static final int          WIDTH_TITLE_2  = 25;

  private static final int            HEIGHT_TITLE_1 = 10;

  private static final int            HEIGHT_TITLE_2 = 20;

  private static final int            PAD            = 5;

  private static final Color          COLOR_LINE     = new Color(0xe1,
                                                                 0xe1,
                                                                 0xe1);

  protected static final Color        COLOR_TIME     = new Color(99, 86, 136);

  private static final AlphaComposite AC_TRANSPARENT = AlphaComposite
                                                         .getInstance(AlphaComposite.SRC_OVER,
                                                                      0.2f);

  /** Model. */
  protected TablePointsModel          model;

  private MyMouseMotionListener       mouseMotionListener;

  public static final int             HEART          = 1;

  public static final int             SPEED          = 2;

  public static final int             PACE           = 3;

  public static final int             ALTITUDE       = 4;

  private int                         type;

  private Color                       colorY;

  private int                         currentIndex;

  private List<JDiagramOneComponent>  listDiagrams;

  /**
   * 
   */
  public JDiagramOneComponent(int type) {
    super();

    listDiagrams = new ArrayList<JDiagramOneComponent>();
    switch (type) {
      case HEART:
        colorY = JDiagramComponent.COLORY1;
        break;

      case ALTITUDE:
        colorY = JDiagramComponent.COLORY2;
        break;

      case SPEED:
        colorY = JDiagramComponent.COLORY3;
        break;

      case PACE:
        colorY = JDiagramComponent.COLORY3;
        break;

      default:
        throw new IllegalArgumentException();
    }

    this.type = type;
    model = new TablePointsModel();
    initialize();
  }

  public void addDIagram(JDiagramOneComponent d) {
    listDiagrams.add(d);
  }

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility
        .getBundle(lang, JDiagramComponent.class);
    model.setUnitX(MessageFormat.format(rb.getString("unitX"),
                                        DistanceUnit.getDefaultUnit()));
  }

  public TablePointsModel getModel() {
    return model;
  }

  /**
   * Coordonnees max des points.
   */
  private void initialize() {
    toolkit = getToolkit();
    setDoubleBuffered(true);
    setIgnoreRepaint(true);

    ModelMapkitManager.getInstance().addChangeListener(model);

    mouseMotionListener = new MyMouseMotionListener();
    addMouseMotionListener(mouseMotionListener);

    addMouseListener(new MyMouseListener());
    addMouseWheelListener(new MyMouseWheelListener());

    performedLanguage(LanguageManager.getManager().getCurrentLang());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paintImmediately(int, int, int, int)
   */
  public void paintImmediately(int x, int y, int w, int h) {
    RepaintManager repaintManager = null;
    boolean save = true;
    if (!isDoubleBuffered()) {
      repaintManager = RepaintManager.currentManager(this);
      save = repaintManager.isDoubleBufferingEnabled();
      repaintManager.setDoubleBufferingEnabled(false);
    }
    super.paintImmediately(x, y, w, h);

    if (repaintManager != null) {
      repaintManager.setDoubleBufferingEnabled(save);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paint(java.awt.Graphics)
   */
  @Override
  public void paint(Graphics g) {
    Dimension d = getSize();

    if (bimg == null || biw != d.width || bih != d.height) {
      bimg = (BufferedImage) ((Graphics2D) g).getDeviceConfiguration()
          .createCompatibleImage(d.width, d.height);
      biw = d.width;
      bih = d.height;
      clearOnce = true;
    }

    Graphics2D g2 = createGraphics2D(d.width, d.height, bimg, g);
    render(d.width, d.height, g2);
    g2.dispose();

    if (bimg != null) {
      g.drawImage(bimg, 0, 0, null);
      toolkit.sync();
    }
  }

  private Graphics2D createGraphics2D(int width,
                                      int height,
                                      BufferedImage bi,
                                      Graphics g) {

    Graphics2D g2 = null;

    if (bi != null) {
      g2 = bi.createGraphics();
    }
    else {
      g2 = (Graphics2D) g;
    }

    g2.setBackground(getBackground());

    if (clearOnce) {
      g2.clearRect(0, 0, width, height);
      clearOnce = false;
    }

    return g2;
  }

  private void render(int w, int h, Graphics2D g2) {
    paintGrid(g2);
    if (model != null && model.indexX2 != 0) {
      paintPoints(g2);
      paintInterval(g2);
      if (model.hasMouseMotionListener()) {
        paintExtra(g2);
      }
      else {
        paintPoint(g2);
      }
    }
    paintYAxis(g2);

  }

  /**
   * Afficher le repere orthonorme.
   */
  private void paintGrid(Graphics2D g2) {
    int x = 0, y;
    double yText;
    String text;
    int tot;
    int lenText;

    g2.setColor(getParent().getBackground());
    g2.fillRect(0, 0, getWidth(), getHeight());

    g2.setColor(Color.white);
    g2.fillRect(WIDTH_TITLE_1,
                HEIGHT_TITLE_1,
                getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2,
                getHeight() - HEIGHT_TITLE_1 - HEIGHT_TITLE_2);

    g2.setColor(getParent().getBackground());

    // Font
    // --------
    FontMetrics metrics = g2.getFontMetrics(GuiFont.FONT_PLAIN_SMALL);
    int highText = metrics.getAscent() - metrics.getDescent();
    g2.setFont(GuiFont.FONT_PLAIN_SMALL);

    // Axe // a Y
    // -----------------
    tot = getHeight() - HEIGHT_TITLE_2;
    yText = tot + PAD + highText + 6;
    g2.setColor(Color.BLACK);
    for (int i = 0; i < model.getGridX().length; i++) {
      // axe // a Y
      x = computeRelativeX(model.getGridX()[i]);

      if (i != 0) {
        g2.setColor((i % 2 == 0) ? Color.LIGHT_GRAY : COLOR_LINE);
      }
      g2.drawLine(x, HEIGHT_TITLE_1, x, tot);
      g2.setColor(Color.BLACK);
      g2.drawLine(x, tot, x, tot + PAD);

      // texte
      text = model.getGridXText(i);
      lenText = g2.getFontMetrics(GuiFont.FONT_PLAIN_SMALL).stringWidth(text);
      if (i % 2 == 0) {
        g2.drawString(text, x - lenText / 2, (int) yText);
      }
    }

    // Axe // a X
    // -----------------
    tot = getWidth() - WIDTH_TITLE_2;
    // double gridy1 = -1;
    for (int i = 0; i < model.getGridY().length; i++) {

      // axe // a X
      y = computeRelativeY(model.getGridY()[i]);
      if (i != 0) {
        g2.setColor((i % 2 == 0) ? Color.LIGHT_GRAY : COLOR_LINE);
      }
      g2.drawLine(WIDTH_TITLE_1, y, tot, y);
      g2.setColor(Color.BLACK);

      if (i % 2 == 0 || (i == model.getGridY().length - 1)) {
        // texte Freq cardiaque
        if (type == HEART || type == ALTITUDE) {
          text = Integer.toString((int) model.getGridY()[i]);
        }
        else {
          text = SpeedUnit.format(model.getGridY()[i]);
        }
        lenText = metrics.stringWidth(text);
        x = WIDTH_TITLE_1 - PAD - lenText - 20;
        x =5;
        g2.setColor(colorY);
        g2.drawString(text, x, y + (highText / 2));
      }
      g2.setColor(Color.LIGHT_GRAY);
    }

  }

  private void paintYAxis(Graphics2D g2) {
    int x = 0, y;
    int tot = getHeight() - HEIGHT_TITLE_2;

    // Axe Y
    g2.setColor(colorY);
    double gridx = model.getGridX()[0];
    x = computeRelativeX(gridx);
    g2.drawLine(x, HEIGHT_TITLE_1, x, tot);
    g2.setColor(Color.BLACK);
    g2.drawLine(x, tot, x, tot + PAD);

    // Axe // a X
    // -----------------
    tot = getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2;
    for (int i = 0; i < model.getGridY().length; i++) {
      // Axe Y
      y = computeRelativeY(model.getGridY()[i]);
      g2.setColor(colorY);
      g2.drawLine(WIDTH_TITLE_1 - PAD, y, WIDTH_TITLE_1, y);
    }

  }

  /**
   * Affiche la courbe.
   */
  private void paintPoints(Graphics2D g2) {
    if (model.indexX2 < 1) {
      return;
    }

    int x1, x2 = 0, y1, y2;

    int tot = getHeight() - HEIGHT_TITLE_2;
    Polygon pol = new Polygon();
    pol.addPoint(computeRelativeX(model.getX(model.indexX1)), tot);

    for (int i = model.indexX1; i < model.indexX2 - 1; i++) {
      x1 = computeRelativeX(model.getX(i));
      x2 = computeRelativeX(model.getX(i + 1));

      g2.setColor(colorY);
      y1 = computeRelativeY(model.getY(i));
      y2 = computeRelativeY(model.getY(i + 1));
      g2.drawLine(x1, y1, x2, y2);
      g2.drawLine(x1, y1 + 1, x2, y2 + 1);
      g2.drawLine(x1, y1 - 1, x2, y2 - 1);
      // remplissage
      pol.addPoint(x1, y1);
      pol.addPoint(x2, y2);
    }

    g2.setColor(colorY);
    pol.addPoint(x2, tot);
    g2.setComposite(AC_TRANSPARENT);
    g2.fillPolygon(pol);
    g2.setComposite(AlphaComposite.SrcOver);
  }

  /**
   * Affiche l'intervalle.
   */
  private void paintInterval(Graphics2D g2) {
    if (model.getIntervalX1() != -1
        && (model.getIntervalX1() >= model.getDistance(model.indexX1))
        && (model.getIntervalX2() <= model.getDistance(model.indexX2 - 1))) {
      int intX1 = computeRelativeXDistance(model.getIntervalX1() / 1000.0);
      int intX2 = computeRelativeXDistance(model.getIntervalX2() / 1000.0);

      int tot = getHeight() - HEIGHT_TITLE_2 - HEIGHT_TITLE_1;
      int pad = 1;

      g2.setColor(Color.orange);
      g2.fillRect(intX1, HEIGHT_TITLE_1, pad, tot);
      g2.fillRect(intX2, HEIGHT_TITLE_1, pad, tot);

      g2.setComposite(AC_TRANSPARENT);
      tot = getHeight() - HEIGHT_TITLE_2 - HEIGHT_TITLE_1;
      g2.fillRect(intX1 + pad, HEIGHT_TITLE_1, intX2 - intX1 - pad, tot);
      g2.setComposite(AlphaComposite.SrcOver);
    }

  }

  /**
   * Calcule la coordonnee relative y.
   */
  private int computeRelativeY(double y) {

    return (int) (getHeight() - HEIGHT_TITLE_2 - (getHeight() - HEIGHT_TITLE_1 - HEIGHT_TITLE_2)
                                                 * (y - model.getGridYMin())
                                                 / (model.getGridYMax() - model
                                                     .getGridYMin()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private int computeRelativeX(double x) {
    return (int) (WIDTH_TITLE_1 + (getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2)
                                  * (x - model.getGridXMin())
                                  / (model.getGridXMax() - model.getGridXMin()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private int computeRelativeXDistance(double x) {
    return (int) (WIDTH_TITLE_1 + (getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2)
                                  * (x - model.getGridXMinDist())
                                  / (model.getGridXMaxDist() - model
                                      .getGridXMinDist()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private double invComputeRelativeX(int x) {
    return (1.0 * (x - WIDTH_TITLE_1)
            * (model.getGridXMax() - model.getGridXMin()) / (getWidth()
                                                             - WIDTH_TITLE_1 - WIDTH_TITLE_2))
           + model.getGridXMin();
  }

  /**
   * Affiche les positions.
   */
  private void paintExtra(Graphics2D g2) {
    if (mouseX < WIDTH_TITLE_1
        || mouseX > getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2) {
      // on remet le point a zero pour la map
      ModelPointsManager.getInstance().setMapCurrentPoint(model, 0);
      return;
    }

    // recuperation des position de la souris
    findMousePoint(mouseX);
    paintY(g2);
  }

  private void paintY(Graphics2D g2) {
    FontMetrics metrics;
    int xg, yg, lenText, highText;
    String st;

    metrics = g2.getFontMetrics(GuiFont.FONT_PLAIN_SMALL);
    highText = (metrics.getAscent() - metrics.getDescent()) / 2;

    // Le temps ecoule
    // -----------------------------------------------------------
    g2.setColor(COLOR_TIME);

    // Dessine le triangle
    int[] tabx = { mouseX - 5, mouseX, mouseX + 5 };
    int y = HEIGHT_TITLE_1;
    int[] taby = { y + 5, y, y + 5 };
    g2.fillPolygon(tabx, taby, 3);

    // Dessine l'axe
    int tot = getHeight() - HEIGHT_TITLE_2 - HEIGHT_TITLE_1;
    g2.fillRect(mouseX, HEIGHT_TITLE_1, 1, tot);

    // Dessine la valeur sur l'axe des x
    st = model.isAxisXDistance() ? model.getCurrentXTime() : model
        .getCurrentXText() + " " + DistanceUnit.getDefaultUnit();
    lenText = metrics.stringWidth(st);
    g2.drawString(st, mouseX - lenText / 2, HEIGHT_TITLE_1 - 2);

    // Valeur axe des X
    // ----------------------------------------------------------
    g2.setColor(Color.DARK_GRAY);

    // Dessine triangle sur axe xgetCurrentXTime
    drawTriangleX(g2, mouseX);

    // Dessine la valeur sur l'axe des x
    yg = getHeight() - HEIGHT_TITLE_2 + 10;
    st = model.isAxisXDistance() ? model.getCurrentXText() : model
        .getCurrentXTime();

    lenText = metrics.stringWidth(st);
    g2.drawString(st, mouseX - lenText / 2, yg);

    // Courbe Y1
    // ----------------------------------------------------------
    if (currentY1 > model.getGridYMin()) {
      g2.setColor(colorY);

      // dessine la souris
      g2.drawRect(mouseX - 2, tabMouseY - 2, 4, 4);

      // Dessine triangle sur axe y
      drawTriangleY1(g2, tabMouseY);

      // Dessine la valeur sur l'axe des y
      st = Integer.toString((int) currentY1);
      lenText = metrics.stringWidth(st);
      xg = WIDTH_TITLE_1 - lenText - 2;
      g2.drawString(st, xg, tabMouseY + highText);
    }

  }

  /**
   * Affiche les positions.
   */
  private void paintPoint(Graphics2D g2) {
    if (mouseX < WIDTH_TITLE_1
        || mouseX > getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2) {
      return;
    }

    paintY(g2);
  }

  /**
   * Dessine le triangle sur l'axe des X.
   */
  private void drawTriangleX(Graphics2D g2, int x) {
    int[] tabx = { x - 5, x, x + 5 };
    int y = -HEIGHT_TITLE_2 + getHeight() - 5;
    int[] taby = { y, y + 5, y };
    g2.fillPolygon(tabx, taby, 3);
  }

  /**
   * Dessine le triangle sur l'axe des Y courbe 1.
   */
  private void drawTriangleY1(Graphics2D g2, int y) {
    int[] tabx = { WIDTH_TITLE_1 + 5, WIDTH_TITLE_1, WIDTH_TITLE_1 + 5 };
    int[] taby = { y - 5, y, y + 5 };
    g2.fillPolygon(tabx, taby, 3);
  }

  /**
   * Fonction recherche du point.
   */
  private void findMousePoint(int x0) {
    double near = Double.MAX_VALUE;
    double nearCur;

    currentIndex = model.indexX2 - 1;
    currentY1 = model.getY(currentIndex);
    double xInv = invComputeRelativeX(x0);

    for (int i = model.indexX1; i < model.indexX2 - 1; i++) {
      nearCur = Math.abs(xInv - model.getX(i));
      if (nearCur == 0) {
        currentIndex = i;
        break;
      }
      else if (near > nearCur) {
        near = nearCur;
        currentIndex = i;
      }
    }

    currentTime = model.getTime(currentIndex);
    currentDistance = model.getDistanceX(currentIndex);
    currentY1 = model.getY(currentIndex);

    tabMouseY = computeRelativeY(currentY1);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TablePointsModel implements ChangeMapListener {

    private DataRunTrk[]     pointsFilter;

    private List<DataRunTrk> points;

    private double           minX;

    private double           maxX;

    private double           minY;

    private double           maxY;

    private double           maxYSpeed;

    private double           maxYPace;

    private double           minYSpeed;

    private double           minYPace;

    private double[]         gridY                  = { 50,
                                                        75,
                                                        100,
                                                        125,
                                                        150,
                                                        175,
                                                        200,
                                                        220 };

    private double[]         gridXDistance;

    private double[]         gridXTime;

    private double[]         gridYSpeed;

    private double[]         gridYPace;

    private String           unitX                  = "Distance (km)";

    private int              intervalX1             = -1;

    private int              intervalX2             = -1;

    private boolean          bZoom                  = true;

    private int              indexX1;

    private int              indexX2;

    private int              currentZoom            = 0;

    private int              maxZoom;

    private boolean          hasMouseMotionListener = true;

    private boolean          isAxisXDistance;

    private boolean          isFilter;

    private int              visibleY3;

    private DecimalFormat    dfAxisXDistance        = new DecimalFormat("#.#");

    private DecimalFormat    dfDistance             = new DecimalFormat("#.###");

    /**
     * 
     */
    public TablePointsModel() {
      isFilter = Configuration.getConfig().getPropertyAsBoolean("Diagram",
                                                                "isFilter",
                                                                false);
      isAxisXDistance = Configuration.getConfig()
          .getPropertyAsBoolean("Diagram", "isAxisXDistance", true);

      visibleY3 = Configuration.getConfig().getPropertyAsInt("Diagram",
                                                             "isVisibleY3",
                                                             1);

      initialize();
      points = ModelPointsManager.getInstance().getListTrks();
      fireChangedAllPoints();
    }

    public void setFilter(boolean isFilter) {
      if (this.isFilter != isFilter) {
        this.isFilter = isFilter;
        Configuration.getConfig().addProperty("Diagram",
                                              "isFilter",
                                              Boolean.toString(isFilter));
        if (isFilter && points != null && pointsFilter == null) {
          applyFilterSavitzyGolay();
        }
        changeVisible();
      }
    }

    public void setAxisX(boolean isAxisXDistance) {
      if (this.isAxisXDistance != isAxisXDistance) {
        this.isAxisXDistance = isAxisXDistance;
        changeVisible();
      }
    }

    public boolean isFilter() {
      return isFilter;
    }

    public boolean isAxisXDistance() {
      return isAxisXDistance;
    }

    private void initialize() {
      gridXDistance = new double[17];
      gridXTime = new double[17];
      for (int i = 0, value = 0; i < gridXDistance.length; i++, value += 2) {
        gridXDistance[i] = value;
        gridXTime[i] = i * 225 * 2 * 1000;
      }
      pointsFilter = null;
      currentZoom = 0;
    }

    public boolean hasMouseMotionListener() {
      return hasMouseMotionListener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedAllPoints
     * (fr.turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedAllPoints(ChangePointsEvent e) {
      if (points != null && points.equals(e.getListTrks())) {
        return;
      }
      points = e.getListTrks();
      JDiagramOneComponent.this.removeMouseMotionListener(mouseMotionListener);
      JDiagramOneComponent.this.addMouseMotionListener(mouseMotionListener);

      setMouseX(0);
      fireChangedAllPoints();
    }

    /*
     * (non-Javadoc)
     * 
     * @seefr.turtlesport.ui.swing.component.ChangePointsListener#changedLap(fr.
     * turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedLap(ChangePointsEvent e) {

      DataRunLap[] runLaps = ModelPointsManager.getInstance().getRunLaps();
      int index = ModelPointsManager.getInstance().getLapIndex();
      if (runLaps != null && index != -1) {
        double[] inter = new double[2];

        inter[0] = 0;
        for (int i = 0; i < index; i++) {
          inter[0] += runLaps[i].getTotalDist();
        }
        inter[1] = inter[0] + runLaps[index].getTotalDist();
        this.intervalX1 = (int) inter[0];
        this.intervalX2 = (int) inter[1];
        revalidate();
        repaint();
      }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedPoint(fr.
     * turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedPoint(ChangePointsEvent e) {
      /*
       * if (e.hasPoints() && model != null) { int index =
       * e.getTrkIndexCurrentPoint(); if (e.isCurrentLastPoint()) { mouseX =
       * computeRelativeX(model.getGridXMax()); revalidate(); repaint(); } else
       * { mouseX = computeRelativeX(model.getX(index));
       * 
       * currentTime = getTime(index); currentDistance = getX(index); currentY1
       * = getY(index);
       * 
       * tabMouseY = computeRelativeY(currentY1); revalidate(); repaint(); } }
       */
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangeMapListener#changedMap(fr.turtlesport
     * .ui.swing.component.ChangeMapEvent)
     */
    public void changedMap(ChangeMapEvent changeEvent) {
    }

    /*
     * (non-Javadoc)
     * 
     * @seefr.turtlesport.ui.swing.component.ChangeMapListener#changedPlay(fr.
     * turtlesport.ui.swing.component.ChangeMapEvent)
     */
    public void changedPlay(ChangeMapEvent e) {
      JDiagramOneComponent.this.removeMouseMotionListener(mouseMotionListener);
      hasMouseMotionListener = !e.isRunning();
      if (hasMouseMotionListener) {
        JDiagramOneComponent.this.addMouseMotionListener(mouseMotionListener);
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @seefr.turtlesport.ui.swing.component.ChangeMapListener#changedSpeed(fr.
     * turtlesport.ui.swing.component.ChangeMapEvent)
     */
    public void changedSpeed(ChangeMapEvent e) {
    }

    public void changeVisible() {
      revalidate();
      repaint();
    }

    protected double getX(int i) {
      return (isAxisXDistance) ? points.get(i).getDistance() / 1000
          : points.get(i).getTime().getTime()
            - points.get(0).getTime().getTime();
    }

    protected double getDistanceX(int i) {
      return points.get(i).getDistance() / 1000;
    }

    protected String getCurrentXText() {
      return dfDistance.format(currentDistance);
    }

    protected String getCurrentXTime() {
      return TimeUnit.formatMilliSecondeTime(currentTime);
    }

    protected long getTime(int i) {
      return points.get(i).getTime().getTime()
             - points.get(0).getTime().getTime();
    }

    protected int getDistance(int i) {
      return (int) points.get(i).getDistance();
    }

    protected double getY(int i) {
      switch (type) {
        case HEART:
          return (model.isFilter()) ? pointsFilter[i].getHeartRate() : points
              .get(i).getHeartRate();
        case SPEED:
          return isVisibleSpeed() ? points.get(i).getSpeed() : points.get(i)
              .getPace();
        case ALTITUDE:
          return (model.isFilter()) ? pointsFilter[i].getAltitude() : points
              .get(i).getAltitude();
        default:
          return 0;
      }
    }

    public void setVisibleY3(int visibleY3) {
      if (this.visibleY3 != visibleY3) {
        this.visibleY3 = visibleY3;
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleY3",
                                              Integer.toString(visibleY3));

        gridY = isVisibleSpeed() ? gridYSpeed : gridYPace;
        maxY = isVisibleSpeed() ? maxYSpeed : maxYPace;
        minY = isVisibleSpeed() ? minYSpeed : minYPace;
        changeVisible();
      }
    }

    public boolean isVisibleSpeed() {
      return (visibleY3 == 0);
    }

    public boolean isVisiblePace() {
      return (visibleY3 == 1);
    }

    protected double[] getGridY() {
      return gridY;
    }

    protected String getUnitX() {
      return unitX;
    }

    protected void setUnitX(String val) {
      unitX = val;
    }

    protected double[] getGridX() {
      return (isAxisXDistance) ? gridXDistance : gridXTime;
    }

    protected String getGridXText(int index) {
      return (isAxisXDistance) ? dfAxisXDistance.format(gridXDistance[index])
          : TimeUnit.formatMilliSecondeTime((long) gridXTime[index]);
    }

    DecimalFormat df = new DecimalFormat("#.#");

    protected double getGridXMin() {
      return (isAxisXDistance) ? gridXDistance[0] : gridXTime[0];
    }

    protected double getGridXMax() {
      return (isAxisXDistance) ? gridXDistance[gridXDistance.length - 1]
          : gridXTime[gridXTime.length - 1];
    }

    protected double getGridXMinDist() {
      return gridXDistance[0];
    }

    protected double getGridXMaxDist() {
      return gridXDistance[gridXDistance.length - 1];
    }

    protected double getGridYMin() {
      return gridY[0];
    }

    protected double getGridYMax() {
      return gridY[gridY.length - 1];
    }

    public int getIntervalX1() {
      return intervalX1;
    }

    public int getIntervalX2() {
      return intervalX2;
    }

    /**
     * Mis a jour des donn&eacute;es.
     * 
     * @param points
     */
    private void fireChangedAllPoints() {
      intervalX1 = -1;
      intervalX2 = -1;
      pointsFilter = null;
      indexX2 = 0;

      if (points == null || points.size() == 0) {
        initialize();
        revalidate();
        repaint();
        return;
      }

      if (points != null) {
        indexX1 = 0;
        indexX2 = points.size();

        minX = Double.MAX_VALUE;
        maxX = 0;
        maxY = 0;
        minY = Double.MAX_VALUE;
        maxY = 0;
        minYSpeed = Double.MAX_VALUE;
        maxYSpeed = 0;
        minYPace = Double.MAX_VALUE;
        maxYPace = 0;

        if (type == SPEED) {
          computeSpeedPace();
        }

        // recuperation des points
        DataRunTrk pPrev = points.get(0);
        for (DataRunTrk p : points) {
          if (p.getTime().before(pPrev.getTime())) {
            p.setTime(pPrev.getTime());
          }
          pPrev = p;

          if (p.getDistance() < minX) {
            minX = p.getDistance();
          }
          if (p.getDistance() > maxX) {
            maxX = p.getDistance();
          }

          switch (type) {
            case HEART:
              if (p.getHeartRate() < gridY[0]) {
                p.setHeartRate((int) gridY[0]);
              }
              else if (p.getHeartRate() > gridY[gridY.length - 1]) {
                p.setHeartRate((int) gridY[gridY.length - 1]);
              }
              if (p.getHeartRate() > maxY) {
                maxY = p.getHeartRate();
              }
              break;
            case SPEED:
              if (p.getSpeed() > maxYSpeed) {
                maxYSpeed = p.getSpeed();
              }
              if (p.getSpeed() < minYSpeed) {
                minYSpeed = p.getSpeed();
              }
              if (p.getPace() > maxYPace) {
                maxYPace = p.getPace();
              }
              if (p.getPace() < minYPace) {
                minYPace = p.getPace();
              }
              break;
            case ALTITUDE:
              if (!p.isValidAltitude()) {
                p.setAltitude(0);
              }
              if (p.getAltitude() > maxY) {
                maxY = p.getAltitude();
              }
              if (p.getAltitude() < minY) {
                minY = p.getAltitude();
              }
              break;
          }

        }

        // Axe des x (distance en metre)
        double max = maxX / 1000.0;
        gridXDistance[0] = 0;
        for (int i = 1; i < gridXDistance.length; i++) {
          gridXDistance[i] = (max * 1.0) * i / (gridXDistance.length - 1);
        }
        // Axe des x (temps)
        gridXTime[0] = 0;
        double maxTime = points.get(points.size() - 1).getTime().getTime()
                         - points.get(0).getTime().getTime();
        for (int i = 1; i < gridXDistance.length; i++) {
          gridXTime[i] = (maxTime * 1.0) * i / (gridXDistance.length - 1);
        }

        if (type == ALTITUDE) {
          for (int i = 0; i < gridY.length; i++) {
            gridY[i] = (int) (minY + (maxY - minY) * i / (gridY.length - 1));
          }
        }
        else if (type == SPEED) {
          minYSpeed = Math.floor(minYSpeed);
          maxYSpeed = Math.floor(maxYSpeed) + 1;
          minYPace = Math.floor(minYPace);
          maxYPace = Math.floor(maxYPace) + 1;
          gridYSpeed = new double[gridY.length];
          gridYPace = new double[gridY.length];
          for (int i = 0; i < gridYSpeed.length; i++) {
            gridYSpeed[i] = (minYSpeed + (maxYSpeed - minYSpeed) * i
                                         / (gridYSpeed.length - 1));
            gridYPace[i] = (minYPace + (maxYPace - minYPace) * i
                                       / (gridYPace.length - 1));
          }

          minY = isVisibleSpeed() ? minYSpeed : minYPace;
          maxY = isVisibleSpeed() ? maxYSpeed : maxYPace;
          gridY = isVisibleSpeed() ? gridYSpeed : gridYPace;
        }
        // max zoom
        currentZoom = 0;
        maxZoom = (int) (Math.log(max) / Math.log(2));

        // filtre
        applyFilterSavitzyGolay();
      }

      revalidate();
      repaint();
    }

    private void computeSpeedPace() {
      for (int i = 0; i < points.size() - 1; i++) {
        long time = points.get(i + 1).getTime().getTime()
                    - points.get(i).getTime().getTime();
        float dist = points.get(i + 1).getDistance()
                     - points.get(i).getDistance();

        double speed = (time == 0) ? 0.D : (dist / time) * 3600;
        points.get(i + 1).setSpeed(speed);
      }
      points.get(0).setSpeed(points.get(1).getSpeed());

      // courbe 3
      double[] y = new double[points.size()];
      for (int i = 0; i < points.size(); i++) {
        y[i] = points.get(i).getSpeed();

      }
      double[] yFilter = SavitzkyGolay.filter(y);
      for (int i = 0; i < points.size(); i++) {
        if (yFilter[i] < 0) {
          yFilter[i] = 0;
        }
        points.get(i).setSpeed(yFilter[i]);
        double value = (yFilter[i] <= 0) ? 0 : (60 / yFilter[i]);
        points.get(i).setPace(value);
      }
    }

    public void changeFilter() {
      if (points == null) {
        return;
      }

      if (model.isFilter()) {
        applyFilterSavitzyGolay();
      }
      revalidate();
      repaint();
    }

    private void applyFilterSavitzyGolay() {
      if (pointsFilter != null) {
        return;
      }

      pointsFilter = new DataRunTrk[points.size()];

      // courbe 1
      double[] y = new double[points.size()];
      for (int i = 0; i < points.size(); i++) {
        y[i] = points.get(i).getHeartRate();
        pointsFilter[i] = new DataRunTrk();
      }
      double[] yFilter = SavitzkyGolay.filter(y);
      for (int i = 0; i < points.size(); i++) {
        pointsFilter[i].setDistance(points.get(i).getDistance());
        if (yFilter[i] < gridY[0]) {
          yFilter[i] = gridY[0];
        }
        else if (yFilter[i] > gridY[gridY.length - 1]) {
          yFilter[i] = gridY[gridY.length - 1];
        }
        pointsFilter[i].setHeartRate((int) yFilter[i]);
      }

      // courbe 2
      for (int i = 0; i < points.size(); i++) {
        y[i] = points.get(i).getAltitude();
      }
      yFilter = SavitzkyGolay.filter(y);
      for (int i = 0; i < points.size(); i++) {
        if (yFilter[i] < 0) {
          yFilter[i] = 0;
        }
        pointsFilter[i].setAltitude((float) yFilter[i]);
      }

      // courbe 3
      for (int i = 0; i < points.size(); i++) {
        pointsFilter[i].setSpeed(points.get(i).getSpeed());
      }

    }

    public void zoomPlus() {
      bZoom = true;
      for (JDiagramOneComponent d : listDiagrams) {
        d.model.bZoom = true;
      }
    }

    public void zoomMoins() {
      bZoom = false;
      for (JDiagramOneComponent d : listDiagrams) {
        d.model.bZoom = false;
      }
    }

    public void reload() {
      if (points == null) {
        return;
      }
      reloadInner();
      
      for (JDiagramOneComponent d : listDiagrams) {
        d.model.reloadInner();
      }
    }
    
    private void reloadInner() {
      if (currentZoom != 0) {
        indexX1 = 0;
        indexX2 = points.size();
        applyZoom();
      }
    }

    protected void zoom(boolean isLeft) {
      if (points == null) {
        return;
      }
      doZoom(isLeft);
      for (JDiagramOneComponent d : listDiagrams) {
        d.model.doZoom(isLeft);
      }
    }

    protected void doZoom(boolean isLeft) {
      if (bZoom) {
        if (currentZoom < maxZoom) {
          currentZoom++;
          if (isLeft) {
            indexX1 /= 2;
            indexX2 = (indexX2 / 2) + (indexX2 % 2);
          }
          else {
            indexX1 += (indexX2 - indexX1) / 2;
          }
          applyZoom();
        }
      }
      else {
        if (currentZoom > 0) {
          currentZoom--;
          if (indexX1 == 0) {
            indexX2 *= 2;
          }
          else if (indexX2 == points.size()) {
            indexX1 -= (indexX2 - indexX1);
          }
          else {
            int inter = (indexX2 - indexX1) / 2;
            indexX1 -= inter;
            indexX2 += inter;
          }
          if (indexX1 < 0) {
            indexX1 = 0;
          }
          if (indexX2 > points.size()) {
            indexX2 = points.size();
          }
          applyZoom();
        }
      }
    }

    private void applyZoom() {
      minX = Double.MAX_VALUE;
      maxX = 0;
      maxY = 0;
      minY = Double.MAX_VALUE;

      // recuperation des max
      for (int i = indexX1; i < indexX2; i++) {
        if (points.get(i).getDistance() < minX) {
          minX = points.get(i).getDistance();
        }
        if (points.get(i).getDistance() > maxX) {
          maxX = points.get(i).getDistance();
        }
        if (points.get(i).getHeartRate() > maxY) {
          maxY = points.get(i).getHeartRate();
        }
      }

      // Axe des x (distance en metre)
      double max = (maxX - minX) / 1000.0;
      gridXDistance[0] = minX / 1000.0;
      for (int i = 1; i < gridXDistance.length; i++) {
        gridXDistance[i] = gridXDistance[i - 1] + max
                           / (gridXDistance.length - 1);
      }
      // Axe des x (temps)
      gridXTime[0] = points.get(indexX1).getTime().getTime()
                     - points.get(0).getTime().getTime();
      double maxTime = points.get(indexX2 - 1).getTime().getTime()
                       - points.get(indexX1).getTime().getTime();
      for (int i = 1; i < gridXTime.length; i++) {
        gridXTime[i] = gridXTime[i - 1] + maxTime / (gridXTime.length - 1);
      }

      revalidate();
      repaint();
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MyMouseMotionListener extends MouseMotionAdapter {
    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseMotionAdapter#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
      final int x = e.getX();
      setMouseX(x);
      revalidate();
      repaint();

      for (JDiagramOneComponent d : listDiagrams) {
        d.setMouseX(x);
        d.revalidate();
        d.repaint();
      }
    }
  }

  private void setMouseX(int mouseX) {
    this.mouseX = mouseX;
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class MyMouseListener extends MouseAdapter {
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      int w = getWidth();
      if (x > WIDTH_TITLE_1 && x < w - WIDTH_TITLE_1 && y > HEIGHT_TITLE_1
          && y < (getHeight() - HEIGHT_TITLE_2)) {
        model.zoom((x < w / 2));
      }
    }
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class MyMouseWheelListener implements MouseWheelListener {

    /*
     * (non-Javadoc)
     * 
     * @seejava.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
     * MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
      int x = e.getX();
      int y = e.getY();
      int w = getWidth();
      if (x > WIDTH_TITLE_1 && x < w - WIDTH_TITLE_1 && y > HEIGHT_TITLE_1
          && y < (getHeight() - HEIGHT_TITLE_2)) {
        model.bZoom = (e.getWheelRotation() < 0);
        model.zoom((x < w / 2));
        
        for (JDiagramOneComponent d: listDiagrams) {
          d.model.bZoom = (e.getWheelRotation() < 0);
          d.model.zoom((x < w / 2));         
        }
      }
    }

  }

}
