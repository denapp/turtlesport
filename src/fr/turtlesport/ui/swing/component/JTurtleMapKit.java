package fr.turtlesport.ui.swing.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.painter.Painter;

import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.map.OpenStreetMapTileFactory;
import fr.turtlesport.map.TileFactoryExtended;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;

/**
 * @author Denis Apparicio
 * 
 */
public class JTurtleMapKit extends JXPanel {

  private JButtonCustom    jButtonZoomPlus;

  private JButtonCustom    jButtonZoomMoins;

  private JButtonCustom    jButtonResize;

  private Dimension        dimButton      = new Dimension(20, 20);

  private JXMapViewer      mainMap;

  private JPanel           jPanelButton;

  private JXSplitButton    jXSplitButtonMap;

  private ButtonGroup      buttonGroupDropDown;

  // model
  private TablePointsModel model          = new TablePointsModel();

  private BufferedImage    imgStop;

  private BufferedImage    imgStart;

  private int              widthImg;

  private int              originalZoom   = -1;

  private GeoPosition      originalPosition;

  private ImageIcon        iconConnect    = ImagesDiagramRepository
                                              .getImageIcon("map.png");

  private ImageIcon        iconDisconnect = ImagesDiagramRepository
                                              .getImageIcon("map-off.png");

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

    // recuperation tilefactory par defaut
    TileFactory tileFactory = OpenStreetMapTileFactory.getDefaultTileFactory();
    setTileFactory(tileFactory);
    mainMap.setZoom(tileFactory.getInfo().getDefaultZoomLevel());
    mainMap.setCenterPosition(new GeoPosition(0, 0));
    mainMap.setRestrictOutsidePanning(true);
  }

  public int getOriginalZoom() {
    return originalZoom;
  }

  public void setOriginalZoom(int originalZoom) {
    this.originalZoom = originalZoom;
  }

  public GeoPosition getOriginalPosition() {
    return originalPosition;
  }

  public void setOriginalPosition(GeoPosition originalPosition) {
    this.originalPosition = originalPosition;
  }

  public TablePointsModel getModelMap() {
    return model;
  }

  public TileFactory getTileFactory() {
    return mainMap.getTileFactory();
  }

  public void setTileFactory(TileFactory fact) {

    // mainMap.setTileFactory(fact);
    // mainMap.setZoom(fact.getInfo().getDefaultZoomLevel());
    // mainMap.setCenterPosition(new GeoPosition(0, 0));

    final String name = ((TileFactoryExtended) fact).getName();
    Enumeration<AbstractButton> e = buttonGroupDropDown.getElements();
    while (e.hasMoreElements()) {
      AbstractButton b = e.nextElement();
      if (b instanceof JCheckBoxMenuItemMap) {
        JCheckBoxMenuItemMap val = (JCheckBoxMenuItemMap) b;
        if (val.getText().equals(name)) {
          buttonGroupDropDown.setSelected(val.getModel(), true);
          val.doClick();
          break;
        }
      }
    }
  }

  public void setCenterPosition(GeoPosition pos) {
    mainMap.setCenterPosition(pos);
    originalPosition = pos;
  }

  public GeoPosition getCenterPosition() {
    return mainMap.getCenterPosition();
  }

  public GeoPosition getAddressLocation() {
    return mainMap.getAddressLocation();
  }

  public void setAddressLocation(GeoPosition pos) {
    mainMap.setAddressLocation(pos);
  }

  public JXMapViewer getMainMap() {
    return this.mainMap;
  }

  public void setZoom(int zoom) {
    mainMap.setZoom(zoom);
  }

  private void initialize() {
    GridBagConstraints gridBagConstraints;

    setLayout(new GridBagLayout());

    mainMap = new JXMapViewer();
    mainMap.setLayout(new GridBagLayout());

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    mainMap.add(getJPanelButton(), gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(mainMap, gridBagConstraints);

    // Evenements
    jButtonZoomMoins.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (model.hasPoints()) {
          setZoom(mainMap.getZoom() + 1);
        }
      }
    });
    jButtonZoomPlus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (model.hasPoints()) {
          setZoom(mainMap.getZoom() - 1);
        }
      }
    });
    jButtonResize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (originalZoom != -1 && model.hasPoints()) {
          mainMap.setZoom(originalZoom);
          mainMap.setCenterPosition(originalPosition);
        }
      }
    });
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
      jXSplitButtonMap.setMargin(new Insets(2, 2, 2, 2));
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
  public class TablePointsModel {

    private List<IGeoPosition> listGeo;

    private GeoPosition        lapDeb;

    private GeoPosition        lapEnd;

    /**
     * 
     */
    public TablePointsModel() {
      super();
    }

    public boolean hasPoints() {
      return (listGeo != null && listGeo.size() > 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.ui.swing.model.IModelMap#updateData(java.util.List)
     */
    public void updateData(List<IGeoPosition> listGeo) {
      this.listGeo = listGeo;
      if (!hasPoints()) {
        mainMap.setZoom(1);
        mainMap.setCenterPosition(new GeoPosition(0, 0));
        return;
      }

      mainMap.setZoom(mainMap.getTileFactory().getInfo().getMinimumZoomLevel());

      HashSet<org.jdesktop.swingx.mapviewer.GeoPosition> hashGeoMap = new HashSet<org.jdesktop.swingx.mapviewer.GeoPosition>();
      final ArrayList<org.jdesktop.swingx.mapviewer.GeoPosition> listGeoMap = new ArrayList<org.jdesktop.swingx.mapviewer.GeoPosition>();
      org.jdesktop.swingx.mapviewer.GeoPosition gp;
      for (IGeoPosition g : listGeo) {
        gp = new org.jdesktop.swingx.mapviewer.GeoPosition(g.getLatitude(), g
            .getLongitude());
        gp = new org.jdesktop.swingx.mapviewer.GeoPosition(g.getLatitude(), g
            .getLongitude());
        hashGeoMap.add(gp);
        listGeoMap.add(gp);
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

      originalZoom = mainMap.getZoom();
      originalPosition = mainMap.getCenterPosition();

      mainMap.setOverlayPainter(new Painter<JXMapViewer>() {
        public void paint(Graphics2D g, JXMapViewer map, int w, int h) {

          Graphics2D g2 = (Graphics2D) g.create();

          // convert from viewport to world bitmap
          Rectangle rect = map.getViewportBounds();
          g2.translate(-rect.x, -rect.y);
          g2.setColor(Color.BLUE);

          GeoPosition[] tab = new GeoPosition[listGeoMap.size()];
          listGeoMap.toArray(tab);

          int deb = -1;
          int end = -1;
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
            // line.setLine(p1.getX(), p1.getY() -2, p2.getX(), p2.getY() -2);
            // g2.draw(line);
          }

          if (lapDeb != null && end == -1 && lapEnd.equals(tab[tab.length - 1])) {
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

          g.dispose();
        }

      });
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.ui.swing.model.IModelMap#getListGeo()
     */
    public List<IGeoPosition> getListGeo() {
      return listGeo;
    }

    /**
     * @param geoPosition
     * @param geoPosition2
     */
    public void updateInt(IGeoPosition gDeb, IGeoPosition gEnd) {
      lapDeb = null;
      lapEnd = null;
      if (gDeb != null) {
        lapDeb = new GeoPosition(gDeb.getLatitude(), gDeb.getLongitude());
      }
      if (gEnd != null) {
        lapEnd = new GeoPosition(gEnd.getLatitude(), gEnd.getLongitude());
      }
      revalidate();
      repaint();
    }

  }

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
    }

    /**
     * @return
     */
    public TileFactory getTileFactory() {
      synchronized (JCheckBoxMenuItemMap.class) {
        tileFactory = OpenStreetMapTileFactory.getTileFactory(getText());
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
      int zoom = mainMap.getZoom();
      GeoPosition geo = mainMap.getCenterPosition();

      mainMap.setTileFactory(getTileFactory());
      OpenStreetMapTileFactory.setDefaultTileFactory(getTileFactory());

      ImageIcon newIcon = ((TileFactoryExtended) tileFactory).isConnected() ? iconConnect
          : iconDisconnect;
      if (newIcon != jXSplitButtonMap.getIcon()) {
        jXSplitButtonMap.setIcon(newIcon);
      }

      mainMap.setZoom(zoom);
      mainMap.setCenterPosition(geo);
    }

  }
}
