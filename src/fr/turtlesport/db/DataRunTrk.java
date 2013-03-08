package fr.turtlesport.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.unit.DistanceUnit;

/**
 * @author Denis Apparicio
 * 
 */
public class DataRunTrk implements Cloneable {
  private static final double INVALID2       = Math.pow(2, 31) - 1;

  private static final int    INVALID        = 0x7FFFFFFF;

  private static final float  INVALID_DIST   = (float) 9.9E24;

  private int                 id;

  private int                 latitude       = INVALID;

  private int                 longitude      = INVALID;

  private Timestamp           time;

  private float               altitude;

  private float               distance       = -1;

  private int                 heartRate      = 0;

  private int                 cadence        = 0xff;

  private int                 temperature    = 0xff;

  /** Vitesse en km/h */
  private double              speed          = 0;

  /** Allure en mn/km */
  private double              pace           = 0;

  private double              MAX_PACE_VALUE = 20;

  /**
   * 
   */
  public DataRunTrk() {
    super();
  }

  /**
   * 
   */
  public DataRunTrk(int id,
                    Timestamp time,
                    int longitude,
                    int latitude,
                    int heartRate,
                    int cadence,
                    float distance,
                    float altitude,
                    int temperature) {
    super();
    this.id = id;
    this.time = time;
    this.longitude = longitude;
    this.latitude = latitude;
    this.heartRate = heartRate;
    this.cadence = cadence;
    this.distance = distance;
    this.altitude = altitude;
    this.temperature = temperature;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      // ne devrait pas arriver
      return null;
    }
  }

  /**
   * @return
   */
  public int getTemperature() {
    return temperature;
  }

  /**
   * @param temperature
   */
  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  /**
   * @return the altitude
   */
  public float getAltitude() {
    return altitude;
  }

  /**
   * D&eacute;termine si ce point GPS est une pause.
   * 
   * @return <code>true</code> si si ce point GPS est une pause,
   *         <code>false</code> sinon.
   */
  public boolean isPause() {
    return !isValidGps() && !isValidCadence() && heartRate == 0;
  }

  /**
   * D&eacute;termine si coordonn&eacute;es GPS sont valides.
   * 
   * @return <code>true</code> si coordonn&eacute;es GPS sont valides,
   *         <code>false</code> sinon.
   */
  public boolean isValidGps() {
    return !(getLongitude() == 0 && getLatitude() == 0)
           && !(getLongitude() >= INVALID2 && getLatitude() >= INVALID2);
  }

  /**
   * D&eacute;termine si la distance est valide.
   * 
   * @return <code>true</code> si la distance est valide, <code>false</code>
   *         sinon.
   */
  public boolean isValidDistance() {
    return getDistance() >= 0 && getDistance() < INVALID_DIST;
  }

  /**
   * @param altitude
   *          the altitude to set
   */
  public void setAltitude(float altitude) {
    this.altitude = altitude;
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
   * @return the distance
   */
  public float getDistance() {
    return distance;
  }

  /**
   * @param distance
   *          the distance to set
   */
  public void setDistance(float distance) {
    this.distance = distance;
  }

  /**
   * @return the heartRate
   */
  public int getHeartRate() {
    return heartRate;
  }

  /**
   * @param heartRate
   *          the heartRate to set
   */
  public void setHeartRate(int heartRate) {
    this.heartRate = heartRate;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * @return the latitude
   */
  public int getLatitude() {
    return latitude;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(int latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the longitude
   */
  public int getLongitude() {
    return longitude;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(int longitude) {
    this.longitude = longitude;
  }

  /**
   * @return the time
   */
  public Timestamp getTime() {
    return time;
  }

  /**
   * @param time
   *          the time to set
   */
  public void setTime(Timestamp time) {
    this.time = time;
  }

  /**
   * D&eacute;termine si la cadence est valide.
   * 
   * @return <code>true</code> si la cadence est valide, <code>false</code>
   *         sinon
   */
  public boolean isValidCadence() {
    return (cadence > 0 && cadence < 0xff);
  }

  /**
   * D&eacute;termine si la tempe&eacute;rature est valide.
   * 
   * @return <code>true</code> si la tempe&eacute;rature est valide,
   *         <code>false</code> sinon
   */
  public boolean isValidTemperature() {
    return (temperature != 0xff);
  }

  /**
   * D&eacute;termine si l'altitude est valide.
   * 
   * @return <code>true</code> si l'altitude est valide, <code>false</code>
   *         sinon
   */
  public boolean isValidAltitude() {
    return (altitude != 1.0e25f);
  }

  /**
   * Restitue la vitesse en km/h.
   * 
   * @return la vitesse en km/h.
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * Valorise la vitesse en km/h.
   * 
   * @param speed
   *          la nouvelle valeur
   */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * Restitue l'allure en mn/km.
   * 
   * @return l'allure en mn/km
   */
  public double getPace() {
    return pace;
  }

  /**
   * 
   * Valorise l'allure en mn/km.
   * 
   * @param pace
   *          la nouvelle valeur
   */
  public void setPace(double pace) {
    this.pace = (pace > MAX_PACE_VALUE) ? MAX_PACE_VALUE : pace;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "[" + latitude + ", " + longitude + ", " + altitude + ", "
           + distance + ", " + time + "]";
  }

  /**
   * Clone en remplacant les points invalide avec un intervalle
   * 
   * @param listTrksOriginal
   * @param intervalle
   *          en seconde
   * @return
   */
  public static List<DataRunTrk> cloneList(List<DataRunTrk> listTrksOriginal,
                                           int interval) {
    List<DataRunTrk> listTrksClone = cloneList(listTrksOriginal);
    List<DataRunTrk> listTrks = new ArrayList<DataRunTrk>();

    if (interval < 1) {
      interval = 1;
    }

    int len = listTrksClone.size() - 1;
    listTrks.add(listTrksClone.get(0));
    for (int i = 1; i < len; i++) {
      // long time = listTrksClone.get(i).getTime().getTime() -
      // listTrksClone.get(0).getTime().getTime();
      // if (time == interval) {
      // interval += interval;
      // listTrks.add(listTrksClone.get(i));
      // }
      // else if (time > interval) {
      // // compute trk
      // DataRunTrk trk = listTrksClone.get(i);
      // // float distDiff = trk.getDistance() - listTrksClone.get(i
      // -1).getDistance();
      // // long timeDiff = listTrksClone.get(i).getTime().getTime() -
      // listTrksClone.get(i-1).getTime().getTime();
      // // trk.setTime(new Timestamp(interval));
      // // double diff = 1.0*(time -interval) * distDiff /timeDiff;
      // // trk.setDistance( (float) (trk.getDistance() - diff));
      // listTrks.add(listTrksClone.get(i));
      // interval += interval;
      // }
      if (i % 2 == 0)
        listTrks.add(listTrksClone.get(i));
    }
    return listTrks;
  }

  /**
   * Clone en remplacant les points invalide.
   * 
   * @param listTrksOriginal
   * @return
   */
  public static List<DataRunTrk> cloneList(List<DataRunTrk> listTrksOriginal) {
    List<DataRunTrk> listTrks = new ArrayList<DataRunTrk>();

    if (listTrksOriginal != null && listTrksOriginal.size() > 0) {
      // recherche du premier point valide
      int i;
      DataRunTrk trkValid = null;
      for (i = 0; i < listTrksOriginal.size(); i++) {
        DataRunTrk t = listTrksOriginal.get(i);
        if (t.isValidGps() && t.isValidDistance()) {
          trkValid = t;
          break;
        }
      }
      if (i > 0 && i < listTrksOriginal.size()) {
        // remplacement des premiers points invalides
        for (int index = 0; index < i; index++) {
          DataRunTrk d = cloneInvalid(listTrksOriginal.get(index), trkValid, 0);
          listTrks.add(d);
        }
      }

      // points suivants
      for (int index = i; index < listTrksOriginal.size(); index++) {
        DataRunTrk t = listTrksOriginal.get(index);
        if (t.isValidGps() && t.isValidDistance()) {
          trkValid = t;
          listTrks.add(t);
        }
        else {
          DataRunTrk d = cloneInvalid(listTrksOriginal.get(index),
                                      trkValid,
                                      trkValid.getDistance());
          listTrks.add(d);
        }
      }

      // conversion distance
      if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
        for (DataRunTrk t : listTrks) {
          t.setDistance((float) DistanceUnit.convert(DistanceUnit.unitKm(),
                                                     DistanceUnit
                                                         .getDefaultUnit(),
                                                     t.getDistance()));
        }
      }

      // vitesse
      for (int index = 0; index < listTrks.size() - 1; index++) {
        long time = listTrks.get(index + 1).getTime().getTime()
                    - listTrks.get(index).getTime().getTime();
        float dist = listTrks.get(index + 1).getDistance()
                     - listTrks.get(index).getDistance();

        double speed = (time == 0) ? 0.D : (dist / time) * 3600;
        listTrks.get(index + 1).setSpeed(speed);
      }
      if (listTrks.size() >= 2) {
        listTrks.get(0).setSpeed(listTrks.get(1).getSpeed());
      }
    }

    return listTrks;
  }

  private static DataRunTrk cloneInvalid(DataRunTrk current,
                                         DataRunTrk trkValid,
                                         float distance) {
    return new DataRunTrk(current.getId(),
                          current.getTime(),
                          trkValid.getLongitude(),
                          trkValid.getLatitude(),
                          current.getHeartRate(),
                          current.getCadence(),
                          distance,
                          trkValid.getAltitude(),
                          current.getTemperature());
  }

}
