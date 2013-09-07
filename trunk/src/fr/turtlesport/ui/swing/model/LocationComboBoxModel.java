package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.util.StringIgnoreCaseComparator;

/**
 * @author Denis Apparicio
 * 
 */
public class LocationComboBoxModel extends DefaultComboBoxModel {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(LocationComboBoxModel.class);
  }

  private StringIgnoreCaseComparator          comparator = new StringIgnoreCaseComparator();

  public LocationComboBoxModel() {
    super();
    fill();
  }

  public void fill() {
    removeAllElements();
    addElement("");
    try {
      List<String> list = RunTableManager.getInstance()
          .retreiveLocations(MainGui.getWindow().getCurrentIdUser());
      for (String d : list) {
        if (d != null && d.trim().length() > 0) {
          addElement(d.trim());
        }
      }
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }

  public void setSelectedLocation(String location) {
    setSelectedItem((location == null) ? "" : location);
  }

  public boolean contains(Object value) {
    for (int i = 0; i < getSize(); i++) {
      if (getElementAt(i).equals(value)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void addElement(Object element) {
    insertElementAt(element, 0);
  }

  @Override
  public void insertElementAt(Object element, int index) {
    int size = getSize();

    for (index = 0; index < size; index++) {
      String o = (String) getElementAt(index);
      if (comparator.compare(o, (String) element) > 0) {
        break;
      }
    }

    super.insertElementAt(element, index);
    // Select an element when it is added to the beginning of the model
    if (index == 0 && element != null) {
      setSelectedItem(element);
    }
  }

}
