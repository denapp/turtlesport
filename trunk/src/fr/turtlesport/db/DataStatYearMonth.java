package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatYearMonth extends DataStatYear {

  private int month;

  public DataStatYearMonth(int year, int month, double distance, int numberRaces) {
    super(year, distance, numberRaces);
    this.month = month;
  }

  public DataStatYearMonth(int year, int month, double distance) {
    this(year, month, distance, 0);
    this.month = month;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  @Override
  public String toString() {
    return getYear() + "-" + getMonth() + "=" + getDistance();
  }

}
