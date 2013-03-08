package fr.turtlesport.ui.swing.component;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelBackgroundImage extends JPanel {

  private Image image;

  public JPanelBackgroundImage(Image image) {
    super();
    this.image = image;
    setLayout(null);
    
    JLabel lblNewLabel = new JLabel("New label");
    lblNewLabel.setForeground(Color.WHITE);
    lblNewLabel.setBounds(17, 262, 140, 16);
    add(lblNewLabel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      g.drawImage(image, 0, 0, null);
    }
  }
}
