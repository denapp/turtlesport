package fr.turtlesport.mail.windows;

import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.IMailManager;
import fr.turtlesport.reg.RegistryWin;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public final class MailWinManager implements IMailManager {

  public MailWinManager() {
    if (!OperatingSystem.isWindows()) {
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
    IMailClient[] mailClients = new IMailClient[3];

    mailClients[0] = new MailClientWinOutlook();
    mailClients[1] = new MailClientWinThunderbird();
    mailClients[2] = new MailClientWinEvolution();

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
    String defaultMailer = RegistryWin.localMachine()
        .get("SOFTWARE\\Clients\\Mail", "");

    if (defaultMailer != null) {
      if (defaultMailer.equalsIgnoreCase("Mozilla Thunderbird")
          && MailClientWinThunderbird.isAvailable()) {
        return new MailClientWinThunderbird();
      }
      if ((defaultMailer.equalsIgnoreCase("Microsoft Outlook") || defaultMailer
          .equalsIgnoreCase("Outlook Express"))) {
        if (MailClientWinOutlook.isAvailable()) {
          return new MailClientWinOutlook();
        }
        else if (MailClientWinDefault.isAvailable()) {
          return new MailClientWinDefault();
        }
      }
    }

    return new MailClientWinDefault();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#getMailClientNames()
   */
  public String[] getMailClientNames() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#defaultMail()
   */
  public String defaultMailName() {
    return null;
  }

}
