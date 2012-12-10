package fr.turtlesport.ui.swing.component.calendar;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.model.ModelRun;
import fr.turtlesport.ui.swing.model.ModelRunCalendar;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelCalendar extends JPanel implements IListDateRunFire,
                                          LanguageListener {
  private static TurtleLogger            log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelCalendar.class);
  }

  private JPanelMonthSelect              jPanelMonthSelect;

  private JPanelMonth                    jPanelMonthPrev1;

  private JPanelMonth                    jPanelMonthPrev2;

  /** Le model. */
  private ModelRunCalendar               model;

  // Liste des listeners
  private ArrayList<CalendarDayListener> listListener = new ArrayList<CalendarDayListener>();

  /**
   * This is the default constructor.
   */
  public JPanelCalendar() {
    super();
    initialize();
    setModel(new ModelRunCalendar());
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang
   * .LanguageEvent)
   */
  public void languageChanged(final LanguageEvent event) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
    LanguageManager.getManager().removeLanguageListener(jPanelMonthSelect);
    LanguageManager.getManager().removeLanguageListener(jPanelMonthPrev1);
    LanguageManager.getManager().removeLanguageListener(jPanelMonthPrev2);
  }

  /**
   * Ajoute un <code>CalendarDayListener</code>.
   * 
   * @param listener
   *          le <code>CalendarDayListener</code> &aecute; ajouter.
   */
  private void addCalendarDayListener(CalendarDayListener listener) {
    if (listener != null) {
      listListener.add(listener);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireDatesUnselect
   * ()
   */
  public void fireSportChanged(Date date, int sportType) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRun#fireDatesUnselect()
   */
  public void fireDatesUnselect() {
    log.debug(">>fireDatesUnselect");

    getJPanelMonthSelect().unselectAllDates();
    getJPanelMonthPrev1().unselectAllDates();
    getJPanelMonthPrev2().unselectAllDates();

    log.debug("<<fireDatesUnselect");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRun#fireDateChanged
   * (java.util.Date)
   */
  public void fireDateChanged(Date date) {
    log.debug(">>fireDateChanged");

    if (date == null) {
      return;
    }

    try {
      model.updateViewDateChanged(this, date);
    }
    catch (SQLException e) {
      log.error("", e);
    }

    log.debug("<<fireDateChanged");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRun#fireHistoric(int)
   */
  public void fireHistoric(int idUser) throws SQLException {
    model.setIdUser(idUser);
    model.updateView(this);

    // mis a jour des boutons date en cours
    if (MainGui.getWindow().getRightComponent() instanceof JPanelRun) {
      JPanelRun p = (JPanelRun) MainGui.getWindow().getRightComponent();
      p.fireHistoric();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRun#fireDateDeleted
   * (java.util.Date)
   */
  public void fireDateDeleted(Date date) {
    log.debug(">>fireDateChanged");
    model.retreiveDate(this, date);
    log.debug("<<fireDateChanged");
  }

  /**
   * Notifie jour actif selectionn&eacute;.
   * 
   * @param event
   */
  protected void fireCalendarSelectActiveDayPerformed(JLabelDay source) {

    if (source != null) {
      JPanelRun panelRun = null;

      Object obj = MainGui.getWindow().getRightComponent();
      if (!(obj instanceof JPanelRun)) {
        panelRun = new JPanelRun();
        MainGui.getWindow().setRightComponent(panelRun);
      }
      else {
        panelRun = (JPanelRun) obj;
      }

      // Recuperation du run.
      try {
        ModelRun model = panelRun.getModel();
        model.updateView(panelRun, source.getDate());
      }
      catch (SQLException e) {
        log.error("", e);
        ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
            .getManager().getCurrentLang(), JPanelCalendar.class);
        JShowMessage.error(rb.getString("errorSQL"));
      }

    }

    for (CalendarDayListener dl : listListener) {
      dl.selectActiveDay(source);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.IListDateRun#
   * fireCalendarSelectActiveDayPerformed(java.util.Date)
   */
  public void fireCalendarSelectActiveDayPerformed(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    for (CalendarDayListener listener : listListener) {
      listener.selectActiveDay(cal);
    }
  }

  public ModelRunCalendar getModel() {
    return model;
  }

  public void setModel(ModelRunCalendar model) {
    this.model = model;
  }

  public void removeDate(Date date) {
    if (getJPanelMonthSelect().removeDate(date)) {
      return;
    }
    if (getJPanelMonthPrev1().removeDate(date)) {
      return;
    }
    getJPanelMonthPrev2().removeDate(date);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    log.debug(">>initialize");

    GridLayout gridLayout = new GridLayout(3, 1);
    gridLayout.setVgap(0);
    this.setLayout(gridLayout);
    this.setSize(228, 330);
    this.add(getJPanelMonthSelect(), null);
    this.add(getJPanelMonthPrev1(), null);
    this.add(getJPanelMonthPrev2(), null);

    // Listener
    jPanelMonthSelect.addCalendarListener(getJPanelMonthPrev1()
        .getCalendarListener());
    jPanelMonthSelect.addCalendarListener(getJPanelMonthPrev2()
        .getCalendarListener());

    // Attention ajouter en dernier.
    jPanelMonthSelect.addCalendarListener(new CalendarMonthListener() {
      public void nextMonth() {
        try {
          model.updateView(JPanelCalendar.this);
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }

      public void prevMonth() {
        try {
          model.updateView(JPanelCalendar.this);
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }

    });

    for (JLabelDay jLabelDay : getJPanelMonthSelect().getJLabelDaysNumber()) {
      addCalendarDayListener(jLabelDay.getCalendarDayListener());
    }
    for (JLabelDay jLabelDay : getJPanelMonthPrev1().getJLabelDaysNumber()) {
      addCalendarDayListener(jLabelDay.getCalendarDayListener());
    }
    for (JLabelDay jLabelDay : getJPanelMonthPrev2().getJLabelDaysNumber()) {
      addCalendarDayListener(jLabelDay.getCalendarDayListener());
    }

    log.debug("<<initialize");
  }

  /**
   * This method initializes jPanelMonthSelect.
   * 
   * @return javax.swing.JPanel
   */
  public JPanelMonthSelect getJPanelMonthSelect() {
    if (jPanelMonthSelect == null) {
      GregorianCalendar calendar = new GregorianCalendar(getDefaultLocale());
      jPanelMonthSelect = new JPanelMonthSelect(calendar);
    }
    return jPanelMonthSelect;
  }

  /**
   * This method initializes jPanelMonthPrev1.
   * 
   * @return javax.swing.JPanel
   */
  public JPanelMonth getJPanelMonthPrev1() {
    if (jPanelMonthPrev1 == null) {
      GregorianCalendar calendar = new GregorianCalendar(getDefaultLocale());
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.add(Calendar.MONTH, -1);
      jPanelMonthPrev1 = new JPanelMonth(calendar, -1);
    }
    return jPanelMonthPrev1;
  }

  /**
   * This method initializes jPanelMonthPrev1.
   * 
   * @return javax.swing.JPanel
   */
  public JPanelMonth getJPanelMonthPrev2() {
    if (jPanelMonthPrev2 == null) {
      GregorianCalendar calendar = new GregorianCalendar(getDefaultLocale());
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.add(Calendar.MONTH, -2);
      jPanelMonthPrev2 = new JPanelMonth(calendar, -2);
    }
    return jPanelMonthPrev2;
  }

} // @jve:decl-index=0:visual-constraint="10,10"
