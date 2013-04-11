package fr.turtlesport.ui.swing.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.RepaintManager;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

public class JTurtleMapViewer extends JXMapViewer {

  private BufferedImage bimg;

  private Toolkit       toolkit;

  private int           biw, bih;

  private boolean       clearOnce;

  public JTurtleMapViewer() {
    super();
    toolkit = getToolkit();
    setDoubleBuffered(true);
    setIgnoreRepaint(true);
  }

  /**
   * Sets the zoom level and map center position. The full track will be visible
   * with as much details as possible. This implementations is a workaround for
   * a bug in JXMapViewer.calculateZoomFrom(), which should do the same.
   * 
   * @param positions
   *          list of positions of the route
   */
  public void setupZoomAndCenterPosition(List<GeoPosition> positions) {
    if (positions == null || positions.size() < 2) {
      return;
    }
    
    // calculate and set center position of the track
    Rectangle2D gpRectangle = createGeoPositionRectangle(positions);
    GeoPosition gpCenter = new GeoPosition(gpRectangle.getCenterX(),
                                           gpRectangle.getCenterY());
    setCenterPosition(gpCenter);

    // calculate mapKit dimensions based on panel dimensions (with a little
    // offset)
    // (there's a bug in JXMapKit.getWidth/getHeight)
    int mapKitWidth = getWidth() - 30;
    int mapKitHeight = getHeight() - 30;

    // start with zoom level for maximum details
    boolean fullTrackVisible = false;
    int currentZoom = 0;
    int maxZoom = getTileFactory().getInfo().getMaximumZoomLevel();

    // stop when the track is completely visible or when the max zoom level has
    // been reached
    while (!fullTrackVisible && currentZoom < maxZoom) {
      currentZoom++;
      setZoom(currentZoom);

      // calculate pixel positions of top left and bottom right in the track
      // rectangle
      Point2D ptTopLeft = convertGeoPosToPixelPos(new GeoPosition(gpRectangle.getX(),
                                                                  gpRectangle
                                                                      .getY()));
      Point2D ptBottomRight = convertGeoPosToPixelPos(new GeoPosition(gpRectangle.getX()
                                                                          + gpRectangle.getWidth(),
                                                                      gpRectangle
                                                                          .getY()
                                                                          + gpRectangle
                                                                              .getHeight()));

      // calculate current track width and height in pixels (can be negative)
      int trackPixelWidth = Math.abs((int) (ptBottomRight.getX() - ptTopLeft
          .getX()));
      int trackPixelHeight = Math.abs((int) (ptBottomRight.getY() - ptTopLeft
          .getY()));

      // track is completely visible when track dimensions are smaller than map
      // viewer dimensions
      fullTrackVisible = trackPixelWidth < mapKitWidth
                         && trackPixelHeight < mapKitHeight;
    }    
  }

  /**
   * Creates a rectangle of minimal size which contains all specified
   * GeoPositions.
   * 
   * @param positions
   *          list of positions of the route
   * @return the created Rectangle
   */
  private Rectangle2D createGeoPositionRectangle(List<GeoPosition> positions) {
    Rectangle2D rect = new Rectangle2D.Double(positions.get(0).getLatitude(),
                                              positions.get(0).getLongitude(),
                                              0,
                                              0);

    for (GeoPosition pos : positions) {
      rect.add(new Point2D.Double(pos.getLatitude(), pos.getLongitude()));
    }
    return rect;
  }

  private Point2D convertGeoPosToPixelPos(GeoPosition geoPosition) {
    return getTileFactory().geoToPixel(geoPosition, getZoom());
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
    super.paint(g2);
    g2.dispose();

    if (bimg != null) {
      g.drawImage(bimg, 0, 0, null);
      toolkit.sync();
    }
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

}
