package fr.turtlesport.geo;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoFile extends IGeoFileDesc {

  /**
   * Lecture.
   * 
   * @param file
   * @return restitue les routes.
   * @throws GeoLoadException
   * @throws FileNotFoundException
   * 
   */
  IGeoRoute[] load(File file) throws GeoLoadException, FileNotFoundException;
}
