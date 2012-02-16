package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataHeartZone {

  /** FC base . */
  private int lowHeartRate;

  /** FC haute */
  private int highHeartRate;

  /**
   * Construit une zone cardiaque.
   */
  public DataHeartZone() {
    super();
    lowHeartRate = 0;
    highHeartRate = 0;
  }

  /**
   * Construit une zone cardiaque.
   * 
   * @param lowHeartRate
   *          FC basse.
   * @param highHeartRate
   *          FC haute.
   * @throws IllegalArgumentException
   */
  public DataHeartZone(int lowHeartRate, int highHeartRate) {
    super();
    if (lowHeartRate < 0) {
      throw new IllegalArgumentException("lowHeartRate=" + lowHeartRate);
    }
    if (highHeartRate < 0) {
      throw new IllegalArgumentException("highHeartRate=" + highHeartRate);
    }
    this.lowHeartRate = lowHeartRate;
    this.highHeartRate = highHeartRate;
  }

  /**
   * Valorise la FC haute.
   * 
   * @param highHeartRate
   *          la nouvelle valeur.
   */
  public void setHighHeartRate(int highHeartRate) {
    this.highHeartRate = highHeartRate;
  }

  /**
   * Restitue la FC haute.
   * 
   * @return la FC haute.
   */
  public int getHighHeartRate() {
    return highHeartRate;
  }

  /**
   * Restitue la FC basse.
   * 
   * @return la FC basse.
   */
  public int getLowHeartRate() {
    return lowHeartRate;
  }

  /**
   * Valorise la FC basse.
   * 
   * @param lowHeartRate
   *          la nouvelle valeur.
   */
  public void setLowHeartRate(int lowHeartRate) {
    this.lowHeartRate = lowHeartRate;
  }

}
