package fr.turtlesport.ui.swing.model;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.turtlesport.db.DataEquipement;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelUserEquipement;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.WeightUnit;
import fr.turtlesport.unit.event.UnitEvent;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelEquipement {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelEquipement.class);
  }

  private DataEquipement      data;

  /** Unites. */
  private String              unitWeight;

  private String              unitDistance;

  /**
   * Construit le model de l'activit&eacute;.
   * 
   * @param data
   */
  public ModelEquipement(DataEquipement data) {
    this.data = data;

    unitWeight = WeightUnit.getDefaultUnit();
    unitDistance = DistanceUnit.getDefaultUnit();
  }

  public DataEquipement getData() {
    return data;
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   */
  public void updateView(JPanelUserEquipement view) {
    log.debug(">>updateView");

    // effacement de la vue
    eraseGui(view);

    // mis a jour du model
    view.setModel(this);

    // conversion si besoin
    if (!WeightUnit.isUnitKg(WeightUnit.getDefaultUnit())) {
      unitWeight = WeightUnit.unitKg();
      setUnitWeight(view, WeightUnit.getDefaultUnit());
    }
    setUnitWeight(view, (String) view.getJComboBoxWeightUnits()
        .getSelectedItem());
    if (!DistanceUnit.isUnitKm(WeightUnit.getDefaultUnit())) {
      unitDistance = DistanceUnit.unitKm();
      setUnitDistance(view, DistanceUnit.getDefaultUnit());
    }
    setUnitDistance(view, (String) view.getJComboBoxDistanceUnits()
        .getSelectedItem());

    // mis a jour de la vue
    view.getJTextFieldWeight().setValue(data.getWeight());
    view.getJLabelValDistanceRun().setText(DistanceUnit.format(data
        .getDistanceAll()));
    view.getJTextFieldDistanceMax().setValue(data.getDistanceMax());

    view.getJCheckBoxDefaultEquipment().setSelected(data.isDefault());
    view.getJCheckBoxWarning().setSelected(data.isAlert());
    view.getJSwitchBox().setSelected(data.isOn());
    view.getJLabelValFirstUsed().setText(dateToString(data.getFirstUsed()));
    view.getJLabelValLastUsed().setText(dateToString(data.getLastUsed()));

    if (data.getPath() != null) {
      File file = new File(data.getPath());
      if (file.isFile()) {
        view.getJButtonPhoto().setFile(file);
      }
    }
    else {
      view.getJButtonPhoto().setIcon(null);
    }

    // ajout des evenements
    view.addEvents(data);

    log.debug("<<updateView");
  }

  /**
   * Effacement de la vue equipement.
   */
  private void eraseGui(JPanelUserEquipement view) {
    // suppression des evenements
    view.removeEvents();

    view.getJTextFieldWeight().setValue(0);
    view.getJLabelValDistanceRun().setText("");
    view.getJTextFieldDistanceMax().setValue(0);
    view.getJCheckBoxWarning().setSelected(false);
    view.getJSwitchBox().setSelected(true);
    view.getJLabelValFirstUsed().setText("");
    view.getJLabelValLastUsed().setText("");
    view.getJButtonPhoto().setText("Photo");
    view.getJButtonPhoto().setIcon(null);
    view.getJTextFieldDistanceMax().setValue(0);
    view.getJLabelValDistanceRun().setText("0");
  }

  /**
   * Sauvegarde.
   * 
   * @throws SQLException
   */
  public void save() throws SQLException {
    log.info(">>save");

    // conversion unites
    if (!DistanceUnit.isUnitKm(unitDistance)) {
      // Distance max.
      data.setDistanceMax((float) DistanceUnit.convert(unitDistance,
                                                       DistanceUnit.unitKm(),
                                                       data.getDistanceMax()));
      // Distance parcouru.
      data.setDistance((float) DistanceUnit.convert(unitDistance, DistanceUnit
          .unitKm(), data.getDistance()));
    }
    if (!WeightUnit.isUnitKg(unitWeight)) {
      data.setWeight((float) WeightUnit.convert(unitWeight,
                                                WeightUnit.unitKg(),
                                                data.getWeight()));
    }

    // sauvegarde des equipements
    EquipementTableManager.getInstance().delete(data);
    EquipementTableManager.getInstance().store(data);
    EquipementTableManager.getInstance().logTable();

    // re-conversion unites
    if (!DistanceUnit.isUnitKm(unitDistance)) {
      // Distance max.
      data.setDistanceMax((float) DistanceUnit.convert(DistanceUnit.unitKm(),
                                                       unitDistance,
                                                       data.getDistanceMax()));
      // Distance parcouru.
      data.setDistance((float) DistanceUnit.convert(DistanceUnit.unitKm(),
                                                    unitDistance,
                                                    data.getDistance()));
    }
    if (!WeightUnit.isUnitKg(unitWeight)) {
      data.setWeight((float) WeightUnit.convert(WeightUnit.unitKg(),
                                                unitWeight,
                                                data.getWeight()));
    }
    
    log.info("<<save");
  }

  /**
   * Changement d'unit&eacute;.
   * 
   * @param view
   *          la vue.
   * @param e
   */
  public void performedUnit(JPanelUserEquipement view, UnitEvent e) {
    if (e.isEventDistance()) {
      setUnitDistance(view, e.getUnit());
    }
    else if (e.isEventWeight()) {
      setUnitWeight(view, e.getUnit());
    }
  }

  /**
   * Mis &agrave; jour de l'unit&eacute; de poids.
   * 
   * @param view
   *          la vue.
   * @param newUnit
   *          la nouvelle unit&eacute;
   */
  public void setUnitWeight(JPanelUserEquipement view, String newUnit) {
    data.setWeight((float) WeightUnit.convert(unitWeight, newUnit, data
        .getWeight()));
    view.getJTextFieldWeight().setValue(data.getWeight());

    unitWeight = newUnit;
    view.getJComboBoxWeightUnits().setSelectedItem(unitWeight);
  }

  /**
   * Mis &agrave; jour de l'unit&eacute; de poids.
   * 
   * @param view
   *          la vue.
   * @param newUnit
   *          la nouvelle unit&eacute;
   */
  public void setUnitDistance(JPanelUserEquipement view, String newUnit) {
    if (data == null || newUnit == null || newUnit.equals(unitDistance)) {
      return;
    }

    // Distance max.
    data.setDistanceMax((float) DistanceUnit.convert(unitDistance,
                                                     newUnit,
                                                     data.getDistanceMax()));
    // Distance parcouru.
    data.setDistance((float) DistanceUnit.convert(unitDistance, newUnit, data
        .getDistance()));

    view.getJTextFieldDistanceMax().setValue(data.getDistanceMax());
    view.getJLabelValDistanceRun().setText(DistanceUnit.format(data
        .getDistance()));

    unitDistance = newUnit;
    view.getJComboBoxDistanceUnits().setSelectedItem(unitDistance);
  }

  private String dateToString(Date date) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
    return ft.format(date);
  }

}
