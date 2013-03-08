package fr.turtlesport.ui.swing.component.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.SwingLookAndFeel;
import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class JPanelMonthSelect extends AbstractJPanelMonth {
  private static TurtleLogger              log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelMonthSelect.class);
  }

  private static final Dimension           DIM_BUTTON   = new Dimension(16, 16);

  // ui
  private JPanel                           jPanelMonthPanel;

  private JLabel                           jLabelMonthYear;

  private JButton                          jButtonPrev;

  private JButton                          jButtonNext;

  // Liste des listeners
  private ArrayList<CalendarMonthListener> listListener = new ArrayList<CalendarMonthListener>();

  /**
   * 
   */
  protected JPanelMonthSelect(Calendar calendar) {
    super(calendar);
    log.debug(">>JPanelMonthSelect");

    initialize();

    log.debug("<<JPanelMonthSelect");
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JPanel#updateUI()
   */
  @Override
  public void updateUI() {
    if (jButtonPrev == null) {
      return;
    }
    // Probleme avec les boutons prev et next en CDE/Motif.
    if (SwingLookAndFeel.isLookAndFeelMotif()) {
      jButtonPrev.setPreferredSize(null);
      jButtonNext.setPreferredSize(null);
    }
    else {
      jButtonPrev.setPreferredSize(DIM_BUTTON);
      jButtonNext.setPreferredSize(DIM_BUTTON);
    }
    super.updateUI();
  }

  /**
   * Ajoute un <code>CalendarListener</code>.
   * 
   * @param listener
   *          le <code>CalendarListener</code> &aecute; ajouter.
   */
  public void addCalendarListener(CalendarMonthListener listener) {
    if (listener != null) {
      listListener.add(listener);
    }
  }

  /**
   * Supprime un <code>CalendarListener</code>.
   * 
   * @param listener
   *          le <code>CalendarListener</code> &aecute; ajouter.
   */
  public void removeCalendarListener(CalendarMonthListener listener) {
    if (listener != null) {
      listListener.remove(listener);
    }
  }

  /**
   * Notifie les listeners mois en moins.
   * 
   */
  protected void fireCalendarPrevMonthPerformed() {
    for (CalendarMonthListener listener : listListener) {
      listener.prevMonth();
    }
  }

  /**
   * Notifie les listeners changement de date.
   * 
   * @param calendar
   */
  public void fireCalendarChangeMonthPerformed(Date date, Date[] dates) {
    if (log.isDebugEnabled()) {
      log.debug(">>fireCalendarChangeMonthPerformed date=" + date);
    }

    getCalendar().setTime(date);
    getCalendar().set(Calendar.DAY_OF_MONTH, 1);
    log.debug(getCalendar().getTime());
    updateMonthYear();
    updateDaysNumber();
    fireCurrentDates(dates);

    log.debug("<<fireCalendarChangeMonthPerformed");
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

      jButtonPrev = new JButton(ImagesRepository.getImageIcon("prev.gif"));
      jButtonNext = new JButton(ImagesRepository.getImageIcon("next.gif"));
      jButtonPrev.setPreferredSize(DIM_BUTTON);
      jButtonNext.setPreferredSize(DIM_BUTTON);

      jLabelMonthYear = new JLabel();
      jLabelMonthYear.setFont(GuiFont.FONT_PLAIN);
      jLabelMonthYear.setHorizontalAlignment(SwingConstants.CENTER);
      jLabelMonthYear.setPreferredSize(new Dimension(120, 20));

      jPanelMonthPanel.setLayout(new BoxLayout(jPanelMonthPanel,
                                               BoxLayout.X_AXIS));
      jPanelMonthPanel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
      jPanelMonthPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

      Dimension hgap10 = new Dimension(10, 1);
      jPanelMonthPanel.add(jButtonPrev, null);
      jPanelMonthPanel.add(Box.createRigidArea(hgap10));

      jPanelMonthPanel.add(jLabelMonthYear, null);
      jPanelMonthPanel.add(Box.createRigidArea(hgap10));
      jPanelMonthPanel.add(Box.createHorizontalGlue());
      jPanelMonthPanel.add(jButtonNext, null);
    }
    return jPanelMonthPanel;
  }

  /**
   * Notifie les listeners mois en plus.
   * 
   */
  protected void fireCalendarNextMonthPerformed() {
    for (CalendarMonthListener listener : listListener) {
      listener.nextMonth();
    }
  }

  /**
   * 
   */
  private void initialize() {
    // Evenement
    jButtonPrev.addActionListener(new ChangeMonthActionListener(false));
    jButtonNext.addActionListener(new ChangeMonthActionListener(true));

    // Setup the keyboard handler.
    InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false),
                 "selectPreviousDay");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false),
                 "selectNextDay");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
                 "selectDayInPreviousWeek");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
                 "selectDayInNextWeek");

    ActionMap actionMap = getActionMap();
    actionMap.put("selectPreviousDay",
                  new KeyboardAction(KeyboardAction.SELECT_PREVIOUS_DAY));
    actionMap.put("selectNextDay",
                  new KeyboardAction(KeyboardAction.SELECT_NEXT_DAY));
    actionMap.put("selectDayInPreviousWeek",
                  new KeyboardAction(KeyboardAction.SELECT_DAY_PREVIOUS_WEEK));
    actionMap.put("selectDayInNextWeek",
                  new KeyboardAction(KeyboardAction.SELECT_DAY_NEXT_WEEK));
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class ChangeMonthActionListener implements ActionListener {
    private boolean isNext;

    public ChangeMonthActionListener(boolean isNext) {
      this.isNext = isNext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      getCalendar().add(Calendar.MONTH, (isNext) ? 1 : -1);
      updateMonthYear();
      updateDaysNumber();
      if (isNext) {
        fireCalendarNextMonthPerformed();
      }
      else {
        fireCalendarPrevMonthPerformed();
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class KeyboardAction extends AbstractAction {
    public static final int SELECT_PREVIOUS_DAY      = 1;

    public static final int SELECT_NEXT_DAY          = 2;

    public static final int SELECT_DAY_PREVIOUS_WEEK = 3;

    public static final int SELECT_DAY_NEXT_WEEK     = 4;

    private int             action;

    private int             currentMonth;

    /**
     * @param action
     */
    public KeyboardAction(int action) {
      this.action = action;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

      switch (action) {
        case SELECT_PREVIOUS_DAY:
          currentMonth = getCalendar().get(Calendar.MONTH);
          getCalendar().add(Calendar.DAY_OF_MONTH, -1);
          updateDate();
          fireMonth();
          break;

        case SELECT_NEXT_DAY:
          currentMonth = getCalendar().get(Calendar.MONTH);
          getCalendar().add(Calendar.DAY_OF_MONTH, 1);
          updateDate();
          fireMonth();
          break;

        case SELECT_DAY_PREVIOUS_WEEK:
          currentMonth = getCalendar().get(Calendar.MONTH);
          getCalendar().add(Calendar.DAY_OF_MONTH, -7);
          updateDate();
          fireMonth();
          break;

        case SELECT_DAY_NEXT_WEEK:
          currentMonth = getCalendar().get(Calendar.MONTH);
          getCalendar().add(Calendar.DAY_OF_MONTH, 7);
          updateDate();
          fireMonth();
          break;

        default:
          break;
      }
    }

    private void fireMonth() {
      int newMonth = getCalendar().get(Calendar.MONTH);
      if (newMonth > currentMonth) {
        fireCalendarNextMonthPerformed();
      }
      else if (newMonth < currentMonth) {
        fireCalendarPrevMonthPerformed();
      }
    }
  }

}
