package fr.turtlesport.protocol.data;

import java.util.List;

import fr.turtlesport.device.garmin.GarminUsbDevice;
import fr.turtlesport.log.TurtleLogger;

public abstract class AbstractWorkout extends AbstractData {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractWorkout.class);
  }

  /** Protocole A1002. */
  private static final String PROTOCOL = "A1002";

  /** Nombre max de lap valide */
  protected static final int     MAX_VALID_STEP = 20;

  /** Etapes. */
  protected List<AbstractStep> listSteps;

  /** Nom de l'etape. */
  private String               name;

  /** Type de sport. */
  private int                  sportType;

  /**
   * @return
   */
  public static AbstractWorkout newInstance() {
    log.debug(">>newInstance");

    AbstractWorkout res;
    String[] data = GarminUsbDevice.getDevice().getDataProtocol(PROTOCOL);
    if (data.length != 1) {
      throw new RuntimeException("pas de protocole " + PROTOCOL);
    }
    log.debug(PROTOCOL + "-->" + data[0]);

    if (D1008WorkoutType.PROTOCOL.equals(data[0])) {
      res = new D1008WorkoutType();
    }
    else if (D1002WorkoutType.PROTOCOL.equals(data[0])) {
      res = new D1002WorkoutType();
    }
    else {
      throw new RuntimeException("protocole non supporte" + data[0]);
    }

    log.debug("<<newInstance");
    return res;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getSportType() {
    return sportType;
  }

  public void setSportType(int sportType) {
    this.sportType = sportType;
  }

  public List<AbstractStep> getListSteps() {
    return listSteps;
  }

  /**
   * D&eacute;termine si le sport est de la course &agrve; pied.
   * 
   * @return <code>true</code> si course &agrve; pied.
   */
  public boolean isSportRunning() {
    return isSportRunning(sportType);
  }

  /**
   * D&eacute;termine si le sport est du v&eacute,lo.
   * 
   * @return <code>true</code> si le sport est du v&eacute,lo.
   */
  public boolean isSportBike() {
    return isSportBike(sportType);
  }

  /**
   * D&eacute;termine si le sport n'est pas v&eacute,lo ou de la la course
   * &agrve; pied.
   * 
   * @return <code>true</code> si n'est pas v&eacute,lo ou de la la course
   *         &agrve; pied.
   */
  public boolean isSportOther() {
    return isSportOther(sportType);
  }

}
