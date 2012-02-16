package fr.turtlesport.mail;

/**
 * @author Denis Apparicio
 * 
 */
public interface IMailManager {
  String DEFAULT_EMAIL_SYSTEM = "systemDefaultEmail";

  /**
   * D&eacute;termine si la fonction de mail est configurable.
   * 
   * @return <code>true</code> si la fonction de mail est configurable,
   *         <code>false</code> sinon.
   */
  boolean isConfigurable();

  /**
   * Restitue les noms des clients mail.
   * 
   * @return les noms des clients mail.
   */
  String[] getMailClientNames();

  /**
   * Restitue les clients mail.
   * 
   * @return les clients mail.
   */
  IMailClient[] getMailClients();

  /**
   * D&eacute;termine si le syst&egrave;me a un mail par d&eacute;faut.
   * 
   * @return <code>true</code> si le syst&egrave;me a un mail par d&eacute;faut,
   *         <code>false</code> sinon.
   */
  boolean hasSystemDefaultMailAvalaible();

  /**
   * Restitue le mail par d&eacute;faut;.
   * 
   * @return <code></code> le
   */
  IMailClient getDefaultMail();

  /**
   * Restitue le nom du mail par d&eacute;faut.
   * 
   * @return le nom du mail par d&eacute;faut.
   */
  String defaultMailName();
}
