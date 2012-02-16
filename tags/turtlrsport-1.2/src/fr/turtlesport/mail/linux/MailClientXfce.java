package fr.turtlesport.mail.linux;

import java.io.IOException;

import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.util.Exec;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.OperatingSystem;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class MailClientXfce implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MailClientXfce.class);
  }

  protected MailClientXfce() {
    super();
    checkAvailable();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getName()
   */
  public String getName() {
    return "Mail XFCE";
  }
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getIcon()
   */
  public ImageIcon getIcon() {
    return null;
  }

  /**
   * D&eacute;termine si le client mail pard&eacute;fault sous xfce est valable.
   * 
   * @return <code>true</code> si le client mail pard&eacute;fault sous xfce est
   *         valable, <code>false</code> sinon.
   */
  protected static boolean isAvailable() {
    return OperatingSystem.isXfce() && Location.isInPath("exo-open");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(fr.turtlesport.mail.MessageMail)
   */
  public void mail(MessageMail message) throws IOException {
    log.debug(">>mail message");

    checkAvailable();
    Exec.exec("exo-open " + message.toMailtoURI());

    log.debug("<<mail");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail()
   */
  public void mail() throws IOException {
    checkAvailable();
    Exec.exec("exo-open mailto:");
  }

  private void checkAvailable() {
    if (!isAvailable()) {
      throw new IllegalAccessError();
    }
  }

}
