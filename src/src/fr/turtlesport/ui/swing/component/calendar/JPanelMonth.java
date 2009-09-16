package fr.turtlesport.ui.swing.component.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class JPanelMonth extends AbstractJPanelMonth {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelMonth.class);
  }

  // ui
  private JPanel                jPanelMonthPanel;

  private JLabel                jLabelMonthYear;

  private CalendarMonthListener listener;

  /**
   * Constuit le mois d'un calendrier.
   * 
   * @param calendar
   */
  protected JPanelMonth(Calendar calendar, int nbMonthLess) {
    super(calendar);
    // ajout des evenements.
    listener = new CalendarListenerMonth(nbMonthLess);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.AbstractJPanelMonth#initializeJPanelMonthPanel()
   */
  @Override
  public JPanel initializeJPanelMonthPanel() {
    if (jPanelMonthPanel == null) {
      Color color = new Color(158, 190, 245);
      jPanelMonthPanel = new JPanel();
      jPanelMonthPanel.setBackground(color);

      jLabelMonthYear = new JLabel();
      jLabelMonthYear.setFont(GuiFont.FONT_PLAIN);
      jLabelMonthYear.setHorizontalAlignment(SwingConstants.CENTER);
      jLabelMonthYear.setPreferredSize(new Dimension(120, 20));

      jPanelMonthPanel.add(jLabelMonthYear);
    }
    return jPanelMonthPanel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.AbstractJPanelMonth#getJLabelMonthYear()
   */
  @Override
  public JLabel getJLabelMonthYear() {
    return jLabelMonthYear;
  }

  /**
   * @return the listener
   */
  public CalendarMonthListener getCalendarListener() {
    return listener;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class CalendarListenerMonth implements CalendarMonthListener {

    private int nbMonthLess;

    public CalendarListenerMonth(int nbMonthLess) {
      this.nbMonthLess = nbMonthLess;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.ui.swing.component.calendar.CalendarMonthListener#nextMonth()
     */
    public void nextMonth() {
      getCalendar().add(Calendar.MONTH, 1);
      updateDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.ui.swing.component.calendar.CalendarMonthListener#prevMonth()
     */
    public void prevMonth() {
      getCalendar().add(Calendar.MONTH, -1);
      updateDate();
    }

    public void changeMonth(Date date, Date[] dates) {
      if (log.isInfoEnabled()) {
        log.info(">>changeMonth date=" + date);
      }

      getCalendar().setTime(date);
      getCalendar().set(Calendar.DAY_OF_MONTH, 1);
      getCalendar().add(Calendar.MONTH, nbMonthLess);
      updateDate();
      fireCurrentDates(dates);

      if (log.isInfoEnabled()) {
        log.info("cal=" + getCalendar().getTime());
        log.info("<<changeMonth");
      }
    }
  }

}
