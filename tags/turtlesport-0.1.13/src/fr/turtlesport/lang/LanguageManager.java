package fr.turtlesport.lang;

import java.util.ArrayList;

import javax.swing.JComponent;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataLanguageRessource;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class LanguageManager {
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(LanguageManager.class);
  }

  /** Libelle des languages. */
  private static final String[]       LANGUAGES = { LanguageFr.getInstance()
          .getName(),
      LanguageEn.getInstance().getName(),
      LanguageSv.getInstance().getName()       };

  /** Liste des listeners. */
  private ArrayList<LanguageListener> listeners = new ArrayList<LanguageListener>();

  private static LanguageManager      singleton = new LanguageManager();

  private ILanguage                   currentLang;

  /**
   * 
   */
  private LanguageManager() {
    // Langage par defaut
    currentLang = LanguageFr.getInstance();

    // Ajout langage
    addLanguageListener(DataLanguageRessource.getInstance());
    fireLanguageChanged(currentLang);
  }

  /**
   * Restitue une instance du manager de language.
   * 
   * @return une instance du manager de language.
   */
  public static LanguageManager getManager() {
    return singleton;
  }

  /**
   * Restitue le langage courant.
   * 
   * @return le langage courant.
   */
  public ILanguage getCurrentLang() {
    return currentLang;
  }

  /**
   * 
   * Restitue le libell&eacute; du langage courant.
   * 
   * @return le libell&eacute; du langage courant.
   */
  public String getCurrentLanguageName() {
    return currentLang.getName();
  }

  /**
   * Restitue les libell&eacute; des locales.
   * 
   * @return les libell&eacute; des locales.
   */
  public String[] getLibelleLocales() {
    return LANGUAGES;
  }

  /**
   * Ajoute un listener.
   * 
   * @param l
   *          le listener &agrave; ajouter.
   */
  public void addLanguageListener(LanguageListener l) {
    if (l != null) {
      listeners.add(l);
    }
    log.debug(">>addLanguageListener " + l.getClass());
  }

  /**
   * Supprime un listener.
   * 
   * @param l
   *          le listener &agrave; supprimer.
   */
  public boolean removeLanguageListener(LanguageListener l) {
    if (l == null) {
      return false;
    }
    boolean bRes = listeners.remove(l);
    log.debug(">>removeLanguageListener " + l.getClass() + "-->" + bRes);
    l.completedRemoveLanguageListener();
    return true;
  }

  /**
   * D&eacute;clenche le changement de langage.
   * 
   * @param lang
   *          le langage.
   */
  public void fireLanguageChanged(ILanguage lang) {
    this.currentLang = lang;
    LanguageEvent e = new LanguageEvent(lang);
    for (LanguageListener l : listeners) {
      l.languageChanged(e);
    }
    // composant
    JComponent.setDefaultLocale(lang.getLocale());
  }

  /**
   * D&eacute;clenche le changement de language.
   * 
   * @param name
   *          nom du language.
   */
  public void fireLanguageChanged(String name) {
    log.debug(">>fireLanguageChanged name=" + name);

    ILanguage lang = getLang(name);
    if (lang != null) {
      fireLanguageChanged(lang);
    }

    log.debug("<<fireLanguageChanged");
  }

  /**
   * D&eacute;clenche le changement de language par d&eacute;fault.
   * 
   * @param locale
   *          le language.
   */
  public void fireLanguageChanged() {
    log.debug(">>fireLanguageChanged");

    String value = Configuration.getConfig().getProperty("general", "language");
    fireLanguageChanged(value);

    log.debug("<<fireLanguageChanged");
  }

  /**
   * Restitue le langage en fonction de son nom.
   * 
   * @param name
   *          nom du langage.
   * @return le langage.
   */
  private ILanguage getLang(String name) {
    if (name == null || "".equals(name)) {
      return null;
    }

    if (LanguageEn.getInstance().getName().equals(name)) {
      return LanguageEn.getInstance();
    }
    if (LanguageFr.getInstance().getName().equals(name)) {
      return LanguageFr.getInstance();
    }
    if (LanguageSv.getInstance().getName().equals(name)) {
      return LanguageSv.getInstance();
    }

    return null;
  }

}
