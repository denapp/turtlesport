package fr.turtlesport.ui.swing.component;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import fr.turtlesport.util.OperatingSystem;

/**
 * @author denis
 * 
 */
public class JTableCustom extends JXTable {

  /** Hashtable des CellEditor. */
  private Hashtable<Cell, TableCellEditor>   tableEditor;

  /** Hashtable des CellRenderer. */
  private Hashtable<Cell, TableCellRenderer> tableRenderer;

  public JTableCustom() {
    super();
    createAlternateStriping();
  }

  public JTableCustom(int numRows, int numColumns) {
    super(numRows, numColumns);
    createAlternateStriping();
  }

  public JTableCustom(Object[][] rowData, Object[] columnNames) {
    super(rowData, columnNames);
    createAlternateStriping();
  }

  public JTableCustom(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
    super(dm, cm, sm);
    createAlternateStriping();
  }

  public JTableCustom(TableModel dm, TableColumnModel cm) {
    super(dm, cm);
    createAlternateStriping();
  }

  public JTableCustom(TableModel dm) {
    super(dm);
    createAlternateStriping();
  }

  public JTableCustom(Vector<?> rowData, Vector<?> columnNames) {
    super(rowData, columnNames);
    createAlternateStriping();
  }

  private void createAlternateStriping() {
    if (OperatingSystem.isMacOSX()) {
      addHighlighter(HighlighterFactory.createAlternateStriping());
    }
    addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                                        null,
                                        Color.RED));

  }

  /**
   * Valorise le <code>TableCellEditor</code> pour une cellule.
   * 
   * @param cellEditor
   * @param row
   * @param column
   */
  public void setCellEditor(TableCellEditor cellEditor, int row, int column) {
    if (cellEditor == null) {
      throw new IllegalArgumentException("cellEditor est null");
    }
    if (row < 0) {
      throw new IllegalArgumentException("row=" + row);
    }
    if (column < 0) {
      throw new IllegalArgumentException("column=" + column);
    }

    if (tableEditor == null) {
      synchronized (JTableCustom.class) {
        tableEditor = new Hashtable<Cell, TableCellEditor>();
      }
    }

    synchronized (JTableCustom.class) {
      Cell cell = new Cell(row, column);
      if (!tableEditor.contains(cell)) {
        tableEditor.remove(cell);
      }
      tableEditor.put(cell, cellEditor);
    }
  }

  /**
   * Valorise le <code>TableCellEditor</code> pour une cellule.
   * 
   * @param cellEditor
   * @param row
   * @param column
   */
  public void setCellRenderer(TableCellRenderer cellRenderer,
                              int row,
                              int column) {
    if (cellRenderer == null) {
      throw new IllegalArgumentException("cellRenderer est null");
    }
    if (row < 0) {
      throw new IllegalArgumentException("row=" + row);
    }
    if (column < 0) {
      throw new IllegalArgumentException("column=" + column);
    }

    if (tableRenderer == null) {
      synchronized (JTableCustom.class) {
        tableRenderer = new Hashtable<Cell, TableCellRenderer>();
      }
    }

    synchronized (JTableCustom.class) {
      Cell cell = new Cell(row, column);
      if (!tableRenderer.contains(cell)) {
        tableRenderer.remove(cell);
      }
      tableRenderer.put(cell, cellRenderer);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JTable#getCellEditor(int, int)
   */
  @Override
  public TableCellEditor getCellEditor(int row, int column) {
    if (tableEditor != null) {
      TableCellEditor cellEditor = tableEditor.get(new Cell(row, column));
      if (cellEditor != null) {
        return cellEditor;
      }
    }

    return super.getCellEditor(row, column);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JTable#getCellRenderer(int, int)
   */
  @Override
  public TableCellRenderer getCellRenderer(int row, int column) {
    if (tableRenderer != null) {
      TableCellRenderer cellRenderer = tableRenderer.get(new Cell(row, column));
      if (cellRenderer != null) {
        return cellRenderer;
      }
    }

    return super.getCellRenderer(row, column);
  }

  /**
   * @author denis
   * 
   */
  private static class Cell {
    private int row;

    private int col;

    public Cell(int row, int col) {
      super();
      this.row = row;
      this.col = col;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof Cell)) {
        return false;
      }

      Cell cell = (Cell) obj;
      return ((row == cell.row) && (col == cell.col));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return row + col;
    }

  }

}
