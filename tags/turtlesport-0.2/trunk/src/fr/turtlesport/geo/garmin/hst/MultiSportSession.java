package fr.turtlesport.geo.garmin.hst;

import java.util.ArrayList;

/**
 * @author Denis Apparicio
 * 
 */
public class MultiSportSession {
  private ArrayList<Sport> listSport;

  /**
   * 
   */
  public MultiSportSession() {
    super();
  }

  /**
   * Ajoute un sport.
   * 
   * @param sport
   *          le sport.
   */
  public void addSport(Sport sport) {
    if (listSport == null) {
      synchronized (MultiSportSession.class) {
        listSport = new ArrayList<Sport>();
      }
    }
    listSport.add(sport);
  }  

}
