package fr.turtlesport.ui.swing.model;


/**
 * @author Denis Apparicio
 * 
 */
public interface ChangeMapListener extends ChangePointsListener {

  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a ChangeEvent object
   */
  void changedSpeed(ChangeMapEvent e);

  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a ChangeEvent object
   */
  void changedMap(ChangeMapEvent changeEvent);

  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a ChangeEvent object
   */
  void changedPlay(ChangeMapEvent e);
}
