package fr.turtlesport.db;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatYearWeek extends AbstractDataStat {

  private int week;

  private int year;

  public DataStatYearWeek(int year,
                          int week,
                          double distance,
                          double timeTot,
                          int numberRaces) {
    super(distance, timeTot, numberRaces);
    this.year = year;
    this.week = week;
  }

  @Override
  public void headerCsv(Writer in) throws IOException {
    // Ecriture en tete
    in.write("YEAR-WEEK");
    delimiter(in);
    super.headerCsv(in);
  }

  public void convertCsv(Writer in) throws IOException {
    in.write(getYear() + "-" + getWeek());
    delimiter(in);
    super.convertCsv(in);
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

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
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
