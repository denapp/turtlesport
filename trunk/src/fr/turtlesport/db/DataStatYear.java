package fr.turtlesport.db;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatYear extends AbstractDataStat {

  private int year;

  public DataStatYear(int year, double distance, double timeTot, int numberRaces) {
    super(distance, timeTot, numberRaces);
    this.year = year;
  }

  @Override
  public void headerCsv(Writer in) throws IOException {
    // Ecriture en tete
    in.write("YEAR");
    delimiter(in);
    super.headerCsv(in);
  }

  public void convertCsv(Writer in) throws IOException {
    in.write(String.valueOf(year));
    delimiter(in);
    super.convertCsv(in);
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
