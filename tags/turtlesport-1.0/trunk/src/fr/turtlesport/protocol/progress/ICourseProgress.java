package fr.turtlesport.protocol.progress;

import fr.turtlesport.protocol.data.D1006CourseType;

/**
 * @author Denis Apparicio
 * 
 */
public interface ICourseProgress {

  int POINT_NOTIFY = 15;

  /**
   * Annule le transfert.
   * 
   */
  boolean abortTransfert();
  
  /**
   * D&eacute;but transfert.
   * 
   * @param run
   *          la course.
   */
  void beginTransfert();
  
  /**
   * D&eacute;but transfert.
   * 
   * @param run
   *          la course.
   */
  void endTransfert();

  /**
   * D&eacute;but transfert tour interm&eacute;diaire.
   */
  void beginTransfertLap();

  /**
   * D&eacute;but transfert point.
   * 
   * @param nombre
   *          de points.
   */
  void beginTransfertTrk(int nbPoints);

  /**
   * D&eacute;but transfert d'un point.
   * 
   * @param run
   *          la course.
   */
  void transfertTrk(D1006CourseType d1006);

  /**
   * D&eacute;but transfert point.
   * 
   * @param nombre
   *          de points.
   */
  void beginTransfertPoint(int nbPoints);

  /**
   * D&eacute;but transfert d'un point.
   * 
   * @param run
   *          la course.
   */
  void transfertPoint(D1006CourseType d1006);

  /**
   * Notification points.
   * 
   * @return
   */
  int pointNotify();

}
