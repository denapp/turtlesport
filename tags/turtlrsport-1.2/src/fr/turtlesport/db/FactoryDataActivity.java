package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public final class FactoryDataActivity {

  private FactoryDataActivity() {
  }

  /**
   * Restitue une activit&eacute;.
   * 
   * @param sportType
   *          sport type.
   * @return l'activit&eacute;
   * @throws IllegalArgumentException
   *           si <code>sportType</code> est invalide.
   */
  public static AbstractDataActivity getInstance(int sportType) {
    if (sportType == DataActivityRun.SPORT_TYPE) {
      return new DataActivityRun();
    }
    if (sportType == DataActivityBike.SPORT_TYPE) {
      return new DataActivityBike();
    }
    if (sportType == DataActivityOther.SPORT_TYPE) {
      return new DataActivityOther();
    }
    return new DefaultDataActivity(sportType);
  }
}
