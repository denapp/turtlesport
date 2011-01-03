package fr.turtlesport.protocol.data;

import fr.turtlesport.util.ByteUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class ProtocolDataType {

  /** Tag pour Physical protocol ID */
  private static final char TAG_PHYS_PROT_ID = 'P';

  /** Tag pour Link protocol ID */
  private static final char TAG_LINK_PROT_ID = 'L';

  /** Tag pour Application protocol ID */
  private static final char TAG_APPL_PROT_ID = 'A';

  /** Tag pour Data Type ID */
  private static final char TAG_DATA_TYPE_ID = 'D';

  /** Tag */
  private char              tag;

  /** Data */
  private int             data;

  /**
   * 
   * @param tag
   * @param data1
   * @param data2
   */
  protected ProtocolDataType(byte tag, byte data1, byte data2) {
    this.tag = (char) (tag & 0xff);
    data = ByteUtil.toShort(data1, data2);
  }

  /**
   * Determine si le tag est un Physical protocol ID.
   * 
   * @return <code>true</code> si le tag est un Physical protocol ID,
   *         <code>false</code> sinon.
   */
  public boolean isTagP() {
    return (tag == TAG_PHYS_PROT_ID);
  }

  /**
   * Determine si le tag est un Link protocol ID.
   * 
   * @return <code>true</code> si le tag est Link protocol ID,
   *         <code>false</code> sinon.
   */
  public boolean isTagL() {
    return (tag == TAG_LINK_PROT_ID);
  }

  /**
   * Determine si le tag est un application protocol ID.
   * 
   * @return <code>true</code> si le tag est un application protocol ID,
   *         <code>false</code> sinon.
   */
  public boolean isTagA() {
    return (tag == TAG_APPL_PROT_ID);
  }

  /**
   * Determine si le tag est un Data Type ID.
   * 
   * @return <code>true</code> si le tag est un Data Type ID,
   *         <code>false</code> sinon.
   */
  public boolean isTagD() {
    return (tag == TAG_DATA_TYPE_ID);
  }

  /**
   * Restitue le tag.
   * 
   * @return le tag.
   */
  public char getTag() {
    return tag;
  }

  /**
   * Restitue le product ID.
   * 
   * @return le product ID.
   */
  public int getData() {
    return data;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof ProtocolDataType)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    
    ProtocolDataType pdt = (ProtocolDataType) obj;
    return ((pdt.data == data) && (pdt.tag == tag));  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return tag + data;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuilder st = new StringBuilder();
    st.append(getTag());
    st.append(getData());
    return st.toString();
  }

}
