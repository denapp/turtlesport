package fr.turtlesport.ui.swing.component.calendar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataSearchRun;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.CursorSwingWorker;
import fr.turtlesport.ui.swing.img.menu.ImagesMenuRepository;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelListDateRun extends JPanel implements IListDateRunFire {
  private JToggleButton    jButtonCalendar;

  private JToggleButton    jButtonList;

  private JToggleButton    jButtonTree;

  private IListDateRunFire panelDateRun;

  private static final int VIEW_CALENDAR = 1;

  private static final int VIEW_TREE     = 2;

  private static final int VIEW_LIST     = 3;

  // private boolean isNeedDrngDrop;

  public JPanelListDateRun() {
    super();
    initialize();
  }

  // public void needDrngDrop(boolean isNeedDrngDrop) {
  // this.isNeedDrngDrop = isNeedDrngDrop;
  // panelDateRun.needDrngDrop(isNeedDrngDrop);
  // }

  private void initialize() {
    setLayout(new BorderLayout());
    setSize(new Dimension(228, 330));
    setPreferredSize(new Dimension(228, 330));

    JPanel panelNorth = new JPanel();
    panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    ButtonGroup group = new ButtonGroup();
    group.add(getJButtonCalendar());
    group.add(getJButtonList());
    group.add(getJButtonTree());
    panelNorth.add(getJButtonCalendar());
    panelNorth.add(getJButtonList());
    panelNorth.add(getJButtonTree());

    int prop = Configuration.getConfig().getPropertyAsInt("general",
                                                          "calendarView",
                                                          VIEW_CALENDAR);
    switch (prop) {
      case VIEW_LIST:
        panelDateRun = new JPanelTableRun();
        jButtonList.setSelected(true);
        break;
      case VIEW_TREE:
        panelDateRun = new JPanelTreeRun();
        jButtonTree.setSelected(true);
        break;
      default:
        panelDateRun = new JPanelCalendar();
        jButtonCalendar.setSelected(true);
    }

    add(panelNorth, BorderLayout.NORTH);
    add((Component) panelDateRun, BorderLayout.CENTER);

    getJButtonCalendar().addActionListener(new CalendarRunAction());
    getJButtonList().addActionListener(new ListRunAction());
    getJButtonTree().addActionListener(new ListTreeRunAction());
  }

  private void removeLanguageListenerPanelDataRun() {
    if (panelDateRun != null) {
      LanguageManager.getManager()
          .removeLanguageListener((LanguageListener) panelDateRun);
      ((LanguageListener) panelDateRun).completedRemoveLanguageListener();
    }
  }

  private void removeUnitListenerPanelDataRun() {
    if (panelDateRun != null && panelDateRun instanceof UnitListener) {
      UnitManager.getManager().removeUnitListener((UnitListener) panelDateRun);
      ((UnitListener) panelDateRun).completedRemoveUnitListener();
    }
  }

  /**
   * This method initializes jButtonCalendar
   * 
   * @return javax.swing.JButton
   */
  private JToggleButton getJButtonCalendar() {
    if (jButtonCalendar == null) {
      Icon icon = ImagesMenuRepository.getImageIcon("schedule-12px.png");
      jButtonCalendar = new JToggleButton(icon);
    }
    return jButtonCalendar;
  }

  /**
   * This method initializes jButtonCalendar
   * 
   * @return javax.swing.JButton
   */
  private JToggleButton getJButtonList() {
    if (jButtonList == null) {
      Icon icon = ImagesMenuRepository.getImageIcon("table-12px.png");
      jButtonList = new JToggleButton(icon);
    }
    return jButtonList;
  }

  private JToggleButton getJButtonTree() {
    if (jButtonTree == null) {
      Icon icon = ImagesMenuRepository.getImageIcon("view_tree-12px.png");
      jButtonTree = new JToggleButton(icon);
    }
    return jButtonTree;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireSportChanged
   * (int)
   */
  public void fireSportChanged(Date date, int sportType) {
    panelDateRun.fireSportChanged(date, sportType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireDatesUnselect
   * ()
   */
  public void fireDatesUnselect() {
    panelDateRun.fireDatesUnselect();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireDateChanged
   * (java.util.Date)
   */
  public void fireDateChanged(Date date) {
    panelDateRun.fireDateChanged(date);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireHistoric
   * (int, fr.turtlesport.db.DataSearchRun)
   */
  public void fireHistoric(final int idUser, DataSearchRun search) throws SQLException {
    panelDateRun.fireHistoric(idUser, search);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#fireDateDeleted
   * (java.util.Date)
   */
  public void fireDateDeleted(Date date) {
    panelDateRun.fireDateDeleted(date);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.calendar.IListDateRunFire#
   * fireCalendarSelectActiveDayPerformed(java.util.Date)
   */
  public void fireCalendarSelectActiveDayPerformed(Date date) {
    panelDateRun.fireCalendarSelectActiveDayPerformed(date);
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class ListRunAction extends AbstractAction {

    public ListRunAction() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      new CursorSwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {

          if (!(panelDateRun instanceof JPanelTableRun)) {
            // listener
            removeLanguageListenerPanelDataRun();
            removeUnitListenerPanelDataRun();

            // Config
            Configuration.getConfig().addProperty("general",
                                                  "calendarView",
                                                  Integer.toString(VIEW_LIST));

            // Ajout du composant
            Component cmp = (Component) panelDateRun;

            panelDateRun = new JPanelTableRun();
            JPanelListDateRun.this.remove(cmp);
            JPanelListDateRun.this.add((Component) panelDateRun,
                                       BorderLayout.CENTER);
            // panelDateRun.needDrngDrop(isNeedDrngDrop);

            // Recuperation des dates
            MainGui.getWindow().fireHistoric();
          }
          return null;
        }
      }.execute();
    }
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class ListTreeRunAction extends AbstractAction {

    public ListTreeRunAction() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      new CursorSwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {
          if (!(panelDateRun instanceof JPanelTreeRun)) {
            // listener
            removeLanguageListenerPanelDataRun();
            removeUnitListenerPanelDataRun();

            // Config
            Configuration.getConfig().addProperty("general",
                                                  "calendarView",
                                                  Integer.toString(VIEW_TREE));

            // Ajout du composant
            Component cmp = (Component) panelDateRun;

            panelDateRun = new JPanelTreeRun();
            JPanelListDateRun.this.remove(cmp);
            JPanelListDateRun.this.add((Component) panelDateRun,
                                       BorderLayout.CENTER);
            // panelDateRun.needDrngDrop(isNeedDrngDrop);

            // Recuperation des dates
            MainGui.getWindow().fireHistoric();
          }
          return null;
        }
      }.execute();
    }
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class CalendarRunAction extends AbstractAction {

    public CalendarRunAction() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      new CursorSwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {
          if (!(panelDateRun instanceof JPanelCalendar)) {
            removeLanguageListenerPanelDataRun();
            removeUnitListenerPanelDataRun();
            // Config
            Configuration.getConfig().addProperty("general",
                                                  "calendarView",
                                                  Integer
                                                      .toString(VIEW_CALENDAR));
            // Ajout du composant
            Component cmp = (Component) panelDateRun;

            panelDateRun = new JPanelCalendar();
            JPanelListDateRun.this.remove(cmp);
            JPanelListDateRun.this.add((Component) panelDateRun,
                                       BorderLayout.CENTER);

            // Recuperation des dates
            MainGui.getWindow().fireHistoric();
          }
          return null;
        }
      }.execute();
    }
  }

}