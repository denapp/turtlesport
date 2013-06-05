package fr.turtlesport.ui.swing.component;

import javax.swing.JComboBox;

import fr.turtlesport.ui.swing.model.ActivityComboBoxModel;

/**
 * @author Denis Apparicio
 * 
 */
public class JComboBoxActivity extends JComboBox {

  public JComboBoxActivity() {
    super();
    setRenderer(new ComboBoxActivityRenderer());
  }

  public JComboBoxActivity(ActivityComboBoxModel aModel) {
    super(aModel);
    setRenderer(new ComboBoxActivityRenderer());
  }
}