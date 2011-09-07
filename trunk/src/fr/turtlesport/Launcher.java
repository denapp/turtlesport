package fr.turtlesport;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.SwingApplication;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public final class Launcher {
  private static TurtleLogger log;

  /** instance unique */
  private static Launcher     singleton = new Launcher();

  /** GUI. */
  private SwingApplication    gui;

  /**
   * Constructeur par defaut
   */
  private Launcher() {
  }

  /**
   * 
   * @return Restitue une instance unique de <code>Application</code>
   */
  public static Launcher getInstance() {
    return singleton;
  }

  /**
   * Demarre l'application.
   */
  public void startIt() {
    // gui
    gui = new SwingApplication();
    gui.startIt();
  }

  /**
   * Arrete l'application.
   */
  public void stopIt() {
    Configuration.getConfig().exit();
    gui.stopIt();
  }

  private static void logProperty(String name) {
    log.warn(name + "=" + System.getProperty(name));
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {

      // gui mac
      if (OperatingSystem.isMacOSX()) {
        // Configuration application Mac OS X
        MacOSXTurleApp.configure();
      }

      try {
        Location.initialize();
      }
      catch (Throwable e) {
        e.printStackTrace();
      }

      // positionne les traces
      String dirExe = Location.dirNameExecution(Launcher.class);
      File file = new File(dirExe, "log4J.xml");
      DOMConfigurator.configure(file.toURI().toURL());
      log = (TurtleLogger) TurtleLogger.getLogger(Launcher.class);
      log.debug(">>main");

      // log
      log.warn("Turtle Sport v"+Version.VERSION);
      logProperty("prop application.home");
      logProperty("java.vendor");
      logProperty("java.runtime.name");
      logProperty("java.version");
      logProperty("java.vm.version");
      logProperty("prop application.home");
      logProperty("os.name");
      logProperty("os.arch");

      // demarre l'application
      Launcher.getInstance().startIt();

      log.debug("<<main");
    }
    catch (Throwable e) {
      log.error("", e);
    }
  }

}
