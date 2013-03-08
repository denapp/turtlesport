package fr.turtlesport.mail.macosx;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;

import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.MailDesktop;
import fr.turtlesport.mail.MessageMail;

/**
 * @author Denis Apparicio
 * 
 */
public class MailClientMacosxDefault implements IMailClient {

  public MailClientMacosxDefault() {
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

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getName()
   */
  public String getName() {
    return "Mac OS X Mail";
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
      try {
        Class<?> clazz = Class.forName("com.apple.eio.FileManager");
        Method method = clazz.getMethod("openURL", String.class);
        method.invoke(clazz, message.toMailtoURI());
      }
      catch (Throwable e) {
        Runtime.getRuntime().exec("open " + message.toMailtoURI());
      }
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
      try {
        Class<?> clazz = Class.forName("com.apple.eio.FileManager");
        Method method = clazz.getMethod("openURL", String.class);
        method.invoke(clazz, "maito:");
      }
      catch (Throwable e) {
        Runtime.getRuntime().exec("open maito:");
      }
    }
  }

}
