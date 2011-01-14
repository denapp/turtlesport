package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataStat;
import fr.turtlesport.db.DataStatTot;
import fr.turtlesport.db.DataStatYear;
import fr.turtlesport.db.DataStatYearMonth;
import fr.turtlesport.db.DataStatYearWeek;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelStat extends JPanel implements LanguageListener,
                                      UnitListener, UserListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelStat.class);
  }

  private int                 idUser = MainGui.getWindow().getCurrentIdUser();

  private JPanel              jPanelCenter;

  private JPanel              jPanelSummary;

  private TitledBorder        borderPanelRunSummary;

  private JLabel              jLabelLibDistTot;

  private JLabel              jLabelValDistTot;

  private JLabel              jLabelLibAllure;

  private JLabel              jLabelValAllure;

  private JLabel              jLabelLibSpeedMoy;

  private JLabel              jLabelValSpeedMoy;

  private JLabel              jLabelLibTimeTot;

  private JLabel              jLabelValTimeTot;

  private JLabel              jLabelLibRaces;

  private JLabel              jLabelValRaces;

  private DataRunTot          runTot;

  private JFreeChart          chart;

  private ChartPanel          jPanelChart;

  private JPanel              jPanelSelectChart;

  private JComboBox           jComboxBoxCriter1;

  private JComboBox           jComboxBoxCriter2;

  private TitledBorder        borderPanelSelect;

  /**
   * 
   */
  public JPanelStat() {
    super();
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.UserListener#userSelect(int)
   */
  public void userSelect(final int idUser) throws SQLException {
    if (SwingUtilities.isEventDispatchThread()) {
      notifyUserSelect(idUser);
    }
    else {
      MainGui.getWindow().beforeRunnableSwing();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            notifyUserSelect(idUser);
          }
          catch (SQLException e) {
            log.error("", e);
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), MainGui.class);
            JShowMessage.error(rb.getString("errorSQL"));
          }
          catch (Throwable e) {
            log.error("", e);
          }
          MainGui.getWindow().afterRunnableSwing();
        }
      });
    }

  }

  private void notifyUserSelect(final int idUser) throws SQLException {
    log.debug(">>notifyUserSelect idUser=" + idUser);

    this.idUser = idUser;
    runTot = new DataRunTot();

    DataStatTot dt = RunLapTableManager.getInstance().total();
    runTot.distanceTot = dt.getDistanceTot();
    runTot.timeTot = dt.getTimeTot();
    runTot.calories = dt.getCalories();

    // distance
    jLabelValDistTot.setText(DistanceUnit
        .formatWithDefaultUnit(runTot.distanceTot));

    // Temps
    jLabelValTimeTot.setText(TimeUnit.formatHundredSecondeTime(runTot.timeTot));

    // vitesse moyenne
    jLabelValSpeedMoy.setText(SpeedPaceUnit
        .computeFormatSpeedWithUnit(runTot.distanceTot, runTot.timeTot));

    // allure moyenne
    jLabelValAllure.setText(PaceUnit
        .computeFormatAllureWithUnit(runTot.distanceTot, runTot.timeTot));

    // courses
    jLabelValRaces.setText(Integer.toString(RunLapTableManager.getInstance()
        .count(idUser)));

    // graph
    updateChart();

    if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
      performedUnit(DistanceUnit.getDefaultUnit());
    }

    log.debug("<<notifyUserSelect");
  }

  private void updateChart() {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());
    String libRace = rb.getString("titleChart");

    final String[] criter1 = { "createChartDistance",
        "createChartTime",
        "createChartRaceNumber" };
    final String[] criter2 = { "Day", "DayOfWeek", "Week", "Month", "Year" };

    String methodName = criter1[jComboxBoxCriter1.getSelectedIndex()]
                        + criter2[jComboxBoxCriter2.getSelectedIndex()];

    try {
      Method method = getClass().getMethod(methodName, String.class);
      chart = (JFreeChart) method.invoke(this, libRace);
    }
    catch (Throwable e) {
      log.error("", e);
    }

    if (chart == null) {
      return;
    }

    chart.setBackgroundPaint(jPanelSummary.getBackground());

    // font
    if (chart.getTitle() != null) {
      chart.getTitle().setFont(GuiFont.FONT_PLAIN);
    }
    int i = 0;
    while (chart.getLegend(i) != null) {
      chart.getLegend(i).setItemFont(GuiFont.FONT_PLAIN);
      i++;
    }
    jPanelChart.setChart(chart);

    jPanelChart.setMouseZoomable(true, true);
    jPanelChart.setMouseWheelEnabled(false);
    jPanelChart.setRangeZoomable(false);
    jPanelChart.setDomainZoomable(true);
    jPanelChart.setFillZoomRectangle(true);
    jPanelChart.setLocale(LanguageManager.getManager().getCurrentLang()
        .getLocale());
    if (jPanelChart.getPopupMenu() != null) {
      jPanelChart.getPopupMenu().setLocale(LanguageManager.getManager()
          .getCurrentLang().getLocale());
    }
  }

  public JFreeChart createChartTimeDayOfWeek(String libRace) throws SQLException {
    DataStat[] resDayOfWeek = RunLapTableManager.getInstance()
        .statByDayOfWeek(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(new SimpleDateFormat("EEEE"));
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
    TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
    cal.set(GregorianCalendar.MINUTE, 0);
    cal.set(GregorianCalendar.MILLISECOND, 1);
    cal.set(GregorianCalendar.WEEK_OF_MONTH, 2);
    cal.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY);

    long timeInMillisDeb, timeInMillisEnd;
    for (int i = 1; i < resDayOfWeek.length; i++) {
      timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.HOUR_OF_DAY, 24);
      timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
             resDayOfWeek[i].getTimeTot());

      String distance = DistanceUnit.formatWithDefaultUnit(resDayOfWeek[i]
          .getDistance());
      s1.addValueExt(distance, libRace + " : "
                               + resDayOfWeek[i].getNumberRaces());
    }
    timeInMillisDeb = cal.getTimeInMillis();
    cal.add(GregorianCalendar.HOUR_OF_DAY, 24);
    timeInMillisEnd = cal.getTimeInMillis();
    s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
           resDayOfWeek[0].getTimeTot());
    s1.addValueExt(resDayOfWeek[0].getDistance() / 1000.0,
                   libRace + " : " + resDayOfWeek[0].getNumberRaces());

    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartTime(dataset, dateAxis);
  }

  public JFreeChart createChartDistanceDayOfWeek(String libRace) throws SQLException {
    DataStat[] resDayOfWeek = RunLapTableManager.getInstance()
        .statByDayOfWeek(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(new SimpleDateFormat("EEEE"));
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
    TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
    cal.set(GregorianCalendar.MINUTE, 0);
    cal.set(GregorianCalendar.MILLISECOND, 1);
    cal.set(GregorianCalendar.WEEK_OF_MONTH, 2);
    cal.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY);

    long timeInMillisDeb, timeInMillisEnd;
    for (int i = 1; i < resDayOfWeek.length; i++) {
      timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.HOUR_OF_DAY, 24);
      timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
             convertDistance(resDayOfWeek[i].getDistance()));

      String ext1 = TimeUnit.formatHundredSecondeTime((long) resDayOfWeek[i]
          .getTimeTot());
      String ext2 = libRace + " : " + resDayOfWeek[i].getNumberRaces();
      s1.addValueExt(ext1, ext2);
    }

    timeInMillisDeb = cal.getTimeInMillis();
    cal.add(GregorianCalendar.HOUR_OF_DAY, 24);
    timeInMillisEnd = cal.getTimeInMillis();
    s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
           convertDistance(resDayOfWeek[0].getDistance()));

    String ext1 = TimeUnit.formatHundredSecondeTime((long) resDayOfWeek[0]
        .getTimeTot());
    String ext2 = libRace + " : " + resDayOfWeek[0].getNumberRaces();
    s1.addValueExt(ext1, ext2);

    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartDistance(dataset, dateAxis);
  }

  public JFreeChart createChartDistanceYear(String libRace) throws SQLException {
    List<DataStatYear> resYear = RunLapTableManager.getInstance()
        .statByYear(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1));
    dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy", LanguageManager
        .getManager().getCurrentLang().getLocale()));
    final TimePeriodValuesExt sYear = new TimePeriodValuesExt(libRace);
    for (DataStatYear d : resYear) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
      cal.set(GregorianCalendar.MONTH, Calendar.JANUARY);
      cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.YEAR, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      sYear.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
                convertDistance(d.getDistance()));
      String ext1 = TimeUnit.formatHundredSecondeTime((long) d.getTimeTot());
      String ext2 = libRace + " : " + d.getNumberRaces();
      sYear.addValueExt(ext1, ext2);
    }

    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(sYear);

    return createChartDistance(dataset, dateAxis);
  }

  public JFreeChart createChartTimeYear(String libRace) throws SQLException {
    List<DataStatYear> resYear = RunLapTableManager.getInstance()
        .statByYear(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1));
    dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy", LanguageManager
        .getManager().getCurrentLang().getLocale()));
    final TimePeriodValuesExt sYear = new TimePeriodValuesExt(libRace);
    for (DataStatYear d : resYear) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
      cal.set(GregorianCalendar.MONTH, Calendar.JANUARY);
      cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.YEAR, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      sYear.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), d
          .getTimeTot());
      String distance = DistanceUnit.formatWithDefaultUnit(d.getDistance());
      sYear.addValueExt(distance, libRace + " : " + d.getNumberRaces());
    }

    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(sYear);

    return createChartTime(dataset, dateAxis);
  }

  public JFreeChart createChartDistanceMonth(String libRace) throws SQLException {
    // List<DataStatYearMonth> resMonth = RunLapTableManager.getInstance()
    // .statByMonth(idUser);
    List<DataStatYearMonth> resMonth = RunLapTableManager.getInstance()
        .statByMonth(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
    dateAxis.setDateFormatOverride(new SimpleDateFormat("MMMMM yyyy",
                                                        LanguageManager
                                                            .getManager()
                                                            .getCurrentLang()
                                                            .getLocale()));
    final TimePeriodValuesExt sMonth = new TimePeriodValuesExt(libRace);
    for (DataStatYearMonth d : resMonth) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.MONTH, d.getMonth() - 1);
      cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
      cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.MONTH, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      sMonth.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
                 convertDistance(d.getDistance()));
      String ext1 = TimeUnit.formatHundredSecondeTime((long) d.getTimeTot());
      String ext2 = libRace + " : " + d.getNumberRaces();
      sMonth.addValueExt(ext1, ext2);
    }
    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(sMonth);
    return createChartDistance(dataset, dateAxis);
  }

  public JFreeChart createChartTimeMonth(String libRace) throws SQLException {
    List<DataStatYearMonth> resMonth = RunLapTableManager.getInstance()
        .statByMonth(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
    dateAxis.setDateFormatOverride(new SimpleDateFormat("MMMMM yyyy",
                                                        LanguageManager
                                                            .getManager()
                                                            .getCurrentLang()
                                                            .getLocale()));
    final TimePeriodValuesExt sMonth = new TimePeriodValuesExt(libRace);
    for (DataStatYearMonth d : resMonth) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.MONTH, d.getMonth() - 1);
      cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
      cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.MONTH, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      sMonth.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), d
          .getTimeTot());
      String distance = DistanceUnit.formatWithDefaultUnit(d.getDistance());
      sMonth.addValueExt(distance, libRace + " : " + d.getNumberRaces());
    }

    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(sMonth);
    return createChartTime(dataset, dateAxis);
  }

  public JFreeChart createChartDistanceWeek(String libRace) throws SQLException {
    List<DataStatYearWeek> resWeek = RunLapTableManager.getInstance()
        .distanceByWeek(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(new SimpleDateFormat("w-yyyy",
                                                        LanguageManager
                                                            .getManager()
                                                            .getCurrentLang()
                                                            .getLocale()));

    // domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 7));
    final TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    for (DataStatYearWeek d : resWeek) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.WEEK_OF_YEAR, d.getWeek());
      cal.set(GregorianCalendar.HOUR_OF_DAY, 01);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.WEEK_OF_YEAR, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
             convertDistance(d.getDistance()));

      String ext1 = TimeUnit.formatHundredSecondeTime((long) d.getTimeTot());
      String ext2 = libRace + " : " + d.getNumberRaces();
      s1.addValueExt(ext1, ext2);
    }

    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartDistance(dataset, dateAxis);
  }

  public JFreeChart createChartTimeWeek(String libRace) throws SQLException {
    List<DataStatYearWeek> resWeek = RunLapTableManager.getInstance()
        .statByWeek(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(new SimpleDateFormat("w-yyyy",
                                                        LanguageManager
                                                            .getManager()
                                                            .getCurrentLang()
                                                            .getLocale()));

    // domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 7));
    final TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    for (DataStatYearWeek d : resWeek) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.WEEK_OF_YEAR, d.getWeek());
      cal.set(GregorianCalendar.HOUR_OF_DAY, 01);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.WEEK_OF_YEAR, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), d
          .getTimeTot());
      String distance = DistanceUnit.formatWithDefaultUnit(d.getDistance());
      s1.addValueExt(distance, libRace + " : " + d.getNumberRaces());
    }

    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartTime(dataset, dateAxis);
  }

  public JFreeChart createChartDistanceDay(String libRace) throws SQLException {
    List<DataRun> listRun = RunTableManager.getInstance().retreive(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(LanguageManager.getManager()
        .getCurrentLang().getDateFormatter());
    TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    for (DataRun d : listRun) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.setTime(d.getTime());
      cal.set(GregorianCalendar.HOUR_OF_DAY, 1);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.HOUR_OF_DAY, 22);
      long timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
             convertDistance(d.getComputeDistanceTot()));
      s1.addValueExt("", "");
    }

    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartDistance(dataset, dateAxis);
  }

  public JFreeChart createChartTimeDay(String libRace) throws SQLException {
    List<DataRun> listRun = RunTableManager.getInstance().retreive(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(LanguageManager.getManager()
        .getCurrentLang().getDateFormatter());
    // domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
    TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    for (DataRun d : listRun) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.setTime(d.getTime());
      cal.set(GregorianCalendar.HOUR_OF_DAY, 1);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.HOUR_OF_DAY, 22);
      long timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), d
          .computeTimeTot());
      s1.addValueExt("", "");
    }

    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartTime(dataset, dateAxis);
  }

  private JFreeChart createChartDistance(final XYDataset dataset,
                                         final DateAxis dateAxis) {
    dateAxis.setLabelFont(GuiFont.FONT_ITALIC);
    dateAxis.setTickLabelFont(GuiFont.FONT_PLAIN_SMALL);
    dateAxis.setLowerMargin(0.01);
    dateAxis.setUpperMargin(0.01);
    dateAxis.setVerticalTickLabels(true);
    dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

    final ValueAxis rangeAxis = new NumberAxis(DistanceUnit.getDefaultUnit());
    XYBarRenderer renderer = new XYBarRenderer();
    final XYPlot plot = new XYPlot(dataset, dateAxis, rangeAxis, renderer);

    if (dateAxis.getDateFormatOverride() != null) {
      String format = "<html><body>{1}<br>{2} " + DistanceUnit.getDefaultUnit()
                      + "<br>{3}<br>{4}</body></html>";
      StandarXYToolTipGenratorExt generator = new StandarXYToolTipGenratorExt(format,
                                                                              dateAxis
                                                                                  .getDateFormatOverride(),
                                                                              new DecimalFormat("0.00"));
      renderer.setBaseToolTipGenerator(generator);
    }

    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.black);
    plot.setRangeGridlinePaint(Color.black);

    // disable bar outlines...
    renderer.setDrawBarOutline(false);

    // set up gradient paints for series...
    final GradientPaint gp = new GradientPaint(0.0f,
                                               0.0f,
                                               Color.blue,
                                               0.0f,
                                               0.0f,
                                               Color.lightGray);
    renderer.setSeriesPaint(0, gp);

    // set the range axis to display integers only...
    plot.getRangeAxis().setLabelFont(GuiFont.FONT_ITALIC);
    plot.getRangeAxis().setTickLabelFont(GuiFont.FONT_ITALIC);
    // chart
    return new JFreeChart("", plot);
  }

  public JFreeChart createChartTime(final XYDataset dataset,
                                    final DateAxis dateAxis) {
    dateAxis.setLabelFont(GuiFont.FONT_ITALIC);
    dateAxis.setTickLabelFont(GuiFont.FONT_PLAIN_SMALL);
    dateAxis.setLowerMargin(0.01);
    dateAxis.setUpperMargin(0.01);
    dateAxis.setVerticalTickLabels(true);
    dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

    final NumberAxis rangeAxis = new NumberAxis("Heure");
    rangeAxis.setNumberFormatOverride(new HourNumberFormat());
    XYBarRenderer renderer = new XYBarRenderer();
    final XYPlot plot = new XYPlot(dataset, dateAxis, rangeAxis, renderer);

    if (dateAxis.getDateFormatOverride() != null) {
      String format = "<html><body>{1}<br>{2}<br>{3}<br>{4}</body></html>";
      StandarXYToolTipGenratorExt generator = new StandarXYToolTipGenratorExt(format,
                                                                              dateAxis
                                                                                  .getDateFormatOverride(),
                                                                              rangeAxis
                                                                                  .getNumberFormatOverride());

      renderer.setBaseToolTipGenerator(generator);
    }

    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.black);
    plot.setRangeGridlinePaint(Color.black);

    // disable bar outlines...
    renderer.setDrawBarOutline(false);

    // set up gradient paints for series...
    final GradientPaint gp = new GradientPaint(0.0f,
                                               0.0f,
                                               Color.blue,
                                               0.0f,
                                               0.0f,
                                               Color.lightGray);
    renderer.setSeriesPaint(0, gp);

    // set the range axis to display integers only...
    plot.getRangeAxis().setLabelFont(GuiFont.FONT_ITALIC);
    plot.getRangeAxis().setTickLabelFont(GuiFont.FONT_ITALIC);
    // chart
    return new JFreeChart("", plot);
  }

  public JFreeChart createChartRaceNumberMonth(String libRace) throws SQLException {
    List<DataStatYearMonth> resMonth = RunLapTableManager.getInstance()
        .statByMonth(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
    dateAxis.setDateFormatOverride(new SimpleDateFormat("MMMMM yyyy",
                                                        LanguageManager
                                                            .getManager()
                                                            .getCurrentLang()
                                                            .getLocale()));
    final TimePeriodValuesExt sMonth = new TimePeriodValuesExt(libRace);
    for (DataStatYearMonth d : resMonth) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.MONTH, d.getMonth() - 1);
      cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
      cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.MONTH, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      sMonth.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), d
          .getNumberRaces());
      sMonth.addValueExt("", libRace + " : " + d.getDistance() / 1000.0);
    }
    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(sMonth);
    return createChartRaceNumber(dataset, dateAxis);
  }

  public JFreeChart createChartRaceNumberDay(String libRace) throws SQLException {
    List<DataRun> listRun = RunTableManager.getInstance().retreive(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(LanguageManager.getManager()
        .getCurrentLang().getDateFormatter());
    TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    for (DataRun d : listRun) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.setTime(d.getTime());
      cal.set(GregorianCalendar.HOUR_OF_DAY, 1);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.HOUR_OF_DAY, 22);
      long timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), 1);
      s1.addValueExt("", "");
    }

    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartRaceNumber(dataset, dateAxis);
  }

  public JFreeChart createChartRaceNumberWeek(String libRace) throws SQLException {
    List<DataStatYearWeek> resWeek = RunLapTableManager.getInstance()
        .distanceByWeek(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(new SimpleDateFormat("w-yyyy",
                                                        LanguageManager
                                                            .getManager()
                                                            .getCurrentLang()
                                                            .getLocale()));

    // domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 7));
    final TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    for (DataStatYearWeek d : resWeek) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.WEEK_OF_YEAR, d.getWeek());
      cal.set(GregorianCalendar.HOUR_OF_DAY, 01);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.WEEK_OF_YEAR, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), d
          .getNumberRaces());
      s1.addValueExt("", libRace + " : " + d.getDistance() / 1000.0);
    }

    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartRaceNumber(dataset, dateAxis);
  }

  public JFreeChart createChartRaceNumberDayOfWeek(String libRace) throws SQLException {
    DataStat[] resDayOfWeek = RunLapTableManager.getInstance()
        .distanceByDayOfWeek(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setDateFormatOverride(new SimpleDateFormat("EEEE"));
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
    TimePeriodValuesExt s1 = new TimePeriodValuesExt(libRace);
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
    cal.set(GregorianCalendar.MINUTE, 0);
    cal.set(GregorianCalendar.MILLISECOND, 1);
    cal.set(GregorianCalendar.WEEK_OF_MONTH, 2);
    cal.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY);

    long timeInMillisDeb, timeInMillisEnd;
    for (int i = 1; i < resDayOfWeek.length; i++) {
      timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.HOUR_OF_DAY, 24);
      timeInMillisEnd = cal.getTimeInMillis();
      s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
             resDayOfWeek[i].getNumberRaces());
      s1.addValueExt("", libRace + " : " + resDayOfWeek[i].getDistance()
                         / 1000.0);
    }

    timeInMillisDeb = cal.getTimeInMillis();
    cal.add(GregorianCalendar.HOUR_OF_DAY, 24);
    timeInMillisEnd = cal.getTimeInMillis();
    s1.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd),
           resDayOfWeek[0].getNumberRaces());
    s1
        .addValueExt("", libRace + " : " + resDayOfWeek[0].getDistance()
                         / 1000.0);

    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(s1);
    return createChartRaceNumber(dataset, dateAxis);
  }

  public JFreeChart createChartRaceNumberYear(String libRace) throws SQLException {
    List<DataStatYear> resYear = RunLapTableManager.getInstance()
        .distanceByYear(idUser);

    DateAxis dateAxis = new DateAxis("");
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1));
    dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy", LanguageManager
        .getManager().getCurrentLang().getLocale()));
    final TimePeriodValuesExt sYear = new TimePeriodValuesExt(libRace);
    for (DataStatYear d : resYear) {
      Calendar cal = GregorianCalendar.getInstance();
      cal.set(GregorianCalendar.YEAR, d.getYear());
      cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
      cal.set(GregorianCalendar.MONTH, Calendar.JANUARY);
      cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
      cal.set(GregorianCalendar.MINUTE, 0);
      cal.set(GregorianCalendar.MILLISECOND, 0);
      long timeInMillisDeb = cal.getTimeInMillis();
      cal.add(GregorianCalendar.YEAR, 1);
      long timeInMillisEnd = cal.getTimeInMillis();
      sYear.add(new SimpleTimePeriod(timeInMillisDeb, timeInMillisEnd), d
          .getNumberRaces());
      sYear.addValueExt("", libRace + " : " + d.getDistance() / 1000.0);
    }

    final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
    dataset.addSeries(sYear);

    return createChartRaceNumber(dataset, dateAxis);
  }

  public JFreeChart createChartRaceNumber(final XYDataset dataset,
                                          final DateAxis dateAxis) {
    dateAxis.setLabelFont(GuiFont.FONT_ITALIC);
    dateAxis.setTickLabelFont(GuiFont.FONT_PLAIN_SMALL);
    dateAxis.setLowerMargin(0.01);
    dateAxis.setUpperMargin(0.01);
    dateAxis.setVerticalTickLabels(true);
    dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

    final NumberAxis rangeAxis = new NumberAxis("Nombre");
    rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
    XYBarRenderer renderer = new XYBarRenderer();
    final XYPlot plot = new XYPlot(dataset, dateAxis, rangeAxis, renderer);

    if (dateAxis.getDateFormatOverride() != null) {
      String format = "<html><body>{1}<br>{2}<br>{3}</body></html>";
      StandarXYToolTipGenratorExt generator = new StandarXYToolTipGenratorExt(format,
                                                                              dateAxis
                                                                                  .getDateFormatOverride(),
                                                                              rangeAxis
                                                                                  .getNumberFormatOverride());

      renderer.setBaseToolTipGenerator(generator);
    }

    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.black);
    plot.setRangeGridlinePaint(Color.black);

    // disable bar outlines...
    renderer.setDrawBarOutline(false);

    // set up gradient paints for series...
    final GradientPaint gp = new GradientPaint(0.0f,
                                               0.0f,
                                               Color.blue,
                                               0.0f,
                                               0.0f,
                                               Color.lightGray);
    renderer.setSeriesPaint(0, gp);

    // set the range axis to display integers only...
    plot.getRangeAxis().setLabelFont(GuiFont.FONT_ITALIC);
    plot.getRangeAxis().setTickLabelFont(GuiFont.FONT_ITALIC);
    // chart
    return new JFreeChart("", plot);
  }

  /*
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(660, 597);
    this.setLayout(new BorderLayout(10, 10));
    // this.add(getJPanelSummary(), BorderLayout.NORTH);
    this.add(getJPanelCenter(), BorderLayout.CENTER);
    this.setFont(GuiFont.FONT_PLAIN);

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);

    // Liste par defaut
    int index1 = Configuration.getConfig()
        .getPropertyAsInt("Stat", "jComboxBoxCriter1", 0);
    int index2 = Configuration.getConfig()
        .getPropertyAsInt("Stat", "jComboxBoxCriter2", 0);
    jComboxBoxCriter1.setSelectedIndex(index1);
    jComboxBoxCriter2.setSelectedIndex(index2);

    // Evenement
    ActionListener action = new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        MainGui.getWindow().beforeRunnableSwing();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            Configuration.getConfig()
                .addProperty("Stat",
                             "jComboxBoxCriter1",
                             Integer.toString(jComboxBoxCriter1
                                 .getSelectedIndex()));
            Configuration.getConfig()
                .addProperty("Stat",
                             "jComboxBoxCriter2",
                             Integer.toString(jComboxBoxCriter2
                                 .getSelectedIndex()));
            updateChart();
            MainGui.getWindow().afterRunnableSwing();
          }
        });
      }
    };
    jComboxBoxCriter2.addActionListener(action);
    jComboxBoxCriter1.addActionListener(action);
  }

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(lang, getClass());

    jLabelLibDistTot.setText(rb.getString("jLabelLibDistTot"));
    jLabelLibTimeTot.setText(rb.getString("jLabelLibTimeTot"));
    jLabelLibSpeedMoy.setText(rb.getString("jLabelLibSpeedMoy"));
    jLabelLibAllure.setText(rb.getString("jLabelLibAllure"));
    borderPanelRunSummary.setTitle(rb.getString("borderPanelRunSummary"));
    borderPanelSelect.setTitle(rb.getString("borderPanelSelect"));
    jLabelLibRaces.setText(rb.getString("jLabelLibRaces"));

    int index1 = jComboxBoxCriter1.getSelectedIndex();
    jComboxBoxCriter1.removeAllItems();
    jComboxBoxCriter1.addItem(rb.getString("jComboxBoxType.item1"));
    jComboxBoxCriter1.addItem(rb.getString("jComboxBoxType.item2"));
    jComboxBoxCriter1.addItem(rb.getString("jComboxBoxType.item3"));
    int index2 = jComboxBoxCriter2.getSelectedIndex();
    jComboxBoxCriter2.removeAllItems();
    jComboxBoxCriter2.addItem(rb.getString("jComboxBoxDate.item1"));
    jComboxBoxCriter2.addItem(rb.getString("jComboxBoxDate.item2"));
    jComboxBoxCriter2.addItem(rb.getString("jComboxBoxDate.item3"));
    jComboxBoxCriter2.addItem(rb.getString("jComboxBoxDate.item4"));
    jComboxBoxCriter2.addItem(rb.getString("jComboxBoxDate.item5"));

    jComboxBoxCriter1.setSelectedIndex((index1 == -1) ? 0 : index1);
    jComboxBoxCriter2.setSelectedIndex((index2 == -1) ? 0 : index2);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang
   * .LanguageEvent)
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
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent e) {
    if (!e.isEventDistance()) {
      return;
    }
    performedUnit(e.getUnit());
  }

  private void performedUnit(String unit) {
    double distTot = DistanceUnit.convert(runTot.unitDistance,
                                          unit,
                                          runTot.distanceTot);
    int timeTot = runTot.timeTot;

    runTot.distanceTot = distTot;
    runTot.unitDistance = unit;

    // distance
    jLabelValDistTot.setText(DistanceUnit.formatWithDefaultUnit(distTot));

    // vitesse moyenne
    jLabelValSpeedMoy
        .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(distTot, timeTot));

    // allure moyenne
    jLabelValAllure.setText(PaceUnit.computeFormatAllureWithUnit(distTot,
                                                                 timeTot));

    updateChart();
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.Y_AXIS));
      jPanelCenter.add(getJPanelSummary(), null);
      jPanelCenter.add(getJPanelSelectChart(), null);
      jPanelCenter.add(getJPanelChart(), null);
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  public JPanel getJPanelSummary() {
    if (jPanelSummary == null) {
      jPanelSummary = new JPanel();

      borderPanelRunSummary = BorderFactory
          .createTitledBorder(null,
                              "Course",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelSummary.setBorder(borderPanelRunSummary);
      GridLayout layout = new GridLayout(3, 4, 10, 10);
      jPanelSummary.setLayout(layout);

      // distance
      jLabelLibDistTot = new JLabel();
      jLabelLibDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelValDistTot = new JLabel();
      jLabelValDistTot.setFont(GuiFont.FONT_PLAIN);

      // Temps
      jLabelLibTimeTot = new JLabel();
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelValTimeTot = new JLabel();
      jLabelValTimeTot.setFont(GuiFont.FONT_PLAIN);

      // vitesse moyenne
      jLabelLibSpeedMoy = new JLabel();
      jLabelLibSpeedMoy.setFont(GuiFont.FONT_PLAIN);
      jLabelValSpeedMoy = new JLabel();
      jLabelValSpeedMoy.setFont(GuiFont.FONT_PLAIN);

      // allure moyenne
      jLabelLibAllure = new JLabel();
      jLabelLibAllure.setFont(GuiFont.FONT_PLAIN);
      jLabelValAllure = new JLabel();
      jLabelValAllure.setFont(GuiFont.FONT_PLAIN);

      // Course
      jLabelLibRaces = new JLabel();
      jLabelLibRaces.setFont(GuiFont.FONT_PLAIN);
      jLabelValRaces = new JLabel();
      jLabelValRaces.setFont(GuiFont.FONT_PLAIN);

      jPanelSummary.add(jLabelLibDistTot);
      jPanelSummary.add(jLabelValDistTot);
      jPanelSummary.add(jLabelLibTimeTot);
      jPanelSummary.add(jLabelValTimeTot);

      jPanelSummary.add(jLabelLibSpeedMoy);
      jPanelSummary.add(jLabelValSpeedMoy);
      jPanelSummary.add(jLabelLibAllure);
      jPanelSummary.add(jLabelValAllure);

      jPanelSummary.add(jLabelLibRaces);
      jPanelSummary.add(jLabelValRaces);
      jPanelSummary.add(new JLabel());
      jPanelSummary.add(new JLabel());
    }
    return jPanelSummary;
  }

  private JPanel getJPanelSelectChart() {
    if (jPanelSelectChart == null) {
      jPanelSelectChart = new JPanel();
      borderPanelSelect = BorderFactory
          .createTitledBorder(null,
                              "Critères",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelSelectChart.setBorder(borderPanelSelect);

      JLabel label = new JLabel(ImagesRepository.getImageIcon("loupe24.png"));
      jPanelSelectChart.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
      jComboxBoxCriter1 = new JComboBox();
      jComboxBoxCriter1.setFont(GuiFont.FONT_PLAIN);

      jComboxBoxCriter2 = new JComboBox();
      jComboxBoxCriter2.setFont(GuiFont.FONT_PLAIN);

      jPanelSelectChart.add(label);
      jPanelSelectChart.add(jComboxBoxCriter1);
      jPanelSelectChart.add(jComboxBoxCriter2);
    }
    return jPanelSelectChart;
  }

  private JPanel getJPanelChart() {
    if (jPanelChart == null) {
      jPanelChart = new ChartPanel(null);
      jPanelChart.setFont(GuiFont.FONT_PLAIN);
      jPanelChart.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));

    }
    return jPanelChart;
  }

  private double convertDistance(double distance) {
    if (DistanceUnit.isDefaultUnitKm()) {
      return distance / 1000.0;
    }
    return DistanceUnit.convertKmToMile(distance / 1000.0);
  }

  private class DataRunTot {
    String unitDistance = DistanceUnit.unitKm();

    double distanceTot  = 0;

    int    timeTot      = 0;

    int    calories     = 0;

    int    avgRate      = 0;

    int    altPlus      = 0;

    int    altMoins     = 0;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class HourNumberFormat extends NumberFormat {

    /*
     * (non-Javadoc)
     * 
     * @see java.text.NumberFormat#format(double, java.lang.StringBuffer,
     * java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(double number,
                               StringBuffer toAppendTo,
                               FieldPosition pos) {
      return format((long) number, toAppendTo, pos);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.NumberFormat#format(long, java.lang.StringBuffer,
     * java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(long number,
                               StringBuffer toAppendTo,
                               FieldPosition pos) {
      return toAppendTo.append(TimeUnit.formatHundredSecondeTime(number));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.NumberFormat#parse(java.lang.String,
     * java.text.ParsePosition)
     */
    @Override
    public Number parse(String source, ParsePosition parsePosition) {
      // TODO Auto-generated method stub
      return null;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class StandarXYToolTipGenratorExt extends StandardXYToolTipGenerator {

    public StandarXYToolTipGenratorExt(java.lang.String formatString,
                                       DateFormat xFormat,
                                       NumberFormat yFormat) {
      super(formatString, xFormat, yFormat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jfree.chart.labels.AbstractXYItemLabelGenerator#generateLabelString
     * (org.jfree.data.xy.XYDataset, int, int)
     */
    @Override
    public String generateLabelString(XYDataset dataset, int series, int item) {
      String result = null;
      Object[] items = createItemArray(dataset, series, item);
      Object[] itemsExt = new Object[items.length + 2];
      System.arraycopy(items, 0, itemsExt, 0, items.length);

      itemsExt[itemsExt.length - 2] = ((TimePeriodValuesExt) (((TimePeriodValuesCollection) dataset)
          .getSeries(series))).getValueExt1(item);
      itemsExt[itemsExt.length - 1] = ((TimePeriodValuesExt) (((TimePeriodValuesCollection) dataset)
          .getSeries(series))).getValueExt2(item);

      result = MessageFormat.format(getFormatString(), itemsExt);
      return result;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TimePeriodValuesExt extends TimePeriodValues {
    List<Object> dataExt1 = new ArrayList<Object>();

    List<Object> dataExt2 = new ArrayList<Object>();

    public TimePeriodValuesExt(String name) {
      super(name);
    }

    public void addValueExt(Object value1, Object value2) {
      dataExt1.add(value1);
      dataExt2.add(value2);
    }

    public Object getValueExt1(int item) {
      return dataExt1.get(item);
    }

    public Object getValueExt2(int item) {
      return dataExt2.get(item);
    }
  }

}
