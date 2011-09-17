package fr.turtlesport.geo.garmin.hst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Denis Apparicio
 * 
 */
public class HistoryFolder {

  private String         name;

  private ArrayList<Run> listRun;

  /**
   * 
   */
  public HistoryFolder() {
    super();
  }

  /**
   * @param name
   */
  public HistoryFolder(String name) {
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
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Ajoute une course.
   * 
   * @param run
   *          la course.
   */
  public void addRun(Run run) {
    if (listRun == null) {
      synchronized (HistoryFolder.class) {
        listRun = new ArrayList<Run>();
      }
    }
    listRun.add(run);
  }

  /**
   * Restitue la liste des run.
   * 
   * @return la liste des run.
   */
  public List<Run> getRuns() {
    if (listRun == null) {
      return Collections.emptyList();
    }
    return listRun;
  }
  
  /**
   * Restitue le nombre de course.
   * 
   * @return le nombre de course.
   */
  public int getRunSize() {
    return (listRun == null) ? 0 : listRun.size();
  }

}
