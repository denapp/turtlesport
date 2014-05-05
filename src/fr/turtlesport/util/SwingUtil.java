package fr.turtlesport.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class SwingUtil {

  /**
   * Sauvegarde le composant swing dans un fichier.
   * 
   * @param component
   * @param filename
   */
  public static void saveComponentAsJPEG(Component component, File file) throws IOException {
    Dimension size = component.getSize();
    BufferedImage myImage = new BufferedImage(size.width,
                                              size.height,
                                              BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = myImage.createGraphics();
    component.paint(g2);
    
    ImageIO.write(myImage, "jpg", file);
  }

}
