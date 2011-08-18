package fr.turtlesport.ui.swing.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataHeartZone;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.DataSpeedZone;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.SwingLookAndFeel;
import fr.turtlesport.ui.swing.component.calendar.JPanelListDateRun;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.unit.event.UnitEvent;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelRun {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelRun.class);
  }

  public DataRun              dataRun;

  /**
   * 
   */
  public ModelRun() {
    super();
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JPanelRun view, Date date) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>updateView " + date);
    }

    if (date == null) {
      eraseGui(view);
      dataRun = null;
      return;
    }

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().findNext(MainGui.getWindow()
                                                         .getCurrentIdUser(),
                                                     date);
    // mis a jour de la vue
    update(view);

    log.info("<<updateView");
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JPanelRun view, DataRun run) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>updateView");
    }

    if (run == null) {
      eraseGui(view);
      dataRun = null;
      return;
    }

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().retreiveWithID(run.getId());

    // mis a jour de la vue
    update(view);

    log.info("<<updateView");
  }

  /**
   * @param summary
   * @throws SQLException
   */
  public void updateViewNext(JPanelRun view) throws SQLException {
    log.debug(">>updateViewNext");

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().findNext(MainGui.getWindow()
                                                         .getCurrentIdUser(),
                                                     dataRun.getTime());

    // mis a jour de la vue
    update(view);

    // mis a jour du calendrier
    JPanelListDateRun jPanelView = MainGui.getWindow().getListDateRun();
    if (jPanelView != null) {
      jPanelView.fireDateChanged(dataRun.getTime());
    }

    log.debug(">>updateViewNext");
  }

  /**
   * @param summary
   * @throws SQLException
   */
  public void updateViewPrev(JPanelRun view) throws SQLException {
    log.debug(">>updateViewPrev");

    // recuperation des donnees
    dataRun = RunTableManager.getInstance().findPrev(MainGui.getWindow()
                                                         .getCurrentIdUser(),
                                                     dataRun.getTime());

    // mis a jour de la vue
    update(view);

    // mis a jour du calendrier
    JPanelListDateRun jPanelCalendar = MainGui.getWindow().getListDateRun();
    if (jPanelCalendar != null) {
      jPanelCalendar.fireDateChanged(dataRun.getTime());
    }

    log.debug(">>updateViewPrev");
  }

  /**
   * @param run
   * @param event
   */
  public void performedUnit(JPanelRun view, UnitEvent e) {
    if (!e.isEventDistance()) {
      return;
    }

    if (dataRun != null) {
      double distTot = 0;
      int timeTot = 0;
      try {
        timeTot = dataRun.computeTimeTot();
        dataRun.setUnit(e.getUnit());
        distTot = dataRun.getComputeDistanceTot();
      }
      catch (SQLException sqle) {
        // ne peut arriver
      }

      // distance
      view.getJLabelValDistTot().setText(DistanceUnit.formatWithUnit(distTot));

      // Temps
      view.getJLabelValTimeTot()
          .setText(TimeUnit.formatHundredSecondeTime(timeTot));

      // vitesse moyenne
      view.getJLabelValSpeedMoy()
          .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(distTot, timeTot));

      // allure moyenne
      view.getJLabelValAllure()
          .setText(PaceUnit.computeFormatAllureWithUnit(distTot, timeTot));
    }

    // mis a jour des tours intermediaires.
    // ------------------------------------------------------
    view.getTableModelLap().performedUnit(e);

    // mis ajour des zones cadiaques
    // --------------------------------------------------
    try {
      updateHeartZone(view);
    }
    catch (SQLException sqle) {
    }

  }

  /**
   * Mis a jour de la vue.
   */
  private void update(JPanelRun view) throws SQLException {
    log.info(">>update");

    // mis a jour du panel general
    // -------------------------------
    updateSummary(view);

    // mis a jour du graph et de la vue
    // --------------------------------------------------
    // recuperation des donnees
    ModelPointsManager.getInstance().setDataRun(this, dataRun);

    // mis a jour de la vue.
    // view.getJPanelMap().getModel().updateData(dataRun, listGeo);

    // Mis a jour des boutons et menu
    // --------------------------------------------------------------------------------
    boolean hasPoint = ModelPointsManager.getInstance().hasPoints();
    view.setEnableMenuRun(hasPoint);
    view.getJMenuItemRunDelete().setEnabled(true);
    view.getJButtonDelete().setEnabled(true);
    MainGui.getWindow().setEnableMenuRun(hasPoint);
    MainGui.getWindow().getJMenuItemRunDelete().setEnabled(true);

    // Pour les boutons de navigation avec CDE/Motif
    // --------------------------------------------------------------------------------
    if (SwingLookAndFeel.isLookAndFeelMotif()) {
      MainGui.getWindow().updateComponentTreeUI();
    }

    // Mis a jour des zones cardiaque
    // --------------------------------------------------
    updateHeartZone(view);

    log.info("<<update");
  }

  /**
   * Effacement de la vue.
   */
  private void eraseGui(JPanelRun view) {
    view.getJLabelValDistTot().setText(null);
    view.getJLabelValTimeTot().setText(null);
    view.getJLabelValAllure().setText(null);
    view.getJLabelValSpeedMoy().setText(null);
    view.getJLabelValCalories().setText(null);
    view.getJLabelValHeart().setText(null);
    view.getJLabelValAlt().setText(null);
    view.getModelActivities().setSelectedItem("");
    view.getModelEquipements().setSelectedItem("");
    view.getJTextFieldNotes().setText("");
    view.getJButtonNext().setEnabled(false);
    view.getJButtonPrev().setEnabled(false);
    view.getJButtonGoogleEarth().setEnabled(false);
    view.getJButtonGoogleMap().setEnabled(false);
    if (view.getJButtonEmail() != null) {
      view.getJButtonEmail().setEnabled(false);
    }
    view.setEnableMenuRun(false);
    MainGui.getWindow().setEnableMenuRun(false);

    try {
      ModelPointsManager.getInstance().setDataRun(this, null);
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }

  /**
   * 
   */
  private void updateSummary(JPanelRun view) throws SQLException {
    log.info(">>updateSummary");

    if (dataRun == null) {
      return;
    }

    int value;
    if (!dataRun.getUnit().equals(DistanceUnit.getDefaultUnit())) {
      // distance
      dataRun.setUnit(DistanceUnit.getDefaultUnit());
    }

    // distance
    view.getJLabelValDistTot().setText(DistanceUnit.formatWithUnit(dataRun
        .getComputeDistanceTot()));

    // Temps
    view.getJLabelValTimeTot()
        .setText(TimeUnit.formatHundredSecondeTime(dataRun.computeTimeTot()));

    // vitesse moyenne
    view.getJLabelValSpeedMoy()
        .setText(SpeedPaceUnit.computeFormatSpeedWithUnit(dataRun
            .getComputeDistanceTot(), dataRun.computeTimeTot()));

    // allure moyenne
    view.getJLabelValAllure()
        .setText(PaceUnit.computeFormatAllureWithUnit(dataRun
            .getComputeDistanceTot(), dataRun.computeTimeTot()));

    // calories.
    value = RunLapTableManager.getInstance().computeCalories(dataRun.getId());
    view.getJLabelValCalories().setText(Integer.toString(value));

    // frequence moyenne/max/min.
    int avg = RunLapTableManager.getInstance().heartAvg(dataRun.getId());
    int min = RunTrkTableManager.getInstance().heartMin(dataRun.getId());
    int max = RunLapTableManager.getInstance().heartMax(dataRun.getId());

    view.getJLabelValHeart().setText(Integer.toString(avg) + " / "
                                     + Integer.toString(min) + " / "
                                     + Integer.toString(max));

    // Altitude.
    int[] alt = RunTrkTableManager.getInstance().altitude(dataRun.getId());
    view.getJLabelValAlt().setText("+" + Integer.toString(alt[0]) + " / -"
                                   + Integer.toString(alt[1]));

    // Activites
    view.getModelActivities().setSelectedActivity(dataRun.getSportType());

    // Equipement
    if (dataRun.getEquipement() != null) {
      view.getModelEquipements().setSelectedItem(dataRun.getEquipement());
    }
    else {
      view.getModelEquipements().setSelectedItem("");
    }
    view.getJTextFieldNotes().requestFocus();

    // Commentaires
    view.getJTextFieldNotes().setText((dataRun.getComments() == null) ? ""
        : dataRun.getComments());

    updateViewButtons(view);

    log.info("<<updateSummary");
  }

  /**
   * Mis &agrave; jour des boutons.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateViewButtons(JPanelRun view) throws SQLException {
    if (dataRun != null) {
      // Bouton suivant
      view.getJButtonNext().setEnabled(RunTableManager.getInstance()
          .hasNext(MainGui.getWindow().getCurrentIdUser(), dataRun.getTime()));

      // Bouton precedent
      view.getJButtonPrev().setEnabled(RunTableManager.getInstance()
          .hasPrev(MainGui.getWindow().getCurrentIdUser(), dataRun.getTime()));
    }
  }

  /**
   * Sauvegarde des commentaires.
   */
  public void saveComments(JPanelRun view) throws SQLException {
    if (dataRun == null) {
      return;
    }

    String comments = dataRun.getComments();
    if (comments == null) {
      comments = "";
    }

    String newComments = view.getJTextFieldNotes().getText();
    if (newComments == null) {
      newComments = "";
    }

    if (!newComments.equals(comments)) {
      RunTableManager.getInstance()
          .updateComments(dataRun.getId(), newComments);
      dataRun.setComments(newComments);
    }
  }

  /**
   * Sauvegarde de &eacute;quipement.
   */
  public void saveEquipment(JPanelRun view) throws SQLException {
    if (dataRun == null) {
      return;
    }

    String equipment = dataRun.getEquipement();
    if (equipment == null) {
      equipment = "";
    }

    String newEquipment = (String) view.getModelEquipements().getSelectedItem();
    if (newEquipment == null) {
      newEquipment = "";
    }

    if (!newEquipment.equals(equipment)) {
      RunTableManager.getInstance().updateEquipment(dataRun.getId(),
                                                    newEquipment);
      dataRun.setEquipement(newEquipment);
    }
  }

  /**
   * Sauvegarde du type de sport.
   */
  public void saveSportType(JPanelRun view) throws SQLException {
    if (dataRun == null) {
      return;
    }

    int sportType = dataRun.getSportType();
    int newSportType = view.getModelActivities().getSportType();
    if (sportType != newSportType) {
      RunTableManager.getInstance().updateSport(dataRun.getId(), newSportType);
      dataRun.setSportType(newSportType);
      if (MainGui.getWindow().getListDateRun() != null) {
        MainGui.getWindow().getListDateRun().fireSportChanged(dataRun.getTime(), newSportType);
      }
    }
  }

  /**
   * Suppression de la course.
   * 
   * @param view
   * @throws SQLException
   */
  public void delete(JPanelRun view) throws SQLException {
    DataRun dataRun = ModelPointsManager.getInstance().getDataRun();
    if (dataRun == null) {
      return;
    }

    RunTableManager.getInstance().delete(dataRun.getId());

    // suppression de la date du calendrier si besoin
    JPanelListDateRun jPanelListDataRun = MainGui.getWindow().getListDateRun();
    if (jPanelListDataRun != null) {
      jPanelListDataRun.fireDateDeleted(dataRun.getTime());
    }

    if (view.getJButtonNext().isEnabled()) {
      updateViewNext(view);
    }
    else if (view.getJButtonPrev().isEnabled()) {
      updateViewPrev(view);
    }
    else {
      eraseGui(view);
    }
  }

  /**
   * Mis &agrave; jour des boutons.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateButtons(JPanelRun view) throws SQLException {
    if (dataRun != null) {
      updateViewButtons(view);
    }
  }

  /**
   * Mis &agrave; jour des zones cardiaques.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateHeartZone(JPanelRun view) throws SQLException {
    if (dataRun == null) {
      view.getChartHeartZone().setChart(null);
      return;
    }

    AbstractDataActivity dataActivity = UserActivityTableManager.getInstance()
        .retreive(view.getModelActivities().getSportType());
    DataHeartZone[] hz = dataActivity.getHeartZones();
    DataSpeedZone[] sz = dataActivity.getSpeedZones();

    long thz[] = new long[hz.length];
    float dhz[] = new float[hz.length];
    long tsz[] = new long[sz.length];
    float dsz[] = new float[sz.length];

    List<DataRunTrk> trks = ModelPointsManager.getInstance().getListTrks();
    if (trks != null) {
      for (int i = 0; i < trks.size() - 1; i++) {
        // heart
        int h = trks.get(i).getHeartRate();
        if (h == 0) {
          continue;
        }
        else {
          for (int j = 0; j < hz.length; j++) {
            if (h >= hz[j].getLowHeartRate() && h < hz[j].getHighHeartRate()) {
              thz[j] += trks.get(i + 1).getTime().getTime()
                        - trks.get(i).getTime().getTime();
              dhz[j] += trks.get(i + 1).getDistance()
                        - trks.get(i).getDistance();
              break;
            }
          }
        }
        // speed
        double s = trks.get(i).getSpeed();
        if (s <= 0) {
          continue;
        }
        else {
          for (int j = 0; j < sz.length; j++) {
            if (s >= sz[j].getLowSpeed() && s < sz[j].getHighSpeed()) {
              tsz[j] += trks.get(i + 1).getTime().getTime()
                        - trks.get(i).getTime().getTime();
              dsz[j] += trks.get(i + 1).getDistance()
                        - trks.get(i).getDistance();
              break;
            }
          }
        }
      }
    }

    // heart
    // ----------------------
    long thzTot = 0;
    for (long v : thz) {
      thzTot += v;
    }
    if (thzTot == 0) {
      thzTot = 1;
    }
    DefaultCategoryDataset datasetHeart = new DefaultCategoryDataset();
    NumberFormat nf = NumberFormat.getPercentInstance(LanguageManager
        .getManager().getLocale());
    double p1, p2;
    String lib;
    String fh = "{0}-{1}";
    String ftext1 = "{0} {1} : {2}-{3} ({4}-{5})";
    String ftext2 = "<html><body>{0} ({1}) {2}</body></html>";
    for (int i = 0; i < hz.length; i++) {
      p1 = 1.0 * hz[i].getLowHeartRate() / dataActivity.getMaxHeartRate();
      p2 = 1.0 * hz[i].getHighHeartRate() / dataActivity.getMaxHeartRate();
      lib = MessageFormat.format(fh,
                                 hz[i].getLowHeartRate(),
                                 hz[i].getHighHeartRate())
            + " ("
            + MessageFormat.format(fh, nf.format(p1), nf.format(p2))
            + ")";
      datasetHeart.setValue(new LongExt(thz[i], thzTot, dhz[i], lib),
                            "",
                            "" + (i + 1));

      view.getjLabelLibHearts()[i].setText(MessageFormat.format(ftext1, view
          .getResourceBundle().getString("zone"), (i + 1), hz[i]
          .getLowHeartRate(), hz[i].getHighHeartRate(), nf.format(p1), nf
          .format(p2)));
      view.getjLabelValHearts()[i].setText(lib);

      double v = 1.0 * thz[i] / thzTot;
      view.getjLabelValHearts()[i].setText(MessageFormat
          .format(ftext2,
                  TimeUnit.formatMilliSecondeTime(thz[i]),
                  nf.format(v),
                  DistanceUnit.formatWithUnit(dhz[i])));
    }
    JFreeChart chartHeart = ChartFactory
        .createBarChart(null,
                        null,
                        null,
                        datasetHeart,
                        PlotOrientation.VERTICAL,
                        false,
                        true,
                        false);
    CategoryPlot plot = (CategoryPlot) chartHeart.getPlot();
    plot.getDomainAxis().setLabelFont(GuiFont.FONT_ITALIC);
    plot.getDomainAxis().setTickLabelFont(GuiFont.FONT_PLAIN_SMALL);
    // plot.getDomainAxis()
    // .setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.black);
    plot.setRangeGridlinePaint(Color.black);

    DateAxis dateAxis = new DateAxis();
    dateAxis.setLabelFont(GuiFont.FONT_ITALIC);
    dateAxis.setTickLabelFont(GuiFont.FONT_PLAIN_SMALL);
    dateAxis.setVerticalTickLabels(false);

    RelativeDateFormat rdf = new RelativeDateFormat();
    rdf.setShowZeroDays(false);
    rdf.setSecondFormatter(new DecimalFormat("00"));
    rdf.setMinuteFormatter(new DecimalFormat("00"));
    rdf.setHourFormatter(new DecimalFormat("00"));
    rdf.setSecondSuffix("");
    rdf.setMinuteSuffix(":");
    rdf.setHourSuffix(":");
    dateAxis.setDateFormatOverride(rdf);
    plot.setRangeAxis(dateAxis);

    // renderer
    CategoryItemRenderer renderer = plot.getRenderer();
    String format = "<html><body><font color=\"red\">\u2665</font>&nbsp;{0}<br>{1} ({2})<br>{3}</body></html>";

    StandardCategoryToolTipGeneratorExt generator = new StandardCategoryToolTipGeneratorExt(format,
                                                                                            rdf);
    renderer.setBaseToolTipGenerator(generator);
    // set up gradient paints for series...
    final GradientPaint gp = new GradientPaint(0.0f,
                                               0.0f,
                                               Color.blue,
                                               0.0f,
                                               0.0f,
                                               Color.lightGray);
    renderer.setSeriesPaint(0, gp);
    renderer.setBaseItemLabelFont(GuiFont.FONT_PLAIN_SMALL);
    chartHeart.setBackgroundPaint(view.getBackground());
    // font
    if (chartHeart.getTitle() != null) {
      chartHeart.getTitle().setFont(GuiFont.FONT_PLAIN);
    }
    int i = 0;
    while (chartHeart.getLegend(i) != null) {
      chartHeart.getLegend(i).setItemFont(GuiFont.FONT_PLAIN);
      i++;
    }
    view.getChartHeartZone().setChart(chartHeart);

    view.getChartHeartZone().setMouseZoomable(true, true);
    view.getChartHeartZone().setMouseWheelEnabled(false);
    view.getChartHeartZone().setRangeZoomable(false);
    view.getChartHeartZone().setDomainZoomable(true);
    view.getChartHeartZone().setFillZoomRectangle(true);
    view.getChartHeartZone().setLocale(LanguageManager.getManager()
        .getCurrentLang().getLocale());

    // speed
    // ----------------------
    long tszTot = 0;
    for (long v : tsz) {
      tszTot += v;
    }
    if (tszTot == 0) {
      tszTot = 1;
    }

    DefaultCategoryDataset datasetSpeed = new DefaultCategoryDataset();
    String fs = "{0}-{1} {2} <br>{3}-{4} {5}";

    for (i = 0; i < sz.length; i++) {
      double sl, sh;
      if (!DistanceUnit.isDefaultUnitKm()) {
        sl = (Double) SpeedPaceUnit.convert(SpeedUnit.unitKmPerH(),
                                            SpeedUnit.unitMilePerH(),
                                            sz[i].getLowSpeed());
        sh = (Double) SpeedPaceUnit.convert(SpeedUnit.unitKmPerH(),
                                            SpeedUnit.unitMilePerH(),
                                            sz[i].getHighSpeed());
        lib = MessageFormat.format(fs,
                                   SpeedPaceUnit.formatSpeed(sl),
                                   SpeedPaceUnit.formatSpeed(sh),
                                   SpeedUnit.unitMilePerH(),
                                   SpeedPaceUnit.convert(SpeedUnit
                                       .unitMilePerH(), SpeedPaceUnit
                                       .unitMnPerMile(), sl),
                                   SpeedPaceUnit.convert(SpeedUnit
                                       .unitMilePerH(), SpeedPaceUnit
                                       .unitMnPerMile(), sh),
                                   SpeedPaceUnit.unitMnPerMile());
      }
      else {
        sl = sz[i].getLowSpeed();
        sh = sz[i].getHighSpeed();
        lib = MessageFormat
            .format(fs,
                    SpeedPaceUnit.formatSpeed(sl),
                    SpeedPaceUnit.formatSpeed(sh),
                    SpeedUnit.unitKmPerH(),
                    SpeedPaceUnit.convert(SpeedUnit.unitKmPerH(),
                                          SpeedPaceUnit.unitMnPerkm(),
                                          sl),
                    SpeedPaceUnit.convert(SpeedUnit.unitKmPerH(),
                                          SpeedPaceUnit.unitMnPerkm(),
                                          sh),
                    SpeedPaceUnit.unitMnPerkm());
      }
      datasetSpeed.setValue(new LongExt(tsz[i], tszTot, dsz[i], lib),
                            "",
                            "" + (i + 1));

      double v = 1.0 * tsz[i] / tszTot;
      view.getjLabelValSpeeds()[i].setText(MessageFormat
          .format(ftext2,
                  TimeUnit.formatMilliSecondeTime(tsz[i]),
                  nf.format(v),
                  DistanceUnit.formatWithUnit(dsz[i])));
    }
    JFreeChart chartSpeed = ChartFactory
        .createBarChart(null,
                        null,
                        null,
                        datasetSpeed,
                        PlotOrientation.VERTICAL,
                        false,
                        true,
                        false);
    plot = (CategoryPlot) chartSpeed.getPlot();
    plot.getDomainAxis().setLabelFont(GuiFont.FONT_ITALIC);
    plot.getDomainAxis().setTickLabelFont(GuiFont.FONT_PLAIN_SMALL);
    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.black);
    plot.setRangeGridlinePaint(Color.black);

    dateAxis = new DateAxis();
    dateAxis.setLabelFont(GuiFont.FONT_ITALIC);
    dateAxis.setTickLabelFont(GuiFont.FONT_PLAIN_SMALL);
    dateAxis.setVerticalTickLabels(false);

    rdf = new RelativeDateFormat();
    rdf.setShowZeroDays(false);
    rdf.setSecondFormatter(new DecimalFormat("00"));
    rdf.setMinuteFormatter(new DecimalFormat("00"));
    rdf.setHourFormatter(new DecimalFormat("00"));
    rdf.setSecondSuffix("");
    rdf.setMinuteSuffix(":");
    rdf.setHourSuffix(":");
    dateAxis.setDateFormatOverride(rdf);
    plot.setRangeAxis(dateAxis);

    // renderer
    renderer = plot.getRenderer();
    format = "<html><body>{0}<br>{1} ({2})<br>{3}</body></html>";

    generator = new StandardCategoryToolTipGeneratorExt(format, rdf);
    renderer.setBaseToolTipGenerator(generator);
    // set up gradient paints for series...
    renderer.setSeriesPaint(0, gp);
    renderer.setBaseItemLabelFont(GuiFont.FONT_PLAIN_SMALL);
    chartSpeed.setBackgroundPaint(view.getBackground());
    // font
    if (chartSpeed.getTitle() != null) {
      chartSpeed.getTitle().setFont(GuiFont.FONT_PLAIN);
    }
    i = 0;
    while (chartSpeed.getLegend(i) != null) {
      chartSpeed.getLegend(i).setItemFont(GuiFont.FONT_PLAIN);
      i++;
    }
    view.getChartSpeedZone().setChart(chartSpeed);
    view.getChartSpeedZone().setMouseZoomable(true, true);
    view.getChartSpeedZone().setMouseWheelEnabled(false);
    view.getChartSpeedZone().setRangeZoomable(false);
    view.getChartSpeedZone().setDomainZoomable(true);
    view.getChartSpeedZone().setFillZoomRectangle(true);
    view.getChartSpeedZone().setLocale(LanguageManager.getManager()
        .getCurrentLang().getLocale());

  }

  private class LongExt extends Number {
    private Long   value;

    private long   tot;

    private float  distance;

    private String lib;

    public LongExt(long value, long tot, float distance, String lib) {
      this.value = value;
      this.tot = tot;
      this.distance = distance;
      this.lib = lib;
    }

    public float getDistance() {
      return distance;
    }

    public long getTot() {
      return tot;
    }

    @Override
    public double doubleValue() {
      return value.doubleValue();
    }

    @Override
    public float floatValue() {
      return value.floatValue();
    }

    @Override
    public int intValue() {
      return value.intValue();
    }

    @Override
    public long longValue() {
      return value.longValue();
    }
  }

  private class StandardCategoryToolTipGeneratorExt extends
      StandardCategoryToolTipGenerator {
    public StandardCategoryToolTipGeneratorExt(String format, DateFormat rdf) {
      super(format, rdf);
    }

    @Override
    public String generateToolTip(CategoryDataset dataset, int row, int column) {
      Object[] items = createItemArray(dataset, row, column);

      LongExt value = (LongExt) dataset.getValue(row, column);

      NumberFormat f = NumberFormat.getPercentInstance(LanguageManager
          .getManager().getLocale());

      double v = 1.0 * value.longValue() / value.getTot();

      return MessageFormat.format(getLabelFormat(), value.lib, items[2], f
          .format(v), DistanceUnit.formatWithUnit(value.getDistance()));
    }
  }
}
