package fr.turtlesport.geo;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoConvertProgress {

  /**
   * Annule la conversion.
   * 
   */
  boolean cancel();

  /**
   * D&eacute;but conversion.
   * 
   * 
   * @param nbRuns
   *          nombre de course.
   */
  void begin(int nbRuns);

  /**
   * Conversion d'un run
   * 
   * @param index
   *          du run.
   * @param nbRuns
   *          nombre de runs.
   */
  void convert(int index, int nbRuns);

  /**
   * Fin conversion.
   * 
   * @param run
   *          la course.
   */
  void end();
}
