package fr.turtlesport.ui.swing.model;

import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.map.AbstractTileFactoryExtended;
import fr.turtlesport.map.DataMap;
import fr.turtlesport.ui.swing.component.GeoPositionMapKit;
import fr.turtlesport.unit.TimeUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelMapkitManager implements ChangePointsListener {
  protected transient ChangeMapEvent    changeEvent;

  protected transient ChangePointsEvent changePointsEvent;

  protected List<ChangeMapListener>     listenerChangeMapList    = new ArrayList<ChangeMapListener>();

  protected List<AddDeleteMapListener>  listenerAddDeleteMapList = new ArrayList<AddDeleteMapListener>();

  private int                           speed                    = 1;

  private boolean                       isRunning                = false;

  private AbstractTileFactoryExtended   tileFactory;

  private static ModelMapkitManager     singleton                = new ModelMapkitManager();

  /**
   * 
   */
  public ModelMapkitManager() {
    super();
    changeEvent = new ChangeMapEvent(this);
    ModelPointsManager.getInstance().addChangeListener(this);
  }

  @Override
  public void changedPoint(ChangePointsEvent e) {
  }

  @Override
  public void changedLap(ChangePointsEvent e) {
  }

  @Override
  public void changedAllPoints(ChangePointsEvent changeEvent) {
    // changement de map
    isRunning = false;
  }

  /**
   * Restitue l'instance unique du <code>ModelMapkitManager</code>.
   * 
   * @return l'instance unique du <code>ModelMapkitManager</code>.
   */
  public static final ModelMapkitManager getInstance() {
    return singleton;
  }

  /**
   * Restitue la vitesse de d&eacute;filement des points.
   * 
   * @return la vitesse de d&eacute;filement des points.
   */
  public final int getSpeed() {
    return speed;
  }

  /**
   * 
   */
  public void play() {
    if (!isRunning) {
      isRunning = true;
      firePlayChanged();
    }
  }

  public void pause() {
    if (isRunning) {
      isRunning = false;
      firePlayChanged();
    }
  }

  /**
   * @return <code>true</code> si tourne, <code>false</code> sinon.
   */
  public final boolean isRunning() {
    return isRunning;
  }

  /**
   * Valorise la vitesse de d&eacute;filement des points.
   * 
   * @param la
   *          vitesse de d&eacute;filement des points.
   */
  public final void setSpeed(int speed) {
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
  public final boolean hasPoints() {
    return ModelPointsManager.getInstance().hasPoints();
  }

  /**
   * Restitue l'index du point courant de la map.
   * 
   * @return
   */
  public final int getMapIndexCurrentPoint() {
    return ModelPointsManager.getInstance().getMapIndexCurrentPoint();
  }

  /**
   * Restitue le point courant de la map.
   * 
   * @return le point courant de la map.
   */
  public final GeoPositionMapKit getMapCurrentPoint() {
    return ModelPointsManager.getInstance().getMapCurrentPoint();
  }

  /**
   * Valorise le point courant au point de d&eacute;but.
   * 
   */
  public final void beginPoint(Object source) {
    ModelPointsManager.getInstance().setMapCurrentPoint(source, 0);
  }

  /**
   * Incr&eacute;mente le point courant de la map.
   * 
   */
  public final void nextPoint(Object source) {
    int value = ModelPointsManager.getInstance().getMapIndexCurrentPoint()
                + speed;

    int max = ModelPointsManager.getInstance().getListGeo().size() - 1;
    if (value > max) {
      ModelPointsManager.getInstance().setMapCurrentPoint(source, 0);
      pause();
    }
    else {
      ModelPointsManager.getInstance().setMapCurrentPoint(source, value);
    }
  }

  /**
   * Valorise le point courant.
   * 
   * @param value
   */
  public final void setMapCurrentPoint(Object source, int value) {
    int max = ModelPointsManager.getInstance().getListGeo().size() - 1;
    if (value > max) {
      ModelPointsManager.getInstance().setMapCurrentPoint(source, max);
      pause();
    }
    else {
      ModelPointsManager.getInstance().setMapCurrentPoint(source, value);
    }
  }

  /**
   * Valorise la map.
   * 
   * @param tileFactory
   *          la map.
   */
  public void setMapTileFactory(AbstractTileFactoryExtended tileFactory) {
    if (tileFactory != null && !tileFactory.equals(this.tileFactory)) {
      this.tileFactory = tileFactory;
    }
    fireMapChanged();
  }

  /**
   * Ajoute une map.
   * 
   * @param tileFactory
   *          la map.
   */
  public void addMapTileFactory(DataMap map) {
    fireAddMapChanged(map);
  }

  /**
   * Ajoute une map.
   * 
   * @param tileFactory
   *          la map.
   */
  public void removeMapTileFactory(DataMap map) {
    fireRemoveMapChanged(map);
  }

  /**
   * Restitue la map.
   * 
   * @return la map.
   */
  public AbstractTileFactoryExtended getMapTileFactory() {
    return tileFactory;
  }

  /**
   * Ajoute un <code>ChangeMapListener</code>.
   * 
   * @param l
   *          le <code>ChangeMapListener</code> &agrave; ajouter.
   */
  public void addChangeListener(ChangeMapListener l) {
    if (!listenerChangeMapList.contains(l)) {
      listenerChangeMapList.add(l);
      ModelPointsManager.getInstance().addChangeListener(l);
      l.changedSpeed(changeEvent);
    }
  }

  /**
   * Ajoute un <code>ChangeMapListener</code>.
   * 
   * @param l
   *          le <code>ChangeMapListener</code> &agrave; ajouter.
   */
  public void addAddDeleteMapListener(AddDeleteMapListener l) {
    if (!listenerAddDeleteMapList.contains(l)) {
      listenerAddDeleteMapList.add(l);
    }
  }

  /**
   * Supprime tous les <code>ChangePointsListener</code>.
   * 
   * @param l
   *          le <code>ChangePointsListener</code> &agrave; supprimer.
   */
  public void removeAllChangeListener() {
    Object[] objs = listenerChangeMapList.toArray();
    if (objs != null) {
      for (Object o : objs) {
        removeChangeListener((ChangeMapListener) o);
      }
    }
  }

  /**
   * Supprime le <code>ChangeMapListener</code>.
   * 
   * @param l
   *          le <code>ChangeMapListener</code> &agrave; supprimer.
   */
  public void removeChangeListener(ChangeMapListener l) {
    listenerChangeMapList.remove(l);
  }

  /**
   * Supprime le <code>ChangeMapListener</code>.
   * 
   * @param l
   *          le <code>AddDeleteMapListener</code> &agrave; supprimer.
   */
  public void removAddDeleteMapListener(AddDeleteMapListener l) {
    listenerAddDeleteMapList.remove(l);
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void firePlayChanged() {
    for (ChangeMapListener l : listenerChangeMapList) {
      l.changedPlay(changeEvent);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireSpeedChanged() {
    for (ChangeMapListener l : listenerChangeMapList) {
      l.changedSpeed(changeEvent);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireMapChanged() {
    for (ChangeMapListener l : listenerChangeMapList) {
      l.changedMap(changeEvent);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireAddMapChanged(DataMap map) {
    AddDeleteMapEvent event = new AddDeleteMapEvent(this, map);
    for (AddDeleteMapListener l : listenerAddDeleteMapList) {
      l.addMap(event);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireRemoveMapChanged(DataMap map) {
    AddDeleteMapEvent event = new AddDeleteMapEvent(this, map);
    for (AddDeleteMapListener l : listenerAddDeleteMapList) {
      l.deleteMap(event);
    }
  }

}
