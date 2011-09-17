package fr.turtlesport.map;


/**
 * @author Denis Apparicio
 * 
 */
public interface TileFactoryExtended  {

  /**
   * Restitue le nom de la <code>TileFactory</code>.
   * 
   * @return le nom de la <code>TileFactory</code>.
   */
  String getName();

  /**
   * D&eacute;termine si cette<code>TileFactory</code> est connect&eacute;e.
   * 
   * @return <code>true</code>si cette<code>TileFactory</code> est
   *         connect&eacute;e, <code>false</code> sinon.
   */
  boolean isConnected();
}
