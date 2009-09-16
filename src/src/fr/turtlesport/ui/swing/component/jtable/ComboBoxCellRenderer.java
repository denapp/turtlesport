package fr.turtlesport.ui.swing.component.jtable;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import fr.turtlesport.ui.swing.GuiFont;

/**
 * @author Denis Apparicio
 * 
 */
public class ComboBoxCellRenderer extends JComboBox implements
                                                   TableCellRenderer {

  public ComboBoxCellRenderer() {
    super();
  }

  public ComboBoxCellRenderer(Object[] items) {
    super(items);
    setFont(GuiFont.FONT_PLAIN);
  }

  public ComboBoxCellRenderer(ComboBoxModel model) {
    super(model);
    setFont(GuiFont.FONT_PLAIN);
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

    // if (isSelected) {
    // setForeground(table.getSelectionForeground());
    // super.setBackground(table.getSelectionBackground());
    // }
    // else {
    // setForeground(table.getForeground());
    // setBackground(table.getBackground());
    // }

    // Select the current value
    setFont(GuiFont.FONT_PLAIN);
    setSelectedItem(value);
    return this;
  }

}
