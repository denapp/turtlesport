package fr.turtlesport.db;

import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class DataActivityRun extends AbstractDataActivity {
  public static final int SPORT_TYPE = 0;

  /**
   * 
   */
  public DataActivityRun() {
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
   * @see fr.turtlesport.db.AbstractDataActivity#setSportType(int)
   */
  @Override
  public void setSportType(int sportType) {
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
  
  @Override
  public String getIconName() {
    return ImagesActivityRepository.IMAGE_SPORT_RUN;
  }
}
