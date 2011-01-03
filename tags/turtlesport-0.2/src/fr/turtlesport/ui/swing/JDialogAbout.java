package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import fr.turtlesport.Version;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.util.BrowserUtil;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogAbout extends JDialog {
  private JPanel         jContentPane;

  private JEditorPane    jEditorPane;

  private JButton        jButtonOK;

  private JPanel         jPanelSouth;

  private JPanel         jPanelNorth;

  private JButton        jButtonLicence;

  private ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                                .getManager().getCurrentLang(), getClass());

  /**
   * @param owner
   */
  public JDialogAbout(Frame owner) {
    super(owner);
    initialize();
  }

  /**
   * @param owner
   * @param modal
   */
  public JDialogAbout(Frame owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  /**
   * about_en.html Affiche la boite de dialogue A propos.
   * 
   */
  public static void prompt() {
    // mis a jour du model et affichage de l'IHM
    JDialogAbout view = new JDialogAbout(MainGui.getWindow(), true);
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
    view.pack();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setContentPane(getJContentPane());
    this.setSize(490, 420);
    this.setResizable(true);
    this.setTitle(rb.getString("title"));
    jButtonOK.setText(LanguageManager.getManager().getCurrentLang().ok());
    try {
      String lang = LanguageManager.getManager().getCurrentLang().getLocale()
          .getLanguage();
      jEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                                    Boolean.TRUE);
      jEditorPane.setFont(GuiFont.FONT_PLAIN);
      jEditorPane.setPage(getClass().getResource("about_" + lang
                                                 + ".properties"));
    }
    catch (Throwable e) {
    }
    jButtonLicence.setText(rb.getString("jButtonLicence"));
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.add(getJPanelNorth(), BorderLayout.NORTH);
      jContentPane.add(getJPanelCenter(), BorderLayout.CENTER);
      jContentPane.add(getJPanelSouth(), BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes jPanelTitle
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelNorth() {
    if (jPanelNorth == null) {
      JLabel jLabel = new JLabel();
      jLabel.setFont(GuiFont.FONT_PLAIN);
      jLabel
          .setText("<html><body><table><tr><td>"
                   + "<img src='"
                   + ImagesRepository.class.getResource("turtleAbout.png")
                       .toString()
                   + "'>"
                   + "</td><td><center>"// try {
                   // String lang =
                   // LanguageManager.getManager().getCurrentLang().getLocale()
                   // .getLanguage();
                   // jEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                   // Boolean.TRUE);
                   // jEditorPane.setFont(GuiFont.FONT_PLAIN);
                   // jEditorPane.setPage(getClass().getResource("about_" + lang
                   // + ".properties"));
                   // jEditorPane.setContentType("text/html");
                   // jEditorPane.setOpaque(false);
                   // jEditorPane.setEditable(false);
                   // }
                   // catch (Throwable e) {
                   // }

                   + "<font size='+3'>Turtle Sport</font><br><b>Version&nbsp;"
                   + Version.VERSION
                   + "</b></center></td></tr></table></body></html>");
      jPanelNorth = new JPanel();
      jPanelNorth.setFont(GuiFont.FONT_PLAIN);
      jPanelNorth.setLayout(new FlowLayout());
      jPanelNorth.add(jLabel);
    }
    return jPanelNorth;
  }

  /**
   * This method initializes jPanelTitle
   * 
   * @return javax.swing.JPanel
   */
  private JEditorPane getJPanelCenter() {
    if (jEditorPane == null) {
      BorderLayout borderLayout = new BorderLayout();
      borderLayout.setHgap(10);

      jEditorPane = new JEditorPane();
      jEditorPane.setFont(GuiFont.FONT_PLAIN);
      jEditorPane.setContentType("text/html");
      jEditorPane.setOpaque(false);
      jEditorPane.setEditable(false);
      jEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                                    Boolean.TRUE);

      jEditorPane.setBorder(BorderFactory.createEtchedBorder());
      jEditorPane.setFont(GuiFont.FONT_PLAIN);

      jEditorPane.setPreferredSize(new Dimension(520, 160));
      jEditorPane.setEditable(false);
      jEditorPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(final HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              URL url = e.getURL();
              if ("http".equals(url.getProtocol())) {
                BrowserUtil.browse(url.toURI());
              }
              else if ("mailto".equals(url.getProtocol())) {
                MessageMail m = new MessageMail();
                m.addToAddrs(url.getFile());
                Mail.mail(m);
              }
            }
            catch (Throwable e1) {
            }
          }
        }
      });

    }
    return jEditorPane;
  }

  /**
   * This method initializes jButtonClose
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonOK() {
    if (jButtonOK == null) {
      jButtonOK = new JButton();
      jButtonOK.setMnemonic(java.awt.event.KeyEvent.VK_ENTER);
      jButtonOK.setFont(GuiFont.FONT_PLAIN);
      jButtonOK.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          dispose();
        }
      });
    }
    return jButtonOK;
  }

  /**
   * This method initializes jPanelSouth
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelSouth() {
    if (jPanelSouth == null) {
      JLabel jLabel = new JLabel("<html><body>Copyright &#169; 2008-2010 Turtle Sport</html></body>");
      jLabel.setFont(GuiFont.FONT_PLAIN);

      jPanelSouth = new JPanel();
      jPanelSouth.setLayout(new BoxLayout(jPanelSouth, BoxLayout.X_AXIS));

      jPanelSouth.add(Box.createRigidArea(new Dimension(10, 1)));
      jPanelSouth.add(jLabel, null);
      jPanelSouth.add(Box.createHorizontalGlue());
      jPanelSouth.add(getJButtonLicence());
      jPanelSouth.add(getJButtonOK());
    }
    return jPanelSouth;
  }

  /**
   * This method initializes jButtonClose
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonLicence() {
    if (jButtonLicence == null) {

      jButtonLicence = new JButton();
      jButtonLicence.setMnemonic(java.awt.event.KeyEvent.VK_ENTER);
      jButtonLicence.setFont(GuiFont.FONT_PLAIN);
      jButtonLicence.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          JDialogLicense.prompt(JDialogAbout.this);
        }
      });
    }
    return jButtonLicence;
  }

}
