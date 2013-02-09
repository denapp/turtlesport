package fr.turtlesport.geo.suunto;

import fr.turtlesport.geo.GeoPositionWithAlt;

/**
 * @author Denis Apparicio
 * 
 */
public class SuuntoPoint extends GeoPositionWithAlt {

  private double energyConsumption = 0;

  public SuuntoPoint() {
    super();
  }

  /**
   * @param latitude
   * @param longitude
   * @param elevation
   */
  public SuuntoPoint(double latitude, double longitude, double elevation) {
    super(latitude, longitude, elevation);
  }

  /**
   * @param latitude
   * @param longitude
   */
  public SuuntoPoint(double latitude, double longitude) {
    super(latitude, longitude);
  }

  public double getEnergyConsumption() {
    return energyConsumption;
  }

  /**
   * Energy en kj par seconde ( /4184 pour conversion en calories)
   * @param energyConsumption
   */
  public void setEnergyConsumption(double energyConsumption) {
    this.energyConsumption = energyConsumption;
  }

}
