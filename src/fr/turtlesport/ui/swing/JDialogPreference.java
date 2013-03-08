package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import fr.turtlesport.Configuration;
import fr.turtlesport.ConfigurationException;
import fr.turtlesport.googleearth.GoogleEarthFactory;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.model.ModelPref;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogPreference extends JDialog implements LanguageListener {

  // ui
  private JPanel         jContentPane;

  private JSplitPane     jSplitPane;

  private JPanel         jPanelButton;

  private JButton        jButtonOK;

  private JButton        jButtonCancel;

  private JTreePref      jTreePref;

  private JPanel         jPanelLeft;

  /** Model */
  private ModelPref      modelPrefGen;

  private ModelPref      modelPrefMail;

  private ModelPref      modelPrefUnit;

  private ModelPref      modelPrefTracks;

  private ModelPref      modelPrefGoogleEarth;

  private ModelPref      modelPrefMap;

  private ModelPref      modelPrefProxy;

  private ResourceBundle rb;

  private boolean        isButtonActive = false;

  /**
   * 
   */
  public JDialogPreference() {
    super();
    initialize();
  }

  /**
   * @param owner
   * @param modal
   */
  public JDialogPreference(Frame owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  /**
   * 
   */
  public static void prompt() {
    // mis a jour du model et affichage de l'IHM
    JDialogPreference view = new JDialogPreference(MainGui.getWindow(), true);
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Window#dispose()
   */
  @Override
  public void dispose() {
    if (!isButtonActive) {
      cancel();
    }
    LanguageManager.getManager().removeLanguageListener(this);
    super.dispose();
  }

  /**
   * Valorise le panel pr&eacute;f&eacute;rence.
   * 
   * @param panel
   *          le panel pr&eacute;f&eacute;rence.
   */
  public void setPanelPreference(JPanel panel) {
    if (getJSplitPane().getRightComponent() != null
        && LanguageListener.class.isAssignableFrom(getJSplitPane()
            .getRightComponent().getClass())) {
      LanguageListener l = (LanguageListener) getJSplitPane()
          .getRightComponent();
      LanguageManager.getManager().removeLanguageListener(l);
    }
    getJSplitPane().setRightComponent(panel);
    getJSplitPane().setDividerLocation(getJSplitPane().getDividerLocation());
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

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, JDialogPreference.class);
    setTitle(rb.getString("title"));
    jButtonOK.setText(lang.ok());
    jButtonCancel.setText(lang.cancel());
    rb = ResourceBundleUtility.getBundle(lang, ModelPref.class);

    modelPrefGen.setTitle(rb.getString("modelPrefGen"));
    modelPrefUnit.setTitle(rb.getString("modelPrefUnit"));
    modelPrefTracks.setTitle(rb.getString("modelPrefTracks"));
    if (GoogleEarthFactory.getDefault().isConfigurable()) {
      modelPrefGoogleEarth.setTitle(rb.getString("modelPrefGoogleEarth"));
    }
    if (Mail.isConfigurable()) {
      modelPrefMail.setTitle(rb.getString("modelPrefMail"));
    }
    modelPrefMap.setTitle(rb.getString("modelPrefMap"));
    modelPrefProxy.setTitle(rb.getString("modelPrefProxy"));

    jTreePref.updateUI();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
    if (getJSplitPane().getRightComponent() != null
        && LanguageListener.class.isAssignableFrom(getJSplitPane()
            .getRightComponent().getClass())) {
      LanguageListener l = (LanguageListener) getJSplitPane()
          .getRightComponent();
      LanguageManager.getManager().removeLanguageListener(l);
    }
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(670, 510);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setTitle("Preferences");
    this.setContentPane(getJContentPane());

    // Mis a jour de l'arbre
    modelPrefGen = new ModelPref("GeneralqsfdQSFQSf", JPanelPrefGen.class);
    DefaultMutableTreeNode node = jTreePref.addObject(modelPrefGen);

    modelPrefUnit = new ModelPref("UnitesfqsfqSFQSffqsfqSF",
                                  JPanelPrefUnits.class);
    jTreePref.addObject(node, modelPrefUnit);

    modelPrefTracks = new ModelPref("UnitesfqsfqSFQSffqsfqSF",
                                    JPanelPrefTracks.class);
    jTreePref.addObject(node, modelPrefTracks);

    if (GoogleEarthFactory.getDefault().isConfigurable()) {
      modelPrefGoogleEarth = new ModelPref("Google EarthFQSfqSF",
                                           JPanelPrefGoogleEarth.class);
      jTreePref.addObject(modelPrefGoogleEarth);
    }
    if (Mail.isConfigurable()) {
      modelPrefMail = new ModelPref("Client de messagerieQSfqSFQSf",
                                    JPanelPrefMail.class);
      jTreePref.addObject(modelPrefMail);
    }

    modelPrefProxy = new ModelPref("MapqsfqSFQSfqSFQF", JPanelPrefProxy.class);
    jTreePref.addObject(modelPrefProxy);

    modelPrefMap = new ModelPref("MapqsfqSFQSfqSFQF", JPanelPrefMap.class);
    jTreePref.addObject(modelPrefMap);

    jTreePref.setSelectionRow(0);

    // Evenements
    jButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isButtonActive = true;
        cancel();
        dispose();
      }
    });

    jButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isButtonActive = true;
        try {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreePref
              .getLastSelectedPathComponent();
          if (node != null && (node.getUserObject() != null)
              && (node.getUserObject() instanceof ModelPref)) {
            ModelPref model = (ModelPref) node.getUserObject();
            model.changeView();
          }
          Configuration.getConfig().commitTransaction();
        }
        catch (ConfigurationException ce) {
          JShowMessage.error(JDialogPreference.this, rb.getString("errorSave"));
        }
        dispose();
      }
    });

    // Debut transaction
    Configuration.getConfig().beginTransaction();

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
  }

  private void cancel() {
    LanguageManager.getManager().removeLanguageListener(JDialogPreference.this);

    // rollback
    Configuration.getConfig().rollbackTransaction();

    // retablissement des poprietes general
    String value = Configuration.getConfig().getProperty("general",
                                                         "lookandfeel");
    if (value != null) {
      SwingLookAndFeel.setLookAndFeel(value);
      MainGui.getWindow().updateComponentTreeUI();
    }
    
    value = Configuration.getConfig().getProperty("general", "language");
    LanguageManager.getManager().fireLanguageChanged(value);

    // retablissement des poprietes units
    value = Configuration.getConfig().getProperty("units", "distance");
    UnitManager.getManager().fireDistanceChanged(value);

    value = Configuration.getConfig().getProperty("units", "speed");
    UnitManager.getManager().fireSpeedPaceChanged(value);

    value = Configuration.getConfig().getProperty("units", "height");
    UnitManager.getManager().fireHeightChanged(value);

    value = Configuration.getConfig().getProperty("units", "weight");
    UnitManager.getManager().fireWeightChanged(value);
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
      jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
      jContentPane.add(getJPanelButton(), BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes jSplitPane
   * 
   * @return javax.swing.JSplitPane
   */
  private JSplitPane getJSplitPane() {
    if (jSplitPane == null) {
      jSplitPane = new JSplitPane();
      jSplitPane.setLeftComponent(getJTreePref());
      jSplitPane.setRightComponent(getJPanelLeft());
      jSplitPane.setDividerLocation(150);
    }
    return jSplitPane;
  }

  /**
   * This method initializes jPanelButton
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      jPanelButton = new JPanel();
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      jPanelButton.setLayout(flowLayout);
      jPanelButton.add(getJButtonOK(), null);
      jPanelButton.add(getJButtonCancel(), null);
    }
    return jPanelButton;
  }

  /**
   * This method initializes jButtonOK
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonOK() {
    if (jButtonOK == null) {
      jButtonOK = new JButton();
      jButtonOK.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonOK;
  }

  /**
   * This method initializes jButtonCancel
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonCancel() {
    if (jButtonCancel == null) {
      jButtonCancel = new JButton();
      jButtonCancel.setText("Annuler");
      jButtonCancel.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonCancel;
  }

  /**
   * This method initializes jTree
   * 
   * @return javax.swing.JTree
   */
  private JTreePref getJTreePref() {
    if (jTreePref == null) {
      jTreePref = new JTreePref(this);
    }
    return jTreePref;
  }

  /**
   * This method initializes jPanelLeft
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelLeft() {
    if (jPanelLeft == null) {
      jPanelLeft = new JPanel();
      jPanelLeft.setLayout(new FlowLayout());
    }
    return jPanelLeft;
  }

} // @jve:decl-index=0:visual-constraint="10,10"
