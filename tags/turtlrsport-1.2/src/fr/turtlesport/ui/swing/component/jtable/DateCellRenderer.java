package fr.turtlesport.ui.swing.component.jtable;

import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import fr.turtlesport.lang.LanguageManager;

/**
 * @author Denis Apparicio
 * 
 */
public class DateCellRenderer extends DefaultTableCellRenderer {

  public DateCellRenderer() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
   *      java.lang.Object, boolean, boolean, int, int)
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
      String date = LanguageManager.getManager().getCurrentLang()
          .getDateFormatter().format((Date) value);
      ((JLabel) component).setText(date);
    }
    return component;
  }
}
