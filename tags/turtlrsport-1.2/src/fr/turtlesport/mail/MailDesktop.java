package fr.turtlesport.mail;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class MailDesktop implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MailDesktop.class);
  }

  public MailDesktop() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getIcon()
   */
  public ImageIcon getIcon() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getName()
   */
  public String getName() {
    return "Mail Desktop";
  }
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(fr.turtlesport.mail.MessageMail)
   */
  public void mail(MessageMail message) throws IOException {
    try {
      Class<?> clazz = Class.forName("java.awt.Desktop");
      Object objDesktop = clazz.getMethod("getDesktop").invoke(clazz);

      Method method = clazz.getMethod("mail", URI.class);
      method.invoke(objDesktop, new URI(message.toMailtoURIEncoded()));
    }
    catch (Throwable e) {
      log.error("", e);
      throw new IOException(e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail()
   */
  public void mail() throws IOException {
    try {
      Class<?> clazz = Class.forName("java.awt.Desktop");
      Object objDesktop = clazz.getMethod("getDesktop").invoke(clazz);

      Method method = clazz.getMethod("mail");
      method.invoke(objDesktop);
    }
    catch (Throwable cause) {
      log.error("", cause);
      throw new IOException(cause.getMessage());
    }
  }

  /**
   * D&etarmine si cette jvm permet d'ouvrir le client mail d&eacute;faut. A
   * partir de java 1.6.
   * 
   * @return <code>true</code> si le client mail est valable, <code>false</code>
   *         sinon.
   */
  public static boolean isAvailable() {
    try {
      Class<?> classDesktop = Class.forName("java.awt.Desktop");

      Boolean b = (Boolean) classDesktop.getMethod("isDesktopSupported")
          .invoke(classDesktop);
      if (!b.booleanValue()) {
        return false;
      }

      Object objDesktop = classDesktop.getMethod("getDesktop")
          .invoke(classDesktop);
      Class<?> classAction = Class.forName("java.awt.Desktop$Action");
      Method method = classDesktop.getMethod("isSupported", classAction);

      Object enumMail = null;
      for (Object c : classAction.getEnumConstants()) {
        if ("MAIL".equals(c.toString())) {
          enumMail = c;
          break;
        }
      }

      b = (Boolean) method.invoke(objDesktop, enumMail);
      return b.booleanValue();
    }
    catch (Throwable e) {
      return false;
    }
  }

}
