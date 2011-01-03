package fr.turtlesport.geo.pcx5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.IGeoFile;
import fr.turtlesport.geo.IGeoRoute;
import fr.turtlesport.log.TurtleLogger;

// H SOFTWARE NAME & VERSION
// I PCX5 2.09
//
// H R DATUM IDX DA DF DX DY DZ
// M G WGS 84 121 +0.000000e+000 +0.000000e+000 +0.000000e+000 +0.000000e+000
// +0.000000e+000
//
// H COORDINATE SYSTEM
// U LAT LON DEG
//
// H LATITUDE LONGITUDE DATE TIME ALT ;track
// T N48.1710663 W000.2996762 15-JUL-07 08:33:09 00135
// T N48.1710612 W000.2997275 15-JUL-07 08:33:14 00135
// T N48.1710508 W000.2997453 15-JUL-07 08:33:18 00134
// T N48.1711036 W000.2998501 15-JUL-07 08:33:22 00135
// T N48.1711994 W000.2999072 15-JUL-07 08:33:25 00135
// T N48.1714154 W000.2999903 15-JUL-07 08:33:32 00135
// T N48.1717046 W000.3001017 15-JUL-07 08:33:40 00135
// T N48.1719335 W000.3002197 15-JUL-07 08:33:45 00135
// T N48.1721902 W000.3003513 15-JUL-07 08:33:51 00136
// T N48.1723737 W000.3004403 15-JUL-07 08:33:56 00128
// T N48.1724084 W000.3004611 15-JUL-07 08:33:57 00142

/**
 * @author Denis Apparicio
 * 
 */
public class Pcx5File implements IGeoFile {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Pcx5File.class);
  }

  /** Extensions */
  private static final String[] EXT = { "rte", "wpt", "trk" };

  /**
   * 
   */
  public Pcx5File() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFile#description()
   */
  public String description() {
    return "Garmin pcx5, CartoExplorateur (*.rte, *.wpt, *.trk)";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFile#extension()
   */
  public String[] extension() {
    return EXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeoFile#load(java.io.File)
   */

  public IGeoRoute[] load(File file) throws GeoLoadException,
                                    FileNotFoundException {
    log.debug(">>load");

    // Lecture
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

    Pcx5GeoRoute data = new Pcx5GeoRoute();

    String line = null;
    try {
      // on se positionne sur la ligne LATITUDE LONGITUDE
      int posLat = 0;
      int posLon = 0;
      int posDate = 0;
      int posTime = 0;
      int posAlt = 0;
      while ((line = reader.readLine()) != null) {
        if (line.contains("LATITUDE") && line.contains("LONGITUDE")
            && line.contains("DATE") && line.contains("TIME")) {
          posLat = line.indexOf("LATITUDE");
          posLon = line.indexOf("LONGITUDE");
          posDate = line.indexOf("DATE");
          posTime = line.indexOf("TIME");
          posAlt = line.indexOf("ALT");
          break;
        }
      }

      if (line == null) {
        throw new GeoLoadException();
      }

      // Recuperation des points
      Pcx5Point p;
      String tmp;
      int posEnd;
      while ((line = reader.readLine()) != null) {
        if ("".equals(line) || line.charAt(0) == ' ') {
          continue;
        }

        p = new Pcx5Point();
        data.add(p);

        // latitude
        posEnd = line.indexOf(' ', posLat);
        tmp = (posEnd == -1) ? line.substring(posLat) : line.substring(posLat,
                                                                       posEnd);
        p.convertLatitude(tmp);

        // longitude
        posEnd = line.indexOf(' ', posLon);
        tmp = (posEnd == -1) ? line.substring(posLon) : line.substring(posLon,
                                                                       posEnd);
        p.convertLongitude(tmp);

        // date - time
        posEnd = line.indexOf(' ', posDate);
        String date = (posEnd == -1) ? line.substring(posDate) : line
            .substring(posDate, posEnd);
        posEnd = line.indexOf(' ', posTime);
        String time = (posEnd == -1) ? line.substring(posTime) : line
            .substring(posTime, posEnd);
        p.convertDateTime(date, time);

        // Alt
        if (posAlt != -1) {
          posEnd = line.indexOf(' ', posAlt);
          tmp = (posEnd == -1) ? line.substring(posAlt) : line
              .substring(posAlt, posEnd);
          p.convertAlt(tmp);
        }

        log.debug(p);
      }
    }
    catch (GeoLoadException e) {
      throw e;
    }
    catch (Throwable e) {
      log.error("", e);
      throw new GeoLoadException(e);
    }
    finally {
      try {
        reader.close();
      }
      catch (IOException e) {
      }
    }

    IGeoRoute[] rep = new IGeoRoute[1];
    rep[0] = data;

    log.debug("<<load");
    return rep;
  }

}
