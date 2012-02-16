package fr.turtlesport.mail.linux;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.mail.URLUTF8Encoder;
import fr.turtlesport.util.Exec;
import fr.turtlesport.util.Location;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class MailClientLinuxEvolution implements IMailClient {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MailClientLinuxEvolution.class);
  }

  protected MailClientLinuxEvolution() {
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
    return "Evolution";
  }
  
  /**
   * D&eacute;termine si le client mail est valable.
   * 
   * @return <code>true</code> si le client mail est valable, <code>false</code>
   *         sinon.
   */
  protected static boolean isAvailable() {
    return Location.isInPath("evolution");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#getIcon()
   */
  public ImageIcon getIcon() {
    return new ImageIcon(getClass().getResource("evolution-mail.png"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.MailClient#mail(fr.turtlesport.mail.Message)
   */
  public void mail(MessageMail message) throws IOException {
    log.debug(">>mail message");

    String[] cmdarray = new String[2];
    cmdarray[0] = "evolution";
    cmdarray[1] = "mailto:?"
                  + constructMailto(message.getToAddrs(),
                                    message.getSubject(),
                                    message.getBody(),
                                    message.getAttachments());
    log.error(cmdarray[1]);

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
    Exec.exec("evolution");
    log.debug("<<mail");
  }

  private String constructMailto(List<String> listTo,
                                 String subject,
                                 String body,
                                 List<File> listAttach) throws UnsupportedEncodingException {
    StringBuilder mailto = new StringBuilder();

    if (listTo != null) {
      for (String value : listTo) {
        mailto.append("to=");
        mailto.append(value);
        mailto.append("&");
      }
    }

    if (subject != null) {
      mailto.append("subject=");
      mailto.append(URLUTF8Encoder.encode(subject));
      mailto.append("&");
    }
    if (body != null) {
      mailto.append("body=");
      mailto.append(URLUTF8Encoder.encode(body));
      mailto.append("&");
    }
    if (listAttach != null) {
      for (File value : listAttach) {
        mailto.append("attach=");
        mailto.append(value.getAbsolutePath());
        mailto.append("&");
      }
    }

    return mailto.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(java.lang.String)
   */
  public void mail(String mailto) throws IOException {
  }
}
