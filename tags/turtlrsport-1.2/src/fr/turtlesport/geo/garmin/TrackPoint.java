package fr.turtlesport.geo.garmin;

import fr.turtlesport.geo.GeoPositionWithAlt;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class TrackPoint extends GeoPositionWithAlt {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(TrackPoint.class);
  }

  /**
   * 
   */
  public TrackPoint() {
    super();
  }

  /**
   * @param hasSensorState
   *          the hasSensorState to set
   */
  public void setSensorState(String sensorState) {
    if ("Present".equals(sensorState)) {
      setSensor(true);
    }
    else if ("Absent".equals(sensorState)) {
      setSensor(false);
    }
    else {
      log.warn("sensorState=" + sensorState);
      setSensor(false);
    }
  }

  /**
   * @param position
   *          the position to set
   */
  public void setPosition(Position position) {
    if (position == null) {
      setLatitude(INVALID_POS);
      setLongitude(INVALID_POS);
    }
    else {
      setLatitude(position.getLatitude());
      setLongitude(position.getLongitude());
    }
  }

}
