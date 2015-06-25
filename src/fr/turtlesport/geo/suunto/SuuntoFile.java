package fr.turtlesport.geo.suunto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import fr.turtlesport.device.IProductDevice;
import fr.turtlesport.geo.AbstractGeoRoute;
import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.IGeoFile;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoRoute;
import fr.turtlesport.geo.IGeoSegment;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.unit.TemperatureUnit;
import fr.turtlesport.util.GeoUtil;
import fr.turtlesport.util.XmlUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class SuuntoFile implements IGeoFile {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(SuuntoFile.class);
  }

  /** Extensions. */
  public static final String[] EXT = { "xml" };

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFileDesc#extension()
   */
  @Override
  public String[] extension() {
    return EXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFileDesc#description()
   */
  @Override
  public String description() {
    return "Suunto Ambit (*.xml)";
  }

  @Override
  public IGeoRoute[] load(File file) throws GeoLoadException,
                                    FileNotFoundException {
    log.debug(">>load");

    IGeoRoute[] rep;

    // Lecture
    FileInputStream fis = new FileInputStream(file);
    SuuntoInputStream in = new SuuntoInputStream(fis);

    SuuntoHandler handler = null;
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();

      handler = new SuuntoHandler();
      parser.parse(in, handler);

      if (log.isInfoEnabled()) {
        log.info("nb points=" + handler.listPoints.size());
        log.info("nb lap=" + handler.listLap.size());
      }

      // construction de la reponse
      ArrayList<IGeoRoute> list = new ArrayList<IGeoRoute>();
      if (handler.listLap != null) {
        list.add(new SuuntoGeoRoute(handler.listLap));
      }
      rep = new IGeoRoute[list.size()];
      if (list.size() > 0) {
        return list.toArray(rep);
      }
    }
    catch (Exception e) {
      log.error("", e);
      throw new GeoLoadException(e);
    }
    finally {
      if (fis != null) {
        try {
          fis.close();
        }
        catch (IOException e) {
          log.error("", e);
        }
      }
    }

    log.debug("<<load");
    return rep;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class SuuntoHandler extends DefaultHandler {
    private StringBuffer      stBuffer;

    private List<Lap>         listLap    = new ArrayList<Lap>();

    private List<SuuntoPoint> listPoints = new ArrayList<SuuntoPoint>();

    private Lap               currentLap;

    private SuuntoPoint       currentSample;

    private boolean           isSample   = false;

    private boolean           isLap      = false;

    private boolean           isLaps     = false;

    private int               diffLap    = 0;

    /**
     * 
     */
    public SuuntoHandler() {
      super();

    }

    @Override
    public void endDocument() throws SAXException {
      if (log.isDebugEnabled()) {
        log.debug(">>endDocument");
      }

      // filtre des geopositions
      final int ephe = 5;
      int len = listPoints.size() - 1;
      double distance = 0;
      double distGeo = 0;
      int indexBegin = -1;
      List<SuuntoPoint> newList = new ArrayList<SuuntoPoint>();
      for (int i = 0; i < len; i++) {
        if (!listPoints.get(i).isInvalidPosition()) {
          int nearIndex = (i < len - 1) ? (i + 1) : (i - 1);
          // HR
          if (!listPoints.get(i).isValidHeartRate()) {
            listPoints.get(i).setHeartRate(listPoints.get(nearIndex)
                .getHeartRate());
          }
          // Altitude
          if (!listPoints.get(i).isValidElevation()) {
            listPoints.get(i).setElevation(listPoints.get(nearIndex)
                .getElevation());
          }

          if (indexBegin != -1) {
            distGeo = GeoUtil.computeDistance(listPoints.get(indexBegin),
                                              listPoints.get(i));
            if (distGeo > ephe) {
              distance += distGeo;
              listPoints.get(i).setDistanceMeters(distance);
              indexBegin = i;
              newList.add(listPoints.get(i));
            }
          }
          else {
            indexBegin = i;
            listPoints.get(i).setDistanceMeters(0);
            newList.add(listPoints.get(i));
          }
        }
      }

      listPoints = newList;
      len = listPoints.size() - 1;

      // On reordonne les laps
      if (listLap == null || listLap.size() == 0) {
        Lap lap = new Lap(listPoints.get(0).getDate(), 0);
        for (SuuntoPoint p : listPoints) {
          lap.addPoint(p);
        }
        if (listLap == null) {
          listLap = new ArrayList<Lap>();
        }
        listLap.add(lap);
      }
      else {
        int i = 0;
        for (Lap lap : listLap) {
          long timeDeb = lap.getStartTime().getTime();
          long timeEnd = timeDeb + lap.getTotalTime();
          if (log.isInfoEnabled()) {
            log.info("timeDeb=" + timeDeb);
            log.info("timeEnd=" + timeEnd);
          }
          for (; i < len; i++) {
            long time = listPoints.get(i).getDate().getTime();
            if (time <= timeEnd) {
              lap.addPoint(listPoints.get(i));
            }
            else {
              break;
            }
          }
        }
      }

      for (Lap lap : listLap) {
        lap.compute();
        if (log.isInfoEnabled()) {
          log.info("TotalTime=" + lap.getTotalTime());
          log.info("distance=" + lap.distance());
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("<<endDocument");
      }
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
                  + " qName=" + localName);
      }

      // sample (suivant les versions Sample ou sample
      if (localName.equals("sample") || localName.equals("Sample")) {
        currentSample = new SuuntoPoint();
        isSample = true;
      }
      // Lap
      else if (localName.equals("Laps")) {
        isLaps = true;
      }
      else if (isLaps && localName.equals("Lap")) {
        currentLap = new Lap(listLap.size());
        isLap = true;
      }

      if (log.isDebugEnabled()) {
        log.debug("<<startElement");
      }
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

      if (localName.equals("sample") || localName.equals("Sample")) {
        isSample = false;
        listPoints.add(currentSample);
      }
      else if (localName.equals("Laps")) {
        isLaps = false;
      }
      else if (isLaps && localName.equals("Lap")) {
        isLap = false;
        if (currentLap.getTotalTimeSeconds() > 0) {
          listLap.add(currentLap);
        }
      }

      // Balises filles sample
      // -----------------------
      if (isSample && stBuffer != null) {
        // HR
        if (localName.equals("HR")) {
          currentSample.setHeartRate((int) (0.5 + 60 * Float.valueOf(stBuffer
              .toString())));
        }
        // time
        else if (localName.equals("UTC")) {
          currentSample.setDate(XmlUtil.getTime(stBuffer.toString()));
          if (log.isDebugEnabled()) {
            log.debug("Point Time: " + currentSample.getDate());
          }
        }
        // Latitude
        else if (localName.equals("Latitude")) {
          currentSample.setLatitude(Double.parseDouble(stBuffer.toString())
                                    * 180.0 / Math.PI);
        }
        // Longitude
        else if (localName.equals("Longitude")) {
          currentSample.setLongitude(Double.parseDouble(stBuffer.toString())
                                     * 180.0 / Math.PI);
        }
        // GPSAltitude
        else if (localName.equals("GPSAltitude")) {
          currentSample.setElevation(Double.parseDouble(stBuffer.toString()));
        }
        // Temperature
        else if (localName.equals("Temperature")) {
          currentSample.setTemperature((int) TemperatureUnit
              .convertToDegree(Double.parseDouble(stBuffer.toString())));
        }
        // Distance
        else if (localName.equals("Distance")) {
          currentSample
              .setDistanceMeters(Integer.parseInt(stBuffer.toString()));
        }
        // EnergyConsumption
        else if (localName.equals("EnergyConsumption")) {
          double kj = Double.parseDouble(stBuffer.toString());
          currentSample.setEnergyConsumption(kj);
        }
        // Speed
        else if (localName.equals("Speed")) {
          // double speed = Float.parseDouble(stBuffer.toString());
          // currentSample.setSpeed(speed);
        }
      }

      // Balises filles Lap
      // --------------------
      if (isLap) {
        // DateTime
        if (localName.equals("DateTime")) {
          Date dateLap = XmlUtil.getTime(stBuffer.toString());

          if (listLap.size() == 0) {
            if (listPoints != null && listPoints.size() > 0) {
              diffLap = (int) Math.round((dateLap.getTime() - listPoints.get(0)
                  .getDate().getTime()) / 3600000.0);
            }
          }
          currentLap.setStartTime(convertDate(XmlUtil.getTime(stBuffer
              .toString()), diffLap));
          if (log.isInfoEnabled()) {
            log.info("Lap Time: " + stBuffer.toString() + " : "
                     + currentLap.getStartTime());
          }
        }
        // Distance
        else if (localName.equals("Distance")) {
          currentLap.setDistance(Integer.parseInt(stBuffer.toString()));
          if (log.isInfoEnabled()) {
            log.info("Lap Distance: " + stBuffer.toString());
          }
        }
        // Duration
        else if (localName.equals("Duration")) {
          currentLap
              .setTotalTimeSeconds(Double.parseDouble(stBuffer.toString()));
          if (log.isInfoEnabled()) {
            log.info("Lap Duration: " + stBuffer.toString());
          }
        }
      }
      stBuffer = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
      String st = new String(ch, start, length).trim();
      if (log.isDebugEnabled()) {
        log.debug(">>characters " + st);
      }

      if (st.length() > 0) {
        if (stBuffer == null) {
          stBuffer = new StringBuffer(st);
        }
        else {
          stBuffer.append(st);
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("<<characters");
      }
    }

    private Date convertDate(Date dateLap, int diff) {
      Calendar calLap = Calendar.getInstance();
      calLap.setTimeZone(TimeZone.getTimeZone("UTC"));
      calLap.setTime(dateLap);

      calLap.add(Calendar.HOUR_OF_DAY, -diff);
      Date dateResult = calLap.getTime();

      return dateResult;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class SuuntoGeoRoute extends AbstractGeoRoute {
    private SuuntoDevice      device      = new SuuntoDevice();

    private List<IGeoSegment> listSegment = new ArrayList<IGeoSegment>();

    public SuuntoGeoRoute(List<Lap> laps) {

      if (laps != null) {
        double dist = 0;
        for (Lap l : laps) {
          listSegment.add(l);
          dist += l.distance();
        }
        setDistanceTot(dist);
      }
    }

    @Override
    public List<IGeoPositionWithAlt> getAllPoints() {
      List<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
      for (IGeoSegment seg : listSegment) {
        list.addAll(seg.getPoints());
      }
      return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
     */
    @Override
    public int getSegmentSize() {
      return listSegment.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegment(int)
     */
    @Override
    public IGeoSegment getSegment(int index) {
      return listSegment.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getSegments()
     */
    @Override
    public List<IGeoSegment> getSegments() {
      return listSegment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.geo.IGeoRoute#getName()
     */
    @Override
    public String getName() {
      return null;
    }

    @Override
    public IProductDevice getProductDevice() {
      return device;
    }
  }

  private static class SuuntoDevice implements IProductDevice {

    @Override
    public String displayName() {
      return "Suunto";
    }

    @Override
    public String id() {
      return null;
    }

    @Override
    public String softwareVersion() {
      return null;
    }

  }

}
