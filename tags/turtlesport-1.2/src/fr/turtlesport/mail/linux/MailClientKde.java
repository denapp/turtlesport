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
public class MailClientKde implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MailClientKde.class);
  }

  /** Commande kmailservice ou kde-open */
  private String              command;

  protected MailClientKde() {
    super();
    checkAvailable();

    if (Location.isInPath("kde-open")) {
      command = "kmailservice";
    }
    else {
      command = "kde-open";
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getName()
   */
  public String getName() {
    return "Mail KDE";
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
   * D&eacute;termine si le client mail pard&eacute;fault sous kde est valable.
   * 
   * @return <code>true</code> si le client mail pard&eacute;fault sous kde est
   *         valable, <code>false</code> sinon.
   */
  protected static boolean isAvailable() {
    return OperatingSystem.isKde()
           && (Location.isInPath("kmailservice") || Location
               .isInPath("kde-open"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(fr.turtlesport.mail.MessageMail)
   */
  public void mail(MessageMail message) throws IOException {
    log.debug(">>mail message");

    checkAvailable();
    String cmd = command + " " + message.toMailtoURI();
    log.debug("cmd=" + cmd);

    Exec.exec(cmd);

    log.debug("<<mail");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail()
   */
  public void mail() throws IOException {
    checkAvailable();
    Exec.exec(command + " mailto:");
  }

  private void checkAvailable() {
    if (!isAvailable()) {
      throw new IllegalAccessError();
    }
  }

}
