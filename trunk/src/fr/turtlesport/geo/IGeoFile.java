package fr.turtlesport.geo;

import fr.turtlesport.device.IProductDevice;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Denis Apparicio
 */
public interface IGeoFile extends IGeoFileDesc {

    /**
     * Lecture.
     *
     * @param file
     * @param productDevice
     * @return restitue les routes.
     * @throws GeoLoadException
     * @throws FileNotFoundException
     */
    IGeoRoute[] load(File file, IProductDevice productDevice) throws GeoLoadException, FileNotFoundException;
}
