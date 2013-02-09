package fr.turtlesport.geo.garmin.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.Decode;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.EventMesg;
import com.garmin.fit.FileCreatorMesg;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.LapMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgListener;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SoftwareMesg;

import fr.turtlesport.IProductDevice;
import fr.turtlesport.ProductDeviceUtil;
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
import fr.turtlesport.util.GeoUtil;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class FitFile implements IGeoFile, IGeoConvertRun {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(FitFile.class);
  }

  /** Extensions. */
  public static final String[] EXT           = { "fit" };

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

  /**
   * Restitue la date du fichier du run.
   * 
   * @param file
   * @return
   * @throws GeoLoadException
   * @throws FileNotFoundException
   */
  public Date retreiveDate(File file) throws GeoLoadException,
                                     FileNotFoundException {
    // Lecture
    FileInputStream fis = new FileInputStream(file);
    try {
      Decode decode = new Decode();
      DateMesgListener listener = new DateMesgListener();
      decode.read(fis, listener);

      if (log.isDebugEnabled()) {
        log.debug("date : " + listener.date);
      }
      return listener.date;
    }
    finally {
      try {
        fis.close();
      }
      catch (IOException e) {
      }
    }

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

      FitMesgListener listener = new FitMesgListener();
      decode.read(fis, listener);

      if (log.isInfoEnabled()) {
        log.info("Nombre de msg : " + listener.nbMsg);
        log.info("Nombre de points : " + listener.session.listRecord.size());
      }

      listener.checkIsValid();

      geos = new IGeoRoute[1];
      geos[0] = new FitGeoRoute(listener.session);
      if (log.isInfoEnabled()) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        log.info("Lap 0 StartTime :"
                 + dateFormat.format(listener.session.listLap.get(0)
                     .getStartTime()));
      }
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

  /**
   * @author Denis Apparicio
   * 
   */
  private class FitMesgListener implements MesgListener {

    private List<ActivityMesg> list    = new ArrayList<ActivityMesg>();

    private Session            session = new Session();

    private FileIdMesg         fileId;

    private ActivityMesg       activityMsg;

    private int                nbMsg   = 0;

    /*
     * (non-Javadoc)
     * 
     * @see com.garmin.fit.MesgListener#onMesg(com.garmin.fit.Mesg)
     */
    public void onMesg(Mesg msg) {
      SimpleDateFormat dateFormat = null;
      if (log.isDebugEnabled() || log.isInfoEnabled()) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      }

      nbMsg++;
      switch (msg.getNum()) {
        case MesgNum.FILE_ID:
          fileId = new FileIdMesg(msg);
          if (log.isInfoEnabled()) {
            log.info("FILE_ID found GarminProduct=" + fileId.getGarminProduct()
                     + " " + fileId.getType());
            log.info("-->FILE_ID");
            log.info("   GarminProduct=" + fileId.getGarminProduct());
            log.info("   Manufacturer=" + fileId.getManufacturer());
            log.info("   Number=" + fileId.getNumber());
            log.info("   SerialNumber=" + fileId.getSerialNumber());
            log.info("-----------");
          }
          break;

        case MesgNum.DEVICE_INFO:
          if (session.deviceInfo == null) {
            session.deviceInfo = new DeviceInfoMesg(msg);
          }
          if (log.isInfoEnabled()) {
            log.info("-->DEVICE_INFO");
            log.info("   Product=" + session.deviceInfo.getProduct());
            log.info("   SerialNumber=" + session.deviceInfo.getSerialNumber());
            log.info("   HardwareVersion="
                     + session.deviceInfo.getHardwareVersion());
            log.info("   Manufacturer=" + session.deviceInfo.getManufacturer());
            log.info("   SoftwareVersion="
                     + session.deviceInfo.getSoftwareVersion());
            log.info("-----------");
          }
          break;

        case MesgNum.SOFTWARE:
          if (log.isInfoEnabled()) {
            SoftwareMesg software = new SoftwareMesg(msg);
            log.info("-->SOFTWARE");
            log.info("   GarminProduct=" + software.getGarminProduct());
            log.info("   Manufacturer=" + software.getManufacturer());
            log.info("   MessageIndex=" + software.getMessageIndex());
            log.info("   PartNumber=" + software.getPartNumber());
            log.info("   Product=" + software.getProduct());
            log.info("   Version=" + software.getVersion());
            log.info("-----------");
          }

        case MesgNum.FILE_CREATOR:
          if (log.isInfoEnabled()) {
            FileCreatorMesg fileCreator = new FileCreatorMesg(msg);

            log.info("-->FILE_CREATOR");
            log.info("   HardwareVersion=" + fileCreator.getHardwareVersion());
            log.info("   SoftwareVersion=" + fileCreator.getSoftwareVersion());
            log.info("-----------");
          }
          break;

        case MesgNum.ACTIVITY:
          activityMsg = new ActivityMesg(msg);
          list.add(activityMsg);
          if (log.isInfoEnabled()) {
            log.info("-->ACTIVITY");
            log.info("   Timestamp="
                     + dateFormat.format(activityMsg.getTimestamp().getDate()));
            log.info("   LocalTimestamp=" + activityMsg.getLocalTimestamp());
            log.info("   TotalTimerTime=" + activityMsg.getTotalTimerTime());
            log.info("   NumSessions=" + activityMsg.getNumSessions());
            log.info("   Type=" + activityMsg.getType());
            log.info("   EventType=" + activityMsg.getEventType());
            log.info("-----------");
          }
          break;

        case MesgNum.SESSION:
          SessionMesg sessionMesg = new SessionMesg(msg);
          session.sessionMesg = sessionMesg;
          if (log.isInfoEnabled()) {
            log.info("-->SESSION");
            log.info("   FirstLapIndex=" + sessionMesg.getFirstLapIndex());
            log.info("   Timestamp="
                     + dateFormat.format(sessionMesg.getTimestamp().getDate()));
            log.info("   TotalDistance=" + sessionMesg.getTotalDistance());
            log.info("   TotalElapsedTime=" + sessionMesg.getTotalElapsedTime());
            log.info("   TotalTimerTime=" + sessionMesg.getTotalTimerTime());
            log.info("   TotalCalories=" + sessionMesg.getTotalCalories());
            log.info("   AvgSpeed=" + sessionMesg.getAvgSpeed());
            log.info("   MaxSpeed=" + sessionMesg.getMaxSpeed());
            log.info("   AvgHeartRate=" + sessionMesg.getAvgHeartRate());
            log.info("   MaxHeartRate=" + sessionMesg.getMaxHeartRate());
            log.info("   StartTime="
                     + dateFormat.format(sessionMesg.getStartTime().getDate()));
            log.info("   StartPositionLat=" + sessionMesg.getStartPositionLat());
            log.info("   StartPositionLong="
                     + sessionMesg.getStartPositionLong());
            log.info("   Type=" + sessionMesg.getEventType());
            log.info("   AvgTemperature=" + sessionMesg.getAvgTemperature());
            log.info("   Event=" + sessionMesg.getEvent());
            log.info("-----------");
          }
          break;

        case MesgNum.LAP:
          LapMesg lapMsg = new LapMesg(msg);
          session.addLapMesg(lapMsg);
          if (log.isInfoEnabled()) {
            log.info("-->LAP");
            if (lapMsg.getTimestamp() != null) {
              log.info("   Timestamp="
                       + dateFormat.format(lapMsg.getTimestamp().getDate()));
            }
            log.info("   TotalDistance=" + lapMsg.getTotalDistance());
            log.info("   TotalElapsedTime=" + lapMsg.getTotalElapsedTime());
            log.info("   TotalTimerTime=" + lapMsg.getTotalTimerTime());
            log.info("   TotalCalories=" + lapMsg.getTotalCalories());
            log.info("   AvgSpeed=" + lapMsg.getAvgSpeed());
            log.info("   MaxSpeed=" + lapMsg.getMaxSpeed());
            log.info("   AvgHeartRate=" + lapMsg.getAvgHeartRate());
            log.info("   MaxHeartRate=" + lapMsg.getMaxHeartRate());
            log.info("   StartTime="
                     + (lapMsg.getStartTime() == null ? null : dateFormat
                         .format(lapMsg.getStartTime().getDate())));
            log.info("   StartPositionLat=" + lapMsg.getStartPositionLat());
            log.info("   StartPositionLong=" + lapMsg.getStartPositionLong());
            log.info("   Cadence=" + lapMsg.getAvgCadence());
            log.info("   Type=" + lapMsg.getEventType());
            log.info("   Event=" + lapMsg.getEvent());
            log.info("-----------");
          }
          break;

        case MesgNum.RECORD:
          RecordMesg record = new RecordMesg(msg);
          session.addRecordMesg(record);
          if (log.isDebugEnabled()) {
            log.debug("RECORD "
                      + dateFormat.format(record.getTimestamp().getDate())
                      + " Distance=" + record.getDistance() + " HeartRate="
                      + record.getHeartRate() + " Speed=" + record.getSpeed()
                      + " Altitude=" + record.getAltitude() + " Lat="
                      + record.getPositionLat() + " lon="
                      + record.getPositionLat() + " cadence="
                      + record.getCadence() + " calories="
                      + record.getCalories() + " temperature="
                      + record.getTemperature());
          }
          break;

        case MesgNum.EVENT:
          if (log.isInfoEnabled()) {
            EventMesg event = new EventMesg(msg);
            log.info("EVENT " + event.getEventType());
          }
          break;

        default:
          if (log.isInfoEnabled()) {
            log.info("NAME=" + msg.getName() + " num=" + msg.getNum());
          }
      }
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

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class FitGeoRoute extends AbstractGeoRoute {
    private Session   session;

    private FitDevice device;

    public FitGeoRoute(Session session) {
      this.session = session;
      initSportType();

      // calories
      if (session.sessionMesg.getTotalCalories() != null
          && session.listLap.size() > 0
          && session.listLap.get(0).getCalories() == 0) {
        session.listLap.get(0).lapmsg.setTotalCalories(session.sessionMesg
            .getTotalCalories());
      }

      // Distance null pour Garmin Forerunner Fenix
      List<IGeoPositionWithAlt> list = getAllPoints();
      double distance = 0;
      ((GeoPositionWithAlt) list.get(0)).setDistanceMeters(distance);
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).isValidDistance()) {
          distance += GeoUtil.computeDistance(list.get(i - 1), list.get(i));
          ((GeoPositionWithAlt) list.get(i)).setDistanceMeters(distance);
        }
      }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getProductDevice()
     */
    @Override
    public IProductDevice getProductDevice() {
      if (session.deviceInfo != null && device == null) {
        device = new FitDevice(session.deviceInfo);
        System.out.println();
      }
      return device;
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
      List<IGeoSegment> list = new ArrayList<IGeoSegment>();
      if (session.listLap != null) {
        for (Lap l : session.listLap) {
          list.add(l);
        }
      }
      return list;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class Session {
    private SessionMesg      sessionMesg;

    private DeviceInfoMesg   deviceInfo;

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

  private class FitDevice implements IProductDevice {
    DeviceInfoMesg deviceInfo;

    public FitDevice(DeviceInfoMesg deviceInfo) {
      this.deviceInfo = deviceInfo;
    }

    @Override
    public String displayName() {
      return (deviceInfo.getProduct() == null) ? null : ProductDeviceUtil
          .name(deviceInfo.getProduct());
    }

    @Override
    public String id() {
      return (deviceInfo.getProduct() == null) ? null : Integer
          .toString(deviceInfo.getProduct());
    }

    @Override
    public String softwareVersion() {
      return (deviceInfo.getSoftwareVersion() == null) ? null : Float
          .toString(deviceInfo.getSoftwareVersion());
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class Lap implements IGeoSegment {
    private LapMesg                   lapmsg;

    private List<IGeoPositionWithAlt> listPoints   = new ArrayList<IGeoPositionWithAlt>();

    private Date                      startTime;

    private int                       index;

    private long                      calories     = -1;

    private int                       avgHeartRate = -1;

    private int                       maxHeartRate = -1;

    public Lap(int index) {
      this.index = index;
    }

    public Lap(int index, LapMesg msg) {
      this(index);
      setLapMsg(msg);
    }

    public void setLapMsg(LapMesg lapmsg) {
      this.lapmsg = lapmsg;
      if (lapmsg.getStartTime() != null) {
        this.startTime = lapmsg.getStartTime().getDate();
      }
    }

    public void addRecord(RecordMesg msg) {
      if (startTime == null) {
        startTime = msg.getTimestamp().getDate();
      }
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
      return (long) ((lapmsg.getTotalTimerTime() == null) ? 0 : lapmsg
          .getTotalTimerTime() * 1000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getTotalPauseTime()
     */
    @Override
    public long getTotalPauseTime() {
      return (long) ((lapmsg.getTotalElapsedTime() == null) ? 0 : (lapmsg
          .getTotalElapsedTime() - lapmsg.getTotalTimerTime()) * 1000);
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
      if (avgHeartRate == -1) {
        if (lapmsg.getAvgHeartRate() != null) {
          avgHeartRate = lapmsg.getAvgHeartRate();
        }
        else {
          int nb = 0;
          long avg = 0;
          for (IGeoPositionWithAlt p : listPoints) {
            if (p.getHeartRate() != 0) {
              nb++;
              avg += p.getHeartRate();
            }
          }
          avgHeartRate = (nb > 0) ? ((int) avg / nb) : 0;
        }
      }
      return avgHeartRate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoSegment#getMaxHeartRate()
     */
    public int getMaxHeartRate() {
      if (maxHeartRate == -1) {
        if (lapmsg.getMaxHeartRate() != null) {
          maxHeartRate = lapmsg.getMaxHeartRate();
        }
        else {
          for (IGeoPositionWithAlt p : listPoints) {
            maxHeartRate = Math.max(p.getHeartRate(), maxHeartRate);
          }
        }
      }
      return maxHeartRate;
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

      if (mesg.getTemperature() != null) {
        setTemperature(mesg.getTemperature());
      }
    }

  }

  /**
   * Fit listener pou recuperer la date.
   * 
   * @author Denis Apparicio
   * 
   */
  private class DateMesgListener implements MesgListener {

    public Date date;

    /*
     * (non-Javadoc)
     * 
     * @see com.garmin.fit.MesgListener#onMesg(com.garmin.fit.Mesg)
     */
    @Override
    public void onMesg(Mesg msg) {
      SimpleDateFormat dateFormat = null;
      if (log.isDebugEnabled() || log.isInfoEnabled()) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      }

      if (msg.getNum() == MesgNum.SESSION) {
        SessionMesg sessionMesg = new SessionMesg(msg);
        date = sessionMesg.getStartTime().getDate();
        if (log.isInfoEnabled()) {
          log.info("-->SESSION");
          log.info("   Timestamp="
                   + dateFormat.format(sessionMesg.getTimestamp().getDate()));
          log.info("   TotalDistance=" + sessionMesg.getTotalDistance());
          log.info("   TotalElapsedTime=" + sessionMesg.getTotalElapsedTime());
          log.info("   TotalTimerTime=" + sessionMesg.getTotalTimerTime());
          log.info("   TotalCalories=" + sessionMesg.getTotalCalories());
          log.info("   AvgSpeed=" + sessionMesg.getAvgSpeed());
          log.info("   MaxSpeed=" + sessionMesg.getMaxSpeed());
          log.info("   AvgHeartRate=" + sessionMesg.getAvgHeartRate());
          log.info("   MaxHeartRate=" + sessionMesg.getMaxHeartRate());
          log.info("   StartTime="
                   + dateFormat.format(sessionMesg.getStartTime().getDate()));
          log.info("   StartPositionLat=" + sessionMesg.getStartPositionLat());
          log.info("   StartPositionLong=" + sessionMesg.getStartPositionLong());
          log.info("   Type=" + sessionMesg.getEventType());
          log.info("   Event=" + sessionMesg.getEvent());
          log.info("-----------");
        }
      }

    }

  }

}
