package fr.turtlesport.geo.kml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.geo.IGeoConvertCourse;
import fr.turtlesport.geo.IGeoConvertProgress;
import fr.turtlesport.geo.IGeoConvertRun;
import fr.turtlesport.geo.pcx5.Pcx5File;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.D1006CourseType;
import fr.turtlesport.protocol.data.D304TrkPointType;
import fr.turtlesport.util.GeoUtil;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public final class KmlGeo implements IGeoConvertRun, IGeoConvertCourse {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Pcx5File.class);
  }

  private static final String[]         EXT      = { "kml" };

  private static final SimpleDateFormat FMT_DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  /**
   * 
   */
  public KmlGeo() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.geo.IGeo#description()
   */
  public String description() {
    return "Google Earth (*.kml)";
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

  /**
   * Conversion vers le format kml.
   * 
   * @param out
   * @param trk
   * @throws KmlConvertException
   * @throws SQLException
   */
  public File convert(DataRun dataRun, File file) throws GeoConvertException,
                                                 SQLException {
    log.debug(">>convert");

    long starTime = System.currentTimeMillis();

    if (dataRun == null) {
      throw new IllegalArgumentException("dataRun est null");
    }
    if (file == null) {
      throw new IllegalArgumentException("file est null");
    }

    List<DataRunTrk> trks = RunTrkTableManager.getInstance()
        .getTrks(dataRun.getId());
    if (trks != null && trks.size() < 1) {
      return null;
    }

    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(file));

      writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      writeln(writer);

      // <kml>
      // --------------------------------------------------
      writer.write("<kml xmlns=\"http://earth.google.com/kml/2.0\">");
      writeln(writer);

      // <Document> fils de <kml>
      // --------------------------------------------------
      writer.write("<Document>");
      writeln(writer);

      // <Style> fils de <Document>
      // --------------------------------------------------
      writer.write("<Style id=\"IconWpt\"/>");
      writeln(writer);

      // <open> fils de <Document>
      // --------------------------------------------------
      writer.write("<open>1</open>");
      writeln(writer);

      // <Placemark> fils de <Document> (liste des points)
      // --------------------------------------------------
      writer.write("<Placemark>");
      writeln(writer);
      // <name> fils de <Placemark>
      writer.write("<name>");
      writer.write("Track (");
      writer.write(Integer.toString(trks.size()));
      writer.write(" records)");
      writer.write("</name>");
      writeln(writer);

      // <Style> fils de <Placemark>
      writer.write("<Style><LineStyle>");
      writer.write("<width>2</width><color>ff0000ff</color>");
      writer.write("</LineStyle></Style>");
      writeln(writer);

      // <LineString> fils de <Placemark>
      writer.write("<LineString>");
      writer.write("<extrude>1</extrude>");
      writer.write("<tessellate>1</tessellate>");

      writer.write("<coordinates>");
      writeCoordinates(writer, trks);
      writer.write("</coordinates>");

      writer.write("</LineString>");
      writeln(writer);

      writer.write("</Placemark>");
      writeln(writer);

      // <Placemark> fils de <Document> (start)
      // ---------------------------------------
      writer.write("<Placemark>");
      writeln(writer);

      // <name> fils de <Placemark>
      writer.write("<name>Start</name>");
      writeln(writer);

      // <Style> fils de <Placemark>
      writer.write("<Style><IconStyle>");
      writer.write("<Icon><href>root://icons/palette-3.png</href></Icon>");
      writer.write("</IconStyle></Style>");
      writeln(writer);

      // <description> fils de <Placemark>
      writer.write("<description>");
      writePointCData(writer, trks.get(0).getTime(), trks.get(0).getAltitude());
      writer.write("</description>");
      writeln(writer);

      // <point> fils de <Placemark>
      writePoint(writer, trks.get(0));
      writer.write("</Placemark>");
      writeln(writer);

      // <Placemark> fils de <Document> (stop)
      // ---------------------------------------
      writer.write("<Placemark>");

      // <name> fils de <Placemark>
      writer.write("<name>Stop</name>");
      writeln(writer);

      // <description> fils de <Placemark>
      writer.write("<description>");
      writePointCData(writer,
                      trks.get(trks.size() - 1).getTime(),
                      trks.get(trks.size() - 1).getAltitude());
      writer.write("</description>");
      writeln(writer);

      // <Style> fils de <Placemark>
      writer.write("<Style><IconStyle>");
      // writer.write("<Icon><href>root://icons/palette-4.png</href></Icon>");
      writer.write("<Icon><href>root://icons/palette-5.png</href></Icon>");
      writer.write("</IconStyle></Style>");
      writeln(writer);

      // <point> fils de <Placemark>
      writePoint(writer, trks.get(trks.size() - 1));

      writer.write("</Placemark>");
      writeln(writer);

      // <Folder> fils de <Document>
      // ---------------------------------------
      DataRunTrk pointMin = retreiveMinAltitudePoint(trks);
      DataRunTrk pointMax = retreiveMaxAltitudePoint(trks);

      if (pointMin != null || pointMax != null) {
        writer.write("<Folder>");
        writeln(writer);
        // <name> fils de <Folder>
        writer.write("<name>Statistics</name>");
        writeln(writer);
      }

      // <Placemark> fils de <Folder> (min)
      // -------------------------------------
      if (pointMin != null) {
        writer.write("<Placemark>");
        writeln(writer);

        // <name> fils de <Placemark>
        writer.write("<name>");
        writer.write("Min altitude " + (int) pointMin.getAltitude());
        writer.write("</name>");
        writeln(writer);

        // <Style> fils de <Placemark>
        writer.write("<Style><IconStyle>");
        writer.write("<Icon><href>root://icons/palette-4.png</href></Icon>");
        writer.write("</IconStyle></Style>");
        writeln(writer);

        // <Point> fils de <Placemark>
        writePoint(writer, pointMin);

        writer.write("</Placemark>");
        writeln(writer);
      }

      // <Placemark> fils de <Folder> (max)
      // -------------------------------------
      if (pointMax != null) {
        writer.write("<Placemark>");
        writeln(writer);

        // <name> fils de <Placemark>
        writer.write("<name>");
        writer.write("Max altitude " + (int) pointMax.getAltitude());
        writer.write("</name>");
        writeln(writer);

        // <Style> fils de <Placemark>
        writer.write("<Style><IconStyle>");
        writer.write("<Icon><href>root://icons/palette-4.png</href></Icon>");
        writer.write("</IconStyle></Style>");
        writeln(writer);

        // <Point> fils de <Placemark>
        writePoint(writer, pointMax);

        writer.write("</Placemark>");
        writeln(writer);

        writer.write("</Folder>");
        writeln(writer);
      }

      // <Folder> fils de <Document>
      // ---------------------------------------
      writer.write("<Folder>");
      writeln(writer);

      // <description> fils de <Folder>
      writer.write("<description>");
      writer.write(Integer.toString(trks.size()));
      writer.write(" records");
      writer.write("</description>");
      writeln(writer);

      // <name> fils de <Folder>
      writer.write("<name>Waypoints</name>");
      writeln(writer);
      for (int i = 0; i < trks.size(); i++) {
        convertWayPoint(writer, trks.get(i), i);
      }
      writeln(writer);
      writer.write("</Folder>");

      writeln(writer);
      writer.write("</Document>");
      writeln(writer);
      writer.write("</kml>");
    }
    catch (IOException e) {
      log.error("", e);
      throw new KmlGeoConvertException(e);
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
    }

    long endTime = System.currentTimeMillis();
    log.info("Temps pour ecrire kml : " + (endTime - starTime) + " ms");

    log.debug("<<convert");
    return file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.convert.IConvert#convert(fr.turtlesport.db.DataRun)
   */
  public File convert(DataRun data) throws GeoConvertException, SQLException {
    // Recuperation du fichier
    String name = LanguageManager.getManager().getCurrentLang()
        .getDateTimeFormatterWithoutSep().format(data.getTime())
                  + ".kml";
    File file = new File(Location.googleEarthLocation(), name);

    // conversion
    convert(data, file);
    return file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.geo.convert.IGeoRunConvert#convert(fr.turtlesport.protocol
   * .data.D1006CourseType, java.io.File)
   */
  public File convert(D1006CourseType data, File file) throws GeoConvertException {
    log.debug(">>convert");

    long starTime = System.currentTimeMillis();

    if (data == null) {
      throw new IllegalArgumentException("data est null");
    }
    if (file == null) {
      throw new IllegalArgumentException("file est null");
    }

    if (data.getListTrkPointTypeSize() < 1) {
      return null;
    }

    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(file));

      writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      writeln(writer);

      // <kml>
      // --------------------------------------------------
      writer.write("<kml xmlns=\"http://earth.google.com/kml/2.0\">");
      writeln(writer);

      // <Document> fils de <kml>
      // --------------------------------------------------
      writer.write("<Document>");
      writeln(writer);

      // <Style> fils de <Document>
      // --------------------------------------------------
      writer.write("<Style id=\"IconWpt\"/>");
      writeln(writer);

      // <open> fils de <Document>
      // --------------------------------------------------
      writer.write("<open>1</open>");
      writeln(writer);

      // <Placemark> fils de <Document> (liste des points)
      // --------------------------------------------------
      writer.write("<Placemark>");
      writeln(writer);
      // <name> fils de <Placemark>
      writer.write("<name>");
      writer.write("Track (");
      writer.write(Integer.toString(data.getListTrkPointTypeSize()));
      writer.write(" records)");
      writer.write("</name>");
      writeln(writer);

      // <Style> fils de <Placemark>
      writer.write("<Style><LineStyle>");
      writer.write("<width>2</width><color>ff0000ff</color>");
      writer.write("</LineStyle></Style>");
      writeln(writer);

      // <LineString> fils de <Placemark>
      writer.write("<LineString>");
      writer.write("<extrude>1</extrude>");
      writer.write("<tessellate>1</tessellate>");

      writer.write("<coordinates>");
      writeCoordinates(writer, data.getListTrkPointType());
      writer.write("</coordinates>");

      writer.write("</LineString>");
      writeln(writer);

      writer.write("</Placemark>");
      writeln(writer);

      // <Placemark> fils de <Document> (start)
      // ---------------------------------------
      writer.write("<Placemark>");
      writeln(writer);

      // <name> fils de <Placemark>
      writer.write("<name>Start</name>");
      writeln(writer);

      // <Style> fils de <Placemark>
      writer.write("<Style><IconStyle>");
      writer.write("<Icon><href>root://icons/palette-3.png</href></Icon>");
      writer.write("</IconStyle></Style>");
      writeln(writer);

      // <description> fils de <Placemark>
      writer.write("<description>");
      writePointCData(writer, data.getListTrkPointType(0).getTime(), data
          .getListTrkPointType(0).getAltitude());
      writer.write("</description>");
      writeln(writer);

      // <point> fils de <Placemark>
      writePoint(writer, data.getListTrkPointType(0));
      writer.write("</Placemark>");
      writeln(writer);

      // <Placemark> fils de <Document> (stop)
      // ---------------------------------------
      writer.write("<Placemark>");

      // <name> fils de <Placemark>
      writer.write("<name>Stop</name>");
      writeln(writer);

      // <description> fils de <Placemark>
      writer.write("<description>");
      writePointCData(writer,
                      data.getListTrkPointType(data.getListTrkPointTypeSize() - 1)
                          .getTime(),
                      data.getListTrkPointType(data.getListTrkPointTypeSize() - 1)
                          .getAltitude());
      writer.write("</description>");
      writeln(writer);

      // <Style> fils de <Placemark>
      writer.write("<Style><IconStyle>");
      // writer.write("<Icon><href>root://icons/palette-4.png</href></Icon>");
      writer.write("<Icon><href>root://icons/palette-5.png</href></Icon>");
      writer.write("</IconStyle></Style>");
      writeln(writer);

      // <point> fils de <Placemark>
      writePoint(writer,
                 data.getListTrkPointType(data.getListTrkPointTypeSize() - 1));

      writer.write("</Placemark>");
      writeln(writer);

      // <Folder> fils de <Document>
      // ---------------------------------------
      D304TrkPointType pointMin = retreiveMinAltitudePoint(data
          .getListTrkPointType());
      D304TrkPointType pointMax = retreiveMaxAltitudePoint(data
          .getListTrkPointType());

      if (pointMin != null || pointMax != null) {
        writer.write("<Folder>");
        writeln(writer);

        // <name> fils de <Folder>
        writer.write("<name>Statistics</name>");
        writeln(writer);
      }

      // <Placemark> fils de <Folder> (min)
      // -------------------------------------
      if (pointMin != null) {
        writer.write("<Placemark>");
        writeln(writer);

        // <name> fils de <Placemark>
        writer.write("<name>");
        writer.write("Min altitude " + (int) pointMin.getAltitude());
        writer.write("</name>");
        writeln(writer);

        // <Style> fils de <Placemark>
        writer.write("<Style><IconStyle>");
        writer.write("<Icon><href>root://icons/palette-4.png</href></Icon>");
        writer.write("</IconStyle></Style>");
        writeln(writer);

        // <Point> fils de <Placemark>
        writePoint(writer, pointMin);

        writer.write("</Placemark>");
        writeln(writer);
      }

      // <Placemark> fils de <Folder> (max)
      // -------------------------------------
      if (pointMax != null) {
        writer.write("<Placemark>");
        writeln(writer);

        // <name> fils de <Placemark>
        writer.write("<name>");
        writer.write("Max altitude " + (int) pointMax.getAltitude());
        writer.write("</name>");
        writeln(writer);

        // <Style> fils de <Placemark>
        writer.write("<Style><IconStyle>");
        writer.write("<Icon><href>root://icons/palette-4.png</href></Icon>");
        writer.write("</IconStyle></Style>");
        writeln(writer);

        // <Point> fils de <Placemark>
        writePoint(writer, pointMax);

        writer.write("</Placemark>");
        writeln(writer);
        writer.write("</Folder>");
        writeln(writer);
      }

      // <Folder> fils de <Document>
      // ---------------------------------------
      writer.write("<Folder>");
      writeln(writer);

      // <description> fils de <Folder>
      writer.write("<description>");
      writer.write(Integer.toString(data.getListTrkPointTypeSize()));
      writer.write(" records");
      writer.write("</description>");
      writeln(writer);

      // <name> fils de <Folder>
      writer.write("<name>Waypoints</name>");
      writeln(writer);
      for (int i = 0; i < data.getListTrkPointTypeSize(); i++) {
        convertWayPoint(writer, data.getListTrkPointType(i), i);
      }
      writeln(writer);
      writer.write("</Folder>");

      writeln(writer);
      writer.write("</Document>");
      writeln(writer);
      writer.write("</kml>");
    }
    catch (IOException e) {
      log.error("", e);
      throw new GeoConvertException(e);
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
    }

    long endTime = System.currentTimeMillis();
    log.info("Temps pour ecrire kml : " + (endTime - starTime) + " ms");

    log.debug("<<convert");
    return file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.geo.convert.IGeoRunConvert#convert(fr.turtlesport.protocol
   * .data.D1006CourseType)
   */
  public File convert(D1006CourseType data) throws GeoConvertException {
    // Recuperation du fichier
    String name = LanguageManager.getManager().getCurrentLang()
        .getDateTimeFormatterWithoutSep()
        .format(data.getListTrkPointType(0).getTime())
                  + ".kml";

    File file = new File(Location.googleEarthLocation(), name);

    // conversion
    convert(data, file);
    return file;
  }

  private void writeln(BufferedWriter writer) throws IOException {
    writer.write("\n");
  }

  private static void writePointCData(Writer writer, Date date, float altitude) throws IOException {
    writer.write("<![CDATA[");
    writer.write(FMT_DATE.format(date));
    writer.write("<br>Altitude ");
    writer.write(Float.toString(altitude));
    writer.write(" meters");
    writer
        .write("<br>Created by <a href=\"http://turtlesport.sourceforge.net\">Turtlesport</a>");
    writer.write("]]>");
  }

  private static String convertPointCoordinates(DataRunTrk point) {
    return convertPointCoordinates(new StringBuilder(), point);
  }

  private static String convertPointCoordinates(D304TrkPointType point) {
    return convertPointCoordinates(new StringBuilder(), point);
  }

  private static String convertPointCoordinates(StringBuilder st,
                                                DataRunTrk point) {
    st.append(GeoUtil.makeLongitudeFromGarmin(point.getLongitude()));
    st.append(',');
    st.append(GeoUtil.makeLatitudeFromGarmin(point.getLatitude()));
    if (point.isValidAltitude()) {
      st.append(',');
      st.append(point.isValidAltitude() ? point.getAltitude() : 0);
    }

    return st.toString();
  }

  private static String convertPointCoordinates(StringBuilder st,
                                                D304TrkPointType point) {
    st.append(GeoUtil.makeLongitudeFromGarmin(point.getPosn().getLongitude()));
    st.append(',');
    st.append(GeoUtil.makeLatitudeFromGarmin(point.getPosn().getLatitude()));
    if (point.isValidAltitude()) {
      st.append(',');
      st.append(point.isValidAltitude() ? point.getAltitude() : 0);
    }

    return st.toString();
  }

  private void writeCoordinates(Writer writer, List<DataRunTrk> points) throws IOException {
    int size = points.size() - 1;
    for (int i = 0; i < points.size(); i++) {
      writePointCoordinates(writer, points.get(i));
      writer.write("\n");
      // writer.write(' ');
    }
    writePointCoordinates(writer, points.get(size));
  }

  private void writeCoordinates(Writer writer, ArrayList<D304TrkPointType> list) throws IOException {
    int size = list.size() - 1;
    for (int i = 0; i < size; i++) {
      writePointCoordinates(writer, list.get(i));
      writer.write(' ');
    }
    writePointCoordinates(writer, list.get(size));
  }

  private void writePointCoordinates(Writer writer, DataRunTrk point) throws IOException {
    writer.write(Double.toString(GeoUtil.makeLongitudeFromGarmin(point
        .getLongitude())));
    writer.write(',');
    writer.write(Double.toString(GeoUtil.makeLatitudeFromGarmin(point
        .getLatitude())));
    if (point.isValidAltitude()) {
      writer.write(',');
      writer.append(Float.toString(point.getAltitude()));
    }
  }

  private void writePointCoordinates(Writer writer, D304TrkPointType point) throws IOException {
    writer.write(Double.toString(GeoUtil.makeLongitudeFromGarmin(point
        .getPosn().getLongitude())));
    writer.write(',');
    writer.write(Double.toString(GeoUtil.makeLatitudeFromGarmin(point.getPosn()
        .getLatitude())));
    if (point.isValidAltitude()) {
      writer.write(',');
      writer.append(Float.toString(point.getAltitude()));
    }
  }

  private void writePoint(Writer writer, DataRunTrk point) throws IOException {
    writer.write("<Point>");
    writer.write("<altitudeMode>absolute</altitudeMode>");
    writer.write("<extrude>1</extrude>");
    writer.write("<coordinates>");
    writer.write(convertPointCoordinates(point));
    writer.write("</coordinates>");
    writer.write("</Point>");
  }

  private void writePoint(Writer writer, D304TrkPointType point) throws IOException {
    writer.write("<Point>");
    writer.write("<altitudeMode>absolute</altitudeMode>");
    writer.write("<extrude>1</extrude>");
    writer.write("<coordinates>");
    writer.write(convertPointCoordinates(point));
    writer.write("</coordinates>");
    writer.write("</Point>");
  }

  private DataRunTrk retreiveMinAltitudePoint(List<DataRunTrk> points) {
    DataRunTrk pointCurrent = null;

    int i = 0;
    for (i = 0; i < points.size(); i++) {
      if (points.get(i).isValidAltitude()) {
        pointCurrent = points.get(i);
        break;
      }
    }
    for (; i < points.size(); i++) {
      if (points.get(i).isValidAltitude()
          && (points.get(i).getAltitude() < pointCurrent.getAltitude())) {
        pointCurrent = points.get(i);
      }
    }
    return pointCurrent;
  }

  private D304TrkPointType retreiveMinAltitudePoint(ArrayList<D304TrkPointType> list) {
    D304TrkPointType pointCurrent = null;

    int i = 0;
    for (i = 0; i < list.size(); i++) {
      if (list.get(i).isValidAltitude()) {
        pointCurrent = list.get(i);
        break;
      }
    }
    for (; i < list.size(); i++) {
      if (list.get(i).isValidAltitude()
          && (list.get(i).getAltitude() < pointCurrent.getAltitude())) {
        pointCurrent = list.get(i);
      }
    }
    return pointCurrent;
  }

  private DataRunTrk retreiveMaxAltitudePoint(List<DataRunTrk> points) {
    DataRunTrk pointCurrent = null;

    int i = 0;
    for (i = 0; i < points.size(); i++) {
      if (points.get(i).isValidAltitude()) {
        pointCurrent = points.get(i);
        break;
      }
    }
    for (; i < points.size(); i++) {
      if (points.get(i).isValidAltitude()
          && (points.get(i).getAltitude() > pointCurrent.getAltitude())) {
        pointCurrent = points.get(i);
      }
    }
    return pointCurrent;
  }

  private D304TrkPointType retreiveMaxAltitudePoint(ArrayList<D304TrkPointType> list) {
    D304TrkPointType pointCurrent = null;

    int i = 0;
    for (i = 0; i < list.size(); i++) {
      if (list.get(i).isValidAltitude()) {
        pointCurrent = list.get(i);
        break;
      }
    }
    for (; i < list.size(); i++) {
      if (list.get(i).isValidAltitude()
          && (list.get(i).getAltitude() > pointCurrent.getAltitude())) {
        pointCurrent = list.get(i);
      }
    }
    return pointCurrent;
  }

  private void convertWayPoint(Writer writer, DataRunTrk p, int index) throws IOException {
    writer.write("<Placemark>");
    writer.write("<name>");
    writer.write(Integer.toString(index));
    writer.write("</name>");

    writer.write("<visibility>0</visibility>");
    writer.write("<styleUrl>#IconWpt</styleUrl>");

    writePoint(writer, p);

    writer.write("</Placemark>");
  }

  private void convertWayPoint(Writer writer, D304TrkPointType p, int index) throws IOException {
    writer.write("<Placemark>");
    writer.write("<name>");
    writer.write(Integer.toString(index));
    writer.write("</name>");

    writer.write("<visibility>0</visibility>");
    writer.write("<styleUrl>#IconWpt</styleUrl>");

    writePoint(writer, p);

    writer.write("</Placemark>");
  }

}
