package fr.turtlesport.geo.energympro;

import com.garmin.fit.Decode;
import fr.turtlesport.device.IProductDevice;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.geo.*;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.Wgs84;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 */
public class CpoFile implements IGeoFile, IGeoConvertRun {
    private static TurtleLogger log;

    static {
        log = (TurtleLogger) TurtleLogger.getLogger(CpoFile.class);
    }

    /**
     * Extensions.
     */
    public static final String[] EXT = {"cpo"};

    /**
     * Restitue la date du fichier du run.
     *
     * @param file
     * @return
     * @throws GeoLoadException
     * @throws FileNotFoundException
     */
    public Date retreiveDate(File file) throws GeoLoadException,
            FileNotFoundException {
        // Lecture
        try {
            SeekableByteChannel sbc = Files.newByteChannel(Paths.get(file.toURI()));

            sbc.position(sbc.size() - DataWorkout.LENGTH);

            // Workout
            ByteBuffer buffer = ByteBuffer.allocate(DataWorkout.LENGTH);
            sbc.read(buffer);
            DataWorkout workout = new DataWorkout();
            workout.parse(buffer);

            return workout.startTime;
        } catch (IOException e) {
            throw new GeoLoadException(e);
        }
    }
    @Override
    public File convert(List<DataRun> runs, IGeoConvertProgress progress, File file) throws GeoConvertException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File convert(DataRun data, File file) throws GeoConvertException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File convert(DataRun data) throws GeoConvertException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IGeoRoute[] load(File file) throws GeoLoadException, FileNotFoundException {
        log.debug(">>load " + file.getName());

        IGeoRoute[] tabRoute = null;
        try {
            SeekableByteChannel sbc = Files.newByteChannel(Paths.get(file.toURI()));

            log.debug("size=" + sbc.size());
            sbc.position(sbc.size() - DataWorkout.LENGTH);

            // Workout
            ByteBuffer buffer = ByteBuffer.allocate(DataWorkout.LENGTH);
            sbc.read(buffer);
            DataWorkout workout = new DataWorkout();
            workout.parse(buffer);

            // Lap
            long pos = DataPoint.LENGTH * workout.totalRecordPoint;
            List<DataLap> laps = new ArrayList<DataLap>();
            for (int lapIndex= 0; lapIndex < workout.lapNumber; lapIndex++) {
                sbc.position(pos);
                buffer = ByteBuffer.allocate(DataLap.LENGTH);
                sbc.read(buffer);
                DataLap dataLap = new DataLap();
                dataLap.parse(buffer);
                laps.add(dataLap);
                pos += DataLap.LENGTH;
            }

            // Point
            pos = 0;
            List<DataPoint> points = new ArrayList<DataPoint>();
            for (int pointIndex= 0; pointIndex < workout.totalRecordPoint; pointIndex++) {
                sbc.position(pos);
                buffer = ByteBuffer.allocate(DataLap.LENGTH);
                sbc.read(buffer);
                DataPoint dataPoint = new DataPoint();
                dataPoint.parse(buffer);
                points.add(dataPoint);

                pos += DataPoint.LENGTH;
            }

            tabRoute = new IGeoRoute[1];
            tabRoute[0] = new CpoGeoRoute(workout, laps, points);
            if (log.isInfoEnabled()) {
                log.info("cpo file : " + tabRoute[0].getStartTime());
                for(IGeoSegment seg : tabRoute[0].getSegments()) {
                    log.info("  lap : " + seg.getStartTime());
                    log.info("     distance : " + seg.distance());
                }
            }

        } catch (IOException e) {
            throw new GeoLoadException(e);
        }

        log.debug("<<load " + file.getName());

        return tabRoute;
    }

    @Override
    public String[] extension() {
        return EXT;
    }

    @Override
    public String description() {
        return "Energympro (*.cpo)";
    }

    /**
     * @author Denis Apparicio
     *
     */
    private class CpoGeoRoute extends AbstractGeoRoute {
        private DataWorkout workout;

        private List<IGeoSegment> listGeoSegment;

        private CPOProductDevice device;

        public CpoGeoRoute(DataWorkout workout, List<DataLap> listLap, List<DataPoint> listPoint) {
            super();
            this.workout = workout;
            this.device = new CPOProductDevice(workout.version, workout.verNum);
            initSportType();

            TreeMap<Short, DataLap> map = new TreeMap<Short, DataLap>();
            for (DataLap lap : listLap) {
                map.put(lap.number, lap);
            }

            List<DataLap> orderLap = new ArrayList<DataLap>();
            Iterator<DataLap> it = map.values().iterator();
            while(it.hasNext()) {
                orderLap.add(it.next());
            }

            long currentTime = workout.startTime.getTime();
            double computeDistV = 0;
            double currentDist = 0;
            List<CpoPoint> listCpoPoint = new ArrayList<CpoPoint>();
            DataPoint prevPoint = null;

            DecimalFormat df = new DecimalFormat("#.##");
            for (DataPoint point: listPoint) {
                currentTime += point.intervalTime*100;
                CpoPoint cpo = new CpoPoint(point, new Date(currentTime));
                listCpoPoint.add(cpo);
                if (prevPoint != null && point.status == 0) {
                    computeDistV += Wgs84.vincentyDistance(prevPoint.latitude,
                            prevPoint.longitude,
                            point.latitude,
                            point.longitude);
                    currentDist += point.intervalDist /100.0;
                    cpo.distanceMetersCompute = computeDistV;
                    cpo.setDistanceMeters(computeDistV);
                    prevPoint = point;
                }

                if (log.isDebugEnabled()) {
                    String libStatus = (DataPoint.Status.OK.ordinal() == point.status)?"status : ":"!STATUS ";
                    log.debug("status=" + point.status +
                            " intDist=" + point.intervalDist +
                            " currentDist=" + df.format(currentDist) +
                            " computeDistV=" + df.format(computeDistV) +
                            " lon=" + point.longitude +
                            " lat=" + point.latitude
                            /* +
                            " alt=" + point.altitude +
                            " intervalTime =" + point.intervalTime +
                            " status,heart=" + point.hearteRateStatus + "," + point.heartRate
                            */
                            );
                }
                if (point.isOk()) {
                    prevPoint = point;
                }
            }

            listGeoSegment = new ArrayList<IGeoSegment>();
            for (DataLap lap : orderLap) {
                LapGeoSegment seg = new LapGeoSegment(lap);
                listGeoSegment.add(seg);
                for (int i = lap.startRecPt; i <= lap.finishRecPt; i++ ) {
                    seg.addPoint(listCpoPoint.get(i));
                }
                seg.setStartTime(listCpoPoint.get(lap.startRecPt).getDate());
                log.debug("LAP");
                log.debug("distance : " +seg.distance());
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.IGeoRoute#getProductDevice()
         */
        @Override
        public IProductDevice getProductDevice() {
            return device;
        }

        public void initSportType() {
            setSportType(SPORT_TYPE_OTHER);
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.AbstractGeoRoute#getStartTime()
         */
        @Override
        public Date getStartTime() {
            return workout.startTime;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.AbstractGeoRoute#distanceTot()
         */
        @Override
        public double distanceTot() {
            /*return workout.totalDist; */
            double distance = 0;
            for (IGeoSegment seg : getSegments()) {
                distance += seg.distance();
            }
            return distance;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.AbstractGeoRoute#totalTime()
         */
        @Override
        public long totalTime() {
            return (long) (workout.totalTime * 100);
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.IGeoRoute#getName()
         */
        public String getName() {
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.IGeoRoute#getAllPoints()
         */
        public List<IGeoPositionWithAlt> getAllPoints() {
            if (getSegmentSize() == 1) {
                return getSegment(0).getPoints();
            }

            ArrayList<IGeoPositionWithAlt> list = new ArrayList<IGeoPositionWithAlt>();
            for (int i = 0; i < getSegmentSize(); i++) {
                for (IGeoPositionWithAlt p : getSegment(i).getPoints()) {
                    list.add(p);
                }
            }
            return list;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.IGeoRoute#getSegmentSize()
         */
        public int getSegmentSize() {
            return listGeoSegment.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.IGeoRoute#getSegment(int)
         */
        public IGeoSegment getSegment(int index) {
            return listGeoSegment.get(index);
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.turtlesport.geo.IGeoRoute#getSegments()
         */
        public List<IGeoSegment> getSegments() {
            return listGeoSegment;
        }
    }

    private class CPOProductDevice implements IProductDevice {
        String displayName;
        String version;

        public CPOProductDevice(String product, short sVersion) {
            StringBuilder st = new StringBuilder();
            st.append("Energympro");
            if (product == null) {
                st.append(' ');
                st.append(product);
            }
            if (sVersion != 0) {
                this.version = Short.toString(sVersion);
                st.append( "(version ");
                st.append(this.version);
                st.append(')');
            }
            this.displayName = st.toString();
        }

        public String displayName() {
            return displayName;
        }

        public String id() {
            return null;
        }

        public String softwareVersion() {
            return version;
        }
    }

    /**
     * @author Denis Apparicio
     *
     */
    private class LapGeoSegment implements IGeoSegment {
        private DataLap data;
        private Date                      startTime;
        private double computeDistance = -1L;
        private List<IGeoPositionWithAlt> listPoint = new ArrayList<IGeoPositionWithAlt>();

        private LapGeoSegment(DataLap data) {
            this.data = data;
        }

        @Override
        public int index() {
            return data.number;
        }

        @Override
        public Date getStartTime() {
            return startTime;
        }

        @Override
        public void setStartTime(Date startTime) {
            log.info("lap setStartTime " +startTime);
            this.startTime = startTime;
        }

        @Override
        public long getTotalTime() {
            return data.totalTime * 100;
        }

        @Override
        public long getTotalPauseTime() {
            return 0;
        }

        @Override
        public double distance() {
            //return data.lDistance;
            if (computeDistance == -1L) {
                synchronized (this) {
                    if (computeDistance == -1L) {
                        IGeoPositionWithAlt pointStart = null;
                        IGeoPositionWithAlt pointStop = null;
                        for (IGeoPositionWithAlt p : listPoint) {
                            if (!p.isInvalidPosition()) {
                                if (pointStart == null) {
                                    pointStart = p;
                                } else {
                                    pointStop = p;
                                }
                            }
                        }
                        if (pointStart != null && pointStop != null) {
                            computeDistance = pointStop.getDistanceMeters() - pointStart.getDistanceMeters();
                        } else {
                            computeDistance = 0;
                        }
                    }
                }
            }
            return computeDistance;
        }

        @Override
        public int getCalories() {
            return data.calorie;
        }

        @Override
        public double getMaxSpeed() {
            return data.maxSpeed/100.0;
        }

        @Override
        public int getAvgHeartRate() {
            return data.avgHeart;
        }

        @Override
        public int getMaxHeartRate() {
            return data.maxHeart;
        }

        @Override
        public List<IGeoPositionWithAlt> getPoints() {
            return listPoint;
        }

        protected void addPoint(CpoPoint point) {
            listPoint.add(point);
        }
    }

    /**
     * @author Denis Apparicio
     *
     */
    private class CpoPoint extends GeoPositionWithAlt {
        private DataPoint data;
        private double distanceMetersCompute;

        private CpoPoint(DataPoint data, Date date) {
            this.data = data;
            if (data.isOk()) {
                setLatitude(data.latitude);
                setLongitude(data.longitude);
                setElevation(data.altitude);
                setDistanceMeters(0);
            }
            if (data.speedStatus == 0) {
                setSpeed((float) (data.speed / 100.0));
            }
            setDate(date);
            if (data.hearteRateStatus ==  DataPoint.Status.OK.ordinal()
                    && data.heartRate > 30) {
                setHeartRate(data.heartRate);
            }
            setCadence(data.cadence);
        }

    }

}
