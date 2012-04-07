package fr.turtlesport.garmin;

import java.io.File;
import java.io.FileFilter;
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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.OperatingSystem;

public class GarminFitDevice implements IGarminDevice {
  private static TurtleLogger log = (TurtleLogger) TurtleLogger
                                      .getLogger(GarminFitDevice.class);

  private FitInfo             info;

  private GarminFitDevice(File dir) throws SAXException,
                                   IOException,
                                   ParserConfigurationException {
    this.info = new FitInfo(dir);
  }

  /**
   * Restitue les fichiers tcx.
   * 
   * @return les fichiers tcx.
   */
  public List<File> getTcxFiles() {
    return getFiles("History", ".tcx");
  }

  /**
   * Restitue les fichiers tcx.
   * 
   * @return les fichiers tcx.
   */
  public List<File> getNewTcxFiles() {
    List<File> files = getFiles("History", ".tcx");

    Iterator<File> it = files.iterator();
    while (it.hasNext()) {
      if (isAlreadyImport(it.next())) {
        it.remove();
      }
    }

    return files;
  }

  private boolean isAlreadyImport(File file) {

    String sDate = file.getName().substring(0, file.getName().length() - 4);

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    try {
      Date date;
      synchronized (getClass()) {
        date = df.parse(sDate);
      }
      Calendar cal = Calendar.getInstance();
      cal.setTimeZone(TimeZone.getTimeZone("GMT"));

      cal.setTime(date);
      date = cal.getTime();

      return (RunTableManager.getInstance().find(DataUser.getAllUser().getId(),
                                                 date) != -1);

    }
    catch (ParseException e) {
      return true;
    }
    catch (Throwable e) {
      log.error("", e);
      return true;
    }
  }

  /**
   * Restitue les fichiers fit.
   * 
   * @return les fichiers fit.
   */
  public List<File> getFitFiles() {
    return getFiles("Activities", ".fit");
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
  public FitInfo getInfo() {
    return info;
  }

  /**
   * Restitue la liste des Mat&eacutes;riels fit support&eacute;s
   * 
   * @return la liste des Mat&eacutes;riels fit support&eacute;s.
   */
  public static List<GarminFitDevice> getDevices() {
    ArrayList<GarminFitDevice> list = new ArrayList<GarminFitDevice>();

    File dir = getDirDevices();
    if (dir == null || !dir.exists()) {
      return list;
    }

    // recuperation des repertoires des montres FIT
    File[] dirsFit = dir.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname != null && pathname.isDirectory()
               && new File(pathname, "GarminDevice.XML").isFile()
               && isFitSupported(pathname);
      }
    });

    // filtre sur les montres supportants TCX
    if (dirsFit != null) {
      for (File f : dirsFit) {
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
    }

    return list;
  }

  /**
   * Recupere les repertoires des devices.
   */
  private static File getDirDevices() {
    if (OperatingSystem.isMacOSX()) {
      File file = new File(System.getProperty("user.home"),
                           "/Library/Application Support/Garmin/Devices");
      return file;
    }

    if (OperatingSystem.isWindows()) {
      StringBuilder st = new StringBuilder();
      String tmp = System.getenv("APPDATA");
      if (tmp != null) {
        st.append(tmp);
        st.append("\\GARMIN\\Devices");
        return new File(st.toString());
      }
    }

    return null;
  }

  private static boolean isFitSupported(File dirFit) {
    return new File(dirFit, "History").isDirectory()
           || new File(dirFit, "Activities").isDirectory();
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
