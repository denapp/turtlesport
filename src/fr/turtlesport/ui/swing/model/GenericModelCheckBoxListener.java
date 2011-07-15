package fr.turtlesport.ui.swing.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JCheckBox;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class GenericModelCheckBoxListener implements ActionListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GenericModelCheckBoxListener.class);
  }

  private Method              methodName;

  private Object              data;

  private JCheckBox           jCheckBox;

  /**
   * @param jPanel
   * @param jCheckBox
   * @param obj
   * @param objOrigin
   * @param methodName
   * @param arg
   * @throws NoSuchMethodException
   */
  public GenericModelCheckBoxListener(JCheckBox jCheckBox,
                              Object data,
                              String methodName) throws NoSuchMethodException {
    this.jCheckBox = jCheckBox;
    this.data = data;
    this.methodName = data.getClass().getMethod(methodName, Boolean.TYPE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    update();
  }

  /**
   * Mis a jour de la valeur.
   */
  private void update() {
    try {
      methodName.invoke(data, jCheckBox.isSelected());
    }
    catch (Throwable e) {
      log.error("", e);
    }
  }

}
