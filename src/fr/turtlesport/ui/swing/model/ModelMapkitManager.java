package fr.turtlesport.ui.swing.model;

import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.TileFactory;

import fr.turtlesport.ui.swing.component.GeoPositionMapKit;
import fr.turtlesport.unit.TimeUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelMapkitManager {
  protected transient ChangeMapEvent    changeEvent;

  protected transient ChangePointsEvent changePointsEvent;

  protected List<ChangeMapListener>     listenerList = new ArrayList<ChangeMapListener>();

  private int                           speed        = 1;

  private boolean                       isRunning    = false;

  private TileFactory                   tileFactory;

  private static ModelMapkitManager     singleton    = new ModelMapkitManager();

  /**
   * 
   */
  public ModelMapkitManager() {
    super();
    changeEvent = new ChangeMapEvent(this);
  }

  /**
   * Restitue l'instance unique du <code>ModelMapkitManager</code>.
   * 
   * @return l'instance unique du <code>ModelMapkitManager</code>.
   */
  public static ModelMapkitManager getInstance() {
    return singleton;
  }

  /**
   * Restitue la vitesse de d&eacute;filement des points.
   * 
   * @return la vitesse de d&eacute;filement des points.
   */
  public int getSpeed() {
    return speed;
  }

  /**
   * 
   */
  public void play() {
    isRunning = !isRunning;
    firePlayChanged();
  }

  /**
   * @return <code>true</code> si tourne, <code>false</code> sinon.
   */
  public boolean isRunning() {
    return isRunning;
  }

  /**
   * Valorise la vitesse de d&eacute;filement des points.
   * 
   * @param la
   *          vitesse de d&eacute;filement des points.
   */
  public void setSpeed(int speed) {
    if (speed < 1) {
      speed = 1;
    }
    if (this.speed != speed) {
      this.speed = speed;
      fireSpeedChanged();
    }
  }

  /**
   * Restitue le temps du point courant.
   * 
   * @return le temps du point courant.
   */
  public String currentTime() {
    if (!hasPoints()
        || ModelPointsManager.getInstance().getMapIndexCurrentPoint() == -1) {
      return "00:00";
    }
    long elapsed = ModelPointsManager.getInstance().getMapCurrentPoint()
        .getTime()
                   - ModelPointsManager.getInstance().getListGeo().get(0)
                       .getTime();
    return TimeUnit.formatHundredSecondeTimeWithoutHour(elapsed / 10);
  }

  /**
   * D&eacute;termine si le mod&egrave;le &agrave; des points.
   * 
   * @return <code>true</code> si le mod&egrave;le &agrave; des points.
   */
  public boolean hasPoints() {
    return ModelPointsManager.getInstance().hasPoints();
  }

  /**
   * Restitue l'index du point courant de la map.
   * 
   * @return
   */
  public int getMapIndexCurrentPoint() {
    return ModelPointsManager.getInstance().getMapIndexCurrentPoint();
  }

  /**
   * Restitue le point courant de la map.
   * 
   * @return le point courant de la map.
   */
  public GeoPositionMapKit getMapCurrentPoint() {
    return ModelPointsManager.getInstance().getMapCurrentPoint();
  }

  /**
   * Valorise le point courant au point de d&eacute;but.
   * 
   */
  public void beginPoint() {
    ModelPointsManager.getInstance().setMapCurrentPoint(0);
  }

  /**
   * Incr&eacute;mente le point courant de la map.
   * 
   */
  public void nextPoint() {
    if (hasPoints()) {
      int value = ModelPointsManager.getInstance().getMapIndexCurrentPoint()
                  + speed;
      int max = ModelPointsManager.getInstance().getListGeo().size() - 1;

      if (value > max) {
        value = max;
        isRunning = false;
      }

      ModelPointsManager.getInstance().setMapCurrentPoint(value);

      if (isRunning == false) {
        firePlayChanged();
      }

    }
  }

  /**
   * Valorise le point courant.
   * 
   * @param value
   */
  public void setMapCurrentPoint(int value) {
    if (hasPoints()) {
      int max = ModelPointsManager.getInstance().getListGeo().size() - 1;
      if (value > max) {
        value = max;
        isRunning = false;
      }

      ModelPointsManager.getInstance().setMapCurrentPoint(value);

      if (isRunning == false) {
        firePlayChanged();
      }
    }
  }

  /**
   * Valorise la map.
   * 
   * @param tileFactory
   *          la map.
   */
  public void setMapTileFactory(TileFactory tileFactory) {
    if (tileFactory != null && !tileFactory.equals(this.tileFactory)) {
      this.tileFactory = tileFactory;
    }
    fireMapChanged();
  }

  /**
   * Restitue la map.
   * 
   * @return la map.
   */
  public TileFactory getMapTileFactory() {
    return tileFactory;
  }

  /**
   * Ajoute un <code>ChangeMapListener</code>.
   * 
   * @param l
   *          le <code>ChangeMapListener</code> &agrave; ajouter.
   */
  public void addChangeListener(ChangeMapListener l) {
    listenerList.add(l);
    ModelPointsManager.getInstance().addChangeListener(l);
    l.changedSpeed(changeEvent);
  }

  /**
   * Supprime le <code>ChangeMapListener</code>.
   * 
   * @param l
   *          le <code>ChangeMapListener</code> &agrave; supprimer.
   */
  public void removeChangeListener(ChangeMapListener l) {
    listenerList.remove(l);
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void firePlayChanged() {
    for (ChangeMapListener l : listenerList) {
      l.changedPlay(changeEvent);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireSpeedChanged() {
    for (ChangeMapListener l : listenerList) {
      l.changedSpeed(changeEvent);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireMapChanged() {
    if (hasPoints()) {
      for (ChangeMapListener l : listenerList) {
        l.changedMap(changeEvent);
      }
    }
  }
}
