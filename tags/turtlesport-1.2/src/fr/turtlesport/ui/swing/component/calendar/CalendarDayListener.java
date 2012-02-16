package fr.turtlesport.ui.swing.component.calendar;

import java.util.Calendar;
import java.util.EventListener;

/**
 * @author Denis Apparicio
 * 
 */
public interface CalendarDayListener extends EventListener {
  
  /**
   * Jour actif selectionn&eacute;.
   * 
   * @param source
   */
  void selectActiveDay(JLabelDay source);
  
  /**
   * Jour actif selectionn&eacute;.
   * 
   * @param calendar
   */
  void selectActiveDay(Calendar calendar);
  
  }
