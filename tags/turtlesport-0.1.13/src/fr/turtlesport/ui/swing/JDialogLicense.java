package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.turtlesport.Launcher;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denisapparicio
 * 
 */
public class JDialogLicense extends JDialog {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogLicense.class);
  }

  private JPanel              jPanelSouth;

  private JButton             jButtonOK;

  private JPanel              jContentPane;

  private JScrollPane         jPanelCenter;

  private JEditorPane         jTextLicense;

  /**
   * @param owner
   * @param modal
   */
  public JDialogLicense(Dialog owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  public static void prompt(JDialogAbout dlg) {
    JDialogLicense gui = new JDialogLicense(dlg, true);
    gui.setLocationRelativeTo(dlg);

    gui.setVisible(true);
  }

  private void initialize() {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());
    this.setTitle(rb.getString("title"));
    this.setContentPane(getJContentPane());
    this.setSize(500, 400);

    // recuperation de la licence
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(Launcher.class
          .getResourceAsStream("lgpl-2.1.txt")));
      StringBuilder st = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        st.append(line);
        st.append("\n");
      }
      jTextLicense.setText(st.toString());
    }
    catch (IOException e) {
      log.error("", e);
      jTextLicense.setText("GPL LICENCE");
    }
    finally {
      try {
        if (reader != null) {
          reader.close();
        }
      }
      catch (IOException e) {
      }
    }
    jTextLicense.setCaretPosition(0);
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

      jTextLicense = new JEditorPane();
      jTextLicense.setFont(GuiFont.FONT_PLAIN);
      jTextLicense.setPreferredSize(new Dimension(520, 160));
      jTextLicense.setEditable(false);
      jPanelCenter = new JScrollPane();
      jPanelCenter.setViewportView(jTextLicense);
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
      JLabel jLabel = new JLabel("� Copyright 2008-2009");
      jLabel.setFont(GuiFont.FONT_PLAIN);

      jPanelSouth = new JPanel();
      jPanelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
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
      jButtonOK.setText("OK");
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

}
