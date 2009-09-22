package fr.turtlesport.ui.swing.component;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.filter.SavitzkyGolay;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDiagramComponent extends JComponent implements LanguageListener,
                                                 UnitListener {
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDiagramComponent.class);
  }

  // Mouse position
  private int                         mouseX         = 0;

  private long                        currentTime;

  private double                      currentX;

  private double                      currentY1;

  private double                      currentY2;

  private int[]                       tabMouseY      = new int[2];

  // gui
  private static final int            WIDTH_TITLE    = 40;

  private static final int            HEIGHT_TITLE_1 = 20;

  private static final int            HEIGHT_TITLE_2 = 40;

  private static final int            PAD            = 5;

  private static final Color          COLOR_LINE     = new Color(0xe1,
                                                                 0xe1,
                                                                 0xe1);

  protected static final Color        COLOR_TIME     = new Color(120, 160, 76);

  private static final AlphaComposite AC_TRANSPARENT = AlphaComposite
                                                         .getInstance(AlphaComposite.SRC_OVER,
                                                                      0.2f);

  /** Model. */
  private TablePointsModel            model;

  /**
   * 
   */
  public JDiagramComponent() {
    super();
    log.debug(">>JDiagramComponent");
    initialize();
    log.debug("<<JDiagramComponent");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang
   * .LanguageEvent)
   */
  public void languageChanged(final LanguageEvent event) {
    if (SwingUtilities.isEventDispatchThread()) {
      performedLanguage(event.getLang());
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          performedLanguage(event.getLang());
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility
        .getBundle(lang, JDiagramComponent.class);

    model.setUnitX(MessageFormat.format(rb.getString("unitX"), DistanceUnit
        .getDefaultUnit()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent e) {
    if (e.isEventDistance()) {
      model.unitChanged(e.getUnit());
    }
  }

  /**
   * @return the model
   */
  public TablePointsModel getModel() {
    return model;
  }

  /**
   * Coordonnees max des points.
   */
  private void initialize() {
    model = new TablePointsModel();
    addMouseMotionListener(new MyMouseMotionListener());
    addMouseListener(new MyMouseListener());
    addMouseWheelListener(new MyMouseWheelListener());
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  /**
   * Sauvegarde le composant swing dans un fichier.
   * 
   * @param component
   * @param filename
   */
  public void saveComponentAsJPEG(File file) throws IOException {
    Dimension size = getSize();
    BufferedImage myImage = new BufferedImage(size.width,
                                              size.height,
                                              BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = myImage.createGraphics();

    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), JDiagramComponent.class);

    paintGrid(g2);
    if (model != null && model.indexX2 != 0) {
      paintPoints(g2);
    }

    g2.setFont(GuiFont.FONT_PLAIN_SMALL);
    int x = WIDTH_TITLE;
    if (JPanelGraph.isVisibleY1()) {
      g2.setColor(Color.red);
      String s = rb.getString("unitY1");
      g2.drawString(s, WIDTH_TITLE, 10);
      x += g2.getFontMetrics().stringWidth(s) + 10;
    }
    if (JPanelGraph.isVisibleY2()) {
      g2.setColor(Color.blue);
      g2.drawString(rb.getString("unitY2"), x, 10);
    }
    OutputStream out = null;
    try {
      out = new FileOutputStream(file);
      ImageIO.write(myImage, "jpg", out);
    }
    finally {
      if (out != null) {
        out.close();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paint(java.awt.Graphics)
   */
  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    paintGrid(g2);
    if (model != null && model.indexX2 != 0) {
      paintPoints(g2);
      paintInterval(g2);
      paintExtra(g2);
    }
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
    g2.fillRect(WIDTH_TITLE,
                HEIGHT_TITLE_1,
                getWidth() - 2 * WIDTH_TITLE,
                getHeight() - HEIGHT_TITLE_1 - HEIGHT_TITLE_2);

    g2.setColor(getParent().getBackground());

    // Font
    // --------
    FontMetrics metrics = g2.getFontMetrics(GuiFont.FONT_PLAIN_SMALL);
    int highText = metrics.getAscent() - metrics.getDescent();
    g2.setFont(GuiFont.FONT_PLAIN_SMALL);
    DecimalFormat df = new DecimalFormat("#.#");

    // Axe // a Y
    // -----------------
    tot = getHeight() - HEIGHT_TITLE_2;
    yText = tot + PAD + highText + 8;
    g2.setColor(Color.BLACK);
    for (int i = 0; i < model.getGridX().length; i++) {
      double gridx = model.getGridX()[i];
      // axe // a Y
      x = computeRelativeX(gridx);

      if (i != 0) {
        g2.setColor((i % 2 == 0) ? Color.LIGHT_GRAY : COLOR_LINE);
      }
      g2.drawLine(x, HEIGHT_TITLE_1, x, tot);
      g2.setColor(Color.BLACK);
      g2.drawLine(x, tot, x, tot + PAD);

      // texte
      g2.setColor(Color.BLACK);
      text = df.format(gridx);
      lenText = g2.getFontMetrics(GuiFont.FONT_PLAIN_SMALL).stringWidth(text);
      if (i % 2 == 0) {
        g2.drawString(text, x - lenText / 2, (int) yText);
      }
    }

    // axe altitude
    g2.setColor(Color.BLACK);
    g2.drawLine(x, HEIGHT_TITLE_1, x, tot);

    // Axe // a X
    // -----------------
    tot = getWidth() - WIDTH_TITLE;
    int gridy1, gridy2 = -1;
    for (int i = 0; i < model.getGridY1().length; i++) {
      gridy1 = model.getGridY1()[i];
      if (model.getGridY2() != null) {
        gridy2 = model.getGridY2()[i];
      }

      // axe // a X
      y = computeRelativeY1(gridy1);
      if (i != 0) {
        g2.setColor((i % 2 == 0) ? Color.LIGHT_GRAY : COLOR_LINE);
      }
      g2.drawLine(WIDTH_TITLE, y, tot, y);
      g2.setColor(Color.BLACK);
      g2.drawLine(WIDTH_TITLE - PAD, y, WIDTH_TITLE, y);

      // altitude
      g2.drawLine(tot, y, tot + PAD, y);

      if (i % 2 == 0 || (i == model.getGridY1().length - 1)) {
        // texte X
        text = Integer.toString(gridy1);
        lenText = metrics.stringWidth(text);
        x = WIDTH_TITLE - PAD - lenText - 15;
        g2.drawString(text, x, y + (highText / 2));

        // texte Altitude
        if (gridy2 != -1) {
          text = Integer.toString(gridy2);
          x = tot + PAD + 10;
          g2.drawString(text, x, y + (highText / 2));
        }
      }

      g2.setColor(Color.LIGHT_GRAY);
    }

    // Unites
    lenText = metrics.stringWidth(model.getUnitX());
    x = getWidth() - WIDTH_TITLE - lenText - 8;
    y = getHeight() - highText;
    g2.setColor(Color.BLACK);
    g2.drawString(model.getUnitX(), x, y);
  }

  /**
   * Affiche la courbe.
   */
  private void paintPoints(Graphics2D g2) {
    if (!JPanelGraph.isVisibleY1() && !JPanelGraph.isVisibleY2()) {
      return;
    }

    int x1, x2 = 0, y1, y2;
    int ya1, ya2;

    int tot = getHeight() - HEIGHT_TITLE_2;
    Polygon pol = new Polygon();
    pol.addPoint(computeRelativeX(model.getX(model.indexX1)), tot);

    // for (int i = 0; i < model.length() - 1; i++) {
    for (int i = model.indexX1; i < model.indexX2 - 1; i++) {
      x1 = computeRelativeX(model.getX(i));
      x2 = computeRelativeX(model.getX(i + 1));

      if (JPanelGraph.isVisibleY1()) {
        g2.setColor(Color.red);
        y1 = computeRelativeY1(model.getY1(i));
        y2 = computeRelativeY1(model.getY1(i + 1));
        g2.drawLine(x1, y1, x2, y2);
      }

      if (JPanelGraph.isVisibleY2()) {
        // trace la courbe
        g2.setColor(Color.blue);
        ya1 = computeRelativeY2(model.getY2(i));
        ya2 = computeRelativeY2(model.getY2(i + 1));
        g2.drawLine(x1, ya1, x2, ya2);

        // remplissage
        pol.addPoint(x1, ya1);
        pol.addPoint(x2, ya2);

        /*
         * g2.setComposite(AC_TRANSPARENT); xPoints[0] = x1; xPoints[1] = x1;
         * xPoints[2] = x2; xPoints[3] = x2; yPoints[0] = tot; yPoints[1] = ya1;
         * yPoints[2] = ya2; yPoints[3] = tot; g2.fillPolygon(xPoints, yPoints,
         * xPoints.length); g2.setComposite(AlphaComposite.SrcOver);
         */
      }
    }
    pol.addPoint(x2, tot);
    g2.setComposite(AC_TRANSPARENT);
    g2.fillPolygon(pol);
    g2.setComposite(AlphaComposite.SrcOver);

  }

  /**
   * Affiche l'intervalle.
   */
  private void paintInterval(Graphics2D g2) {
    if (!JPanelGraph.isVisibleY1() && !JPanelGraph.isVisibleY2()) {
      return;
    }

    if (model.getIntervalX1() != -1
        && (model.getIntervalX1() >= model.getDistance(model.indexX1))
        && (model.getIntervalX2() <= model.getDistance(model.indexX2 - 1))) {
      int intX1 = computeRelativeX(model.getIntervalX1() / 1000.0);
      int intX2 = computeRelativeX(model.getIntervalX2() / 1000.0);

      int tot = getHeight() - HEIGHT_TITLE_2 - HEIGHT_TITLE_1;
      int pad = 1;

      g2.setColor(Color.orange);
      g2.fillRect(intX1, HEIGHT_TITLE_1, pad, tot);
      g2.fillRect(intX2, HEIGHT_TITLE_1, pad, tot);

      g2.setColor(Color.orange);
      g2.setComposite(AC_TRANSPARENT);
      tot = getHeight() - HEIGHT_TITLE_2 - HEIGHT_TITLE_1;
      g2.fillRect(intX1 + pad, HEIGHT_TITLE_1, intX2 - intX1 - pad, tot);
      g2.setComposite(AlphaComposite.SrcOver);
    }

  }

  /**
   * Calcule la coordonnee relative y.
   */
  private int computeRelativeY1(double y) {

    return (int) (getHeight() - HEIGHT_TITLE_2 - (getHeight() - HEIGHT_TITLE_1 - HEIGHT_TITLE_2)
                                                 * (y - model.getGridY1Min())
                                                 / (model.getGridY1Max() - model
                                                     .getGridY1Min()));
  }

  /**
   * Calcule la coordonnee relative y.
   */
  private int computeRelativeY2(double y) {

    return (int) (getHeight() - HEIGHT_TITLE_2 - (getHeight() - HEIGHT_TITLE_1 - HEIGHT_TITLE_2)
                                                 * (y - model.getGridY2Min())
                                                 / (model.getGridY2Max() - model
                                                     .getGridY2Min()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private int computeRelativeX(double x) {
    return (int) (WIDTH_TITLE + (getWidth() - 2 * WIDTH_TITLE)
                                * (x - model.getGridXMin())
                                / (model.getGridXMax() - model.getGridXMin()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private double invComputeRelativeX(int x) {
    return (1.0 * (x - WIDTH_TITLE)
            * (model.getGridXMax() - model.getGridXMin()) / (getWidth() - 2 * WIDTH_TITLE))
           + model.getGridXMin();
  }

  /**
   * Affiche les positions.
   */
  private void paintExtra(Graphics2D g2) {
    if (mouseX < WIDTH_TITLE || mouseX > getWidth() - WIDTH_TITLE) {
      return;
    }
    if (!JPanelGraph.isVisibleY1() && !JPanelGraph.isVisibleY2()) {
      return;
    }

    FontMetrics metrics;
    int xg, yg, lenText, highText;
    String st;

    metrics = g2.getFontMetrics(GuiFont.FONT_PLAIN_SMALL);
    highText = (metrics.getAscent() - metrics.getDescent()) / 2;

    // recuperation des position de la souris
    findPoints(mouseX);

    // Le temps ecoule
    // -------------------
    if (JPanelGraph.isVisibleTime()) {
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
      st = TimeUnit.formatHundredSecondeTime(currentTime / 10);
      lenText = metrics.stringWidth(st);
      g2.drawString(st, mouseX - lenText / 2, HEIGHT_TITLE_1 - 2);
    }

    // Valeur axe des X
    // ----------------------------------------------------------
    g2.setColor(Color.DARK_GRAY);

    // Dessine triangle sur axe x
    drawTriangleX(g2, mouseX);

    // Dessine la valeur sur l'axe des x
    yg = getHeight() - WIDTH_TITLE + 10;
    st = DistanceUnit.format(currentX);
    lenText = metrics.stringWidth(st);
    g2.drawString(st, mouseX - lenText / 2, yg);

    // Courbe Y1
    // ----------------------------------------------------------
    if (JPanelGraph.isVisibleY1() && currentY1 > model.getGridY1Min()) {
      g2.setColor(Color.RED);

      // dessine la souris
      g2.drawRect(mouseX - 2, tabMouseY[0] - 2, 4, 4);

      // Dessine triangle sur axe y
      drawTriangleY1(g2, tabMouseY[0]);

      // Dessine la valeur sur l'axe des y
      // st = Double.toString(currentY1);
      st = Integer.toString((int) currentY1);
      lenText = metrics.stringWidth(st);
      xg = WIDTH_TITLE - lenText - 2;
      g2.drawString(st, xg, tabMouseY[0] + highText);
    }

    // Courbe Y2
    // ----------------------------------------------------------
    if (JPanelGraph.isVisibleY2() && currentY2 > model.getGridY2Min()) {
      g2.setColor(Color.BLUE);

      // dessine la souris
      g2.drawRect(mouseX - 2, tabMouseY[1] - 2, 4, 4);

      // Dessine triangle sur axe y
      drawTriangleY2(g2, tabMouseY[1]);

      // Dessine la valeur sur l'axe des y
      // st = Double.toString(currentY2);
      st = Integer.toString((int) currentY2);
      xg = getWidth() - WIDTH_TITLE + 2;
      g2.drawString(st, xg, tabMouseY[1] + highText);
    }
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
    int[] tabx = { WIDTH_TITLE + 5, WIDTH_TITLE, WIDTH_TITLE + 5 };
    int[] taby = { y - 5, y, y + 5 };
    g2.fillPolygon(tabx, taby, 3);
  }

  /**
   * Dessine le triangle sur l'axe des Y courbe2.
   */
  private void drawTriangleY2(Graphics2D g2, int y) {
    int tot = getWidth() - WIDTH_TITLE;
    int[] tabx = { tot - 5, tot, tot - 5 };
    int[] taby = { y - 5, y, y + 5 };
    g2.fillPolygon(tabx, taby, 3);
  }

  /**
   * Fonction recherche du point.
   */
  private void findPoints(int x0) {
    double near = Double.MAX_VALUE;
    double nearCur;

    int index = model.indexX2 - 1;
    currentY1 = model.getY1(index);
    currentY2 = model.getY2(index);
    double xInv = invComputeRelativeX(x0);

    for (int i = model.indexX1; i < model.indexX2 - 1; i++) {
      nearCur = Math.abs(xInv - model.getX(i));
      if (nearCur == 0) {
        index = i;
        break;
      }
      else if (near > nearCur) {
        near = nearCur;
        index = i;
      }
    }

    currentTime = model.getTime(index);
    currentX = model.getX(index);
    currentY1 = model.getY1(index);
    currentY2 = model.getY2(index);

    tabMouseY[0] = computeRelativeY1(currentY1);
    tabMouseY[1] = computeRelativeY2(currentY2);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TablePointsModel {
    private String       unit        = DistanceUnit.getDefaultUnit();

    private DataRunTrk[] points;

    private DataRunTrk[] pointsFilter;

    private double       minY2;

    private double       maxY2;

    private double       minX1;

    private double       maxX1;

    private double       maxY1;

    private int[]        gridY1      = { 50, 75, 100, 125, 150, 175, 200, 220 };

    private int[]        gridY2;

    private double[]     gridX;

    private String       unitX       = "Distance (km)";

    private int          intervalX1  = -1;

    private int          intervalX2  = -1;

    private boolean      bZoom       = true;

    private int          indexX1;

    private int          indexX2;

    private int          currentZoom = 0;

    private int          maxZoom;

    /**
     * 
     */
    public TablePointsModel() {
      gridX = new double[17];
      for (int i = 0, value = 0; i < gridX.length; i++, value += 2) {
        gridX[i] = value;
      }
    }

    public void unitChanged(String newUnit) {
      if (!unit.equals(newUnit)) {
        ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
            .getManager().getCurrentLang(), JDiagramComponent.class);
        model.setUnitX(MessageFormat.format(rb.getString("unitX"), DistanceUnit
            .getDefaultUnit()));

        if (points != null) {
          for (DataRunTrk p : points) {
            p.setDistance((float) DistanceUnit.convert(unit, newUnit, p
                .getDistance()));
          }
          updateData(points, newUnit);
        }
      }
    }

    public void changeVisible() {
      repaint();
    }

    protected double getX(int index) {
      return points[index].getDistance() / 1000;
    }

    protected long getTime(int index) {
      return points[index].getTime().getTime() - points[0].getTime().getTime();
    }

    protected int getDistance(int index) {
      return (int) points[index].getDistance();
    }

    protected double getY1(int index) {
      return (JPanelGraph.isFilter()) ? pointsFilter[index].getHeartRate()
          : points[index].getHeartRate();
    }

    protected double getY2(int index) {
      return (JPanelGraph.isFilter()) ? pointsFilter[index].getAltitude()
          : points[index].getAltitude();
    }

    /**
     * @return
     */
    protected int[] getGridY1() {
      return gridY1;
    }

    /**
     * @return
     */
    protected int[] getGridY2() {
      return gridY2;
    }

    /**
     * @return
     */
    protected String getUnitX() {
      return unitX;
    }

    /**
     * @return
     */
    protected void setUnitX(String val) {
      unitX = val;
    }

    /**
     * @return
     */
    protected double[] getGridX() {
      return gridX;
    }

    /**
     * @return
     */
    protected double getGridXMin() {
      return gridX[0];
    }

    /**
     * @return
     */
    protected double getGridXMax() {
      return gridX[gridX.length - 1];
    }

    /**
     * @return
     */
    protected int getGridY1Min() {
      return gridY1[0];
    }

    /**
     * @return
     */
    protected int getGridY1Max() {
      return gridY1[gridY1.length - 1];
    }

    /**
     * @return the altitudeMax
     */
    public double getGridY2Max() {
      return gridY2[gridY2.length - 1];
    }

    /**
     * @return the altitudeMin
     */
    public double getGridY2Min() {
      return gridY2[0];
    }

    public int getIntervalX1() {
      return intervalX1;
    }

    public int getIntervalX2() {
      return intervalX2;
    }

    /**
     * Mis &agrave; jour de l'intervalle.
     * 
     * @param intX1
     * @param intX2
     */
    public void updateInt(double intX1, double intX2) {
      this.intervalX1 = (int) intX1;
      this.intervalX2 = (int) intX2;
      repaint();
    }

    /**
     * Mis a jour des donn&eacute;es.
     * 
     * @param points
     */
    public void updateData(DataRunTrk[] points, String unit) {
      log.info(">>updateData");

      this.intervalX1 = -1;
      this.intervalX2 = -1;
      this.points = points;
      this.unit = unit;
      this.pointsFilter = null;

      indexX2 = 0;
      if (points != null) {
        indexX1 = 0;
        indexX2 = points.length;

        minX1 = Double.MAX_VALUE;
        maxX1 = 0;
        maxY1 = 0;
        minY2 = Double.MAX_VALUE;
        maxY2 = 0;

        // recuperation des points
        for (DataRunTrk p : points) {
          if (p.getHeartRate() < gridY1[0]) {
            p.setHeartRate(gridY1[0]);
          }
          else if (p.getHeartRate() > gridY1[gridY1.length - 1]) {
            p.setHeartRate(gridY1[gridY1.length - 1]);
          }
          if (p.getDistance() < minX1) {
            minX1 = p.getDistance();
          }
          if (p.getDistance() > maxX1) {
            maxX1 = p.getDistance();
          }
          if (p.getHeartRate() > maxY1) {
            maxY1 = p.getHeartRate();
          }
          if (!p.isValidAltitude()) {
            p.setAltitude(0);
          }
          if (p.getAltitude() > maxY2) {
            maxY2 = p.getAltitude();
          }
          if (p.getAltitude() < minY2) {
            minY2 = p.getAltitude();
          }
        }

        if (maxY2 == 0) {
          minY2 = 0;
        }
        if ((maxY2 - minY2) < 10) {
          maxY2 += 10;
        }

        // Axe des x (distance en metre)
        double max = maxX1 / 1000.0;
        gridX[0] = 0;
        for (int i = 1; i < gridX.length; i++) {
          gridX[i] = (max * 1.0) * i / (gridX.length - 1);
        }

        // Axe des y courbe 2
        gridY2 = new int[gridY1.length];
        for (int i = 0; i < gridY2.length; i++) {
          gridY2[i] = (int) (minY2 + (maxY2 - minY2) * (gridY1[i] - gridY1[0])
                                     / (getGridY1Max() - getGridY1Min()));
        }

        // max zoom
        currentZoom = 0;
        maxZoom = (int) (Math.log(max) / Math.log(2));
        log.debug("maxZoom=" + maxZoom);

        // filtre
        if (JPanelGraph.isFilter()) {
          applyFilterSavitzyGolay();
        }
      }

      log.info("<<updateData");
      repaint();
    }

    public void changeFilter() {
      if (points == null) {
        return;
      }

      if (JPanelGraph.isFilter()) {
        applyFilterSavitzyGolay();
        // applyFilterSubdivision();
      }
      repaint();
    }

    private void applyFilterSavitzyGolay() {
      if (pointsFilter != null) {
        return;
      }

      pointsFilter = new DataRunTrk[points.length];

      // courbe 1
      double[] y = new double[points.length];
      for (int i = 0; i < points.length; i++) {
        y[i] = points[i].getHeartRate();
        // if (y[i] < 50)
        // System.out.println(i + "=" + y[i]);
        pointsFilter[i] = new DataRunTrk();
      }
      double[] yFilter = SavitzkyGolay.filter(y);
      for (int i = 0; i < points.length; i++) {
        pointsFilter[i].setDistance(points[i].getDistance());
        if (yFilter[i] < gridY1[0]) {
          yFilter[i] = gridY1[0];
        }
        else if (yFilter[i] > gridY1[gridY1.length - 1]) {
          yFilter[i] = gridY1[gridY1.length - 1];
        }
        pointsFilter[i].setHeartRate((int) yFilter[i]);
      }

      // courbe 2
      for (int i = 0; i < points.length; i++) {
        y[i] = points[i].getAltitude();
      }
      yFilter = SavitzkyGolay.filter(y);
      for (int i = 0; i < points.length; i++) {
        if (yFilter[i] < 0) {
          yFilter[i] = 0;
        }
        pointsFilter[i].setAltitude((float) yFilter[i]);
      }
    }

    public void zoomPlus() {
      bZoom = true;
    }

    public void zoomMoins() {
      bZoom = false;
    }

    public void reload() {
      if (points == null || currentZoom == 0) {
        return;
      }
      indexX1 = 0;
      indexX2 = points.length;
      applyZoom();
    }

    private void zoom(boolean isLeft) {
      log.debug(">>zoom");

      if (points == null) {
        return;
      }

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
            indexX1 *= 2;
            indexX2 *= 2;
          }
          else if (indexX2 == points.length) {
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
          if (indexX2 > points.length) {
            indexX2 = points.length;
          }
          applyZoom();
        }
      }

      log.debug("<<zoom");
    }

    private void applyZoom() {
      log.debug(">>applyZoom");

      minX1 = Double.MAX_VALUE;
      maxX1 = 0;
      maxY1 = 0;
      minY2 = Double.MAX_VALUE;
      maxY2 = 0;

      // recuperation des max
      for (int i = indexX1; i < indexX2; i++) {
        if (points[i].getDistance() < minX1) {
          minX1 = points[i].getDistance();
        }
        if (points[i].getDistance() > maxX1) {
          maxX1 = points[i].getDistance();
        }
        if (points[i].getHeartRate() > maxY1) {
          maxY1 = points[i].getHeartRate();
        }
        if (points[i].getAltitude() > maxY2) {
          maxY2 = points[i].getAltitude();
        }
        if (points[i].getAltitude() < minY2) {
          minY2 = points[i].getAltitude();
        }
      }

      // Axe des x (distance en metre)
      double max = (maxX1 - minX1) / 1000.0;
      gridX[0] = minX1 / 1000.0;
      for (int i = 1; i < gridX.length; i++) {
        gridX[i] = gridX[i - 1] + (max * 1.0) / (gridX.length - 1);
      }

      // Axe des y courbe 2
      gridY2 = new int[gridY1.length];
      for (int i = 0; i < gridY2.length; i++) {
        gridY2[i] = (int) (minY2 + (maxY2 - minY2) * (gridY1[i] - gridY1[0])
                                   / (getGridY1Max() - getGridY1Min()));
      }

      repaint();

      log.debug("<<applyZoom");
    }

    // private void applyFilterSubdivision() {
    // pointsFilter = null;
    // pointsFilter = Subdivision.filter(points);
    // }

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
      mouseX = e.getX();
      repaint();
    }
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
      if (x > WIDTH_TITLE && x < w - WIDTH_TITLE && y > HEIGHT_TITLE_1
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
      if (x > WIDTH_TITLE && x < w - WIDTH_TITLE && y > HEIGHT_TITLE_1
          && y < (getHeight() - HEIGHT_TITLE_2)) {
        model.bZoom = (e.getWheelRotation() < 0);
        model.zoom((x < w / 2));
      }
    }

  }

}
