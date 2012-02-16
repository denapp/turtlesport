package fr.turtlesport.geo.garmin.hst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.turtlesport.geo.garmin.Lap;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class Run {
  private ArrayList<Lap> listLap;

  /**
   * 
   */
  public Run() {
    super();
  }

  /**
   * Ajoute un tour interm&eacute;diaire.
   * 
   * @param lap
   *          le tour interm&eacute;diaire.
   */
  public void addLap(Lap lap) {
    if (listLap == null) {
      synchronized (Run.class) {
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
      throw new IndexOutOfBoundsException("size =0, index="+index);      
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
