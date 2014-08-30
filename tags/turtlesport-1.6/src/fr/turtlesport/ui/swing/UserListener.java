package fr.turtlesport.ui.swing;

import java.sql.SQLException;

/**
 * @author Denis Apparicio
 * 
 */
public interface UserListener {

  /**
   * Invoqu&eacute; lorsque l'utilisateur change.
   * 
   * @param idUser
   *          le nouvel utilisateur.
   */
  void userSelect(int idUser) throws SQLException;
}
