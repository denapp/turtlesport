package fr.turtlesport.geo.googlemap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.geo.GeoPosition;
import fr.turtlesport.geo.IGeoConvertProgress;
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.GeoUtil;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class GoogleMapGeo implements IGeoConvertRun {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GoogleMapGeo.class);
  }

  private static final String  MAP_BOUNDS      = "#MAP_BOUNDS#";

  private static final String  MAP_COORDINATE  = "#MAP_COORDINATE#";

  private static final String  MARKER_END      = "#MARKER_END#";

  private static final String  MARKER_START    = "#MARKER_START#";

  private static final String  DAY             = "#DAY#";

  private static final String  DISTANCE        = "#DISTANCE#";

  private static final String  TIME_TOT        = "#TIME_TOT#";

  private static final String  TIME_MOVING     = "#TIME_MOVING#";

  private static final String  TIME_PAUSE      = "#TIME_PAUSE#";

  private static final String  AVG_ALL         = "#AVG_ALL#";

  private static final String  AVG_SPEED       = "#AVG_SPEED#";

  private static final String  CALORIES        = "#CALORIES#";

  private static final String  CARDIO          = "#CARDIO#";

  private static final String  ACTIVITY        = "#ACTIVITY#";

  private static final String  BEGIN_TABLE_LAP = "#BEGIN_TABLE_LAP#";

  private static final String  LAP_TITLE       = "#LAP_TITLE#";

  private static final String  LAP_HEAD        = "#LAP_HEAD#";

  private static final String  LAP_BODY        = "#LAP_BODY#";

  private static final String  END_TABLE_LAP   = "#END_TABLE_LAP#";

  private static final String  LOCALISATION    = "#LOCALISATION#";

  /** Extensions. */
  public static final String[] EXT             = { "gpx" };

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertRun#convert(java.util.List,
   * java.io.File)
   */
  public File convert(List<DataRun> runs,
                      IGeoConvertProgress progress,
                      File file) throws GeoConvertException, SQLException {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertRun#convert(fr.turtlesport.db.DataRun,
   * java.io.File)
   */
  public File convert(DataRun dataRun, File file) throws GeoConvertException,
                                                 SQLException {
    log.debug(">>convert");

    if (dataRun == null) {
      throw new IllegalArgumentException("dataRun est null");
    }
    if (file == null) {
      throw new IllegalArgumentException("file est null");
    }

    List<DataRunTrk> trks = RunTrkTableManager.getInstance()
        .getValidTrks(dataRun.getId());
    if (trks != null && trks.size() < 1) {
      return null;
    }

    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());

    BufferedWriter writer = null;
    BufferedReader reader = null;
    try {
      // mask
      String line;
      reader = new BufferedReader(new InputStreamReader(getClass()
          .getResourceAsStream("google-map-mask.html")));

      writer = new BufferedWriter(new FileWriter(file));

      IGeoPosition sw = new GeoPosition(0, 0);
      IGeoPosition ne = new GeoPosition(0, 0);
      GeoUtil.calculateBounds(sw, ne, trks);
      IGeoPosition mStart = markerStart(trks);
      IGeoPosition mStop = markerEnd(trks);

      dataRun.setUnit(DistanceUnit.getDefaultUnit());
      while ((line = reader.readLine()) != null) {
        if (line.startsWith(MAP_BOUNDS)) {
          mapBounds(writer, line, sw, ne);
        }
        else if (line.startsWith(MAP_COORDINATE)) {
          mapCoordinate(trks, writer, line);
        }
        else if (line.startsWith(MARKER_START)) {
          markerStart(writer, line, mStart);
        }
        else if (line.startsWith(MARKER_END)) {
          markerStop(writer, line, mStop);
        }
        else if (line.startsWith(DAY)) {
          day(dataRun, rb, writer, line);
        }
        else if (line.startsWith(DISTANCE)) {
          distance(dataRun, rb, writer, line);
        }
        else if (line.startsWith(TIME_TOT)) {
          timeTot(dataRun, rb, writer, line);
        }
        else if (line.startsWith(TIME_MOVING)) {
          timeMoving(dataRun, rb, writer, line);
        }
        else if (line.startsWith(TIME_PAUSE)) {
          timePause(dataRun, rb, writer, line);
        }
        else if (line.startsWith(AVG_ALL)) {
          avgAll(dataRun, rb, writer, line);
        }
        else if (line.startsWith(AVG_SPEED)) {
          avgSpeed(dataRun, rb, writer, line);
        }
        else if (line.startsWith(CALORIES)) {
          calories(dataRun, rb, writer, line);
        }
        else if (line.startsWith(CARDIO)) {
          cardio(dataRun, rb, writer, line);
        }
        else if (line.startsWith(LOCALISATION)) {
          localisation(dataRun, rb, writer, line);
        }
        else if (line.startsWith(ACTIVITY)) {
          activity(dataRun, rb, writer, line);
        }
        else if (line.startsWith(BEGIN_TABLE_LAP)) {
          lap(dataRun, rb, reader, writer);
        }
        else {
          writer.write(line);
          writer.write("\r\n");
        }
      }

    }
    catch (IOException e) {
      log.error("", e);
      throw new GeoConvertException(e);
    }
    finally {
      if (reader != null) {
        try {
          reader.close();
        }
        catch (IOException e) {
        }
      }
      if (writer != null) {
        try {
          writer.close();
        }
        catch (IOException e) {
        }
      }
    }

    log.debug("<<convert");
    return file;
  }

  private void localisation(DataRun dataRun,
                            ResourceBundle rb,
                            BufferedWriter writer,
                            String line) throws IOException {
    if (dataRun.getLocation() != null
        && !"".equals(dataRun.getLocation().trim())) {
      line = line.substring(LOCALISATION.length());
      writer.write(MessageFormat.format(line, dataRun.getLocation().trim()));
      writer.write("\r\n");
    }

  }

  private void timeTot(DataRun dataRun,
                       ResourceBundle rb,
                       BufferedWriter writer,
                       String line) throws IOException, SQLException {
    line = line.substring(TIME_TOT.length());
    writer.write(MessageFormat.format(line, rb.getString("TIME_TOT"), TimeUnit
        .formatHundredSecondeTime(dataRun.computeTimeTot())));
    writer.write("\r\n");
  }

  private void timeMoving(DataRun dataRun,
                          ResourceBundle rb,
                          BufferedWriter writer,
                          String line) throws IOException, SQLException {
    if (dataRun.computeTimePauseTot() > 0) {
      line = line.substring(TIME_MOVING.length());
      writer
          .write(MessageFormat.format(line,
                                      rb.getString("TIME_MOVING"),
                                      TimeUnit.formatHundredSecondeTime(dataRun
                                          .computeTimeTot()
                                                                        - dataRun
                                                                            .computeTimePauseTot())));
      writer.write("\r\n");
    }
  }

  private void timePause(DataRun dataRun,
                         ResourceBundle rb,
                         BufferedWriter writer,
                         String line) throws IOException, SQLException {
    if (dataRun.computeTimePauseTot() > 0) {

      line = line.substring(TIME_PAUSE.length());
      writer.write(MessageFormat.format(line,
                                        rb.getString("TIME_PAUSE"),
                                        TimeUnit
                                            .formatHundredSecondeTime(dataRun
                                                .computeTimePauseTot())));
      writer.write("\r\n");
    }
  }

  private void avgAll(DataRun dataRun,
                      ResourceBundle rb,
                      BufferedWriter writer,
                      String line) throws IOException, SQLException {
    line = line.substring(AVG_ALL.length());
    writer.write(MessageFormat.format(line, rb.getString("AVG_ALL"), PaceUnit
        .computeFormatAllureWithUnit(dataRun.getComputeDistanceTot(),
                                     dataRun.computeTimeTot())));
    writer.write("\r\n");
  }

  private void avgSpeed(DataRun dataRun,
                        ResourceBundle rb,
                        BufferedWriter writer,
                        String line) throws IOException, SQLException {
    line = line.substring(AVG_SPEED.length());
    writer.write(MessageFormat.format(line,
                                      rb.getString("AVG_SPEED"),
                                      SpeedPaceUnit
                                          .computeFormatSpeedWithUnit(dataRun
                                              .getComputeDistanceTot(), dataRun
                                              .computeTimeTot())));
    writer.write("\r\n");
  }

  private void day(DataRun dataRun,
                   ResourceBundle rb,
                   BufferedWriter writer,
                   String line) throws IOException, SQLException {
    line = line.substring(DAY.length());
    String day = LanguageManager.getManager().getCurrentLang()
        .getDateFormatter().format(dataRun.getTime());
    String hour = new SimpleDateFormat("kk:mm:ss").format(dataRun.getTime());

    writer.write(MessageFormat.format(line, day, hour));
    writer.write("\r\n");
  }

  private void activity(DataRun dataRun,
                        ResourceBundle rb,
                        BufferedWriter writer,
                        String line) throws IOException {
    line = line.substring(ACTIVITY.length());
    try {
      String lib = UserActivityTableManager.getInstance()
          .retreive(dataRun.getSportType()).toString();
      if (lib != null && "".equals(lib)) {
        writer.write(MessageFormat.format(line, rb.getString("ACTIVITY"), lib));
      }
    }
    catch (Throwable e) {
    }
    writer.write("\r\n");
  }

  private void lap(DataRun dataRun,
                   ResourceBundle rb,
                   BufferedReader reader,
                   BufferedWriter writer) throws IOException {
    try {
      DataRunLap[] runLaps = RunLapTableManager.getInstance()
          .findLaps(dataRun.getId());

      String line = reader.readLine();
      if (runLaps.length <= 1) {
        while (!line.startsWith(END_TABLE_LAP)) {
          line = reader.readLine();
        }
        return;
      }

      while (!line.startsWith(END_TABLE_LAP)) {
        if (line.startsWith(LAP_TITLE)) {
          line = line.substring(LAP_TITLE.length());
          writer.write(MessageFormat.format(line, rb.getString("LAP_TITLE")));
          writer.write("\r\n");
        }
        else if (line.startsWith(LAP_HEAD)) {
          line = line.substring(LAP_HEAD.length());
          String time = rb.getString("LAP_TIME");
          String dist = rb.getString("LAP_DISTANCE");
          String allure = rb.getString("LAP_ALLURE");
          String speed = rb.getString("LAP_SPEED");
          writer.write(MessageFormat.format(line,
                                            time,
                                            dist,
                                            DistanceUnit.getDefaultUnit(),
                                            allure,
                                            PaceUnit.getDefaultUnit(),
                                            speed,
                                            SpeedUnit.getDefaultUnit()));
          writer.write("\r\n");
        }
        else if (line.startsWith(LAP_BODY)) {
          line = line.substring(LAP_BODY.length());
          for (DataRunLap lap : runLaps) {
            if (!DistanceUnit.isUnitKm(DistanceUnit.getDefaultUnit())) {
              lap.setTotalDist((float) DistanceUnit.convert(DistanceUnit
                  .unitKm(), DistanceUnit.getDefaultUnit(), lap.getTotalDist()));
            }

            String dist = DistanceUnit.formatMetersInKm(lap.getTotalDist());
            String time = TimeUnit.formatHundredSecondeTime(lap
                .getMovingTotalTime());
            String speed = SpeedPaceUnit
                .computeFormatSpeed(lap.getTotalDist(),
                                    lap.getMovingTotalTime());
            String allure = PaceUnit.computeAllure(lap.getTotalDist(),
                                                   lap.getRealTotalTime());
            String lineLap = new String(line);
            writer.write(MessageFormat.format(lineLap,
                                              time,
                                              dist,
                                              allure,
                                              speed));
            writer.write("\r\n");
          }
        }
        else {
          writer.write(line);
          writer.write("\r\n");
        }
        line = reader.readLine();
      }
    }
    catch (SQLException e) {
      log.error("", e);
    }

    writer.write("\r\n");
  }

  private void calories(DataRun dataRun,
                        ResourceBundle rb,
                        BufferedWriter writer,
                        String line) throws IOException, SQLException {
    int value = RunLapTableManager.getInstance()
        .computeCalories(dataRun.getId());
    if (value > 0) {
      line = line.substring(CALORIES.length());
      writer.write(MessageFormat.format(line,
                                        rb.getString("CALORIES"),
                                        Integer.toString(value)));
    }
    writer.write("\r\n");
  }

  private void cardio(DataRun dataRun,
                      ResourceBundle rb,
                      BufferedWriter writer,
                      String line) throws IOException, SQLException {
    int avg = dataRun.computeAvgRate();
    if (avg > 0) {
      int min = dataRun.computeMinRate();
      int max = dataRun.computeMaxRate();
      line = line.substring(CARDIO.length());
      writer.write(MessageFormat.format(line,
                                        rb.getString("CARDIO"),
                                        Integer.toString(avg),
                                        Integer.toString(min),
                                        Integer.toString(max)));
    }
    writer.write("\r\n");
  }

  private void distance(DataRun dataRun,
                        ResourceBundle rb,
                        BufferedWriter writer,
                        String line) throws IOException, SQLException {
    line = line.substring(DISTANCE.length());

    writer.write(MessageFormat.format(line,
                                      rb.getString("DISTANCE"),
                                      DistanceUnit.formatWithUnit(dataRun
                                          .getComputeDistanceTot())));
    writer.write("\r\n");
  }

  private void markerStop(BufferedWriter writer, String line, IGeoPosition mStop) throws IOException {
    if (mStop != null) {
      line = line.substring(MARKER_END.length());
      writer.write(MessageFormat.format(line,
                                        Double.toString(mStop.getLatitude()),
                                        Double.toString(mStop.getLongitude())));
    }
    writer.write("\r\n");
  }

  private void markerStart(BufferedWriter writer,
                           String line,
                           IGeoPosition mStart) throws IOException {
    if (mStart != null) {
      line = line.substring(MARKER_START.length());
      writer
          .write(MessageFormat.format(line,
                                      Double.toString(mStart.getLatitude()),
                                      Double.toString(mStart.getLongitude())));
    }
    writer.write("\r\n");
  }

  private void mapCoordinate(List<DataRunTrk> trks,
                             BufferedWriter writer,
                             String line) throws IOException {
    line = line.substring(MAP_COORDINATE.length());
    for (int i = 0; i < trks.size() - 1; i++) {
      addPoint(trks.get(i), writer, line);
      writer.write(",\r\n");
    }
    addPoint(trks.get(trks.size() - 1), writer, line);
    writer.write("\r\n");
  }

  private void addPoint(DataRunTrk p, BufferedWriter writer, String line) throws IOException {
    if (p.isValidGps()) {
      double latitude = GeoUtil.makeLatitudeFromGarmin(p.getLatitude());
      double longitude = GeoUtil.makeLatitudeFromGarmin(p.getLongitude());
      if (log.isDebugEnabled()) {
        log.debug("lat,long=" + latitude + "," + longitude);
      }
      writer.write(MessageFormat.format(line,
                                        Double.toString(latitude),
                                        Double.toString(longitude)));
    }
  }

  private void mapBounds(BufferedWriter writer,
                         String line,
                         IGeoPosition sw,
                         IGeoPosition ne) throws IOException {
    if (log.isDebugEnabled()) {
      log.debug("sw.getLatitude()=" + sw.getLatitude());
      log.debug("sw.getLongitude()=" + sw.getLongitude());
      log.debug("ne.getLatitude()=" + ne.getLatitude());
      log.debug("ne.getLongitude()=" + ne.getLongitude());
    }
    line = line.substring(MAP_BOUNDS.length());
    writer.write(MessageFormat.format(line,
                                      Double.toString(ne.getLatitude()),
                                      Double.toString(ne.getLongitude()),
                                      Double.toString(sw.getLatitude()),
                                      Double.toString(sw.getLongitude())));
    writer.write("\r\n");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertRun#convert(fr.turtlesport.db.DataRun)
   */
  public File convert(DataRun data) throws GeoConvertException, SQLException {
    if (data == null) {
      throw new IllegalArgumentException();
    }

    // construction du nom du fichier
    String name = "map-"
                  + LanguageManager.getManager().getCurrentLang()
                      .getDateTimeFormatterWithoutSep().format(data.getTime())
                  + ".html";
    File file = new File(Location.googleEarthLocation(), name);

    // conversion
    return convert(data, file);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFileDesc#description()
   */
  public String description() {
    return "google map";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFileDesc#extension()
   */
  public String[] extension() {
    return EXT;
  }

  private IGeoPosition markerStart(List<DataRunTrk> list) {
    for (DataRunTrk g : list) {
      if (g.isValidGps()) {
        return new GeoPosition(GeoUtil.makeLatitudeFromGarmin(g.getLatitude()),
                               GeoUtil.makeLatitudeFromGarmin(g.getLongitude()));
      }
    }
    return null;
  }

  private IGeoPosition markerEnd(List<DataRunTrk> list) {
    GeoPosition gStop = null;
    for (int i = list.size() - 1; i > 1; i--) {
      DataRunTrk g = list.get(i);
      if (g.isValidGps()) {
        gStop = new GeoPosition(GeoUtil.makeLatitudeFromGarmin(g.getLatitude()),
                                GeoUtil
                                    .makeLatitudeFromGarmin(g.getLongitude()));
        break;
      }
    }
    return gStop;
  }

}
