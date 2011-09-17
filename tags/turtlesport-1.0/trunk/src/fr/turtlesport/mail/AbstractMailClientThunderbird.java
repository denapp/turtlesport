package fr.turtlesport.mail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Exec;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractMailClientThunderbird implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(AbstractMailClientThunderbird.class);
  }

  /**
   * Restitue la location de thunderbird.
   * 
   * @return la location de thunderbird.
   */
  public abstract String getLocation();

  /**
   * Determine si thunderbird est deja ouvert.
   * 
   * @throws IOException
   *           si erreur.
   * @return <code>true</code> si thunderbird toune, <code>false</code> sinon.s
   */
  public abstract boolean isRunning() throws IOException;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getIcon()
   */
  public ImageIcon getIcon() {
    return new ImageIcon(AbstractMailClientThunderbird.class
        .getResource("thunderbird.png"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.MailClient#mail(fr.turtlesport.mail.Message)
   */
  public void mail(MessageMail message) throws IOException {
    log.debug(">>mail message");

    String[] cmdarray = new String[3];
    cmdarray[0] = getLocation();

    if (isRunning()) {
      cmdarray[1] = "-remote";
      cmdarray[2] = "xfeDoCommand(composeMessage,"
                    + constructMailto(message.getToAddrs(), message
                        .getSubject(), message.getBody(), message
                        .getAttachments()) + ")";
    }
    else {
      cmdarray[1] = "-compose";
      cmdarray[2] = constructMailto(message.getToAddrs(),
                                    message.getSubject(),
                                    message.getBody(),
                                    message.getAttachments());
    }

    if (log.isDebugEnabled()) {
      log.debug("cmdarray[0]=" + cmdarray[0]);
      log.debug("cmdarray[1]=" + cmdarray[1]);
      log.debug("cmdarray[2]=" + cmdarray[2]);
    }

    Exec.exec(cmdarray);

    log.debug("<<mail");
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

  private String constructMailto(List<String> listTo,
                                 String subject,
                                 String body,
                                 List<File> listAttach) {
    StringBuilder mailto = new StringBuilder();

    boolean has = false;
    if (listTo != null && listTo.size() > 0) {
      has = true;
      mailto.append("to=\"");
      for (String value : listTo) {
        mailto.append(value);
        mailto.append(",");
      }
      mailto.deleteCharAt(mailto.length() - 1);
      mailto.append("\"");
    }

    if (subject != null) {
      if (has) {
        mailto.append(',');
      }
      has = true;
      mailto.append("subject='");
      mailto.append(subject);
      mailto.append("'");
    }
    if (body != null) {
      if (has) {
        mailto.append(',');
      }
      mailto.append("body='");
      mailto.append(body);
      mailto.append("'");
    }
    if (listAttach != null && listAttach.size() > 0) {
      if (has) {
        mailto.append(',');
      }
      mailto.append("attachment='");
      for (File value : listAttach) {
        mailto.append("file://");
        mailto.append(value.getAbsolutePath());
        mailto.append(",");
      }
      mailto.deleteCharAt(mailto.length() - 1);
      mailto.append("'");

    }

    return mailto.toString();
  }

}
