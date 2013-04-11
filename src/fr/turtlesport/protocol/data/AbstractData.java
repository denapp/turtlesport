package fr.turtlesport.protocol.data;

import java.io.IOException;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.protocol.GarminProtocolException;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractData {

  /** Running. */
  public static final int SPORT_TYPE_RUNNING = 0;

  /** Velo. */
  public static final int SPORT_TYPE_BIKE    = 1;

  /** OTHER. */
  public static final int SPORT_TYPE_OTHER   = 2;

  /**
   * Serialisation.
   * 
   * @param output
   * @throws IOException
   */
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
  }

  /**
   * Decodage des donnees.
   * 
   * @param input
   *          l'inputstream du packet.
   */
  public abstract void parse(UsbPacketInputStream input);

  /**
   * Decodage des donnees a partir d'un packet.
   * 
   * @param packet
   *          le packet.
   */
  public void parse(UsbPacket packet) {
    parse(new UsbPacketInputStream(packet));
  }

  /**
   * D&eacute;termine si le sport est de la course &agrve; pied.
   * 
   * @param sportType
   * 
   * @return <code>true</code> si course &agrve; pied.
   */
  public boolean isSportRunning(int sportType) {
    return (sportType == SPORT_TYPE_RUNNING);
  }

  /**
   * D&eacute;termine si le sport est du v&eacute,lo.
   * 
   * @param sportType
   * @return <code>true</code> si le sport est du v&eacute,lo.
   */
  public boolean isSportBike(int sportType) {
    return (sportType == SPORT_TYPE_BIKE);
  }

  /**
   * D&eacute;termine si le sport n'est pas v&eacute,lo ou de la la course
   * &agrve; pied.
   * 
   * @param sportType
   * @return <code>true</code> si n'est pas v&eacute,lo ou de la la course
   *         &agrve; pied.
   */
  public boolean isSportOther(int sportType) {
    return (sportType == SPORT_TYPE_OTHER);
  }

}
