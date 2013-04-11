package fr.turtlesport.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

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

    OutputStream out = new FileOutputStream(file);
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    encoder.encode(myImage);
    out.close();
  }

}
