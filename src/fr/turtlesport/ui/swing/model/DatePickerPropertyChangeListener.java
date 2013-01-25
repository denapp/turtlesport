package fr.turtlesport.ui.swing.model;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Date;

import org.jdesktop.swingx.JXDatePicker;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class DatePickerPropertyChangeListener implements
                                             PropertyChangeListener,
                                             FocusListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(DatePickerPropertyChangeListener.class);
  }

  private Method              methodName;

  private JXDatePicker        jxDatePicker;

  private Object              data;

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @param arg
   * @throws NoSuchMethodException
   */
  public DatePickerPropertyChangeListener(JXDatePicker jxDatePicker,
                                          Object data,
                                          String methodName) throws NoSuchMethodException {
    this.jxDatePicker = jxDatePicker;
    this.data = data;
    this.methodName = data.getClass().getMethod(methodName, Date.class);
    jxDatePicker.addFocusListener(this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    update();
  }

  private void update() {
    try {
      Date value = jxDatePicker.getDate();
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
