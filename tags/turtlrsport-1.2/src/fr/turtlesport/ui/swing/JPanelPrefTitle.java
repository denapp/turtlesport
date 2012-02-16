package fr.turtlesport.ui.swing;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefTitle extends JPanel {

  private JLabel jLabelTitle;

  /**
   * 
   */
  public JPanelPrefTitle() {
    super();
    initialize();
  }

  /**
   * 
   * @param title
   */
  public JPanelPrefTitle(String title) {
    super();
    initialize();
    jLabelTitle.setText(title);
  }

  /**
   * Mis &agrave; du titre.
   * 
   * @param title
   *          le nouveau titre.
   */
  public void setTitle(String title) {
    jLabelTitle.setText(title);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(300, 30);

    jLabelTitle = new JLabel();
    jLabelTitle.setText("JLabel");
    jLabelTitle.setFont(GuiFont.FONT_BOLD);

    FlowLayout flowLayout = new FlowLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    this.setLayout(flowLayout);
    this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
    this.add(jLabelTitle, null);
  }

}
