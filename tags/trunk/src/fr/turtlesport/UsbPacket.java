package fr.turtlesport;

import fr.turtlesport.util.ByteUtil;

/**
 * @author Denis Apparicio
 */
public class UsbPacket {

  /** Longueur du packet sans les donnes. */
  private static final int  PACKET_HEADER_SIZE = 12;

  /** Valeur du byte reserve. */
  private static final byte RESERVED           = 0;

  /** byte 0 packet type. */
  private byte              packetType;

  /** packetID. */
  private short             packetID;

  /** Data size. */
  private int               size               = 0;

  /** byte 12+. */
  private byte[]            data;

  /**
   * 
   */
  public UsbPacket() {
    super();
  }

  /**
   * @param packetType
   * @param packetID
   */
  public UsbPacket(byte packetType, short packetID) {
    super();
    this.packetType = packetType;
    this.packetID = packetID;
  }

  /**
   * @param buffer
   */
  public UsbPacket(byte[] buffer) {
    super();
    if (buffer == null || buffer.length < PACKET_HEADER_SIZE) {
      throw new IllegalArgumentException();
    }

    packetType = buffer[0];
    packetID = ByteUtil.toShort(buffer[4], buffer[5]);
    size = ByteUtil.toInt(buffer[8], buffer[9], buffer[10], buffer[11]);

    if (size > 0) {
      data = new byte[size];
      System.arraycopy(buffer, PACKET_HEADER_SIZE, data, 0, size);
    }

  }

  /**
   * Restitue le buffer.
   * 
   * @return le buffer.
   */
  public byte[] makebuffer() {
    byte[] res = new byte[PACKET_HEADER_SIZE + ((size > 0) ? size : 1)];

    /** byte 0 packet type. */
    res[0] = packetType;

    /** byte 1-3 reserved value 0. */
    res[1] = RESERVED;
    res[2] = RESERVED;
    res[3] = RESERVED;

    /** byte 4-5 packetID. */
    res[4] = (byte) packetID;
    res[5] = (byte) (packetID >> 8);

    /** byte 6-7 reserved value 0. */
    res[6] = RESERVED;
    res[7] = RESERVED;

    /** byte 8-11 Data size. */
    res[8] = (byte) size;
    res[9] = (byte) (size >> 8);
    res[10] = (byte) (size >> 16);
    res[11] = (byte) (size >> 24);

    /** byte 12+. */
    if (size > 0 && data != null) {
      System.arraycopy(data, 0, res, PACKET_HEADER_SIZE, data.length);
    }
    else {
      res[12] = 0;
    }

    return res;
  }

  /**
   * Restitue le packet type.
   * 
   * @return le packet type.
   */
  public byte getPacketType() {
    return packetType;
  }

  /**
   * Valorise le packet type.
   * 
   * @param packetType
   *          le packet type.
   */
  public void setPacketType(byte packetType) {
    this.packetType = packetType;
  }

  /**
   * Restitue le packetID.
   * 
   * @return le packetID.
   */
  public short getPacketID() {
    return packetID;
  }

  /**
   * Valorise le packetID.
   * 
   * @param packetID
   *          la nouvelle valeur.
   */
  public void setPacketID(short packetID) {
    this.packetID = packetID;
  }

  /**
   * Restitue la taille des donn&eacute;es.
   * 
   * @return la taille des donn&eacute;es.
   */
  public int getSize() {
    return size;
  }

  /**
   * Valorise la taille des donn&eacute;ees.
   * 
   * @param size
   *          la nouvelle valeur
   */
  public void setSize(int size) {
    if (size < 0) {
      throw new IllegalArgumentException("size " + size);
    }
    this.size = size;
  }

  /**
   * Valorise les donn&eacute;es.
   * 
   * @param data
   *          la nouvelle valeur.
   */
  public void setData(byte[] data) {
    setSize(((data == null) ? 0 : data.length));
    this.data = data;
  }

  /**
   * Valorise les donn&eacute;es.
   * 
   * @param data
   *          la nouvelle valeur.
   */
  public void setData(UsbPacketOutputStream out) {
    setSize(0);
    if (out != null) {
      setData(out.toByteArray());
    }
  }

  /**
   * Restitue les donn&eacute;es.
   * 
   * @return les donn&eacute;es.
   */
  public byte[] getData() {
    return data;
  }

}
