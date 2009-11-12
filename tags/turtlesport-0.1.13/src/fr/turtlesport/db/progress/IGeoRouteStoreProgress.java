package fr.turtlesport.db.progress;

import fr.turtlesport.geo.IGeoRoute;

/**
 * @author Denis Apparicio
 * 
 */
public interface IGeoRouteStoreProgress {
  int POINT_NOTIFY = 15;

  /**
   * D&eacute;but sauvegarde.
   * 
   * @param nombre
   *          de lignes totale &agrave; sauvegarder.
   */
  void beginStore(int maxPoint);
  
  /**
   * Debut sauvegarde ligne.
   * 
   * @param current
   *          ligne courante.
   * @param maxPoint
   *          nombre max. de lignes.
   */
  void store(int current, int maxPoint);

  /**
   * D&eacute;but sauvegarde.
   * 
   * @param route
   *          la course.
   */
  void beginStore(IGeoRoute route);

  /**
   * Fin sauvegarde.
   * 
   * @param route
   *          la course.
   */
  void endStore(IGeoRoute route);

  /**
   * Debut sauvegarde d'un point.
   * 
   * @param route
   *          la course.
   * @param maxpoint
   *          nombre de points maximale &agrave; sauvegarder.
   * @param currentPoint
   *          point courant &aagrve; sauvegarder.
   */
  void storePoint(IGeoRoute route, int currentPoint, int maxPoint);

  /**
   * Fin sauvegarde.
   * 
   */
  void endStore();
}
