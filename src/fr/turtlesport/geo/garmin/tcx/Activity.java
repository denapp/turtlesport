package fr.turtlesport.geo.garmin.tcx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.turtlesport.geo.garmin.Lap;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class Activity {
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

  private Date                id;

  private ArrayList<Lap>      listLap;

  private ActivityCreator     creator;

  /**
   * @param sport
   */
  protected Activity(String sport) {
    super();

    log.debug(">>Activity");

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

    log.debug("<<Activity");
  }

  public ActivityCreator getCreator() {
    return creator;
  }

  public void setCreator(ActivityCreator creator) {
    this.creator = creator;
  }

  /**
   * D&eacute;termine si cette activit&eacute; est running.
   * 
   * @return <code>true</code> si cette activit&eacute; est running.
   */
  public boolean isRunning() {
    return (sportType == SPORT_TYPE_RUNNING);
  }

  /**
   * D&eacute;termine si cette activit&eacute; est du v&eacute;lo.
   * 
   * @return <code>true</code> si cette activit&eacute; est du v&eacute;lo.
   */
  public boolean isBiking() {
    return (sportType == SPORT_TYPE_BIKE);
  }

  /**
   * D&eacute;termine si cette activit&eacute; est autre que running et
   * v&eacute;lo.
   * 
   * @return <code>true</code> si cette activit&eacute; est autre que running et
   *         v&eacute;lo.
   */
  public boolean isOther() {
    return (sportType == SPORT_TYPE_OTHER);
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
   * Valorise la date.
   * 
   * @param id
   *          la date.
   */
  protected void setId(Date id) {
    this.id = id;
  }

  /**
   * Restitue la date.
   * 
   * @return la date.
   */
  public Date getId() {
    return id;
  }

  /**
   * Ajooute un tour interm&eacute;diaire.
   * 
   * @param lap
   *          le tour interm&eacute;diaire.
   */
  protected void addLap(Lap lap) {
    if (listLap == null) {
      synchronized (Activity.class) {
        listLap = new ArrayList<Lap>();
      }
    }
    listLap.add(lap);
  }

  /**
   * Restitue la liste des tours interm&eacute;diaires.
   * 
   * @return la liste des tours interm&eacute;diaires.
   */
  public List<Lap> getLaps() {
    if (listLap == null) {
      return Collections.emptyList();
    }
    return listLap;
  }

  /**
   * Restitue le tour interm&eacute;diaire &agrave; l'index
   * sp&eacute;cifi&eacute;.
   * 
   * @param index
   *          l'index du tour interm&eacute;diaire.
   * @throws IndexOutofBoundException
   */
  public Lap getLap(int index) {
    if (listLap == null) {
      throw new IndexOutOfBoundsException("size =0, index=" + index);
    }
    return listLap.get(index);
  }

  /**
   * Restitue le nombre de tour interm&eacute;diaire.
   * 
   * @return le nombre de tour interm&eacute;diaire.
   */
  public int getLapSize() {
    return (listLap == null) ? 0 : listLap.size();
  }

}
