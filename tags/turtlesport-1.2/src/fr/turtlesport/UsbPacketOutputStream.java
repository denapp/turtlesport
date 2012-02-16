package fr.turtlesport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;
import fr.turtlesport.protocol.data.PositionType;
import fr.turtlesport.util.ByteUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class UsbPacketOutputStream {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UsbPacketOutputStream.class);
  }

  /** OutputStream du packet. */
  private ByteArrayOutputStream out;

  /**
   * 
   */
  public UsbPacketOutputStream() {
    out = new ByteArrayOutputStream();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.ByteArrayOutputStream#write(int)
   */
  public synchronized void write(int b) {
    out.write(b);
  }

  /**
   * Ecriture d'un boolean.
   * 
   * @param b
   */
  public void writeBoolean(boolean b) {
    out.write(b ? 1 : 0);
  }

  /**
   * 
   * Ecriture d'une date.
   * 
   * @throws GarminProtocolException
   */
  public void writeTime(Date date) throws GarminProtocolException {
    long elapsed = date.getTime() - UsbPacketInputStream.elpased1989();
    if (elapsed < 0) {
      elapsed = UsbPacketInputStream.elpased1989();
    }
    writeInt((int) elapsed);
  }

  /**
   * @param value
   * @throws IOException
   */
  public synchronized void write(String value, int len) {
    byte[] bytes = null;
    int strLen = 0;
    int i;

    if (value != null) {
      bytes = value.getBytes();
      strLen = (bytes.length > len) ? len : bytes.length;
    }

    for (i = 0; i < strLen; i++) {
      out.write(bytes[i]);
    }
    for (; i < len; i++) {
      out.write(0);
    }
  }

  /**
   * @param value
   * @throws GarminProtocolException
   */
  public synchronized void writeShort(short value) throws GarminProtocolException {
    try {
      out.write(ByteUtil.to2Bytes(value));
    }
    catch (IOException e) {
      log.error("", e);
      throw new GarminProtocolException(e);
    }
  }

  /**
   * @param value
   * @throws GarminProtocolException
   */
  public synchronized void writeShort(int value) throws GarminProtocolException {
    try {
      out.write(ByteUtil.to2Bytes(value));
    }
    catch (IOException e) {
      log.error("", e);
      throw new GarminProtocolException(e);
    }
  }

  /**
   * @param value
   * @throws IOException
   */
  public synchronized void writeUnused() {
    out.write(0);
  }

  /**
   * @param len
   * @throws IOException
   */
  public synchronized void writeUnused(int len) {
    for (int i = 0; i < len; i++) {
      out.write(0);
    }
  }

  /**
   * @param value
   * @throws IOException
   */
  public synchronized void writeUnusedShort() {
    writeUnused(2);
  }

  /**
   * @param value
   * @throws IOException
   */
  public void writeUnusedInt() {
    writeUnused(4);
  }

  /**
   * @param value
   * @throws IOException
   */
  public void writeUnusedFloat() {
    writeUnused(4);
  }

  /**
   * @param value
   * @throws GarminProtocolException
   */
  public void writeInt(int value) throws GarminProtocolException {
    try {
      out.write(ByteUtil.to4Bytes(value));
    }
    catch (IOException e) {
      log.error("", e);
      throw new GarminProtocolException(e);
    }
  }

  /**
   * @param value
   * @throws GarminProtocolException
   */
  public void writeFloat(float value) throws GarminProtocolException {
    try {
      out.write(ByteUtil.to4Bytes(value));
    }
    catch (IOException e) {
      log.error("", e);
      throw new GarminProtocolException(e);
    }
  }

  /**
   * @param pType
   * @throws GarminProtocolException
   */
  public void writePositionType(PositionType pType) throws GarminProtocolException {
    if (pType == null) {
      throw new IllegalArgumentException();
    }
    pType.serialize(this);
  }

  /**
   * @return
   */
  public byte[] toByteArray() {
    return out.toByteArray();
  }

}
