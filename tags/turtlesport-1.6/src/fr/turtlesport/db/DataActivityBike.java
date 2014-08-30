package fr.turtlesport.db;

import fr.turtlesport.lang.CommonLang;
import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;

/**
 * @author Denis Apparicio
 * 
 */
public class DataActivityBike extends AbstractDataActivity {
  public static final int SPORT_TYPE = 1;

  /**
   * 
   */
  public DataActivityBike() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractDataActivity#getSportType()
   */
  @Override
  public int getSportType() {
    return SPORT_TYPE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractDataActivity#getName()
   */
  @Override
  public String getName() {
    return CommonLang.INSTANCE.getString("sportBike");
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
    return ImagesActivityRepository.IMAGE_SPORT_BIKE;
  }

}
