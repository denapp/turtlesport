package fr.turtlesport.db;

import fr.turtlesport.lang.CommonLang;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatYearMonth extends AbstractDataStat {

  private int year;

  private int month;

  public DataStatYearMonth(int year,
                           int month,
                           double distance,
                           double timeTot,
                           int numberRaces) {
    super(distance, timeTot, numberRaces);
    this.year = year;
    this.month = month;
  }

  @Override
  public void headerCsv(Writer in) throws IOException {
    // Ecriture en tete
    in.write("YEAR-MONTH");
    delimiter(in);
    super.headerCsv(in);
  }

  public void convertCsv(Writer in) throws IOException {
    in.write(getYear() + "-" + getMonth());
    delimiter(in);
    super.convertCsv(in);
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
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
