package fr.turtlesport.geo;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoFileDesc {

  /**
   * Restitue les extensions de ce type de fichier.
   * 
   * @return les extensions de ce type de fichier.
   */
  String[] extension();

  /**
   * Restitue une description de ce type de fichier.
   * 
   * @return une description de ce type de fichier.
   */
  String description();
  
}
