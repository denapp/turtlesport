package fr.turtlesport.protocol.progress;

import fr.turtlesport.protocol.data.AbstractLapType;
import fr.turtlesport.protocol.data.AbstractRunType;

/**
 * @author Denis Apparicio
 * 
 */
public class RunTransfertProgressAdaptor implements IRunTransfertProgress {

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfert(int)
   */
  public void beginTransfert(int nbPacket) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#transfert()
   */
  public void transfert() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfertCourse(fr.turtlesport.protocol.data.D1009RunType)
   */
  public void beginTransfertCourse(AbstractRunType run) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfertPoint(int)
   */
  public void beginTransfertPoint(int nbPacket) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#endTransfert()
   */
  public void endTransfert() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#endTransfertCourse(fr.turtlesport.protocol.data.D1009RunType)
   */
  public void endTransfertCourse(AbstractRunType run) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#beginTransfertLap(int)
   */
  public void beginTransfertLap(int nbPacket) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#transfertLap(fr.turtlesport.protocol.data.D1009RunType,
   *      fr.turtlesport.protocol.data.AbstractLapType)
   */
  public void transfertLap(AbstractRunType run, AbstractLapType lapType) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#transfertPoint(fr.turtlesport.protocol.data.D1009RunType)
   */
  public void transfertPoint(AbstractRunType run) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#pointNotify()
   */
  public int intervalNotify() {
    return 500;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.progress.IRunTransfertProgress#stopTransfert()
   */
  public boolean abortTransfert() {
    return false;
  }

}
