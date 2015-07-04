package fr.turtlesport.device;

import fr.turtlesport.device.energympro.EnergymproDevice;
import fr.turtlesport.device.garmin.GarminDevices;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Apparicio
 */
public final class Devices {

    private Devices() {
    }

    /**
     * @return Liste les appareils garmin.
     */
    public static List<Device> list() {
        List<Device> result = new ArrayList<Device>();

        // Garmin
        result.addAll(GarminDevices.list());

        // Energympro
        result.addAll(EnergymproDevice.getDevices());

        return result;
    }
}
