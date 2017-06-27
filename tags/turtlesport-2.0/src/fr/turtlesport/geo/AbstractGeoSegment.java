package fr.turtlesport.geo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractGeoSegment implements IGeoSegment {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractGeoSegment.class);
  }

  private int                 index;

  private Date                startTime;

  public AbstractGeoSegment(int index) {
    super();
    this.index = index;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#index()
   */
  public int index() {
    return index;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#distance()
   */
  public double distance() {
    List<IGeoPositionWithAlt> list = getPoints();
    if (list == null || list.size() < 2) {
      return 0;
    }

    IGeoPosition[] tab = new IGeoPosition[list.size()];
    list.toArray(tab);
    return GeoUtil.computeDistance(tab);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getStartTime()
   */
  public Date getStartTime() {
    if (startTime == null) {
      List<IGeoPositionWithAlt> list = getPoints();
      if (list != null) {
        for (IGeoPositionWithAlt geo : list) {
          if (geo.getDate() != null
              && geo.getDate().after(UsbPacketInputStream.date1989())) {
            startTime = geo.getDate();
            return startTime;
          }
        }
      }
    }
    return startTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#setStartTime(java.util.Date)
   */
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getTotalTime()
   */
  public long getTotalTime() {
    log.debug(">>getTotalTime");

    List<IGeoPositionWithAlt> list = getPoints();
    if (list == null || list.size() < 2) {
      return 0;
    }

    SimpleDateFormat df = null;
    if (log.isDebugEnabled()) {
      df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
      log.debug("nb points : " + list.size());

    }
    int i = 0;
    Date dateDeb = null, dateFin = null;
    for (i = 0; i < list.size() - 1; i++) {
      if (list.get(i).getDate() != null) {
        dateDeb = list.get(i).getDate();
        if (log.isDebugEnabled()) {
          log.debug("dateDeb=" + df.format(dateDeb));
        }
        break;
      }
    }
    if (dateDeb == null) {
      return 0;
    }
    for (i = list.size() - 1; i > 0; i--) {
      if (list.get(i).getDate() != null) {
        dateFin = list.get(i).getDate();
        if (log.isDebugEnabled()) {
          log.debug("dateFin=" + df.format(dateFin));
        }
        break;
      }
    }
    if (dateFin == null) {
      return 0;
    }

    Calendar c = Calendar.getInstance();
    c.setTime(dateFin);
    long timeTot = c.getTimeInMillis();
    c.setTime(dateDeb);
    timeTot -= c.getTimeInMillis();

    log.debug("<<getTotalTime timeTot=" + timeTot);
    return timeTot;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getTotalMovingTime()
   */
  @Override
  public long getTotalPauseTime() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getAvgHeartRate()
   */
  public int getAvgHeartRate() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getCalories()
   */
  public int getCalories() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getMaxHeartRate()
   */
  public int getMaxHeartRate() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getMaxSpeed()
   */
  public double getMaxSpeed() {
    return 0;
  }

}
