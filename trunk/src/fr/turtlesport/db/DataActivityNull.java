package fr.turtlesport.db;

import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;


/**
 * @author Denis Apparicio
 * 
 */
public class DataActivityNull extends AbstractDataActivity {
  
  /**
   * 
   */
  public DataActivityNull() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractDataActivity#getSportType()
   */
  @Override
  public int getSportType() {
    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractDataActivity#getName()
   */
  @Override
  public String getName() {
    return " ";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractDataActivity#setSportType(int)
   */
  @Override
  public void setSportType(int sportType) {
  }
  
  @Override
  public String getIconName() {
    if (super.getIconName() == null) {
      return ImagesActivityRepository.IMAGE_SPORT_OTHER;
    }
    return super.getIconName();
  }
}
