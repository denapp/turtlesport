package fr.turtlesport.geo.garmin.hst;

import java.util.ArrayList;

/**
 * @author Denis Apparicio
 * 
 */
public class MultiSport {

  private String                       name;

  private ArrayList<MultiSportSession> listMultiSportSession;

  /**
   * @param name
   */
  public MultiSport(String name) {
    super();
    this.name = name;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Ajoute un <code>MultiSportSession</code>.
   * 
   * @param sport
   *          le <code>MultiSportSession</code>.
   */
  public void addMultiSportSession(MultiSportSession m) {
    if (listMultiSportSession == null) {
      synchronized (MultiSportSession.class) {
        listMultiSportSession = new ArrayList<MultiSportSession>();
      }
    }
    listMultiSportSession.add(m);
  }

}
