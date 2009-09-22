package fr.turtlesport.geo;

import java.io.File;
import java.sql.SQLException;

import fr.turtlesport.db.DataRun;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoConvertRun extends IGeoFileDesc {

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
