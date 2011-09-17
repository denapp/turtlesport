package fr.turtlesport.mail.macosx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.util.Exec;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class MailClientMailApp implements IMailClient {

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
    return "Mail";
  }

  /**
   * D&eacute;termine si le client mail est valable.
   * 
   * @return <code>true</code> si le client mail est valable,
   *         <code>false</code> sinon.
   */
  protected static boolean isAvailable() {
    return new File("/Applications/Mail.app").exists();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail(fr.turtlesport.mail.MessageMail)
   */
  public void mail(MessageMail message) throws IOException {
    if (message == null) {
      mail();
      return;
    }

    if (message.getToAddrs().size() == 0 && message.getSubject() == null
        && message.getBody() == null && message.getAttachments().size() > 0) {
      // que des fichiers attaches
      ArrayList<String> list = new ArrayList<String>();
      list.add("open");
      list.add("-a");
      list.add("Mail.app");
      for (File f : message.getAttachments()) {
        list.add(f.getAbsolutePath());
      }

      String[] cmdArray = new String[list.size()];
      list.toArray(cmdArray);

      Exec.exec(cmdArray);
      return;
    }

    // Execution du script Mail.app
    InputStreamReader inr = new InputStreamReader(getClass()
        .getResourceAsStream("mailApp.scpt"));
    BufferedReader reader = new BufferedReader(inr);
    StringWriter writer = new StringWriter();
    String st;
    while ((st = reader.readLine()) != null) {
      if (st.contains("_subject_")) {
        String value = (message.getSubject() != null) ? message.getSubject()
            : "";
        st = st.replaceFirst("_subject_", value);

        value = (message.getBody() != null) ? message.getBody() : "";
        st = st.replaceFirst("_body_", value + "\r\n");

        writer.write(st);
        writer.write("\r\n");
      }
      else if (st.contains("_toAddress_")) {
        if (message.getToAddrs().size() > 0) {
          StringBuilder sb = new StringBuilder();
          for (String t : message.getToAddrs()) {
            sb.append(t);
            sb.append(", ");
          }
          writer.write(st.replaceFirst("_toAddress_", sb.substring(0, sb
              .length() - 2)));
          writer.write("\r\n");
        }
      }
      else if (st.contains("_filename_")) {
        if (message.getAttachments().size() > 0) {
          for (File f : message.getAttachments()) {
            writer.write(st.replaceFirst("_filename_", f.getAbsolutePath()));
            writer.write("\r\n");
          }
        }
      }
      else {
        writer.write(st);
        writer.write("\r\n");
      }
    }

    // sauvegarde du script
    File f = new File(Location.userLocation(), "mailApp.scpt");
    FileWriter fw = new FileWriter(f);
    fw.write(writer.toString());
    fw.close();

    // Execution du script
    String[] cmdArray = new String[2];
    cmdArray[0] = "osascript";
    cmdArray[1] = f.getPath();
    Exec.exec(cmdArray);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.IMailClient#mail()
   */
  public void mail() throws IOException {
    new MailClientMailApp().mail();
    // Exec.exec("open -a Mail.app");
  }

}
