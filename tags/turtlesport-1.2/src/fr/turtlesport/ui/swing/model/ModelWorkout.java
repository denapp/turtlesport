package fr.turtlesport.ui.swing.model;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jdesktop.swingx.JXTreeTable;
import org.xml.sax.SAXException;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.AbstractStepT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.RepeatT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.WorkoutT;

import fr.turtlesport.Configuration;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.workout.JPanelWorkout;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelWorkout {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelWorkout.class);
  }

  /**
   * Mise a jour de la vue.
   * 
   * @param view
   * @throws SQLException
   */
  public void updateView(JPanelWorkout view) throws SQLException {
    List<WorkoutT> workouts = null;
    // recuperation des workout
    // if (!Configuration.getConfig().getPropertyAsBoolean("Workouts",
    // "init",
    // false)) {
    Configuration.getConfig().addProperty("Workouts", "init", "true");
    workouts = retreive(getClass()
        .getResourceAsStream("predefined_workouts.tcx"));
    // }

    if (workouts != null) {
      DefaultListModel model = (DefaultListModel) view.getjListWorkout()
          .getModel();
      for (int i = 0; i < workouts.size(); i++) {
        model.add(i, workouts.get(i));
      }
    }
  }

  public void updateView(JPanelWorkout view, Object selectedValue) {
    clear(view);

    if (selectedValue == null || !(selectedValue instanceof WorkoutT)) {
      return;
    }

    WorkoutT w = (WorkoutT) selectedValue;

    // Title
    view.getjPanelWorkoutTitle().getjTextFieldName().setText(w.getName());
    view.getjPanelWorkoutTitle().getjTextFieldNotes().setText(w.getNotes());
    switch (w.getSport()) {
      case RUNNING:
        view.getjPanelWorkoutTitle().getjComboBoxSportType()
            .setSelectedIndex(0);
        break;
      case BIKING:
        view.getjPanelWorkoutTitle().getjComboBoxSportType()
            .setSelectedIndex(1);
        break;
      case OTHER:
        view.getjPanelWorkoutTitle().getjComboBoxSportType()
            .setSelectedIndex(2);
    }
    view.getjPanelWorkoutTitle().getjTextFieldNotes().setCaretPosition(0);

    // Steps
//    DefaultMutableTreeNode top = new DefaultMutableTreeNode();
//    DefaultTreeModel treeModel = new DefaultTreeModel(top);
//   
//    JXTreeTable tree = view.getjPanelSteps().getJTreeStep();
//    tree.setTreeTableModel(treeModel);
//    List<AbstractStepT> listStep = w.getStep();
//    for (AbstractStepT step : listStep) {
//      DefaultMutableTreeNode node = null;
//      if (step instanceof RepeatT) {
//        node = new DefaultMutableTreeNode(step);
//      }
//      else {
//        node = new DefaultMutableTreeNode(step);
//      }
//      top.add(node);
//    }
    view.getjPanelSteps().fireStep(w);
  }

  private void clear(JPanelWorkout view) {
    view.getjPanelWorkoutTitle().getjTextFieldName().setText("");
    view.getjPanelWorkoutTitle().getjTextFieldNotes().setText("");
  }

  private List<WorkoutT> retreive(InputStream inXml) {
    try {
      // Schema
      SchemaFactory schemaFactory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema;

      schema = schemaFactory
          .newSchema(new StreamSource(TrainingCenterDatabaseT.class
              .getResourceAsStream("TrainingCenterDatabasev2.xsd")));

      // JAXB avec validation schema
      JAXBContext jc = JAXBContext
          .newInstance("com.garmin.xmlschemas.trainingcenterdatabase.v2");
      Unmarshaller u = jc.createUnmarshaller();
      u.setSchema(schema);

      // JAXB Unmarshall
      StreamSource xml = new StreamSource(inXml);
      JAXBElement<TrainingCenterDatabaseT> je = u
          .unmarshal(xml, TrainingCenterDatabaseT.class);
      return je.getValue().getWorkouts().getWorkout();
    }
    catch (SAXException e) {
      e.printStackTrace();
    }
    catch (JAXBException e) {
    }
    return null;
  }
}
