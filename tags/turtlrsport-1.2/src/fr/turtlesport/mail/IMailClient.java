package fr.turtlesport.mail;

import java.io.IOException;

import javax.swing.ImageIcon;

/**
 * @author denis
 * 
 */
public interface IMailClient {

  /**
   * Ouverture du client mail avec un message.
   * 
   * @param message
   *          le message.
   */
  void mail(MessageMail message) throws IOException;

  /**
   * Ouverture du client mail.
   */
  void mail() throws IOException;

  /**
   * Restitue l'image de ce mail.
   * 
   * @return l'image de ce mail.
   */
  public ImageIcon getIcon();

  /**
   * Restitue le nom du client mail.
   * 
   * @return le nom du client mail.
   */
  public String getName();
}
