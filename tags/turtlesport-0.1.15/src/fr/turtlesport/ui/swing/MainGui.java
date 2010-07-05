package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import fr.turtlesport.Configuration;
import fr.turtlesport.Launcher;
import fr.turtlesport.MacOSXTurleApp;
import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.UserTableManager;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.protocol.A1000RunTransferProtocol;
import fr.turtlesport.ui.swing.component.JMenuItemTurtle;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JXSplitButton;
import fr.turtlesport.ui.swing.component.calendar.JPanelCalendar;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.img.menu.ImagesMenuRepository;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.ui.swing.model.ModelRunCalendar;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.update.Update;
import fr.turtlesport.util.BrowserUtil;
import fr.turtlesport.util.OperatingSystem;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class MainGui extends JFrame implements LanguageListener {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MainGui.class);
  }

  private static MainGui       window;

  private JPanel               jContentPane;

  private JMenuBar             jMenuBar;

  private JMenu                jMenuFile;

  private JMenu                jMenuRun;

  private JMenuItemTurtle      jMenuItemRunDetail;

  private JMenuItemTurtle      jMenuItemQuit;

  private JMenu                jMenuAbout;

  private JMenuItemTurtle      jMenuItemAbout;

  private JMenuItemTurtle      jMenuItemCheckUpdate;

  private JMenuItemTurtle      jMenuItemPreference;

  private JMenuItemTurtle      jMenuItemNet;

  private Status               status;

  private Status               statusCode;

  private JPanel               jPanelStatusCode;

  private JPanel               jPanelSouth;

  private JSplitPane           jSplitPaneCenter;

  private JToolBar             jToolBar;

  private JButton              jButtonRetreive;

  private JButton              jButtonPreference;

  private JButton              jButtonPrefUser;

  private JButton              jButtonStat;

  private MainGuiMouseListener quitMouseListener;

  private MainGuiMouseListener aboutMouseListener;

  private MainGuiMouseListener prefMouseListener;

  private MainGuiMouseListener retreiveMouseListener;

  private MainGuiMouseListener userPrefMouseListener;

  private MainGuiMouseListener siteMouseListener;

  private MainGuiMouseListener usersMouseListener;

  private JMenuItemTurtle      jMenuItemRunGoogleEarth;

  private JMenuItemTurtle      jMenuItemRunDelete;

  private JMenuItemTurtle      jMenuItemRunSave;

  private JMenu                jMenuRunExport;

  private JMenuItemTurtle      jMenuItemRunExportGpx;

  private JMenuItemTurtle      jMenuItemRunExportGoogleEarth;

  private JMenuItemTurtle      jMenuItemRunExportTcx;

  private JMenuItemTurtle      jMenuItemRunExportHst;

  private JMenuItemTurtle      jMenuItemRunMap;

  private JMenuItem            jMenuItemRunImport;

  private JMenuItem            jMenuItemRunAdd;

  private JMenuItemTurtle      jMenuItemMail;

  private Properties           menuProperties;

  private JMenuItemTurtle      jMenuItemRunEmail;

  private JMenuItem            jMenuItemDonate;

  private JXSplitButton        jXSplitButtonUser;

  private ButtonGroup          buttonGroupDropDown;

  private int                  currentIdUser = -1;

  private JMenuItemTurtle      jMenuItemTwitter;

  private JMenuItemTurtle jMenuItemRunGoogleMap;

  /**
   * 
   */
  public MainGui() {
    super();
    window = this;

    initialize();
  }

  public Properties getMenuProperties() {
    if (menuProperties == null) {
      // proprietes des menu
      menuProperties = new Properties();
      try {
        menuProperties.load(getClass().getResourceAsStream("jmenu.properties"));
      }
      catch (IOException e) {
        log.error("", e);
      }
    }
    return menuProperties;
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
    ResourceBundle rb = ResourceBundleUtility.getBundle(lang, getClass());

    prefMouseListener.setMessage(rb.getString("prefMouseListener"));
    retreiveMouseListener.setMessage(rb.getString("retreiveMouseListener"));
    userPrefMouseListener.setMessage(rb.getString("userPrefMouseListener"));
    siteMouseListener.setMessage(rb.getString("siteMouseListener"));
    usersMouseListener.setMessage(rb.getString("usersMouseListener"));

    if (!OperatingSystem.isMacOSX()) {
      aboutMouseListener.setMessage(rb.getString("aboutMouseListener"));
      jMenuFile.setText(rb.getString("jMenuFile"));
      jMenuItemQuit.setText(rb.getString("jMenuItemQuit"));
      jMenuItemAbout.setText(rb.getString("jMenuItemAbout"));
      jMenuItemPreference.setText(rb.getString("jMenuItemPreference"));
      quitMouseListener.setMessage(rb.getString("quitMouseListener"));
    }

    jMenuRun.setText(rb.getString("jMenuRun"));
    jMenuItemRunDetail.setText(rb.getString("jMenuItemRunDetail"));
    jMenuItemRunMap.setText(rb.getString("jMenuItemRunMap"));
    if (jMenuItemRunEmail != null) {
      jMenuItemRunEmail.setText(rb.getString("jMenuItemRunEmail"));
    }
    jMenuItemRunGoogleEarth.setText(rb.getString("jMenuItemRunGoogleEarth"));
    jMenuRunExport.setText(rb.getString("jMenuRunExport"));
    jMenuItemRunExportGpx.setText(rb.getString("jMenuItemRunExportGpx"));
    jMenuItemRunExportGoogleEarth.setText(rb
        .getString("jMenuItemRunExportGoogleEarth"));
    jMenuItemRunExportTcx.setText(rb.getString("jMenuItemRunExportTcx"));
    jMenuItemRunExportHst.setText(rb.getString("jMenuItemRunExportHst"));

    jMenuItemRunImport.setText(rb.getString("jMenuItemRunImport"));
    jMenuItemRunAdd.setText(rb.getString("jMenuItemRunAdd"));
    jMenuItemRunSave.setText(rb.getString("jMenuItemRunSave"));
    jMenuItemRunDelete.setText(rb.getString("jMenuItemRunDelete"));

    jMenuItemNet.setText(rb.getString("jMenuItemNet"));
    jMenuItemTwitter.setText(rb.getString("jMenuItemTwitter"));
    if (jMenuItemMail != null) {
      jMenuItemMail.setText(rb.getString("jMenuItemMail"));
    }
    jMenuItemCheckUpdate.setText(rb.getString("jMenuItemCheckUpdate"));

    jMenuAbout.setText(rb.getString("jMenuAbout"));
    jMenuItemDonate.setText(rb.getString("jMenuItemDonate"));
    for (ActionListener al : jMenuItemDonate.getActionListeners()) {
      if (al instanceof NetAction) {
        ((NetAction) al).setUrl(rb.getString("donateUrl"));
      }
    }

    // mis a jour popup menu pour All user
    JCheckBoxMenuItem miAllUser = (JCheckBoxMenuItem) jXSplitButtonUser
        .getDropDownMenu().getComponent(0);
    miAllUser.setText(DataUser.getAllUser().getFirstName());

    if (currentIdUser == -1) {
      setTitle("Turtle Sport - " + miAllUser.getText());
    }
    setCurrentIdUser(currentIdUser);
  }

  /**
   * @return Returns the window.
   */
  public static MainGui getWindow() {
    return window;
  }

  /**
   * Restitue l'utilisateur courant.
   * 
   * @return l'utilisateur courant.
   */
  public int getCurrentIdUser() {
    return currentIdUser;
  }

  private void setCurrentIdUser(int currentIdUser) {
    if (this.currentIdUser != currentIdUser) {
      this.currentIdUser = currentIdUser;
      Configuration.getConfig().addProperty("general",
                                            "currentIdUser",
                                            Integer.toString(currentIdUser));
      // mis a jour des dates
      fireHistoric();

      // clear panel run si different de all user
      if (jSplitPaneCenter.getRightComponent() instanceof UserListener) {
        try {
          ((UserListener) jSplitPaneCenter.getRightComponent())
              .userSelect(currentIdUser);
          if (!DataUser.isAllUser(currentIdUser)) {
            ((JPanelCalendar) jSplitPaneCenter.getLeftComponent())
                .fireDatesUnselect();
          }
        }
        catch (SQLException e) {
          log.error("", e);
          ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
              .getManager().getCurrentLang(), JPanelCalendar.class);
          JShowMessage.error(rb.getString("errorSQL"));
        }
      }
    }
  }

  /**
   * 
   */
  public void updateComponentTreeUI() {
    SwingUtilities.updateComponentTreeUI(this);
    status.addNotify();
    statusCode.addNotify();
  }

  /**
   * Attende avant swing runnable.
   */
  public void beforeRunnableSwing() {
    MainGui.getWindow().setCursor(Cursor
        .getPredefinedCursor(Cursor.WAIT_CURSOR));
  }

  /**
   * Attende avant swing runnable.
   */
  public void afterRunnableSwing() {
    MainGui.getWindow().setCursor(Cursor.getDefaultCursor());
  }

  /**
   * Rend les menus de courses activable ou non.
   * 
   * @param b
   *          <code>true</code> pour activer les menus de course.
   */
  public void setEnableMenuRun(boolean b) {
    getJMenuItemRunDetail().setEnabled(b);
    getJMenuItemRunMap().setEnabled(b);
    getJMenuItemRunGoogleEarth().setEnabled(b);
    getJMenuItemRunGoogleMap().setEnabled(b);
    if (jMenuItemRunEmail != null) {
      jMenuItemRunEmail.setEnabled(b);
    }
    getJMenuRunExport().setEnabled(b);
    getJMenuItemRunExportGpx().setEnabled(b);
    getJMenuItemRunExportGoogleEarth().setEnabled(b);
    getJMenuItemRunExportTcx().setEnabled(b);
    getJMenuItemRunExportHst().setEnabled(b);
    getJMenuItemRunSave().setEnabled(b);
    getJMenuItemRunDelete().setEnabled(b);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    log.debug(">>initialize");

    // on ajuste la taille car pas de menu sous mac os x
    this.setSize(1200, 727 - (OperatingSystem.isMacOSX() ? 27 : 0));
    this.setContentPane(getJContentPane());
    initJMenuBar();

    this.setFont(GuiFont.FONT_PLAIN);
    this.setIconImage(ImagesMenuRepository.getImage("turtle.png"));

    // Gestion des evenements
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        // on gere la mode macosx de ne pas fermer l'appli
        if (!OperatingSystem.isMacOSX()) {
          quit();
        }
        else {
          setState(Frame.ICONIFIED);
        }
      }

      @Override
      public void windowOpened(WindowEvent e) {
        if (Update.isCheckFirstEnd() && Update.checkFirst()) {
          ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
              .getManager().getCurrentLang(), MainGui.class);
          JShowMessage.ok(rb.getString("MessageUpdateYes"));
        }
      }
    });

    PrefAction prefAction = new PrefAction();
    prefMouseListener = new MainGuiMouseListener("Preferences");

    if (!OperatingSystem.isMacOSX()) {
      QuitAction quitAction = new QuitAction();
      jMenuItemQuit.addActionListener(quitAction);
      quitMouseListener = new MainGuiMouseListener("Quitter l'application");
      jMenuItemQuit.addMouseListener(quitMouseListener);

      AboutAction aboutAction = new AboutAction();
      jMenuItemAbout.addActionListener(aboutAction);
      aboutMouseListener = new MainGuiMouseListener("Affiche les informations de version");
      jMenuItemAbout.addMouseListener(aboutMouseListener);

      jMenuItemPreference.addActionListener(prefAction);
      jMenuItemPreference.addMouseListener(prefMouseListener);
    }

    jButtonPreference.addActionListener(prefAction);
    jButtonPreference.addMouseListener(prefMouseListener);

    RetreiveAction retreiveAction = new RetreiveAction();
    jButtonRetreive.addActionListener(retreiveAction);
    retreiveMouseListener = new MainGuiMouseListener("Recuperation des courses du Garmin");
    jButtonRetreive.addMouseListener(retreiveMouseListener);

    UserAction userAction = new UserAction();
    jButtonPrefUser.addActionListener(userAction);
    userPrefMouseListener = new MainGuiMouseListener("Profil utilisateur");
    jButtonPrefUser.addMouseListener(userPrefMouseListener);

    StatAction statAction = new StatAction();
    jButtonStat.addActionListener(statAction);

    ImportActionListener importAction = new ImportActionListener();
    jMenuItemRunImport.addActionListener(importAction);

    RunAddActionListener runAction = new RunAddActionListener();
    jMenuItemRunAdd.addActionListener(runAction);

    NetAction netAction = new NetAction("http://turtlesport.sourceforge.net");
    jMenuItemNet.addActionListener(netAction);
    siteMouseListener = new MainGuiMouseListener("Site web http://turtlesport.sourceforge.net");
    jMenuItemNet.addMouseListener(siteMouseListener);

    netAction = new NetAction("http://twitter.com/turtlesport");
    jMenuItemTwitter.addActionListener(netAction);
    jMenuItemTwitter
        .addMouseListener(new MainGuiMouseListener("http://twitter.com/turtlesport"));

    usersMouseListener = new MainGuiMouseListener("");
    jXSplitButtonUser.addMouseListener(usersMouseListener);

    if (jMenuItemMail != null) {
      MailAction mailAction = new MailAction();
      jMenuItemMail.addActionListener(mailAction);
    }

    jMenuItemCheckUpdate.addActionListener(new CheckUpdateAction());
    jMenuItemDonate.addActionListener(new DonationAction());

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());

    // recuperation de l'utilisateur courant
    int oldIdUser = Configuration.getConfig().getPropertyAsInt("general",
                                                               "currentIdUser",
                                                               -1);
    try {
      if (oldIdUser != -1 && !UserTableManager.getInstance().exist(oldIdUser)) {
        oldIdUser = -1;
      }
    }
    catch (SQLException sqle) {
      log.error("", sqle);
      oldIdUser = -1;
    }

    Enumeration<AbstractButton> e = buttonGroupDropDown.getElements();
    while (e.hasMoreElements()) {
      AbstractButton b = e.nextElement();
      if (b instanceof JCheckBoxMenuItemUser) {
        JCheckBoxMenuItemUser val = (JCheckBoxMenuItemUser) b;
        if (val.getIdUser() == oldIdUser) {
          buttonGroupDropDown.setSelected(val.getModel(), true);
          val.actionPerformed(null);
          break;
        }
      }
    }

    setRightComponent(new JPanelRun());
    setCurrentIdUser(oldIdUser);

    log.debug("<<initialize");
  }

  /**
   * This method initializes jJMenuBar
   * 
   * @return javax.swing.JMenuBar
   */
  private void initJMenuBar() {
    if (jMenuBar == null) {
      // proprietes des menu
      menuProperties = new Properties();
      try {
        menuProperties.load(getClass().getResourceAsStream("jmenu.properties"));
      }
      catch (IOException e) {
        log.error("", e);
      }

      jMenuBar = new JMenuBar();
      this.setJMenuBar(jMenuBar);

      if (!OperatingSystem.isMacOSX()) {
        jMenuBar.add(getJMenuFile());
        jMenuBar.add(getJMenuRun());
        jMenuBar.add(getJMenuAbout());
      }
      else {
        jMenuBar.add(getJMenuRun());
        MacOSXTurleApp.addWindowMenu();
        jMenuBar.add(getJMenuAbout());
      }

    }
  }

  /**
   * This method initializes jJMenuBar
   * 
   * @return javax.swing.JMenuBar
   */
  private JToolBar getJJToolbar() {
    if (jToolBar == null) {
      jToolBar = new JToolBar();
      jToolBar.add(getJButtonRetrieve());
      jToolBar.add(getJXSplitButtonUser());
      jToolBar.add(getJButtonPrefUser());
      jToolBar.add(getJButtonStat());
      jToolBar.addSeparator();
      jToolBar.add(getJButtonPreference());
    }
    return jToolBar;
  }

  /**
   * This method initializes jButtonRetreive
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonRetrieve() {
    if (jButtonRetreive == null) {
      jButtonRetreive = new JButton();
      jButtonRetreive.setIcon(ImagesMenuRepository.getImageIcon("down.png"));
      jButtonRetreive.setEnabled(true);
    }
    return jButtonRetreive;
  }

  /**
   * This method initializes jButtonStat
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonStat() {
    if (jButtonStat == null) {
      jButtonStat = new JButton();
      jButtonStat.setIcon(ImagesMenuRepository.getImageIcon("stat.png"));
      jButtonStat.setEnabled(true);
    }
    return jButtonStat;
  }

  /**
   * This method initializes jButtonPreference
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonPreference() {
    if (jButtonPreference == null) {
      jButtonPreference = new JButton();
      jButtonPreference.setIcon(ImagesMenuRepository.getImageIcon("prefs.png"));
      jButtonRetreive.setEnabled(true);
    }
    return jButtonPreference;
  }

  /**
   * This method initializes jButtonRetreive
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonPrefUser() {
    if (jButtonPrefUser == null) {
      jButtonPrefUser = new JButton();
      jButtonPrefUser.setIcon(ImagesMenuRepository.getImageIcon("config.png"));
    }
    return jButtonPrefUser;
  }

  private JXSplitButton getJXSplitButtonUser() {
    if (jXSplitButtonUser == null) {
      jXSplitButtonUser = new JXSplitButton(ImagesMenuRepository
          .getImageIcon("run.png"));
      jXSplitButtonUser.setFont(GuiFont.FONT_PLAIN);
      setUsers();
    }
    return jXSplitButtonUser;
  }

  private void setUsers() {
    List<DataUser> list = null;
    try {
      list = UserTableManager.getInstance().retreive();
    }
    catch (SQLException e) {
      log.error("", e);
      list = new ArrayList<DataUser>();
    }

    JPopupMenu jPopupMenuDropDown = new JPopupMenu();
    buttonGroupDropDown = new ButtonGroup();

    // Ajout all user
    JCheckBoxMenuItemUser jmiUser = new JCheckBoxMenuItemUser(DataUser
        .getAllUser().getId(), DataUser.getAllUser().getFirstName());
    buttonGroupDropDown.add(jmiUser);
    jPopupMenuDropDown.add(jmiUser);
    jPopupMenuDropDown.addSeparator();

    // Ajout des utilisateurs
    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        jmiUser = new JCheckBoxMenuItemUser(list.get(i).getId(),
                                            list.get(i).getFirstName() + " "
                                                + list.get(i).getLastName());
        buttonGroupDropDown.add(jmiUser);
        jPopupMenuDropDown.add(jmiUser);
      }
    }

    jXSplitButtonUser.setDropDownMenu(jPopupMenuDropDown);
  }

  /**
   * This method initializes jMenuFile
   * 
   * @return javax.swing.JMenu
   */
  private JMenu getJMenuFile() {
    if (jMenuFile == null) {
      jMenuFile = new JMenu();
      jMenuFile.setFont(GuiFont.FONT_PLAIN);
      jMenuFile.setText("Fichier");
      jMenuFile.add(getJMenuItemQuit());
    }
    return jMenuFile;
  }

  /**
   * This method initializes jMenuRun
   * 
   * @return javax.swing.JMenu
   */
  private JMenu getJMenuRun() {
    if (jMenuRun == null) {
      jMenuRun = new JMenu();
      jMenuRun.setFont(GuiFont.FONT_PLAIN);
      jMenuRun.add(getJMenuItemRunDetail());
      jMenuRun.add(getJMenuItemRunMap());
      if (Mail.isSupported()) {
        jMenuRun.add(getJMenuItemRunEmail());
      }
      jMenuRun.add(getJMenuItemRunGoogleEarth());
      jMenuRun.add(getJMenuItemRunGoogleMap());
      jMenuRun.addSeparator();
      jMenuRun.add(getJMenuRunExport());
      jMenuRun.add(getJMenuItemRunImport());
      jMenuRun.add(getJMenuItemRunAdd());
      jMenuRun.addSeparator();
      jMenuRun.add(getJMenuItemRunSave());
      jMenuRun.add(getJMenuItemRunDelete());
    }
    return jMenuRun;
  }

  /**
   * This method initializes jMenuItemQuit
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getJMenuItemQuit() {
    if (jMenuItemQuit == null) {
      jMenuItemQuit = new JMenuItemTurtle();
      jMenuItemQuit.setFont(GuiFont.FONT_PLAIN);
      jMenuItemQuit.setAccelerator(menuProperties, "jMenuItemQuit");
    }
    return jMenuItemQuit;
  }

  /**
   * This method initializes jMenuItemRunDetail.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunDetail() {
    if (jMenuItemRunDetail == null) {
      jMenuItemRunDetail = new JMenuItemTurtle();
      jMenuItemRunDetail.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunDetail.setAccelerator(menuProperties, "jMenuItemRunDetail");
      jMenuItemRunDetail.setEnabled(false);
    }
    return jMenuItemRunDetail;
  }

  /**
   * This method initializes jMenuItemRunMap.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunMap() {
    if (jMenuItemRunMap == null) {
      jMenuItemRunMap = new JMenuItemTurtle();
      jMenuItemRunMap.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunMap.setAccelerator(menuProperties, "jMenuItemRunMap");
      jMenuItemRunMap.setEnabled(false);
    }
    return jMenuItemRunMap;
  }

  /**
   * This method initializes jMenuItemRunGoogleEarth.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunGoogleEarth() {
    if (jMenuItemRunGoogleEarth == null) {
      jMenuItemRunGoogleEarth = new JMenuItemTurtle();
      jMenuItemRunGoogleEarth.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunGoogleEarth.setAccelerator(menuProperties,
                                             "jMenuItemRunGoogleEarth");
      jMenuItemRunGoogleEarth.setEnabled(false);
    }
    return jMenuItemRunGoogleEarth;
  }
  
  /**
   * This method initializes jMenuItemRunGoogleEarth.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunGoogleMap() {
    if (jMenuItemRunGoogleMap == null) {
      jMenuItemRunGoogleMap = new JMenuItemTurtle();
      jMenuItemRunGoogleMap.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunGoogleMap.setAccelerator(menuProperties,
                                             "jMenuItemRunGoogleMap");
      jMenuItemRunGoogleMap.setEnabled(false);
    }
    return jMenuItemRunGoogleMap;
  }

  /**
   * This method initializes jMenuItemRunEmail.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunEmail() {
    if (jMenuItemRunEmail == null) {
      jMenuItemRunEmail = new JMenuItemTurtle();
      jMenuItemRunEmail.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunEmail.setAccelerator(menuProperties, "jMenuItemRunEmail");
      jMenuItemRunEmail.setEnabled(false);
    }
    return jMenuItemRunEmail;
  }

  /**
   * This method initializes jMenuItemRunImport.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItem getJMenuItemRunImport() {
    if (jMenuItemRunImport == null) {
      jMenuItemRunImport = new JMenuItem();
      jMenuItemRunImport.setFont(GuiFont.FONT_PLAIN);
    }
    return jMenuItemRunImport;
  }

  /**
   * This method initializes jMenuItemRunImport.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItem getJMenuItemRunAdd() {
    if (jMenuItemRunAdd == null) {
      jMenuItemRunAdd = new JMenuItem();
      jMenuItemRunAdd.setFont(GuiFont.FONT_PLAIN);
    }
    return jMenuItemRunAdd;
  }

  /**
   * This method initializes jMenuRunExport.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenu getJMenuRunExport() {
    if (jMenuRunExport == null) {
      jMenuRunExport = new JMenu();
      jMenuRunExport.setFont(GuiFont.FONT_PLAIN);
      jMenuRunExport.add(getJMenuItemRunExportGpx());
      jMenuRunExport.add(getJMenuItemRunExportGoogleEarth());
      jMenuRunExport.add(getJMenuItemRunExportTcx());
      jMenuRunExport.add(getJMenuItemRunExportHst());
      jMenuRunExport.setEnabled(false);
    }
    return jMenuRunExport;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportGpx() {
    if (jMenuItemRunExportGpx == null) {
      jMenuItemRunExportGpx = new JMenuItemTurtle();
      jMenuItemRunExportGpx.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportGpx.setAccelerator(menuProperties,
                                           "jMenuItemRunExportGpx");
      jMenuItemRunExportGpx.setEnabled(false);
    }
    return jMenuItemRunExportGpx;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportTcx() {
    if (jMenuItemRunExportTcx == null) {
      jMenuItemRunExportTcx = new JMenuItemTurtle();
      jMenuItemRunExportTcx.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportTcx.setAccelerator(menuProperties,
                                           "jMenuItemRunExportTcx");
      jMenuItemRunExportTcx.setEnabled(false);
    }
    return jMenuItemRunExportTcx;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportHst() {
    if (jMenuItemRunExportHst == null) {
      jMenuItemRunExportHst = new JMenuItemTurtle();
      jMenuItemRunExportHst.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportHst.setAccelerator(menuProperties,
                                           "jMenuItemRunExportHst");
      jMenuItemRunExportHst.setEnabled(false);
    }
    return jMenuItemRunExportHst;
  }

  /**
   * This method initializes jMenuItemRunExportGpx.
   * 
   * @return javax.swing.JMenuItem
   */
  protected JMenuItemTurtle getJMenuItemRunExportGoogleEarth() {
    if (jMenuItemRunExportGoogleEarth == null) {
      jMenuItemRunExportGoogleEarth = new JMenuItemTurtle();
      jMenuItemRunExportGoogleEarth.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunExportGoogleEarth
          .setAccelerator(menuProperties, "jMenuItemRunExportGoogleEarth");
      jMenuItemRunExportGoogleEarth.setEnabled(false);
    }
    return jMenuItemRunExportGoogleEarth;
  }

  /**
   * This method initializes jMenuItemRunGoogleEarth.
   * 
   * @return javax.swing.JMenuItem
   */
  public JMenuItemTurtle getJMenuItemRunDelete() {
    if (jMenuItemRunDelete == null) {
      jMenuItemRunDelete = new JMenuItemTurtle();
      jMenuItemRunDelete.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunDelete.setAccelerator(menuProperties, "jMenuItemRunDelete");
      jMenuItemRunDelete.setEnabled(false);
    }
    return jMenuItemRunDelete;
  }

  /**
   * This method initializes jMenuItemRunSave.
   * 
   * @return javax.swing.JMenuItem
   */
  public JMenuItemTurtle getJMenuItemRunSave() {
    if (jMenuItemRunSave == null) {
      jMenuItemRunSave = new JMenuItemTurtle();
      jMenuItemRunSave.setFont(GuiFont.FONT_PLAIN);
      jMenuItemRunSave.setAccelerator(menuProperties, "jMenuItemRunSave");
      jMenuItemRunSave.setEnabled(false);
    }
    return jMenuItemRunSave;
  }

  /**
   * This method initializes jMenuApropos
   * 
   * @return javax.swing.JMenu
   */
  private JMenu getJMenuAbout() {
    if (jMenuAbout == null) {
      jMenuAbout = new JMenu();
      jMenuAbout.setText("?");
      jMenuAbout.setFont(GuiFont.FONT_PLAIN);

      jMenuAbout.add(getJMenuItemNet());
      if (Mail.isSupported()) {
        jMenuAbout.add(getJMenuItemMail());
      }
      jMenuAbout.add(getJMenuItemTwitter());
      jMenuAbout.add(getJMenuItemCheckUpdate());
      if (!OperatingSystem.isMacOSX()) {
        jMenuAbout.add(getJMenuItemPreference());
        jMenuAbout.add(getJMenuItemAbout());
      }
      jMenuAbout.addSeparator();
      jMenuAbout.add(getJMenuItemDonate());
    }
    return jMenuAbout;
  }

  /**
   * This method initializes jMenuItemAbout
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItemTurtle getJMenuItemAbout() {
    if (jMenuItemAbout == null) {
      jMenuItemAbout = new JMenuItemTurtle();
      jMenuItemAbout.setFont(GuiFont.FONT_PLAIN);
      jMenuItemAbout.setAccelerator(menuProperties, "jMenuItemAbout");
    }
    return jMenuItemAbout;
  }

  /**
   * This method initializes jMenuItemDonate
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getJMenuItemDonate() {
    if (jMenuItemDonate == null) {
      jMenuItemDonate = new JMenuItem();
      jMenuItemDonate.setFont(GuiFont.FONT_PLAIN);
    }
    return jMenuItemDonate;
  }

  /**
   * This method initializes jMenuItemCheckUpdate
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItemTurtle getJMenuItemCheckUpdate() {
    if (jMenuItemCheckUpdate == null) {
      jMenuItemCheckUpdate = new JMenuItemTurtle();
      jMenuItemCheckUpdate.setFont(GuiFont.FONT_PLAIN);
      jMenuItemCheckUpdate.setAccelerator(menuProperties, "jMenuItemAbout");
    }
    return jMenuItemCheckUpdate;
  }

  /**
   * This method initializes jMenuItemAbout
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItemTurtle getJMenuItemMail() {
    if (jMenuItemMail == null) {
      jMenuItemMail = new JMenuItemTurtle();
      jMenuItemMail.setFont(GuiFont.FONT_PLAIN);
      jMenuItemMail.setAccelerator(menuProperties, "jMenuItemMail");
    }
    return jMenuItemMail;
  }

  /**
   * This method initializes jMenuItemAbout
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItemTurtle getJMenuItemNet() {
    if (jMenuItemNet == null) {
      jMenuItemNet = new JMenuItemTurtle();
      jMenuItemNet.setFont(GuiFont.FONT_PLAIN);
      jMenuItemNet.setAccelerator(menuProperties, "jMenuItemNet");
    }
    return jMenuItemNet;
  }

  /**
   * This method initializes jMenuItemTwitter
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItemTurtle getJMenuItemTwitter() {
    if (jMenuItemTwitter == null) {
      jMenuItemTwitter = new JMenuItemTurtle();
      jMenuItemTwitter.setIcon(ImagesRepository.getImageIcon("twitter-t.jpg"));
      jMenuItemTwitter.setFont(GuiFont.FONT_PLAIN);
      jMenuItemNet.setAccelerator(menuProperties, "jMenuItemTwitter");
    }
    return jMenuItemTwitter;
  }

  /**
   * This method initializes jMenuItemPreference
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItemTurtle getJMenuItemPreference() {
    if (jMenuItemPreference == null) {
      jMenuItemPreference = new JMenuItemTurtle();
      jMenuItemPreference.setFont(GuiFont.FONT_PLAIN);
      jMenuItemPreference.setText("Preferences...");
      jMenuItemPreference.setAccelerator(menuProperties, "jMenuItemPreference");
    }
    return jMenuItemPreference;
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setOpaque(true);
      jContentPane.setLayout(new BorderLayout(5, 5));
      jContentPane.setBorder(javax.swing.BorderFactory
          .createBevelBorder(javax.swing.border.BevelBorder.RAISED));
      jContentPane.add(getJSplitPanelCenter(), BorderLayout.CENTER);
      jContentPane.add(getJPanelSouth(), BorderLayout.SOUTH);
      jContentPane.add(getJJToolbar(), BorderLayout.PAGE_START);
    }
    return jContentPane;
  }

  /**
   * This method initializes jPanelCenter.
   * 
   * @return javax.swing.JPanel
   */
  private JSplitPane getJSplitPanelCenter() {
    if (jSplitPaneCenter == null) {
      jSplitPaneCenter = new JSplitPane();
      jSplitPaneCenter.setOpaque(false);
      jSplitPaneCenter.setOneTouchExpandable(true);
      jSplitPaneCenter.setLeftComponent(new JPanel());
      jSplitPaneCenter.setRightComponent(new JPanel());
      jSplitPaneCenter.setDividerLocation(204);
    }
    return jSplitPaneCenter;
  }

  /**
   * This method initializes jPanelSouth
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelSouth() {
    if (jPanelSouth == null) {
      jPanelSouth = new JPanel();
      jPanelSouth.setLayout(new BorderLayout(5, 0));
      jPanelSouth.setBorder(BorderFactory.createLoweredBevelBorder());

      status = new Status();
      status.setFont(new JLabel().getFont());
      jPanelSouth.add(status, java.awt.BorderLayout.CENTER);

      jPanelSouth.add(getJPanelStatusCode(), java.awt.BorderLayout.WEST);
    }
    return jPanelSouth;
  }

  /**
   * This method initializes jPanelStatusCode
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelStatusCode() {
    if (jPanelStatusCode == null) {
      BorderLayout borderLayout = new BorderLayout();
      jPanelStatusCode = new JPanel();
      jPanelStatusCode.setLayout(borderLayout);
      jPanelStatusCode.setBorder(javax.swing.BorderFactory
          .createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

      statusCode = new Status();
      statusCode.setFont(new JLabel().getFont());
      statusCode.setSize(new Dimension(30, 15));
      jPanelStatusCode.add(statusCode, BorderLayout.CENTER);
    }
    return jPanelStatusCode;
  }

  /**
   * Quitte l'application.
   */
  private void quit() {
    Launcher.getInstance().stopIt();
  }

  /**
   * Action quitter l'application.
   * 
   * @author Denis Apparicio
   * 
   */
  private class QuitAction extends AbstractAction {

    public QuitAction() {
      super();
    }

    public void actionPerformed(ActionEvent e) {
      MainGui.this.quit();
    }
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class AboutAction extends AbstractAction {

    public AboutAction() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      // Affichage de l'IHM
      JDialogAbout.prompt();
    }
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class NetAction extends AbstractAction {
    private String url;

    public NetAction(String url) {
      super();
      this.url = url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      try {
        BrowserUtil.browse(new URI(url));
      }
      catch (URISyntaxException ue) {
      }
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class DonationAction extends AbstractAction {

    public DonationAction() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      try {
        String url;
        if (LanguageManager.getManager().getCurrentLang().hasWebSiteTranslate()) {
          url = "http://turtlesport.sourceforge.net/"
                + LanguageManager.getManager().getLocale().getLanguage()
                    .toUpperCase() + "/donation.html";

        }
        else {
          url = "http://turtlesport.sourceforge.net/EN/donation.html";
        }
        BrowserUtil.browse(new URI(url));
      }
      catch (URISyntaxException ue) {
      }
    }

  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class PrefAction extends AbstractAction {

    public PrefAction() {
      super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      // Affichage de l'IHM
      JDialogPreference.prompt();
    }
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class MainGuiMouseListener extends MouseAdapter {
    private String message;

    /**
     * @param message
     */
    public MainGuiMouseListener(String message) {
      this.message = message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
      MainGui.this.status.setText(message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
      MainGui.this.status.setText("");
    }
  }

  /**
   * Mis &agrave; jour des dates du calendrier. Si une date du calendrier
   * &eacute;tait d&eacute;j&agrave; selectionn&eacute;e, on garde la selection.
   */
  protected void fireHistoric() {
    JPanelCalendar panel = null;

    try {
      // mis a jour de la vue
      if (jSplitPaneCenter.getLeftComponent() instanceof JPanelCalendar) {
        panel = (JPanelCalendar) jSplitPaneCenter.getLeftComponent();
      }
      else {
        panel = new JPanelCalendar();
        panel.setModel(new ModelRunCalendar());
      }

      panel.fireHistoric(currentIdUser);
    }
    catch (SQLException e) {
      log.error("", e);
    }
    finally {
      if (!(jSplitPaneCenter.getLeftComponent() instanceof JPanelCalendar)) {
        jSplitPaneCenter.setLeftComponent(panel);
      }
    }
  }

  /**
   * Mis &agrave; jour des utilisateurs.
   */
  protected void fireUsers() {
    setUsers();
    try {
      if (currentIdUser == -1
          || !UserTableManager.getInstance().exist(currentIdUser)) {
        setCurrentIdUser(-1);
      }
      else {
        setCurrentIdUser(currentIdUser);
      }
    }
    catch (SQLException e) {
      log.error("", e);
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class RetreiveAction extends AbstractAction {

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    public void actionPerformed(ActionEvent e) {
      log.debug(">>actionPerformed");

      beforeRunnableSwing();

      new SwingWorker() {

        @Override
        public Object construct() {
          A1000RunTransferProtocol a1000 = new A1000RunTransferProtocol();

          try {
            a1000.init();
          }
          catch (Throwable th) {
            log.error("", th);
            JShowMessage.error(th.getMessage());
            return null;
          }

          // ui
          try {
            JDialogProgressRun dlg = new JDialogProgressRun(MainGui.getWindow(),
                                                            false);
            dlg.setLocationRelativeTo(MainGui.getWindow());
            dlg.setVisible(true);
            dlg.retreive(a1000);
          }
          catch (SQLException e) {
            log.error("", e);
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), MainGui.class);
            JShowMessage.error(rb.getString("errorSQL"));
          }
          return null;
        }

        @Override
        public void finished() {
          afterRunnableSwing();
        }

      }.start();

      log.debug("<<actionPerformed");
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class ImportActionListener implements ActionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {
      // ui
      JDialogImport.prompt();
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class RunAddActionListener implements ActionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionevent) {
      // ui
      try {
        JDialogAddRun.prompt();
      }
      catch (SQLException e) {
        log.error("", e);
      }
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class UserAction extends AbstractAction {

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    public void actionPerformed(ActionEvent e) {
      if (jSplitPaneCenter.getRightComponent() instanceof JPanelUserProfile) {
        return;
      }

      beforeRunnableSwing();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          try {
            JPanelUserProfile panel = new JPanelUserProfile();
            setRightComponent(panel);
            panel.updateView();
            MainGui.getWindow().setEnableMenuRun(false);
            // Pour les boutons de navigation avec CDE/Motif
            if (SwingLookAndFeel.isLookAndFeelMotif()) {
              MainGui.getWindow().updateComponentTreeUI();
            }
          }
          catch (SQLException e) {
            log.error("", e);
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), MainGui.class);
            JShowMessage.error(rb.getString("errorSQL"));
          }

          afterRunnableSwing();
        }
      });

    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class StatAction extends AbstractAction {

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    public void actionPerformed(ActionEvent e) {
      if (jSplitPaneCenter.getRightComponent() instanceof JPanelStat) {
        return;
      }

      beforeRunnableSwing();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          try {
            JPanelStat panel = new JPanelStat();
            setRightComponent(panel);
            panel.userSelect(currentIdUser);
            MainGui.getWindow().setEnableMenuRun(false);
            // Pour les boutons de navigation avec CDE/Motif
            if (SwingLookAndFeel.isLookAndFeelMotif()) {
              MainGui.getWindow().updateComponentTreeUI();
            }
          }
          catch (SQLException e) {
            log.error("", e);
            ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
                .getManager().getCurrentLang(), MainGui.class);
            JShowMessage.error(rb.getString("errorSQL"));
          }
          catch (Throwable e) {
            log.error("", e);
          }
          afterRunnableSwing();
        }
      });

    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MailAction extends AbstractAction {

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    public void actionPerformed(ActionEvent e) {
      try {
        if (!Mail.isConfigurationDone()) {
          ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
              .getManager().getCurrentLang(), MainGui.class);
          JShowMessage.ok(rb.getString("mailconfig"));
        }
        MessageMail msg = new MessageMail();
        msg.addToAddrs("turtlesport@free.fr");
        Mail.mail(msg);
      }
      catch (IOException ioe) {
        log.error("", ioe);
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class CheckUpdateAction extends AbstractAction {

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    public void actionPerformed(ActionEvent e) {
      beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
              .getManager().getCurrentLang(), MainGui.class);
          try {
            boolean hasUpdate = Update.check();
            afterRunnableSwing();
            if (hasUpdate) {
              JShowMessage.ok(rb.getString("MessageUpdateYes"), rb
                  .getString("MessageUpdateTitle"));
            }
            else {
              JShowMessage.ok(rb.getString("MessageUpdateNo"), rb
                  .getString("MessageUpdateTitle"));
            }
          }
          catch (IOException ioe) {
            log.error("", ioe);
            afterRunnableSwing();
            JShowMessage.error(rb.getString("MessageUpdateError"), rb
                .getString("MessageUpdateTitle"));
          }

        }
      });

    }

  }

  /**
   * @return
   */
  public JPanelCalendar getJPanelCalendar() {
    Object obj = jSplitPaneCenter.getLeftComponent();
    return (obj instanceof JPanelCalendar) ? (JPanelCalendar) obj : null;
  }

  /**
   * This method initializes jPanelCenter.
   * 
   * @return javax.swing.JPanel
   */
  public Object getRightComponent() {
    return getJSplitPanelCenter().getRightComponent();
  }

  public void setRightComponent(JPanel panel) {
    if (jSplitPaneCenter.getRightComponent() != null) {
      // Suppression des listeners
      if (!(panel instanceof JPanelRun)) {
        ModelPointsManager.getInstance().removeAllChangeListener();
        ModelMapkitManager.getInstance().removeAllChangeListener();
      }

      if (LanguageListener.class.isAssignableFrom(jSplitPaneCenter
          .getRightComponent().getClass())) {
        LanguageListener l = (LanguageListener) jSplitPaneCenter
            .getRightComponent();
        LanguageManager.getManager().removeLanguageListener(l);
      }
      if (UnitListener.class.isAssignableFrom(jSplitPaneCenter
          .getRightComponent().getClass())) {
        UnitListener l = (UnitListener) jSplitPaneCenter.getRightComponent();
        UnitManager.getManager().removeUnitListener(l);
      }
    }

    // Deselection des dates du calendar
    JPanelCalendar panelCalendar = getJPanelCalendar();
    if (panelCalendar != null) {
      panelCalendar.fireDatesUnselect();
    }

    jSplitPaneCenter.setRightComponent(panel);
    jSplitPaneCenter.setDividerLocation(jSplitPaneCenter.getDividerLocation());
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class JCheckBoxMenuItemUser extends JCheckBoxMenuItem implements
                                                               ActionListener {
    private int idUser;

    public JCheckBoxMenuItemUser(int idUser, String fullName) {
      super(fullName);
      this.idUser = idUser;
      addActionListener(JCheckBoxMenuItemUser.this);
    }

    public int getIdUser() {
      return idUser;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      MainGui.this.setTitle("Turtle Sport - " + getText());
      beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setCurrentIdUser(idUser);
          afterRunnableSwing();
        }
      });

    }

  }

}
