package fr.turtlesport.geo.garmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.turtlesport.geo.AbstractGeoSegment;
import fr.turtlesport.geo.IGeoPositionWithAlt;

/**
 * @author Denis Apparicio
 * 
 */
public class Lap extends AbstractGeoSegment {

  private Date             startTimeXml;

  private double           distanceMeters;

  private int              calories;

  private int              averageHeartRateBpm = 0;

  private int              maximumHeartRateBpm = 0;

  private String           intensity;

  private int              cadence;

  private String           triggerMethod;

  private ArrayList<Track> listTrack;

  private int              avgHeartRate        = -1;

  private int              maxHeartRate        = -1;

  private double           maxSpeed            = 0;

  private long             totalTime           = 0;

  /**
   * 
   * @param startTime
   * @param index
   */
  public Lap(Date startTime, int index) {
    super(index);
    this.startTimeXml = startTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoSegment#getTotalPauseTime()
   */
  @Override
  public long getTotalPauseTime() {
    return super.getTotalTime() - getTotalTime();
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
   * @see fr.turtlesport.geo.AbstractGeoSegment#getTotalTime()
   */
  @Override
  public long getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(long totalTime) {
    this.totalTime = totalTime;
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
    if (listTrack != null) {
      for (Track t : listTrack) {
        if (t.getTrackPoints() != null) {
          for (TrackPoint p : t.getTrackPoints()) {
            list.add(p);
          }
        }
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

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.AbstractGeoSegment#distance()
   */
  @Override
  public double distance() {
    return distanceMeters;
  }

  /**
   * @return the distanceMeters
   */
  public double getDistanceMeters() {
    return distanceMeters;
  }

  /**
   * @param distanceMeters
   *          the distanceMeters to set
   */
  public void setDistanceMeters(double distanceMeters) {
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
   * @return the intensity
   */
  public String getIntensity() {
    return intensity;
  }

  /**
   * @param intensity
   *          the intensity to set
   */
  public void setIntensity(String intensity) {
    this.intensity = intensity;
  }

  /**
   * @return the cadence
   */
  public int getCadence() {
    return cadence;
  }

  /**
   * @param cadence
   *          the cadence to set
   */
  public void setCadence(int cadence) {
    this.cadence = cadence;
  }

  /**
   * @return the triggerMethod
   */
  public String getTriggerMethod() {
    return triggerMethod;
  }

  /**
   * @param triggerMethod
   *          the triggerMethod to set
   */
  public void setTriggerMethod(String triggerMethod) {
    this.triggerMethod = triggerMethod;
  }

  /**
   * Ajoute une track.
   * 
   * @param t
   *          la track.
   */
  public void addTrack(Track t) {
    if (listTrack == null) {
      synchronized (Lap.class) {
        listTrack = new ArrayList<Track>();
      }
    }
    listTrack.add(t);
  }

  /**
   * Restitue la liste des pistes.
   * 
   * @return la liste des pistes.
   */
  public List<Track> getTracks() {
    if (listTrack == null) {
      return Collections.emptyList();
    }
    return listTrack;
  }

  /**
   * Restitue la piste &agrave; l'index sp&eacute;cifi&eacute;.
   * 
   * @param index
   *          l'index du tour interm&eacute;diaire.
   * @throws IndexOutofBoundException
   */
  public Track getTrack(int index) {
    if (listTrack == null) {
      throw new IndexOutOfBoundsException("size =0, index=" + index);
    }
    return listTrack.get(index);
  }

  /**
   * Restitue le nombre de piste.
   * 
   * @return le nombre de piste.
   */
  public int getTrackSize() {
    return (listTrack == null) ? 0 : listTrack.size();
  }

}
