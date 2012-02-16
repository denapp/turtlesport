package fr.turtlesport.ui.swing.action;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class YearMonth {
  public int year  = -1;

  public int month = -1;

  public YearMonth(int year, int month) {
    this.year = year;
    this.month = month;
  }

  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public abstract String getLibelle();
}
