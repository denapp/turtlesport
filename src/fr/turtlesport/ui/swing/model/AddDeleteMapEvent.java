package fr.turtlesport.ui.swing.model;

import javax.swing.event.ChangeEvent;

import fr.turtlesport.map.DataMap;

/**
 * @author Denis Apparicio
 * 
 */
public class AddDeleteMapEvent extends ChangeEvent {

  private DataMap dataMap;
  
  /**
   * @param source
   */
  public AddDeleteMapEvent(Object source, DataMap dataMap) {
    super(source);
    this.dataMap = dataMap;
  }

  /**
   * Restitue la map ajout&eacute;e ou supprim&eacute;e.
   * 
   * @return la map ajout&eacute;e ou supprim&eacute;e.
   */
  public DataMap getDataMap() {
    return dataMap;
  }

  /**
   * Restitue le nom de la map &agrave; ajout&eacute;e ou supprim&eacute;e.
   * 
   * @return le nom de map ajout&eacute;e ou supprim&eacute;e.
   */
  public String getMapName() {
    return dataMap.getName();
  }
}
