package fr.turtlesport.map;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

import javax.swing.JPanel;

import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 * @author Denis Apparicio
 * 
 */
public class TurtleEmptyTileFactory extends AbstractTileFactoryExtended {

  /** The empty tile image. */
  private BufferedImage      emptyTile;

  /** Nom de la map. */
  public static final String NAME = "mercator";

  /**
   * 
   */
  public TurtleEmptyTileFactory() {
    this(new TileFactoryInfo(NAME,
                             1,
                             15,
                             17,
                             256,
                             true,
                             true,
                             "",
                             "x",
                             "y",
                             "z"));
  }

  /**
   * @param info
   */
  public TurtleEmptyTileFactory(TileFactoryInfo info) {
    super(info);

    // construction de l'image
    int tileSize = info.getTileSize(info.getMinimumZoomLevel());
    emptyTile = new BufferedImage(tileSize,
                                  tileSize,
                                  BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = emptyTile.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new JPanel().getBackground());
    g.fillRect(0, 0, tileSize, tileSize);
    g.dispose();

    // cache
    setTileCache(new TurleEmpyCache());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.AbstractTileFactoryExtended#getBaseURL()
   */
  @Override
  public String getBaseURL() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.TileFactoryName#getName()
   */
  public String getName() {
    return NAME;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.TileFactoryExtended#isConnected()
   */
  public boolean isConnected() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdesktop.swingx.mapviewer.TileFactory#getTile(int, int, int)
   */
  @Override
  public Tile getTile(int x, int y, int zoom) {
    return new Tile(x, y, zoom) {

      public boolean isLoaded() {
        return true;
      }

      public BufferedImage getImage() {
        return emptyTile;
      }

    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdesktop.swingx.mapviewer.TileFactory#startLoading(org.jdesktop.swingx
   * .mapviewer.Tile)
   */
  @Override
  protected void startLoading(Tile tile) {
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TurleEmpyCache extends TileCache {

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.TileCache#get(java.net.URI)
     */
    @Override
    public BufferedImage get(URI uri) throws IOException {
      return emptyTile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.TileCache#needMoreMemory()
     */
    @Override
    public void needMoreMemory() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.TileCache#put(java.net.URI, byte[],
     * java.awt.image.BufferedImage)
     */
    @Override
    public void put(URI uri, byte[] bytes, BufferedImage img) {
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.map.AbstractTileFactoryExtended#isEditable()
   */
  @Override
  public boolean isEditable() {
    return false;
  }

}
