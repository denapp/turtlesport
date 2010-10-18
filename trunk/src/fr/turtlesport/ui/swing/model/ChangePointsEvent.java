package fr.turtlesport.ui.swing.model;

import java.util.List;

import javax.swing.event.ChangeEvent;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.ui.swing.component.GeoPositionMapKit;

/**
 * @author Denis Apparicio
 * 
 */
public class ChangePointsEvent extends ChangeEvent {

  /**
   * @param source
   */
  public ChangePointsEvent(ModelPointsManager source) {
    super(source);
  }

  /**
   * Restitue la liste des GeoPositions valide.
   * 
   * @return la liste des points.
   */
  public List<GeoPositionMapKit> getListGeo() {
    return ((ModelPointsManager) getSource()).getListGeo();
  }

  /**
   * Restitue la liste des points.
   * 
   * @return la liste des points.
   */
  public List<DataRunTrk> getListTrks() {
    return ((ModelPointsManager) getSource()).getListTrks();
  }

  /**
   * Restitue le run.
   * 
   * @return le run.
   */
  public DataRun getDataRun() {
    return ((ModelPointsManager) getSource()).getDataRun();
  }

  /**
   * Restitue la position de d&eacute;but du tour.
   * 
   * @return la position de d&eacute;but du tour.
   */
  public GeoPosition getGeoPositionLapDeb() {
    return ((ModelPointsManager) getSource()).getGeoPositionLapDeb();
  }

  /**
   * Restitue la position de fin du tour.
   * 
   * @return la position de fin du tour.
   */
  public GeoPosition getGeoPositionLapEnd() {
    return ((ModelPointsManager) getSource()).getGeoPositionLapEnd();
  }

  /**
   * D&eacute;termine si le mod&egrave;le &agrave; des points.
   * 
   * @return <code>true</code> si le mod&egrave;le &agrave; des points.
   */
  public boolean hasPoints() {
    return ((ModelPointsManager) getSource()).hasPoints();
  }

  /**
   * Restitue l'index du point courant de la map.
   * 
   * @return
   */
  public int getMapIndexCurrentPoint() {
    return ((ModelPointsManager) getSource()).getMapIndexCurrentPoint();
  }

  /**
   * Restitue le point courant de la map.
   * 
   * @return le point courant de la map.
   */
  public GeoPositionMapKit getMapCurrentPoint() {
    return ((ModelPointsManager) getSource()).getMapCurrentPoint();
  }

  /**
   * Restitue l'index du point courant.
   * 
   * @return
   */
  public final int getTrkIndexCurrentPoint() {
    return ((ModelPointsManager) getSource()).getTrkIndexCurrentPoint();
  }

  /**
   * Determine si dernier point.
   * 
   * @return <ode>true</code> si dernier point.
   */
  public final boolean isCurrentLastPoint() {
    return ((ModelPointsManager) getSource()).isCurrentLastPoint();
  }

}
