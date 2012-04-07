package fr.turtlesport.garmin;

import java.util.ArrayList;
import java.util.List;

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
  public static List<IGarminDevice> list() {
    List<IGarminDevice> result = new ArrayList<IGarminDevice>();

    // Garmin USB
    try {
      result.add(GarminUsbDevice.init());
    }
    catch (UsbProtocolException e) {
    }

    // Garmin FIT
    List<GarminFitDevice> fit = GarminFitDevice.getDevices();

    result.addAll(fit);
    return result;
  }
}
