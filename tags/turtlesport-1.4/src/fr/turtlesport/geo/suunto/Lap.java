package fr.turtlesport.geo.suunto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.turtlesport.geo.AbstractGeoSegment;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class Lap extends AbstractGeoSegment {

  private Date                startTimeXml;

  private double              distanceMeters;

  private int                 calories;

  private int                 averageHeartRateBpm = 0;

  private int                 maximumHeartRateBpm = 0;

  protected List<SuuntoPoint> listPoints;

  private int                 avgHeartRate        = -1;

  private int                 maxHeartRate        = -1;

  private double              maxSpeed            = 0;

  private double              totalTimeSeconds;

  /**
   * 
   * @param index
   */
  public Lap(int index) {
    super(index);
  }

  /**
   * 
   * @param startTime
   * @param index
   */
  public Lap(Date startTime, int index) {
    super(index);
    this.startTimeXml = startTime;
  }

  protected void compute() {
    if (listPoints != null && listPoints.size() > 0) {
      SuuntoPoint pointBegin = listPoints.get(0);
      SuuntoPoint pointEnd = listPoints.get(listPoints.size() - 1);

      // Suppression des points GPS
      List<SuuntoPoint> newList = new ArrayList<SuuntoPoint>();
      newList.add(listPoints.get(0));

      int indexBegin = 0;
      double distance = listPoints.get(0).getDistanceMeters();
      double diff;
      final int ephe = 5;
      for (int i = 0; i < listPoints.size() - 2; i++) {
        diff = GeoUtil.computeDistance(listPoints.get(indexBegin),
                                       listPoints.get(i + 1));
        if (diff > ephe) {
          distance += diff;
          indexBegin = i + 1;
          listPoints.get(i + 1).setDistanceMeters(distance);
          newList.add(listPoints.get(i + 1));
        }
      }
      distance += GeoUtil.computeDistance(listPoints.get(listPoints.size()- 2),
                                          listPoints.get(listPoints.size()- 1));
      listPoints.get(listPoints.size()- 1).setDistanceMeters(distance);
      newList.add(listPoints.get(listPoints.size() - 1));

      listPoints = newList;
      
      // Temps
      setStartTime(pointBegin.getDate());

      // Temps total
      setTotalTimeSeconds((pointEnd.getDate().getTime() - pointBegin.getDate()
          .getTime()) / 1000.0);
      
      // Distance total
      distanceMeters = distance - listPoints.get(0).getDistanceMeters();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getTotalPauseTime()
   */
  @Override
  public long getTotalPauseTime() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.AbstractGeoSegment#getStartTime()
   */
  @Override
  public Date getStartTime() {
    return (startTimeXml == null) ? super.getStartTime() : startTimeXml;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#setStartTime(java.util.Date)
   */
  public void setStartTime(Date startTime) {
    this.startTimeXml = startTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getAvgHeartRate()
   */
  @Override
  public int getAvgHeartRate() {
    if (averageHeartRateBpm == 0) {
      if (avgHeartRate == -1) {
        avgHeartRate = 0;
        List<IGeoPositionWithAlt> list = getPoints();
        if (list != null) {
          int nb = 0;
          for (IGeoPositionWithAlt geo : list) {
            if (geo.getHeartRate() > 0) {
              avgHeartRate += geo.getHeartRate();
              nb++;
            }
          }
          if (nb != 0) {
            avgHeartRate /= nb;
          }
        }
      }
      return avgHeartRate;
    }
    return averageHeartRateBpm;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getMaxHeartRate()
   */
  @Override
  public int getMaxHeartRate() {
    if (maximumHeartRateBpm == 0) {
      if (maxHeartRate == -1) {
        maxHeartRate = 0;
        List<IGeoPositionWithAlt> list = getPoints();
        if (list != null) {
          for (IGeoPositionWithAlt geo : list) {
            if (geo.getHeartRate() > maxHeartRate) {
              maxHeartRate = geo.getHeartRate();
            }
          }
        }
      }
      return maxHeartRate;
    }
    return maximumHeartRateBpm;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getMaxSpeed()
   */
  @Override
  public double getMaxSpeed() {
    return maxSpeed;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getPoints()
   */
  public List<IGeoPositionWithAlt> getPoints() {
    List<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
    if (listPoints != null) {
      for (SuuntoPoint p : listPoints) {
        list.add(p);
      }
    }
    return list;
  }

  /**
   * Valorise la fr&eacute;quence cardiaque maximale.
   * 
   * @param maxHeartRate
   *          la fr&eacute;quence cardiaque maximale.
   */
  public void setMaxHeartRate(int maxHeartRate) {
    this.maxHeartRate = maxHeartRate;
  }

  /**
   * Valorise la vitesse maximale.
   * 
   * @param maxSpeed
   *          la vitesse maximale.
   */
  public void setMaxSpeed(double maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  /**
   * Valorise la fr&eacute;quence cardiaque moyenne.
   * 
   * @param avgHeartRate
   *          la fr&eacute;quence cardiaque moyenne.
   */
  public void setAvgHeartRate(int avgHeartRate) {
    this.avgHeartRate = avgHeartRate;
  }

  /**
   * Restitue la date du lap.
   * 
   * @return la date du lap.
   */
  public Date getStartTimeXml() {
    return startTimeXml;
  }

  /**
   * Valorise la date du lap.
   * 
   * @param startTimeXml
   *          la date du lap.
   */
  public void setStartTimeXml(Date startTimeXml) {
    this.startTimeXml = startTimeXml;
  }

  /**
   * @return the totalTimeSeconds
   */
  public double getTotalTimeSeconds() {
    return totalTimeSeconds;
  }

  /**
   * @param totalTimeSeconds
   *          the totalTimeSeconds to set
   */
  public void setTotalTimeSeconds(double totalTimeSeconds) {
    this.totalTimeSeconds = totalTimeSeconds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.AbstractGeoSegment#distance()
   */
  @Override
  public double distance() {
    return distanceMeters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.AbstractGeoSegment#getTotalTime()
   */
  @Override
  public long getTotalTime() {
    return (long) (totalTimeSeconds * 1000);
  }

  /**
   * @param distanceMeters
   *          the distanceMeters to set
   */
  public void setDistance(double distanceMeters) {
    this.distanceMeters = distanceMeters;
  }

  /**
   * @return the calories
   */
  @Override
  public int getCalories() {
    return calories;
  }

  /**
   * @param calories
   *          the calories to set
   */
  public void setCalories(int calories) {
    this.calories = calories;
  }

  /**
   * @return the averageHeartRateBpm
   */
  public int getAverageHeartRateBpm() {
    return averageHeartRateBpm;
  }

  /**
   * @param averageHeartRateBpm
   *          the averageHeartRateBpm to set
   */
  public void setAverageHeartRateBpm(int averageHeartRateBpm) {
    this.averageHeartRateBpm = averageHeartRateBpm;
  }

  /**
   * @return the maximumHeartRateBpm
   */
  public int getMaximumHeartRateBpm() {
    return maximumHeartRateBpm;
  }

  /**
   * @param maximumHeartRateBpm
   *          the maximumHeartRateBpm to set
   */
  public void setMaximumHeartRateBpm(int maximumHeartRateBpm) {
    this.maximumHeartRateBpm = maximumHeartRateBpm;
  }

  /**
   * Ajoute un point.
   * 
   * @param p
   *          le point.
   */
  public void addPoint(SuuntoPoint p) {
    if (listPoints == null) {
      synchronized (Lap.class) {
        listPoints = new ArrayList<SuuntoPoint>();
      }
    }
    listPoints.add(p);
  }

}
