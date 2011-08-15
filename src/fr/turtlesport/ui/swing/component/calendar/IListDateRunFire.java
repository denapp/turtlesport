package fr.turtlesport.ui.swing.component.calendar;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author Denis Apparicio
 * 
 */
public interface IListDateRunFire {

  /**
   * Notifie aucune date selectionn&eacute;e.
   * 
   */
  void fireDatesUnselect();

  /**
   * Notifie jour actif selecsionn&eacute;.
   * 
   * @param date
   */
  void fireDateChanged(Date date);

  /**
   * Mis a jour du calendrier.
   */
  void fireHistoric(int idUser) throws SQLException;

  /**
   * Notifie suppression d'une date.
   * 
   * @param date
   */
  void fireDateDeleted(Date date);

  /**
   * Notifie jour actif selecsionn&eacute;.
   * 
   * @param event
   */
  void fireCalendarSelectActiveDayPerformed(Date date);
}