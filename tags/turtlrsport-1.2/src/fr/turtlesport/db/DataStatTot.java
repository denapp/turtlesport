package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataStatTot {

  private int    calories;

  private double distanceTot;

  private int    timeTot;

  public DataStatTot(int calories, double distanceTot, int timeTot) {
    super();
    this.calories = calories;
    this.distanceTot = distanceTot;
    this.timeTot = timeTot;
  }

  public int getCalories() {
    return calories;
  }

  public void setCalories(int calories) {
    this.calories = calories;
  }

  public double getDistanceTot() {
    return distanceTot;
  }

  public void setDistanceTot(double distanceTot) {
    this.distanceTot = distanceTot;
  }

  public int getTimeTot() {
    return timeTot;
  }

  public void setTimeTot(int timeTot) {
    this.timeTot = timeTot;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "calories=" + calories + " distanceTot=" + distanceTot + " timeTot="
           + timeTot;
  }

}
