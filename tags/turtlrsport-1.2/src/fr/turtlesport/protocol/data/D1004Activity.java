package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

/**
 * @author Denis Apparicio
 * 
 */
public class D1004Activity extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1004Activity.class);
  }

  /** Zones FC. */
  private D1004RateZone[]     rateZones;

  /** Zones Vitesses. */
  private D1004SpeedZone[]    speedZones;

  /** Poids de l'equipement. */
  private float               gearWeight;

  /** FCMax */
  private int                 maxHeartRate;

  /**
   * 
   */
  public D1004Activity() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    // Zones FC
    rateZones = new D1004RateZone[5];
    for (int i = 0; i < rateZones.length; i++) {
      rateZones[i] = new D1004RateZone();
      rateZones[i].parse(input);
    }

    // Zones vitesse
    speedZones = new D1004SpeedZone[10];
    for (int i = 0; i < speedZones.length; i++) {
      speedZones[i] = new D1004SpeedZone();
      speedZones[i].parse(input);
    }

    gearWeight = input.readFloat();
    maxHeartRate = input.read();

    // uint8 unused1;
    // uint16 unused2;
    input.readByte();
    input.readShort();

    log.debug("<<parse");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
    log.debug(">>serialize");

    // Zones FC
    for (int i = 0; i < rateZones.length; i++) {
      rateZones[i].serialize(output);
    }

    // Zones vitesse
    for (int i = 0; i < speedZones.length; i++) {
      speedZones[i].serialize(output);
    }

    output.writeFloat(gearWeight);
    output.write(maxHeartRate);

    // uint8
    // uint16
    output.writeUnused(3);
    output.writeUnusedShort();

    log.debug("<<serialize");
  }

  /**
   * Restitue le poids de l'equipement.
   * 
   * @return le poids de l'equipement.
   */
  public float getGearWeight() {
    return gearWeight;
  }

  /**
   * Valorise le poids de l'equipement.
   * 
   * @param gearWeight
   *          la nouvelle valeur.
   */
  public void setGearWeight(float gearWeight) {
    this.gearWeight = gearWeight;
  }

  /**
   * Restitue la FCMax.
   * 
   * @return la FCMax.
   */
  public int getMaxHeartRate() {
    return maxHeartRate;
  }

  /**
   * Valorise la FCMax.
   * 
   * @param maxHeartRate
   *          la FCMax.
   */
  public void setMaxHeartRate(int maxHeartRate) {
    this.maxHeartRate = maxHeartRate;
  }

  /**
   * Restitue les zones cardiaques.
   * 
   * @return les zones cardiaques.
   */
  public D1004RateZone[] getRateZones() {
    return rateZones;
  }

  /**
   * Restitue les zones de vitesse.
   * 
   * @return les zones de vitesse.
   */
  public D1004SpeedZone[] getSpeedZones() {
    return speedZones;
  }

}
