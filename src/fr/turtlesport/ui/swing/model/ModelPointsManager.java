package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.ui.swing.component.GeoPositionMapKit;

/**
 * @author Denis Apparicio
 * 
 */
public final class ModelPointsManager {

  protected transient ChangePointsEvent changeEvent     = new ChangePointsEvent(this);

  private List<GeoPositionMapKit>       listGeo;

  private List<DataRunTrk>              listTrksOriginal;

  private List<DataRunTrk>              listTrks;

  private DataRunLap[]                  runLaps;

  private DataRun                       dataRun;

  private int                           lapIndex        = -1;

  private GeoPosition                   lapGeoBegin;

  private GeoPosition                   lapGeoEnd;

  private int                           currentMapPoint = 0;

  protected List<ChangePointsListener>  listenerList    = new ArrayList<ChangePointsListener>();

  private static ModelPointsManager     singleton       = new ModelPointsManager();

  private ModelPointsManager() {
  }

  /**
   * Restitue l'instance unique du <code>ModelPoints</code>.
   * 
   * @return l'instance unique du <code>ModelPoints</code>.
   */
  public static ModelPointsManager getInstance() {
    return singleton;
  }

  /**
   * D&eacute;termine si les points ont des donn&eacute;es de cadence.
   * 
   * @return <code>true</code> si les points ont des donn&eacute;es de cadence,
   *         <code>false</code> sinon.
   */
  public final boolean hasCadencePoints() {
    if (getListTrks() != null) {
      int nbPoints = 0;
      for (DataRunTrk p : getListTrks()) {
        if (p.isValidCadence()) {
          nbPoints++;
          if (nbPoints > 2) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * D&eacute;termine si les points ont des donn&eacute;es cardio.
   * 
   * @return <code>true</code> si les points ont des donn&eacute;es cardio,
   *         <code>false</code> sinon.
   */
  public final boolean hasHeartPoints() {
    if (getListTrks() != null) {      
      int nbPoints = 0;
      for (DataRunTrk p : getListTrks()) {
        if (p.getHeartRate() > 0) {
          nbPoints++;
          if (nbPoints > 2) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * D&eacute;termine si les points ont des donn&eacute;es de temperature.
   * 
   * @return <code>true</code> si les points ont des donn&eacute;es de
   *         temperature, <code>false</code> sinon.
   */
  public final boolean hasTemperaturePoints() {
    if (getListTrks() != null) {
      int nbPoints = 0;
      for (DataRunTrk p : getListTrks()) {
        if (p.isValidTemperature()) {
          nbPoints++;
          if (nbPoints > 2) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Restitue la liste des geo position.
   * 
   * @return la liste des points.
   */
  public final List<GeoPositionMapKit> getListGeo() {
    return listGeo;
  }

  /**
   * Restitue la liste des points.
   * 
   * @return la liste des points.
   */
  public final List<DataRunTrk> getListTrks() {
    return listTrks;
  }

  /**
   * Restitue les tours interm&eacute;m&eacute;diares.
   * 
   * @return les tours interm&eacute;m&eacute;diares.
   */
  public final DataRunLap[] getRunLaps() {
    return runLaps;
  }

  /**
   * Restitue le nombre de tours interm&eacute;m&eacute;diares.
   * 
   * @return le nombre de tours interm&eacute;m&eacute;diares.
   */
  public final int runLapsSize() {
    return (runLaps == null) ? 0 : runLaps.length;
  }

  /**
   * Restitue index du tour.
   * 
   * @return index du tour.
   */
  public final int getLapIndex() {
    return lapIndex;
  }

  /**
   * Valorise le run.
   * 
   * @param dataRun
   *          the dataRun to set
   * @throws SQLException
   */
  /**
   * @param source
   * @param dataRun
   * @throws SQLException
   */
  public final void setDataRun(Object source, DataRun dataRun) throws SQLException {
    if (dataRun != null && dataRun.equals(this.dataRun)) {
      return;
    }

    this.dataRun = dataRun;
    listGeo = null;
    listTrks = null;
    lapGeoBegin = null;
    lapGeoEnd = null;
    currentMapPoint = 0;
    runLaps = null;
    lapIndex = -1;

    if (dataRun != null) {
      // Les points
      listTrksOriginal = RunTrkTableManager.getInstance()
          .getAllTrks(dataRun.getId());
      listTrks = DataRunTrk.cloneList(listTrksOriginal);

      // Les Laps
      runLaps = RunLapTableManager.getInstance().findLaps(dataRun.getId());

      // Les GeoPositions
      listGeo = new ArrayList<GeoPositionMapKit>();

      // recuperation des donnees
      for (int i = 0; i < listTrks.size(); i++) {
        GeoPositionMapKit geo = new GeoPositionMapKit(listTrks.get(i), i);
        listGeo.add(geo);
      }
    }

    fireAllPointsChanged(source);
  }

  /**
   * Valorise le tour interm&eacute;m&eaute;diaire.
   * 
   * @param newLapIndex
   *          index du tour.
   */
  public final void setLap(Object source, int newLapIndex) {
    if ((newLapIndex > runLapsSize() - 1) || newLapIndex == lapIndex) {
      return;
    }
    lapIndex = newLapIndex;

    // calcul des geopositions
    lapGeoBegin = null;
    lapGeoEnd = null;
    if (runLaps != null) {
      long searchTime = runLaps[lapIndex].getStartTime().getTime();
      int i = 0;
      for (i = 0; i < listGeo.size(); i++) {
        long l = searchTime - listGeo.get(i).getTime();
        if (l <= 0) {
          lapGeoBegin = listGeo.get(i);
          break;
        }
      }
      if (lapGeoBegin != null) {
        if (lapIndex < runLaps.length - 1) {
          searchTime = runLaps[lapIndex + 1].getStartTime().getTime();
          for (; i < listGeo.size(); i++) {
            long l = searchTime - listGeo.get(i).getTime();
            if (l <= 0) {
              lapGeoEnd = listGeo.get(i);
              break;
            }
          }
        }
        else {
          lapGeoEnd = listGeo.get(listGeo.size() - 1);
        }
      }
    }

    // on declenche
    fireLapChanged(source);
  }

  /**
   * Restitue le run.
   * 
   * @return le run.
   */
  public final DataRun getDataRun() {
    return dataRun;
  }

  /**
   * Restitue la position de d&eacute;but du tour.
   * 
   * @return la position de d&eacute;but du tour.
   */
  public final GeoPosition getGeoPositionLapDeb() {
    return lapGeoBegin;
  }

  /**
   * Restitue la position de fin du tour.
   * 
   * @return la position de fin du tour.
   */
  public final GeoPosition getGeoPositionLapEnd() {
    return lapGeoEnd;
  }

  /**
   * D&eacute;termine si le mod&egrave;le &agrave; des points.
   * 
   * @return <code>true</code> si le mod&egrave;le &agrave; des points.
   */
  public final boolean hasPoints() {
    return (listGeo != null && listGeo.size() > 0);
  }

  /**
   * Restitue l'index du point courant de la map.
   * 
   * @return
   */
  public final int getMapIndexCurrentPoint() {
    return currentMapPoint;
  }

  /**
   * Restitue l'index du point courant.
   * 
   * @return
   */
  public final int getTrkIndexCurrentPoint() {
    return (getMapCurrentPoint() == null) ? -1 : getMapCurrentPoint()
        .getIndex();
  }

  /**
   * Restitue l'index du point courant.
   * 
   * @return
   */
  public final boolean isCurrentLastPoint() {
    return (listGeo != null && currentMapPoint == (listGeo.size() - 1));
  }

  /**
   * Valorise l'index du point courant de la map.
   * 
   * @param currentMapPoint
   *          la nouvelle valeur.
   * @return <code>true</code> si dernier point.
   */
  public final void setMapCurrentPoint(Object source, int currentMapPoint) {
    if (this.currentMapPoint != currentMapPoint) {
      this.currentMapPoint = currentMapPoint;
      firePointChanged(source);
    }
  }

  /**
   * Restitue le point courant de la map.
   * 
   * @return le point courant de la map.
   */
  public final GeoPositionMapKit getMapCurrentPoint() {
    if (currentMapPoint >= 0 && listGeo != null
        && currentMapPoint < listGeo.size()) {
      return listGeo.get(currentMapPoint);
    }
    return null;
  }

  /**
   * Ajoute un <code>ChangePointsListener</code>.
   * 
   * @param l
   *          le <code>ChangePointsListener</code> &a grave; ajouter.
   */
  public void addChangeListener(ChangePointsListener l) {
    if (!listenerList.contains(l)) {
      listenerList.add(l);
      if (hasPoints()) {
        l.changedAllPoints(changeEvent);
        l.changedPoint(changeEvent);
      }
    }
  }

  /**
   * Supprime le <code>ChangePointsListener</code>.
   * 
   * @param l
   *          le <code>ChangePointsListener</code> &agrave; supprimer.
   */
  public void removeChangeListener(ChangePointsListener l) {
    listenerList.remove(l);
  }

  /**
   * Supprime tous les <code>ChangePointsListener</code>.
   * 
   * @param l
   *          le <code>ChangePointsListener</code> &agrave; supprimer.
   */
  public void removeAllChangeListener() {
    Object[] objs = listenerList.toArray();
    if (objs != null) {
      for (Object o : objs) {
        removeChangeListener((ChangePointsListener) o);
      }
    }

  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireAllPointsChanged(Object source) {
    for (ChangePointsListener l : listenerList) {
      if (!l.equals(source)) {
        l.changedAllPoints(changeEvent);
      }
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireLapChanged(Object source) {
    for (ChangePointsListener l : listenerList) {
      if (!l.equals(source)) {
        l.changedLap(changeEvent);
      }
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void firePointChanged(Object source) {
    for (ChangePointsListener l : listenerList) {
      if (!l.equals(source)) {
        l.changedPoint(changeEvent);
      }
    }
  }

}
