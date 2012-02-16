package fr.turtlesport.ui.swing.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ConvertStringTo;

/**
 * @author Denis Apparicio
 * 
 */
public class SpeedModelDocListener extends GenericModelDocListener {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(SpeedModelDocListener.class);
  }

  private static final SimpleDateFormat DF_TIME = new SimpleDateFormat("mm:ss");

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @throws NoSuchMethodException
   */
  public SpeedModelDocListener(JFormattedTextField jTextField,
                               Object data,
                               String methodName) throws NoSuchMethodException {
    super(jTextField, data, methodName, String.class);
  }

  /**
   * @param jTextField
   * @param data
   * @param methodName
   * @param arg
   * @throws NoSuchMethodException
   */
  public SpeedModelDocListener(JFormattedTextField jTextField,
                               Object data,
                               String methodName,
                               Class<?> arg) throws NoSuchMethodException {
    super(jTextField, data, methodName, arg);
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
      Object text = ((JFormattedTextField) getJTextField()).getValue();
      if (text == null || "".equals(text)) {
        return;
      }

      Object value;
      if (text instanceof Date) {
        value = ConvertStringTo.toObject(getArg(), DF_TIME.format((Date) text)
            .replace(':', '.'));
      }
      else {
        value = ConvertStringTo.toObject(getArg(), text.toString());
      }
      getMethodName().invoke(getData(), value);
    }
    catch (Throwable e) {
      log.error("", e);
    }
  }

}
