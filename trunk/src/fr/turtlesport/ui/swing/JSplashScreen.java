package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import fr.turtlesport.ui.swing.component.JPanelBackgroundImage;
import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * Splashscreen.
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
      ImageIcon icon = ImagesRepository.getImageIcon("splash.jpg");
      jLabelSplash.setIcon(icon);
      jLabelSplash.setSize(icon.getIconWidth(), icon.getIconHeight());

      jContentPane = new JPanelBackgroundImage(ImagesRepository.getImage("splash.jpg"));
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setBorder(BorderFactory.createLineBorder(Color.black, 1));
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
      status = new Status();
      status.setBounds(0, 0, 412, 20);

      jPanelStatus = new JPanel();
      jPanelStatus.setOpaque(true);
      jPanelStatus.setLayout(new BoxLayout(jPanelStatus, BoxLayout.Y_AXIS));
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
      jProgressBar.setPreferredSize(new Dimension(412, 10));
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
