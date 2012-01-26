package fr.turtlesport.ui.swing;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Barre de statut.
 * 
 * @author Denis Apparicio
 * 
 */
public class Status extends JLabel {

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

  /**
   * 
   * 
   */
  public Status() {
    super("    ");
    setFont(GuiFont.FONT_PLAIN);
    message = " ";
  }

  /**
   * Mis &aecute; jour du message de niveau info en sp&aecute;cifiant le texte
   * du message.
   * 
   * @param value
   *          texte du message &aecute; mettre &aecute; jour.
   */
  // public void setText(String value) {
  // setMessage(value, levelMessage);
  // }

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

  /**
   * Mis &aecute; jour du message.
   */
  private void setMessage(String value, int niveau) {
    this.message = value;
    this.levelMessage = niveau;
    if (SwingUtilities.isEventDispatchThread()) {
      doMessage();
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
          doMessage();
        }
      });
    }
  }

  private void doMessage() {
    setBackground(getColorMessage());
    setText(message);
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
