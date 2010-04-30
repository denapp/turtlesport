package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStat {

  private double distance;

  private int    numberRaces = 0;

  public DataStat(double distance, int numberRaces) {
    super();
    this.distance = distance;
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

}
