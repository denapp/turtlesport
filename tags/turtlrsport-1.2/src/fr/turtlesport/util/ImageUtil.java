package fr.turtlesport.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author Denis Apparicio
 * 
 */
public final class ImageUtil {

  /**
   * 
   */
  private ImageUtil() {
  }

  /**
   * Constuit une image au dimension sp&eacute;cifi&eacute;.
   * 
   * @param fileImg
   * @param maxWidth
   * @param maxHeight
   * @return
   * @throws IOException
   */
  public static ImageIcon makeImage(final File fileImg,
                                    int maxWidth,
                                    int maxHeight) throws IOException {
    if (fileImg == null) {
      return null;
    }
    return makeImage(ImageIO.read(fileImg), maxWidth, maxHeight);
  }

  /**
   * Constuit une image au dimension sp&eacute;cifi&eacute;.
   * 
   * @param fileImg
   * @param maxWidth
   * @param maxHeight
   * @return
   * @throws IOException
   */
  public static ImageIcon makeImage(final InputStream input,
                                    int maxWidth,
                                    int maxHeight) throws IOException {
    if (input == null) {
      return null;
    }
    return makeImage(ImageIO.read(input), maxWidth, maxHeight);
  }

  /**
   * Constuit une image au dimension sp&eacute;cifi&eacute;.
   * 
   * @param fileImg
   * @param maxWidth
   * @param maxHeight
   * @return
   */
  public static ImageIcon makeImage(BufferedImage bufImg,
                                    int maxWidth,
                                    int maxHeight)  {
    if (bufImg == null) {
      return null;
    }
    ImageIcon image = null;

    int width = bufImg.getHeight();
    int height = bufImg.getWidth();

    if (width > maxWidth || height > maxHeight) {
      Image imgNew = bufImg.getScaledInstance(maxWidth,
                                              maxHeight,
                                              Image.SCALE_SMOOTH);
      image = new ImageIcon(imgNew);
    }
    else {
      image = new ImageIcon(bufImg);
    }

    return image;
  }

  /**
   * Constuit une image au dimension sp&eacute;cifi&eacute;.
   * 
   * @param fileImg
   * @param maxWidth
   * @param maxHeight
   * @return
   * @throws IOException
   */
  public static ImageIcon makeImage(final Image img, int maxWidth, int maxHeight) {
    if (img == null) {
      return null;
    }
    Image imgNew = img.getScaledInstance(maxWidth,
                                         maxHeight,
                                         Image.SCALE_SMOOTH);
    return new ImageIcon(imgNew);
  }
}
