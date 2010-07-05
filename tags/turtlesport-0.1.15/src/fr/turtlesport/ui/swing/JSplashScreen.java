package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * Splashscreen Outils Caisse.
 * 
 * @author Denis Apparicio
 * 
 */
public class JSplashScreen extends JWindow {

  private JPanel       jContentPane;

  private JLabel       jLabelSplash;

  private JPanel       jPanelStatus;

  private JProgressBar jProgressBar;

  private Status       status;

  /**
   * 
   */
  public JSplashScreen() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(412, 310);
    this.setContentPane(getJContentPane());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jLabelSplash = new JLabel();
      ImageIcon icon = ImagesRepository.getImageIcon("splash.png");
      jLabelSplash.setIcon(icon);
      jLabelSplash.setSize(icon.getIconWidth(), icon.getIconHeight());

      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setBorder(BorderFactory.createLineBorder(Color.black, 1));
      jContentPane.add(jLabelSplash, java.awt.BorderLayout.CENTER);
      jContentPane.add(getJPanelStatus(), java.awt.BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes jPanelStatus
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelStatus() {
    if (jPanelStatus == null) {
      Color color = new Color(120, 207, 67);

      status = new Status();
      status.setFont(GuiFont.FONT_PLAIN);
      status.setBackground(color);
      status.setBounds(0, 0, 412, 20);

      jPanelStatus = new JPanel();
      jPanelStatus.setBackground(color);
      jPanelStatus.setLayout(new BoxLayout(jPanelStatus, BoxLayout.Y_AXIS));
      jPanelStatus.add(status, null);
      jPanelStatus.add(getJProgressBar(), null);
    }
    return jPanelStatus;
  }

  /**
   * This method initializes jPanelStatus
   * 
   * @return javax.swing.JPanel
   */
  private JProgressBar getJProgressBar() {
    if (jProgressBar == null) {
      jProgressBar = new JProgressBar();
      jProgressBar.setIndeterminate(true);
    }
    return jProgressBar;
  }

  /**
   * Mise &aecute; jour du message de progression.
   * 
   * @param msg
   *          le message
   */
  public void updateProgress(String msg) {
    status.setMessageProgress("  " + msg);
  }

  /**
   * Mise &aecute; jour du message de progression erreur.
   * 
   * @param msg
   *          le message
   */
  public void updateError(String msg) {
    status.setMessageError("  " + msg);
  }

  /**
   * Mise &aecute; jour du message de progression.
   * 
   * @param msg
   *          le message
   */
  public void pause() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        jProgressBar.setIndeterminate(false);
        jProgressBar.setValue(0);
      }
    });
  }

  /**
   * Mise &aecute; jour du message de progression.
   * 
   * @param msg
   *          le message
   */
  public void setIndeterminate(final boolean isValue) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        jProgressBar.setIndeterminate(isValue);
      }
    });
  }

  /**
   * Mise &aecute; jour du message de progression.
   * 
   * @param msg
   *          le message
   */
  public void setValue(final int value) {
   // SwingUtilities.invokeLater(new Runnable() {
     // public void run() {
        jProgressBar.setValue(value);
      //}
   // });
  }

  /**
   * Mise &aecute; jour du message de progression.
   * 
   * @param msg
   *          le message
   */
  public void setMaximum(final int value) {
    jProgressBar.setStringPainted(true);
    jProgressBar.setMaximum(value);
  }

  /**
   * Mise &aecute; jour du message de progression.
   * 
   * @param msg
   *          le message
   */
  public void setMinimum(final int value) {
    jProgressBar.setMinimum(value);
  }

}
