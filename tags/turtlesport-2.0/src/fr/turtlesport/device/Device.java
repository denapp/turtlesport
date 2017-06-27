package fr.turtlesport.device;

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
    List<FileDevice> getFiles();

    /**
     *
     * @return Restitue les nouveaux fichiers.
     */
    List<FileDevice> getNewFiles();
}
