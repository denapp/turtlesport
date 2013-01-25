package fr.turtlesport.garmin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.turtlesport.IProductDevice;
import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.garmin.fit.FitFile;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public class GarminFitDevice implements IProductDevice {
  private static TurtleLogger   log      = (TurtleLogger) TurtleLogger
                                             .getLogger(GarminFitDevice.class);

  private GarminDeviceInfo               info;

  /** Noms des repertoires TCX */
  private static final String[] DIRS_TCX = { "History" };

  /** Noms des repertoires FIT */
  private static final String[] DIRS_FIT = { "Activities", "ACTIVITY", "Activity" };

  private GarminFitDevice(File dir) throws SAXException,
                                   IOException,
                                   ParserConfigurationException {
    this.info = new GarminDeviceInfo(dir);
  }

  /**
   * 
   * @return Restitue les nouveaux fichiers.
   */
  public List<File> getNewFiles() {
    return (getTcxFiles().size() > 0) ? getNewTcxFiles() : getNewFitFiles();
  }

  /**
   * 
   * @return Restitue les fichiers tcx.
   */
  public List<File> getTcxFiles() {
    for (String dir : DIRS_TCX) {
      List<File> files = getFiles(dir, ".tcx");
      if (files.size() > 0) {
        return files;
      }
    }
    return new ArrayList<File>();
  }

  /**
   * Restitue les fichiers tcx.
   * 
   * @return les fichiers tcx.
   */
  public List<File> getNewTcxFiles() {
    List<File> files = getTcxFiles();

    Iterator<File> it = files.iterator();
    while (it.hasNext()) {
      if (isTcxAlreadyImport(it.next())) {
        it.remove();
      }
    }

    return files;
  }

  /**
   * Restitue les fichiers fit.
   * 
   * @return les fichiers fit.
   */
  public List<File> getFitFiles() {
    for (String dir : DIRS_FIT) {
      List<File> files = getFiles(dir, ".fit");
      if (files.size() > 0) {
        return files;
      }
    }
    return new ArrayList<File>();
  }

  /**
   * Restitue les nouveaux fichiers fit.
   * 
   * @return les fichiers fir.
   */
  public List<File> getNewFitFiles() {
    List<File> files = getFitFiles();

    Iterator<File> it = files.iterator();
    while (it.hasNext()) {
      if (isFitAlreadyImport(it.next())) {
        it.remove();
      }
    }

    return files;
  }

  private boolean isTcxAlreadyImport(File file) {
    String sDate = file.getName().substring(0, file.getName().length() - 4);

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    try {
      Date date;
      synchronized (getClass()) {
        date = df.parse(sDate);
      }
      return isAlreadyImport(date);
    }
    catch (ParseException e) {
      return true;
    }
  }

  private boolean isFitAlreadyImport(File file) {
    try {
      return isAlreadyImport(new FitFile().retreiveDate(file));
    }
    catch (FileNotFoundException e) {
    }
    catch (GeoLoadException e) {
      log.error("", e);
    }
    return false;
  }

  private boolean isAlreadyImport(Date date) {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTimeZone(TimeZone.getTimeZone("GMT"));
      cal.setTime(date);
      date = cal.getTime();

      return (RunTableManager.getInstance().find(DataUser.getAllUser().getId(),
                                                 date) != -1);
    }
    catch (Throwable e) {
      log.error("", e);
      return true;
    }
  }

  private List<File> getFiles(final String dirName, final String ext) {
    List<File> files = new ArrayList<File>();

    // recuperation des repertoires des montres FIT
    File[] dirsFit = new File(info.getDir(), dirName)
        .listFiles(new FileFilter() {
          public boolean accept(File pathname) {
            return pathname != null && pathname.isFile()
                   && pathname.getPath().toLowerCase().endsWith(ext);
          }
        });

    // filtre sur les montres supportants TCX
    if (dirsFit != null) {
      for (File f : dirsFit) {
        files.add(f);
      }
    }
    return files;
  }

  /**
   * Restitue les informations sur le device.
   * 
   * @return Restitue les informations sur le device.
   */
  public GarminDeviceInfo getInfo() {
    return info;
  }

  /**
   * Restitue la liste des Mat&eacutes;riels fit support&eacute;s
   * 
   * @return la liste des Mat&eacutes;riels fit support&eacute;s.
   */
  public static List<GarminFitDevice> getDevices() {
    List<GarminFitDevice> list = new ArrayList<GarminFitDevice>();

    List<File> dirs = getDirDevices();
    if (dirs.size() == 0) {
      return list;
    }

    // recuperation des repertoires des montres FIT
    for (File dir : dirs) {
      if (isGarminFitAvailable(dir)) {
        addDevice(dir, list);
      }

      // recherche sous repertoire
      File[] dirsFit = dir.listFiles(new FileFilter() {
        public boolean accept(File pathname) {
          return isGarminFitAvailable(pathname);
        }
      });

      // filtre sur les montres supportants fit
      if (dirsFit != null) {
        for (File f : dirsFit) {
          addDevice(f, list);
        }
      }
    }
    return list;
  }

  private static void addDevice(File f, List<GarminFitDevice> list) {
    try {
      GarminFitDevice device = new GarminFitDevice(f);
      if (log.isDebugEnabled()) {
        log.debug(device.getInfo());
      }
      list.add(device);
    }
    catch (Throwable e) {
      log.error("", e);
    }

  }

  private static boolean isGarminFitAvailable(File pathname) {
    if (pathname != null && pathname.isDirectory()
        && isFitorTcxSupported(pathname)) {
      for (String s : GarminDeviceInfo.GARMIN_DEVICE_NAME) {
        if (new File(pathname, s).isFile()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Recupere les repertoires des devices.
   */
  private static List<File> getDirDevices() {
    List<File> listDir = new ArrayList<File>();

    // Mac OS X
    if (OperatingSystem.isMacOSX()) {
      // Ant
      File file = new File(System.getProperty("user.home"),
                           "/Library/Application Support/Garmin/Devices");
      listDir.add(file);

      // Foreunner usb like Foreunner 10, 110..
      file = new File("/Volumes/GARMIN/Garmin");
      listDir.add(file);

      return listDir;
    }
    // Windows
    else if (OperatingSystem.isWindows()) {
      StringBuilder st = new StringBuilder();
      String tmp = System.getenv("APPDATA");
      if (tmp != null) {
        st.append(tmp);
        st.append("\\GARMIN\\Devices");
        listDir.add(new File(st.toString()));
      }

      // Ajout des Forerunner 10, 110, Garmin Fenix...
      for (File dir : File.listRoots()) {
        if (new File(dir, "GARMIN").exists()) {
          listDir.add(new File(dir, "GARMIN"));
        }
      }
    }
    // Linux
    else if (OperatingSystem.isLinux()) {
      // Forerunner 10, 110..
      listDir.add(new File("/media/GARMIN"));

      String userName = System.getProperty("user.name");
      if (userName != null) {
        listDir.add(new File("/media/" + userName + "/GARMIN"));
      }      
    }

    return listDir;
  }

  private static boolean isFitorTcxSupported(File dirFit) {
    for (String dir : DIRS_FIT) {
      if (new File(dirFit, dir).isDirectory()) {
        return true;
      }
    }
    for (String dir : DIRS_TCX) {
      if (new File(dirFit, dir).isDirectory()) {
        return true;
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.garmin.IGarminDevice#displayName()
   */
  public String displayName() {
    return info.getDisplayName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.garmin.IGarminDevice#id()
   */
  public String id() {
    return info.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.garmin.IGarminDevice#softwareVersion()
   */
  public String softwareVersion() {
    return info.getSoftwareVersion();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return displayName() + " (" + info.getId() + ")";
  }
}
