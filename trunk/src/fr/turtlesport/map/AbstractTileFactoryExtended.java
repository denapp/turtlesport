package fr.turtlesport.map;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.mapviewer.AbstractTileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractTileFactoryExtended extends AbstractTileFactory {

  private ImageIcon smallIcon;

  private ImageIcon bigIcon;

  public AbstractTileFactoryExtended(TileFactoryInfo info) {
    super(info);
  }

  /**
   * Restitue l'url de base.
   * 
   * @return Restitue l'url de base.
   */
  public abstract String getBaseURL();

  /**
   * D&eacute;termine si cette<code>TileFactory</code> est &eacute;ditable.
   * 
   * @return <code>true</code>si cette<code>TileFactory</code> est
   *         &eacute;ditable, <code>false</code> sinon.
   */
  public abstract boolean isEditable();

  /**
   * Restitue le nom de la <code>TileFactory</code>.
   * 
   * @return le nom de la <code>TileFactory</code>.
   */
  public abstract String getName();

  /**
   * D&eacute;termine si cette<code>TileFactory</code> est connect&eacute;e.
   * 
   * @return <code>true</code>si cette<code>TileFactory</code> est
   *         connect&eacute;e, <code>false</code> sinon.
   */
  public abstract boolean isConnected();

  /**
   * @return Restitue l'icone de cette map.
   */
  public ImageIcon getSmallIcon() {
    return smallIcon;
  }

  /**
   * Valorise l'icone de cette map.
   * 
   * @param icon
   *          le nouvel icone.
   */
  public void setSmallIcon(ImageIcon icon) {
    this.smallIcon = icon;
  }

  /**
   * @return Restitue l'icone de cette map.
   */
  public ImageIcon getBigIcon() {
    return bigIcon;
  }

  /**
   * Valorise l'icone de cette map.
   * 
   * @param icon
   *          le nouvel icone.
   */
  public void setBigIcon(ImageIcon bigIcon) {
    this.bigIcon = bigIcon;
  }
}
