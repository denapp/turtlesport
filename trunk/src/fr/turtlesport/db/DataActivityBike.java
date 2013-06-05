package fr.turtlesport.db;

import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;
import fr.turtlesport.util.ResourceBundleUtility;

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
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());
    return rb.getString("name");
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
      return ImagesActivityRepository.IMAGE_SPORT_BIKE;
    }
    return super.getIconName();
  }

}
