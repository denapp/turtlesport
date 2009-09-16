package fr.turtlesport.ui.swing.component.jtable;

import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class TimeCellEditor extends AbstractCellEditor implements
                                                      TableCellEditor {

  private final JFormattedTextField jText;

  public TimeCellEditor() {
    super();

    DateFormat df = new SimpleDateFormat("HH:mm:ss");
    jText = new JFormattedTextField(df);
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
    jText.setValue(value);
    return jText;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#getCellEditorValue()
   */
  public Object getCellEditorValue() {
    try {
      jText.commitEdit();
    }
    catch (ParseException e) {
    }
    return jText.getValue();
  }

}
