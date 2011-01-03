package fr.turtlesport.unit.event;

import java.util.ArrayList;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedUnit;

/**
 * @author Denis Apparicio
 * 
 */
public final class UnitManager {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UnitManager.class);
  }

  /** Liste des listeners. */
  private ArrayList<UnitListener> listeners = new ArrayList<UnitListener>();

  private static UnitManager      singleton = new UnitManager();

  private UnitManager() {
  }

  /**
   * Restitue une instance du manager de language.
   * 
   * @return une instance du manager de language.
   */
  public static UnitManager getManager() {
    return singleton;
  }

  /**
   * Ajoute un listener.
   * 
   * @param l
   *          le listener &agrave; ajouter.
   */
  public void addUnitListener(UnitListener l) {
    if (l != null) {
      if (log.isDebugEnabled()) {
        log.debug("addUnitListener class=" + l.getClass().getName() + "(" + l
                  + ")");
      }
      listeners.add(l);
    }
  }

  /**
   * Supprime un listener.
   * 
   * @param l
   *          le listener &agrave; supprimer.
   */
  public boolean removeUnitListener(UnitListener l) {
    if (l == null) {
      return false;
    }
    boolean b = listeners.remove(l);
    if (log.isDebugEnabled()) {
      log.debug("remove class=" + l.getClass() + "(" + l + ") " + b);
    }
    l.completedRemoveUnitListener();
    return b;
  }

  /**
   * D&eacute;clenche le changement d'unit&eacute; de distance.
   * 
   * @param unit
   *          la nouvelle unit&eacute;.
   */
  public void fireDistanceChanged(String unit) {
    String newUnit;

    // Distance.
    fireUnitChanged(UnitEvent.DISTANCE, unit);

    // Allure
    newUnit = PaceUnit.unit(unit);
    if (newUnit != null) {
      fireUnitChanged(UnitEvent.PACE, newUnit);
    }

    // Vitesse
    newUnit = SpeedUnit.unit(unit);
    if (newUnit != null) {
      fireUnitChanged(UnitEvent.SPEED, newUnit);
    }
  }

  /**
   * D&eacute;clenche le changement d'unit&eacute; de vitesse.
   * 
   * @param unit
   *          la nouvelle unit&eacute;.
   */
  public void fireSpeedPaceChanged(String unit) {
    String newUnit;

    fireUnitChanged(UnitEvent.SPEED_PACE, unit);

    // Allure
    newUnit = PaceUnit.unit(unit);
    if (newUnit != null) {
      fireUnitChanged(UnitEvent.PACE, newUnit);
    }

    // Vitesse
    newUnit = SpeedUnit.unit(unit);
    if (newUnit != null) {
      fireUnitChanged(UnitEvent.SPEED, newUnit);
    }
  }

  /**
   * D&eacute;clenche le changement d'unit&eacute; de vitesse.
   * 
   * @param unit
   *          la nouvelle unit&eacute;.
   */
  public void fireSpeedChanged(String unit) {
    fireUnitChanged(UnitEvent.SPEED, unit);
  }

  /**
   * D&eacute;clenche le changement d'unit&eacute; de poids.
   * 
   * @param unit
   *          la nouvelle unit&eacute;.
   */
  public void fireWeightChanged(String unit) {
    fireUnitChanged(UnitEvent.WEIGHT, unit);
  }

  /**
   * D&eacute;clenche le changement d'unit&eacute; de hauteur.
   * 
   * @param unit
   *          la nouvelle unit&eacute;.
   */
  public void fireHeightChanged(String unit) {
    fireUnitChanged(UnitEvent.HEIGHT, unit);
  }

  /**
   * D&eacute;clenche le changement d'unit&eacute;.
   * 
   * @param type
   *          le type de l'unit&eacute;
   * @param unit
   *          la nouvelle unit&eacute;.
   */
  private void fireUnitChanged(int type, String unit) {
    UnitEvent e = new UnitEvent(type, unit);
    for (UnitListener l : listeners) {
      l.unitChanged(e);
    }
  }

}
