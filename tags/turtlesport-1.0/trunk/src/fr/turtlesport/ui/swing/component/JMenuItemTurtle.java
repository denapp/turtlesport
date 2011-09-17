package fr.turtlesport.ui.swing.component;

import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import fr.turtlesport.util.OperatingSystem;

/**
 * @author denis apparicio
 * 
 */
public class JMenuItemTurtle extends JMenuItem {

  private static final String ACCELERATOR_KEY = "AcceleratorKey";

  public JMenuItemTurtle() {
    super();
  }

  public JMenuItemTurtle(Action a) {
    super(a);
  }

  public JMenuItemTurtle(Icon icon) {
    super(icon);
  }

  public JMenuItemTurtle(String text, Icon icon) {
    super(text, icon);
  }

  /**
   * @param props
   * @param name
   */
  public void setAccelerator(Properties props, String name) {
    if (props == null) {
      return;
    }

    String acceleratorKey = null;

    if (OperatingSystem.isMacOSX()) {
      acceleratorKey = props.getProperty(name + "." + ACCELERATOR_KEY + "."
                                         + "MacOSX");
    }
    else {
      acceleratorKey = props.getProperty(name + "." + ACCELERATOR_KEY);
    }

    if (acceleratorKey != null) {
      setAccelerator(KeyStroke.getKeyStroke(acceleratorKey));
    }
  }

  public JMenuItemTurtle(String text, int mnemonic) {
    super(text, mnemonic);
  }

  public JMenuItemTurtle(String text) {
    super(text);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.AbstractButton#addActionListener(java.awt.event.ActionListener)
   */
  @Override
  public void addActionListener(ActionListener l) {
    for (ActionListener al : getActionListeners()) {
      removeActionListener(al);
    }
    super.addActionListener(l);
  }

}
