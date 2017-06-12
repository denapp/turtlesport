package fr.turtlesport.geo;

import fr.turtlesport.device.IProductDevice;
import fr.turtlesport.geo.energympro.CpoFile;
import fr.turtlesport.geo.garmin.fit.FitFile;
import fr.turtlesport.geo.garmin.hst.HstFile;
import fr.turtlesport.geo.garmin.tcx.TcxFile;
import fr.turtlesport.geo.gpx.GpxFile;
import fr.turtlesport.geo.suunto.SuuntoFile;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Denis Apparicio
 * 
 */
public final class FactoryGeoLoad {

  /** Pour conversion gpx. */
  public static final String GPX = "gpx";

  /** Pour conversion tcx. */
  public static final String TCX = "tcx";

  /** Pour conversion hst. */
  public static final String HST = "hst";

  /** Pour conversion FIT. */
  public static final String FIT = "fit";

  /** Pour conversion Suunto. */
  public static final String XML = "xml";

  
  private FactoryGeoLoad() {
  }

  /**
   * Restitue une instance.
   * 
   * @param ext
   *          l'extension.
   * @return une instance.
   */
  public static IGeoFile getInstance(String ext) {
    if (isIn(GpxFile.EXT, ext)) {
      return new GpxFile();
    }
    if (isIn(TcxFile.EXT, ext)) {
      return new TcxFile();
    }
    if (isIn(HstFile.EXT, ext)) {
      return new HstFile();
    }
    if (isIn(FitFile.EXT, ext)) {
      return new FitFile();
    }
    if (isIn(SuuntoFile.EXT, ext)) {
      return new SuuntoFile();
    }
    if (isIn(CpoFile.EXT, ext)) {
      return new CpoFile();
    }
    
    throw new IllegalArgumentException();
  }

  /**
   * Restitue les pistes.
   * 
   * @param file
   *          le fichier
   * @return
   * @throws FileNotFoundException
   *           si fichier non trouv&eacute;
   * @throws GeoLoadException
   */
  public static IGeoRoute[] getRoutes(File file, IProductDevice productDevice) throws FileNotFoundException,
                                                GeoLoadException {
    if (file == null || !file.isFile()) {
      throw new FileNotFoundException();
    }

    // recuperation de l'extension
    String ext = null;
    String s = file.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase();
    }

    IGeoFile geo = getInstance(ext);
    return geo.load(file, productDevice);
  }

  private static boolean isIn(String[] ext, String theExt) {
    for (String s : ext) {
      if (s.equalsIgnoreCase(theExt)) {
        return true;
      }
    }
    return false;
  }
}
