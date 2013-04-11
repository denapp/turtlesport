package fr.turtlesport.ui.swing.component.jtable;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import fr.turtlesport.ui.swing.GuiFont;

/**
 * @author Denis Apparicio
 * 
 */
public class ProgressBarCellRenderer extends JProgressBar implements
TableCellRenderer {
  public ProgressBarCellRenderer() {
    this(0, 100);
  }

  public ProgressBarCellRenderer(int min, int max) {
    super(min, max);
    setBorderPainted(true);
    setPreferredSize(new Dimension(60, 20));
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
                                                 int col) {
    if (value != null && value instanceof Integer) {
      int val = (Integer) value;
      setValue(val);
      if (val == 100) {
        setFont(GuiFont.FONT_PLAIN);
      }
    }
    return this;
  }
}