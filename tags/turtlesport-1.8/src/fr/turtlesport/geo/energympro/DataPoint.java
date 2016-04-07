package fr.turtlesport.geo.energympro;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ByteUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 */
public class DataPoint {
    protected static final int LENGTH = 48;

    private static TurtleLogger log;

    static {
        log = (TurtleLogger) TurtleLogger.getLogger(DataWorkout.class);
    }

    public enum Status { OK, MISS, NOGOOD, BAD }

    double latitude;
    double longitude;
    short altitude;
    double speed;
    short intervalDist;
    long intervalTime;
    short status;
    short heartRate;
    byte hearteRateStatus;
    double speedSpeed;
    byte speedStatus;
    short cadence;
    byte cadenceStatus;
    short powerCadence;
    short power;
    byte powerStatus;

    protected DataPoint() {
    }

    public boolean isOk() {
        return status == 0;
    }

    public void parse(ByteBuffer buffer) throws IOException {
        buffer.position(0);

        // latitude
        latitude = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get()) / 1000000.0;

        // longitude
        longitude = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get()) / 1000000.0;

        // altitude
        altitude = ByteUtil.toShort(buffer.get(), buffer.get());

        // Reserved
        ByteUtil.toShort(buffer.get(), buffer.get());

        // speed
        speed = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get()) / 100.0;

        // intervalDist
        intervalDist = ByteUtil.toShort(buffer.get(), buffer.get());

        // Reserved
        ByteUtil.toShort(buffer.get(), buffer.get());

        // lntervalTime
        intervalTime = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get()) / 10;

        // Status
        status = buffer.get();

        // HR
        heartRate = (short) (buffer.get() & 0xFF);
        hearteRateStatus = buffer.get();

        // Speed
        speedSpeed = (buffer.get() & 0xFF) / 100.0;
        speedStatus = buffer.get();

        // Reserved
        buffer.get();

        // Cadence
        cadence = (short) (buffer.get() & 0xFF);
        cadenceStatus = buffer.get();

        // Power
        powerCadence = ByteUtil.toShort(buffer.get(), buffer.get());
        power = ByteUtil.toShort(buffer.get(), buffer.get());
        powerStatus = buffer.get();

        if (log.isDebugEnabled()) {
            log.debug("status : " + status +
             " intervalDist=" + intervalDist +
             " lat : " + latitude +
             " long : " + longitude +
             " speed=" + speed +
             " alt=" + altitude +
             " lntervalTime : " + intervalTime
             /*
             " status=" + status +
             " heartRate=" + heartRate +
             " hearteRateStatus=" + hearteRateStatus +
             " speed=" + speed +
             " speedStatus=" + speedStatus +
             " cadence=" + cadence +
             " cadenceStatus=" + cadenceStatus +
             " powerCadence=" + powerCadence +
             " power=" + power +
             " powerStatus=" + powerStatus
            */
            );
        }
    }

}
