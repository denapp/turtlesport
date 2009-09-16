package fr.turtlesport.util;

import java.nio.ByteBuffer;

/**
 * @author Denis Apparicio
 */
public final class ByteUtil {

  private static ByteBuffer byteBufferFloat  = ByteBuffer.allocate(4);

  private static ByteBuffer byteBufferDouble = ByteBuffer.allocate(8);

  /**
   * 
   */
  private ByteUtil() {
  }

  /**
   * Convertion d'un int en 2 bytes.
   * 
   * @param value
   * 
   * @return o le tableau de bytes.
   */
  public static byte[] to2Bytes(int value) {
    byte[] bytes = new byte[2];
    bytes[0] = (byte) (value & 0x00FF);
    // bytes[1] = (byte) ((value & 0xFF00) >> 8);
    bytes[1] = (byte) ((value >> 8) & 0x00FF);
    return bytes;
  }

  /**
   * Convertion d'un short en 2 bytes.
   * 
   * @param value
   * 
   * @return o le tableau de bytes.
   */
  public static byte[] to2Bytes(short value) {
    byte[] bytes = new byte[2];
    bytes[0] = (byte) (value & 0x00FF);
    // bytes[1] = (byte) ((value & 0xFF00) >> 8);
    bytes[1] = (byte) ((value >> 8) & 0x00FF);
    return bytes;
  }

  /**
   * Convertion d'un short en 2 bytes.
   * 
   * @param value
   * 
   * @return o le tableau de bytes.
   */
  public static synchronized byte[] to4Bytes(float value) {
    byteBufferFloat.position(0);
    byteBufferFloat.putFloat(value);
    byte[] tmp = byteBufferFloat.array();
    byte[] res = new byte[4];

    for (int i = 0; i < 4; i++) {
      res[i] = tmp[3 - i];
    }

    return res;
  }

  /**
   * Convertion d'un short en 2 bytes.
   * 
   * @param value
   * 
   * @return o le tableau de bytes.
   */
  public static synchronized byte[] to8Bytes(double value) {

    byteBufferDouble.position(0);
    byteBufferDouble.putDouble(value);
    byte[] tmp = byteBufferDouble.array();
    byte[] res = new byte[8];

    for (int i = 0; i < res.length; i++) {
      res[i] = tmp[7 - i];
    }

    return res;
  }

  /**
   * Convertion d'un int en 4 bytes.
   * 
   * @param value
   * 
   * @return o le tableau de bytes.
   */
  public static byte[] to4Bytes(int value) {
    byte[] result = new byte[4];
    for (int i = 0; i < result.length; i++) {
      result[i] = (byte) (value & 0xFF);
      value = value >> 8;
    }
    return result;
  }

  /**
   * Convert 2 bytes into a short.
   * <p>
   * This converts the 2 bytes into a short. The msb will be the high byte (8
   * bits) of the short, and the lsb will be the low byte (8 bits) of the short.
   * 
   * @param msb
   *          The Most Significant Byte.
   * @param lsb
   *          The Least Significant Byte.
   * @return A short representing the bytes.
   */
  public static short toShort(byte lsb, byte msb) {
    return (short) ((0xff00 & (short) (msb << 8)) | (0x00ff & lsb));
  }

  /**
   * Convert 2 shorts into an int.
   * <p>
   * This converts the 2 shorts into an int.
   * 
   * @param mss
   *          The Most Significant Short.
   * @param lss
   *          The Least Significant Short.
   * @return An int representing the shorts.
   */
  public static int toInt(short mss, short lss) {
    return ((0xffff0000 & (int) (mss << 16)) | (0x0000ffff & (int) lss));
  }

  /**
   * Convert 4 bytes into an int.
   * <p>
   * This converts the 4 bytes into an int.
   * 
   * @param byte3
   *          The byte to be left-shifted 24 bits.
   * @param byte2
   *          The byte to be left-shifted 16 bits.
   * @param byte1
   *          The byte to be left-shifted 8 bits.
   * @param byte0
   *          The byte that will not be left-shifted.
   * @return An int representing the bytes.
   */
  public static int toInt(byte byte0, byte byte1, byte byte2, byte byte3) {
    return toInt(toShort(byte2, byte3), toShort(byte0, byte1));
  }

  /**
   * Convert 4 bytes into float.
   * <p>
   * Format is IEEE 754 32-bit single precision
   * 
   * @param byte1
   * @param byte2
   * @param byte3
   * @param byte4
   * @return float.
   */
  public static float toFloat(byte byte1, byte byte2, byte byte3, byte byte4) {
    int acum;
    acum = (byte1 & 0xff);
    acum |= (byte2 & 0xff) << 8;
    acum |= (byte3 & 0xff) << 16;
    acum |= (byte4 & 0xff) << 24;
    return Float.intBitsToFloat(acum);
  }

  /**
   * Convert 4 bytes into float.
   * <p>
   * Format is IEEE 754 32-bit single precision
   * 
   * @param byte1
   * @param byte2
   * @param byte3
   * @param byte4
   * @return float.
   */
  public static double toDouble(byte byte1,
                                byte byte2,
                                byte byte3,
                                byte byte4,
                                byte byte5,
                                byte byte6,
                                byte byte7,
                                byte byte8) {
    int acum;
    acum = (byte1 & 0xff);
    acum |= (byte2 & 0xff) << 8;
    acum |= (byte3 & 0xff) << 16;
    acum |= (byte4 & 0xff) << 24;
    return Float.intBitsToFloat(acum);
  }

}
