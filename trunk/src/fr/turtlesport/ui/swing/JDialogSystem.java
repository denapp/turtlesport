package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.turtlesport.Version;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denisapparicio
 * 
 */
public class JDialogSystem extends JDialog {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogSystem.class);
  }

  private JPanel              jPanelSouth;

  private JButton             jButtonOK;

  private JPanel              jContentPane;

  private JScrollPane         jPanelCenter;

  private JEditorPane         jText;

  private JButton             jButtonCopy;

  /**
   * @param owner
   * @param modal
   */
  public JDialogSystem(Dialog owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  public static void prompt(JDialogAbout dlg) {
    JDialogSystem gui = new JDialogSystem(dlg, true);
    gui.setLocationRelativeTo(dlg);

    gui.setVisible(true);
  }

  private void initialize() {
    this.setContentPane(getJContentPane());
    this.setSize(500, 400);

    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());
    this.setTitle(rb.getString("title"));
    jButtonOK.setText(LanguageManager.getManager().getCurrentLang().ok());
    jButtonCopy.setText(rb.getString("copy"));

    // Construction
    StringWriter writer = new StringWriter();
    writer.write("Turtle Sport v" + Version.VERSION);
    writer.write("\r\n");
    try {
      System.getProperties().store(writer, null);
      jText.setText(writer.toString());
    }
    catch (IOException e) {
      log.error("", e);
      jText.setText(writer.toString());
    }
    jText.setCaretPosition(0);
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
      jContentPane.add(getJPanelCenter(), BorderLayout.CENTER);
      jContentPane.add(getJPanelSouth(), BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes jPanelSouth
   * 
   * @return javax.swing.JPanel
   */
  private JScrollPane getJPanelCenter() {
    if (jPanelCenter == null) {

      jText = new JEditorPane();
      jText.setFont(GuiFont.FONT_PLAIN);
      jText.setPreferredSize(new Dimension(520, 160));
      jText.setEditable(false);
      jPanelCenter = new JScrollPane();
      jPanelCenter.setViewportView(jText);
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jPanelSouth
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelSouth() {
    if (jPanelSouth == null) {
      jPanelSouth = new JPanel();
      jPanelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelSouth.add(getJButtonCopy());
      jPanelSouth.add(getJButtonOK());
    }
    return jPanelSouth;
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

  private JButton getJButtonCopy() {
    if (jButtonCopy == null) {
      jButtonCopy = new JButton();
      jButtonCopy.setMnemonic(java.awt.event.KeyEvent.VK_ENTER);
      jButtonCopy.setFont(GuiFont.FONT_PLAIN);
      jButtonCopy.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          // copy clipboard
          StringSelection contents = new StringSelection(jText.getText());
          Clipboard clipboard = Toolkit.getDefaultToolkit()
              .getSystemClipboard();
          clipboard.setContents(contents, null);
        }
      });
    }
    return jButtonCopy;
  }
}
