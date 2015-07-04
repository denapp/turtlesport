package fr.turtlesport.device.garmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import fr.turtlesport.device.Device;
import org.xml.sax.SAXException;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.OperatingSystem;

/**
 * Classe pour les montres type Foretrex Fenix.
 * 
 * @author Denis Apparicio
 * 
 */
public class GarminGpxDevice implements Device {
  private static TurtleLogger log = (TurtleLogger) TurtleLogger
                                      .getLogger(GarminGpxDevice.class);

  private GarminDeviceInfo    info;

  private GarminGpxDevice(File dir) throws SAXException,
                                   IOException,
                                   ParserConfigurationException {
    this.info = new GarminDeviceInfo(dir);
  }

  /**
   * Restitue les informations sur le device.
   * 
   * @return Restitue les informations sur le device.
   */
  public GarminDeviceInfo getInfo() {
    return info;
  }

  private static String[] currentGpx() {
    String path = "GPX" + File.separator + "current" + File.separator;
    return new String[] { path + "current.gpx", path + "Current.gpx" };
  }

  /**
   * Restitue la liste des Mat&eacutes;riels fit support&eacute;s
   * 
   * @return la liste des Mat&eacutes;riels fit support&eacute;s.
   */
  public static List<GarminGpxDevice> getDevices() {
    List<GarminGpxDevice> list = new ArrayList<GarminGpxDevice>();

    List<File> dirs = getDirDevices();
    if (dirs.size() == 0) {
      return list;
    }

    // recuperation des repertoires des montres FIT
    for (File dir : dirs) {
      // Fichier : garmin/garmin/GPX/current/current.gpx 
      for (String name : currentGpx()) {
        File currentGpx = new File(dir, name);
        if (currentGpx.isFile()) {
          addDevice(dir, list);
          break;
        }
      }
    }

    return list;
  }

  /**
   * Recupere les fichiers Current.gpx
   */
  private static List<File> getDirDevices() {
    List<File> listDir = new ArrayList<File>();

    // Foreunner usb like Foreunner Foretrex 401

    // Mac OS X
    if (OperatingSystem.isMacOSX()) {
      listDir.add(new File("/Volumes/GARMIN"));
      listDir.add(new File("/Volumes/garmin"));
      listDir.add(new File("/Volumes/garmin/Garmin"));
      listDir.add(new File("/Volumes/GARMIN/Garmin"));
      return listDir;
    }
    // Windows
    else if (OperatingSystem.isWindows()) {
      for (File dir : File.listRoots()) {
        if (new File(dir, "GARMIN").exists()) {
          listDir.add(new File(dir, "GARMIN"));
          listDir.add(new File(dir, "GARMIN" + File.separator + "GARMIN"));
        }
      }
    }
    // Linux
    else if (OperatingSystem.isLinux()) {
      listDir.add(new File("/media/GARMIN/Garmin"));
      listDir.add(new File("/media/Garmin"));
      listDir.add(new File("/media/garmin"));
      String userName = System.getProperty("user.name");
      if (userName != null) {
        listDir.add(new File("/media/" + userName + "/GARMIN/Garmin"));
        listDir.add(new File("/media/" + userName + "/Garmin"));
        listDir.add(new File("/media/" + userName + "/garmin"));        
      }
    }

    return listDir;
  }

  private static void addDevice(File dir, List<GarminGpxDevice> list) {
    try {
      GarminGpxDevice device = new GarminGpxDevice(dir);
      if (log.isDebugEnabled()) {
        log.debug(device.getInfo());
      }
      list.add(device);
    }
    catch (Throwable e) {
      log.error("", e);
    }

  }

  @Override
  public List<File> getFiles() {
    List<File> list = new ArrayList<File>();
    list.add(getCurrentFile());
    return list;
  }

  @Override
  public List<File> getNewFiles() {
    return getFiles();
  }

  @Override
  public String displayName() {
    return info.getDisplayName();
  }

  @Override
  public String id() {
    return info.getId();
  }

  @Override
  public String softwareVersion() {
    return info.getSoftwareVersion();
  }

  @Override
  public String toString() {
    return displayName() + " (" + info.getId() + ")";
  }

  public File getCurrentFile() {
    for (String name : currentGpx()) {
      File currentGpx = new File(info.getDir(), name);
      if (currentGpx.isFile()) {
        return currentGpx;
      }
    }
    return null;
  }
}
