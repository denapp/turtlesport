package fr.turtlesport.db;

import fr.turtlesport.lang.CommonLang;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractDataStat implements IDataStat {

  private double distance;

  private double timeTot;

  private int    numberRaces = 0;

  public AbstractDataStat(double distance, double timeTot, int numberRaces) {
    super();
    this.distance = distance;
    this.timeTot = timeTot;
    this.numberRaces = numberRaces;
  }

  @Override
  public void headerCsv(Writer in) throws IOException {
    // Ecriture en tete;
    in.write(CommonLang.INSTANCE.distanceWithUnit());
    delimiter(in);
    in.write(CommonLang.INSTANCE.getString("time"));
    delimiter(in);
    in.write(CommonLang.INSTANCE.getString("Activity"));
  }

  @Override
  public void convertCsv(Writer in) throws IOException {
    in.write(DistanceUnit.formatDefaultUnit(distance));
    delimiter(in);
    in.write(TimeUnit.formatHundredSecondeTime((long)timeTot));
    delimiter(in);
    in.write(String.valueOf(numberRaces));
  }

  public void delimiter(Writer in) throws IOException {
    in.write(';');
  }

  public int getNumberRaces() {
    return numberRaces;
  }

  public void setNumberRaces(int numberRaces) {
    this.numberRaces = numberRaces;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double getTimeTot() {
    return timeTot;
  }

  public void setTimeTot(double timeTot) {
    this.timeTot = timeTot;
  }

}
