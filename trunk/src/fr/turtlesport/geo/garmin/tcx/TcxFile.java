package fr.turtlesport.geo.garmin.tcx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.geo.GeoConvertProgressAdaptor;
import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.IGeoConvertProgress;
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.geo.IGeoFile;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoRoute;
import fr.turtlesport.geo.IGeoSegment;
import fr.turtlesport.geo.garmin.Lap;
import fr.turtlesport.geo.garmin.Position;
import fr.turtlesport.geo.garmin.Track;
import fr.turtlesport.geo.garmin.TrackPoint;
import fr.turtlesport.geo.gpx.GpxGeoConvertException;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.GeoUtil;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.XmlUtil;

/**
 * Training Center Database v2.
 * 
 * @author Denis Apparicio
 * 
 */
public class TcxFile implements IGeoFile, IGeoConvertRun {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(TcxFile.class);
  }

  /** Extensions. */
  public static final String[] EXT = { "tcx" };

  private DecimalFormat        formatDec;

  private SimpleDateFormat     timeFormat;

  /**
   * 
   */
  public TcxFile() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeo#description()
   */
  public String description() {
    return "Garmin Training Center Database v2 (*.tcx)";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeo#extension()
   */
  public String[] extension() {
    return EXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertRun#convert(java.util.List,
   * java.io.File)
   */
  public File convert(final List<DataRun> runs,
                      IGeoConvertProgress progress,
                      File file) throws GeoConvertException, SQLException {
    if (runs == null || runs.size() == 0) {
      return null;
    }
    if (file == null) {
      throw new IllegalArgumentException("file est null");
    }
    if (progress == null) {
      progress = new GeoConvertProgressAdaptor();
    }

    long startTime = -1;
    if (log.isInfoEnabled()) {
      startTime = System.currentTimeMillis();
    }

    timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    // begin
    int size = runs.size();
    progress.begin(runs.size());

    BufferedWriter writer = null;
    boolean isError = true;
    try {
      writer = new BufferedWriter(new FileWriter(file));

      // begin
      writeBegin(writer);
      writeln(writer);

      // Folder
      boolean hasRun = writeFolders(runs, writer);
      if (hasRun) {
        writeln(writer);

        // Tag Activities
        writer.write("<Activities>");

        for (int index = 0; index < size; index++) {
          if (progress.cancel()) {
            return null;
          }
          // progression -> run traite
          progress.convert(index, size);

          DataRun data = runs.get(index);
          if (checkRun(data)) {
            // Recuperation des laps.
            DataRunLap[] laps = RunLapTableManager.getInstance()
                .findLaps(data.getId());

            // Ecriture de l activite
            writeActivity(data, laps, writer);
          }
        }

        // Tag Fin Activities
        writer.write("</Activities>");
        writeln(writer);

        // End
        writeEnd(writer);
        isError = false;
      }
    }
    catch (IOException e) {
      log.error("", e);
      throw new GpxGeoConvertException(e);
    }
    finally {
      if (writer != null) {
        try {
          writer.close();
        }
        catch (IOException e) {
          log.error("", e);
        }
      }
      if (isError) {
        file.delete();
      }
    }

    if (log.isInfoEnabled()) {
      long endTime = System.currentTimeMillis();
      log.info("Temps pour ecrire : " + (endTime - startTime) + " ms");
    }

    log.debug("<<convert");
    return (isError) ? null : file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertRun#convert(fr.turtlesport.db.DataRun,
   * java.io.File)
   */
  public File convert(DataRun data, File file) throws GeoConvertException,
                                              SQLException {
    log.debug(">>convert");

    if (data == null) {
      throw new IllegalArgumentException("dataRun est null");
    }
    if (file == null) {
      throw new IllegalArgumentException("file est null");
    }

    timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    // Recuperation des points des tours intermediaires.
    DataRunLap[] laps = RunLapTableManager.getInstance().findLaps(data.getId());
    if (laps == null || laps.length == 0) {
      return null;
    }

    long startTime = System.currentTimeMillis();

    BufferedWriter writer = null;
    boolean isError = false;
    try {
      writer = new BufferedWriter(new FileWriter(file));

      // begin
      writeBegin(writer);
      writeln(writer);

      // Tag Activities
      writer.write("<Activities>");

      // Ecriture de l activite
      writeActivity(data, laps, writer);

      // Tag Fin Activities
      writer.write("</Activities>");
      writeln(writer);

      // End
      writeEnd(writer);
    }
    catch (IOException e) {
      log.error("", e);
      isError = true;
      throw new GpxGeoConvertException(e);
    }
    catch (SQLException e) {
      log.error("", e);
      isError = true;
      throw e;
    }
    finally {
      if (writer != null) {
        try {
          writer.close();
        }
        catch (IOException e) {
          log.error("", e);
        }
        if (isError) {
          file.delete();
        }
      }
    }

    long endTime = System.currentTimeMillis();
    log.info("Temps pour ecrire : " + (endTime - startTime) + " ms");

    log.debug("<<convert");
    return file;
  }

  private DecimalFormat getDecimalFormat() {
    if (formatDec == null) {
      DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);
      dfs.setDecimalSeparator('.');
      formatDec = new DecimalFormat("#.#######", dfs);
    }
    return formatDec;
  }

  private boolean writeFolders(List<DataRun> runs, BufferedWriter writer) throws IOException,
                                                                         SQLException {

    boolean hasRun = false;

    writer.write("<Folders>");
    writeln(writer);
    writer.write("<History>");
    writeln(writer);
    writer.write("<Running Name=\"Running\">");
    for (DataRun data : runs) {
      if (data.isSportRunning() && checkRun(data)) {
        hasRun = true;
        writeActivityRef(data, writer);
      }
    }
    writeln(writer);
    writer.write("</Running>");
    writeln(writer);

    writer.write("<Biking Name=\"Biking\">");
    for (DataRun data : runs) {
      if (data.isSportBike() && checkRun(data)) {
        hasRun = true;
        writeActivityRef(data, writer);
      }
    }
    writeln(writer);
    writer.write("</Biking>");
    writeln(writer);

    writer.write("<Other Name=\"Other\">");
    for (DataRun data : runs) {
      if (data.isSportOther() && checkRun(data)) {
        hasRun = true;
        writeActivityRef(data, writer);
      }
    }
    writeln(writer);
    writer.write("</Other>");
    writeln(writer);

    writer.write("<MultiSport Name=\"MultiSport\"/>");
    writeln(writer);

    writer.write("</History>");
    writeln(writer);
    writer.write("</Folders>");

    return hasRun;
  }

  private void writeActivityRef(DataRun data, BufferedWriter writer) throws IOException {
    writeln(writer);
    writer.write("<ActivityRef>");
    writer.write("<Id>");
    writer.write(timeFormat.format(data.getTime()));
    writer.write("</Id>");
    writer.write("</ActivityRef>");
  }

  private void writeActivity(DataRun data,
                             DataRunLap[] laps,
                             BufferedWriter writer) throws IOException,
                                                   SQLException {
    if (laps == null || laps.length == 0) {
      return;
    }

    // Activity
    writeln(writer);
    writer.write("<Activity ");
    writeSportType(writer, data);
    writer.write(">");
    writeln(writer);
    writer.write("<Id>");
    writer.write(timeFormat.format(data.getTime()));

    // writer.write(getTimeFormat().format(data.getTime()));
    writer.write("</Id>");
    writeln(writer);

    // Ecriture des tours intermediaires.
    for (DataRunLap l : laps) {
      writeLap(writer, data, l);
    }

    // end
    writer.write("</Activity>");
    writeln(writer);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertRun#convert(fr.turtlesport.db.DataRun)
   */
  public File convert(DataRun data) throws GeoConvertException, SQLException {
    // construction du nom du fichier

    // construction du nom du fichier
    String name = LanguageManager.getManager().getCurrentLang()
        .getDateTimeFormatterWithoutSep().format(data.getTime())
                  + EXT[0];
    File file = new File(Location.googleEarthLocation(), name);

    // conversion
    convert(data, file);
    return file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFile#load(java.io.File)
   */
  public IGeoRoute[] load(File file) throws GeoLoadException,
                                    FileNotFoundException {
    log.debug(">>load");

    IGeoRoute[] rep;

    // Lecture
    FileInputStream fis = new FileInputStream(file);

    TcxHandler handler = null;
    try {
      // Validation schema
      SchemaFactory factory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      StreamSource ss = new StreamSource(getClass()
          .getResourceAsStream("TrainingCenterDatabasev2.xsd"));
      Schema schema = factory.newSchema(ss);
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(file));

      // Recuperation du parser
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);

      // parsing
      SAXParser parser = spf.newSAXParser();
      handler = new TcxHandler();
      parser.parse(fis, handler);

      // construction de la reponse
      if (handler.listActivity != null) {
        rep = new IGeoRoute[handler.listActivity.size()];
      }
      else {
        rep = new IGeoRoute[0];
      }

      // construction de la reponse
      ArrayList<IGeoRoute> list = new ArrayList<IGeoRoute>();
      if (handler.listActivity != null) {
        for (Activity actv : handler.listActivity) {
          list.add(new ActivityGeoRoute(actv));
        }
      }
      rep = new IGeoRoute[list.size()];
      if (list.size() > 0) {
        list.toArray(rep);
      }
    }
    catch (Throwable e) {
      log.error("", e);
      throw new GeoLoadException(e);
    }

    log.debug("<<load");
    return rep;
  }

  private void writeBegin(BufferedWriter writer) throws IOException {
    writer
        .write("<TrainingCenterDatabase xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd\">");
  }

  private void writeln(BufferedWriter writer) throws IOException {
    writer.write("\n");
  }

  private void writeSportType(BufferedWriter writer, DataRun data) throws IOException {
    writer.write("Sport=\"");
    if (data.isSportRunning()) {
      writer.write("Running");
    }
    else if (data.isSportBike()) {
      writer.write("Biking");
    }
    else {
      writer.write("Other");
    }
    writer.write("\"");
  }

  private void writeEnd(BufferedWriter writer) throws IOException {
    // writer.write("<Author xsi:type=\"Application_t\">");
    // writer.write("<Name>Turtle Sport</Name>");
    // writer.write("<Build>");
    // writer.write("<Version>");
    // writer.write("<VersionMajor>" + Version.VERSION_MAJOR +
    // "</VersionMajor>");
    // writer.write("<VersionMinor>" + Version.VERSION_MINOR +
    // "</VersionMinor>");
    // writer.write("</Version>");
    // writer.write("<Type>Release</Type>");
    // writer.write("</Build>");
    // writer.write("<LangID>FR</LangID>");
    // writer.write("<PartNumber>0</PartNumber>");
    // writer.write("</Author>");
    writeln(writer);
    writer.write("</TrainingCenterDatabase>");
  }

  private void writeTrkPoint(BufferedWriter writer, DataRunTrk point) throws IOException {
    writer.write("<Trackpoint>");

    // Time
    writer.write("<Time>" + timeFormat.format(point.getTime()) + "</Time>");

    if (point.isValidGps()) {
      // position
      double latitude = GeoUtil.makeLatitudeFromGarmin(point.getLatitude());
      double longitude = GeoUtil.makeLatitudeFromGarmin(point.getLongitude());
      writer.write("<Position>");
      writer.write("<LatitudeDegrees>" + getDecimalFormat().format(latitude)
                   + "</LatitudeDegrees>");
      writer.write("<LongitudeDegrees>" + getDecimalFormat().format(longitude)
                   + "</LongitudeDegrees>");
      writer.write("</Position>");
      // Altitude
      if (point.isValidAltitude()) {
        writer.write("<AltitudeMeters>"
                     + getDecimalFormat().format(point.getAltitude())
                     + "</AltitudeMeters>");
      }
      // DistanceMeters
      writer.write("<DistanceMeters>"
                   + getDecimalFormat().format(point.getDistance())
                   + "</DistanceMeters>");
      // HeartRateBpm
      if (point.getHeartRate() > 0) {
        writer.write("<HeartRateBpm xsi:type=\"HeartRateInBeatsPerMinute_t\">");
        writer.write("<Value>" + point.getHeartRate() + "</Value>");
        writer.write("</HeartRateBpm>");
        writer.write("<SensorState>Present</SensorState>");
      }
      else {
        writer.write("<SensorState>Absent</SensorState>");
      }
      // Cadence
      if (point.isValidCadence()) {
        writer.write("<Cadence>" + point.getCadence() + "</Cadence>");
      }
    }
    
    writer.write("</Trackpoint>");
  }

  private void writeLap(BufferedWriter writer, DataRun data, DataRunLap l) throws IOException,
                                                                          SQLException {
    log.debug(">>writeLap");

    // recuperation des points du tour
    Date dateEnd = new Date(l.getStartTime().getTime() + l.getTotalTime() * 10);
    DataRunTrk[] trks = RunTrkTableManager.getInstance()
        .getTrks(data.getId(), l.getStartTime(), dateEnd);

    if (!hasValidpoints(trks)) {
      log.warn("pas de points pour ce tour run : id " + data.getId() + " "
               + timeFormat.format(data.getTime()));
      return;
    }

    // Ecriture
    String startTime = timeFormat.format(l.getStartTime());
    if (log.isDebugEnabled()) {
      log.debug("Lap StartTime=" + startTime);
    }
    writer.write("<Lap StartTime=\"" + startTime + "\">");
    writeln(writer);

    // TotalTimeSeconds
    double totalTime = l.getTotalTime() / 100.0;
    writer.write("<TotalTimeSeconds>" + getDecimalFormat().format(totalTime)
                 + "</TotalTimeSeconds>");
    // DistanceMeters
    writeln(writer);
    writer.write("<DistanceMeters>"
                 + getDecimalFormat().format(l.getTotalDist())
                 + "</DistanceMeters>");
    // MaximumSpeed
    writeln(writer);
    writer.write("<MaximumSpeed>" + getDecimalFormat().format(l.getMaxSpeed())
                 + "</MaximumSpeed>");
    // Calories
    writeln(writer);
    writer.write("<Calories>" + l.getCalories() + "</Calories>");
    // AverageHeartRateBpm
    if (l.getAvgHeartRate() > 0) {
      writer.write("<AverageHeartRateBpm>");
      writer.write("<Value>" + l.getAvgHeartRate() + "</Value>");
      writer.write("</AverageHeartRateBpm>");
    }
    // MaximumHeartRateBpm
    if (l.getMaxHeartRate() > 0) {
      writer.write("<MaximumHeartRateBpm>");
      writer.write("<Value>" + l.getMaxHeartRate() + "</Value>");
      writer.write("</MaximumHeartRateBpm>");
    }
    // Intensity
    writeln(writer);
    writer.write("<Intensity>Active</Intensity>");
    // TriggerMethod
    writeln(writer);
    writer.write("<TriggerMethod>Manual</TriggerMethod>");

    // Track
    writeln(writer);
    writer.write("<Track>");
    for (DataRunTrk t : trks) {
      writeln(writer);
      writeTrkPoint(writer, t);
    }

    writeln(writer);
    writer.write("</Track>");

    writeln(writer);
    writer.write("</Lap>");
    writeln(writer);

    log.debug("<<writeLap");
  }

  private boolean hasValidpoints(DataRunTrk[] trks) {
    if (trks != null) {
      for (DataRunTrk t : trks) {
        if (t.isValidGps()) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean checkRun(DataRun run) throws SQLException {
    DataRunLap[] laps = RunLapTableManager.getInstance().findLaps(run.getId());
    if (laps == null || laps.length == 0) {
      if (run.getTime() == null) {
        log.warn("Run id=" + run.getId() + " pas de lap");

      }
      else {
        log.warn("Run id=" + run.getId() + " date="
                 + timeFormat.format(run.getTime()) + " pas de lap");

      }
      return false;
    }
    // verification date du lap
    for (DataRunLap l : laps) {
      if (l.getStartTime().before(run.getTime())) {
        log.warn("Run " + run.getId() + " : dateRun="
                 + timeFormat.format(run.getTime()) + " lap="
                 + timeFormat.format(l.getStartTime()));
        return false;
      }
    }

    return RunTrkTableManager.getInstance().hasTrks(run.getId());
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TcxHandler extends DefaultHandler {
    private StringBuffer        stBuffer;

    private ArrayList<Activity> listActivity;

    private Activity            currentActivity;

    private boolean             isActivity            = false;

    private Lap                 currentLap;

    private boolean             isLap                 = false;

    private boolean             isAverageHeartRateBpm = false;

    private boolean             isHeartRateBpm        = false;

    private boolean             isMaximumHeartRateBpm = false;

    private Track               currentTrack;

    private boolean             isTrack               = false;

    private TrackPoint          currentTrackPoint;

    private boolean             isTrackpoint          = false;

    private Position            currentPosition;

    private boolean             isPosition;

    /**
     * 
     */
    public TcxHandler() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri,
                             String localName,
                             String qName,
                             Attributes attrs) throws SAXParseException {
      if (log.isDebugEnabled()) {
        log.debug(">>startElement uri=" + uri + " localName=" + localName
                  + " qName=" + qName);
      }

      // Activities
      if (localName.equals("Activities")) {
        listActivity = new ArrayList<Activity>();
      }
      // Activity
      else if (localName.equals("Activity")) {
        // sport
        currentActivity = new Activity(attrs.getValue("Sport"));
        isActivity = true;
      }
      // Lap
      else if (localName.equals("Lap") && isActivity) {
        // sport
        currentLap = new Lap(XmlUtil.getTime(attrs.getValue("StartTime")),
                             currentActivity.getLapSize());
        isLap = true;
      }
      // AverageHeartRateBpm
      else if (localName.equals("AverageHeartRateBpm") && isLap) {
        isAverageHeartRateBpm = true;
      }
      // AverageHeartRateBpm
      else if (localName.equals("MaximumHeartRateBpm") && isLap) {
        isMaximumHeartRateBpm = true;
      }
      // HeartRateBpm
      else if (localName.equals("HeartRateBpm") && isTrackpoint) {
        isHeartRateBpm = true;
      }
      // Track
      else if (localName.equals("Track") && isLap) {
        isTrack = true;
        currentTrack = new Track();
      }
      // Trackpoint
      else if (localName.equals("Trackpoint") && isTrack) {
        isTrackpoint = true;
        currentTrackPoint = new TrackPoint();
      }
      // Position
      else if (localName.equals("Position") && isTrackpoint) {
        isPosition = true;
        currentPosition = new Position();
      }

      log.debug("<<startElement");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) {
      if (log.isDebugEnabled()) {
        log.debug(">>endElement uri=" + uri + " localName=" + localName
                  + " qName=" + qName);
      }

      if (localName.equals("id")) {
        // id
        if (isActivity) {
          currentActivity.setId(XmlUtil.getTime(stBuffer.toString()));
        }
      }
      else if (localName.equals("TotalTimeSeconds") && isLap) { // TotalTimeSeconds
        // isLap
        currentLap.setTotalTimeSeconds(new Double(stBuffer.toString()));
      }
      else if (localName.equals("Time") && isTrackpoint) { // Time
        // Trackpoint
        currentTrackPoint.setDate(XmlUtil.getTime(stBuffer.toString()));
      }
      else if (localName.equals("AltitudeMeters") && isTrackpoint) { // AltitudeMeters
        // Trackpoint
        currentTrackPoint.setElevation(new Double(stBuffer.toString()));
      }
      else if (localName.equals("DistanceMeters")) { // DistanceMeters
        if (isTrackpoint) {
          // TrackPoint
          currentTrackPoint.setDistanceMeters(new Double(stBuffer.toString()));
        }
        else if (isLap) {
          // Lap
          currentLap.setDistanceMeters(new Double(stBuffer.toString()));
        }
      }
      else if (localName.equals("SensorState") && isTrackpoint) { // SensorState
        // TrackPoint
        currentTrackPoint.setSensorState(stBuffer.toString());
      }
      else if (localName.equals("MaximumSpeed") && isLap) { // MaximumSpeed
        // Lap
        currentLap.setMaxSpeed(new Double(stBuffer.toString()));
      }
      else if (localName.equals("Calories") && isLap) { // Calories
        // Lap
        currentLap.setCalories(new Integer(stBuffer.toString()));
      }
      else if (localName.equals("Value")) { // Value
        if (isAverageHeartRateBpm) {
          // AverageHeartRateBpm -> Value
          currentLap.setAverageHeartRateBpm(new Integer(stBuffer.toString()));
        }
        else if (isMaximumHeartRateBpm) {
          // AverageHeartRateBpm -> Value
          currentLap.setMaximumHeartRateBpm(new Integer(stBuffer.toString()));
        }
        else if (isHeartRateBpm) {
          // HeartRateBpm -> Value
          currentTrackPoint.setHeartRate(new Integer(stBuffer.toString()));
        }
      }
      else if (localName.equals("Intensity") && isLap) {// Intensity
        // Lap
        currentLap.setIntensity(stBuffer.toString());
      }
      else if (localName.equals("Cadence")) {// Cadence
        if (isTrackpoint) {
          // TrackPoint
          currentTrackPoint.setCadence(new Integer(stBuffer.toString()));
        }
        else if (isLap) {
          // Lap
          currentLap.setCadence(new Integer(stBuffer.toString()));
        }
      }
      else if (localName.equals("TriggerMethod") && isLap) {// TriggerMethod
        // Lap
        currentLap.setTriggerMethod(stBuffer.toString());
      }
      else if (localName.equals("LatitudeDegrees") && isPosition) { // LatitudeDegrees
        // Position
        currentPosition.setLatitude(new Double(stBuffer.toString()));
      }
      else if (localName.equals("LongitudeDegrees") && isPosition) {// LongitudeDegrees
        // Position
        currentPosition.setLongitude(new Double(stBuffer.toString()));
      }
      stBuffer = null;

      // Activity
      // -------------
      if (localName.equals("Activity")) {
        listActivity.add(currentActivity);
        isActivity = false;
      }
      // Lap
      // ---------
      else if (localName.equals("Lap") && isLap) {
        isLap = false;
        currentActivity.addLap(currentLap);
      }
      // Track
      // ---------
      else if (localName.equals("Track") && isTrack) {
        isTrack = false;
        currentLap.addTrack(currentTrack);
      }
      // Trackpoint
      // ---------------
      else if (localName.equals("Trackpoint") && isTrackpoint) {
        isTrackpoint = false;
        currentTrack.addPoint(currentTrackPoint);
        log.info(currentTrackPoint);
      }

      // Position
      // -------------------
      else if (localName.equals("Position") && isPosition) {
        isPosition = false;
        currentTrackPoint.setPosition(currentPosition);
      }
      // AverageHeartRateBpm
      // ----------------------
      else if (localName.equals("AverageHeartRateBpm")) {
        isAverageHeartRateBpm = false;
      }
      // MaximumHeartRateBpm
      // ----------------------
      else if (localName.equals("MaximumHeartRateBpm")) {
        isMaximumHeartRateBpm = false;
      }
      // HeartRateBpm
      // ----------------------
      else if (localName.equals("HeartRateBpm")) {
        isHeartRateBpm = false;
      }

      log.debug("<<endElement");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
      String st = new String(ch, start, length).trim();
      log.debug(">>characters " + st);

      if (st.length() > 0) {
        if (stBuffer == null) {
          stBuffer = new StringBuffer(st);
        }
        else {
          stBuffer.append(st);
        }
      }

      log.debug("<<characters ");
    }
  }

  /**
   * Repr�sente une GEORoute pour une cativite.
   * 
   * @author Denis Apparicio
   * 
   */
  private class ActivityGeoRoute extends AbstractGeoRoute {
    private Activity activity;

    private double   distanceTot = 0;

    /**
     * @param activity
     */
    public ActivityGeoRoute(Activity activity) {
      this.activity = activity;
      if (activity.isRunning()) {
        setSportType(IGeoRoute.SPORT_TYPE_RUNNING);
      }
      else if (activity.isBiking()) {
        setSportType(IGeoRoute.SPORT_TYPE_BIKE);
      }
      else {
        setSportType(IGeoRoute.SPORT_TYPE_OTHER);
      }

      // Distance et temps totale
      double timeTot = 0;
      for (Lap lap : activity.getLaps()) {
        distanceTot += lap.getDistanceMeters();
        timeTot += lap.getTotalTimeSeconds();
      }
      initTimeTot((long) (timeTot * 1000));
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#distanceTot()
     */
    @Override
    public double distanceTot() {
      return distanceTot;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getAllPoints()
     */
    public List<IGeoPositionWithAlt> getAllPoints() {
      ArrayList<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
      for (Lap lap : activity.getLaps()) {
        for (Track trk : lap.getTracks()) {
          for (TrackPoint p : trk.getTrackPoints()) {
            list.add(p);
          }
        }
      }
      return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getName()
     */
    public String getName() {
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
     */
    public int getSegmentSize() {
      return activity.getLapSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#getStartTime()
     */
    @Override
    public Date getStartTime() {
      return activity.getLap(0).getStartTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegment(int)
     */
    public IGeoSegment getSegment(int index) {
      return activity.getLap(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegments()
     */
    public List<IGeoSegment> getSegments() {
      List<IGeoSegment> list = new ArrayList<IGeoSegment>();
      if (activity.getLaps() != null) {
        for (Lap l : activity.getLaps()) {
          list.add(l);
        }
      }
      return list;
    }

  }

}
