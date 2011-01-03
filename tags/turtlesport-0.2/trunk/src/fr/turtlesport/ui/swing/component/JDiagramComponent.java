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
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.filter.SavitzkyGolay;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.model.ChangeMapEvent;
import fr.turtlesport.ui.swing.model.ChangeMapListener;
import fr.turtlesport.ui.swing.model.ChangePointsEvent;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedUnit;
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
  // Mouse position
  private int                         mouseX         = 0;

  private long                        currentTime;

  private double                      currentDistance;

  private double                      currentY1;

  private double                      currentY2;

  private double                      currentY3;

  private int[]                       tabMouseY      = new int[3];

  // gui
  private static final int            WIDTH_TITLE_1  = 60;

  protected static final int          WIDTH_TITLE_2  = 50;

  private static final int            HEIGHT_TITLE_1 = 10;

  private static final int            HEIGHT_TITLE_2 = 20;

  private static final int            PAD            = 5;

  private static final Color          COLOR_LINE     = new Color(0xe1,
                                                                 0xe1,
                                                                 0xe1);

  // protected static final Color COLOR_TIME = new Color(120, 160, 76);
  protected static final Color        COLOR_TIME     = new Color(99, 86, 136);

  public static final Color           COLORY1        = Color.RED;

  public static final Color           COLORY2        = Color.BLUE;

  // public static final Color COLORY3 = new Color(0x2d,
  // 0xd9,
  // 0x27);

  public static final Color           COLORY3        = new Color(0x00,
                                                                 0x8b,
                                                                 0x00);

  private static final AlphaComposite AC_TRANSPARENT = AlphaComposite
                                                         .getInstance(AlphaComposite.SRC_OVER,
                                                                      0.2f);

  /** Model. */
  private TablePointsModel            model;

  private MyMouseMotionListener       mouseMotionListener;

  /**
   * 
   */
  protected JDiagramComponent() {
    super();
    initialize();
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
    model.setUnitX(MessageFormat.format(rb.getString("unitX"),
                                        DistanceUnit.getDefaultUnit()));
  }

  /*
   * FONT_PLAIN_SMALL (non-Javadoc)
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

    mouseMotionListener = new MyMouseMotionListener();
    addMouseMotionListener(mouseMotionListener);

    addMouseListener(new MyMouseListener());
    addMouseWheelListener(new MyMouseWheelListener());

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);

    ModelMapkitManager.getInstance().addChangeListener(model);
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
    int x = WIDTH_TITLE_1;
    if (model.isVisibleY1()) {
      g2.setColor(COLORY1);
      String s = rb.getString("unitY1");
      g2.drawString(s, WIDTH_TITLE_1, 10);
      x += g2.getFontMetrics().stringWidth(s) + 10;
    }
    if (model.isVisibleY2()) {
      g2.setColor(COLORY2);
      g2.drawString(rb.getString("unitY2"), x, 10);
    }
    if (model.isVisibleY3()) {
      g2.setColor(COLORY3);
      g2.drawString(rb.getString("unitY3"), x, 10);
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
                getWidth() - 2 * WIDTH_TITLE_1 - WIDTH_TITLE_2,
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
    tot = getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2;
    int gridy1, gridy2 = -1;
    double gridy3 = -1;
    for (int i = 0; i < model.getGridY1().length; i++) {
      gridy1 = model.getGridY1()[i];
      if (model.isVisibleY2() && model.getGridY2() != null) {
        gridy2 = model.getGridY2()[i];
      }
      if (model.isVisibleY3() && model.getGridY3() != null) {
        gridy3 = model.getGridY3()[i];
      }

      // axe // a X
      y = computeRelativeY1(gridy1);
      if (i != 0) {
        g2.setColor((i % 2 == 0) ? Color.LIGHT_GRAY : COLOR_LINE);
      }
      g2.drawLine(WIDTH_TITLE_1, y, tot, y);
      g2.setColor(Color.BLACK);

      if (i % 2 == 0 || (i == model.getGridY1().length - 1)) {
        // texte Freq cardiaque
        text = Integer.toString(gridy1);
        lenText = metrics.stringWidth(text);
        x = WIDTH_TITLE_1 - PAD - lenText - 15;
        g2.setColor(COLORY1);
        g2.drawString(text, x, y + (highText / 2));

        // texte Altitude
        if (gridy2 != -1) {
          g2.setColor(COLORY2);
          text = Integer.toString(gridy2);
          x = tot + PAD + 15;
          g2.drawString(text, x, y + (highText / 2));
        }

        // texte Y3
        if (gridy3 != -1) {
          g2.setColor(COLORY3);
          text = model.isVisibleSpeed() ? SpeedUnit.format(gridy3) : PaceUnit
              .format(gridy3);
          x = tot + PAD + 15;
          g2.drawString(text, x + WIDTH_TITLE_2, y + (highText / 2));
        }

      }
      g2.setColor(Color.LIGHT_GRAY);
    }

  }

  private void paintYAxis(Graphics2D g2) {
    int x = 0, y;
    int tot = getHeight() - HEIGHT_TITLE_2;

    // axe freq. cardiaque
    g2.setColor(COLORY1);
    double gridx = model.getGridX()[0];
    x = computeRelativeX(gridx);
    g2.drawLine(x, HEIGHT_TITLE_1, x, tot);
    g2.setColor(Color.BLACK);
    g2.drawLine(x, tot, x, tot + PAD);

    // axe altitude
    g2.setColor(COLORY2);
    x = computeRelativeX(model.getGridX()[model.getGridX().length - 1]);
    g2.drawLine(x, HEIGHT_TITLE_1, x, tot);

    // axe y3
    if (model.isVisibleY3()) {
      g2.setColor(COLORY3);
      g2.drawLine(x + WIDTH_TITLE_2, HEIGHT_TITLE_1, x + WIDTH_TITLE_2, tot);
    }

    // Axe // a X
    // -----------------
    tot = getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2;
    int gridy1 = -1;
    for (int i = 0; i < model.getGridY1().length; i++) {
      gridy1 = model.getGridY1()[i];

      // Freq. Cardiaque
      y = computeRelativeY1(gridy1);
      g2.setColor(COLORY1);
      g2.drawLine(WIDTH_TITLE_1 - PAD, y, WIDTH_TITLE_1, y);

      // altitude
      g2.setColor(COLORY2);
      g2.drawLine(tot, y, tot + PAD, y);

      // speed
      if (model.isVisibleY3()) {
        g2.setColor(COLORY3);
        g2.drawLine(tot + WIDTH_TITLE_2, y, tot + WIDTH_TITLE_2 + PAD, y);
      }
    }

  }

  /**
   * Affiche la courbe.
   */
  private void paintPoints(Graphics2D g2) {
    if (!model.isVisibleY1() && !model.isVisibleY2() && !model.isVisibleY3()) {
      return;
    }

    if (model.indexX2 < 1) {
      return;
    }

    int x1, x2 = 0, y1, y2;
    int ya1, ya2;

    int tot = getHeight() - HEIGHT_TITLE_2;
    Polygon pol = new Polygon();
    pol.addPoint(computeRelativeX(model.getX(model.indexX1)), tot);

    for (int i = model.indexX1; i < model.indexX2 - 1; i++) {
      x1 = computeRelativeX(model.getX(i));
      x2 = computeRelativeX(model.getX(i + 1));

      if (model.isVisibleY1()) {
        g2.setColor(COLORY1);
        y1 = computeRelativeY1(model.getY1(i));
        y2 = computeRelativeY1(model.getY1(i + 1));
        g2.drawLine(x1, y1, x2, y2);
      }

      if (model.isVisibleY2()) {
        // trace la courbe
        g2.setColor(COLORY2);
        ya1 = computeRelativeY2(model.getY2(i));
        ya2 = computeRelativeY2(model.getY2(i + 1));
        g2.drawLine(x1, ya1, x2, ya2);

        // remplissage
        pol.addPoint(x1, ya1);
        pol.addPoint(x2, ya2);

        // g2.setComposite(AC_TRANSPARENT); xPoints[0] = x1; xPoints[1] = x1;
        // xPoints[2] = x2; xPoints[3] = x2; yPoints[0] = tot; yPoints[1] = ya1;
        // yPoints[2] = ya2; yPoints[3] = tot; g2.fillPolygon(xPoints, yPoints,
        // xPoints.length); g2.setComposite(AlphaComposite.SrcOver);
      }

      if (model.isVisibleY3()) {
        g2.setColor(COLORY3);
        y1 = computeRelativeY3(model.getY3(i));
        y2 = computeRelativeY3(model.getY3(i + 1));
        g2.drawLine(x1, y1, x2, y2);
      }

    }

    g2.setColor(COLORY2);
    pol.addPoint(x2, tot);
    g2.setComposite(AC_TRANSPARENT);
    g2.fillPolygon(pol);
    g2.setComposite(AlphaComposite.SrcOver);
  }

  /**
   * Affiche l'intervalle.
   */
  private void paintInterval(Graphics2D g2) {
    if (!model.isVisibleY1() && !model.isVisibleY2() && model.isVisibleY3()) {
      return;
    }

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
   * Calcule la coordonnee relative y.
   */
  private int computeRelativeY3(double y) {

    return (int) (getHeight() - HEIGHT_TITLE_2 - (getHeight() - HEIGHT_TITLE_1 - HEIGHT_TITLE_2)
                                                 * (y - model.getGridY3Min())
                                                 / (model.getGridY3Max() - model
                                                     .getGridY3Min()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private int computeRelativeX(double x) {
    return (int) (WIDTH_TITLE_1 + (getWidth() - 2 * WIDTH_TITLE_1 - WIDTH_TITLE_2)
                                  * (x - model.getGridXMin())
                                  / (model.getGridXMax() - model.getGridXMin()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private int computeRelativeXDistance(double x) {
    return (int) (WIDTH_TITLE_1 + (getWidth() - 2 * WIDTH_TITLE_1 - WIDTH_TITLE_2)
                                  * (x - model.getGridXMinDist())
                                  / (model.getGridXMaxDist() - model
                                      .getGridXMinDist()));
  }

  /**
   * Calcule la coordonnee relative x.
   */
  private double invComputeRelativeX(int x) {
    return (1.0 * (x - WIDTH_TITLE_1)
            * (model.getGridXMax() - model.getGridXMin()) / (getWidth() - 2
                                                             * WIDTH_TITLE_1 - WIDTH_TITLE_2))
           + model.getGridXMin();
  }

  /**
   * Affiche les positions.
   */
  private void paintExtra(Graphics2D g2) {
    if (!model.isVisibleY1() && !model.isVisibleY2() && !model.isVisibleY3()) {
      return;
    }
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
    if (model.isVisibleTime()) {
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
    }

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
    if (model.isVisibleY1() && currentY1 > model.getGridY1Min()) {
      g2.setColor(COLORY1);

      // dessine la souris
      g2.drawRect(mouseX - 2, tabMouseY[0] - 2, 4, 4);

      // Dessine triangle sur axe y
      drawTriangleY1(g2, tabMouseY[0]);

      // Dessine la valeur sur l'axe des y
      st = Integer.toString((int) currentY1);
      lenText = metrics.stringWidth(st);
      xg = WIDTH_TITLE_1 - lenText - 2;
      g2.drawString(st, xg, tabMouseY[0] + highText);
    }

    // Courbe Y2
    // ----------------------------------------------------------
    if (model.isVisibleY2() && currentY2 > model.getGridY2Min()) {
      g2.setColor(COLORY2);

      // dessine la souris
      g2.drawRect(mouseX - 2, tabMouseY[1] - 2, 4, 4);

      // Dessine triangle sur axe y
      drawTriangleY2(g2, tabMouseY[1]);

      // Dessine la valeur sur l'axe des y
      // st = Double.toString(currentY2);
      st = Integer.toString((int) currentY2);
      xg = getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2 + 2;
      g2.drawString(st, xg, tabMouseY[1] + highText);
    }

    // Courbe Y3
    // ----------------------------------------------------------
    if (model.isVisibleY3() && currentY3 > model.getGridY3Min()) {
      g2.setColor(COLORY3);

      // dessine la souris
      g2.drawRect(mouseX - 2, tabMouseY[2] - 2, 4, 4);

      // Dessine triangle sur axe y
      drawTriangleY3(g2, tabMouseY[2]);

      // Dessine la valeur sur l'axe des y
      st = model.isVisibleSpeed() ? SpeedUnit.format(currentY3) : PaceUnit
          .format(currentY3);

      xg = getWidth() - WIDTH_TITLE_1 + 2;
      g2.drawString(st, xg, tabMouseY[2] + highText);
    }

  }

  /**
   * Affiche les positions.
   */
  private void paintPoint(Graphics2D g2) {
    if (!model.isVisibleY1() && !model.isVisibleY2() && !model.isVisibleY3()) {
      return;
    }
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
   * Dessine le triangle sur l'axe des Y courbe2.
   */
  private void drawTriangleY2(Graphics2D g2, int y) {
    int tot = getWidth() - WIDTH_TITLE_1 - WIDTH_TITLE_2;
    int[] tabx = { tot - 5, tot, tot - 5 };
    int[] taby = { y - 5, y, y + 5 };
    g2.fillPolygon(tabx, taby, 3);
  }

  /**
   * Dessine le triangle sur l'axe des Y courbe3.
   */
  private void drawTriangleY3(Graphics2D g2, int y) {
    int tot = getWidth() - WIDTH_TITLE_1;
    int[] tabx = { tot - 5, tot, tot - 5 };
    int[] taby = { y - 5, y, y + 5 };
    g2.fillPolygon(tabx, taby, 3);
  }

  /**
   * Fonction recherche du point.
   */
  private void findMousePoint(int x0) {
    double near = Double.MAX_VALUE;
    double nearCur;

    int index = model.indexX2 - 1;
    currentY1 = model.getY1(index);
    currentY2 = model.getY2(index);
    currentY3 = model.getY3(index);
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

    // on met a jour le point pour la map
    ModelPointsManager.getInstance().setMapCurrentPoint(model, index);

    currentTime = model.getTime(index);
    currentDistance = model.getDistanceX(index);
    currentY1 = model.getY1(index);
    currentY2 = model.getY2(index);
    currentY3 = model.getY3(index);

    tabMouseY[0] = computeRelativeY1(currentY1);
    tabMouseY[1] = computeRelativeY2(currentY2);
    tabMouseY[2] = computeRelativeY3(currentY3);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class TablePointsModel implements ChangeMapListener {
    private String           unit                   = DistanceUnit
                                                        .getDefaultUnit();

    private DataRunTrk[]     pointsFilter;

    private List<DataRunTrk> points;

    private double           minY3Speed;

    private double           maxY3Speed;

    private double           minY3Pace;

    private double           maxY3Pace;

    private double           minY2;

    private double           maxY2;

    private double           minX1;

    private double           maxX1;

    private double           maxY1;

    private int[]            gridY1                 = { 50,
                                                        75,
                                                        100,
                                                        125,
                                                        150,
                                                        175,
                                                        200,
                                                        220 };

    private int[]            gridY2;

    private double[]         gridY3Speed;

    private double[]         gridY3Pace;

    private double[]         gridXDistance;

    private double[]         gridXTime;

    private String           unitX                  = "Distance (km)";

    private int              intervalX1             = -1;

    private int              intervalX2             = -1;

    private boolean          bZoom                  = true;

    private int              indexX1;

    private int              indexX2;

    private int              currentZoom            = 0;

    private int              maxZoom;

    private boolean          hasMouseMotionListener = true;

    private boolean          isVisibleY1;

    private boolean          isVisibleY2;

    private int              visibleY3;

    private boolean          isAxisXDistance;

    private boolean          isVisibleTime;

    private boolean          isFilter;

    private DecimalFormat    dfAxisXDistance        = new DecimalFormat("#.#");

    private DecimalFormat    dfDistance             = new DecimalFormat("#.###");

    /**
     * 
     */
    public TablePointsModel() {
      isVisibleY1 = Configuration.getConfig()
          .getPropertyAsBoolean("Diagram", "isVisibleY1", true);
      isVisibleY2 = Configuration.getConfig()
          .getPropertyAsBoolean("Diagram", "isVisibleY2", false);
      visibleY3 = Configuration.getConfig().getPropertyAsInt("Diagram",
                                                             "isVisibleY3",
                                                             -1);
      isVisibleTime = Configuration.getConfig()
          .getPropertyAsBoolean("Diagram", "isVisibleTime", true);

      isFilter = Configuration.getConfig().getPropertyAsBoolean("Diagram",
                                                                "isFilter",
                                                                false);
      isAxisXDistance = Configuration.getConfig()
          .getPropertyAsBoolean("Diagram", "isAxisXDistance", true);

      initialize();
    }

    public void setVisibleTime(boolean isVisibleTime) {
      if (this.isVisibleTime != isVisibleTime) {
        this.isVisibleTime = isVisibleTime;
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleTime",
                                              Boolean.toString(isVisibleTime));
        changeVisible();
      }
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

    public void setVisibleY1(boolean isVisibleY1) {
      if (this.isVisibleY1 != isVisibleY1) {

        this.isVisibleY1 = isVisibleY1;
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleY1",
                                              Boolean.toString(isVisibleY1));
        changeVisible();
      }
    }

    public void setVisibleY2(boolean isVisibleY2) {
      if (this.isVisibleY2 != isVisibleY2) {
        this.isVisibleY2 = isVisibleY2;
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleY2",
                                              Boolean.toString(isVisibleY2));
        changeVisible();
      }
    }

    public void setVisibleY3(int visibleY3) {
      if (this.visibleY3 != visibleY3) {
        this.visibleY3 = visibleY3;
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleY3",
                                              Integer.toString(visibleY3));
        changeVisible();
      }
    }

    public void setAxisX(boolean isAxisXDistance) {
      if (this.isAxisXDistance != isAxisXDistance) {
        this.isAxisXDistance = isAxisXDistance;
        Configuration.getConfig()
            .addProperty("Diagram",
                         "isAxisXDistance",
                         Boolean.toString(isAxisXDistance));
        changeVisible();
      }
    }

    public boolean isFilter() {
      return isFilter;
    }

    public boolean isVisibleTime() {
      return isVisibleTime;
    }

    public boolean isVisibleY1() {
      return isVisibleY1;
    }

    public boolean isVisibleY2() {
      return isVisibleY2;
    }

    public boolean isVisibleY3() {
      return (visibleY3 != -1);
    }

    public boolean isVisibleSpeed() {
      return (visibleY3 == 0);
    }

    public boolean isVisiblePace() {
      return (visibleY3 == 1);
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

    public void unitChanged(String newUnit) {
      if (!unit.equals(newUnit)) {
        ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
            .getManager().getCurrentLang(), JDiagramComponent.class);
        model.setUnitX(MessageFormat.format(rb.getString("unitX"),
                                            DistanceUnit.getDefaultUnit()));

        if (points != null) {
          for (DataRunTrk p : points) {
            p.setDistance((float) DistanceUnit.convert(unit,
                                                       newUnit,
                                                       p.getDistance()));
          }
          unit = newUnit;
          fireChangedAllPoints();
        }
      }
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
      JDiagramComponent.this.removeMouseMotionListener(mouseMotionListener);
      JDiagramComponent.this.addMouseMotionListener(mouseMotionListener);
      mouseX = 0;
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
      if (e.hasPoints() && model != null) {
        int index = e.getTrkIndexCurrentPoint();
        if (e.isCurrentLastPoint()) {
          mouseX = computeRelativeX(model.getGridXMax());
          repaint();
        }
        else {
          mouseX = computeRelativeX(model.getX(index));
        
        currentTime = getTime(index);
        currentDistance = getX(index);
        currentY1 = getY1(index);
        currentY2 = getY2(index);
        currentY3 = getY3(index);

        tabMouseY[0] = computeRelativeY1(currentY1);
        tabMouseY[1] = computeRelativeY2(currentY2);
        tabMouseY[2] = computeRelativeY3(currentY3);
        repaint();
        }
      }
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
      JDiagramComponent.this.removeMouseMotionListener(mouseMotionListener);
      hasMouseMotionListener = !e.isRunning();
      if (hasMouseMotionListener) {
        JDiagramComponent.this.addMouseMotionListener(mouseMotionListener);
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

    protected double getY1(int i) {
      if (model.isFilter()) {
        return pointsFilter[i].getHeartRate();
      }
      return points.get(i).getHeartRate();
    }

    protected double getY2(int i) {
      return (model.isFilter()) ? pointsFilter[i].getAltitude() : points.get(i)
          .getAltitude();
    }

    protected double getY3(int i) {
      return isVisibleSpeed() ? points.get(i).getSpeed() : points.get(i)
          .getPace();
    }

    protected int[] getGridY1() {
      return gridY1;
    }

    protected int[] getGridY2() {
      return gridY2;
    }

    protected double[] getGridY3() {
      return (visibleY3 == 0) ? gridY3Speed : gridY3Pace;
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

    protected int getGridY1Min() {
      return gridY1[0];
    }

    protected int getGridY1Max() {
      return gridY1[gridY1.length - 1];
    }

    public double getGridY2Max() {
      return gridY2[gridY2.length - 1];
    }

    public double getGridY2Min() {
      return gridY2[0];
    }

    public double getGridY3Max() {
      return (visibleY3 == 0) ? gridY3Speed[gridY3Speed.length - 1]
          : gridY3Pace[gridY3Speed.length - 1];
    }

    public double getGridY3Min() {
      return (visibleY3 == 0) ? gridY3Speed[0] : gridY3Pace[0];
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
      gridY3Pace = null;
      gridY3Speed = null;

      if (points == null || points.size() == 0) {
        initialize();
        repaint();
        return;
      }

      if (points != null) {
        indexX1 = 0;
        indexX2 = points.size();

        minX1 = Double.MAX_VALUE;
        maxX1 = 0;
        maxY1 = 0;
        minY2 = Double.MAX_VALUE;
        maxY2 = 0;
        minY3Speed = Double.MAX_VALUE;
        maxY3Speed = 0;
        minY3Pace = Double.MAX_VALUE;
        maxY3Pace = 0;

        computeSpeedPace();

        // recuperation des points
        DataRunTrk pPrev = points.get(0);
        for (DataRunTrk p : points) {
          if (p.getTime().before(pPrev.getTime())) {
            p.setTime(pPrev.getTime());
          }
          pPrev = p;

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
          if (p.getSpeed() > maxY3Speed) {
            maxY3Speed = p.getSpeed();
          }
          if (p.getSpeed() < minY3Speed) {
            minY3Speed = p.getSpeed();
          }
          if (p.getPace() > maxY3Pace) {
            maxY3Pace = p.getPace();
          }
          if (p.getPace() < minY3Pace) {
            minY3Pace = p.getPace();
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

        // Axe des y courbe 2
        gridY2 = new int[gridY1.length];
        for (int i = 0; i < gridY2.length; i++) {
          gridY2[i] = (int) (minY2 + (maxY2 - minY2) * i / (gridY2.length - 1));
        }

        // Axe des y courbe 3
        minY3Speed = Math.floor(minY3Speed);
        maxY3Speed = Math.floor(maxY3Speed) + 1;
        minY3Pace = Math.floor(minY3Pace);
        maxY3Pace = Math.floor(maxY3Pace) + 1;
        gridY3Speed = new double[gridY1.length];
        gridY3Pace = new double[gridY1.length];
        for (int i = 0; i < gridY3Speed.length; i++) {
          gridY3Speed[i] = (minY3Speed + (maxY3Speed - minY3Speed) * i
                                         / (gridY3Speed.length - 1));
          gridY3Pace[i] = (minY3Pace + (maxY3Pace - minY3Pace) * i
                                       / (gridY3Pace.length - 1));
        }

        // max zoom
        currentZoom = 0;
        maxZoom = (int) (Math.log(max) / Math.log(2));

        // filtre
        if (model.isFilter()) {
          applyFilterSavitzyGolay();
        }
      }

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
        if (yFilter[i] < gridY1[0]) {
          yFilter[i] = gridY1[0];
        }
        else if (yFilter[i] > gridY1[gridY1.length - 1]) {
          yFilter[i] = gridY1[gridY1.length - 1];
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
    }

    public void zoomMoins() {
      bZoom = false;
    }

    public void reload() {
      if (points == null || currentZoom == 0) {
        return;
      }
      indexX1 = 0;
      indexX2 = points.size();
      applyZoom();
    }

    private void zoom(boolean isLeft) {
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
      minX1 = Double.MAX_VALUE;
      maxX1 = 0;
      maxY1 = 0;
      minY2 = Double.MAX_VALUE;
      maxY2 = 0;
      minY3Speed = Double.MAX_VALUE;
      maxY3Speed = 0;
      minY3Pace = Double.MAX_VALUE;
      maxY3Pace = 0;

      // recuperation des max
      for (int i = indexX1; i < indexX2; i++) {
        if (points.get(i).getDistance() < minX1) {
          minX1 = points.get(i).getDistance();
        }
        if (points.get(i).getDistance() > maxX1) {
          maxX1 = points.get(i).getDistance();
        }
        if (points.get(i).getHeartRate() > maxY1) {
          maxY1 = points.get(i).getHeartRate();
        }
        if (points.get(i).getAltitude() > maxY2) {
          maxY2 = points.get(i).getAltitude();
        }
        if (points.get(i).getAltitude() < minY2) {
          minY2 = points.get(i).getAltitude();
        }
        if (points.get(i).getSpeed() > maxY3Speed) {
          maxY3Speed = points.get(i).getSpeed();
        }
        if (points.get(i).getSpeed() < minY3Speed) {
          minY3Speed = points.get(i).getSpeed();
        }
        if (points.get(i).getPace() > maxY3Pace) {
          maxY3Pace = points.get(i).getPace();
        }
        if (points.get(i).getPace() < minY3Pace) {
          minY3Pace = points.get(i).getPace();
        }
      }

      // Axe des x (distance en metre)
      double max = (maxX1 - minX1) / 1000.0;
      gridXDistance[0] = minX1 / 1000.0;
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

      // Axe des y courbe 2
      gridY2 = new int[gridY1.length];
      for (int i = 0; i < gridY2.length; i++) {
        gridY2[i] = (int) (minY2 + (maxY2 - minY2) * (gridY1[i] - gridY1[0])
                                   / (getGridY1Max() - getGridY1Min()));
      }

      // Axe des y courbe 3
      minY3Speed = Math.floor(minY3Speed);
      maxY3Speed = Math.floor(maxY3Speed) + 1;
      minY3Pace = Math.floor(minY3Pace);
      maxY3Pace = Math.floor(maxY3Pace) + 1;
      gridY3Speed = new double[gridY1.length];
      gridY3Pace = new double[gridY1.length];
      for (int i = 0; i < gridY3Speed.length; i++) {
        gridY3Speed[i] = (minY3Speed + (maxY3Speed - minY3Speed) * i
                                       / (gridY3Speed.length - 1));
        gridY3Pace[i] = (minY3Pace + (maxY3Pace - minY3Pace) * i
                                     / (gridY3Pace.length - 1));
      }

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
