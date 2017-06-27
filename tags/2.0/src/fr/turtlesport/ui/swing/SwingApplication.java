package fr.turtlesport.ui.swing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import fr.turtlesport.Configuration;
import fr.turtlesport.ConfigurationException;
import fr.turtlesport.ProxyConfiguration;
import fr.turtlesport.db.DatabaseManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.update.Update;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.LocationException;
import fr.turtlesport.util.ResourceBundleUtility;
import fr.turtlesport.util.SystemProperties;

/**
 * @author Denis Apparicio
 * 
 */
public class SwingApplication {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(SwingApplication.class);
  }

  private JSplashScreen       splash;

  /**
   * D&eacute;marrage de l'application.
   */
  public void startIt() {
    log.debug(">>startIt");

    ResourceBundle rb;

    Future<Boolean> futureHasUpdate = null;
    ExecutorService execute = Executors.newSingleThreadExecutor();

    try {
      // Initialisation des proprietes
      SystemProperties.configure();

      // Initialisation des localisations
      Location.initialize();

      // Chargement du fichier de configuration.
      Configuration.initialize();

      // Mis a jour du look an feel
      SwingLookAndFeel.setDefaultLookAndFeel();

      // Affichage splash screen
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      createSplashScreen(screenSize);
      showSplashScreen();

      // mis a jour du proxy
      ProxyConfiguration.configure();

      // Recherche des mises a jour
      futureHasUpdate = execute.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          return Update.checkAtBoot();
        }
      });
    }
    catch (LocationException e) {
      splash.updateError(e.getMessage());
      splash.pause();
      JShowMessage.error(splash, e.getMessage());
      splash.dispose();
      stopIt();
    }
    catch (ConfigurationException e) {
      rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
          .getCurrentLang(), getClass());
      splash.updateError(rb.getString("splashError"));
      splash.pause();
      JShowMessage.error(splash, rb.getString("errorConfigFile"));
      splash.dispose();
      stopIt();
    }

    // Changement de langage
    LanguageManager.getManager().fireLanguageChanged();
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    // Initialisation de la database.
    splash.updateProgress(rb.getString("splashLoading"));

    // Initialisation de la database.
    try {
      DatabaseManager.initDatabase(splash);
      RunTableManager.getInstance().exist(0);
    }
    catch (SQLException e) {
      log.error("Initialisation", e);

      splash.updateError(rb.getString("splashError"));
      splash.pause();

      boolean isAlreayRunning = false;
      Throwable th = e.getCause();
      while (th != null && th instanceof SQLException) {
        SQLException sqle = (SQLException) th;
        if ("XSDB6".equals(sqle.getSQLState())) {
          // instance de derby deja boote
          splash.toBack();
          JShowMessage.error(splash, rb.getString("errorAlreadyRun"));
          isAlreayRunning = true;
          break;
        }
        th = th.getCause();
      }

      if (!isAlreayRunning) {
        splash.toBack();
        JShowMessage.error(splash, rb.getString("errorInitDatabase"));
      }

      splash.dispose();
      stopIt();
    }

    // Affichage de l'IHM
    if (!futureHasUpdate.isDone()) {
      splash.updateProgress(rb.getString("splashUpdate"));
      try {
        futureHasUpdate.get(2, TimeUnit.SECONDS);
      }
      catch (Throwable e) {
      }
    }

    splash.updateProgress(rb.getString("splashOpen"));
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createMainGUI(Toolkit.getDefaultToolkit().getScreenSize());
        SwingApplication.this.releaseSplashScreen();
        SwingApplication.this.showGUI();
      }
    });

    log.debug("<<startIt");
  }

  /**
   * Stop de l'application.
   */
  public void stopIt() {
    // fermeture de la database
    try {
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }
    catch (SQLException e) {
      if (!"XJ015".equals(e.getSQLState())) {
        log.error("", e);
      }
    }
    finally {
      System.exit(0);
    }
  }

  /**
   * Creation du splash screen
   */
  private void createSplashScreen(Dimension screenSize) {
    splash = new JSplashScreen();
    Dimension splashSize = splash.getSize();
    if (splashSize.height > screenSize.height) {
      splashSize.height = screenSize.height;
    }
    if (splashSize.width > screenSize.width) {
      splashSize.width = screenSize.width;
    }
    splash.setLocation((screenSize.width - splashSize.width) / 2,
                       (screenSize.height - splashSize.height) / 2);
  }

  /**
   * Affichage du splash screen
   */
  private void showSplashScreen() {
    if (SwingUtilities.isEventDispatchThread()) {
      splash.setVisible(true);
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          splash.setVisible(true);
        }
      });
    }
  }

  /**
   * Suppression du splash screen
   */
  private void releaseSplashScreen() {
    if (SwingUtilities.isEventDispatchThread()) {
      splash.dispose();
      splash = null;
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          splash.dispose();
          splash = null;
        }
      });
    }    
  }

  /**
   * Creation de l'IHM principale (Frame)
   */
  private void createMainGUI(Dimension screenSize) {
    MainGui gui = new MainGui();
    Dimension frameSize = gui.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    gui.setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);

    gui.fireHistoric();
  }

  /**
   * Affichage de l'IHM
   * 
   * @param screenSize
   */
  private void showGUI() {
    MainGui.getWindow().setVisible(true);
    MainGui.getWindow().updateComponentTreeUI();
  }

}
