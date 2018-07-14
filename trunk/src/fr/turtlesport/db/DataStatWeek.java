package fr.turtlesport.db;

import fr.turtlesport.lang.LanguageManager;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormatSymbols;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatWeek extends AbstractDataStat {

  private int dayOfWeek;

  public DataStatWeek(int dayOfWeek, double distance, double timeTot, int numberRaces) {
    super(distance, timeTot, numberRaces);
    this.dayOfWeek = dayOfWeek;
  }

  @Override
  public void headerCsv(Writer in) throws IOException {
    // Ecriture en tete
    in.write("DAY");
    delimiter(in);
    super.headerCsv(in);
  }

  public void convertCsv(Writer in) throws IOException {
    //in.write(String.valueOf(dayOfWeek));
    String[] days = new DateFormatSymbols(
            LanguageManager.getManager().getCurrentLang().getLocale()).getWeekdays();
    int index = dayOfWeek+2;
    if (index > 7) {
      index = 1;
    }
    in.write(days[index]);
    delimiter(in);
    super.convertCsv(in);
  }

  public int getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(int dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }
}
