package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.calendar.JPanelCalendar;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelRunCalendar {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelRunCalendar.class);
  }

  private ArrayList<Date>     listDate = new ArrayList<Date>();

  private int                 idUser;

  /**
   * 
   */
  public ModelRunCalendar() {
    super();
  }

  public int getIdUser() {
    return idUser;
  }

  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  /**
   * Restitue le dates.
   */
  public Date[] getDates() {
    Date[] dates = new Date[listDate.size()];
    if (dates.length > 0) {
      listDate.toArray(dates);
    }
    return dates;
  }

  /**
   * Mise a jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JPanelCalendar view) throws SQLException {
    log.debug(">>updateView");

    Date d1;
    Date d2;
    Date[] dates;

    listDate.clear();

    // mois 1
    d1 = view.getJPanelMonthSelect().monthFirstDay();
    d2 = view.getJPanelMonthSelect().monthLastDay();
    dates = RunTableManager.getInstance()
        .retrieveDates(MainGui.getWindow().getCurrentIdUser(), d1, d2);
    view.getJPanelMonthSelect().fireCurrentDates(dates);

    for (Date d : dates) {
      listDate.add(d);
    }

    // mois 2
    d1 = view.getJPanelMonthPrev1().monthFirstDay();
    d2 = view.getJPanelMonthPrev1().monthLastDay();
    dates = RunTableManager.getInstance()
        .retrieveDates(MainGui.getWindow().getCurrentIdUser(), d1, d2);
    view.getJPanelMonthPrev1().fireCurrentDates(dates);
    for (Date d : dates) {
      listDate.add(d);
    }

    // mois 3
    d1 = view.getJPanelMonthPrev2().monthFirstDay();
    d2 = view.getJPanelMonthPrev2().monthLastDay();
    dates = RunTableManager.getInstance()
        .retrieveDates(MainGui.getWindow().getCurrentIdUser(), d1, d2);
    view.getJPanelMonthPrev2().fireCurrentDates(dates);
    for (Date d : dates) {
      listDate.add(d);
    }

    log.debug("<<updateView");
  }

  /**
   * Mise a jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateViewDateChanged(JPanelCalendar view, Date date) throws SQLException {
    log.debug(">>updateViewDateChanged");

    Date d1;
    Date d2;
    Date[] dates;
    Calendar cal;

    cal = Calendar.getInstance();
    cal.setTime(date);

    if (!view.getJPanelMonthSelect().isInMonth(cal)
        && !view.getJPanelMonthPrev1().isInMonth(cal)
        && !view.getJPanelMonthPrev2().isInMonth(cal)) {
      listDate.clear();

      // mois 1
      view.getJPanelMonthSelect().setCalendar(cal);
      d1 = view.getJPanelMonthSelect().monthFirstDay();
      d2 = view.getJPanelMonthSelect().monthLastDay();
      dates = RunTableManager.getInstance()
          .retrieveDates(MainGui.getWindow().getCurrentIdUser(), d1, d2);
      view.getJPanelMonthSelect().fireCurrentDates(dates);
      for (Date d : dates) {
        listDate.add(d);
      }

      // mois 2
      cal = (Calendar) cal.clone();
      cal.add(Calendar.MONTH, -1);
      view.getJPanelMonthPrev1().setCalendar(cal);
      d1 = view.getJPanelMonthPrev1().monthFirstDay();
      d2 = view.getJPanelMonthPrev1().monthLastDay();
      dates = RunTableManager.getInstance()
          .retrieveDates(MainGui.getWindow().getCurrentIdUser(), d1, d2);
      view.getJPanelMonthPrev1().fireCurrentDates(dates);
      for (Date d : dates) {
        listDate.add(d);
      }

      // mois 3
      cal = (Calendar) cal.clone();
      cal.add(Calendar.MONTH, -1);
      view.getJPanelMonthPrev2().setCalendar(cal);
      d1 = view.getJPanelMonthPrev2().monthFirstDay();
      d2 = view.getJPanelMonthPrev2().monthLastDay();
      dates = RunTableManager.getInstance()
          .retrieveDates(MainGui.getWindow().getCurrentIdUser(), d1, d2);
      view.getJPanelMonthPrev2().fireCurrentDates(dates);
      for (Date d : dates) {
        listDate.add(d);
      }

      dates = new Date[listDate.size()];
      if (dates.length > 0) {
        listDate.toArray(dates);
      }
      view.getJPanelMonthSelect().fireCalendarChangeMonthPerformed(date, dates);
    }

    view.fireCalendarSelectActiveDayPerformed(date);

    log.debug("<<updateViewDateChanged");
  }

  /**
   * Suppression d'une date.
   * 
   * @param panel
   * @param date
   */
  public void retreiveDate(JPanelCalendar view, Date date) {
    if (log.isDebugEnabled()) {
      log.debug(">>retreiveDate date=" + date);
    }

    if (date == null) {
      return;
    }

    if (listDate.remove(date)) {
      view.removeDate(date);
    }

    log.debug("<<retreiveDate");
  }

}
