package fr.turtlesport.db;

import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public final class DataLanguageRessource implements LanguageListener {

  private static DataLanguageRessource singleton = new DataLanguageRessource();

  private ResourceBundle               rb;

  /**
   * 
   */
  private DataLanguageRessource() {
    // Ajouter directement dans LanguageManager.
  }

  /**
   * Restitue une instance unique.
   * 
   * @return
   */
  public static DataLanguageRessource getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang.LanguageEvent)
   */
  public void languageChanged(final LanguageEvent event) {
    rb = ResourceBundleUtility.getBundle(event.getLang(), getClass());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  /**
   * Restitue la chaine pour la <code>key</code>.
   * 
   * @param key
   * @return
   */
  public String getString(String key) {
    return rb.getString(key);
  }

}
