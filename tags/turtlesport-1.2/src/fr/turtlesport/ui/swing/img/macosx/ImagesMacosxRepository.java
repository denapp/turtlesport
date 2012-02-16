package fr.turtlesport.ui.swing.img.macosx;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author denis
 * 
 */
public final class ImagesMacosxRepository {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ImagesMacosxRepository.class);
  }

  /**
   * 
   */
  private ImagesMacosxRepository() {
    super();
  }

  /**
   * Restitue une image icone.
   * 
   * @param name
   *          nom de l'image icone.
   * @return
   */
  public static ImageIcon getImageIcon(String name) {
    return new ImageIcon(ImagesMacosxRepository.class.getResource(name));
  }

  /**
   * Restitue une image.
   * 
   * @param name
   *          nom de l'image.
   * @return
   */
  public static Image getImage(String name) {
    try {
      return ImageIO.read(ImagesMacosxRepository.class.getResource(name));
    }
    catch (IOException e) {
      log.error("", e);
    }
    return null;
  }

}
