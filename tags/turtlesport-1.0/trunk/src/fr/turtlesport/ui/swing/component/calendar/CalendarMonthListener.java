package fr.turtlesport.ui.swing.component.calendar;

import java.util.EventListener;

/**
 * @author Denis Apparicio
 * 
 */
public interface CalendarMonthListener extends EventListener {


  /**
   * Mois en moins.
   * 
   * @param nbMonth
   */
  void prevMonth();

  /**
   * Mois en plus.
   * 
   * @param e
   * @param nbMonth
   */
  void nextMonth();
}
