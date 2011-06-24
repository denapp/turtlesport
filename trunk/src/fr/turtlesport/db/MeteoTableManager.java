package fr.turtlesport.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.meteo.DataMeteo;

public class MeteoTableManager extends AbstractTableManager {
  private static TurtleLogger      log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MeteoTableManager.class);
  }

  private static MeteoTableManager singleton = new MeteoTableManager();

  /**
   * 
   */
  private MeteoTableManager() {
    super();
  }

  /**
   * Restitue une instance unique.
   */
  public static MeteoTableManager getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractTableManager#getTableName()
   */
  @Override
  public String getTableName() {
    return DatabaseManager.TABLE_METEO;
  }

  /**
   * R&eacute;cup&eacute;p&eacute;ration des donn&eacute;es meteo
   * 
   * @run le run
   * @return <code>true</code> si ligne trouv&eacute;e.
   */
  public DataMeteo retreive(DataRun run) throws SQLException {
    log.debug(">>retreieve run=" + run.getId());

    DataMeteo data = null;
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, run.getId());

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        Timestamp ts = rs.getTimestamp("time");
        data = new DataMeteo(new Date(ts.getTime()));
        data.setHumidity(rs.getInt("humidity"));
        data.setImageIconIndex(rs.getInt("condition"));
        data.setTemperature(rs.getInt("temperature"));
        data.setWindSpeedkmh(rs.getFloat("wind_speed"));
        data.setWindDirection(rs.getString("wind_dir"));
        data.setPressurehPa(rs.getInt("pression"));
        data.setVisibility(rs.getFloat("visibility"));

        st.append("condition SMALLINT, ");
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreieve");
    return data;
  }

  /**
   * D&eacute;termine si des donn&eacute;es meteo exite pour ce run
   * 
   * @run le run
   * @return <code>true</code> si des donn&eacute;es meteo exite pour ce run.
   */
  public boolean exist(DataRun run) throws SQLException {
    if (run == null) {
      return false;
    }
    log.debug(">>exist run=" + run.getId());

    Connection conn = DatabaseManager.getConnection();
    boolean bRes = false;
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT id FROM ");
      st.append(getTableName());
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, run.getId());

      ResultSet rs = pstmt.executeQuery();
      bRes = rs.next();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<exist " + bRes);
    return bRes;
  }

  /**
   * Enregistre les donn&eacute;es meteo du run..
   * 
   * @param meteo
   * @param run
   *          un utilisateur.
   * @throws SQLException
   */
  public void store(DataMeteo meteo, DataRun run) throws SQLException {
    log.debug(">>store");

    if (meteo == null || run == null) {
      throw new IllegalArgumentException();
    }

    delete(run.getId());

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("INSERT INTO ");
      st.append(getTableName());
      st.append("(id,");
      st.append(" time,");
      st.append(" condition,");
      st.append(" temperature,");
      st.append(" humidity,");
      st.append(" wind_speed,");
      st.append(" wind_dir,");
      st.append(" pression,");
      st.append(" visibility)");
      st.append(" VALUES(?, ?, ?,?, ?, ?, ?, ?, ?)");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, run.getId());

      pstmt.setTimestamp(2, (meteo.getDate() == null) ? null
          : new java.sql.Timestamp(meteo.getDate().getTime()));
      pstmt.setInt(3, meteo.getImageIconIndex());
      pstmt.setInt(4, meteo.getTemperature());
      pstmt.setInt(5, meteo.getHumidity());
      pstmt.setFloat(6, meteo.getWindSpeedkmh());
      pstmt.setString(7, meteo.getWindDirection());
      pstmt.setInt(8, meteo.getPressurehPa());
      pstmt.setFloat(9, meteo.getVisibility());

      pstmt.executeUpdate();
      pstmt.close();

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<store");
  }

  /**
   * Suppression des donn&eacute;es meteo pur un run
   * 
   * @param id
   * @throws SQLException
   */
  public void delete(int id) throws SQLException {
    log.debug(">>delete id=" + id);

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("DELETE FROM ");
      st.append(getTableName());
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<delete id=" + id);
  }

  /**
   * Update de la condition
   * 
   * @param dataRun
   * @param condition
   * @throws SQLException
   */
  public void updateCondition(DataRun dataRun, int condition) throws SQLException {
    log.debug(">>update dataRun  condition");

    if (dataRun == null) {
      return;
    }

    // Debut de tansaction
    DatabaseManager.beginTransaction();

    try {
      int id = dataRun.getId();

      // update du run
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET condition=?");
      st.append(" WHERE id = ?");

      Connection conn = DatabaseManager.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, condition);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
    }
    catch (SQLException e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw e;
    }
    catch (Throwable e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw new RuntimeException(e);
    }

    // ok -> commit
    DatabaseManager.commitTransaction();
    DatabaseManager.getConnection().close();

    log.debug("<<update");
  }

  /**
   * Update de la condition
   * 
   * @param dataRun
   * @param temperature
   * @throws SQLException
   */
  public void updateTemperature(DataRun dataRun, int temperature) throws SQLException {
    log.debug(">>update dataRun temperature");

    if (dataRun == null) {
      return;
    }

    // Debut de tansaction
    DatabaseManager.beginTransaction();

    try {
      int id = dataRun.getId();

      // update du run
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET temperature=?");
      st.append(" WHERE id = ?");

      Connection conn = DatabaseManager.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, temperature);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
    }
    catch (SQLException e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw e;
    }
    catch (Throwable e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw new RuntimeException(e);
    }

    // ok -> commit
    DatabaseManager.commitTransaction();
    DatabaseManager.getConnection().close();

    log.debug("<<update");
  }

}
