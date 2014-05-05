package fr.turtlesport.map;

import org.jdesktop.swingx.mapviewer.AbstractTileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractTileFactoryExtended extends AbstractTileFactory {
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
   * D&eacute;termine si cette<code>TileFactory</code> est &eacute;ditable.
   * 
   * @return <code>true</code>si cette<code>TileFactory</code> est
   *         &eacute;ditable, <code>false</code> sinon.
   */
  public abstract boolean isEditable();
}
