package fr.turtlesport.geo;

import fr.turtlesport.geo.garmin.hst.HstFile;
import fr.turtlesport.geo.garmin.tcx.TcxFile;
import fr.turtlesport.geo.gpx.GpxFile;
import fr.turtlesport.geo.kml.KmlGeo;

/**
 * @author Denis Apparicio
 * 
 */
public final class FactoryGeoConvertRun {

  /** Pour conversion kml. */
  public static final String KML = "kml";

  /** Pour conversion gpx. */
  public static final String GPX = "gpx";
  
  /** Pour conversion tcx. */
  public static final String TCX = "tcx";
  
  /** Pour conversion hst. */
  public static final String HST = "hst";

  private FactoryGeoConvertRun() {
  }

  /**
   * Restitue une instance.
   * 
   * @param extension
   *          l'extension.
   * @return une instance.
   */
  public static IGeoConvertRun getInstance(String extension) {

    if (KML.equals(extension)) {
      return new KmlGeo();
    }
    if (GPX.equals(extension)) {
      return new GpxFile();
    }
    if (TCX.equals(extension)) {
      return new TcxFile();
    }
    if (HST.equals(extension)) {
      return new HstFile();
    }

    throw new IllegalArgumentException();
  }
}
