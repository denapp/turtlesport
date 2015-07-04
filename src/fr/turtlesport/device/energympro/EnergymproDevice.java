package fr.turtlesport.device.energympro;

import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.device.Device;
import fr.turtlesport.device.garmin.GarminDeviceInfo;
import fr.turtlesport.geo.GeoLoadException;
import fr.turtlesport.geo.energympro.CpoFile;
import fr.turtlesport.geo.garmin.fit.FitFile;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.OperatingSystem;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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

    private EnergymproDeviceInfo info;

    private File dir;

    private EnergymproDevice(File dir) throws IOException {
        this.info = new EnergymproDeviceInfo(new File(dir, EnergymproDeviceInfo.FILENAME));
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

    public List<File> getNewFiles() {
        List<File> files = getFiles();

        Iterator<File> it = files.iterator();
        while (it.hasNext()) {
            if (isAlreadyImport(it.next())) {
                it.remove();
            }
        }

        return files;
    }

    @Override
    public List<File> getFiles() {

        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".cpo");
            }
        };

        List<File> result = new ArrayList<File>();
        File[] files;
        File dirWorkout = new File(dir, "Workout");
        files = dirWorkout.listFiles(filter);
        if (files != null) {
            for(File f: files) {
                result.add(f);
            }
        }

        return result;
    }

    @Override
    public String displayName() {
        return info.getProduct() + " v" + info.getVersion();
    }

    @Override
    public String id() {
        return info.getProduct() +" v" + info.getVersion();
    }

    @Override
    public String softwareVersion() {
        return "" + info.getVersion();
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

    private static boolean isDirAvailable(File pathname) {
        return (pathname != null &&
                pathname.isDirectory() &&
                new File(pathname, EnergymproDeviceInfo.FILENAME).isFile() &&
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
