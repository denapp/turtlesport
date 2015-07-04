package fr.turtlesport.device;

import java.io.File;
import java.util.List;

/**
 * Carat&eacute;rise un device.
 *
 * @author Denis Apparicio
 *
 */
public interface Device extends IProductDevice {

    /**
     * @return les fichiers tracks de ce device.
     */
    List<File> getFiles();

    /**
     *
     * @return Restitue les nouveaux fichiers.
     */
    List<File> getNewFiles();
}
