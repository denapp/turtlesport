package fr.turtlesport.geo.energympro;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ByteUtil;
import fr.turtlesport.util.DateUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by denisapparicio on 14/04/2015.
 */
public class DataWorkout {
    protected static final int LENGTH = 80;

    // typedef struct Workout
    // {
    // DATE dateStart; // start date
    // TIME timeStart; // start time
    // WORD TotalRecPt; // Total record Point
    // long TotalTime; // Total Time
    // long TotalDist; // Total Distance
    // WORD LapNumber; // Lap Number
    // WORD Calory; // Calory
    // long MaxSpeed; // Max Speed
    // long AvgSpeed; // average Speed
    // BYTE MaxHeart; //Max Heartrate
    // BYTE AvgHeart; // average Heart
    // WORD Ascent; // Ascent
    // WORD Descent; // Descent
    // SINT MinAlti; // Min Altitude
    // SINT MaxAlti; // Max Altitude
    // BYTE AvgCad; BYTE BestCad; // Best Cadence
    // WORD AvgPower; // average Power
    // WORD MaxPower; // Max Power
    // VERSION
    // char
    // // average Cadence
    // Version;
    // // Version information
    // Back[18];
    // // Reserved
    // }

    private static TurtleLogger log;

    static {
        log = (TurtleLogger) TurtleLogger.getLogger(DataWorkout.class);
    }

    Date startTime;
    short totalRecordPoint;
    int totalTime;
    int totalDist;
    short lapNumber;
    int avgSpeed;
    short calorie;
    int maxSpeed;
    short maxHeart;
    short avgHeart;
    short ascent;
    short descent;
    short minAlti;
    short maxAlti;
    short avgCad;
    short maxCad;
    short avgPower;
    short maxPower;
    String version;
    short verNum;

    protected DataWorkout() {
    }

    public void parse(ByteBuffer buffer) throws IOException {
        log.debug(">>parse");

        // Date
        buffer.position(0);
        byte year = buffer.get();
        byte month = buffer.get();
        byte day = buffer.get();


        // Time
        byte hour = buffer.get();
        byte mn = buffer.get();
        byte seconds = buffer.get();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2000 + year, month - 1, day, hour, mn, seconds);
        startTime = calendar.getTime();

        // Total record Point
        totalRecordPoint = ByteUtil.toShort(buffer.get(), buffer.get());

        // TotalTime
        totalTime = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get());

        // TotalDist
        totalDist = ByteUtil.toInt(buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get());

        // LapNumber
        lapNumber = ByteUtil.toShort(buffer.get(), buffer.get());

        // Calorie
        calorie = ByteUtil.toShort(buffer.get(), buffer.get());

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

        // Ascent
        ascent = ByteUtil.toShort(buffer.get(), buffer.get());

        // Descent
        descent = ByteUtil.toShort(buffer.get(), buffer.get());

        // MinAlti
        minAlti = ByteUtil.toShort(buffer.get(), buffer.get());

        // MaxAlti
        maxAlti = ByteUtil.toShort(buffer.get(), buffer.get());

        // AvgCad
        avgCad = (short) (buffer.get() & 0xFF);

        // MaxCad
        maxCad = (short) (buffer.get() & 0xFF);

        // AvgPower
        avgPower = ByteUtil.toShort(buffer.get(), buffer.get());

        // MaxPower
        maxPower = ByteUtil.toShort(buffer.get(), buffer.get());

        // Version
        byte[] buf = new byte[15];
        buffer.get(buf);
        int i = 0;
        for (i = 0; i < buf.length && buf[i] != 0; i++);
        version = new String(buf,0, i);


        // Reserved
        buffer.get();

        // Version.VerNum
        verNum = ByteUtil.toShort(buffer.get(), buffer.get());

        if (log.isInfoEnabled()) {
            log.info("Date : 20" + year + "-" + month + "-" + day);
            log.info("Time : " + hour + ":" + mn + ":" + seconds);
            log.info("Date : " + startTime);
            log.info("Total record Point : " + totalRecordPoint);
            log.info("TotalTime : " + totalTime);
            log.info("TotalDist=" + totalDist);
            log.info("LapNumber=" + lapNumber);
            log.info("Calorie=" + calorie);
            log.info("MaxSpeed=" + maxSpeed);
            log.info("AvgSpeed=" + avgSpeed);
            log.info("MaxHeart=" + maxHeart);
            log.info("AvgHeart=" + avgHeart);
            log.info("Ascent=" + ascent);
            log.info("Descent=" + descent);
            log.info("MinAlti=" + minAlti);
            log.info("MaxAlti=" + maxAlti);
            log.info("AvgCad=" + avgCad);
            log.info("MaxCad=" + maxCad);
            log.info("avgPower=" + avgPower);
            log.info("maxPower=" + maxPower);
            log.info("version=" + version);
            log.info("verNum=" + verNum);
        }

        log.debug("<<parse");
    }

}
