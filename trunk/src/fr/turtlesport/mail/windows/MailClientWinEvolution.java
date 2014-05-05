package fr.turtlesport.mail.windows;

import java.io.File;
import java.io.IOException;

import fr.turtlesport.mail.AbstractMailClientEvolution;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.reg.RegistryWin;

/**
 * @author Denis Apparicio
 * 
 */
public class MailClientWinEvolution extends AbstractMailClientEvolution {
  private String location;

  /**
   * 
   */
  protected MailClientWinEvolution() {
    location = location();
    if (location == null) {
      throw new IllegalAccessError();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.AbstractMailClientThunderbird#getLocation()
   */
  @Override
  public String getLocation() {
    return location;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.AbstractMailClientThunderbird#mail()
   */
  @Override
  public void mail() throws IOException {
    new MailClientWinDefault().mail();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.AbstractMailClientThunderbird#mail(fr.turtlesport.mail.MessageMail)
   */
  @Override
  public void mail(MessageMail message) throws IOException {
    if (message.getAttachments() == null
        || message.getAttachments().size() == 0) {
      // Seul l'attachement pose probleme : on prend le standard
      new MailClientWinDefault().mail(message);
    }
    else {
      super.mail(message);
    }
  }

  /**
   * D&eacute;termine si le client mail est valable.
   * 
   * @return <code>true</code> si le client mail est valable,
   *         <code>false</code> sinon.
   */
  protected static boolean isAvailable() {
    return location() != null;
  }

  private static String location() {
    String path = ":\\Program Files\\mozilla.org\\Evolution\\evolution.exe";
    File f = new File("C" + path);
    if (f.isFile()) {
      return f.getPath();
    }
    f = new File("D" + path);
    if (f.isFile()) {
      return f.getPath();
    }

    String command = RegistryWin.localMachine()
        .get("Software\\Clients\\Mail\\evolution.exe\\shell\\Open\\command",
             null);
    if (command != null) {
      command = command.replace('\"', ' ');
    }
    return command;
  }

}
