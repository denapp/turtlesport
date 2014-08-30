package fr.turtlesport.ui.swing.model;

import javax.swing.event.ChangeEvent;

import fr.turtlesport.map.AbstractTileFactoryExtended;
import fr.turtlesport.ui.swing.component.GeoPositionMapKit;

/**
 * @author Denis Apparicio
 * 
 */
public class ChangeMapEvent extends ChangeEvent {

  /**
   * @param source
   */
  public ChangeMapEvent(ModelMapkitManager source) {
    super(source);
  }

  /**
   * Restitue la vitesse de d&eacute;filement des points.
   * 
   * @return la vitesse de d&eacute;filement des points.
   */
  public int getSpeed() {
    return ((ModelMapkitManager) getSource()).getSpeed();
  }

  /**
   * D&eacute;termine si le mod&egrave;le &agrave; des points.
   * 
   * @return <code>true</code> si le mod&egrave;le &agrave; des points.
   */
  public boolean hasPoints() {
    return ((ModelMapkitManager) getSource()).hasPoints();
  }

  /**
   * Restitue l'index du point courant de la map.
   * 
   * @return
   */
  public int getMapIndexCurrentPoint() {
    return ((ModelMapkitManager) getSource()).getMapIndexCurrentPoint();
  }

  /**
   * Restitue la map.
   * 
   * @return la map.
   */
  public AbstractTileFactoryExtended getMapTileFactory() {
    return ((ModelMapkitManager) getSource()).getMapTileFactory();
  }

  /**
   * @return <code>true</code> si tourne, <code>false</code> sinon.
   */
  public boolean isRunning() {
    return ((ModelMapkitManager) getSource()).isRunning();
  }

  /**
   * Restitue le point courant de la map.
   * 
   * @return le point courant de la map.
   */
  public GeoPositionMapKit getMapCurrentPoint() {
    return ((ModelMapkitManager) getSource()).getMapCurrentPoint();
  }

}
