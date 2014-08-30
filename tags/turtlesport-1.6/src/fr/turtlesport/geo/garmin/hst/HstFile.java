package fr.turtlesport.geo.garmin.hst;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import fr.turtlesport.IProductDevice;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.GeoConvertException;
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
 * Training Center Database v1.
 * 
 * @author Denis Apparicio
 * 
 */
public class HstFile implements IGeoFile, IGeoConvertRun {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(HstFile.class);
  }

  /** Extensions. */
  public static final String[] EXT = { "hst" };

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeo#description()
   */
  public String description() {
    return "GarminTrainingCenter Database v1 (*.hst)";
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
  public File convert(DataRun data, File file) throws GeoConvertException,
                                              SQLException {
    if (data == null) {
      throw new IllegalArgumentException("dataRun est null");
    }
    if (file == null) {
      throw new IllegalArgumentException("file est null");
    }

    // Recuperation des points des tours intermediaires.
    DataRunLap[] laps = RunLapTableManager.getInstance().findLaps(data.getId());
    if (laps != null && laps.length < 1) {
      return null;
    }

    long startTime = System.currentTimeMillis();

    BufferedWriter writer = null;
    boolean isError = false;
    try {
      writer = new BufferedWriter(new FileWriter(file));
      SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

      // begin
      writeBegin(writer, data);

      // Ecriture des tours intermediaires.
      for (DataRunLap l : laps) {
        writeLap(writer, data, l, timeFormat);
      }

      // end
      writeEnd(writer, data);
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
    log.info("Temps pour ecrire gpx : " + (endTime - startTime) + " ms");

    log.debug("<<convert");
    return file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoConvertRun#convert(fr.turtlesport.db.DataRun)
   */
  public File convert(DataRun data) throws GeoConvertException, SQLException {
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

    HstHandler handler = null;
    try {
      // Validation schema
      SchemaFactory factory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      StreamSource ss = new StreamSource(getClass()
          .getResourceAsStream("TrainingCenterDatabasev1.xsd"));
      Schema schema = factory.newSchema(ss);
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(file));

      // Recuperation du parser
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);

      // parsing
      SAXParser parser = spf.newSAXParser();
      handler = new HstHandler();
      parser.parse(fis, handler);

      // construction de la reponse
      ArrayList<IGeoRoute> list = new ArrayList<IGeoRoute>();
      if (handler.running != null) {
        for (Run run : handler.running.getRuns()) {
          list.add(new RunGeoRoute(run, IGeoRoute.SPORT_TYPE_RUNNING));
        }
      }
      if (handler.biking != null) {
        for (Run run : handler.biking.getRuns()) {
          list.add(new RunGeoRoute(run, IGeoRoute.SPORT_TYPE_BIKE));
        }
      }
      if (handler.other != null) {
        for (Run run : handler.other.getRuns()) {
          list.add(new RunGeoRoute(run, IGeoRoute.SPORT_TYPE_OTHER));
        }
      }
      rep = new IGeoRoute[list.size()];
      if (list.size() > 0) {
        list.toArray(rep);
      }
    }
    catch (Exception e) {
      log.error("", e);
      throw new GeoLoadException(e);
    }

    log.debug("<<load");
    return rep;
  }

  private void writeBegin(BufferedWriter writer, DataRun data) throws IOException {
    writer
        .write("<TrainingCenterDatabase xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v1\">");
    writeln(writer);
    writer.write("<History>");
    writeln(writer);

    if (data.isSportRunning()) {
      writer.write("<Running Name=\"Running\">");
    }
    else if (data.isSportBike()) {
      writer.write("<Biking Name=\"Biking\">");
    }
    else {
      writer.write("<Other Name=\"Other\">");
    }
    writeln(writer);
    writer.write("<Run>");
  }

  private void writeEnd(BufferedWriter writer, DataRun data) throws IOException {
    writeln(writer);
    writer.write("</Run>");
    if (data.isSportRunning()) {
      writer.write("</Running>");
      writeln(writer);
      writer.write("<Biking Name=\"Biking\"></Biking>");
      writeln(writer);
      writer.write("<Other Name=\"Other\"/>");
    }
    else if (data.isSportBike()) {
      writer.write("</Biking>");
      writeln(writer);
      writer.write("<Running Name=\"Running\"/>");
      writeln(writer);
      writer.write("<Other Name=\"Other\"/>");
    }
    else {
      writer.write("</Other>");
      writeln(writer);
      writer.write("<Running Name=\"Running\"/>");
      writeln(writer);
      writer.write("<Biking Name=\"Biking\"/>");
    }
    writeln(writer);
    writer.write("<MultiSport Name=\"MultiSport\"/>");
    writeln(writer);
    writer.write("</History>");
    writeln(writer);
    writer.write("</TrainingCenterDatabase>");
  }

  private void writeLap(BufferedWriter writer,
                        DataRun data,
                        DataRunLap l,
                        SimpleDateFormat timeFormat) throws IOException,
                                                    SQLException {

    // recuperation des points du tour
    Date dateEnd = new Date(l.getStartTime().getTime() + l.getRealTotalTime() * 10);
    DataRunTrk[] trks = RunTrkTableManager.getInstance()
        .getTrks(data.getId(), l.getStartTime(), dateEnd);

    if (!hasValidpoints(trks)) {
      log.warn("pas de points pour ce tour");
      return;
    }

    // Ecriture
    writeln(writer);
    writer.write("<Lap StartTime=\"" + timeFormat.format(l.getStartTime())
                 + "\">");
    // TotalTimeSeconds
    double totalTime = l.getRealTotalTime() / 100.0;
    writer.write("<TotalTimeSeconds>" + totalTime + "</TotalTimeSeconds>");
    // DistanceMeters
    writeln(writer);
    writer.write("<DistanceMeters>" + l.getTotalDist() + "</DistanceMeters>");
    // MaximumSpeed
    writeln(writer);
    writer.write("<MaximumSpeed>" + l.getMaxSpeed() + "</MaximumSpeed>");
    // Calories
    writeln(writer);
    writer.write("<Calories>" + l.getCalories() + "</Calories>");
    // AverageHeartRateBpm
    if (l.getAvgHeartRate() > 0) {
      writer.write("<AverageHeartRateBpm>" + l.getAvgHeartRate()
                   + "</AverageHeartRateBpm>");
    }
    // MaximumHeartRateBpm
    if (l.getMaxHeartRate() > 0) {
      writer.write("<MaximumHeartRateBpm>" + l.getMaxHeartRate()
                   + "</MaximumHeartRateBpm>");
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
      writeTrkPoint(writer, t, timeFormat);
    }

    writeln(writer);
    writer.write("</Track>");
    writeln(writer);
    writer.write("</Lap>");
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

  private void writeTrkPoint(BufferedWriter writer,
                             DataRunTrk point,
                             SimpleDateFormat timeFormat) throws IOException {
    writer.write("<Trackpoint>");

    // Time
    writer.write("<Time>" + timeFormat.format(point.getTime()) + "</Time>");

    // position
    if (point.isValidGps()) {
      double latitude = GeoUtil.makeLatitudeFromGarmin(point.getLatitude());
      double longitude = GeoUtil.makeLatitudeFromGarmin(point.getLongitude());
      writer.write("<Position>");
      writer.write("<LatitudeDegrees>" + latitude + "</LatitudeDegrees>");
      writer.write("<LongitudeDegrees>" + longitude + "</LongitudeDegrees>");
      writer.write("</Position>");
      // Altitude
      writer.write("<AltitudeMeters>" + point.getAltitude()
                   + "</AltitudeMeters>");
      // DistanceMeters
      writer.write("<DistanceMeters>" + point.getDistance()
                   + "</DistanceMeters>");
      // HeartRateBpm
      if (point.getHeartRate() > 0) {
        writer.write("<HeartRateBpm>" + point.getHeartRate()
                     + "</HeartRateBpm>");
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

  private void writeln(BufferedWriter writer) throws IOException {
    writer.write("\n");
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class HstHandler extends DefaultHandler {
    private StringBuffer      stBuffer;

    private History           history;

    private boolean           isHistory             = false;

    private HistoryFolder     running;

    private boolean           isRunning             = false;

    private HistoryFolder     biking;

    private boolean           isBiking              = false;

    private HistoryFolder     other;

    private boolean           isOther               = false;

    private MultiSport        multiSport;

    private boolean           isMultiSport          = false;

    private MultiSportSession currentMultiSportSession;

    private boolean           isMultiSportSession   = false;

    private Run               currentRun;

    private boolean           isRun                 = false;

    private Lap               currentLap;

    private boolean           isLap                 = false;

    private boolean           isAverageHeartRateBpm = false;

    private boolean           isHeartRateBpm        = false;

    private boolean           isMaximumHeartRateBpm = false;

    private boolean           isTrack               = false;

    private Track             currentTrack;

    private boolean           isTrackpoint          = false;

    private TrackPoint        currentTrackPoint;

    private boolean           isPosition            = false;

    private Position          currentPosition;

    /**
     * 
     */
    public HstHandler() {
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

      // History
      if (localName.equals("History")) {
        isHistory = true;
        history = new History();
      }
      // Running
      else if (localName.equals("Running") && isHistory) {
        isRunning = true;
        running = new HistoryFolder(attrs.getValue("Name"));
      }
      // Biking
      else if (localName.equals("Biking") && isHistory) {
        isBiking = true;
        biking = new HistoryFolder(attrs.getValue("Name"));
      }
      // Other
      else if (localName.equals("Other") && isHistory) {
        isOther = true;
        other = new HistoryFolder(attrs.getValue("Name"));
      }
      // MultiSport
      else if (localName.equals("MultiSport") && isHistory) {
        isMultiSport = true;
        multiSport = new MultiSport(attrs.getValue("Name"));
      }
      // MultiSportSession
      else if (localName.equals("MultiSportSession") && isMultiSport) {
        isMultiSportSession = true;
        currentMultiSportSession = new MultiSportSession();
      }
      // FisrtSport ou NextSport
      else if (localName.equals("FisrtSport") || qName.equals("NextSport")
               && isMultiSport) {
        isMultiSportSession = true;
        currentMultiSportSession = new MultiSportSession();
      }
      // Run
      else if (localName.equals("Run") && (isRunning || isBiking || isOther)) {
        isRun = true;
        currentRun = new Run();
      }
      // Lap
      else if (qName.equals("Lap") && isRun) {
        currentLap = new Lap(XmlUtil.getTime(attrs.getValue("StartTime")),
                             currentRun.getLapSize());
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

      if (localName.equals("TotalTimeSeconds") && isLap) { // TotalTimeSeconds
        // Lap
        currentLap.setTotalTime((long) (1000*new Double(stBuffer.toString())));
      }
      else if (localName.equals("Time") && isTrackpoint) { // Time
        // TrackPoint
        currentTrackPoint.setDate(XmlUtil.getTime(stBuffer.toString()));
      }
      else if (localName.equals("AltitudeMeters") && isTrackpoint) { // AltitudeMeters
        // TrackPoint
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
      else if (localName.equals("HeartRateBpm") && isTrackpoint) { // AltitudeMeters
        // TrackPoint
        currentTrackPoint.setHeartRate(new Integer(stBuffer.toString()));
      }
      else if (localName.equals("MaximumSpeed") && isLap) { // MaximumSpeed
        // Lap
        currentLap.setMaxSpeed(new Double(stBuffer.toString()));
      }
      else if (localName.equals("Calories")) { // Calories
        // Lap
        currentLap.setCalories(new Integer(stBuffer.toString()));
      }
      else if (localName.equals("Value")) { // Value
        if (isAverageHeartRateBpm) {
          // AverageHeartRateBpm -> Value
          currentLap.setAverageHeartRateBpm(new Integer(stBuffer.toString()));
        }
        else if (isMaximumHeartRateBpm) {
          // MaximumHeartRateBpm -> Value
          currentLap.setMaximumHeartRateBpm(new Integer(stBuffer.toString()));
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

      // History
      // ------------
      if (localName.equals("History")) {
        isHistory = false;
      }

      // Running
      // ------------
      if (localName.equals("Running") && isHistory) {
        isRunning = false;
        if (running.getRunSize() > 0) {
          history.setRunning(running);
        }
      }

      // Biking
      // ------------
      if (localName.equals("Biking") && isHistory) {
        isBiking = false;
        if (biking.getRunSize() > 0) {
          history.setBiking(biking);
        }
      }

      // Other
      // ------------
      if (localName.equals("Other") && isHistory) {
        isOther = false;
        if (other.getRunSize() > 0) {
          history.setOther(other);
        }
      }

      // Run
      // ------------
      if (localName.equals("Run") && isRun) {
        isRun = false;
        if (isRunning) {
          running.addRun(currentRun);
        }
        else if (isBiking) {
          biking.addRun(currentRun);
        }
        else if (isOther) {
          other.addRun(currentRun);
        }
      }

      // Lap
      // ------------
      if (localName.equals("Lap") && isLap) {
        isLap = false;
        currentRun.addLap(currentLap);
      }

      // Track
      // ------------
      if (localName.equals("Track") && isTrack) {
        isTrack = false;
        currentLap.addTrack(currentTrack);
      }

      // Trackpoint
      // ------------
      if (localName.equals("Trackpoint") && isTrackpoint) {
        isTrackpoint = false;
        currentTrack.addPoint(currentTrackPoint);
        log.info(currentTrackPoint);
      }

      // Position
      // ------------
      if (localName.equals("Position") && isPosition) {
        isPosition = false;
        currentTrackPoint.setPosition(currentPosition);
      }

      // AverageHeartRateBpm
      // ----------------------
      if (localName.equals("AverageHeartRateBpm")) {
        isAverageHeartRateBpm = false;
      }

      // MaximumHeartRateBpm
      // ----------------------
      if (localName.equals("MaximumHeartRateBpm")) {
        isMaximumHeartRateBpm = false;
      }

      // HeartRateBpm
      // ----------------------
      if (localName.equals("HeartRateBpm")) {
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
   * @author Denis Apparicio
   * 
   */
  private class RunGeoRoute extends AbstractGeoRoute {
    private Run    run;

    private double distanceTot = 0;

    /**
     * @param run
     * @param sportType
     */
    public RunGeoRoute(Run run, int sportType) {
      this.run = run;
      setSportType(sportType);

      // Distance et temps totale
      long timeTot = 0;
      for (Lap lap : run.getLaps()) {
        distanceTot += lap.getDistanceMeters();
        timeTot += lap.getTotalTime();
      }
      initTimeTot(timeTot);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#getStartTime()
     */
    @Override
    public Date getStartTime() {
      return run.getLap(0).getStartTime();
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
     * @see fr.turtlesport.geo.IGeoRoute#getName()
     */
    public String getName() {
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getAllPoints()
     */
    public List<IGeoPositionWithAlt> getAllPoints() {
      ArrayList<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
      for (Lap lap : run.getLaps()) {
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
     * @see fr.turtlesport.geo.IGeoRoute#getSegment(int)
     */
    public IGeoSegment getSegment(int index) {
      return run.getLap(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
     */
    public int getSegmentSize() {
      return run.getLapSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegments()
     */
    public List<IGeoSegment> getSegments() {
      List<IGeoSegment> list = new ArrayList<IGeoSegment>();
      if (run.getLaps() != null) {
        for (Lap l : run.getLaps()) {
          list.add(l);
        }
      }
      return list;
    }

    @Override
    public IProductDevice getProductDevice() {
      return null;
    }

  }

}
