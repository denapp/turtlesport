package fr.turtlesport.ui.swing;

import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import fr.turtlesport.Configuration;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.img.macosx.ImagesMacosxRepository;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public final class SwingLookAndFeel {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(SwingLookAndFeel.class);
  }

  static {
    // Les icones de JOptionPane sont mal geres sous MacoSX suivant la version
    // de JVM
    if (OperatingSystem.isMacOSX()) {
      UIManager.put("OptionPane.errorIcon",
                    ImagesMacosxRepository.getImageIcon("error.png"));
      UIManager.put("OptionPane.informationIcon",
                    ImagesMacosxRepository.getImageIcon("info.png"));
      UIManager.put("OptionPane.questionIcon",
                    ImagesMacosxRepository.getImageIcon("question.png"));
      UIManager.put("OptionPane.warningIcon",
                    ImagesMacosxRepository.getImageIcon("warning.png"));
    }
  }

  private SwingLookAndFeel() {
  }

  /**
   * Restitue le nom du look and feele courant.
   * 
   * @return le nom du look and feele courant.
   */
  public static String getCurrentLookAndFeel() {
    return UIManager.getLookAndFeel().getName();
  }

  /**
   * Restitue la liste des look and feel. Pour MacosX on ne restitue que le look
   * and feel MacosX.
   * 
   * @return la liste des look and feel.
   */
  public static String[] getLookAndFeel() {
    String[] res;
    ArrayList<String> list = null;

    if (OperatingSystem.isMacOSX()) {
      list = new ArrayList<String>();
      list.add(getCurrentLookAndFeel());
    }
    else {
      UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
      if (infos != null) {
        list = new ArrayList<String>();
        for (UIManager.LookAndFeelInfo info : infos) {
          list.add(info.getName());
        }
      }
    }

    if (list == null || list.size() == 0) {
      res = new String[0];
    }
    else {
      res = new String[list.size()];
      list.toArray(res);
    }

    return res;
  }

  /**
   * Valorise un look and feel.
   * 
   */
  public static void setLookAndFeel(String name) {
    log.debug(">>setLookAndFeel name=" + name);

    try {
      UIManager.setLookAndFeel(getLookAndFeelClassName(name));
      SwingUtilities.updateComponentTreeUI(MainGui.getWindow());
    }
    catch (Exception e) {
      log.error("set LookAndFeel", e);
    }

    log.debug("<<setLookAndFeel name=" + name);
  }

  /**
   * Valorise le look and feel par d&eacute;faut.
   */
  public static void setDefaultLookAndFeel() {
    log.debug(">>setDefaultLookAndFeel");

    // recuperation du look and feel dans le .ini
    String value = getProperty();
    try {
      if (value == null || "".equals(value.trim())) {
        if (OperatingSystem.isLinux()) {
          try {
            UIManager.setLookAndFeel(getLookAndFeelClassName("Nimbus"));
          }
          catch(Throwable e) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
          }
        }
        else {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());          
        }
      }
      else if (!value.equals(getCurrentLookAndFeel())
               && getLookAndFeelClassName(value) != null) {
        UIManager.setLookAndFeel(getLookAndFeelClassName(value));
      }
      setProperty(getCurrentLookAndFeel());
    }
    catch (Exception e) {
      log.error("set LookAndFeel", e);
    }
    log.debug("<<setDefaultLookAndFeel");
  }

  /**
   * D&eacute;termine si look and feel CD/Motif.
   * 
   * @return <code>true</code> si look and feel CD/Motif, <code>false</code>
   *         sinon.
   * 
   */
  public static boolean isLookAndFeelMotif() {
    return "CDE/Motif".equals(UIManager.getLookAndFeel().getName());
  }

  /**
   * D&eacute;termine si look and feel Mac OS X.
   * 
   * @return <code>true</code> si look and feel Mac OS X, <code>false</code>
   *         sinon.
   * 
   */
  public static boolean isLookAndFeelMacOSX() {
    return "Mac OS X".equals(UIManager.getLookAndFeel().getName());
  }

  /**
   * Valorise un look and feel.
   * 
   */
  public static String getLookAndFeelClassName(String name) {
    log.debug(">>getLookAndFeelClassName name=" + name);

    if (name == null || "".equals(name)) {
      throw new IllegalArgumentException();
    }

    String className = null;

    UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
    if (infos != null) {
      for (UIManager.LookAndFeelInfo info : infos) {
        if (info.getName().equals(name)) {
          className = info.getClassName();
          break;
        }
      }
    }

    log.debug("<<getLookAndFeelClassName className=" + className);
    return className;
  }

  private static String getProperty() {
    return Configuration.getConfig().getProperty("general", "lookandfeel");
  }

  private static void setProperty(String value) {
    Configuration.getConfig().addProperty("general", "lookandfeel", value);
  }

}
