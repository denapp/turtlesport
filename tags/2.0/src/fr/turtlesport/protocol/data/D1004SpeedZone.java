package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

// struct
// {
// float32 low_speed; /* In meters-per-second */
// float32 high_speed; /* In meters-per-second */
// char name[16]; /* Null-terminated speed-zone name */
// } speed_zones[10];

/**
 * @author Denis Apparicio
 * 
 */
public class D1004SpeedZone extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1004SpeedZone.class);
  }

  /** Vitesse basse. */
  private float               lowSpeed;

  /** Vitesse haute. */
  private float               highSpeed;

  /** Nom de la zone. */
  private String              name;

  /**
   * 
   */
  public D1004SpeedZone() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream in) {
    log.debug(">>parse");

    lowSpeed = in.readFloat();
    highSpeed = in.readFloat();
    name = in.readString(16);

    log.debug("<<parse");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream out) throws GarminProtocolException {
    log.debug(">>serialize");

    out.writeFloat(lowSpeed);
    out.writeFloat(highSpeed);
    out.write(name, 16);

    log.debug("<<serialize");

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
