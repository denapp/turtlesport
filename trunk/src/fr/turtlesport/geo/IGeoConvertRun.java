package fr.turtlesport.geo;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import fr.turtlesport.db.DataRun;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoConvertRun extends IGeoFileDesc {

  /**
   * Conversion de plusieurs run.
   * 
   * @param runs
   *          liste des runs
   * @param progress
   * @param file
   *          fichier de sauvegarde des runs.
   * @throws KmlConvertException
   * @throws SQLException
   */
  File convert(List<DataRun> runs, IGeoConvertProgress progress, File file) throws GeoConvertException,
                                                                           SQLException;

  /**
   * Conversion vers le format.
   * 
   * @param out
   * @param trk
   * @throws KmlConvertException
   * @throws SQLException
   */
  File convert(DataRun data, File file) throws GeoConvertException,
                                       SQLException;

  /**
   * Conversion vers le format.
   * 
   * @param out
   * @param trk
   * @throws KmlConvertException
   * @throws SQLException
   */
  File convert(DataRun data) throws GeoConvertException, SQLException;
}
