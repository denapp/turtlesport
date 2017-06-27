package fr.turtlesport.lang;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TemperatureUnit;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * Classe pour les langues.
 * 
 * @author Denis Apparicio
 * 
 */
public enum CommonLang implements LanguageListener {

  INSTANCE;

  private ResourceBundle rb;

  /**
   * @return Restitue l'altitude avec l'unit&eacute;.
   */
  public String altitudeWithUnit() {
    return rb.getString("Altitude") + "(m)";
  }

  /**
   * @return Restitue la distance avec l'unit&eacute;.
   */
  public String distanceWithUnit() {
    String msg = rb.getString("Distance") + "({0})";
    return MessageFormat.format(msg, DistanceUnit.getDefaultUnit());
  }

  /**
   * @return Restitue la temp&eacute;rature avec l'unit&eacute;.
   */
  public String temperatureWithUnit() {
    String msg = rb.getString("Temperature") + "({0})";
    return MessageFormat.format(msg, TemperatureUnit.getDefaultUnit());
  }

  /**
   * @return Restitue la vitesse avec l'unit&eacute;.
   */
  public String speedWithUnit() {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), CommonLang.class);
    String msg = rb.getString("Speed") + "({0})";
    return MessageFormat.format(msg, SpeedUnit.getDefaultUnit());
  }

  /**
   * @return Restitue l'allure avec l'unit&eacute;.
   */
  public String paceWithUnit() {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), CommonLang.class);
    String msg = rb.getString("Pace") + "({0})";
    return MessageFormat.format(msg, PaceUnit.getDefaultUnit());
  }

  @Override
  public void languageChanged(LanguageEvent event) {
    rb = ResourceBundleUtility.getBundle(event.getLang(), CommonLang.class);
  }

  @Override
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
