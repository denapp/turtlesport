package fr.turtlesport.unit.event;

/**
 * @author Denis Apparicio
 * 
 */
public interface UnitListener {

  /**
   * Invoqu&eacute; lorsqu'une unit&eacute; change.
   */
  void unitChanged(UnitEvent event);
  
  /**
   * Invoqu&eacute; lorsque le listener est supprim&eacute;.
   */
  void completedRemoveUnitListener();

}
