package fr.turtlesport.mail.linux;

import fr.turtlesport.Configuration;
import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.IMailManager;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public final class MailLinuxManager implements IMailManager {

  /**
   * 
   */
  public MailLinuxManager() {
    if (!OperatingSystem.isLinux()) {
      throw new IllegalAccessError();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#getMailClientNames()
   */
  public String[] getMailClientNames() {
    String[] names = { "Thunderbird", "KMail", "Evolution" };
    return names;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#getMailClients()
   */
  public IMailClient[] getMailClients() {
    IMailClient[] mailClients = new IMailClient[3];

    mailClients[0] = new MailClientLinuxThunderbird();
    mailClients[1] = new MailClientLinuxKMail();
    mailClients[2] = new MailClientLinuxEvolution();

    return mailClients;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#defaultMail()
   */
  public String defaultMailName() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#isConfigurable()
   */
  public boolean isConfigurable() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#hasSystemDefaultMailAvalaible()
   */
  public boolean hasSystemDefaultMailAvalaible() {
    return MailClientKde.isAvailable() || MailClientGnome.isAvailable()
           || MailClientXfce.isAvailable();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailManager#getDefaultMail()
   */
  public IMailClient getDefaultMail() {

    String defaultClient = Configuration.getConfig().getProperty("mail",
                                                                 "client");

    if (defaultClient == null) {// pas de mail par defaut
      if (MailClientKde.isAvailable()) {
        // mail par defaut de kde
        return new MailClientKde();
      }
      if (MailClientGnome.isAvailable()) {
        // mail par defaut de gome
        return new MailClientGnome();
      }
      if (MailClientXfce.isAvailable()) {
        // mail par defaut de xfce
        return new MailClientXfce();
      }
    }
    else {
      if ("Thunderbird".equals(defaultClient)
          && MailClientLinuxEvolution.isAvailable()) {
        return new MailClientLinuxThunderbird();
      }
      if ("Evolution".equals(defaultClient)
          && MailClientLinuxEvolution.isAvailable()) {
        return new MailClientLinuxEvolution();
      }
      if ("KMail".equals(defaultClient) && MailClientLinuxKMail.isAvailable()) {
        return new MailClientLinuxKMail();
      }
    }

    if (MailClientLinuxEvolution.isAvailable()) {
      return new MailClientLinuxThunderbird();
    }
    if (MailClientLinuxEvolution.isAvailable()) {
      return new MailClientLinuxEvolution();
    }
    if (MailClientLinuxKMail.isAvailable()) {
      return new MailClientLinuxKMail();
    }

    return null;
  }

}
