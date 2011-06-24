package fr.turtlesport.ui.swing.component;

import java.awt.Component;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class IconListRenderer extends DefaultListCellRenderer {
  private Map<String, ImageIcon> icons;

  public IconListRenderer(Map<String, ImageIcon> icons) {
    this.icons = icons;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.
   * swing.JList, java.lang.Object, int, boolean, boolean)
   */
  @Override
  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus) {

    // Get the renderer component from parent class
    JLabel label = (JLabel) super.getListCellRendererComponent(list,
                                                               value,
                                                               index,
                                                               isSelected,
                                                               cellHasFocus);

    // Get icon to use for the list item value
    Icon icon = icons.get(value);
    label.setIcon(icon);

    // Set icon to display for value
//    if (isSelected) {
//      label.setBackground(list.getSelectionBackground());
//      label.setForeground(list.getSelectionForeground());
//    }
//    else {
//      label.setBackground(list.getBackground());
//      label.setForeground(list.getForeground());
//    }

    return label;
  }

}
