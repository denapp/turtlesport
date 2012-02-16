package fr.turtlesport.ui.swing.component;

import java.awt.Font;
import java.util.Date;
import java.util.Locale;

import org.jdesktop.swingx.JXDatePicker;

import fr.turtlesport.lang.ILanguage;

/**
 * @author Denis Apparicio
 * 
 */
public class JXDatePickerLocale extends JXDatePicker {

  public JXDatePickerLocale() {
    super();
  }

  public JXDatePickerLocale(Date selection, Locale locale) {
    super(selection, locale);
  }

  public JXDatePickerLocale(Date selected) {
    super(selected);
  }

  public JXDatePickerLocale(Locale locale) {
    super(locale);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdesktop.swingx.JXDatePicker#setFont(java.awt.Font)
   */
  @Override
  public void setFont(Font font) {
    super.setFont(font);
    getEditor().setFont(font);
    getMonthView().setFont(font);
    getLinkPanel().setFont(font);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Component#setLocale(java.util.Locale)
   */
  public void setLanguage(ILanguage lang) {
    setLocale(lang.getLocale());
    setFormats(lang.getDateFormatter());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Component#setLocale(java.util.Locale)
   */
  @Override
  public void setLocale(Locale l) {
    if (Locale.ENGLISH.equals(l)) {
      // les properties de JXDatePicker en anglais sont
      // DatePicker_en_GB.properties DatePicker_en_US.properties
      // pas de DatePicker_en.properties
      if (Locale.getDefault().equals(Locale.US)) {
        l = Locale.US;
      }
      else {
        l = Locale.UK;
      }
    }
    super.setLocale(l);
  }

}
