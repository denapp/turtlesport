package fr.turtlesport.geo.suunto;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Denis Apparicio
 * 
 */
public class SuuntoInputStream extends FilterInputStream {

  private int    readLen   = 0;

  private int    readEnd   = -1;

  private int    len1      = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                               .getBytes().length;

  private byte[] rootBegin = "<turtle>".getBytes();

  private byte[] rootEnd   = "</turtle>".getBytes();

  protected SuuntoInputStream(InputStream in) {
    super(in);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    for (int i = 0; i < len; i++) {
      int value = read();
      if (value != -1) {
        b[i] = (byte) value;
      }
      else {
        return (i == 0) ? -1 : i;
      }
    }
    return len;
  }

  @Override
  public int read() throws IOException {
    // Lecture de <?xml version="1.0" encoding="utf-8"?>
    if (readLen < len1) {
      readLen++;
      return super.read();
    }

    // Lecture de la racine
    if (readLen < (len1 + rootBegin.length)) {
      return rootBegin[readLen++ - len1];
    }

    // Lecture du buffer
    int b = super.read();
    if (b != -1) {
      return b;
    }

    // Lecture de la racine fin
    if (readEnd < (rootEnd.length - 1)) {
      return rootEnd[++readEnd];
    }

    return -1;
  }

}
