package fr.turtlesport.ui.swing.component;

import java.awt.Component;
import java.awt.Cursor;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.util.BrowserUtil;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * IHM pour la gestion des messages.
 * 
 * @author Denis Apparicio
 * 
 */
public final class JShowMessage {

  /**
   * 
   */
  private JShowMessage() {
    super();
  }

  /**
   * 
   * @param msg
   *          le message de la boite de dialogue
   * @param title
   *          le titre de de la boite de dialogue
   * @param msgType
   *          le type de message
   * @param options
   * @param initialValue
   * @return true si la r�ponse est oui
   */
  public static boolean question(Component parent,
                                 String msg,
                                 String title,
                                 int msgType,
                                 Object[] options,
                                 Object initialValue) {
    int rep = option(parent,
                     msg,
                     title,
                     JOptionPane.YES_NO_OPTION,
                     msgType,
                     options,
                     initialValue);

    return ((rep == JOptionPane.YES_OPTION) ? true : false);
  }

  /**
   * @param msg
   * @param title
   * @param options
   * @param initialValue
   * @return true si la r&eacute;ponse est oui
   */
  public static boolean question(String msg,
                                 String title,
                                 int msgType,
                                 Object[] options,
                                 Object initialValue) {
    return question(MainGui.getWindow(),
                    msg,
                    title,
                    msgType,
                    options,
                    initialValue);
  }

  /**
   * Affiche une boite de dialogue avec une question.
   * 
   * @param msg
   * @param title
   * @return true si la r&eacute;ponse est oui
   */
  public static boolean question(String msg, String title) {
    return question(MainGui.getWindow(), msg, title);
  }

  /**
   * Affiche une boite de dialogue avec une question.
   * 
   * @param msg
   * @param title
   * @return true si la r&eacute;ponse est oui
   */
  public static boolean question(Component parent, String msg, String title) {
    Object[] options = { LanguageManager.getManager().getCurrentLang().yes(),
        LanguageManager.getManager().getCurrentLang().no() };
    Object initialValue = LanguageManager.getManager().getCurrentLang().no();
    return question(parent,
                    msg,
                    title,
                    JOptionPane.QUESTION_MESSAGE,
                    options,
                    initialValue);
  }

  /**
   * Affiche une boite de dialogue option.
   * 
   * @param parentComponent
   *          le composant parent
   * @param msg
   *          le message
   * @param title
   *          le titre de la boite de dialogue.
   * @param optionType
   *          le type de la boite de dialogue.
   * @param msgType
   *          le type du message.
   * @param options
   *          les options.
   * @param initialValue
   *          l'option initiale.
   * @return l'option s�lectionn� ou <code>CLOSED_OPTION</code> si l'utilisateur
   *         a ferm� la boite de dialogue.
   */
  public static int option(Component parentComponent,
                           String msg,
                           String title,
                           int optionType,
                           int msgType,
                           Object[] options,
                           Object initialValue) {
    return JOptionPane.showOptionDialog(parentComponent,
                                        getMessage(msg),
                                        title,
                                        optionType,
                                        msgType,
                                        null,
                                        options,
                                        initialValue);
  }

  /**
   * Affiche dans une boite de dialogue option.
   * 
   * @param msg
   *          le message
   * @param title
   *          le titre de la boite de dialogue.
   * @param optionType
   *          le type de la boite de dialogue.
   * @param msgType
   *          le type du message.
   * @param options
   *          les options.
   * @param initialValue
   *          l'option initiale.
   * @return l'option s�lectionn� ou <code>CLOSED_OPTION</code> si l'utilisateur
   *         a ferm� la boite de dialogue.
   */
  public static int option(String msg,
                           String title,
                           int optionType,
                           int msgType,
                           Object[] options,
                           Object initialValue) {

    return option(MainGui.getWindow(),
                  msg,
                  title,
                  optionType,
                  msgType,
                  options,
                  initialValue);
  }

  /**
   * Affichage d'une boite de dialogue avec un message d'erreur.
   * 
   * @param msg
   *          le message d'erreur.
   * @param title
   *          le titre de la fenetre.
   * 
   */
  public static void error(String msg, String title) {
    MainGui.getWindow().setCursor(Cursor.getDefaultCursor());
    message(msg, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Affichage d'une boite de dialogue avec un message d'erreur.
   * 
   * @param msg
   *          message d'erreur.
   */
  public static void error(String msg) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), JShowMessage.class);
    error(msg, rb.getString("error"));
  }

  /**
   * Affichage d'une boite de dialogue avec un message d'erreur.
   * 
   * @param parentComponent
   *          le composant parent.
   * @param msg
   *          le message d'erreur.
   * @param title
   *          le titre de la fenetre.
   */
  public static void error(Component parent, String msg, String title) {
    message(parent, msg, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Affichage d'une boite de dialogue avec un message d'erreur.
   * 
   * @param parentComponent
   *          le composant parent.
   * @param msg
   *          le message d'erreur.
   */
  public static void error(Component parent, String msg) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), JShowMessage.class);
    error(parent, msg, rb.getString("error"));
  }

  /**
   * Affichage d'une boite de dialogue ok avec un message.
   * 
   * @param msg
   *          le message.
   * @param title
   *          le titre de la fenetre.
   */
  public static void ok(String msg, String title) {
    message(msg, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Affiche une boite de dialogue avec saisie.
   * 
   * @param msg
   *          le message.
   * @param title
   *          le titre de la fenetre.
   * @return la valeur saisie.
   */
  public static String showInputDialog(String msg, String title) {
    return showInputDialog(MainGui.getWindow(), msg, title);
  }

  /**
   * Affiche une boite de dialogue avec saisie.
   * 
   * @param parentComponent
   *          le composant parent.
   * @param msg
   *          le message.
   * @param title
   *          le titre de la fenetre.
   */
  public static String showInputDialog(Component parentComponent,
                                       String msg,
                                       String title) {

    Object[] options = { LanguageManager.getManager().getCurrentLang().cancel(),
        LanguageManager.getManager().getCurrentLang().ok() };
    Object initialValue = options[1];

    JOptionPane pane = new JOptionPane(msg,
                                       JOptionPane.INFORMATION_MESSAGE,
                                       JOptionPane.OK_CANCEL_OPTION,
                                       null,
                                       options,
                                       initialValue);

    pane.setWantsInput(true);
    pane.setComponentOrientation(((parentComponent == null) ? MainGui
        .getWindow() : parentComponent).getComponentOrientation());

    JDialog dialog = pane.createDialog(parentComponent, title);
    pane.selectInitialValue();
    dialog.setVisible(true);
    dialog.dispose();

    if (!options[1].equals(pane.getValue())) {
      return null;
    }

    Object value = pane.getInputValue();
    if (value == JOptionPane.UNINITIALIZED_VALUE) {
      return null;
    }
    return (String) value;
  }

  /**
   * Affichage d'une boite de dialogue ok avec un message.
   * 
   * @param msg
   *          le message.
   */
  public static void ok(String msg) {
    ok(msg, null);
  }

  /**
   * Affichage d'une boite de dialogue ok avec un message.
   * 
   * @param parentComponent
   *          le composant parent.
   * @param msg
   *          le message.
   * @param title
   *          le titre de la fenetre.
   */
  public static void ok(Component parent, String msg, String title) {
    message(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Affichage d'une boite de dialogue ok avec un message.
   * 
   * @param parentComponent
   *          le composant parent.
   * @param msg
   *          le message.
   */
  public static void ok(Component parent, String msg) {
    ok(parent, msg, null);
  }

  /**
   * Affichage d'une boite de dialogue ok avec un message.
   * 
   * @param msg
   *          le message.
   * @param title
   *          le titre de la fenetre.
   * @param msgType
   *          le type du message.
   */
  public static void message(String msg, String title, int msgType) {
    message(MainGui.getWindow(), msg, title, msgType);
  }

  /**
   * Affichage d'une boite de dialogue message.
   * 
   * @param parentComponent
   *          le composant parent.
   * @param msg
   *          le message.
   * @param title
   *          le titre de la boite de dialogue.
   * @param msgType
   *          le type du message.
   */
  public static void message(Component parent,
                             String msg,
                             String title,
                             int msgType) {
    JOptionPane
        .showMessageDialog(parent, getMessage(msg), title, msgType, null);
  }

  private static Object getMessage(String msg) {
    if (msg != null && (msg.startsWith("<html>") || msg.startsWith("<HTML>"))
        && (msg.contains("mailto:") || msg.contains("href="))) {

      final JEditorPane editorPane = new JEditorPane();
      editorPane.setFont(GuiFont.FONT_PLAIN);
      editorPane.setEditable(false);
      editorPane.setContentType("text/html");
      editorPane.setFont(GuiFont.FONT_PLAIN);
      editorPane.setBackground(new JPanel().getBackground());
      editorPane.setText(msg);

      editorPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(final HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              BrowserUtil.browse(e.getURL().toURI());
            }
            catch (URISyntaxException e1) {
            }
          }
        }
      });

      return editorPane;
    }

    return msg;
  }
}