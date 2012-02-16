package fr.turtlesport.unit.event;

/**
 * @author Denis Apparicio
 * 
 */
public class UnitEvent {

  /** Les types. */
  protected static final int DISTANCE    = 1;

  protected static final int PACE        = 2;

  protected static final int SPEED       = 3;

  protected static final int SPEED_PACE  = 4;

  protected static final int WEIGHT      = 5;

  protected static final int HEIGHT      = 6;

  protected static final int TEMPERATURE = 7;

  /** Le type. */
  private int                type;

  /** La nouvelle unit&eacute;. */
  private String             unit;

  /**
   * @param locale
   */
  protected UnitEvent(int type, String unit) {
    super();

    if (type < DISTANCE && type > HEIGHT) {
      throw new IllegalArgumentException("type=" + type);
    }
    if (unit == null || "".equals(unit)) {
      throw new IllegalArgumentException("unit");
    }

    this.type = type;
    this.unit = unit;
  }

  /**
   * Restitue le type.
   * 
   * @return le type.
   */
  public int getType() {
    return type;
  }

  /**
   * Restitue la nouvelle unit&eacute;.
   * 
   * @return la nouvelle unit&eacute;.
   */
  public String getUnit() {
    return unit;
  }

  /**
   * D&eacute;termine si type distance.
   * 
   * @return <code>true</code> si type distance, <code>false</code> sinon.
   */
  public boolean isEventDistance() {
    return (type == DISTANCE);
  }

  /**
   * D&eacute;termine si type allure.
   * 
   * @return <code>true</code> si type allure, <code>false</code> sinon.
   */
  public boolean isEventPace() {
    return (type == PACE);
  }

  /**
   * D&eacute;termine si type vitesse et allure.
   * 
   * @return <code>true</code> si type vitesse, <code>false</code> sinon.
   */
  public boolean isEventSpeedAndPace() {
    return (type == SPEED_PACE);
  }

  /**
   * D&eacute;termine si type vitesse.
   * 
   * @return <code>true</code> si type vitesse, <code>false</code> sinon.
   */
  public boolean isEventSpeed() {
    return (type == SPEED);
  }

  /**
   * D&eacute;termine si type poids.
   * 
   * @return <code>true</code> si type poids, <code>false</code> sinon.
   */
  public boolean isEventWeight() {
    return (type == WEIGHT);
  }

  /**
   * D&eacute;termine si type hauteur.
   * 
   * @return <code>true</code> si type hauteur, <code>false</code> sinon.
   */
  public boolean isEventHeight() {
    return (type == HEIGHT);
  }

  /**
   * D&eacute;termine si type temp&eacute;rature.
   * 
   * @return <code>true</code> si type temp&eacute;rature, <code>false</code> sinon.
   */
  public boolean isEventTemperature() {
    return (type == TEMPERATURE);
  }

}
