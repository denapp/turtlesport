package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatYearWeek extends DataStatYear {

  private int week;

  public DataStatYearWeek(int year,
                          int week,
                          double distance,
                          double timeTot,
                          int numberRaces) {
    super(year, distance, timeTot, numberRaces);
    this.week = week;
  }

  /**
   * Ajoute une distance.
   * 
   * @param distance
   *          la distance
   */
  public void addDistance(double distance) {
    setDistance(getDistance() + distance);
  }

  public int getWeek() {
    return week;
  }

  public void setWeek(int week) {
    this.week = week;
  }

  @Override
  public String toString() {
    return getYear() + "-" + getWeek() + "=" + getDistance();
  }
}
