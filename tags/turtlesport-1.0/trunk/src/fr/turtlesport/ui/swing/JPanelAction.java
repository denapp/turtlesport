package fr.turtlesport.ui.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelAction extends JPanel {

  private JLabel                 jLabelTitle;

  private static final Border    BORDER_RAISED  = BorderFactory
                                                    .createBevelBorder(EtchedBorder.RAISED);

  private static final Border    BORDER_LOWERED = BorderFactory
                                                    .createBevelBorder(EtchedBorder.LOWERED);

  private static final Dimension DIM_PREF       = new Dimension(170, 25);

  // @jve:decl-index=0:

  /**
   * This is the default constructor.
   */
  public JPanelAction(String text) {
    super();
    initialize();
    jLabelTitle.setText(text);
  }

  /**
   * This is the default constructor.
   */
  public JPanelAction() {
    super();
    initialize();
  }

  /**
   * 
   */
  public void setBorderRaised() {
    setBorder(BORDER_RAISED);
  }

  /**
   * 
   */
  public void setBorderLowered() {
    setBorder(BORDER_LOWERED);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {   
    jLabelTitle = new JLabel();
    jLabelTitle.setText("JLabel");
    jLabelTitle.setFont(GuiFont.FONT_PLAIN);
    
    FlowLayout flowLayout = new FlowLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    this.setLayout(flowLayout);
    this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    this.setSize(DIM_PREF);
    this.add(jLabelTitle, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
   */
  @Override
  public void setPreferredSize(Dimension dimension) {
    super.setPreferredSize(DIM_PREF);
  }

} // @jve:decl-index=0:visual-constraint="10,10"
