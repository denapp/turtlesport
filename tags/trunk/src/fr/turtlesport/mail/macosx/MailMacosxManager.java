package fr.turtlesport.mail.macosx;

import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.IMailManager;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public final class MailMacosxManager implements IMailManager {

  public MailMacosxManager() {
    if (!OperatingSystem.isMacOSX()) {
      throw new IllegalAccessError();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#isConfigurable()
   */
  public boolean isConfigurable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#getMailClients()
   */
  public IMailClient[] getMailClients() {
    IMailClient[] mailClients = new IMailClient[1];

    mailClients[0] = new MailClientMailApp();
    return mailClients;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#hasSystemDefaultMailAvalaible()
   */
  public boolean hasSystemDefaultMailAvalaible() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#getDefaultMail()
   */
  public IMailClient getDefaultMail() {
    if (MailClientMailApp.isAvailable()) {
      return new MailClientMailApp();
    }
    return new MailClientMacosxDefault();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#getMailClientNames()
   */
  public String[] getMailClientNames() {
    String[] names = { "OS X Mail" };
    return names;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#defaultMail()
   */
  public String defaultMailName() {
    return (MailClientMailApp.isAvailable()) ? "OS X Mail" : null;
  }

}
