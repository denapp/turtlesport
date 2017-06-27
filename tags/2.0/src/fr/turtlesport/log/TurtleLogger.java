package fr.turtlesport.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Denis Apparicio
 */
public class TurtleLogger extends Logger {

  // It's enough to instantiate a factory once and for all.
  private static TurtleLoggerFactory myFactory = new TurtleLoggerFactory();

  /**
   * Just calls the parent constuctor.
   */
  public TurtleLogger(String name) {
    super(name);
  }

  /**
   * This method overrides {@link Logger#getLogger}by supplying its own factory
   * type as a parameter.
   */
  public static Logger getLogger(String name) {
    return Logger.getLogger(name, myFactory);
  }

  /**
   * This method overrides {@link Logger#getLogger}by supplying its own factory
   * type as a parameter.
   */
  public static Logger getLogger(Class clazz) {
    return getLogger(clazz.getName());
  }

  /**
   * Trace le tableau de bytes de niveau information sur la sortie
   * <code>out</code> (un &eacute;lement par ligne).
   * 
   * @param bytes
   *          tableau de bytes &agrave; tracer.
   * @param nom
   *          nom du tableau &agrave;tracer.
   */
  public void debug(byte[] bytes, String nom) {
    if (bytes == null) {
      return;
    }

    if (isDebugEnabled()) {

      StringBuilder st = new StringBuilder(nom);
      st.append(' ');
      st.append(bytes.length);
      st.append(" bytes");
      debug(st.toString());

      st = new StringBuilder(nom);
      st.append(' ');
      for (int i = 0; i < bytes.length; i++) {
        if ((i != 0) && (i % 16 == 0)) {
          debug(st.toString());
          st = new StringBuilder(nom);
          st.append(' ');
        }
        st.append(toHexString(bytes[i]));
        st.append(' ');
      }

      debug(st.toString());
    }
  }

  /**
   * Trace le tableau de bytes de niveau information sur la sortie
   * <code>out</code> (un &eacute;l&eacute;ment par ligne).
   * 
   * @param bytes
   *          tableau de bytes &agrave;tracer.
   * @param nom
   *          nom du tableau &agrave; tracer.
   */
  public void error(byte[] bytes, String nom) {
    if (bytes == null) {
      return;
    }

    if (isEnabledFor(Level.ERROR)) {

      StringBuilder st = new StringBuilder(nom);
      st.append(' ');
      st.append(bytes.length);
      st.append(" bytes");
      error(st.toString());

      st = new StringBuilder(nom);
      st.append(' ');
      for (int i = 0; i < bytes.length; i++) {
        if ((i != 0) && (i % 16 == 0)) {
          error(st.toString());
          st = new StringBuilder(nom);
          st.append(' ');
        }
        st.append(toHexString(bytes[i]));
        st.append(' ');
      }

      error(st.toString());
    }
  }

  /**
   * Trace le tableau de bytes de niveau information sur la sortie
   * <code>out</code> (un &eacute;l&eacute;ment par ligne).
   * 
   * @param bytes
   *          tableau de bytes &agrave; tracer.
   * @param nom
   *          nom du tableau &agrave; tracer.
   */
  public void fatal(byte[] bytes, String nom) {
    if (bytes == null) {
      return;
    }

    if (isEnabledFor(Level.FATAL)) {

      StringBuilder st = new StringBuilder(nom);
      st.append(' ');
      st.append(bytes.length);
      st.append(" bytes");
      fatal(st.toString());

      st = new StringBuilder(nom);
      st.append(' ');
      for (int i = 0; i < bytes.length; i++) {
        if ((i != 0) && (i % 16 == 0)) {
          fatal(st.toString());
          st = new StringBuilder(nom);
          st.append(' ');
        }
        st.append(toHexString(bytes[i]));
        st.append(' ');
      }

      fatal(st.toString());
    }
  }

  /**
   * Trace le tableau de bytes de niveau information sur la sortie
   * <code>out</code> (un &eacute;l&eacute;ment par ligne).
   * 
   * @param bytes
   *          tableau de bytes &agrave; tracer.
   * @param nom
   *          nom du tableau &agrave; tracer.
   */
  public void info(byte[] bytes, String nom) {
    if (bytes == null) {
      return;
    }

    if (isInfoEnabled()) {
      StringBuilder st = new StringBuilder(nom);
      st.append(' ');
      st.append(bytes.length);
      st.append(" bytes");
      info(st.toString());

      st = new StringBuilder(nom);
      st.append(' ');
      for (int i = 0; i < bytes.length; i++) {
        if ((i != 0) && (i % 16 == 0)) {
          info(st.toString());
          st = new StringBuilder(nom);
          st.append(' ');
        }
        st.append(toHexString(bytes[i]));
        st.append(' ');
      }

      info(st.toString());
    }
  }

  /**
   * Trace le tableau de bytes de niveau information sur la sortie
   * <code>out</code> (un &eacute;l&eacute;ment par ligne).
   * 
   * @param bytes
   *          tableau de bytes &agrave; tracer.
   * @param nom
   *          nom du tableau &agrave; tracer.
   */
  public void warn(byte[] bytes, String nom) {
    if (bytes == null) {
      return;
    }

    if (isEnabledFor(Level.WARN)) {
      StringBuilder st = new StringBuilder(nom);
      st.append(' ');
      st.append(bytes.length);
      st.append(" bytes");
      warn(st.toString());

      st = new StringBuilder(nom);
      st.append(' ');
      for (int i = 0; i < bytes.length; i++) {
        if ((i != 0) && (i % 16 == 0)) {
          warn(st.toString());
          st = new StringBuilder(nom);
          st.append(' ');
        }
        st.append(toHexString(bytes[i]));
        st.append(' ');
      }

      warn(st.toString());
    }
  }

  private String toHexString(byte b) {
    StringBuilder st = new StringBuilder();
    String init = Integer.toHexString((b) & 0xFF).toUpperCase();
    if (init.length() == 1) {
      st.append("0");
    }
    st.append(init);
    return st.toString();
  }

}
