package fr.turtlesport.device.energympro;

import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.device.Device;
import fr.turtlesport.device.FileDevice;
import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.energympro.CpoFile;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.OperatingSystem;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author Denis Apparicio
 *
 */
public class EnergymproDevice implements Device {
    private static TurtleLogger log      = (TurtleLogger) TurtleLogger
            .getLogger(EnergymproDevice.class);

    private EnergymproProductDevice productDevice;

    private File dir;

    private EnergymproDevice(File dir) throws IOException {
        this.productDevice = new EnergymproProductDevice(new File(dir, EnergymproProductDevice.FILENAME));
        this.dir = dir;
    }

    /**
     * Restitue la liste des Mat&eacutes;riels support&eacute;s
     *
     * @return la liste des Mat&eacutes;riels support&eacute;s.
     */
    public static List<EnergymproDevice> getDevices() {
        List<EnergymproDevice> list = new ArrayList<EnergymproDevice>();

        List<File> dirs = getDirDevices();
        if (dirs.size() == 0) {
            return list;
        }

        // recuperation des repertoires des montres
        for (File dir : dirs) {
            if (isDirAvailable(dir)) {
                addDevice(dir, list);
            }
        }
        return list;
    }

    private static void addDevice(File f, List<EnergymproDevice> list) {
        try {
            EnergymproDevice device = new EnergymproDevice(f);
            list.add(device);
        }
        catch (Throwable e) {
            log.error("", e);
        }
    }

    public List<FileDevice> getNewFiles() {
        List<FileDevice> files = getFiles();

        Iterator<FileDevice> it = files.iterator();
        while (it.hasNext()) {
            if (isAlreadyImport(it.next().getFile())) {
                it.remove();
            }
        }

        return files;
    }

    @Override
    public List<FileDevice> getFiles() {

        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".cpo");
            }
        };

        List<FileDevice> result = new ArrayList<>();
        File[] files;
        File dirWorkout = new File(dir, "Workout");
        files = dirWorkout.listFiles(filter);
        if (files != null) {
            for(File f: files) {
                result.add(new FileDevice(f, this));
            }
        }

        return result;
    }

    @Override
    public String displayName() {
        return productDevice.getProduct() + " v" + productDevice.getVersion();
    }

    @Override
    public String id() {
        return productDevice.getProduct() +" v" + productDevice.getVersion();
    }

    @Override
    public String softwareVersion() {
        return "" + productDevice.getVersion();
    }

    /**
     * Recupere les repertoires des devices.
     */
    private static List<File> getDirDevices() {
        List<File> listDir = new ArrayList<File>();

        // Mac OS X
        if (OperatingSystem.isMacOSX()) {
            File file = new File(System.getProperty("user.home"),
                    "/Library/Application Support/NO NAME");
            listDir.add(file);

            file = new File("/Volumes/NO NAME");
            listDir.add(file);

            return listDir;
        }
        // Windows
        else if (OperatingSystem.isWindows()) {
            for (File dir : File.listRoots()) {
                listDir.add(dir);
            }
        }
        // Linux
        else if (OperatingSystem.isLinux()) {
            final FileFilter filter = new FileFilter() {
               public boolean accept(File pathname) {
                 return  (pathname != null && pathname.isDirectory());
               }
            };
          
            File[] dirs = new File("/media").listFiles(filter);
            if (dirs != null) {
              for(File f: dirs) {
                listDir.add(f);                    
              }
            }
            
            String userName = System.getProperty("user.name");
            if (userName != null) {
                dirs = new File("/media/" + userName).listFiles(filter);
                if (dirs != null) {
                  for(File f: dirs) {
                    listDir.add(f);                    
                  }
                }
            }
        }

        return listDir;
    }
    
    private static boolean isDirAvailable(File pathname) {
        return (pathname != null &&
                pathname.isDirectory() &&
                new File(pathname, EnergymproProductDevice.FILENAME).isFile() &&
                new File(pathname, "Workout").exists());
    }

    private boolean isAlreadyImport(File file) {
        try {
            return isAlreadyImport(new CpoFile().retreiveDate(file));
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

    @Override
    public String toString() {
        return displayName();
    }

}
