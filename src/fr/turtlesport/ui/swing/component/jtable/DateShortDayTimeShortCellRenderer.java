package fr.turtlesport.ui.swing.component.jtable;

import java.awt.Component;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import fr.turtlesport.lang.LanguageManager;

/**
 * @author Denis Apparicio
 * 
 */
public class DateShortDayTimeShortCellRenderer extends DefaultTableCellRenderer {
  private DateFormat dateFormat;

  public DateShortDayTimeShortCellRenderer() {
    super();
    setLocale(LanguageManager.getManager().getLocale());
  }

  public void setLocale(Locale locale) {
    dateFormat = new SimpleDateFormat("E", new DateFormatSymbols(locale));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent
   * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
   */
  @Override
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {

    Component component = super.getTableCellRendererComponent(table,
                                                              value,
                                                              isSelected,
                                                              hasFocus,
                                                              row,
                                                              column);
    if (value != null && component instanceof JLabel) {
      StringBuilder st = new StringBuilder();
      st.append(dateFormat.format((Date) value));
      st.append(" ");
      st.append(LanguageManager.getManager().getCurrentLang()
          .getDateTimeShortFormatter().format((Date) value));
      ((JLabel) component).setText(st.toString());
    }
    return component;
  }
}
