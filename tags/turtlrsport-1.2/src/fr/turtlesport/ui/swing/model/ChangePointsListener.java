package fr.turtlesport.ui.swing.model;

import java.util.EventListener;

/**
 * @author Denis Apparicio
 * 
 */
public interface ChangePointsListener extends EventListener {

  /**
   * Invocation lorsque le point courant a chang&eacute;
   * 
   * @param e
   *          ChangeEvent object
   */
  void changedPoint(ChangePointsEvent e);

  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a ChangeEvent object
   */
  void changedLap(ChangePointsEvent e);

  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a ChangeEvent object
   */
  void changedAllPoints(ChangePointsEvent changeEvent);

}
