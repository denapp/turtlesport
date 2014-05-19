package fr.turtlesport.ui.swing.model;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.reflect.Method;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ConvertStringTo;

/**
 * @author Denis Apparicio
 * 
 */
public class GenericModelDocListener implements DocumentListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GenericModelDocListener.class);
  }

  private Method              methodName;

  private JTextField          jTextField;

  private Object              data;

  private Class<?>               arg;

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @throws NoSuchMethodException
   */
  public GenericModelDocListener(JTextField jTextField,
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
  public GenericModelDocListener(JTextField jTextField,
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
   * @boolean hasFocus
   * @throws NoSuchMethodException
   */
  public GenericModelDocListener(JTextField jTextField,
                                 Object data,
                                 String methodName,
                                 boolean hasFocus) throws NoSuchMethodException {
    this(jTextField, data, methodName, String.class, hasFocus);
  }

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @param arg
   * @boolean hasFocus
   * @throws NoSuchMethodException
   */
  public GenericModelDocListener(JTextField jTextField,
                                 Object data,
                                 String methodName,
                                 Class<?> arg,
                                 boolean hasFocus) throws NoSuchMethodException {
    this.jTextField = jTextField;
    this.data = data;
    this.arg = arg;
    this.methodName = data.getClass().getMethod(methodName, arg);
    FocusAdapter focusListener = new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        fireUpdate();
      }
    };
    jTextField.addFocusListener(focusListener);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
   */
  public void changedUpdate(DocumentEvent e) {
    update();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
   */
  public void insertUpdate(DocumentEvent e) {
    update();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
   */
  public void removeUpdate(DocumentEvent e) {
    update();
  }

  /**
   * Mis &agrave; jour.
   */
  public void fireUpdate() {
    update();
  }

  /**
   * Mis a jour de la valeur. 
   */
  private void update() {
    try {
      Object text = null;
      if (jTextField instanceof JFormattedTextField) {
        text = ((JFormattedTextField) jTextField).getValue();        
      }
      else {
        text = jTextField.getText();
      }
      if (text != null && "".equals(text)) {
        text = null;
      }

      if (text instanceof Date) {
        methodName.invoke(data, text);
      }
      else {
        methodName
            .invoke(data, ConvertStringTo.toObject(arg, (text == null) ? null
                : text.toString()));
      }
    }
    catch (Throwable e) {
      log.error("", e);
    }
  }

  /**
   * @return the arg
   */
  public Class<?> getArg() {
    return arg;
  }

  /**
   * @return the data
   */
  public Object getData() {
    return data;
  }

  /**
   * @return the jTextField
   */
  public JTextField getJTextField() {
    return jTextField;
  }

  /**
   * @return the methodName
   */
  public Method getMethodName() {
    return methodName;
  }

}
