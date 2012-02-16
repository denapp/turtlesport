package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.turtlesport.Configuration;
import fr.turtlesport.googleearth.GoogleEarthFactory;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.PanelPrefListener;
import fr.turtlesport.util.Exec;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefGoogleEarth extends JPanel implements LanguageListener,
                                                 PanelPrefListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelPrefGoogleEarth.class);
  }

  private JPanelPrefTitle     jPanelTitle;

  private JPanel              jPanelCenter;

  private JLabel              jLabelLibGoogleEarth;

  private JTextField          jTextFieldPathGoogleEarth;

  private JButton             jButtonChoose;

  private JButton             jButtonTest;

  private ResourceBundle      rb;

  /**
   * 
   */
  public JPanelPrefGoogleEarth() {
    super();
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.PanelPrefListener#viewChanged()
   */
  public void viewChanged() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang
   * .LanguageEvent)
   */
  public void languageChanged(final LanguageEvent event) {
    if (SwingUtilities.isEventDispatchThread()) {
      performedLanguage(event.getLang());
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          performedLanguage(event.getLang());
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, getClass());

    jPanelTitle.setTitle(rb.getString("title"));
    jLabelLibGoogleEarth.setText(rb.getString("jLabelLibGoogleEarth"));
    jButtonChoose.setText(rb.getString("jButtonChoose"));
    jButtonTest.setText(rb.getString("jButtonTest"));
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setHgap(5);
    borderLayout.setVgap(5);
    this.setLayout(borderLayout);
    this.setSize(534, 218);
    this.add(getJPanelTitle(), BorderLayout.NORTH);
    this.add(getJPanelCenter(), BorderLayout.CENTER);

    // Evenement
    FocusAdapter focusListenerPath = new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        String value = jTextFieldPathGoogleEarth.getText();
        Configuration.getConfig().addProperty("google", "googleearth", value);
      }
    };
    jTextFieldPathGoogleEarth.addFocusListener(focusListenerPath);

    jButtonChoose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        int ret = fileChooser.showOpenDialog(JPanelPrefGoogleEarth.this);
        if (ret == JFileChooser.APPROVE_OPTION) {
          String value = fileChooser.getSelectedFile().getAbsolutePath();
          jTextFieldPathGoogleEarth.setText(value);
          Configuration.getConfig().addProperty("google", "googleearth", value);
        }
      }
    });

    jButtonTest.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final String command = jTextFieldPathGoogleEarth.getText();
        if (command == null || "".equals(command)) {
          return;
        }

        MainGui.getWindow().beforeRunnableSwing();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            // Lancement de la commande
            try {
              Exec.exec(command);
            }
            catch (IOException e) {
              log.warn("", e);
              JShowMessage.error(JPanelPrefGoogleEarth.this, rb
                  .getString("execGoogleEarth"));
            }
          }
        });
        MainGui.getWindow().afterRunnableSwing();
      }
    });

    performedLanguage(LanguageManager.getManager().getCurrentLang());
    LanguageManager.getManager().addLanguageListener(this);
  }

  /**
   * This method initializes jPanelTitle
   * 
   * @return javax.swing.JPanel
   */
  private JPanelPrefTitle getJPanelTitle() {
    if (jPanelTitle == null) {
      jPanelTitle = new JPanelPrefTitle("Google Earth");
    }
    return jPanelTitle;
  }

  /**
   * This method initializes jPanelCenter
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jLabelLibGoogleEarth = new JLabel();
      jLabelLibGoogleEarth.setText("Google Earth : ");
      jLabelLibGoogleEarth.setFont(GuiFont.FONT_PLAIN);

      jPanelCenter = new JPanel();
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.LEFT);
      jPanelCenter.setLayout(flowLayout);
      jPanelCenter.setAlignmentY(TOP_ALIGNMENT);
      jPanelCenter.add(jLabelLibGoogleEarth, null);
      jPanelCenter.add(getJTextFieldPathGoogleEarth(), null);
      jPanelCenter.add(getJButtonChoose(), null);
      jPanelCenter.add(getJButtonTest(), null);
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jTextFieldGoogle
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getJTextFieldPathGoogleEarth() {
    if (jTextFieldPathGoogleEarth == null) {
      jTextFieldPathGoogleEarth = new JTextField();
      Dimension dim = new Dimension(300, 25);
      jTextFieldPathGoogleEarth.setPreferredSize(dim);
      jTextFieldPathGoogleEarth.setMinimumSize(dim);
      jTextFieldPathGoogleEarth.setFont(GuiFont.FONT_PLAIN);
      jTextFieldPathGoogleEarth.requestFocus();

      // Valorisation de la commande
      jTextFieldPathGoogleEarth.setText(GoogleEarthFactory.getDefault()
          .getPath());
    }
    return jTextFieldPathGoogleEarth;
  }

  /**
   * This method initializes jButtonChoose
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonChoose() {
    if (jButtonChoose == null) {
      jButtonChoose = new JButton();
      jButtonChoose.setText("Choisir");
      jButtonChoose.setFont(GuiFont.FONT_PLAIN);
      jButtonChoose.setMnemonic(java.awt.event.KeyEvent.VK_O);
    }
    return jButtonChoose;
  }

  /**
   * This method initializes jButtonExec
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonTest() {
    if (jButtonTest == null) {
      jButtonTest = new JButton();
      jButtonTest.setText("Tester");
      jButtonTest.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonTest;
  }

} // @jve:decl-index=0:visual-constraint="10,10"
