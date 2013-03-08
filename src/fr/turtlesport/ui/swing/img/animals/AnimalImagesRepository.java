package fr.turtlesport.ui.swing.img.animals;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.ImageDesc;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis
 * 
 */
public final class AnimalImagesRepository implements LanguageListener {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AnimalImagesRepository.class);
  }

  private ResourceBundle                rb;

  private static AnimalImagesRepository singleton;

  /**
   * 
   */
  private AnimalImagesRepository() {
    super();
  }

  /**
   * Restitue une instance unique.
   * 
   * @return
   */
  public static AnimalImagesRepository getInstance() {
    if (singleton == null) {
      synchronized (AnimalImagesRepository.class) {
        singleton = new AnimalImagesRepository();
        LanguageManager.getManager().addLanguageListener(singleton);
        singleton.rb = ResourceBundleUtility.getBundle(LanguageManager
            .getManager().getCurrentLang(), AnimalImagesRepository.class);
      }
    }
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang
   * .LanguageEvent)
   */
  public void languageChanged(LanguageEvent event) {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), AnimalImagesRepository.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  /**
   * Restitue les noms des images.
   * 
   * @return les noms des images.
   */
  public String[] getNames() {
    // Restitue la liste des noms d'images
    Enumeration<String> e = rb.getKeys();
    ArrayList<String> list = new ArrayList<String>();
    while (e.hasMoreElements()) {
      list.add(e.nextElement());
    }

    String[] names = new String[list.size()];
    if (names.length > 0) {
      list.toArray(names);
    }

    return names;
  }

  /**
   * Restitue une image icone.
   * 
   * @param name
   *          nom de l'image icone.
   * @return
   */
  public ImageIcon getSmallImageIcon(String name) {
    if (name == null) {
      return null;
    }

    String tmp = rb.getString(name);
    StringBuilder st = new StringBuilder();
    st.append(tmp.substring(0, tmp.indexOf('.')));
    if ("turtleHead.png".equals(tmp)) {
      st.append("Small.png");
    }
    else {
      st.append("Small.gif");
    }
    return new ImageIcon(AnimalImagesRepository.class
        .getResource(st.toString()));
  }

  /**
   * Restitue une image icone.
   * 
   * @param name
   *          nom de l'image icone.
   * @return
   */
  public ImageIcon getImageIcon(String name) {
    if (name == null) {
      return null;
    }

    String tmp = rb.getString(name);
    return new ImageIcon(AnimalImagesRepository.class.getResource(tmp));
  }

  /**
   * Restitue une image icone.
   * 
   * @param name
   *          nom de l'image icone.
   * @return
   */
  public ImageDesc getImageDesc(String name) {
    if (name == null) {
      return null;
    }
    return new ImageDesc(AnimalImagesRepository.class, rb.getString(name));
  }

  /**
   * Restitue une image.
   * 
   * @param name
   *          nom de l'image.
   * @return
   */
  public Image getImage(String name) {
    try {
      if (name == null) {
        return null;
      }
      String tmp = (name.endsWith(".gif") ? name : name + ".gif");
      return ImageIO.read(AnimalImagesRepository.class.getResource(tmp));
    }
    catch (IOException e) {
      log.error("", e);
    }
    return null;
  }

}
