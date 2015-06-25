package fr.turtlesport;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.SwingApplication;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.OperatingSystem;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;

/**
 * @author Denis Apparicio
 * 
 */
public final class TurtleSport {
  private static TurtleLogger log;

  /** instance unique */
  private static TurtleSport     singleton = new TurtleSport();

  /** GUI. */
  private SwingApplication    gui;

  /**
   * Constructeur par defaut
   */
  private TurtleSport() {
  }

  /**
   * 
   * @return Restitue une instance unique de <code>Application</code>
   */
  public static TurtleSport getInstance() {
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
      String dirExe = Location.dirNameExecution(TurtleSport.class);
      File file = new File(dirExe, "log4J.xml");
      DOMConfigurator.configure(file.toURI().toURL());
      log = (TurtleLogger) TurtleLogger.getLogger(TurtleSport.class);
      log.debug(">>main");

      // log
      log.warn("Turtle Sport v"+Version.VERSION);
      logProperty("prop application.home");
      logProperty("java.vendor");
      logProperty("java.runtime.name");
      logProperty("java.version");
      logProperty("java.vm.name");
      logProperty("java.vm.version");
      logProperty("sun.arch.data.model"); 
      logProperty("prop application.home");
      logProperty("os.name");
      logProperty("os.arch");

      // demarre l'application
      TurtleSport.getInstance().startIt();

      log.debug("<<main");
    }
    catch (Throwable e) {
      log.error("", e);
    }
  }

}
