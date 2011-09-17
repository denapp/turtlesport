package fr.turtlesport.ui.swing.component.jtable;

import java.awt.Component;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.JXDatePicker;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;

/**
 * @author Denis Apparicio
 * 
 */
public class DateCellEditor extends AbstractCellEditor implements
                                                      TableCellEditor {

  private final JXDatePicker datePicker;

  /**
   * 
   */
  public DateCellEditor() {
    Locale locale = LanguageManager.getManager().getCurrentLang().getLocale();
    
    if (Locale.ENGLISH.equals(locale)) {
      // les properties de JXDatePicker en anglais sont
      // DatePicker_en_GB.properties DatePicker_en_US.properties 
      // pas de DatePicker_en.properties
      if (Locale.getDefault().equals(Locale.US)) {
        locale = Locale.US;
      }
      else {
        locale = Locale.UK;
      }
    }
    this.datePicker = new JXDatePicker(locale);

    this.datePicker.setFont(GuiFont.FONT_PLAIN);
    this.datePicker.getMonthView().setFont(GuiFont.FONT_PLAIN);
  }

  public void setDate(Date date) {
    datePicker.setDate(date);
  }

  /**
   * @return the datePicker
   */
  public JXDatePicker getDatePicker() {
    return datePicker;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#getCellEditorValue()
   */
  public Object getCellEditorValue() {
    return datePicker.getDate();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
   *      java.lang.Object, boolean, int, int)
   */
  public Component getTableCellEditorComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               int row,
                                               int column) {
    if (value instanceof Date) {
      datePicker.setDate((Date) value);
    }
    return datePicker;
  }

}
