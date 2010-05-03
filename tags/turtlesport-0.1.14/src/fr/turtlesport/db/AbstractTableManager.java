package fr.turtlesport.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractTableManager {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(RunLapTableManager.class);
  }

  /**
   * Restitue le nom de la table.
   * 
   * @return le nom de la table.
   */
  public abstract String getTableName();

  /**
   * Conversion d'un boolean en smallInt.
   * 
   * @param b
   *          le boolean.
   * @return
   */
  protected static int convertToSmallInt(boolean b) {
    return b ? 0 : 1;
  }

  /**
   * Conversion d'un smallInt en booleen.
   * 
   * @param smallInt
   *          le boolean.
   * @return
   */
  protected static boolean convertToBoolean(int val) {
    return (val == 0) ? true : false;
  }

  /**
   * Restitue le nombre de ligne de la table.
   * 
   * @return
   * @throws Exception
   */
  public int count() throws SQLException {
    log.debug(">>count");

    int count;
    Connection conn = DatabaseManager.getConnection();

    try {
      PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM "
                                                      + getTableName());

      ResultSet rs = pstmt.executeQuery();
      rs.next();
      count = rs.getInt(1);
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<count " + count);
    return count;
  }

  /**
   * Log de la table.
   * 
   * @throws Exception
   */
  public void logTable() throws SQLException {
    if (!log.isInfoEnabled()) {
      return;
    }

    log.info(">>logTable");

    StringBuilder st;
    Connection conn = DatabaseManager.getConnection();
    try {
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM "
                                                      + getTableName());
      ResultSet rs = pstmt.executeQuery();

      // Colonnes
      log.info("Table " + getTableName());
      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();
      st = new StringBuilder();
      for (int i = 1; i <= numberOfColumns; i++) {
        st.append(rsmd.getColumnName(i));
        st.append("|");
      }
      log.info(st.toString());

      // Lignes
      while (rs.next()) {
        st = new StringBuilder();
        for (int i = 1; i <= numberOfColumns; i++) {
          st.append(rs.getObject(i));
          st.append("|");
        }
        log.info(st.toString());
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<logTable");
  }

  /**
   * Log de la table.
   * 
   * @throws Exception
   */
  public void logTable(Connection conn, PreparedStatement pstmt) throws SQLException {
    if (!log.isInfoEnabled()) {
      return;
    }

    log.info(">>logTable pstmt");

    StringBuilder st;
    try {
      ResultSet rs = pstmt.executeQuery();

      // Colonnes
      log.info("Table " + getTableName());
      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();
      st = new StringBuilder();
      for (int i = 1; i <= numberOfColumns; i++) {
        st.append(rsmd.getColumnName(i));
        st.append("|");
      }
      log.info(st.toString());

      // Lignes
      while (rs.next()) {
        st = new StringBuilder();
        for (int i = 1; i <= numberOfColumns; i++) {
          st.append(rs.getObject(i));
          st.append("|");
        }
        log.info(st.toString());
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<logTable");
  }
}
