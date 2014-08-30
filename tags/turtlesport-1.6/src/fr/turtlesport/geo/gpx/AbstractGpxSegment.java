package fr.turtlesport.geo.gpx;

import java.util.List;

import fr.turtlesport.geo.AbstractGeoSegment;
import fr.turtlesport.geo.IGeoPositionWithAlt;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractGpxSegment extends AbstractGeoSegment {

  private int avgHeartRate = -1;

  private int maxHeartRate = -1;

  public AbstractGpxSegment(int index) {
    super(index);
  }

  private synchronized void computeHeart() {
    if (avgHeartRate == -1) {
      List<IGeoPositionWithAlt> points = getPoints();
      if (points != null && points.size() > 0) {
        long avg = 0;
        maxHeartRate = 0;
        for (IGeoPositionWithAlt p : points) {
          avg += (p.getHeartRate() < 0) ? 0 : p.getHeartRate();
          maxHeartRate = Math.max(maxHeartRate, p.getHeartRate());
        }
        avgHeartRate = (int) (avg / points.size());
      }
    }
  }

  @Override
  public int getAvgHeartRate() {
    computeHeart();
    return avgHeartRate;
  }

  @Override
  public int getMaxHeartRate() {
    computeHeart();
    return maxHeartRate;
  }

}
