package fr.turtlesport.ui.swing.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.RepaintManager;

import org.jdesktop.swingx.JXMapViewer;

public class JTurtleMapViewer extends JXMapViewer {

  private BufferedImage bimg;

  private Toolkit       toolkit;

  private int           biw, bih;

  private boolean       clearOnce;

  public JTurtleMapViewer() {
    super();
    toolkit = getToolkit();
    setDoubleBuffered(true);
    setIgnoreRepaint(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paint(java.awt.Graphics)
   */
  @Override
  public void paint(Graphics g) {
    Dimension d = getSize();

    if (bimg == null || biw != d.width || bih != d.height) {
      bimg = (BufferedImage) ((Graphics2D) g).getDeviceConfiguration()
          .createCompatibleImage(d.width, d.height);
      biw = d.width;
      bih = d.height;
      clearOnce = true;
    }

    Graphics2D g2 = createGraphics2D(d.width, d.height, bimg, g);
    super.paint(g2);
    g2.dispose();

    if (bimg != null) {
      g.drawImage(bimg, 0, 0, null);
      toolkit.sync();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paintImmediately(int, int, int, int)
   */
  public void paintImmediately(int x, int y, int w, int h) {
    RepaintManager repaintManager = null;
    boolean save = true;
    if (!isDoubleBuffered()) {
      repaintManager = RepaintManager.currentManager(this);
      save = repaintManager.isDoubleBufferingEnabled();
      repaintManager.setDoubleBufferingEnabled(false);
    }
    super.paintImmediately(x, y, w, h);

    if (repaintManager != null) {
      repaintManager.setDoubleBufferingEnabled(save);
    }
  }

  private Graphics2D createGraphics2D(int width,
                                      int height,
                                      BufferedImage bi,
                                      Graphics g) {

    Graphics2D g2 = null;

    if (bi != null) {
      g2 = bi.createGraphics();
    }
    else {
      g2 = (Graphics2D) g;
    }

    g2.setBackground(getBackground());

    if (clearOnce) {
      g2.clearRect(0, 0, width, height);
      clearOnce = false;
    }

    return g2;
  }

}
