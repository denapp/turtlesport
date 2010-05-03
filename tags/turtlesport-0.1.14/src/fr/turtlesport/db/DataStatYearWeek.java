package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatYearWeek extends DataStatYear {

  private int week;

  public DataStatYearWeek(int year, int week, double distance, int numberRaces) {
    super(year, distance, numberRaces);
    this.week = week;
  }

  public DataStatYearWeek(int year, int week, double distance) {
    this(year, week, distance, 0);
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
