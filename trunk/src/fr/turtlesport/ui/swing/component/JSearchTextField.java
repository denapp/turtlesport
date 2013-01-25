package fr.turtlesport.ui.swing.component;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.JButton;
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

  public JSearchTextField(int column) {
    super();
    setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));

    jTextField = new JTextField(column);
    jTextField.setFont(GuiFont.FONT_PLAIN);
    jTextField.setFont(GuiFont.FONT_PLAIN);
    jTextField.setMinimumSize(jTextField.getPreferredSize());

    jButtonSearch = new JButton(ImagesRepository.getImageIcon("find-20x20.png"));
    jButtonSearch.setMinimumSize(jButtonSearch.getPreferredSize());
    // jButtonSearch.setBorder(BorderFactory.createEmptyBorder());
    // jButtonSearch.setContentAreaFilled(false);

    jButtonSearch.setFocusPainted(true);
    jButtonSearch.setBorderPainted(true);
    jButtonSearch.setContentAreaFilled(true);

    add(jTextField);
    add(jButtonSearch);
  }

  public JSearchTextField() {
    this(10);
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
