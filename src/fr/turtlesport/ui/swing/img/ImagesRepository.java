package fr.turtlesport.ui.swing.img;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author denis
 * 
 */
public final class ImagesRepository {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ImagesRepository.class);
  }

  /**
   * 
   */
  private ImagesRepository() {
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
    return new ImageIcon(ImagesRepository.class.getResource(name));
  }

  /**
   * Restitue une image.
   * 
   * @param name
   *          nom de l'image.
   * @return
   */
  public static BufferedImage getImage(String name) {
    try {
      return ImageIO.read(ImagesRepository.class.getResource(name));
    }
    catch (IOException e) {
      log.error("", e);
    }
    return null;
  }

}
