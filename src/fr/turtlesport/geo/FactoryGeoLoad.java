package fr.turtlesport.geo;

import java.io.File;
import java.io.FileNotFoundException;

import fr.turtlesport.geo.garmin.fit.FitFile;
import fr.turtlesport.geo.garmin.hst.HstFile;
import fr.turtlesport.geo.garmin.tcx.TcxFile;
import fr.turtlesport.geo.gpx.GpxFile;
import fr.turtlesport.geo.suunto.SuuntoFile;

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
   * @param extension
   *          l'extension.
   * @return une instance.
   */
  public static IGeoFile getInstance(String extension) {
    if (GPX.equals(extension)) {
      return new GpxFile();
    }
    if (TCX.equals(extension)) {
      return new TcxFile();
    }
    if (HST.equals(extension)) {
      return new HstFile();
    }
    if (FIT.equals(extension)) {
      return new FitFile();
    }
    if (XML.equals(extension)) {
      return new SuuntoFile();
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
  public static IGeoRoute[] getRoutes(File file) throws FileNotFoundException,
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

    IGeoFile geo;
    if (isIn(GpxFile.EXT, ext)) {
      geo = new GpxFile();
    }
    else if (isIn(TcxFile.EXT, ext)) {
      geo = new TcxFile();
    }
    else if (isIn(HstFile.EXT, ext)) {
      geo = new HstFile();
    }
    else if (isIn(FitFile.EXT, ext)) {
      geo = new FitFile();
    }
    else if (isIn(SuuntoFile.EXT, ext)) {
      geo = new SuuntoFile();
    }
    else {
      throw new IllegalArgumentException();
    }

    return geo.load(file);
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
