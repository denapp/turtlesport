package fr.turtlesport.ui.swing.component.calendar;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.MainGui;

/**
 * @author Denis Apparicio
 * 
 */
public class JLabelDay extends JLabel implements CalendarDayListener {

  private static final Color  RESET_COLOR = new JLabel().getForeground();

  private int                 index;

  private AbstractJPanelMonth owner;

  /** Date selectionn&eacute;. */
  private boolean             isSelect;

  /** Date active mais non selectionn&eacute. */
  private boolean             isActive;

  /** Nombre d'�venement pour cetet date. */
  private int                 nbEvents;

  /** texte du jour */
  private int                 dayOfMonth;

  /*
   * 
   */
  protected JLabelDay(int index, AbstractJPanelMonth owner) {
    super();

    this.index = index;
    this.owner = owner;
    isSelect = false;
    isActive = false;
    nbEvents = 0;

    addMouseListener(new DayMouseListener());
  }

  /**
   * Valorise le numer&eacute;ro du jour.
   * 
   * @param dayNumber
   *          numer&eacute;ro du jour.
   */
  protected void setDayOfMonth(int dayOfMonth) {
    this.dayOfMonth = dayOfMonth;
    setText("<html><body>" + String.valueOf(dayOfMonth)
            + "<sup>&nbsp;</sup></body></html>");
  }

  /**
   * @return
   */
  public int getNbEvents() {
    return nbEvents;
  }

  /**
   * @return
   */
  public CalendarDayListener getCalendarDayListener() {
    return this;
  }

  /**
   * Restitue la date du label.
   * 
   * @return la date du label.
   */
  public Date getDate() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(owner.getCalendar().getTime());
    cal.add(Calendar.DAY_OF_MONTH, index - owner.getLocalFirstDay());
    return cal.getTime();
  }

  /**
   * D&eacute;termine si ce <code>JLabelDay</code> est une date active.
   * 
   * @return <code>true</code> si date active.
   */
  public boolean isActive() {
    return isActive;
  }

  /**
   * D&eacute;termine si ce <code>JLabelDay</code> est une date
   * selectionn&eacute;e.
   * 
   * @return <code>true</code> si date selectionn&eacute;e.
   */
  public boolean isSelect() {
    return isSelect;
  }

  /**
   * 
   */
  public void reset() {
    isActive = false;
    isSelect = false;
    nbEvents = 0;
    uiEvents();
    setForeground(RESET_COLOR);
    setFont(GuiFont.FONT_PLAIN);
  }

  /**
   * 
   */
  public void removeEvents() {
    nbEvents--;
    if (nbEvents <= 0) {
      reset();
    }
    else {
      uiEvents();
    }
  }

  /**
   * 
   */
  public void fireActive() {
    isActive = true;
    isSelect = false;
    nbEvents++;
    uiEvents();
    setForeground(Color.blue);
    setFont(GuiFont.FONT_BOLD);
  }

  /**
   * 
   */
  public void fireUnselect() {
    isActive = true;
    isSelect = false;
    uiEvents();
    setForeground(Color.blue);
    setFont(GuiFont.FONT_BOLD);
  }

  /**
   * 
   */
  public void fireSelect() {
    isActive = true;
    isSelect = true;
    nbEvents++;
    uiEvents();
    setForeground(Color.red);
    setFont(GuiFont.FONT_BOLD);
  }

  private void uiEvents() {
    if (nbEvents > 1) {
      setText("<html><body>" + dayOfMonth + "<sup>" + nbEvents
              + "</sup><body></html>");
    }
    else {
      setText("<html><body>" + dayOfMonth + "<sup>&nbsp;</sup><body></html>");
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DayMouseListener extends MouseAdapter {

    public DayMouseListener() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
      if (JLabelDay.this.isActive && e.getButton() == MouseEvent.BUTTON1) {
        MainGui.getWindow().beforeRunnableSwing();

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JPanelCalendar owner = (JPanelCalendar) JLabelDay.this.owner
                .getParent();
            owner.fireCalendarSelectActiveDayPerformed(JLabelDay.this);
            MainGui.getWindow().afterRunnableSwing();
          }
        });

      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.CalendarDayListener#selectActiveDay(fr.turtlesport.ui.swing.component.calendar.JLabelDay)
   */
  public void selectActiveDay(JLabelDay source) {
    if (JLabelDay.this.isActive) {
      if (source != JLabelDay.this) {
        setForeground(Color.blue);
        isSelect = false;
      }
      else {
        isSelect = true;
        setForeground(Color.red);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.CalendarDayListener#selectActiveDay(java.util.Calendar)
   */
  public void selectActiveDay(Calendar calendar) {
    if (JLabelDay.this.isActive) {
      Calendar currentCal = Calendar.getInstance();
      currentCal.setTime(owner.getCalendar().getTime());
      currentCal.add(Calendar.DAY_OF_MONTH, index - owner.getLocalFirstDay());

      if (currentCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
          && currentCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
          && currentCal.get(Calendar.DAY_OF_MONTH) == calendar
              .get(Calendar.DAY_OF_MONTH)) {
        setForeground(Color.red);
        isSelect = true;
      }
      else {
        setForeground(Color.blue);
        isSelect = false;
      }
    }
  }

}
