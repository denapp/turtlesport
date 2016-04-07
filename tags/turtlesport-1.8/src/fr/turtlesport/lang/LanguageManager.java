package fr.turtlesport.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.UIManager;

import fr.turtlesport.Configuration;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class LanguageManager {
  private static TurtleLogger               log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(LanguageManager.class);
  }

  /** Les languages. */
  private static final ILanguage[]          ILANGUAGES = { LanguageFr
          .getInstance(),
//      LanguageUs.getInstance(),
      LanguageEn.getInstance(),
      LanguageSv.getInstance(),
      LanguageEs.getInstance(),
      LanguageCa.getInstance(),
      LanguageDe.getInstance(),
      LanguageHu.getInstance(),
      LanguageIt.getInstance(),
      LanguageNl.getInstance(),
      LanguagePt.getInstance(),
      LanguageZh.getInstance()};

  private static HashMap<String, ILanguage> mapLang;

  /** Liste des listeners. */
  private List<LanguageListener>            listeners  = new ArrayList<LanguageListener>();

  private static LanguageManager            singleton  = new LanguageManager();

  private ILanguage                         currentLang;

  /**
   * 
   */
  private LanguageManager() {
    mapLang = new HashMap<String, ILanguage>();
    for (ILanguage l : ILANGUAGES) {
      mapLang.put(l.toString(), l);
    }

    // Langage par defaut
    currentLang = LanguageEn.getInstance();

    // Ajout langage
    addLanguageListener(CommonLang.INSTANCE);
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
   * Restitue le <code>Locale</code> du langage.
   * 
   * @return le <code>Locale</code> du langage.
   */
  public Locale getLocale() {
    return currentLang.getLocale();
  }

  /**
   * Restitue les languages.
   * 
   * @return les languages.
   */
  public ILanguage[] getLanguages() {
    return ILANGUAGES;
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
      if (log.isDebugEnabled()) {
        log.debug(">>addLanguageListener " + l.getClass());
      }
    }
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
    Locale.setDefault(lang.getLocale());
    UIManager.put("OptionPane.cancelButtonText", lang.cancel());
  }

  /**
   * D&eacute;clenche le changement de language.
   * 
   * @param name
   *          nom du language.
   */
  public void fireLanguageChanged(String name) {
    log.debug(">>fireLanguageChanged name=" + name);

    ILanguage lang = mapLang.get(name);
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

}
