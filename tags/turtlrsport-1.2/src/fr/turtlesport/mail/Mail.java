package fr.turtlesport.mail;

import java.io.IOException;

import fr.turtlesport.Configuration;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.linux.MailLinuxManager;
import fr.turtlesport.mail.macosx.MailMacosxManager;
import fr.turtlesport.mail.windows.MailWinManager;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public final class Mail {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Mail.class);
  }
  
  private static IMailManager manager;
  static {
    if (OperatingSystem.isLinux()) {
      manager = new MailLinuxManager();
    }
    else if (OperatingSystem.isWindows()) {
      manager = new MailWinManager();
    }
    else if (OperatingSystem.isMacOSX()) {
      manager = new MailMacosxManager();
    }
  }

  private Mail() {
  }

  /**
   * D&eacute;termine si cle client de messagerie est choisi.
   * 
   * @return <code>true</code> si choisi.
   */
  public static boolean isChoose() {
    return Configuration.getConfig().getPropertyAsBoolean("mail",
                                                          "isChoose",
                                                          false);
  }

  /**
   * D&eacute;termine si cle client de messagerie est choisi.
   * 
   * @return <code>true</code> si choisi.
   */
  public static void setChoose(String name) {
    Configuration.getConfig()
        .addProperty("mail", "isConfigurationDone", "true");
    Configuration.getConfig().addProperty("mail", "isChoose", "true");

    Configuration.getConfig().addProperty("mail", "client", name);
  }

  /**
   * Restitue le nom du mail par d&eacute;faut.
   * 
   * @return le nom du mail par d&eacute;faut.
   */
  public static String defaultMailName() {
    return manager.defaultMailName();
  }

  /**
   * D&eacute;termine si la fonction de mail est configurable.
   * 
   * @return <code>true</code> si la fonction de mail est configurable,
   *         <code>false</code> sinon.
   */
  public static boolean isConfigurable() {
    return manager.isConfigurable();
  }

  /**
   * D&eacute;termine si le mail a &eacute;t&eacute; configur&eacute;.
   * 
   * @return <code>true</code> si le mail a &eacute;t&eacute; configur&eacute;,
   *         <code>false</code> sinon.
   */
  public static boolean isConfigurationDone() {
    if (!isConfigurable()) {
      return true;
    }

    if (!Configuration.getConfig().getPropertyAsBoolean("mail",
                                                        "isConfigurationDone",
                                                        false)) {
      Configuration.getConfig().addProperty("mail",
                                            "isConfigurationDone",
                                            "true");
      return false;
    }
    return true;
  }

  /**
   * Restitue les noms des clients mail.
   * 
   * @return les noms des clients mail.
   */
  public static String[] getMailClientNames() {
    return manager.getMailClientNames();
  }

  /**
   * Restitue les clients mail.
   * 
   * @return les clients mail.
   */
  public static IMailClient[] getMailClients() {
    return manager.getMailClients();
  }

  /**
   * Restitue le mail client &agrave; partir de son nom.
   * 
   * @return e mail client &agrave; partir de son nom.
   */
  public static IMailClient getMailClient(String name) {
    if (name != null) {
      IMailClient[] clients = getMailClients();
      for (IMailClient m : clients) {
        if (name.equals(m.getName())) {
          return m;
        }
      }
    }
    return null;
  }

  /**
   * D&eacute;termine si le syst&egrave;me a un mail par d&eacute;faut.
   * 
   * @return <code>true</code> si le syst&egrave;me a un mail par d&eacute;faut,
   *         <code>false</code> sinon.
   */
  public static boolean hasSystemDefaultMailAvalaible() {
    return manager.hasSystemDefaultMailAvalaible();
  }

  /**
   * Restitue le mail par d&eacute;faut;.
   * 
   * @return <code></code> le
   */
  public static IMailClient getDefaultMail() {
    return manager.getDefaultMail();
  }

  /**
   * D&eacute;termine si la fonction de mail est support&eacute;.
   * 
   * @return <code>true</code> si la fonction de mail est support&eacute;.
   */
  public static boolean isSupported() {
    try {
      return (manager.getDefaultMail() != null);
    }
    catch (Throwable e) {
      log.error("", e);
      return false;
    }
  }

  /**
   * Ouverture du client mail avec un message.
   * 
   * @param message
   *          le message.
   */
  public static void mail(MessageMail message) throws IOException {
    checkSupported();
    manager.getDefaultMail().mail(message);
  }

  /**
   * Ouverture du client mail.
   */
  public static void mail() throws IOException {
    checkSupported();
    manager.getDefaultMail().mail();
  }

  private static void checkSupported() {
    if (!isSupported()) {
      throw new UnsupportedOperationException();
    }
  }

}
