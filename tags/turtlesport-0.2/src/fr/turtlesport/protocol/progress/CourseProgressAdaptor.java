package fr.turtlesport.protocol.progress;

import fr.turtlesport.protocol.data.D1006CourseType;

/**
 * @author Denis Apparicio
 * 
 */
public class CourseProgressAdaptor implements ICourseProgress {

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#abortTransfert()
   */
  public boolean abortTransfert() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfert()
   */
  public void beginTransfert() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfertLap()
   */
  public void beginTransfertLap() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfertTrk(int)
   */
  public void beginTransfertTrk(int nbPoints) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#endTransfert()
   */
  public void endTransfert() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#transfertTrk(fr.turtlesport.protocol.data.D1006CourseType)
   */
  public void transfertTrk(D1006CourseType d1006) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfertPoint(int)
   */
  public void beginTransfertPoint(int nbPoints) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#transfertPoint(fr.turtlesport.protocol.data.D1006CourseType)
   */
  public void transfertPoint(D1006CourseType d1006) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.ICourseTransfertProgress#pointNotify()
   */
  public int pointNotify() {
    return POINT_NOTIFY;
  }

}
