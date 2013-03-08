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
public class MailClientGnome implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MailClientGnome.class);
  }

  protected MailClientGnome() {
    super();
    checkAvailable();
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
    return "Mail Gnome";
  }
  /**
   * D&eacute;termine si le client mail pard&eacute;fault sous gnome est
   * valable.
   * 
   * @return <code>true</code> si le client mail pard&eacute;fault sous gnome
   *         est valable, <code>false</code> sinon.
   */
  protected static boolean isAvailable() {
    return OperatingSystem.isGnome() && Location.isInPath("gnome-open");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(fr.turtlesport.mail.MessageMail)
   */
  public void mail(MessageMail message) throws IOException {
    log.debug(">>mail message");

    checkAvailable();
    Exec.exec("gnome-open " + message.toMailtoURI());

    log.debug("<<mail");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail()
   */
  public void mail() throws IOException {
    checkAvailable();
    Exec.exec("gnome-open mailto:");
  }

  private void checkAvailable() {
    if (!isAvailable()) {
      throw new IllegalAccessError();
    }
  }

}
