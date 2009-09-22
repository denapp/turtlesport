package fr.turtlesport.ui.swing.model;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import fr.turtlesport.db.DataUser;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelUserAthlete;
import fr.turtlesport.unit.HeightUnit;
import fr.turtlesport.unit.WeightUnit;
import fr.turtlesport.unit.event.UnitEvent;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelAthlete {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelAthlete.class);
  }

  private DataUser            data;

  /** Unites. */
  private String              unitWeight = WeightUnit.getDefaultUnit();

  private String              unitHeight = HeightUnit.getDefaultUnit();

  /**
   * Construit le model de l'athl&egrave;te.
   * 
   * @param data
   *          l'athl&egrave;te.
   */
  public ModelAthlete(DataUser data) {
    this.data = data;
  }

  public DataUser getData() {
    return data;
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   */
  public void updateView(JPanelUserAthlete view) {
    log.debug(">>updateView");

    // effacement de la vue
    eraseGui(view);

    // mis a jour du model
    view.setModel(this);

    // Mis a jour des donnees
    view.getJLabelLastName().setText(data.getLastName());
    view.getJLabelFirstName().setText(data.getFirstName());
    view.getJRadioButtonMale().setSelected(data.isMale());

    // poids conversion si besoin
    if (!WeightUnit.isUnitKg(WeightUnit.getDefaultUnit())) {
      data.setWeight((float) WeightUnit.convert(WeightUnit.unitKg(), WeightUnit
          .getDefaultUnit(), data.getWeight()));
    }
    view.getJTextFieldWeight().setValue(data.getWeight());

    // taille
    if (!HeightUnit.isUnitCm(HeightUnit.getDefaultUnit())) {
      data.setHeight((float) HeightUnit.convert(HeightUnit.unitCm(), HeightUnit
          .getDefaultUnit(), data.getHeight()));
    }
    view.getJTextFieldHeight().setValue(data.getHeight());

    // Date de naissance (si pas de date 20ans par defaut)
    Date dateBirth;
    if (data.getBirthDate() == null) {
      Calendar cal = Calendar.getInstance(LanguageManager.getManager()
          .getCurrentLang().getLocale());
      cal.add(Calendar.YEAR, -20);
      dateBirth = cal.getTime();
    }
    else {
      dateBirth = data.getBirthDate();
    }
    view.getJDatePicker().setDate(dateBirth);

    // image
    if (data.getPath() != null) {
      view.getJButtonPhoto().setFile(data.getPath());
    }
    else {
      view.getJButtonPhoto().setText("Photo");
      view.getJButtonPhoto().setIcon(null);
    }

    // ajout des evenements
    view.addEvents(data);

    log.debug("<<updateView");
  }

  /**
   * Effacement de la vue.
   */
  private void eraseGui(JPanelUserAthlete view) {
    // suppression des evenements
    view.removeEvents();

    view.getJLabelLastName().setText("");
    view.getJLabelFirstName().setText("");
    view.getJTextFieldWeight().setValue(0);
    view.getJTextFieldHeight().setValue(0);
    view.getJDatePicker().setDate(new Date());
    view.getJButtonPhoto().setText("Photo");
    view.getJButtonPhoto().setIcon(null);
  }

  /**
   * Sauvegarde.
   * 
   * @throws SQLException
   */
  public void save() throws SQLException {
    log.info(">>save");

    // conversion unites
    if (!WeightUnit.isUnitKg(unitWeight)) {
      data.setWeight((float) WeightUnit.convert(unitWeight,
                                                WeightUnit.unitKg(),
                                                data.getWeight()));
    }

    // sauvegarde des equipements

    // re-conversion unites
    if (!WeightUnit.isUnitKg(unitWeight)) {
      data.setWeight((float) WeightUnit.convert(WeightUnit.unitKg(),
                                                unitWeight,
                                                data.getWeight()));
    }

    log.info("<<save");
  }

  public void beforeSave() {
    // conversion unite des zones
    if (!WeightUnit.isUnitKg(unitWeight)) {
      double newWeight = WeightUnit.convert(unitWeight,
                                            WeightUnit.unitKg(),
                                            data.getHeight());
      data.setWeight((float) newWeight);
    }
    if (!HeightUnit.isUnitCm(unitHeight)) {
      double newHeight = HeightUnit.convert(unitWeight,
                                            HeightUnit.unitCm(),
                                            data.getHeight());
      data.setHeight((float) newHeight);
    }
  }

  public void afterSave() {
    // re-conversion des unites
    if (!WeightUnit.isUnitKg(unitWeight)) {
      double newWeight = WeightUnit.convert(WeightUnit.unitKg(),
                                            unitWeight,
                                            data.getHeight());
      data.setWeight((float) newWeight);
    }
    if (!HeightUnit.isUnitCm(unitHeight)) {
      double newHeight = HeightUnit.convert(HeightUnit.unitCm(),
                                            unitWeight,
                                            data.getHeight());
      data.setHeight((float) newHeight);
    }
  }

  /**
   * Changement d'unit&eacute;.
   * 
   * @param view
   *          la vue.
   * @param event
   */
  public void performedUnit(JPanelUserAthlete view, UnitEvent e) {
    if (e.isEventWeight()) {
      setUnitWeight(view, e.getUnit());
    }
    else if (e.isEventHeight()) {
      setUnitHeight(view, e.getUnit());
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
  public void setUnitWeight(JPanelUserAthlete view, String newUnit) {
    if (data == null || newUnit == null || newUnit.equals(unitWeight)) {
      return;
    }

    data.setWeight((float) WeightUnit.convert(unitWeight, newUnit, data
        .getWeight()));

    view.getJTextFieldWeight().setValue(data.getWeight());

    unitWeight = newUnit;
    view.getJComboBoxWeightUnits().setSelectedItem(unitWeight);
  }

  /**
   * Mis &agrave; jour de l'unit&eacute; de hauteur.
   * 
   * @param view
   *          la vue.
   * @param newUnit
   *          la nouvelle unit&eacute;
   */
  public void setUnitHeight(JPanelUserAthlete view, String newUnit) {
    if (data == null || newUnit == null || newUnit.equals(unitHeight)) {
      return;
    }

    data.setHeight((float) HeightUnit.convert(unitHeight, newUnit, data
        .getHeight()));

    if (HeightUnit.isUnitCm(newUnit)) {
      data.setHeight(new Float(data.getHeight()).intValue());
    }

    view.getJTextFieldHeight().setValue(data.getHeight());

    unitHeight = newUnit;
    view.getJComboBoxHeightUnits().setSelectedItem(unitHeight);
  }
}
