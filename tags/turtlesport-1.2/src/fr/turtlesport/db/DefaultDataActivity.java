package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DefaultDataActivity extends AbstractDataActivity {

  private int    sportType = -1;

 
  /**
   * @param sportType
   */
  public DefaultDataActivity(int sportType) {
    super();
    this.sportType = sportType;
  }

  /**
   * @param name
   */
  public DefaultDataActivity(String name) {
    super();
    setName(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractDataActivity#getSportType()
   */
  @Override
  public int getSportType() {
    return sportType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractDataActivity#setSportType(int)
   */
  @Override
  public void setSportType(int sportType) {
    this.sportType = sportType;
  }

}
