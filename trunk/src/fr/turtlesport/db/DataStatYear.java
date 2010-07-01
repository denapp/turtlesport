package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatYear extends DataStat {

  private int year;

  public DataStatYear(int year, double distance, double timeTot, int numberRaces) {
    super(distance, timeTot, numberRaces);
    this.year = year;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "year=" + getYear() + " distance=" + getDistance() + " numberRaces="
           + getNumberRaces();
  }
}
