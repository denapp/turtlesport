package fr.turtlesport.ui.swing.model;


/**
 * @author Denis Apparicio
 * 
 */
public interface AddDeleteMapListener {

  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a AddDeleteMapEvent object
   */
  void deleteMap(AddDeleteMapEvent e);

  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a AddDeleteMapEvent object
   */
  void addMap(AddDeleteMapEvent changeEvent);
}
