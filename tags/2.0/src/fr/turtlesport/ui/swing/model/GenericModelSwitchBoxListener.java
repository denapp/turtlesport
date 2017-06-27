package fr.turtlesport.ui.swing.model;

import fr.turtlesport.db.DataEquipement;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JSwitchBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

/**
 * @author Denis Apparicio
 * 
 */
public class GenericModelSwitchBoxListener implements ActionListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GenericModelSwitchBoxListener.class);
  }

  private Method              methodName;

  private Object              data;

  private JSwitchBox          jSwitchBox;

  /**
   * @param jSwitchBox
   * @param data
   * @param methodName
   *
   * @throws NoSuchMethodException
   */
  public GenericModelSwitchBoxListener(JSwitchBox jSwitchBox,
                                       Object data,
                                       String methodName) throws NoSuchMethodException {
    this.jSwitchBox = jSwitchBox;
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
      methodName.invoke(data, jSwitchBox.isSelected());
    }
    catch (Throwable e) {
      log.error("", e);
    }
  }

}
