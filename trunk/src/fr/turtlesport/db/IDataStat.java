package fr.turtlesport.db;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Denis Apparicio
 */
public interface IDataStat {

    void headerCsv(Writer in) throws IOException;

    void convertCsv(Writer in) throws IOException;
}
