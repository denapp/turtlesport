package fr.turtlesport.db;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.IDataStat;
import fr.turtlesport.lang.CommonLang;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.DateUtil;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

/**
 * Created by denisapparicio on 20/04/2016.
 */
public class DataStatRun implements IDataStat {
    private DataRun data;

    public DataStatRun(DataRun data) {
        this.data = data;
    }

    @Override
    public void headerCsv(Writer in) throws IOException {
        // Ecriture en tete;
        in.write("date");
        delimiter(in);
        in.write(CommonLang.INSTANCE.distanceWithUnit());
        delimiter(in);
        in.write(CommonLang.INSTANCE.getString("time"));
        delimiter(in);
        in.write(CommonLang.INSTANCE.getString("Activity"));
    }

    @Override
    public void convertCsv(Writer in) throws IOException {
        try {
            in.write(LanguageManager.getManager().getCurrentLang().
                    getDateTimeShortFormatter().format(data.getTime()));
            delimiter(in);
            in.write(DistanceUnit.formatDefaultUnit(data.getComputeDistanceTot()));
            delimiter(in);
            in.write(TimeUnit.formatHundredSecondeTime(data.computeTimeTot()));
            delimiter(in);
            in.write(data.getLibelleSportType());
        } catch (SQLException e) {
            throw new IOException(e);
        }

    }

    public void delimiter(Writer in) throws IOException {
        in.write(';');
    }
}
