package fr.turtlesport.ui.swing;

import java.awt.Font;

/**
 * Classe pour d&eacute;finir les <code>font</code> pour les IHM.
 * 
 * @author Denis Apparicio
 * 
 */
public final class GuiFont {

  public static final Font   FONT_PLAIN_SMALL = new Font("SansSerif",
                                                         java.awt.Font.PLAIN,
                                                         10);

  public static final Font   FONT_PLAIN       = new Font("SansSerif",
                                                         java.awt.Font.PLAIN,
                                                         11);

  public static final Font   FONT_ITALIC      = new Font("SansSerif",
                                                         Font.ITALIC,
                                                         11);

  public static final String FONT_PLAIN_HTML  = "font-family:SansSerif;font-size:11";

  public static final Font   FONT_BOLD        = new Font("SansSerif",
                                                         Font.BOLD,
                                                         11);

  public static final Font   FONT_BOLD_ITALIC = new Font("SansSerif",
                                                         Font.BOLD
                                                             | Font.ITALIC,
                                                         11);

  private GuiFont() {
  }
}
