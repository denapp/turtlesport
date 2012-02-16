package fr.turtlesport.db.progress;

import fr.turtlesport.protocol.data.AbstractRunType;

/**
 * @author Denis Apparicio
 * 
 */
public interface IRunStoreProgress {
  int POINT_NOTIFY = 15;

  /**
   * D&eacute;but sauvegarde.
   * 
   * @param nombre
   *          de lignes totale &agrave; sauvegarder.
   */
  void beginStore(int maxLines);

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
   * @param run
   *          la course.
   */
  void beginStore(AbstractRunType run);

  /**
   * Fin sauvegarde.
   * 
   * @param run
   *          la course.
   */
  void endStore(AbstractRunType run);

  /**
   * Debut transfert point.
   * 
   */
  void beginStorePoint();

  /**
   * Debut sauvegarde d'un point.
   * 
   * @param run
   *          la course.
   * @param maxpoint
   *          nombre de points maximale &aagrve; sauvegarder.
   * @param currentPoint
   *          point courant &aagrve; sauvegarder.
   */
  void storePoint(AbstractRunType run, int currentPoint, int maxPoint);

  /**
   * Fin sauvegarde.
   * 
   */
  void endStore();
}
