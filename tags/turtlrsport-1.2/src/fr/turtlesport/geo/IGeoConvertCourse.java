package fr.turtlesport.geo;

import java.io.File;

import fr.turtlesport.protocol.data.D1006CourseType;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoConvertCourse {
  
  /**
   * Conversion vers le format.
   * 
   * @param out
   * @param trk
   * @throws KmlConvertException
   */
  File convert(D1006CourseType data, File file) throws GeoConvertException;

  /**
   * Conversion vers le format.
   * 
   * @param out
   * @param trk
   * @throws KmlConvertException
   */
  File convert(D1006CourseType data) throws GeoConvertException;
}
