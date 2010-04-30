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
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.GeoPositionMapKit;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public final class ModelPointsManager {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelPointsManager.class);
  }

  protected transient ChangePointsEvent changeEvent     = new ChangePointsEvent(this);

  private List<GeoPositionMapKit>       listGeo;

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
  public DataRunLap[] getRunLaps() {
    return runLaps;
  }

  /**
   * Restitue le nombre de tours interm&eacute;m&eacute;diares.
   * 
   * @return le nombre de tours interm&eacute;m&eacute;diares.
   */
  public int runLapsSize() {
    return (runLaps == null) ? 0 : runLaps.length;
  }

  /**
   * Restitue index du tour.
   * 
   * @return index du tour.
   */
  public int getLapIndex() {
    return lapIndex;
  }

  /**
   * Valorise le run.
   * 
   * @param dataRun
   *          the dataRun to set
   * @throws SQLException
   */
  public final void setDataRun(DataRun dataRun) throws SQLException {
    if (dataRun != null && dataRun.equals(this.dataRun)) {
      return;
    }

    this.dataRun = dataRun;
    listGeo = null;
    lapGeoBegin = null;
    lapGeoEnd = null;
    currentMapPoint = 0;
    runLaps = null;
    lapIndex = -1;

    if (dataRun != null) {
      // Les points
      listTrks = RunTrkTableManager.getInstance().getTrks(dataRun.getId());
      if (listTrks != null) {
        if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
          for (DataRunTrk t : listTrks) {
            t.setDistance((float) DistanceUnit.convert(DistanceUnit.unitKm(),
                                                       DistanceUnit
                                                           .getDefaultUnit(),
                                                       t.getDistance()));
          }
        }
      }

      // Les Laps
      runLaps = RunLapTableManager.getInstance().findLaps(dataRun.getId());

      // Les GeoPositions
      listGeo = new ArrayList<GeoPositionMapKit>();

      // recuperation des donnees
      if (listTrks != null) {
        for (int i = 0; i < listTrks.size(); i++) {
          if (listTrks.get(i).isValidGps()) {
            listGeo.add(new GeoPositionMapKit(listTrks.get(i), i));
          }
        }
      }
    }

    fireAllPointsChanged();
  }

  /**
   * Valorise le tour interm&eacute;m&eaute;diaire.
   * 
   * @param newLapIndex
   *          index du tour.
   */
  public void setLap(int newLapIndex) {
    if ((newLapIndex > runLapsSize() - 1) || newLapIndex == lapIndex) {
      return;
    }
    lapIndex = newLapIndex;

    // calcul des geopositions
    lapGeoBegin = null;
    lapGeoEnd = null;
    if (runLaps != null) {
      DataRunTrk deb = null;
      DataRunTrk end = null;

      try {
        // Map
        deb = RunLapTableManager.getInstance().lapTrkBegin(runLaps[lapIndex]
                                                               .getId(),
                                                           runLaps[lapIndex]
                                                               .getLapIndex());
        if (deb != null) {
          end = RunLapTableManager.getInstance().lapTrkEnd(runLaps[lapIndex]
                                                               .getId(),
                                                           runLaps[lapIndex]
                                                               .getLapIndex());
        }

        if (deb != null && end != null) {
          lapGeoBegin = makeGeoMapFromGarmin(deb.getLatitude(), deb
              .getLongitude());
          lapGeoEnd = makeGeoMapFromGarmin(end.getLatitude(), end
              .getLongitude());
        }
      }
      catch (SQLException e) {
        lapGeoBegin = null;
        lapGeoEnd = null;
        log.error("", e);
      }

    }

    // on deckenche
    fireLapChanged();
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
   * Valorise l'index du point courant de la map.
   * 
   * @param currentMapPoint
   *          la nouvelle valeur.
   * @return <code>true</code> si dernier point.
   */
  public final void setMapCurrentPoint(int currentMapPoint) {
    if (this.currentMapPoint != currentMapPoint) {
      this.currentMapPoint = currentMapPoint;
      firePointChanged();
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
    listenerList.add(l);
    if (hasPoints()) {
      l.changedAllPoints(changeEvent);
      l.changedPoint(changeEvent);
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
  protected void fireAllPointsChanged() {
    for (ChangePointsListener l : listenerList) {
      l.changedAllPoints(changeEvent);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void fireLapChanged() {
    for (ChangePointsListener l : listenerList) {
      l.changedLap(changeEvent);
    }
  }

  /**
   * Execute chaque<code>ChangeListener</code>.
   * 
   */
  protected void firePointChanged() {
    for (ChangePointsListener l : listenerList) {
      l.changedPoint(changeEvent);
    }
  }

  /**
   * Conversion d'un point garmin en point g&eacute;graphique.
   * 
   * @param latitude
   * @param longitude
   * @return
   */
  private GeoPosition makeGeoMapFromGarmin(int gLatitude, int gLongitude) {
    if (GeoUtil.isInvalidGarminGpx(gLatitude)
        || GeoUtil.isInvalidGarminGpx(gLongitude)) {
      return null;
    }
    return new GeoPosition(GeoUtil.makeLatitudeFromGarmin(gLatitude), GeoUtil
        .makeLongitudeFromGarmin(gLongitude));
  }

}
