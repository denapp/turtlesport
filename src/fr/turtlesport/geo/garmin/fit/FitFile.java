package fr.turtlesport.geo.garmin.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.Decode;
import com.garmin.fit.EventMesg;
import com.garmin.fit.FileCreatorMesg;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.LapMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgListener;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.GeoPositionWithAlt;
import fr.turtlesport.geo.IGeoConvertProgress;
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.geo.IGeoFile;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoRoute;
import fr.turtlesport.geo.IGeoSegment;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class FitFile implements IGeoFile, IGeoConvertRun, MesgListener {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(FitFile.class);
  }

  /** Extensions. */
  public static final String[] EXT           = { "fit" };

  private List<ActivityMesg>   list          = new ArrayList<ActivityMesg>();

  private Session              session       = new Session();

  private FileIdMesg           fileId;

  private ActivityMesg         activityMsg;

  private int                  nbMsg         = 0;

  private static final double  CONST_CONVERT = 180 / Math.pow(2, 31);

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFileDesc#extension()
   */
  public String[] extension() {
    return EXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFileDesc#description()
   */
  public String description() {
    return "Garmin fit (*.fit)";
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
    throw new UnsupportedOperationException();
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
    IGeoRoute[] geos = null;

    // Lecture
    FileInputStream fis = new FileInputStream(file);

    try {
      Decode decode = new Decode();
      decode.read(fis, this);

      if (log.isDebugEnabled()) {
        log.debug("Nombre de msg : " + nbMsg);
        log.debug("Nombre de points : " + session.listRecord.size());
      }

      checkIsValid();

      geos = new IGeoRoute[1];
      geos[0] = new FitGeoRoute(session);
    }
    finally {
      try {
        fis.close();
      }
      catch (IOException e) {
      }
    }

    return geos;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.garmin.fit.MesgListener#onMesg(com.garmin.fit.Mesg)
   */
  public void onMesg(Mesg msg) {
    SimpleDateFormat dateFormat = null;
    if (log.isDebugEnabled()) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    nbMsg++;
    switch (msg.getNum()) {
      case MesgNum.FILE_ID:
        fileId = new FileIdMesg(msg);
        if (log.isDebugEnabled()) {
          log.debug("FILE_ID found GarminProduct=" + fileId.getGarminProduct()
                    + " " + fileId.getType());
        }
        if (log.isDebugEnabled()) {
          log.debug("-->FILE_ID");
          log.debug("   GarminProduct=" + fileId.getGarminProduct());
          log.debug("   Manufacturer=" + fileId.getManufacturer());
          log.debug("   Number=" + fileId.getNumber());
          log.debug("   SerialNumber=" + fileId.getSerialNumber());
          log.debug("   Type=" + fileId.getType());
          log.debug("-----------");
        }
        break;

      case MesgNum.FILE_CREATOR:
        if (log.isDebugEnabled()) {
          FileCreatorMesg fileCreator = new FileCreatorMesg(msg);

          log.debug("-->FILE_CREATOR");
          log.debug("   HardwareVersion=" + fileCreator.getHardwareVersion());
          log.debug("   SoftwareVersion=" + fileCreator.getSoftwareVersion());
          log.debug("-----------");
        }
        break;

      case MesgNum.ACTIVITY:
        activityMsg = new ActivityMesg(msg);
        list.add(activityMsg);
        if (log.isDebugEnabled()) {
          log.debug("-->ACTIVITY");
          log.debug("   Timestamp="
                    + dateFormat.format(activityMsg.getTimestamp().getDate()));
          log.debug("   LocalTimestamp=" + activityMsg.getLocalTimestamp());
          log.debug("   TotalTimerTime=" + activityMsg.getTotalTimerTime());
          log.debug("   NumSessions=" + activityMsg.getNumSessions());
          log.debug("   Type=" + activityMsg.getType());
          log.debug("   EventType=" + activityMsg.getEventType());
          log.debug("-----------");
        }
        break;

      case MesgNum.SESSION:
        SessionMesg sessionMesg = new SessionMesg(msg);
        addSession(sessionMesg);
        if (log.isDebugEnabled()) {
          log.debug("-->SESSION");
          log.debug("   Timestamp="
                    + dateFormat.format(sessionMesg.getTimestamp().getDate()));
          log.debug("   TotalDistance=" + sessionMesg.getTotalDistance());
          log.debug("   TotalElapsedTime=" + sessionMesg.getTotalElapsedTime());
          log.debug("   TotalCalories=" + sessionMesg.getTotalCalories());
          log.debug("   AvgSpeed=" + sessionMesg.getAvgSpeed());
          log.debug("   MaxSpeed=" + sessionMesg.getMaxSpeed());
          log.debug("   AvgHeartRate=" + sessionMesg.getAvgHeartRate());
          log.debug("   MaxHeartRate=" + sessionMesg.getMaxHeartRate());
          log.debug("   StartTime="
                    + dateFormat.format(sessionMesg.getStartTime().getDate()));
          log.debug("   StartPositionLat=" + sessionMesg.getStartPositionLat());
          log.debug("   StartPositionLong="
                    + sessionMesg.getStartPositionLong());
          log.debug("   Type=" + sessionMesg.getEventType());
          log.debug("   Event=" + sessionMesg.getEvent());
          log.debug("-----------");
        }
        break;

      case MesgNum.LAP:
        LapMesg lapMsg = new LapMesg(msg);
        addLap(lapMsg);
        if (log.isDebugEnabled()) {
          log.debug("-->LAP");
          if (lapMsg.getTimestamp() != null) {
            log.debug("   Timestamp="
                      + dateFormat.format(lapMsg.getTimestamp().getDate()));
          }
          log.debug("   TotalDistance=" + lapMsg.getTotalDistance());
          log.debug("   TotalElapsedTime=" + lapMsg.getTotalElapsedTime());
          log.debug("   TotalCalories=" + lapMsg.getTotalCalories());
          log.debug("   AvgSpeed=" + lapMsg.getAvgSpeed());
          log.debug("   MaxSpeed=" + lapMsg.getMaxSpeed());
          log.debug("   AvgHeartRate=" + lapMsg.getAvgHeartRate());
          log.debug("   MaxHeartRate=" + lapMsg.getMaxHeartRate());
          log.debug("   StartTime="
                    + dateFormat.format(lapMsg.getStartTime().getDate()));
          log.debug("   StartPositionLat=" + lapMsg.getStartPositionLat());
          log.debug("   StartPositionLong=" + lapMsg.getStartPositionLong());
          log.debug("   Type=" + lapMsg.getEventType());
          log.debug("   Event=" + lapMsg.getEvent());
          log.debug("-----------");
        }
        break;

      case MesgNum.RECORD:
        RecordMesg record = new RecordMesg(msg);
        addRecordMesg(record);
        if (log.isDebugEnabled()) {
          log.debug("RECORD "
                    + dateFormat.format(record.getTimestamp().getDate())
                    + " Distance=" + record.getDistance() + " HeartRate="
                    + record.getHeartRate() + " Speed=" + record.getSpeed()
                    + " Altitude=" + record.getAltitude() + " Lat="
                    + record.getPositionLat() + " lon="
                    + record.getPositionLat());
        }
        break;

      case MesgNum.EVENT:
        if (log.isDebugEnabled()) {
          EventMesg event = new EventMesg(msg);
          log.debug("EVENT " + event.getEventType());
        }
        break;

      default:
        if (log.isDebugEnabled()) {
          log.debug("NAME=" + msg.getName() + " num=" + msg.getNum());
        }
    }
  }

  private void addLap(LapMesg msg) {
    session.addLapMesg(msg);
  }

  private void addSession(SessionMesg msg) {
    session.sessionMesg = msg;
  }

  private void addRecordMesg(RecordMesg msg) {
    session.addRecordMesg(msg);
  }

  public void checkIsValid() throws GeoLoadException {
    // fileID
    if (fileId == null) {
      log.warn("fileId est null");
      throw new GeoLoadException("fileId est null");
    }
    if (fileId.getType() != com.garmin.fit.File.ACTIVITY) {
      log.warn("Not activity file : " + fileId.getType());
      throw new GeoLoadException("Not activity file : " + fileId.getType());
    }

    // 1 message Activity
    if (activityMsg == null) {
      log.warn("activityMsg est null");
      throw new GeoLoadException("activityMsg est null");
    }

    // 1 sessionMesg Activity
    if (session.sessionMesg == null) {
      log.warn("sessionMesg est null");
      throw new GeoLoadException("sessionMesg est null");
    }

    // Au moins un lap
    if (session.listLap.size() == 0) {
      log.warn("pas de lap");
      throw new GeoLoadException("pas de lap");
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class FitGeoRoute extends AbstractGeoRoute {
    private Session session;

    public FitGeoRoute(Session session) {
      this.session = session;
      initSportType();
    }

    public void initSportType() {
      switch (session.sessionMesg.getSport()) {
        case CYCLING:
          setSportType(SPORT_TYPE_BIKE);
          break;
        case RUNNING:
          setSportType(SPORT_TYPE_RUNNING);
          break;
        default:
          setSportType(SPORT_TYPE_OTHER);
          break;
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#getStartTime()
     */
    @Override
    public Date getStartTime() {
      return session.sessionMesg.getStartTime().getDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#distanceTot()
     */
    @Override
    public double distanceTot() {
      return (session.sessionMesg.getTotalDistance() == null) ? 0
          : session.sessionMesg.getTotalDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#totalTime()
     */
    @Override
    public long totalTime() {
      return (long) (session.sessionMesg.getTotalElapsedTime() == null ? 0
          : session.sessionMesg.getTotalElapsedTime() * 1000);
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
      if (getSegmentSize() == 1) {
        return getSegment(0).getPoints();
      }

      ArrayList<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
      for (int i = 0; i < getSegmentSize(); i++) {
        for (IGeoPositionWithAlt p : getSegment(i).getPoints()) {
          list.add(p);
        }
      }
      return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
     */
    public int getSegmentSize() {
      return session.listLap.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegment(int)
     */
    public IGeoSegment getSegment(int index) {
      return session.listLap.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegments()
     */
    public List<IGeoSegment> getSegments() {
      List<IGeoSegment> res = new ArrayList<IGeoSegment>();
      Collections.copy(res, session.listLap);
      return res;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class Session {
    private SessionMesg      sessionMesg;

    private List<Lap>        listLap    = new ArrayList<Lap>();

    private List<RecordMesg> listRecord = new ArrayList<RecordMesg>();

    public Session() {
      listLap.add(new Lap(0));
    }

    public void addLapMesg(LapMesg msg) {
      if (listLap.size() == 1 && listLap.get(0).lapmsg == null) {
        listLap.get(0).setLapMsg(msg);
      }
      else {
        listLap.add(new Lap(listLap.size(), msg));
      }
    }

    public void addRecordMesg(RecordMesg msg) {
      listLap.get(listLap.size() - 1).addRecord(msg);
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class Lap implements IGeoSegment {
    private LapMesg                   lapmsg;

    private List<IGeoPositionWithAlt> listPoints = new ArrayList<IGeoPositionWithAlt>();

    private Date                      startTime;

    private int                       index;

    public Lap(int index) {
      this.index = index;
    }

    public Lap(int index, LapMesg msg) {
      this(index);
      setLapMsg(msg);
    }

    public void setLapMsg(LapMesg lapmsg) {
      this.lapmsg = lapmsg;
      this.startTime = lapmsg.getStartTime().getDate();
    }

    public void addRecord(RecordMesg msg) {
      listPoints.add(new FitPoint(msg));
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#index()
     */
    public int index() {
      return index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getStartTime()
     */
    public Date getStartTime() {
      return startTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#setStartTime(java.util.Date)
     */
    public void setStartTime(Date startTime) {
      this.startTime = startTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getTotalTime()
     */
    public long getTotalTime() {
      return (long) ((lapmsg.getTotalElapsedTime() == null) ? 0 : lapmsg
          .getTotalElapsedTime() * 1000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#distance()
     */
    public double distance() {
      return (lapmsg.getTotalDistance() == null) ? 0 : lapmsg
          .getTotalDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getCalories()
     */
    public int getCalories() {
      return (lapmsg.getTotalCalories() == null) ? 0 : lapmsg
          .getTotalCalories();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getMaxSpeed()
     */
    public double getMaxSpeed() {
      return (lapmsg.getMaxSpeed() == null) ? 0 : lapmsg.getMaxSpeed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getAvgHeartRate()
     */
    public int getAvgHeartRate() {
      return (lapmsg.getAvgHeartRate() == null) ? 0 : lapmsg.getAvgHeartRate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getMaxHeartRate()
     */
    public int getMaxHeartRate() {
      return (lapmsg.getMaxHeartRate() == null) ? 0 : lapmsg.getMaxHeartRate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getPoints()
     */
    public List<IGeoPositionWithAlt> getPoints() {
      return listPoints;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  public class FitPoint extends GeoPositionWithAlt {

    public FitPoint(RecordMesg mesg) {
      super();
      if (mesg.getPositionLat() != null && mesg.getPositionLong() != null) {
        setLatitude(mesg.getPositionLat() * CONST_CONVERT);
        setLongitude(mesg.getPositionLong() * CONST_CONVERT);
      }

      setDate(mesg.getTimestamp().getDate());

      if (mesg.getHeartRate() != null) {
        setHeartRate(mesg.getHeartRate());
      }

      if (mesg.getDistance() != null) {
        setDistanceMeters(mesg.getDistance());
      }

      if (mesg.getCadence() != null) {
        setCadence(mesg.getCadence());
      }

      if (mesg.getAltitude() != null) {
        setElevation(mesg.getAltitude());
      }
    }

  }
}
