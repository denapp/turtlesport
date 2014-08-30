package fr.turtlesport.ui.swing.component;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;

/**
 * @author Denis Apparicio
 *
 */
public class ComboBoxActivityListCellRenderer extends DefaultListCellRenderer {


  @Override
  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus) {

    JLabel label = (JLabel) super.getListCellRendererComponent(list,
                                                               value,
                                                               index,
                                                               isSelected,
                                                               cellHasFocus);
    // Set icon to display for value
    if (!(value instanceof String)) {
      AbstractDataActivity data = (AbstractDataActivity) value;
      label.setIcon(ImagesActivityRepository.getImageIconSmall(data.getIconName()));
    }
    return label;
  }

}
