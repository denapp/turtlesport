package fr.turtlesport.mail.windows;

import java.io.IOException;

import javax.swing.ImageIcon;

import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.MailDesktop;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.util.LaunchWinApp;

/**
 * @author Denis Apparicio
 * 
 */
public class MailClientWinDefault implements IMailClient {

  public MailClientWinDefault() {
    super();
    if (!isAvailable()) {
      throw new IllegalAccessError();
    }
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
   * D&eacute;termine si le client mail est valable.
   * 
   * @return <code>true</code> si le client mail est valable, <code>false</code>
   *         sinon.
   */
  protected static boolean isAvailable() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(fr.turtlesport.mail.MessageMail)
   */
  public void mail(MessageMail message) throws IOException {
    if (MailDesktop.isAvailable()) {
      new MailDesktop().mail(message);
    }
    else {
      LaunchWinApp.launch("open", message.toMailtoURIEncoded());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail()
   */
  public void mail() throws IOException {
    if (MailDesktop.isAvailable()) {
      new MailDesktop().mail();
    }
    else {
      LaunchWinApp.launch("open", "mailto:");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getName()
   */
  public String getName() {
    return null;
  }

}
