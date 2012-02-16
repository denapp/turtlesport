package fr.turtlesport.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Denis Apparicio
 * 
 */
public class DateFormatSymbolsExtended extends DateFormatSymbols {

  public DateFormatSymbolsExtended() {
    super();
  }

  public DateFormatSymbolsExtended(Locale locale) {
    super(locale);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.text.DateFormatSymbols#getShortWeekdays()
   */
  @Override
  public String[] getShortWeekdays() {
    String[] days = super.getShortWeekdays();
    String[] daysOrder = new String[days.length];
    daysOrder[0] = days[Calendar.MONDAY];
    daysOrder[1] = days[Calendar.TUESDAY];
    daysOrder[2] = days[Calendar.WEDNESDAY];
    daysOrder[3] = days[Calendar.THURSDAY];
    daysOrder[4] = days[Calendar.FRIDAY];
    daysOrder[5] = days[Calendar.SATURDAY];
    daysOrder[6] = days[Calendar.SUNDAY];
    return daysOrder;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.text.DateFormatSymbols#getWeekdays()
   */
  @Override
  public String[] getWeekdays() {
    String[] days = super.getWeekdays();
    String[] daysOrder = new String[days.length];
    daysOrder[0] = days[Calendar.MONDAY];
    daysOrder[1] = days[Calendar.TUESDAY];
    daysOrder[2] = days[Calendar.WEDNESDAY];
    daysOrder[3] = days[Calendar.THURSDAY];
    daysOrder[4] = days[Calendar.FRIDAY];
    daysOrder[5] = days[Calendar.SATURDAY];
    daysOrder[6] = days[Calendar.SUNDAY];
    return daysOrder;
  }

}
