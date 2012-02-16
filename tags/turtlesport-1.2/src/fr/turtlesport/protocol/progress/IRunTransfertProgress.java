package fr.turtlesport.protocol.progress;

import fr.turtlesport.protocol.data.AbstractLapType;
import fr.turtlesport.protocol.data.AbstractRunType;

/**
 * @author Denis Apparicio
 * 
 */
public interface IRunTransfertProgress {

  int POINT_NOTIFY = 15;

  /**
   * Annule le transfert.
   * 
   */
  boolean abortTransfert();

  /**
   * D&eacute;but transfert.
   * 
   * @param nbPacket
   *          nombre de packet &agrave; transfer&eacute;r.
   */
  void beginTransfert(int nbPacket);

  /**
   * Transfert.
   */
  void transfert();

  /**
   * D&eacute;but transfert.
   * 
   * @param run
   *          la course.
   */
  void endTransfert();

  /**
   * D&eacute;but transfert course.
   * 
   * @param run
   *          la course.
   */
  void beginTransfertCourse(AbstractRunType run);

  /**
   * Fin transfert course.
   * 
   * @param run
   *          la course.
   */
  void endTransfertCourse(AbstractRunType run);

  /**
   * D&eacute;but transfert point.
   * 
   * @param nbPacket
   *          nombre de packet &agrave; transfer&eacute;r.
   */
  void beginTransfertPoint(int nbPacket);

  /**
   * D&eacute;but transfert d'un point.
   * 
   * @param run
   *          la course.
   */
  void transfertPoint(AbstractRunType run);

  /**
   * D&eacute;but transfert tour interm&eacute;diaire.
   * 
   * @param nbPacket
   *          nombre de packet &agrave; transfer&eacute;r.
   */
  void beginTransfertLap(int nbPacket);

  /**
   * D&eacute;but transfert d'un tour interm&eacute;diaire.
   * 
   * @param run
   *          la course.
   * @param lapType
   *          le tour interm&eacute;diaire.
   */
  void transfertLap(AbstractRunType run, AbstractLapType lapType);

  /**
   * Notification points.
   * 
   * @return
   */
  int intervalNotify();
}
