package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class EquipementComboBoxModel extends DefaultComboBoxModel {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(EquipementComboBoxModel.class);
  }

  public EquipementComboBoxModel() {
    super();
    addElement("");
    try {
      List<String> list = EquipementTableManager.getInstance().retreiveNames();
      for (String d : list) {
        addElement(d);
      }
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }
}
