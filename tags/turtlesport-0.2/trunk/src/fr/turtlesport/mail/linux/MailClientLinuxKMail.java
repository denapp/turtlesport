package fr.turtlesport.mail.linux;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.util.Exec;
import fr.turtlesport.util.Location;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class MailClientLinuxKMail implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MailClientLinuxKMail.class);
  }

  protected MailClientLinuxKMail() {
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
    return new ImageIcon(getClass()
        .getResource("kmail.png"));
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getName()
   */
  public String getName() {
    return "KMail";
  }

  /**
   * D&eacute;termine si le client mail est valable.
   * 
   * @return <code>true</code> si le client mail est valable, <code>false</code>
   *         sinon.
   */
  protected static boolean isAvailable() {
    return Location.isInPath("kmail");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.MailClient#mail(fr.turtlesport.mail.Message)
   */
  public void mail(MessageMail message) throws IOException {
    log.debug(">>mail message");

    ArrayList<String> listCmd = new ArrayList<String>();
    listCmd.add("kmail");

    constructMailto(listCmd,
                    message.getToAddrs(),
                    message.getSubject(),
                    message.getBody(),
                    message.getAttachments());

    String[] cmdarray = new String[listCmd.size()];
    listCmd.toArray(cmdarray);

    Exec.exec(cmdarray);

    log.debug(cmdarray);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.MailClient#mail()
   */
  public void mail() throws IOException {
    log.debug(">>mail");
    Exec.exec("thunderbird");
    log.debug("<<mail");
  }

  private String constructMailto(List<String> listCmd,
                                 List<String> listTo,
                                 String subject,
                                 String body,
                                 List<File> listAttach) throws UnsupportedEncodingException {
    StringBuilder mailto = new StringBuilder();

    if (listTo != null) {
      for (String value : listTo) {
        listCmd.add(value);
      }
    }

    if (subject != null) {
      listCmd.add("--subject");
      listCmd.add(subject);
    }
    if (body != null) {
      listCmd.add("--body");
      listCmd.add(body);
    }
    if (listAttach != null) {
      for (File value : listAttach) {
        listCmd.add("--attach");
        listCmd.add("file://" + value.getAbsolutePath());
      }
    }

    return mailto.toString();
  }

}
