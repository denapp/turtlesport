package fr.turtlesport.geo.fit;

import com.garmin.fit.RecordMesg;

import fr.turtlesport.geo.GeoPositionWithAlt;

/**
 * @author Denis Apparicio
 * 
 */
public class FitPoint extends GeoPositionWithAlt {

  public FitPoint(RecordMesg mesg) {
    if (mesg.getPositionLat() != null && mesg.getPositionLong() != null) {
      setLatitude(mesg.getPositionLat());
      setLongitude(mesg.getPositionLong());
    }

    setDate(mesg.getTimestamp().getDate());

    if (mesg.getHeartRate() != null) {
      setHeartRate(mesg.getHeartRate());
    }

    if (mesg.getDistance() != null) {
      setDistanceMeters(mesg.getDistance());
    }

    if (mesg.getCadence() != null) {
      setCadence(mesg.getCadence());
    }

    if (mesg.getAltitude() != null) {
      setElevation(mesg.getAltitude());
    }
  }

}
