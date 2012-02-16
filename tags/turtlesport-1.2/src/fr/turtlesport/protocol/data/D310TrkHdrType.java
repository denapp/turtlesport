package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// bool dspl; /* display on the map? */
// uint8 color; /* color (same as D108) */
// /* char trk_ident[]; null-terminated string */
// } D310_Trk_Hdr_Type;
/**
 * @author Denis Apparicio
 * 
 */
public class D310TrkHdrType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D310TrkHdrType.class);
  }

  // Constantes pour les couleurs.
  private static final int    COLOR_BLACK         = 0;

  private static final int    COLOR_DARK_RED      = 1;

  private static final int    COLOR_DARK_GREEN    = 2;

  private static final int    COLOR_DARK_YELLOW   = 3;

  private static final int    COLOR_DARK_BLUE     = 4;

  private static final int    COLOR_DARK_MAGENTA  = 5;

  private static final int    COLOR_DARK_CYAN     = 6;

  private static final int    COLOR_LIGHT_GRAY    = 7;

  private static final int    COLOR_DARK_GRAY     = 8;

  private static final int    COLOR_RED           = 9;

  private static final int    COLOR_GREEN         = 10;

  private static final int    COLOR_YELLOW        = 11;

  private static final int    COLOR_BLUE          = 12;

  private static final int    COLOR_MAGENTA       = 13;

  private static final int    COLOR_CYAN          = 14;

  private static final int    COLOR_WHITE         = 15;

  private static final int    COLOR_TRANSPARENT   = 16;

  private static final int    COLOR_DEFAULT_COLOR = 255;

  /** Determine si visible sur la map. */
  private boolean             isDspl;

  /** Couleur. */
  private int                 color;

  /** Identifiant. */
  private String              trkIdent;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    isDspl = input.readBoolean();
    color = input.read();
    trkIdent = input.readString();

    log.debug("<<decode");
  }

  /**
   * @return the color
   */
  public int getColor() {
    return color;
  }

  /**
   * @param color
   *          the color to set
   */
  public void setColor(int color) {
    this.color = color;
  }

  /**
   * @return the isDspl
   */
  public boolean isDspl() {
    return isDspl;
  }

  /**
   * @param isDspl
   *          the isDspl to set
   */
  public void setDspl(boolean isDspl) {
    this.isDspl = isDspl;
  }

  /**
   * @return the trkIdent
   */
  public String getTrkIdent() {
    return trkIdent;
  }

  /**
   * @param trkIdent
   *          the trkIdent to set
   */
  public void setTrkIdent(String trkIdent) {
    this.trkIdent = trkIdent;
  }

  /**
   * @return
   */
  public boolean isColorBlack() {
    return (getColor() == COLOR_BLACK);
  }

  /**
   * @return
   */
  public boolean isColorDarkRed() {
    return (getColor() == COLOR_DARK_RED);
  }

  /**
   * @return
   */
  public boolean isColorDarkGreen() {
    return (getColor() == COLOR_DARK_GREEN);
  }

  /**
   * @return
   */
  public boolean isColorDarkYellow() {
    return (getColor() == COLOR_DARK_YELLOW);
  }

  /**
   * @return
   */
  public boolean isColorDarkBlue() {
    return (getColor() == COLOR_DARK_BLUE);
  }

  /**
   * @return
   */
  public boolean isColorDarkMagenta() {
    return (getColor() == COLOR_DARK_MAGENTA);
  }

  /**
   * @return
   */
  public boolean isColorDarkCyan() {
    return (getColor() == COLOR_DARK_CYAN);
  }

  /**
   * @return
   */
  public boolean isColorLightGray() {
    return (getColor() == COLOR_LIGHT_GRAY);
  }

  /**
   * @return
   */
  public boolean isColorDarkGray() {
    return (getColor() == COLOR_DARK_GRAY);
  }

  /**
   * @return
   */
  public boolean isColorRed() {
    return (getColor() == COLOR_RED);
  }

  /**
   * @return
   */
  public boolean isColorGreen() {
    return (getColor() == COLOR_GREEN);
  }

  /**
   * @return
   */
  public boolean isColorYellow() {
    return (getColor() == COLOR_YELLOW);
  }

  /**
   * @return
   */
  public boolean isColorBlue() {
    return (getColor() == COLOR_BLUE);
  }

  /**
   * @return
   */
  public boolean isColorMagenta() {
    return (getColor() == COLOR_MAGENTA);
  }

  /**
   * @return
   */
  public boolean isColorCyan() {
    return (getColor() == COLOR_CYAN);
  }

  /**
   * @return
   */
  public boolean isColorWhite() {
    return (getColor() == COLOR_WHITE);
  }

  /**
   * @return
   */
  public boolean isColorTransparent() {
    return (getColor() == COLOR_TRANSPARENT);
  }

  /**
   * @return
   */
  public boolean isColorDefaultColor() {
    return (getColor() == COLOR_DEFAULT_COLOR);
  }

}
