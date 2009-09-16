package fr.turtlesport.db.progress;

import fr.turtlesport.protocol.data.D1009RunType;

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
  public void beginStore(D1009RunType run) {

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
  public void endStore(D1009RunType run) {
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
  public void storePoint(D1009RunType run, int currentPoint, int maxPoint) {
  }

}
