package fr.turtlesport.mail.windows;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.reg.RegistryWin;
import fr.turtlesport.util.Library;

/**
 * @author Denis Apparicio
 * 
 */
public class MailClientWinOutlook implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MailClientWinOutlook.class);
  }

  private static boolean      isInit = false;

  /**
   * 
   */
  protected MailClientWinOutlook() {
    super();
    if (!isAvailable()) {
      throw new IllegalAccessError();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getName()
   */
  public String getName() {
    return "Outlook";
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
   * @return <code>true</code> si le client mail est valable,
   *         <code>false</code> sinon.
   */
  protected static boolean isAvailable() {
    String value = RegistryWin.localMachine()
        .get("SOFTWARE\\Microsoft\\Windows Messaging Subsystem", "MAPI");
    return ("1".equals(value));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail()
   */
  public void mail() throws IOException {
    new MailClientWinDefault().mail();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(fr.turtlesport.mail.MessageMail)
   */
  public void mail(final MessageMail message) throws IOException {
    if (!isInit) {
      // chargement de la librairie
      Library.load(getClass(), "turtleMail");
      isInit = true;
    }

    if ((message.getAttachments() == null || message.getAttachments().size() == 0)) {
      // Seul l'attachement pose probleme : on prend le standard
      new MailClientWinDefault().mail(message);
      return;
    }

    new Thread(new Runnable() {
      public void run() {
        String[] toArray = null;
        String[] attachArray = null;

        // to
        List<String> listTo = message.getToAddrs();
        if (listTo != null && listTo.size() > 0) {
          toArray = new String[listTo.size()];
          listTo.toArray(toArray);
        }

        // attachement
        List<File> listFile = message.getAttachments();
        if (listFile != null && listFile.size() > 0) {
          attachArray = new String[listFile.size()];
          for (int i = 0; i < listFile.size(); i++) {
            attachArray[i] = listFile.get(i).getAbsolutePath();
          }
        }

        try {
          openMapiMailerInner(toArray,
                              null,
                              null,
                              message.getSubject(),
                              message.getBody(),
                              attachArray);
        }
        catch (IOException e) {
          log.error("", e);
        }
      }

    }).start();

  }

  /**
   * Opens the system default mailer with relevant information filled in.
   * 
   * @param toArray
   *          the email address array of the "To" field.
   * @param ccArray
   *          the email address array of the "Cc" field.
   * @param bccArray
   *          the email address array of the "Bcc" field.
   * @param subject
   *          the string of the "Subject" field.
   * @param body
   *          the string of the "Body" field.
   * @param attachArray
   *          the array of the abosolute paths of the attached files.
   */
  private native void openMapiMailerInner(String[] toArray,
                                          String[] ccArray,
                                          String[] bccArray,
                                          String subject,
                                          String body,
                                          String[] attachArray) throws IOException;

}
