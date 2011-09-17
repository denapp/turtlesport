package fr.turtlesport.geo.garmin.hst;

import fr.turtlesport.geo.garmin.tcx.Activity;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class Sport {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Activity.class);
  }

  /** Running. */
  public static final int     SPORT_TYPE_RUNNING = 0;

  /** Velo. */
  public static final int     SPORT_TYPE_BIKE    = 1;

  /** OTHER. */
  public static final int     SPORT_TYPE_OTHER   = 2;

  private int                 sportType;

  private Run                 run;

  /**
   * @param sport
   */
  protected Sport(String sport) {
    super();

    log.debug(">>Sport");

    if ("Running".equals(sport)) {
      sportType = SPORT_TYPE_RUNNING;
    }
    else if ("Biking".equals(sport)) {
      sportType = SPORT_TYPE_BIKE;
    }
    else if ("Other".equals(sport)) {
      sportType = SPORT_TYPE_OTHER;
    }
    else {
      log.warn("sport=" + sport);
      sportType = SPORT_TYPE_OTHER;
    }

    log.debug("<<Sport");
  }

  /**
   * Restitue le sport de cette activit&eacute;.
   * 
   * @return le sport de cette activit&eacute;.
   */
  public int getSportType() {
    return sportType;
  }

  /**
   * Restitue le <code>Run</code> de ce sport.
   * 
   * @return le <code>Run</code> de ce sport.
   */
  public Run getRun() {
    return run;
  }

  /**
   * Valorise le <code>Run</code> de ce sport.
   * 
   * @param run
   *          le <code>Run</code> de ce sport.
   */
  public void setRun(Run run) {
    this.run = run;
  }

}
