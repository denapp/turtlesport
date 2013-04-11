package fr.turtlesport.lang;


/**
 * @author Denis Apparicio
 * 
 */
public class LanguageEvent {

  private ILanguage lang;

  /**
   * @param lang
   */
  public LanguageEvent(ILanguage lang) {
    super();
    this.lang = lang;
  }

  /**
   * Restitue le langage.
   * 
   * @return le langage.
   */
  public ILanguage getLang() {
    return lang;
  }

}
