package fr.turtlesport.meteo;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class WuGeoLookupHandler extends DefaultHandler {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(WuGeoLookupHandler.class);
  }

  private boolean             isNearbyWeatherStations = false;

  private boolean             isStation               = false;

  private List<StationMeteo>       listStation             = new ArrayList<StationMeteo>();

  private StationMeteo             currentStation;

  private boolean             isCity;

  private boolean             isIcao;

  private boolean             isLon;

  private boolean             isLat;

  private StringBuffer        stBuffer;

  /**
   * Restitue la liste des stations.
   * 
   * @return
   */
  public List<StationMeteo> getListStation() {
    return listStation;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
   * java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri,
                           String localName,
                           String qName,
                           Attributes attributes) throws SAXException {
    log.debug(">>startElement uri=" + uri + " localName=" + localName
              + " qName=" + qName);

    if ("nearby_weather_stations".equals(localName)) {
      isNearbyWeatherStations = true;
    }
    if ("station".equals(localName) && isNearbyWeatherStations) {
      isStation = true;
      currentStation = new StationMeteo();
    }
    if ("nearby_weather_stations".equals(localName)) {
      isNearbyWeatherStations = true;
    }

    if (isStation) {
      if ("city".equals(localName)) {
        isCity = true;
      }
      if ("icao".equals(localName)) {
        isIcao = true;
      }
      if ("lat".equals(localName)) {
        isLat = true;
      }
      if ("lon".equals(localName)) {
        isLon = true;
      }
    }

    log.debug("<<startElement");
  }

  /*
   * (non-Javadoc) BufferedReader
   * 
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("station".equals(localName)) {
      isStation = false;
      listStation.add(currentStation);
      currentStation = null;
    }
    if (isCity) {
      if (stBuffer != null) {
        currentStation.setCity(stBuffer.toString());
      }
      isCity = false;
    }
    if (isLat) {
      if (stBuffer != null) {
        currentStation.setLatitude(stBuffer.toString());
      }
      isLat = false;
    }
    if (isLon) {
      if (stBuffer != null) {
        currentStation.setLongitude(stBuffer.toString());
      }
      isLon = false;
    }
    if (isIcao) {
      if (stBuffer != null) {
        currentStation.setAirportCode(stBuffer.toString());
      }
      isIcao = false;
    }
    stBuffer = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (!isCity && !isLat && !isLon && !isIcao) {
      return;
    }

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
