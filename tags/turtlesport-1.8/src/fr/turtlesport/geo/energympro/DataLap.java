package fr.turtlesport.geo.energympro;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ByteUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 */
public class DataLap {
    protected static final int LENGTH = 44;

    private static TurtleLogger log;
    static {
        log = (TurtleLogger) TurtleLogger.getLogger(DataWorkout.class);
    }

    long splitTime;
    long totalTime;
    short number;
    long lDistance;
    short calorie;
    long maxSpeed;
    long avgSpeed;
    short maxHeart;
    short avgHeart;
    short minAlti;
    short maxAlti;
    short avgCad;
    short maxCad;
    short avgPower;
    short maxPower;
    short startRecPt;
    short finishRecPt;

    protected DataLap() {
    }

    public void parse(ByteBuffer buffer) throws IOException {
        log.debug(">>parse");

        buffer.position(0);

        // splitTime
        splitTime = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get());

        // TotalTime
        totalTime = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get());

        // Number
        number = ByteUtil.toShort(buffer.get(), buffer.get());

        // Reserved
        ByteUtil.toShort(buffer.get(), buffer.get());

        // lDistance
        lDistance = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get());

        // Calorie
        calorie = ByteUtil.toShort(buffer.get(), buffer.get());

        // Reserved
        ByteUtil.toShort(buffer.get(), buffer.get());

        // MaxSpeed
        maxSpeed = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get());

        // AvgSpeed
        avgSpeed = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get());

        // MaxHeart
        maxHeart = (short) (buffer.get() & 0xFF);

        // AvgHeart
        avgHeart = (short) (buffer.get() & 0xFF);

        // MinAlti
        minAlti = ByteUtil.toShort(buffer.get(), buffer.get());

        // MaxAlti
        maxAlti = ByteUtil.toShort(buffer.get(), buffer.get());

        // AvgCad
        avgCad = (short) (buffer.get() & 0xFF);

        // AvgCad
        maxCad = (short) (buffer.get() & 0xFF);

        // AvgPower
        avgPower = ByteUtil.toShort(buffer.get(), buffer.get());

        // MaxPower
        maxPower = ByteUtil.toShort(buffer.get(), buffer.get());

        // StartRecPt
        startRecPt = ByteUtil.toShort(buffer.get(), buffer.get());

        // MaxPower
        finishRecPt = ByteUtil.toShort(buffer.get(), buffer.get());

        if (log.isInfoEnabled()) {
            log.info("LAP : " + number);
            log.info("splitTime : " + splitTime);
            log.info("totalTime : " + totalTime);
            log.info("number=" + number);
            log.info("lDistance=" + lDistance);
            log.info("Calorie=" + calorie);
            log.info("MaxSpeed=" + maxSpeed);
            log.info("AvgSpeed=" + avgSpeed);
            log.info("MaxHeart=" + maxHeart);
            log.info("AvgHeart=" + avgHeart);
            log.info("MinAlti=" + minAlti);
            log.info("MaxAlti=" + maxAlti);
            log.info("AvgCad=" + avgCad);
            log.info("MaxCad=" + maxCad);
            log.info("AvgPower=" + avgPower);
            log.info("MaxPower=" + maxPower);
            log.info("StartRecPt=" + startRecPt);
            log.info("FinishRecPt=" + finishRecPt);
        }
        log.debug("<<parse");
    }
}
