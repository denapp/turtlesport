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
import java.util.List;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.lang.CommonLang;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class JDiagramCompareComponent extends JPanel {
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

  public static final int          WIDTH_TITLE_2  = 25;

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

  private int                         type;

  private Color                       colorY;

  private int                         currentIndex;

  /**
   * 
   */
  public JDiagramCompareComponent() {
    super();
    model = new TablePointsModel();
    initialize();
  }

  private void performedLanguage(ILanguage lang) {
    model.setUnitX(CommonLang.INSTANCE.distanceWithUnit());
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
    	text = model.getGridYText(i);
        lenText = metrics.stringWidth(text);
        x = WIDTH_TITLE_1 - PAD - lenText - 20;
        x = 5;
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
        || mouseX > getWidth() - WIDTH_TITLE_2) {
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
  public class TablePointsModel {

    private List<DataRunTrk> points1;
    
    private List<DataRunTrk> points2;

    private double           minX;

    private double           maxX;

    private double           minY;

    private double           maxY;

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

    private String           unitX                  = "Distance (km)";

    private boolean          bZoom                  = true;

    private int              indexX1;

    private int              indexX2;

    private int              currentZoom            = 0;

    private int              maxZoom;

    private boolean          hasMouseMotionListener = true;

    private boolean          isAxisXDistance;

    private boolean          isFilter;

    private DecimalFormat    dfAxisXDistance        = new DecimalFormat("#.#");

    private DecimalFormat    dfDistance             = new DecimalFormat("#.###");

    /**
     * 
     */
    public TablePointsModel() {

      isAxisXDistance = Configuration.getConfig()
          .getPropertyAsBoolean("Diagram", "isAxisXDistance", true);

      initialize();
      points1 = ModelPointsManager.getInstance().getListTrks();
      fireChangedAllPoints();
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
      currentZoom = 0;
    }

    public boolean hasMouseMotionListener() {
      return hasMouseMotionListener;
    }


    public void changeVisible() {
      revalidate();
      repaint();
    }

    protected double getX(int i) {
      return (isAxisXDistance) ? points1.get(i).getDistance() / 1000
          : points1.get(i).getTime().getTime()
            - points1.get(0).getTime().getTime();
    }

    protected double getDistanceX(int i) {
      return points1.get(i).getDistance() / 1000;
    }

    protected String getCurrentXText() {
      return dfDistance.format(currentDistance);
    }

    protected String getCurrentXTime() {
      return TimeUnit.formatMilliSecondeTime(currentTime);
    }

    protected long getTime(int i) {
      return points1.get(i).getTime().getTime()
             - points1.get(0).getTime().getTime();
    }

    protected int getDistance(int i) {
      return (int) points1.get(i).getDistance();
    }

    protected double getY(int i) {
     return (!isAxisXDistance) ? points1.get(i).getDistance() / 1000
    	       : points1.get(i).getTime().getTime()
    	            - points1.get(0).getTime().getTime();
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

    protected String getGridYText(int index) {
        return (!isAxisXDistance) ? dfAxisXDistance.format(gridXDistance[index])
            : TimeUnit.formatMilliSecondeTime((long) gridXTime[index]);
    }
    
    protected double getGridXMin() {
      return (isAxisXDistance) ? gridXDistance[0] : gridXTime[0];
    }

    protected double getGridXMax() {
      return (isAxisXDistance) ? gridXDistance[gridXDistance.length - 1]
          : gridXTime[gridXTime.length - 1];
    }

    protected double getGridYMin() {
      return (!isAxisXDistance) ? gridXDistance[0] : gridXTime[0];
    }

    protected double getGridYMax() {
        return (!isAxisXDistance) ? gridXDistance[gridXDistance.length - 1]
                : gridXTime[gridXTime.length - 1];
    }

    protected double getGridXMinDist() {
      return gridXDistance[0];
    }

    protected double getGridXMaxDist() {
      return gridXDistance[gridXDistance.length - 1];
    }
 
    public void changedAllPoints(List<DataRunTrk> points1, List<DataRunTrk> points2) {
    	this.points1 = points1;
    	this.points2 = points2;
        fireChangedAllPoints();
    }
  
    /**
     * Mis a jour des donn&eacute;es.
     * 
     * @param points1
     */
    private void fireChangedAllPoints() {
      indexX2 = 0;

      if (points1 == null || points1.size() == 0 || points2 == null || points2.size() == 0) {
        initialize();
        revalidate();
        repaint();
        return;
      }

      if (points1 != null && points2 != null) {
        indexX1 = 0;
        indexX2 = points1.size();

        minX = Double.MAX_VALUE;
        maxX = 0;
        maxY = 0;
        minY = Double.MAX_VALUE;
        maxY = 0;

        // recuperation des points
        DataRunTrk pPrev = points1.get(0);
        for (DataRunTrk p : points1) {
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
        }
        pPrev = points1.get(0);
        for (DataRunTrk p : points2) {
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
        }
        
        // Axe des x (distance en metre)
        double max = maxX / 1000.0;
        gridXDistance[0] = 0;
        for (int i = 1; i < gridXDistance.length; i++) {
          gridXDistance[i] = (max * 1.0) * i / (gridXDistance.length - 1);
        }
        // Axe des x (temps)
        gridXTime[0] = 0;
        double maxTime1 = points1.get(points1.size() - 1).getTime().getTime()
                         - points1.get(0).getTime().getTime();
        double maxTime2 = points2.get(points2.size() - 1).getTime().getTime()
                - points2.get(0).getTime().getTime();
        double maxTime = Math.max(maxTime1, maxTime2);
        for (int i = 1; i < gridXTime.length; i++) {
          gridXTime[i] = (maxTime * 1.0) * i / (gridXTime.length - 1);
        }

        // max zoom
        currentZoom = 0;
        maxZoom = (int) (Math.log(max) / Math.log(2));
      }

      revalidate();
      repaint();
    }


    public void zoomPlus() {
      bZoom = true;
      model.bZoom = true;
    }

    public void zoomMoins() {
      bZoom = false;
        model.bZoom = false;
    }

    public void reload() {
      if (points1 == null) {
        return;
      }
      reloadInner();
    }

    private void reloadInner() {
      if (currentZoom != 0) {
        indexX1 = 0;
        indexX2 = points1.size();
        applyZoom();
      }
    }

    protected void zoom(boolean isLeft) {
      if (points1 == null) {
        return;
      }
      doZoom(isLeft);
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
          else if (indexX2 == points1.size()) {
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
          if (indexX2 > points1.size()) {
            indexX2 = points1.size();
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
        if (points1.get(i).getDistance() < minX) {
          minX = points1.get(i).getDistance();
        }
        if (points1.get(i).getDistance() > maxX) {
          maxX = points1.get(i).getDistance();
        }
        if (points1.get(i).getHeartRate() > maxY) {
          maxY = points1.get(i).getHeartRate();
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
      gridXTime[0] = points1.get(indexX1).getTime().getTime()
                     - points1.get(0).getTime().getTime();
      double maxTime = points1.get(indexX2 - 1).getTime().getTime()
                       - points1.get(indexX1).getTime().getTime();
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
      }
    }

  }

}
