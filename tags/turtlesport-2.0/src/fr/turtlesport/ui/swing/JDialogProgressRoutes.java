package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.A1006Course;
import fr.turtlesport.protocol.data.D1006CourseType;
import fr.turtlesport.protocol.progress.ICourseProgress;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogProgressRoutes extends JDialog implements ICourseProgress {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogProgressRoutes.class);
  }

  private JPanel              jContentPane;

  private JButton             jButtonOK;

  private JPanel              jPanelSouth;

  private JProgressBar        jProgressBar;

  private JLabel              jLabelProgress;

  private ResourceBundle      rb;

  private boolean             isAbortTransfert = false;

  private boolean             isRetreive       = false;

  /**
   * @param owner
   */
  public JDialogProgressRoutes(Frame owner, boolean modal, boolean isRetreive) {
    super(owner, modal);
    this.isRetreive = isRetreive;
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#abortTransfert()
   */
  public boolean abortTransfert() {
    return isAbortTransfert;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfert()
   */
  public void beginTransfert() {
    jLabelProgress.setText(rb.getString("jLabelProgressBeginTransfer"));
    jProgressBar.setIndeterminate(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#endTransfert()
   */
  public void endTransfert() {
    jButtonOK.setEnabled(true);
    jLabelProgress.setText(rb.getString("jLabelProgressEndTransfer"));
    jProgressBar.setIndeterminate(false);
    jProgressBar.setMaximum(100);
    jProgressBar.setValue(100);

    jButtonOK.setText(LanguageManager.getManager().getCurrentLang().ok());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfertLap
   * ()
   */
  public void beginTransfertLap() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfertTrk
   * (int)
   */
  public void beginTransfertTrk(int nbTrkPoints) {
    log.debug("beginTransfertTrk nbPoints=" + nbTrkPoints);
    jProgressBar.setMaximum(nbTrkPoints);
    jProgressBar.setStringPainted(true);
    jProgressBar.setIndeterminate(false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#transfertTrk(
   * fr.turtlesport.protocol.data.D1006CourseType)
   */
  public void transfertTrk(D1006CourseType d1006) {
    int value = jProgressBar.getValue() + pointNotify();
    jProgressBar.setValue(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#beginTransfertPoint
   * (int)
   */
  public void beginTransfertPoint(int nbPoints) {
    log.debug("beginTransfertPoint nbPoints=" + nbPoints);
    if (nbPoints != 0) {
      jProgressBar.setMaximum(nbPoints);
      jProgressBar.setValue(0);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#transfertPoint
   * (fr.turtlesport.protocol.data.D1006CourseType)
   */
  public void transfertPoint(D1006CourseType d1006) {
    log.debug("transfertPoint");
    int value = jProgressBar.getValue() + pointNotify();
    jProgressBar.setValue(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.protocol.progress.ICourseTransfertProgress#pointNotify()
   */
  public int pointNotify() {
    return 40;
  }

  /**
   * Recuperation des parcours.
   * 
   * @param a1000
   */
  public boolean retreive(A1006Course a1006) {

    long deb;
    boolean isOk = true;
    try {
      // Recuperation des run.
      deb = System.currentTimeMillis();
      a1006.retrieve(this);
      log.warn("Temps pour recuperer les parcours (ms) --> "
               + (System.currentTimeMillis() - deb));
      setCursor(Cursor.getDefaultCursor());
    }
    catch (Throwable th) {
      log.error("", th);
      dispose();
      JShowMessage.error(th.getMessage());
      isOk = false;
    }

    return isOk && !isAbortTransfert;
  }

  /**
   * Envoi des parcours.
   * 
   * @param a1000
   */
  public boolean send(A1006Course a1006) {

    long deb;
    boolean isOk = true;
    try {
      // Recuperation des run.
      deb = System.currentTimeMillis();
      a1006.send(this);
      log.warn("Temps pour recuperer les parcours (ms) --> "
               + (System.currentTimeMillis() - deb));
      setCursor(Cursor.getDefaultCursor());
    }
    catch (Throwable th) {
      log.error("", th);
      dispose();
      JShowMessage.error(th.getMessage());
      isOk = false;
    }

    return isOk && !isAbortTransfert;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Window#dispose()
   */
  @Override
  public void dispose() {
    super.dispose();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    this.setSize(300, 120);
    this.setTitle(rb.getString(isRetreive ? "titleRetreive" : "titleSend"));
    this.setContentPane(getJContentPane());
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    jLabelProgress.setText(rb.getString("jLabelProgressBeginTransfer"));

    // evenements
    jButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionevent) {
        if (jButtonOK.getText().equals(LanguageManager.getManager()
            .getCurrentLang().cancel())) {
          isAbortTransfert = true;
        }
        dispose();
      }
    });
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jLabelProgress = new JLabel();
      jLabelProgress.setPreferredSize(new Dimension(200, 20));
      jLabelProgress.setHorizontalAlignment(SwingConstants.LEFT);
      jLabelProgress.setVerticalAlignment(SwingConstants.TOP);
      jLabelProgress.setVerticalTextPosition(SwingConstants.TOP);
      jLabelProgress.setFont(GuiFont.FONT_PLAIN);

      BorderLayout borderLayout = new BorderLayout(5, 5);
      jContentPane = new JPanel();
      jContentPane.setLayout(borderLayout);
      jContentPane.add(new JLabel(" "), BorderLayout.NORTH);
      jContentPane.add(new JLabel(" "), BorderLayout.WEST);
      jContentPane.add(jLabelProgress, BorderLayout.CENTER);
      jContentPane.add(getJPanelSouth(), BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes jPanelSouth
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelSouth() {
    if (jPanelSouth == null) {
      jPanelSouth = new JPanel();
      jPanelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
      jPanelSouth.add(getJProgressBar(), null);
      jPanelSouth.add(getJButtonOK(), null);
    }
    return jPanelSouth;
  }

  /**
   * This method initializes jButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonOK() {
    if (jButtonOK == null) {
      jButtonOK = new JButton();
      jButtonOK.setFont(GuiFont.FONT_PLAIN);
      jButtonOK.setText(LanguageManager.getManager().getCurrentLang().cancel());
    }
    return jButtonOK;
  }

  /**
   * This method initializes jProgressBar
   * 
   * @return javax.swing.JProgressBar
   */
  private JProgressBar getJProgressBar() {
    if (jProgressBar == null) {
      jProgressBar = new JProgressBar();
      jProgressBar.setPreferredSize(new Dimension(200, 18));
      jProgressBar.setMaximumSize(new Dimension(200, 18));
      jProgressBar.setMinimumSize(new Dimension(200, 18));
      jProgressBar.setIndeterminate(true);
      jProgressBar.setFont(GuiFont.FONT_PLAIN);
    }
    return jProgressBar;
  }

} // @jve:decl-index=0:visual-constraint="10,10"
