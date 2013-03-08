package fr.turtlesport.ui.swing.component;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * @author Denis Apparicio
 * 
 */
public class JSearchTextField extends JPanel {

  private JTextField jTextField;

  private JButton    jButtonSearch;

  public JSearchTextField(int column, Icon icon) {
    super();
    setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));

    JLabel labelIcon = new JLabel(icon);
    labelIcon.setMinimumSize(labelIcon.getPreferredSize());
    labelIcon.setMaximumSize(labelIcon.getPreferredSize());
    
    jTextField = new JTextField(column);
    jTextField.setFont(GuiFont.FONT_PLAIN);
    jTextField.setFont(GuiFont.FONT_PLAIN);
    jTextField.setMinimumSize(jTextField.getPreferredSize());
    jTextField.setMaximumSize(jTextField.getPreferredSize());

    jButtonSearch = new JButton(ImagesRepository.getImageIcon("find-20x20.png"));
    Dimension dim = new Dimension(22,22);
    jButtonSearch.setMinimumSize(dim);
    jButtonSearch.setPreferredSize(dim);
    jButtonSearch.setMaximumSize(dim);
    jButtonSearch.setBorder(BorderFactory.createEmptyBorder());
    jButtonSearch.setContentAreaFilled(false);
    jButtonSearch.setBorderPainted(false);
    
    add(labelIcon);
    add(jTextField);
    add(jButtonSearch);
    setOpaque(false);
  }

  public JSearchTextField(Icon icon) {
    this(10, icon);
  }

  /**
   * Valorise l'action lorsque le bouton de recherche est actionn&eacute;.
   * 
   * @param action
   *          l'action a d&eacute;clench&eacute;e;.
   */
  public void setFindAction(ActionListener action) {
    jButtonSearch.addActionListener(action);
    jTextField.addActionListener(action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Component#addFocusListener(java.awt.event.FocusListener)
   */
  @Override
  public synchronized void addFocusListener(FocusListener l) {
    jTextField.addFocusListener(l);
  }

  /**
   * @return Restitue le texte de recherche.
   */
  public String getText() {
    return jTextField.getText();
  }
}
