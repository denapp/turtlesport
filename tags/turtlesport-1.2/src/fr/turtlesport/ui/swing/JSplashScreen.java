package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXImagePanel;

import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * Splashscreen.
 * 
 * @author Denis Apparicio
 * 
 */
public class JSplashScreen extends JWindow {

  private JXImagePanelStatus jContentPane;

  private JProgressBar       jProgressBar;

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
    this.setSize(412, 302);
    this.setContentPane(getJContentPane());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JXImagePanelStatus getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JXImagePanelStatus(ImagesRepository.class.getResource("splash.jpg"));
      jContentPane.setLayout(new BorderLayout(0,0));
      jContentPane.setBorder(BorderFactory.createLineBorder(Color.black, 1));
      jContentPane.add(getJProgressBar(), java.awt.BorderLayout.SOUTH);
    }
    return jContentPane;
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
    jContentPane.setMessageProgress("  " + msg);
  }

  /**
   * Mise &aecute; jour du message de progression erreur.
   * 
   * @param msg
   *          le message
   */
  public void updateError(String msg) {
    jContentPane.setMessageError("  " + msg);
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
    jProgressBar.setValue(value);
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

  /**
   * @author Denis Apparicio
   * 
   */
  private static class JXImagePanelStatus extends JXImagePanel {
    /** Message. */
    private String             message;

    /** Niveau du message. */
    private int                levelMessage         = LEVEL_PROGRESS;

    /** Niveau erreur pour le message de statut. */
    private static final int   LEVEL_ERROR          = 1;

    /** Niveau message pour le message de statut. */
    private static final int   LEVEL_PROGRESS       = 2;

    /** Couleur du message de niveau avancement */
    private static final Color COLOR_LEVEL_PROGRESS = Color.white;

    /** Couleur du message de niveau erreur */
    private static final Color COLOR_LEVEL_ERROR    = Color.red;

    public JXImagePanelStatus(URL url) {
      super(url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXImagePanel#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (message != null) {
        g.setFont(GuiFont.FONT_PLAIN);
        g.setColor((levelMessage == LEVEL_ERROR) ? COLOR_LEVEL_ERROR
            : COLOR_LEVEL_PROGRESS);
        g.drawString(message, 10, 240);
      }
    }

    /**
     * Mis &aecute; jour du message.
     */
    private void setMessage(String value, int niveau) {
      this.message = value;
      this.levelMessage = niveau;
      repaint();
    }

    /**
     * Mis &aecute; jour du message de niveau action en sp&eecute;cifiant le
     * texte du message.
     * 
     * @param value
     *          texte du message &aecute; mettre &aecute; jour.
     */
    public void setMessageProgress(String value) {
      setMessage(value, LEVEL_PROGRESS);
    }

    /**
     * Mis &aecute; jour du message de niveau action en sp&eecute;cifiant le
     * texte du message.
     * 
     * @param value
     *          texte du message &aecute; mettre &aecute; jour.
     */
    public void setMessageError(String value) {
      setMessage(value, LEVEL_ERROR);
    }
  }

}
