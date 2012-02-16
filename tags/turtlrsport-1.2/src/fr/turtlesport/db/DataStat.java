package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStat {

  private double distance;

  private double timeTot;

  private int    numberRaces = 0;

  public DataStat(double distance, double timeTot, int numberRaces) {
    super();
    this.distance = distance;
    this.timeTot = timeTot;
    this.numberRaces = numberRaces;
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
