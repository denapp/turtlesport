package fr.turtlesport.geo.garmin.hst;


/**
 * @author Denis Apparicio
 * 
 */
public class History {
  private HistoryFolder running;

  private HistoryFolder biking;

  private HistoryFolder other;

  public History() {
    super();
  }

  /**
   * @return the running
   */
  public HistoryFolder getRunning() {
    return running;
  }

  /**
   * @param running
   *          the running to set
   */
  public void setRunning(HistoryFolder running) {
    this.running = running;
  }

  /**
   * @return the biking
   */
  public HistoryFolder getBiking() {
    return biking;
  }

  /**
   * @param biking
   *          the biking to set
   */
  public void setBiking(HistoryFolder biking) {
    this.biking = biking;
  }

  /**
   * @return the other
   */
  public HistoryFolder getOther() {
    return other;
  }

  /**
   * @param other
   *          the other to set
   */
  public void setOther(HistoryFolder other) {
    this.other = other;
  }

}
