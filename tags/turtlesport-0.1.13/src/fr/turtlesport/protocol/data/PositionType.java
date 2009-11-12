package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.protocol.GarminProtocolException;

// typedef struct
// {
// sint32 lat; /* latitude in semicircles */
// sint32 lon; /* longitude in semicircles */
// } position_type;

/**
 * @author Denis Apparicio
 * 
 */
public class PositionType extends AbstractData {

  public static final int INVALID = 0x7FFFFFFF;

  private int             latitude;

  private int             longitude;

  /**
   * 
   */
  public PositionType() {
    super();

    this.latitude = INVALID;
    this.longitude = INVALID;
  }

  /**
   * @param latitude
   * @param logitude
   */
  public PositionType(int latitude, int longitude) {
    super();
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * D&eacute;termine si cette position est invalide.
   * 
   * @return <code>true</code> si cette postion est invalide.
   */
  public boolean iPositionInvalid() {
    return (latitude == INVALID && longitude == INVALID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    latitude = input.readInt();
    longitude = input.readInt();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
    output.writeInt(latitude);
    output.writeInt(longitude);
  }

  /**
   * @return the latitude
   */
  public int getLatitude() {
    return latitude;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(int latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the logitude
   */
  public int getLongitude() {
    return longitude;
  }

  /**
   * @param logitude
   *          the logitude to set
   */
  public void setLongitude(int longitude) {
    this.longitude = longitude;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder st = new StringBuilder();
    st.append("latitude=");
    st.append(latitude);
    st.append("; longitude=");
    st.append(longitude);
    return st.toString();
  }

}
