package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.MainGui;

/**
 * @author Denis Apparicio
 * 
 */
public class LocationComboBoxModel extends DefaultComboBoxModel {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(LocationComboBoxModel.class);
  }

  
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
}
