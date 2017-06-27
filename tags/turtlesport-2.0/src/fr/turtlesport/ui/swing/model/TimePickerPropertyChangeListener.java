package fr.turtlesport.ui.swing.model;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Date;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JTextFieldTime;

/**
 * @author Denis Apparicio
 * 
 */
public class TimePickerPropertyChangeListener implements
                                             PropertyChangeListener,
                                             FocusListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(TimePickerPropertyChangeListener.class);
  }

  private Method              methodName;

  private JTextFieldTime      jTime;

  private Object              data;

  /**
   * @param jTime
   * @param data
   * @param methodName
   * @param arg
   * @throws NoSuchMethodException
   */
  public TimePickerPropertyChangeListener(JTextFieldTime jTime,
                                          Object data,
                                          String methodName) throws NoSuchMethodException {
    this.jTime = jTime;
    this.data = data;
    this.methodName = data.getClass().getMethod(methodName, Long.TYPE);
    jTime.addFocusListener(this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    update();
  }

  private void update() {
    try {
      int value = jTime.getTime();
      methodName.invoke(data, value);
    }
    catch (Throwable e) {
      log.error("", e);
    }
  }

  @Override
  public void focusGained(FocusEvent evt) {
  }

  @Override
  public void focusLost(FocusEvent evt) {
    update();
  }

}
