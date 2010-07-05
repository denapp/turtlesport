package fr.turtlesport.ui.swing.component;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * @author Denis Apparicio
 * 
 */
public class ProgressBarTurtleUI extends BasicProgressBarUI {

  public static ComponentUI createUI(JComponent c) {
    return new ProgressBarTurtleUI();
  }

  public void update(Graphics g, JComponent c) {
    if (c.isOpaque()) {
      g.setColor(c.getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
    }
    paint(g, c);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.plaf.basic.BasicProgressBarUI#setCellLength(int)
   */
  @Override
  public void setCellLength(int cellLen) {
    super.setCellLength(cellLen);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.plaf.basic.BasicProgressBarUI#setCellSpacing(int)
   */
  @Override
  public void setCellSpacing(int cellSpace) {
    super.setCellSpacing(cellSpace);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.swing.plaf.basic.BasicProgressBarUI#paintIndeterminate(java.awt.Graphics
   * , javax.swing.JComponent)
   */
  @Override
  protected void paintIndeterminate(Graphics g, JComponent c) {
    // Paint the bouncing box.
    boxRect = getBox(boxRect);
    if (boxRect != null) {
      g.setColor(Color.blue);
      g.fillRect(boxRect.x, boxRect.y, boxRect.width, boxRect.height);
    }
  }

  public static void main(String[] args) {
    System.out.println("paintIndeterminate");
    
  }

}
