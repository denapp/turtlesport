package fr.turtlesport.ui.swing.component.jtable;

import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import fr.turtlesport.ui.swing.GuiFont;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class TimeCellRenderer implements TableCellRenderer {

  private JFormattedTextField jText;

  public TimeCellRenderer() {
    super();
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    jText = new JFormattedTextField(df);
    jText.setFont(GuiFont.FONT_PLAIN);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
   *      java.lang.Object, boolean, boolean, int, int)
   */
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    jText.setValue(value);
    return jText;
  }

}
