package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

// typedef struct
// {
// uint16 index; /* unique among all tracks received from device
// */
// } D311_Trk_Hdr_Type;
/**
 * @author Denis Apparicio
 * 
 */
public class D311TrkHdrType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D311TrkHdrType.class);
  }

  /** Index. */
  private int                 index;

  /**
   * 
   */
  public D311TrkHdrType() {
    super();
  }

  /**
   * @param index
   */
  public D311TrkHdrType(int index) {
    super();
    this.index = index;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    index = input.readShort();

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

    output.writeShort(index);

    log.debug("<<serialize");
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  /**
   * @param index
   *          the index to set
   */
  public void setIndex(int index) {
    this.index = index;
  }

}
