package fr.turtlesport.ui.swing.model;

import javax.swing.JPanel;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogPreference;
import fr.turtlesport.ui.swing.component.PanelPrefListener;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelPref {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelPref.class);
  }

  private String              title;

  private Class<?>               clazz;

  private JPanel              view;

  /**
   * @param clazz
   */
  public ModelPref(String title, Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException("clazz");
    }
    this.title = title;
    this.clazz = clazz;
  }

  /**
   * Mis &agrave; jour de la vue.
   * 
   * @param owner
   */
  public void updateView(JDialogPreference owner) {
    try {
      view = (JPanel) clazz.newInstance();
      owner.setPanelPreference(view);
    }
    catch (InstantiationException e) {
      // ne peut arriver
      log.error("InstantiationException", e);
      throw new RuntimeException();
    }
    catch (IllegalAccessException e) {
      // ne peut arriver
      log.error("IllegalAccessException", e);
      throw new RuntimeException();
    }
  }

  /**
   * La vue change.
   */
  public void changeView() {
    ((PanelPrefListener) view).viewChanged();
  }

  /**
   * Mis &agrave; jour du titre.
   * 
   * @param title
   *          le nouveau titre
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return title;
  }

}
