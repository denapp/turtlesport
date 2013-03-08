package fr.turtlesport.meteo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class WuCurrentHandler extends DefaultHandler {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(WuCurrentHandler.class);
  }

  private DataMeteo           data;

  private StringBuffer        stBuffer;

  /**
   * Restitue les donn&eacute;es m&eacute;t&eacute;o.
   * 
   * @return les donn&eacute;es m&eacute;t&eacute;o.
   */
  public DataMeteo getData() {
    return data;
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
    stBuffer = null;
  }

  /*
   * (non-Javadoc) BufferedReader
   * 
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("weather".equals(localName)) {
      data.setCondition(stBuffer.toString());
    }
    else if ("local_time_rfc822".equals(localName)) {
      SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                                                 Locale.ENGLISH);
      try {
        data = new DataMeteo(df.parse(stBuffer.toString()));
      }
      catch (ParseException e) {
        log.error("", e);
        data = new DataMeteo(Calendar.getInstance().getTime());
      }
    }
    else if ("temp_c".equals(localName)) {
      data.setTemperature(Integer.parseInt(stBuffer.toString()));
    }
    else if ("relative_humidity".equals(localName)) {
      String st = stBuffer.toString();
      st = st.substring(0, st.length() - 1);
      data.setHumidity((int) Float.parseFloat(st));
    }
    else if ("pressure_mb".equals(localName)) {
      data.setPressurehPa((int) Float.parseFloat(stBuffer.toString()));
    }
    else if ("visibility_km".equals(localName)) {
      if ("N/A".equals(stBuffer.toString())) {
        data.setVisibility(-1);
      }
      else {
        data.setVisibility((int) Float.parseFloat(stBuffer.toString()));
      }
    }
    else if ("wind_dir".equals(localName)) {
      data.setWindDirection(stBuffer.toString());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
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
