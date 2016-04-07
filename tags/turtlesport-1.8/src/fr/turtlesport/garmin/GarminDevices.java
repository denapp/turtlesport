package fr.turtlesport.garmin;

import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.IProductDevice;
import fr.turtlesport.log.TurtleLogger;

/**
 * Liste les montres garmins reconnues.
 * 
 * @author Denis Apparicio
 * 
 */
public class GarminDevices {
  private static TurtleLogger  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GarminDevices.class);
  }


  private GarminDevices() {
  }

  /**
   * @return Liste les appareils garmin.
   */
  public static List<IProductDevice> list() {
    List<IProductDevice> result = new ArrayList<IProductDevice>();

    // Garmin USB
    try {
      result.add(GarminUsbDevice.init());
    }
    catch (Throwable e) {
      log.error("",e);
    }

    // Garmin FIT
    List<GarminFitDevice> fit = GarminFitDevice.getDevices();
    result.addAll(fit);
    
    // Garmin Garmin Foretrex 401
    List<GarminGpxDevice> gpx = GarminGpxDevice.getDevices();
    result.addAll(gpx);
    
    return result;
  }
}
