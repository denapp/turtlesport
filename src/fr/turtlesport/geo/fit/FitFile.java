package fr.turtlesport.geo.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.Decode;
import com.garmin.fit.EventMesg;
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
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.geo.IGeoFile;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoRoute;
import fr.turtlesport.geo.IGeoSegment;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Location;

public class FitFile implements IGeoFile, IGeoConvertRun {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(FitFile.class);
  }

  /** Extensions. */
  public static final String[] EXT = { "fit" };

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

    // Lecture
    FileInputStream fis = new FileInputStream(file);

    try {
      Decode decode = new Decode();
      ListenerFitMsg listener = new ListenerFitMsg();
      decode.read(fis, listener);

      listener.checkIsValid();

      IGeoRoute[] geos = new IGeoRoute[1];
      geos[0] = new FitGeoRoute(listener);
    }
    finally {
      try {
        fis.close();
      }
      catch (IOException e) {
      }
    }

    return null;
  }

  private class ListenerFitMsg implements MesgListener {
    private List<ActivityMesg> list       = new ArrayList<ActivityMesg>();

    private FileIdMesg         fileId;

    private ActivityMesg       activityMsg;

    private SessionMesg        sessionMesg;

    private List<LapMesg>      listLap    = new ArrayList<LapMesg>();

    private List<RecordMesg>   listRecord = new ArrayList<RecordMesg>();

    public void onMesg(Mesg msg) {
      switch (msg.getNum()) {
        case MesgNum.FILE_ID:
          fileId = new FileIdMesg(msg);
          if (log.isDebugEnabled()) {
            log.debug("FILE_ID found GarminProduct="
                      + fileId.getGarminProduct() + " " + fileId.getType());
          }
          break;

        case MesgNum.ACTIVITY:
          activityMsg = new ActivityMesg(msg);
          list.add(activityMsg);
          if (log.isDebugEnabled()) {
            log.debug("ACTIVITY " + activityMsg.getTimestamp() + " ; "
                      + activityMsg.getLocalTimestamp() + " ; "
                      + activityMsg.getTotalTimerTime());
          }
          break;

        case MesgNum.SESSION:
          sessionMesg = new SessionMesg(msg);
          if (log.isDebugEnabled()) {
            log.debug("SESSION TotalDistance=" + sessionMesg.getTotalDistance());
          }
          break;

        case MesgNum.LAP:
          LapMesg lap = new LapMesg(msg);
          listLap.add(lap);
          if (log.isDebugEnabled()) {
            log.debug("LAP TotalDistance=" + lap.getTotalDistance() + " "
                      + lap.getTimestamp().getDate());
          }
          break;

        case MesgNum.RECORD:
          RecordMesg record = new RecordMesg(msg);
          listRecord.add(record);
          if (log.isDebugEnabled()) {
            log.debug("RECORD " + record.getTimestamp() + " Distance="
                      + record.getDistance());
          }
          break;

        case MesgNum.EVENT:
          EventMesg event = new EventMesg(msg);
          if (log.isDebugEnabled()) {
            log.debug("EVENT " + event.getEventType());
          }
          break;

default:
          if (log.isDebugEnabled()) {
            log.debug("NAME=" + msg.getName() + " num=" + msg.getNum());
          }
      }
    }

    public void checkIsValid() throws GeoLoadException {
      // fileID
      if (fileId == null) {
        log.warn("fileId est  null");
        throw new GeoLoadException("fileId est  null");
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
      if (sessionMesg == null) {
        log.warn("sessionMesg est null");
        throw new GeoLoadException("sessionMesg est null");
      }

      // Au moins un lap
      if (listLap.size() == 0) {
        log.warn("pas de lap");
        throw new GeoLoadException("pas de lap");
      }

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class FitGeoRoute extends AbstractGeoRoute {
    private ListenerFitMsg    listener;

    private List<IGeoSegment> listSegment = new ArrayList<IGeoSegment>();

    public FitGeoRoute(ListenerFitMsg listener) {
      this.listener = listener;
      initSportType();

      // segment
      for (int i = 0; i < listener.listLap.size(); i++) {
        listSegment.add(new FitLapSegment(listener.listLap.get(i), i));
      }
    }

    public void initSportType() {
      switch (listener.sessionMesg.getSport()) {
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
      return listener.sessionMesg.getTimestamp().getDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#distanceTot()
     */
    @Override
    public double distanceTot() {
      return listener.sessionMesg.getTotalDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.AbstractGeoRoute#totalTime()
     */
    @Override
    public long totalTime() {
      return (long) (listener.sessionMesg.getTotalElapsedTime() * 1000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getName()
     */
    public String getName() {
      return null;
    }

    public List<IGeoPositionWithAlt> getAllPoints() {
      // TODO Auto-generated method stub
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
     */
    public int getSegmentSize() {
      return listSegment.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegment(int)
     */
    public IGeoSegment getSegment(int index) {
      return listSegment.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegments()
     */
    public List<IGeoSegment> getSegments() {
      return listSegment;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class FitLapSegment implements IGeoSegment {

    private LapMesg mesg;

    private int     index;

    private Date    startTime;

    public FitLapSegment(LapMesg mesg, int index) {
      this.mesg = mesg;
      this.index = index;
      this.startTime = mesg.getStartTime().getDate();
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
      return (long) (mesg.getTotalElapsedTime() * 1000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#distance()
     */
    public double distance() {
      return mesg.getTotalDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getCalories()
     */
    public int getCalories() {
      return (mesg.getTotalCalories() == null) ? 0 : mesg.getTotalCalories();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getMaxSpeed()
     */
    public double getMaxSpeed() {
      return (mesg.getMaxSpeed() == null) ? 0 : mesg.getMaxSpeed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getAvgHeartRate()
     */
    public int getAvgHeartRate() {
      return (mesg.getAvgHeartRate() == null) ? 0 : mesg.getAvgHeartRate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getMaxHeartRate()
     */
    public int getMaxHeartRate() {
      return (mesg.getMaxHeartRate() == null) ? 0 : mesg.getMaxHeartRate();
    }

    public List<IGeoPositionWithAlt> getPoints() {
      // TODO Auto-generated method stub
      return null;
    }

  }
}
