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
import java.util.ArrayList;
import java.util.Enumeration;
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

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.map.AbstractTileFactoryExtended;
import fr.turtlesport.map.AllMapsFactory;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.ui.swing.model.ChangeMapEvent;
import fr.turtlesport.ui.swing.model.ChangePointsEvent;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class JTurtleMapKitCompare extends JXPanel {
  private int                     originalZoom   = -1;

  private GeoPosition             originalPosition;

  private JButtonCustom           jButtonZoomPlus;

  private JButtonCustom           jButtonZoomMoins;

  private JButtonCustom           jButtonResize;

  private Dimension               dimButton      = new Dimension(20, 20);

  private JTurtleMapViewer        mainMap;

  private JPanel                  jPanelButton;

  private JXSplitButton           jXSplitButtonMap;

  private ButtonGroup             buttonGroupDropDown;

  private ImageIcon               iconConnect    = ImagesDiagramRepository
                                                     .getImageIcon("map.png");

  private ImageIcon               iconDisconnect = ImagesDiagramRepository
                                                     .getImageIcon("map-off.png");

  private JMediaMapKitCompare     jMediaMapKit;

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
  public JTurtleMapKitCompare() {
    initialize();
    imgStop = ImagesRepository.getImage("flag_red24.png");
    imgStart = ImagesRepository.getImage("flag_green24.png");
    widthImg = imgStop.getWidth();

    mapListener = new MapKitChangeMapListener();

    // recuperation tilefactory par defaut
    TileFactory tileFactory = AllMapsFactory.getInstance().getDefaultTileFactory();

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
    GridBagConstraints gridBagConstraints;

    mainMap = new JTurtleMapViewer();

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

    jMediaMapKit = new JMediaMapKitCompare(this);
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    setLayout(new BorderLayout(0, 0));
    add(mainMap, BorderLayout.CENTER);
    add(jMediaMapKit, BorderLayout.PAGE_END);

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

      for (String s : AllMapsFactory.getInstance().getTileNames()) {
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
    private AbstractTileFactoryExtended tileFactory;

    public JCheckBoxMenuItemMap(String text) {
      super(text);
      addActionListener(JCheckBoxMenuItemMap.this);
      setFont(GuiFont.FONT_PLAIN);
    }

    /**
     * @return
     */
    public AbstractTileFactoryExtended getTileFactory() {
      if (tileFactory == null) {
        synchronized (JCheckBoxMenuItemMap.class) {
          tileFactory = AllMapsFactory.getInstance().getTileFactory(getText());
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
  public class MapKitChangeMapListener {

    private List<DataRun>          runs        = new ArrayList<DataRun>();

    private List<List<DataRunTrk>> listListTrk = new ArrayList<List<DataRunTrk>>();

    public MapKitChangeMapListener() {
      super();
    }

    public void removeRun(DataRun dataRun) {
      int index = runs.indexOf(dataRun);
      runs.remove(index);
      listListTrk.remove(index);

      showMap();
    }

    public void addRun(DataRun dataRun) {
      if (runs.contains(dataRun)) {
        return;
      }

      runs.add(dataRun);
      try {
        List<DataRunTrk> listTrks = RunTrkTableManager.getInstance()
            .getValidTrks(dataRun.getId());
        if (listTrks != null && listTrks.size() > 0) {
          listListTrk.add(DataRunTrk.cloneList(listTrks, 3000));
        }
        else {
          runs.remove(dataRun);
        }
      }
      catch (SQLException e) {
      }

      showMap();
    }

    private void showMap() {
      // Temps max
      computeTimeTot(runs);

      // Recuperation des points
      List<org.jdesktop.swingx.mapviewer.GeoPosition> listGeoMapAll = new ArrayList<org.jdesktop.swingx.mapviewer.GeoPosition>();

      // Les GeoPositions
      List<List<GeoPositionMapKit>> listListGeoMap = new ArrayList<List<GeoPositionMapKit>>();
      for (List<DataRunTrk> trks : listListTrk) {
        List<GeoPositionMapKit> listGeoMap = new ArrayList<GeoPositionMapKit>();
        listListGeoMap.add(listGeoMap);
        for (int i = 0; i < trks.size(); i++) {
          GeoPositionMapKit geo = new GeoPositionMapKit(trks.get(i), i);
          listGeoMap.add(geo);
          listGeoMapAll.add(geo);
        }
      }

      // calculate zoom
      mainMap.setupZoomAndCenterPosition(listGeoMapAll);

      org.jdesktop.swingx.mapviewer.GeoPosition gp;
      // Ajout des drapeaux
      for (List<GeoPositionMapKit> listGeoMap : listListGeoMap) {
        Point2D p = mainMap.getTileFactory().geoToPixel(listGeoMap.get(0),
                                                        mainMap.getZoom());
        p.setLocation(p.getX(), p.getY() - widthImg);
        gp = mainMap.getTileFactory().pixelToGeo(p, mainMap.getZoom());

        p = mainMap.getTileFactory().geoToPixel(listGeoMap.get(listGeoMap
                                                    .size() - 1),
                                                mainMap.getZoom());
        p.setLocation(p.getX(), p.getY() - widthImg);
        gp = mainMap.getTileFactory().pixelToGeo(p, mainMap.getZoom());
      }

      final List<GeoPositionMapKit[]> listGeoTab = new ArrayList<GeoPositionMapKit[]>();
      for (List<GeoPositionMapKit> listGeo : listListGeoMap) {
        GeoPositionMapKit[] tab = new GeoPositionMapKit[listGeo.size()];
        listGeo.toArray(tab);
        listGeoTab.add(tab);
      }

      mainMap.setOverlayPainter(new Painter<JXMapViewer>() {
        public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
          Graphics2D g2 = (Graphics2D) g.create();

          // convert from viewport to world bitmap
          Rectangle rect = map.getViewportBounds();
          g2.translate(-rect.x, -rect.y);

          for (int i = 0; i < listGeoTab.size(); i++) {
            // g2.setColor((i%2==0)?Color.BLUE: Color.RED);
            g2.setColor(Color.RED);
            paintPoints(g2, map, listGeoTab.get(i));
          }

          for (GeoPositionMapKit[] tab : listGeoTab) {
            // start
            Point2D p = map.getTileFactory().geoToPixel(tab[0], map.getZoom());
            g2.drawImage(imgStart,
                         (int) p.getX(),
                         (int) p.getY() - widthImg,
                         widthImg,
                         widthImg,
                         null);
            p = map.getTileFactory().geoToPixel(tab[0], map.getZoom());
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
            p = map.getTileFactory().geoToPixel(tab[tab.length - 1],
                                                map.getZoom());
            g2.drawImage(imgStop,
                         (int) p.getX(),
                         (int) p.getY() - widthImg,
                         widthImg,
                         widthImg,
                         null);
          }
          g.dispose();
        }

        private void paintPoints(Graphics2D g2,
                                 JXMapViewer map,
                                 GeoPositionMapKit[] tab) {
          for (int i = 0; i < tab.length - 1; i++) {
            Point2D p1 = map.getTileFactory().geoToPixel(tab[i], map.getZoom());
            Point2D p2 = map.getTileFactory().geoToPixel(tab[i + 1],
                                                         map.getZoom());
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

      });
    }

    private void computeTimeTot(List<DataRun> runs) {
      // Temps total
      try {
        int timeTot = 0;
        for (DataRun run : runs) {
          timeTot = Math.max(run.computeTimeTot(), timeTot);
        }
        jMediaMapKit.getModel()
            .setTimeTot(TimeUnit.formatHundredSecondeTimeWithoutHour(timeTot));
      }
      catch (SQLException sqle) {
        jMediaMapKit.getModel().setTimeTot("");
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedLap(fr.
     * turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedLap(ChangePointsEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.turtlesport.ui.swing.component.ChangePointsListener#changedPoint(fr
     * .turtlesport.ui.swing.component.ChangePointsEvent)
     */
    public void changedPoint(final ChangePointsEvent e) {
      mainMap.repaint();
      JTurtleMapKitCompare.this.jMediaMapKit.firePogressBarPlayUpdate(e
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
      JTurtleMapKitCompare.this.jMediaMapKit.getJProgressBarSpeed()
          .setValue(e.getSpeed());
    }

    /*
     * (non-Javadoc)
     * 
     * @seefr.turtlesport.ui.swing.component.ChangeMapListener#changedPlay(fr.
     * turtlesport.ui.swing.component.ChangeMapEvent)
     */
    public void changedPlay(ChangeMapEvent e) {
      if (e.isRunning()) {
        JTurtleMapKitCompare.this.jMediaMapKit.startTimer();
      }
      else {
        JTurtleMapKitCompare.this.jMediaMapKit.stopTimer();
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

      AbstractTileFactoryExtended tile = e.getMapTileFactory();
      mainMap.setTileFactory(tile);
      AllMapsFactory.getInstance().setDefaultTileFactory(tile);

      mainMap.setZoom(zoom);
      mainMap.setCenterPosition(geo);

      // Selectionne le menuItem
      setTileMenu(tile);
    }

    protected void setTileMenu(TileFactory tile) {
//      final String name = ((TileFactoryExtended) tile).getName();
//      Enumeration<AbstractButton> e = buttonGroupDropDown.getElements();
//      while (e.hasMoreElements()) {
//        AbstractButton b = e.nextElement();
//        if (b instanceof JCheckBoxMenuItemMap) {
//          JCheckBoxMenuItemMap val = (JCheckBoxMenuItemMap) b;
//          if (val.getText().equals(name)) {
//            buttonGroupDropDown.setSelected(val.getModel(), true);
//            val.setSelected(true);
//            break;
//          }
//        }
//      }
//
//      ImageIcon newIcon = ((TileFactoryExtended) tile).isConnected() ? iconConnect
//          : iconDisconnect;
//      if (newIcon != jXSplitButtonMap.getIcon()) {
//        jXSplitButtonMap.setIcon(newIcon);
//      }
    }

  }

}
