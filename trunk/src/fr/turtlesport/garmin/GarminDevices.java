package fr.turtlesport.garmin;

import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.IProductDevice;
import fr.turtlesport.UsbProtocolException;

/**
 * Liste les montres garmins reconnues.
 * 
 * @author Denis Apparicio
 * 
 */
public class GarminDevices {
 

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
    catch (UsbProtocolException e) {
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
