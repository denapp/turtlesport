package fr.turtlesport.ui.swing.model;

import javax.swing.JFormattedTextField;

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataHeartZone;
import fr.turtlesport.db.DataSpeedZone;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JPanelUserActivity;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.util.ConvertStringTo;

/**
 * @author Denis Apparicio
 * 
 */
public class ModelActivity {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ModelActivity.class);
  }

  /** L'activite. */
  private AbstractDataActivity dataActivity;

  /** Unites. */
  private String               unitSpeedAndPace;

  private boolean              isFcMax = false;

  /**
   * Construit le model de l'activit&eacute;.
   * 
   * @param dataActivity
   */
  public ModelActivity(AbstractDataActivity dataActivity) {
    this.dataActivity = dataActivity;

    unitSpeedAndPace = SpeedPaceUnit.getDefaultUnit();
  }

  public AbstractDataActivity getDataActivity() {
    return dataActivity;
  }

  /**
   * Restitue le type de sport.
   * 
   * @return le type de sport.
   */
  public int getSportType() {
    return dataActivity.getSportType();
  }

  /**
   * Mis &aecute; jour de la vue.
   * 
   * @param view
   */
  public void updateView(JPanelUserActivity view) {
    log.debug(">>updateView " + dataActivity.getSportType());

    // effacement de la vue
    eraseGui(view);

    // mis a jour du model
    view.setModel(this);

    // mis a jour des zones
    updateZones(view);
    setUnitHeart(view);

    // mis a jour sport par defaut
    view.getJCheckBoxDefaultActivity().setSelected(dataActivity
        .isDefaultActivity());

    log.debug("<<updateView");
  }

  /**
   * Effacement de la vue.
   */
  private void eraseGui(JPanelUserActivity view) {
    // suppression des evenements
    view.removeEvents();

    view.getJTextFieldMaxHeartRate().setValue(0);
    view.getJTextFieldSpeedName().setText("");
    view.getJTextFieldZoneSpeedMin().setValue(0.0);
    view.getJTextFieldZoneSpeedMax().setValue(0.0);
    view.getJTextFieldSpeedName().setText("");
    view.getJTextFieldZoneSpeedMin().setValue(0.0);
    view.getJTextFieldZoneSpeedMax().setValue(0.0);

  }

  /**
   * Mis a jour des zones.
   */
  private void updateZones(JPanelUserActivity view) {
    view.addEvents(dataActivity);

    // conversion si besoin
    if (!SpeedPaceUnit.isUnitKmPerH(SpeedPaceUnit.getDefaultUnit())) {
      unitSpeedAndPace = SpeedPaceUnit.unitKmPerH();
      setUnitSpeedAndSpace(view, SpeedPaceUnit.getDefaultUnit());
    }
  }

  public void beforeSave() {
    // Zones
    // ------------------------------------------

    // conversion unite des zones
    if (!SpeedPaceUnit.isUnitKmPerH(unitSpeedAndPace)) {
      // mis a jour des valeurs
      Object newSpeedLow, newSpeedHigh;

      for (DataSpeedZone data : dataActivity.getSpeedZones()) {
        newSpeedLow = SpeedPaceUnit.convert(unitSpeedAndPace, SpeedPaceUnit
            .unitKmPerH(), data.getLowSpeed());
        newSpeedHigh = SpeedPaceUnit.convert(unitSpeedAndPace, SpeedPaceUnit
            .unitKmPerH(), data.getHighSpeed());
        data.setLowSpeed(new Float((Double) newSpeedLow));
        data.setHighSpeed(new Float((Double) newSpeedHigh));
      }
    }

    if (isFcMax) {
      int maxHeart = dataActivity.getMaxHeartRate();
      for (DataHeartZone data : dataActivity.getHeartZones()) {
        data.setHighHeartRate((data.getHighHeartRate() * maxHeart) / 100);
        data.setLowHeartRate((data.getLowHeartRate() * maxHeart) / 100);
      }
    }
  }

  public void afterSave() {
    // re-conversion des unites
    if (!SpeedPaceUnit.isUnitKmPerH(unitSpeedAndPace)) {
      // mise a jour des valeurs
      Object newSpeedLow, newSpeedHigh;

      for (DataSpeedZone data : dataActivity.getSpeedZones()) {
        newSpeedLow = SpeedPaceUnit.convert(SpeedPaceUnit.unitKmPerH(),
                                            unitSpeedAndPace,
                                            data.getLowSpeed());
        newSpeedHigh = SpeedPaceUnit.convert(SpeedPaceUnit.unitKmPerH(),
                                             unitSpeedAndPace,
                                             data.getHighSpeed());
        if (newSpeedHigh instanceof String) {
          Float fNewSpeedLow = (Float) ConvertStringTo
              .toObject(Float.TYPE, ((String) newSpeedLow).replace(':', '.'));
          Float fNewSpeedHigh = (Float) ConvertStringTo
              .toObject(Float.TYPE, ((String) newSpeedHigh).replace(':', '.'));
          data.setLowSpeed(fNewSpeedLow);
          data.setHighSpeed(fNewSpeedHigh);
        }
        else {
          data.setLowSpeed(new Float((Double) newSpeedLow));
          data.setHighSpeed(new Float((Double) newSpeedHigh));
        }
      }
    }

    if (isFcMax) {
      int maxHeart = dataActivity.getMaxHeartRate();
      for (DataHeartZone data : dataActivity.getHeartZones()) {
        data.setHighHeartRate(100 * data.getHighHeartRate() / maxHeart);
        data.setLowHeartRate(100 * data.getLowHeartRate() / maxHeart);
      }
    }
  }

  /**
   * Changement d'unit&eacute;.
   * 
   * @param view
   *          la vue.
   * @param event
   */
  public void performedUnit(JPanelUserActivity view, UnitEvent e) {
    if (e.isEventSpeedAndPace()) {
      setUnitSpeedAndSpace(view, e.getUnit());
    }
  }

  /**
   * Mis &agrave; jour de l'unit&eacute; cardiaque.
   * 
   * @param view
   *          la vue.
   * @param newUnit
   *          la nouvelle unit&eacute;
   */
  public void setUnitHeart(JPanelUserActivity view) {
    if (dataActivity.getHeartZones().length == 0
        || (isFcMax == view.getJRadioButtonPourFcMax().isSelected())) {
      return;
    }

    isFcMax = view.getJRadioButtonPourFcMax().isSelected();
    int maxHeart = Integer.parseInt(view.getJTextFieldMaxHeartRate().getText());

    int index = view.getJComboBoxZoneHeart().getSelectedIndex();
    DataHeartZone data;
    for (int i = 0; i < dataActivity.getHeartZones().length; i++) {
      data = dataActivity.getHeartZones()[i];

      // mis a jour valeurs
      if (isFcMax && maxHeart != 0) {
        data.setHighHeartRate(100 * data.getHighHeartRate() / maxHeart);
        data.setLowHeartRate(100 * data.getLowHeartRate() / maxHeart);
      }
      else {
        data.setHighHeartRate((data.getHighHeartRate() * maxHeart) / 100);
        data.setLowHeartRate((data.getLowHeartRate() * maxHeart) / 100);
      }
    }

    data = dataActivity.getHeartZones()[index];
    view.getJTextFieldZoneHeartMax().setValue(data.getHighHeartRate());
    view.getJTextFieldZoneHeartMin().setValue(data.getLowHeartRate());
  }

  /**
   * Mis &agrave; jour de l'unit&eacute; de vitesse.
   * 
   * @param view
   *          la vue.
   * @param newUnit
   *          la nouvelle unit&eacute;
   */
  public void setUnitSpeedAndSpace(JPanelUserActivity view, String newUnit) {
    if (newUnit == null || newUnit.equals(unitSpeedAndPace)) {
      return;
    }

    int index = view.getJComboBoxZoneSpeed().getSelectedIndex();

    // mis a jour des valeurs
    Object newSpeedLow, newSpeedHigh;
    DataSpeedZone data;
    for (int i = 0; i < dataActivity.getSpeedZones().length; i++) {
      data = dataActivity.getSpeedZones()[i];

      newSpeedLow = SpeedPaceUnit.convert(unitSpeedAndPace, newUnit, data
          .getLowSpeed());
      newSpeedHigh = SpeedPaceUnit.convert(unitSpeedAndPace, newUnit, data
          .getHighSpeed());

      // Les formatters
      if (i == index) {
        convertSpeedFormat(view.getJTextFieldZoneSpeedMin(), newSpeedLow);
        convertSpeedFormat(view.getJTextFieldZoneSpeedMax(), newSpeedHigh);
      }

      // Vitesse basse et haute
      if (newSpeedLow instanceof String) {
        // Time
        data.setLowSpeed(Float.parseFloat(((String) newSpeedLow).replace(':',
                                                                         '.')));
        data.setHighSpeed(Float
            .parseFloat(((String) newSpeedHigh).replace(':', '.')));
      }
      else {
        data.setLowSpeed(new Float((Double) newSpeedLow));
        data.setHighSpeed(new Float((Double) newSpeedHigh));
      }
    }

    unitSpeedAndPace = newUnit;
    view.getJComboBoxSpeedAndPaceUnit().setSelectedItem(unitSpeedAndPace);
  }

  private void convertSpeedFormat(JFormattedTextField jTextFieldZoneSpeed,
                                  Object newSpeed) {
    try {
      if (newSpeed instanceof String) {
        if (!JPanelUserActivity.TIME_FORMATTER_FACTORY
            .equals(jTextFieldZoneSpeed.getFormatterFactory())) {
          jTextFieldZoneSpeed
              .setFormatterFactory(JPanelUserActivity.TIME_FORMATTER_FACTORY);
        }
        jTextFieldZoneSpeed.setText(newSpeed.toString());
      }
      else {
        if (!JPanelUserActivity.SPEED_FORMATTER_FACTORY
            .equals(jTextFieldZoneSpeed.getFormatterFactory())) {
          jTextFieldZoneSpeed.setValue(null);
          jTextFieldZoneSpeed
              .setFormatterFactory(JPanelUserActivity.SPEED_FORMATTER_FACTORY);
        }
        jTextFieldZoneSpeed.setValue(newSpeed);
      }

    }
    catch (Throwable e) {
      log.warn("", e);
    }
  }

}
