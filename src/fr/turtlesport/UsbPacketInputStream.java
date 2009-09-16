package fr.turtlesport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.PositionType;
import fr.turtlesport.util.ByteUtil;

/**
 * 
 * @author Denis Apparicio
 * 
 * 
 * 
 */

public class UsbPacketInputStream extends InputStream {

  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UsbPacketInputStream.class);
  }

  /** Temps ecoule depuis le 12 am December 31, 1989 UTC. */
  private static Date          date1989;

  private static long          elapsed1989;
  static {
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getDefault());
    //cal.set(1989, 11, 30, 23, 0, 0);
    cal.set(1989, 11, 31, 0, 0, 0);
    date1989 = cal.getTime();
    elapsed1989 = date1989.getTime();
  }

  /** InputStream du packet. */
  private ByteArrayInputStream in;

  /**
   * 
   * @param packet
   */
  public UsbPacketInputStream(UsbPacket packet) {
    in = new ByteArrayInputStream(packet.getData());
  }

  /**
   * 
   * @param bytes
   * 
   */
  public UsbPacketInputStream(byte[] bytes) {
    in = new ByteArrayInputStream(bytes);
  }

  /**
   * Restitue la date de r&eacute;f&eacute;rence.
   * 
   * @return la date de r&eacute;f&eacute;rence.
   */
  public static Date date1989() {
    return date1989;
  }

  /**
   * Temps &eacute;coul&eacute; depuis la date de r&eacute;f&eacute;rence.
   * 
   * @return le temps &eacute;coul&eacute; depuis la date de
   *         r&eacute;f&eacute;rence.
   */
  public static long elpased1989() {
    return elapsed1989;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.InputStream#available()
   */
  @Override
  public int available() {
    int res = in.available();
    log.debug("available=" + res);
    return res;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.InputStream#read()
   */
  @Override
  public int read() {
    return in.read();
  }

  /**
   * 
   * Lecture.
   * 
   */
  public void readUnused() {
    read();
  }

  /**
   * 
   * Lecture.
   * 
   */
  public void readUnused(int len) {
    for (int i = 0; i < len; i++) {
      read();
    }
  }

  /**
   * 
   * Lecture d'un short.
   * 
   * @return
   */
  public void readUnusedShort() {
    readUnused(2);
  }

  /**
   * 
   * Lecture.
   * 
   */
  public void readUnusedInt() {
    readUnused(4);
  }

  /**
   * 
   * Lecture.
   * 
   */
  public void readUnusedFloat() {
    readUnused(4);
  }

  /**
   * Lecture d'un byte.
   * 
   * @return
   */

  public byte readByte() {
    return (byte) in.read();
  }

  /**
   * 
   * Lecture d'un boolean.
   * 
   * @return
   */
  public boolean readBoolean() {
    return (in.read() != 0);
  }

  /**
   * 
   * Lecture d'une chaine.
   * 
   * @return
   */

  public String readString() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte b;
    while ((b = readByte()) != 0) {
      out.write(b);
    }
    return new String(out.toByteArray());
  }

  /**
   * 
   * Lecture d'une chaine.
   * 
   * @return
   * 
   */
  public String readString(int len) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    int b;
    int i;
    for (i = 0; i < (len - 1); i++) {
      b = read();
      if (b == 0) {
        break;
      }
      out.write(b);
    }

    for (; i < (len - 1); i++) {
      read();
    }

    return new String(out.toByteArray());
  }

  /**
   * 
   * Lecture d'une date.
   * 
   * @return
   */
  public Date readTime() {
    // Temps ecoule
    int time = ByteUtil.toInt(readByte(), readByte(), readByte(), readByte());
    GregorianCalendar cal = new GregorianCalendar();

    cal.setTimeZone(TimeZone.getDefault());
    cal.setTime(date1989);
    cal.add(Calendar.SECOND, time);

    cal.add(Calendar.MILLISECOND, TimeZone.getDefault().getOffset(cal
        .getTimeInMillis()));

    Date date = cal.getTime();
    return date;
  }

  /**
   * 
   * Lecture d'un short.
   * 
   * @return
   * 
   */
  public short readShort() {
    return ByteUtil.toShort(readByte(), readByte());
  }

  /**
   * 
   * Lecture d'un float.
   * 
   * @return
   */
  public float readFloat() {
    return ByteUtil.toFloat(readByte(), readByte(), readByte(), readByte());
  }

  /**
   * 
   * Lecture d'un int.
   * 
   * @return
   */
  public int readInt() {
    return ByteUtil.toInt(readByte(), readByte(), readByte(), readByte());
  }

  /**
   * 
   * Lecture d'un <code>PositionType</code>.
   * 
   * @return
   */
  public PositionType readPositionType() {
    PositionType p = new PositionType();
    p.parse(this);
    return p;
  }

}
