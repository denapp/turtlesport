package fr.turtlesport.ui.swing.component;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.util.GeoUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class GeoPositionMapKit extends GeoPosition {
  private long  time;

  private float distance;

  private int   heartRate;

  private int   index;

  public GeoPositionMapKit(DataRunTrk p, int index) {
    super(GeoUtil.makeLatitudeFromGarmin(p.getLatitude()), GeoUtil
        .makeLongitudeFromGarmin(p.getLongitude()));
    this.index = index;
    time = p.getTime().getTime();
    distance = p.getDistance();
    heartRate = p.getHeartRate();
  }

  /**
   * @return the time
   */
  public long getTime() {
    return time;
  }

  /**
   * @return the distance
   */
  public float getDistance() {
    return distance;
  }

  /**
   * @return the heartRate
   */
  public int getHeartRate() {
    return heartRate;
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

}
