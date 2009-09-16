package fr.turtlesport.ui.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Barre de statut.
 * 
 * @author Denis Apparicio
 * 
 */
public class Status extends Canvas {

  /** Niveau erreur pour le message de statut. */
  private static final int   LEVEL_ERROR           = 1;

  /** Niveau action pour le message de statut. */
  private static final int   LEVEL_ACTION          = 2;

  /** Niveau message pour le message de statut. */
  private static final int   LEVEL_PROGRESS        = 3;

  /** Couleur du message de niveau avancement */
  private static final Color COLOR_NIVEAU_PROGRESS = Color.blue;

  /** Couleur du message de niveau information */
  private static final Color COLOR_NIVEAU_ACTION   = Color.black;

  /** Couleur du message de niveau erreur */
  private static final Color COLOR_NIVEAU_ERREUR   = Color.red;

  /** Message. */
  private String             message;

  /** Niveau du message. */
  private int                levelMessage          = LEVEL_ACTION;

  private Image              imageBuffer;

  /**
   * 
   * 
   */
  public Status() {
    super();
    message = " ";
  }

  /**
   * Mis &aecute; jour du message de niveau info en sp&aecute;cifiant le texte
   * du message.
   * 
   * @param value
   *          texte du message &aecute; mettre &aecute; jour.
   */
  public void setText(String value) {
    setMessage(value, levelMessage);
  }

  /**
   * Mis &aecute; jour du message de niveau action en sp&eecute;cifiant le texte
   * du message.
   * 
   * @param value
   *          texte du message &aecute; mettre &aecute; jour.
   */
  public void setMessageProgress(String value) {
    setMessage(value, LEVEL_PROGRESS);
  }

  /**
   * Mis &aecute; jour du message de niveau action en sp&eecute;cifiant le texte
   * du message.
   * 
   * @param value
   *          texte du message &aecute; mettre &aecute; jour.
   */
  public void setMessageAction(String value) {
    setMessage(value, LEVEL_ACTION);
  }

  /**
   * Mis &aecute; jour du message de niveau action en sp&eecute;cifiant le texte
   * du message.
   * 
   * @param value
   *          texte du message &aecute; mettre &aecute; jour.
   * @param erreur
   *          code retour &aecute; formatter.
   */
  public void setMessageAction(String value, int erreur) {
    setMessage(value + formatCodeErreur(erreur), LEVEL_ACTION);
  }

  /**
   * Mis &aecute; jour du message de niveau action en sp&eecute;cifiant le texte
   * du message.
   * 
   * @param value
   *          texte du message &aecute; mettre &aecute; jour.
   */
  public void setMessageError(String value) {
    setMessage(value, LEVEL_ERROR);
  }

  /**
   * Mis &aecute; jour du message de niveau action en sp&eecute;cifiant le texte
   * du message.
   * 
   * @param value
   *          texte du message &aecute; mettre &aecute; jour.
   * @param erreur
   *          code retour &aecute; formatter.
   */
  public void setMessageError(String value, int erreur) {
    setMessage(value + formatCodeErreur(erreur), LEVEL_ERROR);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Canvas#addNotify()
   */
  public void addNotify() {
    super.addNotify();
    repaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Canvas#paint(java.awt.Graphics)
   */
  public final void paint(Graphics g) {
    g.setFont(GuiFont.FONT_PLAIN);
    int nHeight = g.getFontMetrics().getMaxAscent();

    // on efface le message precedent
    if (getParent() != null) {
      g.setColor(getParent().getBackground());
    }
    else {
      g.setColor(getBackground());
    }    
    g.fillRect(0, 0, getWidth(), getHeight());
    

    g.setColor(getColorMessage());
    g.drawString(message, 0, nHeight);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Canvas#update(java.awt.Graphics)
   */
  public void update(Graphics graphics) {
    int nWidth = 0;
    int nHeight = 0;
    Dimension dim;
    Graphics graphicsNew;

    dim = this.getSize();
    if (imageBuffer != null) {
      nWidth = imageBuffer.getWidth(this);
      nHeight = imageBuffer.getHeight(this);
    }
    if (imageBuffer == null || nWidth < dim.width || nHeight < dim.height) {
      nWidth = Math.max(nWidth, dim.width);
      nHeight = Math.max(nHeight, dim.height);
      imageBuffer = this.createImage(nWidth, nHeight);
    }

    graphicsNew = imageBuffer.getGraphics();
    if (graphicsNew == null) {
      graphicsNew = graphics;
    }

    super.update(graphicsNew);

    if (graphicsNew != graphics) {
      graphics.drawImage(imageBuffer, 0, 0, dim.width, dim.height, this);
    }
  }

  /**
   * Mis &aecute; jour du message.
   */
  private void setMessage(String value, int niveau) {
    this.message = value;
    this.levelMessage = niveau;
    Graphics g = getGraphics();
    if (g != null) {
      // super.paint(getGraphics());
      // this.setForeground(getColorMessage());
      this.paint(getGraphics());
    }
  }

  /**
   * Restitue la couleur du message
   * 
   * @return Color la couleur du message;
   */
  private Color getColorMessage() {
    switch (levelMessage) {
      case LEVEL_PROGRESS:
        return COLOR_NIVEAU_PROGRESS;
      case LEVEL_ACTION:
        return COLOR_NIVEAU_ACTION;
      case LEVEL_ERROR:
        return COLOR_NIVEAU_ERREUR;
      default:
        return COLOR_NIVEAU_ACTION;
    }
  }

  /**
   * Formate d'un code erreur.
   * 
   * @param erreur
   *          le code erreur &aecute; formater.
   * @return code erreur formatte.
   */
  private String formatCodeErreur(int erreur) {
    StringBuffer buffer = new StringBuffer(" (0x");
    String init = Integer.toHexString(erreur).toUpperCase();
    for (int i = 0; i < 2 - init.length(); i++) {
      buffer.append('0');
    }
    buffer.append(init);
    buffer.append(")");

    return buffer.toString();
  }

}
