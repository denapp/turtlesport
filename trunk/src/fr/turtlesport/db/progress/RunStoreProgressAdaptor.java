package fr.turtlesport.db.progress;

import fr.turtlesport.protocol.data.AbstractRunType;

/**
 * @author Denis Apparicio
 * 
 */
public class RunStoreProgressAdaptor implements IRunStoreProgress {

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#beginStore(int)
   */
  public void beginStore(int maxLines) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#beginStore(fr.turtlesport.protocol.data.D1009RunType)
   */
  public void beginStore(AbstractRunType run) {

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#beginStorePoint()
   */
  public void beginStorePoint() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#endStore(fr.turtlesport.protocol.data.D1009RunType)
   */
  public void endStore(AbstractRunType run) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#endStore()
   */
  public void endStore() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#store(int, int)
   */
  public void store(int current, int maxPoint) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.progress.IRunStoreProgress#storePoint(fr.turtlesport.protocol.data.D1009RunType,
   *      int, int)
   */
  public void storePoint(AbstractRunType run, int currentPoint, int maxPoint) {
  }

}
