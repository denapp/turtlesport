package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataActivityNull;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class ActivityComboBoxModel extends DefaultComboBoxModel {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ActivityComboBoxModel.class);
  }

  public ActivityComboBoxModel(DataActivityNull data) {
    super();

    try {
      addElement(data);
      List<AbstractDataActivity> list = UserActivityTableManager.getInstance()
          .retreive();
      for (AbstractDataActivity d : list) {
        addElement(d);
      }
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }
  
  public ActivityComboBoxModel() {
    super();

    try {
      List<AbstractDataActivity> list = UserActivityTableManager.getInstance()
          .retreive();
      for (AbstractDataActivity d : list) {
        addElement(d);
      }
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }

  public void setSelectedActivity(int sportType) {
    for (int i = 0; i < getSize(); i++) {
      AbstractDataActivity d = (AbstractDataActivity) getElementAt(i);
      if (d.getSportType() == sportType) {
        setSelectedItem(d);
        return;
      }
    }

    for (int i = 0; i < getSize(); i++) {
      AbstractDataActivity d = (AbstractDataActivity) getElementAt(i);
      if (d.getSportType() == DataActivityOther.SPORT_TYPE) {
        setSelectedItem(d);
        return;
      }
    }
  }

  public int getSportType() {
    Object obj = getSelectedItem();
    if (obj instanceof String || obj == null) {
      return DataActivityOther.SPORT_TYPE;
    }
    return ((AbstractDataActivity) obj).getSportType();
  }
}
