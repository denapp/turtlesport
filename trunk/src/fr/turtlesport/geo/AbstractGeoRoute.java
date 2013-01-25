package fr.turtlesport.geo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.db.DataActivityBike;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.DataActivityRun;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
/**
 * @author denisapparicio
 * 
 */
public abstract class AbstractGeoRoute implements IGeoRoute {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractGeoRoute.class);
  }

  /** Distance totale parcourue. */
  private double              distanceTot = -1;

  /** Temps total en milli secondes. */
  private long                timeTot     = -1;

  private int                 sportType;

  private Object              extra;

  /**
   * 
   */
  public AbstractGeoRoute() {
    log.debug(">>AbstractGeoRoute");

    // initialisation du sport par defaut
    try {
      sportType = UserActivityTableManager.getInstance()
          .retreiveDefaultActivitySportType();
    }
    catch (SQLException sqle) {
      log.error("", sqle);
      sportType = DataActivityOther.SPORT_TYPE;
    }

    switch (sportType) {
      case DataActivityRun.SPORT_TYPE:
      case DataActivityBike.SPORT_TYPE:
      case DataActivityOther.SPORT_TYPE:
        break;
      default:
        sportType = DataActivityOther.SPORT_TYPE;
        break;
    }

    log.debug("sportType=" + sportType);
    
    log.debug("<<AbstractGeoRoute");
  }

  public void initTimeTot(long timeTot) {
    if (this.timeTot == -1 && timeTot > 0) {
      this.timeTot = timeTot;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getSportType()
   */
  public int getSportType() {
    return sportType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#setSportType(int)
   */
  public void setSportType(int sportType) {
    switch (sportType) {
      case SPORT_TYPE_RUNNING:
        this.sportType = SPORT_TYPE_RUNNING;
        break;
      case SPORT_TYPE_BIKE:
        this.sportType = SPORT_TYPE_BIKE;
        break;
      default:
        this.sportType = SPORT_TYPE_OTHER;
        break;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getDistanceTot()
   */
  public double distanceTot() {
    if (distanceTot == -1) {
      synchronized (AbstractGeoRoute.class) {

        List<IGeoPositionWithAlt> list = getAllPoints();
        if (list == null || list.size() < 2) {
          return 0;
        }

        IGeoPosition[] tab = new IGeoPosition[list.size()];
        list.toArray(tab);
        distanceTot = GeoUtil.computeDistance(tab);
      }
    }
    return distanceTot;
  }

  public void setDistanceTot(double distanceTot) {
    this.distanceTot = distanceTot;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#totalTime()
   */
  public long totalTime() {
    if (timeTot == -1) {
      if (hasPointsDate()) {
        synchronized (AbstractGeoRoute.class) {
          timeTot = 0;
          if (getAllPoints().size() > 1) {
            Calendar c = Calendar.getInstance();
            c.setTime(getAllPoints().get(getAllPoints().size() - 1).getDate());

            timeTot = c.getTimeInMillis();
            c.setTime(getAllPoints().get(0).getDate());
            timeTot -= c.getTimeInMillis();
          }
        }
      }
    }
    return timeTot;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#hasPointsDate()
   */
  public boolean hasPointsDate() {
    List<IGeoPositionWithAlt> list = getAllPoints();
    if (list == null) {
      return false;
    }
    for (IGeoPositionWithAlt geo : list) {
      if (geo.getDate() == null
          || geo.getDate().before(UsbPacketInputStream.date1989())) {
        return false;
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getExtra()
   */
  public Object getExtra() {
    return extra;
  }

  /**
   * Valorise les donn&eacute;es compl&eacute;mentaires.
   * 
   * @param extra
   *          la nouvelle valeur.
   */
  public void setExtra(Object extra) {
    this.extra = extra;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#getStartTime()
   */
  public Date getStartTime() {
    List<IGeoPositionWithAlt> list = getAllPoints();
    if (list == null) {
      return null;
    }
    for (IGeoPositionWithAlt geo : list) {
      if (geo.getDate() != null
          && geo.getDate().after(UsbPacketInputStream.date1989())) {
        return geo.getDate();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#setStartTime(java.util.Date)
   */
  public void setStartTime(Date date) {
    log.debug(">>setStartTime");

    SimpleDateFormat df = null;

    List<IGeoPositionWithAlt> list = getAllPoints();
    if (list == null) {
      log.debug("list null");
      return;
    }

    if (date == null) {
      throw new IllegalArgumentException("date est null");
    }
    if (date.after(new Date())) {
      throw new IllegalArgumentException("date est apres la date du jour");
    }
    if (date.before(UsbPacketInputStream.date1989())) {
      throw new IllegalArgumentException("date est avant "
                                         + UsbPacketInputStream.date1989());
    }

    long newStartTime = date.getTime();
    Date dateOrigin = getStartTime();
    if (dateOrigin == null) {
      log.warn("dateOrigin est null");
      return;
    }

    if (log.isDebugEnabled()) {
      df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
      log.debug("dateOrigin=" + df.format(dateOrigin));
      log.debug("newStartTime=" + df.format(date));
    }

    long startTimeOrigin = dateOrigin.getTime();
    long diff = (newStartTime - startTimeOrigin);
    if (Math.abs(diff) < 1000) {
      // si inferieur a 1 seconde on ne fait rien
      log.debug("Moins d une seconde");
      return;
    }

    // Mis a jour des points
    for (IGeoPositionWithAlt geo : list) {
      if (geo.getDate() != null
          && geo.getDate().after(UsbPacketInputStream.date1989())) {
        Date d = new Date(geo.getDate().getTime() + diff);
        if (log.isDebugEnabled()) {
          log.debug("diff=" + diff + " ; oldDate=" + df.format(geo.getDate())
                    + " ; newDate=" + df.format(d));
        }
        geo.setDate(d);
      }
    }

    // Mis a jour des Laps
    for (IGeoSegment seg : getSegments()) {
      if (seg.getStartTime() != null) {
        Date d = new Date(seg.getStartTime().getTime() + diff);
        seg.setStartTime(d);
      }
    }

    log.debug("<<setStartTime");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#setTotalTime(long)
   */
  public void setTotalTime(long totalTime) {
    log.debug(">>setTotalTime totalTime=" + totalTime);

    if (totalTime < 1000) {
      throw new IllegalArgumentException("totalTime=" + totalTime);
    }
    if (!hasPointsDate()) {
      return;
    }

    SimpleDateFormat df = null;
    if (log.isDebugEnabled()) {
      df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
    }

    double timeTotOrigin = totalTime();
    long startTime = getStartTime().getTime();
    for (IGeoPositionWithAlt geo : getAllPoints()) {
      long millis = (long) (startTime + totalTime
                                        * ((geo.getDate().getTime() - startTime) / timeTotOrigin));
      Date d = new Date(millis);
      if (log.isDebugEnabled()) {
        log.debug("millis=" + millis + " ; oldDate=" + df.format(geo.getDate())
                  + " ; newDate=" + df.format(d));
      }
      geo.setDate(d);
    }

    log.debug("<<setTotalTime");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoRoute#update(java.util.Date, long)
   */
  public void update(Date startTime, long totalTime) {
    if (log.isDebugEnabled()) {
      log
          .debug(">>update startTime=" + startTime + " ; totalTime="
                 + totalTime);
    }

    List<IGeoPositionWithAlt> list = getAllPoints();
    if (list == null) {
      log.debug("list null");
      return;
    }

    // verification de la date
    if (startTime == null) {
      throw new IllegalArgumentException("date est null");
    }
    if (startTime.after(new Date())) {
      throw new IllegalArgumentException("date est apres la date du jour");
    }
    if (startTime.before(UsbPacketInputStream.date1989())) {
      throw new IllegalArgumentException("date est avant "
                                         + UsbPacketInputStream.date1989());
    }

    // verification de la duree
    if (totalTime < 1000) {
      throw new IllegalArgumentException("totalTime=" + totalTime);
    }

    SimpleDateFormat df = null;
    if (log.isDebugEnabled()) {
      df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
    }

    // on verifie si changement de date et/ou duree
    boolean isNewDate = true;
    Date dateOrigin = getStartTime();
    if (dateOrigin != null) {
      isNewDate = (Math.abs((double) (dateOrigin.getTime() - startTime
          .getTime())) > 1000);
    }
    boolean isNewTotalTime = (totalTime() == -1)
                             || (Math.abs((double) (totalTime() - totalTime)) > 1000);

    if (hasPointsDate()) {
      if (isNewDate) {
        setStartTime(startTime);
      }
      if (isNewTotalTime) {
        setTotalTime(totalTime);
      }
    }
    else {
      double distance = 0;
      double speed = distanceTot() / totalTime;
      double delay;
      list.get(0).setDate(startTime);
      if (log.isDebugEnabled()) {
        log.debug("distance=0 ; date=" + df.format(startTime));
      }
      for (int i = 1; i < list.size(); i++) {
        IGeoPositionWithAlt geo = list.get(i);
        if (!geo.isInvalidPosition() && geo.isValidDistance()) {
          distance = list.get(i).getDistanceMeters();
          delay = distance / speed;
          Date d = new Date((long) (startTime.getTime() + delay));
          if (log.isDebugEnabled()) {
            log.debug("distance=" + distance + " ; date=" + df.format(d));
          }
          list.get(i).setDate(d);
        }
      }
    }

    log.debug("<<update");
  }
  
  
}
