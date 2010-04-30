package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EtchedBorder;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.painter.Painter;

import fr.turtlesport.map.OpenStreetMapTileFactory;
import fr.turtlesport.map.TileFactoryExtended;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.ui.swing.model.ChangeMapEvent;
import fr.turtlesport.ui.swing.model.ChangeMapListener;
import fr.turtlesport.ui.swing.model.ChangePointsEvent;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class JTurtleMapKit extends JXPanel {
  private int                     originalZoom   = -1;

  private GeoPosition             originalPosition;

  private JButtonCustom           jButtonZoomPlus;

  private JButtonCustom           jButtonZoomMoins;

  private JButtonCustom           jButtonResize;

  private Dimension               dimButton      = new Dimension(20, 20);

  private JXMapViewer             mainMap;

  private JPanel                  jPanelButton;

  private JXSplitButton           jXSplitButtonMap;

  private ButtonGroup             buttonGroupDropDown;

  private ImageIcon               iconConnect    = ImagesDiagramRepository
                                                     .getImageIcon("map.png");

  private ImageIcon               iconDisconnect = ImagesDiagramRepository
                                                     .getImageIcon("map-off.png");

  private JMediaMapKit            jMediaMapKit;

  private BufferedImage           imgStop;

  private BufferedImage           imgStart;

  private int                     widthImg;

  // model
  private MapKitChangeMapListener mapListener;

  // private ModelMapkitManager model;

  private GeoMouseMotionListener  geoMouseMotionListener;

  /**
   * 
   */
  public JTurtleMapKit(boolean isSmallFlag) {
    initialize();

    if (isSmallFlag) {
      imgStop = ImagesRepository.getImage("flag_red12.png");
      imgStart = ImagesRepository.getImage("flag_green12.png");
    }
    else {
      imgStop = ImagesRepository.getImage("flag_red24.png");
      imgStart = ImagesRepository.getImage("flag_green24.png");
    }
    widthImg = imgStop.getWidth();

    mapListener = new MapKitChangeMapListener();
    ModelMapkitManager.getInstance().addChangeListener(mapListener);

    // recuperation tilefactory par defaut
    TileFactory tileFactory = OpenStreetMapTileFactory.getDefaultTileFactory();

    // mis a jour du bouton
    mapListener.setTileMenu(tileFactory);
    mainMap.setTileFactory(tileFactory);
    mainMap.setZoom(tileFactory.getInfo().getDefaultZoomLevel());
    mainMap.setCenterPosition(new GeoPosition(0, 0));
    mainMap.setRestrictOutsidePanning(true);
  }

  /**
   * @return the mapListener
   */
  public MapKitChangeMapListener getMapListener() {
    return mapListener;
  }

  /**
   * Affiche les informations de geo localisation.
   * 
   * @param isVisible
   *          <code>true</code> si on affiche les informations de geo
   *          localisation.
   */
  public void setGeoPositionVisible(boolean isVisible) {
    if (isVisible) {
      if (geoMouseMotionListener != null) {
        return;
      }
      geoMouseMotionListener = new GeoMouseMotionListener();
      mainMap.addMouseMotionListener(new GeoMouseMotionListener());
      jMediaMapKit.getJLabelGeoPosition().setVisible(true);
    }
    else if (geoMouseMotionListener != null) {
      mainMap.removeMouseMotionListener(geoMouseMotionListener);
      jMediaMapKit.getJLabelGeoPosition().setText("");
      jMediaMapKit.getJLabelGeoPosition().setVisible(false);
    }
  }

  /**
   * Restitue le zoom.
   * 
   * @return originalZoom
   */
  public int getOriginalZoom() {
    return originalZoom;
  }

  /**
   * Valorise le zoom
   * 
   * @param originalZoom
   *          la nouvelle valeur.
   */
  public void setOriginalZoom(int originalZoom) {
    this.originalZoom = originalZoom;
  }

  /**
   * Restitue la position.
   * 
   * @return the originalPosition
   */
  public GeoPosition getOriginalPosition() {
    return originalPosition;
  }

  /**
   * Valorise la position.
   * 
   * @param originalPosition
   *          la .nouvelle valeur.
   */
  public void setOriginalPosition(GeoPosition originalPosition) {
    this.originalPosition = originalPosition;
  }

  /**
   * Resutue la map.
   * 
   * @return la map.
   */
  public JXMapViewer getMainMap() {
    return this.mainMap;
  }

  /**
   * Valorise le zoom.
   * 
   * @param zoom
   *          la nouvelle valeur.
   */
  public void setZoom(int zoom) {
    mainMap.setZoom(zoom);
  }

  private void initialize() {
    // GridBagConstraints gridBagConstraints;
    //
    // setLayout(new GridBagLayout());
    //
    // mainMap = new JXMapViewer();
    // mainMap.setLayout(new GridBagLayout());
    //
    // gridBagConstraints = new GridBagConstraints();
    // gridBagConstraints.gridx = 0;
    // gridBagConstraints.gridy = 0;
    // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    // gridBagConstraints.weightx = 1.0;
    // gridBagConstraints.weighty = 1.0;
    // gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    // mainMap.add(getJPanelButton(), gridBagConstraints);
    //
    // gridBagConstraints = new GridBagConstraints();
    // gridBagConstraints.gridx = 0;
    // gridBagConstraints.gridy = 0;
    // gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    // gridBagConstraints.weightx = 1.0;
    // gridBagConstraints.weighty = 1.0;
    // gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    // jMediaMapKit = new JMediaMapKit(this);
    // mainMap.add(jMediaMapKit, gridBagConstraints);
    //
    // gridBagConstraints = new java.awt.GridBagConstraints();
    // gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    // gridBagConstraints.weightx = 1.0;
    // gridBagConstraints.weighty = 1.0;
    // add(mainMap, gridBagConstraints);

    GridBagConstraints gridBagConstraints;

    mainMap = new JXMapViewer();
    mainMap.setLayout(new GridBagLayout());
    mainMap.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    mainMap.add(getJPanelButton(), gridBagConstraints);

    jMediaMapKit = new JMediaMapKit(this);
    // setBorder(BorderFactory.createCompoundBorder(BorderFactory
    // .createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    setLayout(new BorderLayout(0, 0));
    add(mainMap, BorderLayout.CENTER);
    add(jMediaMapKit, BorderLayout.PAGE_END);

    // setLayout(new BorderLayout());
    //
    // mainMap = new JXMapViewer();
    // jMediaMapKit = new JMediaMapKit(this);
    //    
    // add(mainMap, BorderLayout.CENTER);
    // add(getJPanelButton(), BorderLayout.PAGE_START);
    // add(jMediaMapKit, BorderLayout.PAGE_END);

    // Evenements
    jButtonZoomMoins.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (ModelPointsManager.getInstance().hasPoints()) {
          setZoom(mainMap.getZoom() + 1);
        }
      }
    });
    jButtonZoomPlus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (ModelPointsManager.getInstance().hasPoints()) {
          setZoom(mainMap.getZoom() - 1);
        }
      }
    });
    jButtonResize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (getOriginalZoom() != -1) {
          mainMap.setZoom(getOriginalZoom());
          mainMap.setCenterPosition(getOriginalPosition());
        }
      }
    });

  }

  /**
   * Affiche les informations de temps.
   * 
   * @param isVisible
   *          <code>true</code> si on affiche les informations de temps.
   */
  public void setTimeVisible(boolean isVisible) {
    jMediaMapKit.getModel().setTimeVisible(isVisible);
    if (isVisible) {
      ModelMapkitManager.getInstance().removeChangeListener(mapListener);
      ModelMapkitManager.getInstance().addChangeListener(mapListener);
    }
  }

  /**
   * D&eacute;termine si les informations de geo localisation sont affichées.
   * 
   * @return <code>true</code> si on affiche les informations de geo
   *         localisation.
   */
  public boolean hasGeoPositionVisible() {
    return (geoMouseMotionListener != null);
  }

  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      jPanelButton = new JPanel();
      jPanelButton.setOpaque(false);
      jPanelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));

      jPanelButton.add(getJXSplitButtonMap());
      jPanelButton.add(getJButtonZoomMoins());
      jPanelButton.add(getJButtonZoomPlus());
      jPanelButton.add(getJButtonResize());
    }
    return jPanelButton;
  }

  /**
   * This method initializes jButtonZoomPlus
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonZoomPlus() {
    if (jButtonZoomPlus == null) {
      jButtonZoomPlus = new JButtonCustom();
      jButtonZoomPlus.setIcon(ImagesDiagramRepository.getImageIcon("plus.png"));

      jButtonZoomPlus.setMargin(new Insets(2, 2, 2, 2));
      jButtonZoomPlus.setMaximumSize(dimButton);
      jButtonZoomPlus.setMinimumSize(dimButton);
      jButtonZoomPlus.setOpaque(false);
      jButtonZoomPlus.setPreferredSize(dimButton);
    }
    return jButtonZoomPlus;
  }

  /**
   * This method initializes jCheckBoxMap.
   * 
   * @return javax.swing.JButton
   */
  public JXSplitButton getJXSplitButtonMap() {
    if (jXSplitButtonMap == null) {
      buttonGroupDropDown = new ButtonGroup();
      JPopupMenu popmenu = new JPopupMenu();

      for (String s : OpenStreetMapTileFactory.getTileNames()) {
        JCheckBoxMenuItemMap mi = new JCheckBoxMenuItemMap(s);
        buttonGroupDropDown.add(mi);
        popmenu.add(mi);
      }

      jXSplitButtonMap = new JXSplitButton(null, null, popmenu);
      // jXSplitButtonMap.setMargin(new Insets(2, 2, 2, 2));
      Dimension dimButton = new Dimension(30, 20);

      jXSplitButtonMap.setMaximumSize(dimButton);
      jXSplitButtonMap.setMinimumSize(dimButton);
      jXSplitButtonMap.setOpaque(false);
      jXSplitButtonMap.setPreferredSize(dimButton);
    }
    return jXSplitButtonMap;
  }

  /**
   * This method initializes jButtonZoomMoins
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonZoomMoins() {
    if (jButtonZoomMoins == null) {
      jButtonZoomMoins = new JButtonCustom();
      jButtonZoomMoins.setIcon(ImagesDiagramRepository
          .getImageIcon("minus.png"));

      jButtonZoomMoins.setMargin(new Insets(2, 2, 2, 2));
      jButtonZoomMoins.setMaximumSize(dimButton);
      jButtonZoomMoins.setMinimumSize(dimButton);
      jButtonZoomMoins.setOpaque(false);
      jButtonZoomMoins.setPreferredSize(dimButton);
    }
    return jButtonZoomMoins;
  }

  /**
   * This method initializes jButtonResize
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonResize() {
    if (jButtonResize == null) {
      jButtonResize = new JButtonCustom();
      jButtonResize.setIcon(ImagesDiagramRepository
          .getImageIcon("view-fullscreen.png"));

      jButtonResize.setMargin(new Insets(2, 2, 2, 2));
      jButtonResize.setMaximumSize(dimButton);
      jButtonResize.setMinimumSize(dimButton);
      jButtonResize.setOpaque(false);
      jButtonResize.setPreferredSize(dimButton);
    }
    return jButtonResize;
  }

  /**
   * @author Denis Apparicio
   * 
   */

  /**
   * @author Denis Apparicio
   * 
   */
  public class JCheckBoxMenuItemMap extends JCheckBoxMenuItem implements
                                                             ActionListener {
    private TileFactory tileFactory;

    public JCheckBoxMenuItemMap(String text) {
      super(text);
      addActionListener(JCheckBoxMenuItemMap.this);
      setFont(GuiFont.FONT_PLAIN);
    }

    /**
     * @return
     */
    public TileFactory getTileFactory() {
      if (tileFactory == null) {
        synchronized (JCheckBoxMenuItemMap.class) {
          tileFactory = OpenStreetMapTileFactory.getTileFactory(getText());
        }
      }
      return tileFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      ModelMapkitManager.getInstance().setMapTileFactory(getTileFactory());
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class GeoMouseMotionListener extends MouseMotionAdapter {
    public void mouseMoved(MouseEvent e) {
      Point p = mainMap.getMousePosition();
      if (p != null) {
        GeoPosition gp = mainMap.convertPointToGeoPosition(p);
        jMediaMapKit.getJLabelGeoPosition().setText(GeoUtil.geoPosition(gp
            .getLatitude(), gp.getLongitude()));
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MapKitChangeMapListener implements ChangeMapListener {

    public MapKitChangeMapListener() {
      super();
      ModelPointsManager.getInstance().addChangeListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedAllPoints
     * (fr.turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedAllPoints(ChangePointsEvent e) {
      if (!e.hasPoints()) {
        mainMap.setZoom(1);
        mainMap.setCenterPosition(new GeoPosition(0, 0));
        jMediaMapKit.getModel().setMaximum(0);
        jMediaMapKit.getModel().setTimeTot("");
        return;
      }
      // Timetot
      try {
        jMediaMapKit.getModel().setTimeTot(TimeUnit
            .formatHundredSecondeTimeWithoutHour(ModelPointsManager
                .getInstance().getDataRun().computeTimeTot()));
      }
      catch (SQLException sqle) {
        jMediaMapKit.getModel().setTimeTot("");
      }

      jMediaMapKit.getModel().setMaximum(e.getListGeo().size());
      mainMap.setZoom(mainMap.getTileFactory().getInfo().getMinimumZoomLevel());

      HashSet<org.jdesktop.swingx.mapviewer.GeoPosition> hashGeoMap = new HashSet<org.jdesktop.swingx.mapviewer.GeoPosition>();
      List<GeoPositionMapKit> listGeoMap = e.getListGeo();
      org.jdesktop.swingx.mapviewer.GeoPosition gp;
      for (GeoPositionMapKit g : e.getListGeo()) {
        hashGeoMap.add(g);
      }

      mainMap.calculateZoomFrom(hashGeoMap);

      // Ajout des drapeaux
      Point2D p = mainMap.getTileFactory().geoToPixel(listGeoMap.get(0),
                                                      mainMap.getZoom());
      p.setLocation(p.getX(), p.getY() - widthImg);
      gp = mainMap.getTileFactory().pixelToGeo(p, mainMap.getZoom());
      hashGeoMap.add(gp);

      p = mainMap.getTileFactory().geoToPixel(listGeoMap
                                                  .get(listGeoMap.size() - 1),
                                              mainMap.getZoom());
      p.setLocation(p.getX(), p.getY() - widthImg);
      gp = mainMap.getTileFactory().pixelToGeo(p, mainMap.getZoom());
      hashGeoMap.add(gp);

      // re-calculate
      mainMap.calculateZoomFrom(hashGeoMap);

      setOriginalZoom(mainMap.getZoom());
      setOriginalPosition(mainMap.getCenterPosition());

      final GeoPosition[] tab = new GeoPosition[listGeoMap.size()];
      listGeoMap.toArray(tab);

      mainMap.setOverlayPainter(new Painter<JXMapViewer>() {
        public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
          Graphics2D g2 = (Graphics2D) g.create();

          // convert from viewport to world bitmap
          Rectangle rect = map.getViewportBounds();
          g2.translate(-rect.x, -rect.y);
          g2.setColor(Color.BLUE);

          int deb = -1;
          int end = -1;
          GeoPosition lapDeb = ModelPointsManager.getInstance()
              .getGeoPositionLapDeb();
          GeoPosition lapEnd = ModelPointsManager.getInstance()
              .getGeoPositionLapEnd();
          for (int i = 0; i < tab.length - 1; i++) {
            Point2D p1 = map.getTileFactory().geoToPixel(tab[i], map.getZoom());
            Point2D p2 = map.getTileFactory().geoToPixel(tab[i + 1],
                                                         map.getZoom());
            if (lapDeb != null) {
              if (lapDeb.equals(tab[i])) {
                deb = i;
              }
              else if (lapEnd.equals(tab[i])) {
                end = i;
              }
            }
            Line2D line = new Line2D.Double();
            line.setLine(p1, p2);
            g2.draw(line);
            line.setLine(p1.getX(), p1.getY() + 1, p2.getX(), p2.getY() + 1);
            g2.draw(line);
            line.setLine(p1.getX(), p1.getY() + 2, p2.getX(), p2.getY() + 2);
            g2.draw(line);
            line.setLine(p1.getX(), p1.getY() - 1, p2.getX(), p2.getY() - 1);
            g2.draw(line);
          }

          if (ModelPointsManager.getInstance().getGeoPositionLapDeb() != null
              && end == -1
              && ModelPointsManager.getInstance().getGeoPositionLapEnd()
                  .equals(tab[tab.length - 1])) {
            end = tab.length - 2;
          }
          if (deb != -1 && end != -1) {
            for (int i = deb; i <= end; i++) {
              Point2D p1 = map.getTileFactory().geoToPixel(tab[i],
                                                           map.getZoom());
              Point2D p2 = map.getTileFactory().geoToPixel(tab[i + 1],
                                                           map.getZoom());
              g2.setColor(Color.YELLOW);
              Line2D line = new Line2D.Double();
              line.setLine(p1, p2);
              g2.draw(line);
              line.setLine(p1.getX(), p1.getY() + 1, p2.getX(), p2.getY() + 1);
              g2.draw(line);
              line.setLine(p1.getX(), p1.getY() + 2, p2.getX(), p2.getY() + 2);
              g2.draw(line);
              line.setLine(p1.getX(), p1.getY() - 1, p2.getX(), p2.getY() - 1);
              g2.draw(line);
            }
          }

          // start
          Point2D p = map.getTileFactory().geoToPixel(tab[0], map.getZoom());
          g2.drawImage(imgStart,
                       (int) p.getX(),
                       (int) p.getY() - widthImg,
                       widthImg,
                       widthImg,
                       null);
          // stop
          p = map.getTileFactory().geoToPixel(tab[tab.length - 1],
                                              map.getZoom());
          g2.drawImage(imgStop,
                       (int) p.getX(),
                       (int) p.getY() - widthImg,
                       widthImg,
                       widthImg,
                       null);

          // currentPoint
          p = null;
          int current = ModelMapkitManager.getInstance()
              .getMapIndexCurrentPoint();
          if (current != -1) {
            p = map.getTileFactory().geoToPixel(tab[current], map.getZoom());
            g2.setColor(Color.RED);
            g2.fillOval((int) p.getX() - 3, (int) p.getY() - 3, 6, 6);
          }
          g.dispose();
        }

      });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedLap(fr.
     * turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedLap(ChangePointsEvent e) {
      JTurtleMapKit.this.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedPoint(fr
     * .turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedPoint(ChangePointsEvent e) {
      JTurtleMapKit.this.repaint();
      JTurtleMapKit.this.jMediaMapKit.firePogressBarPlayUpdate(e
          .getMapIndexCurrentPoint(), e.getMapCurrentPoint());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangeMapListener#changedSpeed(javax
     * .swing.event.ChangeEvent)
     */
    public void changedSpeed(ChangeMapEvent e) {
      JTurtleMapKit.this.jMediaMapKit.getJProgressBarSpeed().setValue(e
          .getSpeed());
    }

    /*
     * (non-Javadoc)
     * 
     * @seefr.turtlesport.ui.swing.component.ChangeMapListener#changedPlay(fr.
     * turtlesport.ui.swing.component.ChangeMapEvent)
     */
    public void changedPlay(ChangeMapEvent e) {
      if (e.isRunning()) {
        JTurtleMapKit.this.jMediaMapKit.startTimer();
      }
      else {
        JTurtleMapKit.this.jMediaMapKit.stopTimer();
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangeMapListener#changedMap(fr.turtlesport
     * .ui.swing.component.ChangeMapEvent)
     */
    public void changedMap(ChangeMapEvent e) {
      int zoom = mainMap.getZoom();
      GeoPosition geo = mainMap.getCenterPosition();

      TileFactory tile = e.getMapTileFactory();
      mainMap.setTileFactory(tile);
      OpenStreetMapTileFactory.setDefaultTileFactory(tile);

      mainMap.setZoom(zoom);
      mainMap.setCenterPosition(geo);

      // Selectionne le menuItem
      setTileMenu(tile);
    }

    protected void setTileMenu(TileFactory tile) {
      final String name = ((TileFactoryExtended) tile).getName();
      Enumeration<AbstractButton> e = buttonGroupDropDown.getElements();
      while (e.hasMoreElements()) {
        AbstractButton b = e.nextElement();
        if (b instanceof JCheckBoxMenuItemMap) {
          JCheckBoxMenuItemMap val = (JCheckBoxMenuItemMap) b;
          if (val.getText().equals(name)) {
            buttonGroupDropDown.setSelected(val.getModel(), true);
            val.setSelected(true);
            break;
          }
        }
      }

      ImageIcon newIcon = ((TileFactoryExtended) tile).isConnected() ? iconConnect
          : iconDisconnect;
      if (newIcon != jXSplitButtonMap.getIcon()) {
        jXSplitButtonMap.setIcon(newIcon);
      }
    }

  }

}
