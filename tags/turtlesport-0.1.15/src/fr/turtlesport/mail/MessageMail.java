package fr.turtlesport.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a message structure.
 * 
 * <p>
 * It consists of several necessary message fields.
 * 
 */
public class MessageMail {

  /** Adresse mail TO */
  private List<String>        toAddrs;

  /** Sujet */
  private String              subject;

  /** Body */
  private String              body;

  /** Fichiers joints */
  private List<File>          attachments;

  /**
   * Constructor of a <code>Message</code> object.
   */
  public MessageMail() {
    toAddrs = new ArrayList<String>();
    attachments = new ArrayList<File>();
  }

  /**
   * Gets an iterator of the message "To" address list.
   * 
   * @returen an <code>Iterator</code> object of the message "To" address
   *          list.
   */
  public List<String> getToAddrs() {
    return toAddrs;
  }

  /**
   * Sets the message "To" address list.
   * 
   * @param atoList
   *          an email address list for the "To" field.
   */
  public void addToAddrs(String toAddr) {
    if (!toAddrs.contains(toAddr)) {
      toAddrs.add(toAddr);
    }
  }

  /**
   * Gets the "Subject" field of the message.
   * 
   * @return the value of the "Subject" field.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the message "Subject" field.
   * 
   * @param asubject
   *          a string for the "Subject" field.
   * 
   */
  public void setSubject(String asubject) {
    subject = asubject;
  }

  /**
   * Gets the "Body" field of the message.
   * 
   * @return the value of the "Body" field.
   */
  public String getBody() {
    return body;
  }

  /**
   * Sets the message "Body" field.
   * 
   * @param abody
   *          a string for the "Body" field.
   */
  public void setBody(String abody) {
    body = abody;
  }

  /**
   * Gets an iterator of the message "Attachment" file list.
   * 
   * @return an <code>Iterator</code> object of the message "Attachment" file
   *         list.
   */
  public List<File> getAttachments() {
    return attachments;
  }

  /**
   * Sets the message "Attachments" field.
   * 
   * @param attachList
   *          the given attachment list, whose elements are the abosolute paths
   *          of files to be attached.
   * @throws IOException
   *           if any of the attached files is not readable.
   */
  public void addAttachment(File file) {
    // Ajoute le fichier
    if (!attachments.contains(file) && file.canRead() && !file.isHidden()
        && file.isFile()) {
      attachments.add(file);
    }
  }

  /**
   * Restitue l'URI mailto de ce message.
   * 
   * @return l'URI mailto de ce message.
   */
  public String toMailtoURI() {
    return toMailtoURI(false);
  }

  /**
   * Restitue l'URI mailto de ce message.
   * 
   * @return l'URI mailto de ce message.
   */
  public String toMailtoURIEncoded() {
    return toMailtoURI(true);
  }

  private String toMailtoURI(boolean isEncoded) {
    StringBuilder st = new StringBuilder();
    st.append("mailto:");
    for (String t : toAddrs) {
      st.append(t);
      st.append(',');
    }
    if (toAddrs.size() > 0) {
      st.deleteCharAt(st.length() - 1);
    }

    if (subject != null || body != null || attachments.size() > 0) {
      st.append('?');

      // sujet
      if (subject != null) {
        st.append("subject=");
        st.append((isEncoded) ? encode(subject) : subject);
        st.append("&");
      }

      // body
      if (body != null) {
        st.append("body=");
        st.append((isEncoded) ? encode(body) : body);
        st.append("&");
      }

      // attachement
      if (st.charAt(st.length() - 1) == '&') {
        st.deleteCharAt(st.length() - 1);
      }
      for (File f : attachments) {
        st.append("&attach=");
        st.append(f.getAbsolutePath());
      }      
    }
    return st.toString();
  }

  private String encode(String s) {
    return URLUTF8Encoder.encode(s);
  }
}
