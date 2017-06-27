package fr.turtlesport.ui.swing.component;

import java.text.Format;
import java.text.ParseException;

import javax.swing.text.InternationalFormatter;

/**
 * @author Denis Apparicio
 * 
 */
public class BlankInternationalFormatter extends InternationalFormatter {

  public BlankInternationalFormatter() {
    super();
  }

  public BlankInternationalFormatter(Format format) {
    super(format);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.swing.text.InternationalFormatter#stringToValue(java.lang.String)
   */
  @Override
  public Object stringToValue(String text) throws ParseException {
    if (text.equals("") || text == null) {
      return null;
    }
    return super.stringToValue(text);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.swing.text.InternationalFormatter#valueToString(java.lang.Object)
   */
  @Override
  public String valueToString(Object value) throws ParseException {
    if (value == null || value.equals("")) {
      return "";
    }
    return super.valueToString(value);
  }

}
