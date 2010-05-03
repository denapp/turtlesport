package fr.turtlesport.lang;

/**
 * @author Denis Apparicio
 * 
 */
public interface LanguageListener {

  /**
   * Invoqu&eacute; lorsque le language change.
   */
  void languageChanged(LanguageEvent event);

  /**
   * Invoqu&eacute; lorsque le listener est supprim&eacute;.
   */
  void completedRemoveLanguageListener();

}
