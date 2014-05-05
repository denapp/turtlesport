package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import fr.turtlesport.CantWriteIOException;
import fr.turtlesport.NotDirIOException;
import fr.turtlesport.db.DatabaseManager;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.PanelPrefListener;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefTracks extends JPanelPref implements LanguageListener,
                                            PanelPrefListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelPrefTracks.class);
  }

  private JPanelPrefTitle     jPanelTitle;

  private JPanel              jPanelCenter;

  private JLabel              jLabelLibDir;

  private JTextField          jTextFieldPath;

  private JButton             jButtonChoose;

  private JButton             jButtonDefaultDir;

  private ResourceBundle      rb;

  private JLabel              jLabelLibValidDir;

  private JButton             jButtonCopy;

  /**
   * 
   */
  public JPanelPrefTracks() {
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
    jLabelLibDir.setText(rb.getString("jLabelLibDir"));
    jButtonChoose.setText(rb.getString("jButtonChoose"));
    jButtonCopy.setText(rb.getString("jButtonCopy"));
    jButtonCopy.setToolTipText(rb.getString("jButtonCopyTooltipText"));

    jButtonDefaultDir.setText(rb.getString("jButtonDefaultDir"));
    jLabelLibValidDir.setText(rb.getString("jLabelDir"));
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
    this.add(getJPanelTitle(), BorderLayout.NORTH);
    this.add(getJPanelCenter(), BorderLayout.CENTER);

    // Evenement
    FocusAdapter focusListenerPath = new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        String value = jTextFieldPath.getText();
        try {
          DatabaseManager.setDirectory(new File(value));
        }
        catch (FileNotFoundException e1) {
          log.warn("", e1);
        }
        catch (CantWriteIOException e1) {
          log.warn("", e1);
        }
        catch (NotDirIOException e1) {
          log.warn("", e1);
        }
      }
    };
    jTextFieldPath.addFocusListener(focusListenerPath);

    jButtonChoose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int ret = fileChooser.showOpenDialog(JPanelPrefTracks.this);
        if (ret == JFileChooser.APPROVE_OPTION) {
          String value = fileChooser.getSelectedFile().getAbsolutePath();
          jTextFieldPath.setText(value);
          try {
            DatabaseManager.setDirectory(fileChooser.getSelectedFile());
          }
          catch (FileNotFoundException ioe) {
            rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
                                                 .getCurrentLang(), JPanelPrefTracks.class);
            JShowMessage.error(MessageFormat.format(rb.getString("dirFileNotFoundException"),
                                                    jTextFieldPath.getText()));
          }
          catch (CantWriteIOException cioe) {
            rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
                                                 .getCurrentLang(), JPanelPrefTracks.class);
            JShowMessage.error(MessageFormat.format(rb.getString("dirCantWriteIOException"),
                                                    jTextFieldPath.getText()));
          }
          catch (NotDirIOException nioe) {
            rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
                                                 .getCurrentLang(), JPanelPrefTracks.class);
            JShowMessage.error(MessageFormat.format(rb.getString("dirFileNotFoundException"),
                                                    jTextFieldPath.getText()));
          }
        }
      }
    });

    jButtonDefaultDir.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTextFieldPath.setText(DatabaseManager.getDefaultDirectory());
        try {
          DatabaseManager.setDirectory(new File(DatabaseManager.getDefaultDirectory()));
        }
        catch (FileNotFoundException e1) {
          log.warn("", e1);
        }
        catch (CantWriteIOException e1) {
          log.warn("", e1);
        }
        catch (NotDirIOException e1) {
          log.warn("", e1);
        }
      }
    });

    jButtonCopy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
                                                 .getCurrentLang(), JPanelPrefTracks.class);
            try {
              DatabaseManager.backUpDatabase(new File(jTextFieldPath.getText()));
              JShowMessage.ok(rb.getString("copyOK"), rb.getString("title"));
            }
            catch (FileNotFoundException ioe) {
              JShowMessage.error(MessageFormat.format(rb.getString("dirFileNotFoundException"),
                                                      jTextFieldPath.getText()));
            }
            catch (CantWriteIOException cioe) {
              JShowMessage.error(MessageFormat.format(rb.getString("dirCantWriteIOException"),
                                                      jTextFieldPath.getText()));
            }
            catch (NotDirIOException nioe) {
              JShowMessage.error(MessageFormat.format(rb.getString("dirFileNotFoundException"),
                                                      jTextFieldPath.getText()));
            }
            catch (SQLException sqle) {
              log.error("", sqle);
              JShowMessage.error(rb.getString("errorCopy"));
            }
            finally {
              setCursor(Cursor.getDefaultCursor());
            }
          }
        });
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
      jPanelTitle = new JPanelPrefTitle("Tracks");
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
      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(null);
      
      jLabelLibDir = new JLabel();
      jLabelLibDir.setHorizontalAlignment(SwingConstants.LEFT);
      jLabelLibDir.setText("Valid dir");
      jLabelLibDir.setFont(GuiFont.FONT_ITALIC);
      jLabelLibDir.setBounds(new Rectangle(5, 5, 300, 25));
      jPanelCenter.add(jLabelLibDir);

      jPanelCenter.add(getJTextFieldPath());
      jPanelCenter.add(getJButtonChoose());
      jPanelCenter.add(getJButtonCopy());

      jPanelCenter.add(getJButtonDefaultDir());

      jLabelLibValidDir = new JLabel();
      jLabelLibValidDir.setBounds(new Rectangle(5, 95, 500, 25));
      jLabelLibValidDir.setHorizontalAlignment(SwingConstants.LEFT);
      jLabelLibValidDir.setText("Valid dir");
      jLabelLibValidDir.setFont(GuiFont.FONT_ITALIC);
      jPanelCenter.add(jLabelLibValidDir);
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jTextFieldPath
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getJTextFieldPath() {
    if (jTextFieldPath == null) {
      jTextFieldPath = new JTextField();
      jTextFieldPath.setBounds(new Rectangle(5, 35, 300, 25));
      jTextFieldPath.setFont(GuiFont.FONT_PLAIN);
      jTextFieldPath.requestFocus();

      // Valorisation de la commande
      jTextFieldPath.setText(DatabaseManager.getDirectory());
    }
    return jTextFieldPath;
  }

  public JButton getJButtonDefaultDir() {
    if (jButtonDefaultDir == null) {
      jButtonDefaultDir = new JButton();
      jButtonDefaultDir.setText("Defaut");
      jButtonDefaultDir.setBounds(new Rectangle(5, 65, 280, 25));
      jButtonDefaultDir.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonDefaultDir;
  }

  public JButton getJButtonChoose() {
    if (jButtonChoose == null) {
      jButtonChoose = new JButton();
      jButtonChoose.setText("Choisir");
      jButtonChoose.setFont(GuiFont.FONT_PLAIN);
      jButtonChoose.setBounds(new Rectangle(315, 35, 80, 25));
    }
    return jButtonChoose;
  }
 
  public JButton getJButtonCopy() {
    if (jButtonCopy == null) {
      jButtonCopy = new JButton();
      jButtonCopy.setText("Export");
      jButtonCopy.setFont(GuiFont.FONT_PLAIN);
      jButtonCopy.setBounds(new Rectangle(405, 35, 80, 25));
    }
    return jButtonCopy;
  }
  
} // @jve:decl-index=0:visual-constraint="10,10"
