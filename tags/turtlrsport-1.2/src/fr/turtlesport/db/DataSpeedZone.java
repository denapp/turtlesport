package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataSpeedZone {

  /** Nom de la zone. */
  private String name;

  /** Vitesse basse. */
  private float  lowSpeed;

  /** Vitesse haute. */
  private float  highSpeed;

  /** Unite. */
  private String unit;

  /**
   * Construit une zone de vitesse.
   */
  public DataSpeedZone() {
    super();
    lowSpeed = 0;
    highSpeed = 0;
  }

  /**
   * Construit une zone de vitesse.
   * 
   * @param name
   *          nom de la zone de vitesse.
   * @param lowSpeed
   *          vitesse basse.
   * @param highSpeed
   *          vitesse haute.
   * @param unit
   *          unit&eacute; de vitesse.
   * @throws IllegalArgumentException
   */
  public DataSpeedZone(String name, float lowSpeed, float highSpeed, String unit) {
    super();

    if (lowSpeed < 0) {
      throw new IllegalArgumentException("lowSpeed=" + lowSpeed);
    }
    if (highSpeed < 0) {
      throw new IllegalArgumentException("highSpeed=" + lowSpeed);
    }
    this.name = name;
    this.lowSpeed = lowSpeed;
    this.highSpeed = highSpeed;
    this.unit = unit;
  }

  /**
   * Restitue l'unit&eacute; de vitesse.
   * 
   * @return l'unit&eacute; de vitesse.
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Valorise l'unit&eacute; de vitesse.
   * 
   * @param unit
   *          la nouvelle valeur.
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * Restitue la vitesse maximale.
   * 
   * @return la vitesse maximale.
   */
  public float getHighSpeed() {
    return highSpeed;
  }

  /**
   * Valorise la vitesse maximale.
   * 
   * @param highSpeed
   *          la nouvelle valeur.
   */
  public void setHighSpeed(float highSpeed) {
    this.highSpeed = highSpeed;
  }

  /**
   * Restitue la vitesse minimale.
   * 
   * @return la vitesse minimale.
   */
  public float getLowSpeed() {
    return lowSpeed;
  }

  /**
   * Valorise la vitesse minimale.
   * 
   * @param lowSpeed
   *          la vitesse minimale.
   */
  public void setLowSpeed(float lowSpeed) {
    this.lowSpeed = lowSpeed;
  }

  /**
   * Restitue le nom de la zone.
   * 
   * @return le nom de la zone.
   */
  public String getName() {
    return name;
  }

  /**
   * Valorise le nom de la zone.
   * 
   * @param le
   *          nom de la zone.
   */
  public void setName(String name) {
    this.name = name;
  }

}
