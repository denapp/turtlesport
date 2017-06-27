package fr.turtlesport.ui.swing.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Date;

import javax.swing.JFormattedTextField;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ConvertStringTo;

/**
 * @author Denis Apparicio
 * 
 */
public class GenericPropertyChangeListener implements PropertyChangeListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(GenericPropertyChangeListener.class);
  }

  private Method              methodName;

  private JFormattedTextField jTextField;

  private Object              data;

  private Class<?>            arg;

  private Object              nullValue;

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @throws NoSuchMethodException
   */
  public GenericPropertyChangeListener(JFormattedTextField jTextField,
                                       Object data,
                                       String methodName) throws NoSuchMethodException {
    this(jTextField, data, methodName, String.class);
  }

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @param arg
   * @throws NoSuchMethodException
   */
  public GenericPropertyChangeListener(JFormattedTextField jTextField,
                                       Object data,
                                       String methodName,
                                       Class<?> arg) throws NoSuchMethodException {
    this.jTextField = jTextField;
    this.data = data;
    this.arg = arg;
    this.methodName = data.getClass().getMethod(methodName, arg);
  }

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @param arg
   * @param nullValue
   * @throws NoSuchMethodException
   */
  public GenericPropertyChangeListener(JFormattedTextField jTextField,
                                       Object data,
                                       String methodName,
                                       Class<?> arg,
                                       Object nullValue) throws NoSuchMethodException {
    this.jTextField = jTextField;
    this.data = data;
    this.arg = arg;
    this.methodName = data.getClass().getMethod(methodName, arg);
    this.nullValue = nullValue;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    try {
      Object text = null;
      if (jTextField instanceof JFormattedTextField) {
        text = ((JFormattedTextField) jTextField).getValue();
      }
      if (text != null && "".equals(text)) {
        text = null;
      }

      if (text instanceof Date) {
        methodName.invoke(data, text);
      }
      else {
        methodName.invoke(data, ConvertStringTo
              .toObject(arg, (text == null) ? null : text.toString(), nullValue));
        
      }
    }
    catch (Throwable e) {
      log.error("", e);
    }

  }

}
