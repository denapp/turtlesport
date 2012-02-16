package fr.turtlesport.ui.swing.component.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractJPanelMonth extends JPanel implements
                                                        LanguageListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractJPanelMonth.class);
  }

  // ui
  private JPanel              jPanelDayPanel;

  private JLabel[]            jLabelsDaysOfWeek;

  private JLabelDay[]         jLabelDaysNumber;

  //
  private Calendar            calendar;

  private SimpleDateFormat    dateFormat = new SimpleDateFormat("MMMM yyyy",
                                                                getDefaultLocale());

  private String[]            weekDays   = new DateFormatSymbols(getDefaultLocale())
                                             .getShortWeekdays();

  private int                 firstDayOfWeek;

  private int                 localFirstDay;

  private boolean             isInit     = false;

  /**
   * 
   */
  protected AbstractJPanelMonth(Calendar calendar) {
    super();
    setCalendar(calendar);
  }

  public Date monthFirstDay() {
    return calendar.getTime();
  }

  public Date monthLastDay() {
    Calendar cal = (Calendar) calendar.clone();
    cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.MILLISECOND, -1);
    return cal.getTime();
  }

  /**
   * D&eacute;selection de toutes les dates.
   * 
   */
  public void unselectAllDates() {
    log.debug(">>unselectAllDates");
    for (JLabelDay d : jLabelDaysNumber) {
      if (d.isSelect()) {
        d.fireUnselect();
      }
    }
    log.debug("<<unselectAllDates");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang.LanguageEvent)
   */
  public void languageChanged(final LanguageEvent event) {
    if (SwingUtilities.isEventDispatchThread()) {
      performedLanguage(event.getLang());
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          performedLanguage(event.getLang());
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  private void performedLanguage(ILanguage lang) {
    dateFormat = new SimpleDateFormat("MMMM yyyy", lang.getLocale());
    weekDays = new DateFormatSymbols(lang.getLocale()).getShortWeekdays();
    updateMonthYear();
    updateDaysOfWeek();
  }

  /**
   * D&eacute;termine si cette date est dans le mois.
   * 
   * @param cal
   * @return
   */
  public boolean isInMonth(Calendar calendar) {
    if (calendar == null) {
      return false;
    }
    return (calendar.get(Calendar.YEAR) == this.calendar.get(Calendar.YEAR))
           && (calendar.get(Calendar.MONTH) == this.calendar
               .get(Calendar.MONTH));
  }

  /**
   * Restitue la date du mois en cours.
   * 
   * @return
   */
  public Calendar getCalendar() {
    return calendar;
  }

  /**
   * @param calendar
   * @return
   */
  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
    this.calendar.set(Calendar.DAY_OF_MONTH, 1);
    this.calendar.set(Calendar.HOUR_OF_DAY, 0);
    this.calendar.set(Calendar.MINUTE, 0);
    this.calendar.set(Calendar.SECOND, 0);
    this.calendar.set(Calendar.MILLISECOND, 0);

    firstDayOfWeek = calendar.getFirstDayOfWeek();
    if (!isInit) {
      initialize();
    }
    updateDate();
  }

  /**
   * @return
   */
  public JLabelDay[] getJLabelDaysNumber() {
    return jLabelDaysNumber;
  }

  /**
   * @return
   */
  public int getLocalFirstDay() {
    return localFirstDay;
  }

  /**
   * 
   */
  public abstract JPanel initializeJPanelMonthPanel();

  /**
   * 
   */
  public abstract JLabel getJLabelMonthYear();

  public int selectIndex() {
    int indexSelect = -1;
    for (int i = 0; i < jLabelDaysNumber.length; i++) {
      if (jLabelDaysNumber[i].isSelect()) {
        log.error("indexSelect=" + indexSelect);
        indexSelect = i;
      }
    }
    return indexSelect;
  }

  /**
   * @param date
   */
  public void fireCurrentDates(Date[] dates) {
    log.debug(">>fireCurrentDates");

    // Recuperation de l'index selectionne et reset
    int indexSelect = -1;
    for (int i = 0; i < jLabelDaysNumber.length; i++) {
      if (jLabelDaysNumber[i].isSelect()) {
        indexSelect = i;
      }
      jLabelDaysNumber[i].reset();
    }

    // update
    Calendar cal = Calendar.getInstance();
    int day;
    int index;
    for (Date d : dates) {
      cal.setTime(d);
      if (isInMonth(cal)) {
        day = cal.get(Calendar.DAY_OF_MONTH);
        index = localFirstDay + day - 1;
        if (index != indexSelect) {
          jLabelDaysNumber[index].fireActive();
        }
        else {
          jLabelDaysNumber[index].fireSelect();
        }
      }
    }

    // mis a jour date du jour
    setUIDateOfDay();

    log.debug("<<fireCurrentDates");
  }

  /**
   * Suppression d'une date.
   * 
   * @param date
   *          la date &agrave; supprimer.
   */
  protected boolean removeDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    if (cal.get(Calendar.YEAR) == this.calendar.get(Calendar.YEAR)
        && cal.get(Calendar.MONTH) == this.calendar.get(Calendar.MONTH)) {
      int day = cal.get(Calendar.DAY_OF_MONTH);
      jLabelDaysNumber[localFirstDay + day - 1].removeEvents();
      return true;
    }
    return false;
  }

  /**
   * Reactualise la locale et reaffiche le contenu des composants.
   */
  public void updateDate() {
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    updateMonthYear();
    updateDaysOfWeek();
    updateDaysNumber();
  }

  /**
   * Affiche le mois et l'annee en cours.
   */
  public void updateMonthYear() {
    getJLabelMonthYear().setText(dateFormat.format(calendar.getTime()));
  }

  /**
   * Affiche les jours de la semaine.
   */
  public void updateDaysOfWeek() {
    for (int i = 1; i < weekDays.length; i++) {
      final int index = (i - 2 + firstDayOfWeek) % 7 + 1;
      jLabelsDaysOfWeek[i - 1].setText(weekDays[index]);
    }
  }

  /**
   * Affiche le numero des jours.
   */
  public void updateDaysNumber() {
    int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
    localFirstDay = (firstDayOfMonth - firstDayOfWeek + 7) % 7;

    boolean isFull = false;
    boolean isEmpty;
    int dayOfMonth;

    Calendar cal = Calendar.getInstance();
    cal.setTime(calendar.getTime());

    for (int i = 0; i < jLabelDaysNumber.length; i++) {
      // Determine si le composant est affiche ou non
      isEmpty = (i < localFirstDay) || isFull;
      jLabelDaysNumber[i].setVisible(!isEmpty);
      jLabelDaysNumber[i].setBorder(null);

      // Affichage du jour
      if (!isEmpty) {
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        jLabelDaysNumber[i].setDayOfMonth(dayOfMonth);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        isFull = (1 == cal.get(Calendar.DAY_OF_MONTH));
      }
    }

    // date du jour
    setUIDateOfDay();
  }

  private void setUIDateOfDay() {
    // mis a jour date du jour
    Calendar calDay = Calendar.getInstance();
    calDay.setTimeInMillis(System.currentTimeMillis());
    if (isInMonth(calDay)) {
      int index = localFirstDay + calDay.get(Calendar.DAY_OF_MONTH) - 1;
      jLabelDaysNumber[index].setBorder(BorderFactory
          .createLineBorder(Color.black));
    }
  }

  /**
   * 
   */
  private void initialize() {
    if (!isInit) {
      this.setLayout(new BorderLayout());
      this.add(initializeJPanelMonthPanel(), BorderLayout.NORTH);
      this.add(getJPanelDayPanel(), BorderLayout.CENTER);

      LanguageManager.getManager().addLanguageListener(this);
      performedLanguage(LanguageManager.getManager().getCurrentLang());
      isInit = true;
    }
  }

  /**
   * 
   */
  private JPanel getJPanelDayPanel() {
    if (jPanelDayPanel == null) {
      jPanelDayPanel = new JPanel();
      GridLayout gridLayout = new GridLayout(7, 7, 0, 0);
      jPanelDayPanel.setLayout(gridLayout);

      jLabelsDaysOfWeek = new JLabel[7];
      for (int i = 0; i < 7; i++) {
        jLabelsDaysOfWeek[i] = new JLabel("");
        jLabelsDaysOfWeek[i].setFont(GuiFont.FONT_PLAIN);
        jLabelsDaysOfWeek[i].setHorizontalAlignment(SwingConstants.CENTER);
        jPanelDayPanel.add(jLabelsDaysOfWeek[i]);
      }

      jLabelDaysNumber = new JLabelDay[42];
      for (int i = 0; i < jLabelDaysNumber.length; i++) {
        jLabelDaysNumber[i] = new JLabelDay(i, this);
        jLabelDaysNumber[i].setFont(GuiFont.FONT_PLAIN);
        jLabelDaysNumber[i].setHorizontalAlignment(SwingConstants.CENTER);
        jPanelDayPanel.add(jLabelDaysNumber[i]);
      }
    }
    return jPanelDayPanel;
  }

}
