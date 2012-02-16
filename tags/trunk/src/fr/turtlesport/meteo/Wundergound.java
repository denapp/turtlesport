package fr.turtlesport.meteo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.CVSReader;

/**
 * @author Denis Apparicio
 * 
 */
public class Wundergound {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Wundergound.class);
  }

  private Wundergound() {
  }

  /**
   * Recheche une station meteo.
   * 
   * @param latitude
   * @param longitude
   * @return la station meteo la plus proche.
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public static StationMeteo lookup(double latitude, double longitude) throws IOException,
                                                                      ParserConfigurationException,
                                                                      SAXException {

    StringBuilder sUrl = new StringBuilder("http://api.wunderground.com/auto/wui/geo/GeoLookupXML/index.xml?query=");
    sUrl.append(latitude);
    sUrl.append(',');
    sUrl.append(longitude);
    URL url = new URL(sUrl.toString());

    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
    cnx.setConnectTimeout(3000);
    cnx.setReadTimeout(3000);
    cnx.setRequestMethod("GET");
    cnx.setDoInput(true);
    cnx.addRequestProperty("Accept-Language", "en;q=0.6,en-us;q=0.4,sv;q=0.2");

    if (cnx.getResponseCode() == HttpURLConnection.HTTP_OK) {
      try {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();

        WuGeoLookupHandler handler = new WuGeoLookupHandler();
        BufferedReader reader = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
        InputSource source = new InputSource(reader);
        parser.parse(source, handler);

        if (handler.getListStation().size() > 0) {
          return handler.getListStation().get(0);
        }
      }
      finally {
        cnx.disconnect();
      }
    }
    return null;
  }

  /**
   * Restitue la m&eacute;t&eacute;o courante.
   * 
   * @param station
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public static DataMeteo current(StationMeteo station) throws IOException,
                                                       ParserConfigurationException,
                                                       SAXException {

    URL url = new URL("http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=" + station.getAirportCode());
    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();

    cnx.setConnectTimeout(3000);
    cnx.setReadTimeout(3000);
    cnx.setRequestMethod("GET");
    cnx.setDoInput(true);
    cnx.addRequestProperty("Accept-Language", "en;q=0.6,en-us;q=0.4,sv;q=0.2");

    if (cnx.getResponseCode() == HttpURLConnection.HTTP_OK) {
      try {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();

        WuCurrentHandler handler = new WuCurrentHandler();
        BufferedReader reader = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
        InputSource source = new InputSource(reader);
        parser.parse(source, handler);
        
        return handler.getData();
      }
      finally {
        if (cnx != null) {
          cnx.disconnect();
        }
      }
    }

    return null;
  }

  /**
   * Recu&eacute;re l'historique.
   * 
   * @param station
   * @param cal
   * @throws IOException
   */
  public static List<DataMeteo> history(StationMeteo station, Calendar cal) throws IOException {

    List<DataMeteo> listDatas = new ArrayList<DataMeteo>();

    StringBuilder sUrl = new StringBuilder("http://www.wunderground.com/history/airport/");
    sUrl.append(station.getAirportCode());
    sUrl.append('/');
    sUrl.append(cal.get(Calendar.YEAR));
    sUrl.append('/');
    sUrl.append(cal.get(Calendar.MONTH) + 1);
    sUrl.append('/');
    sUrl.append(cal.get(Calendar.DAY_OF_MONTH));
    sUrl.append("/DailyHistory.html?req_city=NA&req_state=NA&req_statename=NA&format=1");

    URL url = new URL(sUrl.toString());
    if (log.isInfoEnabled()) {
      log.info(sUrl);
    }

    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
    cnx.setConnectTimeout(5000);
    cnx.setReadTimeout(5000);
    cnx.setRequestMethod("GET");
    cnx.setDoInput(true);
    cnx.addRequestProperty("Accept",
                           "text,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    cnx.addRequestProperty("Accept-Language", "en;q=0.6,en-us;q=0.4,sv;q=0.2");
    cnx.addRequestProperty("Accept-Charset", "en;q=0.6,en-us;q=0.4,sv;q=0.2");
    cnx.addRequestProperty("Accept-Language", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
    cnx.addRequestProperty("User-Agent",
                           "Mozilla/5.0 (X11; Linux i686; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");

    if (cnx.getResponseCode() == HttpURLConnection.HTTP_OK) {
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
        CVSReader cvs = new CVSReader(reader);
        cvs.readHeader();

        String[] datas;
        while ((datas = cvs.readLine()) != null) {
          DataMeteo d = DataMeteo.compute(datas);
          if (d != null) {
            listDatas.add(d);
          }
        }
      }
      finally {
        cnx.disconnect();
      }
    }

    return listDatas;
  }

  /**
   * Recu&eacute;re l'historique.
   * 
   * @param station
   * @param date
   * @return les donn&eacute;es meteo
   * @throws IOException
   */
  public static List<DataMeteo> history(StationMeteo station, Date date) throws IOException {
    if (log.isDebugEnabled()) {
      log.debug(">>history date=" + date);
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    log.debug("<<history");
    return history(station, cal);
  }
}
