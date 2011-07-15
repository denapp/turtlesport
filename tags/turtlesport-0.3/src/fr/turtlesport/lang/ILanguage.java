package fr.turtlesport.lang;

import java.text.DateFormat;
import java.util.Locale;

import javax.swing.ImageIcon;

/**
 * @author Denis Apparicio
 * 
 */
public interface ILanguage {

  /**
   * Restitue le <code>Locale</code> du langage.
   * 
   * @return le <code>Locale</code> du langage.
   */
  Locale getLocale();

  /**
   * Restitue le drapeau du langage.
   * 
   * @return le drapeau du langage.
   */
  ImageIcon getFlag();

  /**
   * Restitue le nom de ce langage.
   * 
   * @return le nom de ce langage.
   */
  String getName();

  /**
   * Restitue le formatter pour une date (JJ/MM/AAAA).
   * 
   * @return le formatter pour une date.
   */
  DateFormat getDateFormatter();

  /**
   * Restitue le formatter pour une date sans separator.
   * 
   * @return le formatter pour une date.
   */
  DateFormat getDateTimeFormatterWithoutSep();

  /**
   * Restitue le libell&eacute, pour non.
   * 
   * @return le libell&eacute, pour non.
   */
  String no();

  /**
   * Restitue le libell&eacute, pour oui.
   * 
   * @return le libell&eacute, pour oui.
   */
  String yes();

  /**
   * Restitue le libell&eacute, pour annuler.
   * 
   * @return le libell&eacute, pour annuler.
   */
  String cancel();

  /**
   * Restitue le libell&eacute, pour ok.
   * 
   * @return le libell&eacute, pour ok.
   */
  String ok();

  /**
   * D&eacut;termine si le site web est traduit.
   * 
   * @return <code>true</code> si e site web est traduit.
   */
  boolean hasWebSiteTranslate();

  /**
   * Restitue l'encoding des fichiers properties.
   * 
   * @return  l'encoding des fichiers properties.
   */
  String getEncoding();

}
