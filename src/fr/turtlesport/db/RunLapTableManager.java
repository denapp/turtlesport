package fr.turtlesport.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.turtlesport.geo.IGeoSegment;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.AbstractLapType;

/**
 * @author Denis Apparicio
 * 
 */
public final class RunLapTableManager extends AbstractTableManager {
  private static TurtleLogger       log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(RunLapTableManager.class);
  }

  private static RunLapTableManager singleton = new RunLapTableManager();

  /**
   * 
   */
  private RunLapTableManager() {
    super();
  }

  /**
   * Restitue une instance unique.
   */
  public static RunLapTableManager getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractTableManager#getTableName()
   */
  @Override
  public String getTableName() {
    return DatabaseManager.TABLE_RUN_LAP;
  }

  /**
   * D&eacute;termine si ce run &agrave; des tours intermediaires.
   * 
   * @param idRun
   * @return <code>true</code> si ce run &agrave; des tours intermediaires,
   *         <code>false</code> sinon.
   * @throws SQLException
   */
  public boolean hasLap(int idRun) throws SQLException {

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);

      ResultSet rs = pstmt.executeQuery();

      return rs.next();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }
  }

  /**
   * Restitue les tours intermediaires du run.
   * 
   * @param idRun
   * @return
   * @throws SQLException
   */
  public DataRunLap[] findLaps(int idRun) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>findLaps idRun=" + idRun);
    }

    DataRunLap[] res;

    long startTime = System.currentTimeMillis();
    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE id=? ORDER BY lap_index ASC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);

      ArrayList<DataRunLap> list = new ArrayList<DataRunLap>();
      DataRunLap drl;
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        drl = new DataRunLap();
        drl.setId(rs.getInt("id"));
        drl.setLapIndex(rs.getInt("lap_index"));
        drl.setStartTime(rs.getTimestamp("start_time"));
        drl.setTotalTime(rs.getInt("total_time"));
        drl.setRealTotalTime(rs.getInt("total_time"));
        drl.setMovingTotalTime(rs.getInt("total_moving_time"));
        drl.setTotalDist(rs.getInt("total_dist"));
        drl.setMaxSpeed(rs.getFloat("max_speed"));
        drl.setCalories(rs.getInt("calories"));
        drl.setAvgHeartRate(rs.getInt("avg_heart_rate"));
        drl.setMaxHeartRate(rs.getInt("max_heart_rate"));

        list.add(drl);
      }

      res = new DataRunLap[list.size()];
      if (res.length > 0) {
        list.toArray(res);
        for (int i = 0; i < res.length - 1; i++) {
          res[i].setTotalLap(res.length);
          long time = res[i + 1].getStartTime().getTime()
                      - res[i].getStartTime().getTime();
          res[i].setRealTotalTime((int) time / 10);
        }

        DataRunTrk trk = RunTrkTableManager.getInstance().getLastTrk(idRun);
        if (trk != null && trk.getTime() != null) {
          long time = trk.getTime().getTime()
                      - res[list.size() - 1].getStartTime().getTime();
          res[list.size() - 1].setRealTotalTime((int) time / 10);
        }

      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isInfoEnabled()) {
      long delay = System.currentTimeMillis() - startTime;
      log.info("<<findLaps delay=" + delay + "ms");
    }
    return res;
  }

  /**
   * Insertion d'un tour interm&eacute;diaire.
   * 
   * @param id
   * @param lap
   * @throws SQLException
   */
  public void store(int id, AbstractLapType lap) throws SQLException {
    log.debug(">>store id=" + id);

    if (lap == null) {
      throw new IllegalArgumentException("lapType est null");
    }

    store(id,
          lap.getIndex(),
          lap.getStartTime(),
          lap.getTotalTime(),
          0,
          lap.getTotalDist(),
          lap.getMaxSpeed(),
          lap.getCalories(),
          lap.getAvgHeartRate(),
          lap.getMaxHeartRate());

    log.debug("<<store");
  }

  /**
   * Insertion d'un tour interm&eacute;diaire.
   * 
   * @param id
   * @param lap
   * @throws SQLException
   */
  protected void store(int id, IGeoSegment lap) throws SQLException {
    log.debug(">>store id=" + id);

    if (lap == null) {
      throw new IllegalArgumentException("lapType est null");
    }

    if (log.isDebugEnabled()) {
      log.debug("TotalTime=" + lap.getTotalTime());
      log.debug("TotalPauseTime=" + lap.getTotalPauseTime());
    }

    store(id,
          lap.index(),
          lap.getStartTime(),
          (int) ((lap.getTotalTime() + lap.getTotalPauseTime()) / 10),
          (int) (lap.getTotalTime() / 10),
          (float) lap.distance(),
          (float) lap.getMaxSpeed(),
          lap.getCalories(),
          lap.getAvgHeartRate(),
          lap.getMaxHeartRate());

    log.debug("<<store");
  }

  /**
   * Suppression des tours interm&eacute;mediaire.
   * 
   * @param id
   * @throws SQLException
   */
  protected void delete(int id) throws SQLException {
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
   * Restitue les tours intermediaires du run.
   * 
   * @param idRun
   * @return
   * @throws SQLException
   */
  public boolean findLap(int idRun, int lapIndex, Date startTime) throws SQLException {
    log.debug(">>findLap idRun=" + idRun);

    boolean bRes;

    Connection conn = DatabaseManager.getConnection();

    try {
      if (log.isDebugEnabled()) {
        logTable();
      }

      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE id=? AND (lap_index=? OR start_time=?)");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);
      pstmt.setInt(2, lapIndex);
      pstmt.setTimestamp(3, new Timestamp(startTime.getTime()));

      ResultSet rs = pstmt.executeQuery();
      bRes = rs.next();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<findLap bRes=" + bRes);
    return bRes;
  }

  /**
   * Insertion d'un run interm&eacute;diaire.
   */
  protected int store(int id,
                      int lapIndex,
                      Date startTime,
                      int totalTime,
                      int movingTime,
                      float totalDist,
                      float maxSpeed,
                      int calories,
                      int avgHeartRate,
                      int maxHeartRate) throws SQLException {
    log.debug(">>store");

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      if (!RunTableManager.getInstance().exist(id)) {
        throw new SQLException("id non trouve.");
      }

      if (calories < 0) {
        calories = 0;
      }

      StringBuilder st = new StringBuilder();
      st.append("INSERT INTO ");
      st.append(getTableName());
      st.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      pstmt.setInt(1, id);
      pstmt.setInt(2, lapIndex);
      pstmt.setTimestamp(3, new Timestamp(startTime.getTime()));
      pstmt.setInt(4, totalTime);
      pstmt.setFloat(5, totalDist);
      pstmt.setFloat(6, maxSpeed);
      pstmt.setInt(7, avgHeartRate);
      pstmt.setInt(8, maxHeartRate);
      pstmt.setInt(9, calories);
      pstmt.setInt(10, movingTime);
      pstmt.executeUpdate();
    }
    catch (SQLException e) {
      // rollback
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw e;
    }

    if (!isInTransaction) {
      DatabaseManager.commitTransaction();
    }
    DatabaseManager.releaseConnection(conn);

    log.debug("<<store id=" + id);
    return id;
  }

  /**
   * Restitue les calories d'un run.
   * 
   * @param idRun
   * @return
   * @throws SQLException
   */
  public int computeCalories(int idRun) throws SQLException {
    log.debug(">>computeCalories idRun=" + idRun);

    int calories = 0;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append(" SELECT SUM(int(calories)) FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        calories = rs.getInt(1);
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<computeCalories");
    return calories;
  }

  /**
   * Restitue la frequence cardiaque max d'un run.
   * 
   * @return
   * @throws SQLException
   */
  public int heartMax(int idRun) throws SQLException {
    log.debug(">>heartMax idRun=" + idRun);

    int res = 0;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append(" SELECT MAX(max_heart_rate) FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        res = rs.getInt(1);
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<heartMax");
    return res;
  }

  /**
   * Restitue la frequence cardiaque moyenne d'un run.
   * 
   * @return
   * @throws SQLException
   */
  public DataRunTrk lapTrkBegin(int idRun, int lapIndex) throws SQLException {
    log.info(">>lapTrkBegin idRun=" + idRun + " lapIndex=" + lapIndex);

    DataRunTrk trk = null;

    Connection conn = DatabaseManager.getConnection();
    try {

      StringBuilder st = new StringBuilder();
      st.append("SELECT start_time, total_time FROM ");
      st.append(getTableName());
      st.append(" WHERE id=? AND lap_index=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);
      pstmt.setInt(2, lapIndex);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        Timestamp timestampDeb = rs.getTimestamp(1);
        int totalTime = rs.getInt(2);
        rs.close();

        if (log.isInfoEnabled()) {
          log.info("timestamp=" + timestampDeb);
          log.info("totalTime=" + totalTime);
        }
        Timestamp timestampEnd = new Timestamp(timestampDeb.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestampDeb);
        cal.add(Calendar.MILLISECOND, totalTime * 10);
        timestampEnd.setTime(cal.getTimeInMillis());

        st = new StringBuilder();
        st.append("SELECT * FROM ");
        st.append(RunTrkTableManager.getInstance().getTableName());
        st.append(" WHERE id=?");
        st.append(" AND distance <> ?");
        st.append(" AND (latitude <> ? AND longitude <> ?) ");
        st.append(" AND (time BETWEEN ? AND ?)");
        st.append(" ORDER BY time ASC");

        pstmt = conn.prepareStatement(st.toString());
        pstmt.setInt(1, idRun);
        pstmt.setFloat(2, 1.0e25f);
        pstmt.setInt(3, 0x7FFFFFFF);
        pstmt.setInt(4, 0x7FFFFFFF);
        pstmt.setTimestamp(5, timestampDeb);
        pstmt.setTimestamp(6, timestampEnd);

        rs = pstmt.executeQuery();
        if (rs.next()) {
          trk = new DataRunTrk();

          trk.setId(rs.getInt(1));
          trk.setLatitude(rs.getInt(2));
          trk.setLongitude(rs.getInt(3));
          trk.setTime(rs.getTimestamp(4));
          trk.setAltitude(rs.getFloat(5));
          trk.setDistance(rs.getFloat(6));
          trk.setHeartRate(rs.getInt(7));
        }
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<lapTrkBegin");
    return trk;
  }

  /**
   * Restitue la frequence cardiaque moyenne d'un run.
   * 
   * @return
   * @throws SQLException
   */
  public DataRunTrk lapTrkEnd(int idRun, int lapIndex) throws SQLException {
    log.info(">>lapTrkEnd idRun=" + idRun + " lapIndex=" + lapIndex);

    DataRunTrk trk = null;

    Connection conn = DatabaseManager.getConnection();
    try {

      StringBuilder st = new StringBuilder();
      st.append("SELECT start_time, total_time FROM ");
      st.append(getTableName());
      st.append(" WHERE id=? AND lap_index=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);
      pstmt.setInt(2, lapIndex);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        Timestamp timestamp = rs.getTimestamp(1);
        int totalTime = rs.getInt(2);
        rs.close();

        if (log.isInfoEnabled()) {
          log.info("timestamp=" + timestamp);
          log.info("totalTime=" + totalTime);
        }

        if (totalTime > 0) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(timestamp);
          cal.add(Calendar.MILLISECOND, totalTime * 10);
          timestamp.setTime(cal.getTimeInMillis());

          st = new StringBuilder();
          st.append("SELECT * FROM ");
          st.append(RunTrkTableManager.getInstance().getTableName());
          st.append(" WHERE id=?");
          st.append(" AND distance <> ?");
          st.append(" AND (latitude <> ? AND longitude <> ?) ");
          st.append(" AND time <= ?");
          st.append(" ORDER BY time DESC");

          pstmt = conn.prepareStatement(st.toString());
          pstmt.setInt(1, idRun);
          pstmt.setFloat(2, 1.0e25f);
          pstmt.setInt(3, 0x7FFFFFFF);
          pstmt.setInt(4, 0x7FFFFFFF);
          pstmt.setTimestamp(5, timestamp);

          rs = pstmt.executeQuery();
          if (rs.next()) {
            trk = new DataRunTrk();

            trk.setId(rs.getInt(1));
            trk.setLatitude(rs.getInt(2));
            trk.setLongitude(rs.getInt(3));
            trk.setTime(rs.getTimestamp(4));
            trk.setAltitude(rs.getFloat(5));
            trk.setDistance(rs.getFloat(6));
            trk.setHeartRate(rs.getInt(7));
          }
        }
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<lapTrkEnd");
    return trk;
  }

  /**
   * Restitue la frequence cardiaque moyenne d'un run.
   * 
   * @param idRun
   * @return
   * @throws SQLException
   */
  public int heartAvg(int idRun) throws SQLException {
    log.info(">>heartAvg idRun=" + idRun);

    double res = 0;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT SUM(total_time) FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        int sumTotalTime = rs.getInt(1);
        rs.close();
        log.info("sumTotalTime=" + sumTotalTime);

        st = new StringBuilder();
        st.append("SELECT avg_heart_rate, total_time FROM ");
        st.append(getTableName());
        st.append(" WHERE id=?");

        pstmt = conn.prepareStatement(st.toString());
        pstmt.setInt(1, idRun);
        rs = pstmt.executeQuery();

        int avgHeart, totalTime;
        while (rs.next()) {
          avgHeart = rs.getInt(1);
          totalTime = rs.getInt(2);
          res += (avgHeart * totalTime * 1.0) / sumTotalTime;
        }
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<heartAvg");
    return (int) res;
  }

  /**
   * Restitue la distance totale.
   * 
   * @param idRun
   * @return la distance totale.
   * @throws SQLException
   */
  public double distanceTot(int idRun) throws SQLException {
    log.info(">>distanceTot idRun=" + idRun);

    double distanceTot = 0;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append(" SELECT SUM(total_dist) FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        distanceTot = rs.getFloat(1);
        rs.close();
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<distanceTot distanceTot=" + distanceTot);
    return distanceTot;
  }

  /**
   * Restitue la distance totale par an.
   * 
   * @param idUser
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatYear[] distanceByYear(int idUser, int sportType) throws SQLException {
    log.info(">>distanceByYear");

    List<DataStatYear> res = new ArrayList<DataStatYear>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT ");
        st.append("Year(LAP.start_time) AS THE_YEAR,");
        st.append("SUM(LAP.total_dist),");
        st.append("COUNT(DISTINCT LAP.id) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP ");
        if (sportType != -1) {
          st.append(", " + DatabaseManager.TABLE_RUN + " RUN ");
          st.append("WHERE RUN.SPORT_TYPE=? AND RUN.ID = LAP.ID ");
        }
        st.append("GROUP BY Year(LAP.start_time)");
      }
      else {
        st.append("SELECT ");
        st.append("Year(LAP.start_time) AS THE_YEAR,");
        st.append("SUM(LAP.total_dist),");
        st.append("COUNT(DISTINCT LAP.ID) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID=RUN.ID ");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=? AND RUN.ID = LAP.ID");
        }
        st.append(" GROUP BY Year(LAP.start_time)");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        res.add(new DataStatYear(rs.getInt(1), rs.getDouble(2), 0, rs.getInt(3)));

      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<distanceByYear");

    DataStatYear[] tabRes = new DataStatYear[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue la distance totale par an.
   * 
   * @param idUser
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatYear[] timeByYear(int idUser) throws SQLException {
    log.info(">>distanceByYear");

    List<DataStatYear> res = new ArrayList<DataStatYear>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT");
        st.append(" Year(start_time) AS THE_YEAR,");
        st.append(" SUM(total_time),");
        st.append(" COUNT(DISTINCT id)");
        st.append("FROM ");
        st.append(getTableName());
        st.append(" GROUP BY Year(start_time)");
      }
      else {
        st.append("SELECT ");
        st.append("Year(LAP.start_time) AS THE_YEAR,");
        st.append("SUM(LAP.total_time),");
        st.append("COUNT(DISTINCT LAP.id) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        st.append(" GROUP BY Year(LAP.start_time)");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        res.add(new DataStatYear(rs.getInt(1), 0, rs.getDouble(2), rs.getInt(3)));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<distanceByYear");

    DataStatYear[] tabRes = new DataStatYear[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue la distance totale par mois.
   * 
   * @param idUser
   *
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatYearMonth[] distanceByMonth(int idUser, int sportType) throws SQLException {
    log.info(">>distanceByMonth");

    List<DataStatYearMonth> res = new ArrayList<DataStatYearMonth>();
    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT ");
        st.append(" Year(LAP.start_time) AS THE_YEAR,");
        st.append(" Month(LAP.start_time) AS THE_MONTH,");
        st.append(" SUM(LAP.total_dist) AS TOT_DIST,");
        st.append(" COUNT(DISTINCT LAP.ID) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID=RUN.ID");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" GROUP BY Year(LAP.start_time), Month(LAP.start_time)");
        st.append(" ORDER BY THE_YEAR, THE_MONTH");
      }
      else {
        st.append("SELECT ");
        st.append(" Year(LAP.start_time) AS THE_YEAR,");
        st.append(" Month(LAP.start_time) AS THE_MONTH,");
        st.append(" SUM(LAP.total_dist) AS TOT_DIST,");
        st.append(" COUNT(DISTINCT LAP.ID) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID=RUN.ID");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" GROUP BY Year(LAP.start_time), Month(LAP.start_time)");
        st.append(" ORDER BY THE_YEAR, THE_MONTH");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        ++index;
        pstmt.setInt(index, idUser);
      }
      if (sportType != -1) {
        ++index;
        pstmt.setInt(index, sportType);
      }
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        res.add(new DataStatYearMonth(rs.getInt(1), rs.getInt(2), rs
            .getDouble(3), 0, rs.getInt(5)));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<distanceByMonth");

    DataStatYearMonth[] tabRes = new DataStatYearMonth[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue la distance totale par mois.
   * 
   * @param idUser
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatYearMonth[] timeByMonth(int idUser) throws SQLException {
    log.info(">>timeByMonth");

    List<DataStatYearMonth> res = new ArrayList<DataStatYearMonth>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT");
        st.append(" Year(start_time) AS THE_YEAR,");
        st.append(" Month(start_time) AS THE_MONTH,");
        st.append(" SUM(total_time) AS TOT_TIME, ");
        st.append(" COUNT(DISTINCT id) ");
        st.append("FROM ");
        st.append(getTableName());
        st.append(" GROUP BY Year(start_time), Month(start_time)");
        st.append(" ORDER  BY THE_YEAR, THE_MONTH");
      }
      else {
        // st.append("SELECT");
        // st.append(" Year(LAP.start_time) AS THE_YEAR,");
        // st.append(" Month(LAP.start_time) AS THE_MONTH,");
        // st.append(" SUM(LAP.total_time) AS TOT_TIME,");
        // st.append(" COUNT(DISTINCT LAP.id) ");
        // st.append("FROM ");
        // st.append(getTableName() + " LAP, ");
        // st.append(RunTableManager.getInstance().getTableName() +
        // " RUN ");
        // st.append("WHERE LAP.ID = RUN.ID ");
        // st.append(" AND RUN.id_user=?");
        // st.append(" GROUP BY Year(LAP.start_time), Month(LAP.start_time)");
        // st.append(" ORDER  BY THE_YEAR, THE_MONTH");

        st.append("SELECT");
        st.append(" Year(LAP.start_time) AS THE_YEAR,");
        st.append(" Month(LAP.start_time) AS THE_MONTH,");
        st.append(" SUM(LAP.total_time) AS TOT_TIME,");
        st.append(" COUNT(DISTINCT LAP.id) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        st.append(" GROUP BY Year(LAP.start_time), Month(LAP.start_time)");
        st.append(" ORDER  BY THE_YEAR, THE_MONTH");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        res.add(new DataStatYearMonth(rs.getInt(1), rs.getInt(2), 0, rs
            .getDouble(3), rs.getInt(4)));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<timeByMonth");

    DataStatYearMonth[] tabRes = new DataStatYearMonth[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue les stats par semaine.
   * 
   * @return les stats par semaine.
   * @throws SQLException
   */
  public DataStatYearWeek[] statByWeek(int idUser, int sportType) throws SQLException {
    log.info(">>statByWeek");

    List<DataStatYearWeek> res = new ArrayList<DataStatYearWeek>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();

      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT");
        st.append(" THE_YEAR,");
        st.append(" THE_WEEK,");
        st.append(" SUM(THE_TOT_DIST),");
        st.append(" SUM(THE_TOT_TIME),");
        st.append(" COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.yearWeek(LAP.start_time) AS THE_YEAR,");
        st.append(" APP.week(LAP.start_time) AS THE_WEEK,");
        st.append(" LAP.total_dist AS THE_TOT_DIST,");
        st.append(" CASE WHEN LAP.total_moving_time > 0 then LAP.total_moving_time else LAP.total_time end AS THE_TOT_TIME,");
        // st.append(" LAP.total_time AS THE_TOT_TIME,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID");
        if (sportType != -1) {
          st.append(" AND SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_YEAR, THE_WEEK");
        st.append(" ORDER  BY THE_YEAR, THE_WEEK");
      }
      else {
        st.append("SELECT");
        st.append(" THE_YEAR,");
        st.append(" THE_WEEK,");
        st.append(" SUM(THE_TOT_DIST),");
        st.append(" SUM(THE_TOT_TIME),");
        st.append(" COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.yearWeek(LAP.start_time) AS THE_YEAR,");
        st.append(" APP.week(LAP.start_time) AS THE_WEEK,");
        st.append(" LAP.total_dist AS THE_TOT_DIST,");
        // st.append(" LAP.total_time AS THE_TOT_TIME,");
        st.append(" CASE WHEN LAP.total_moving_time > 0 then LAP.total_moving_time else LAP.total_time end AS THE_TOT_TIME,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_YEAR, THE_WEEK");
        st.append(" ORDER  BY THE_YEAR, THE_WEEK");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }
      ResultSet rs = pstmt.executeQuery();

      DataStatYearWeek dLast = new DataStatYearWeek(1, -1, 0, 0, 0);
      while (rs.next()) {
        dLast = new DataStatYearWeek(rs.getInt(1),
                                     rs.getInt(2),
                                     rs.getDouble(3),
                                     rs.getDouble(4),
                                     rs.getInt(5));
        res.add(dLast);
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<statByWeek");

    DataStatYearWeek[] tabRes = new DataStatYearWeek[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue les stats par jour de la semaine.
   * 
   * @param idUser
   * @return les stats par jour de la semaine.
   * @throws SQLException
   */
  public DataStatWeek[] statByDayOfWeek(int idUser, int sportType) throws SQLException {
    log.info(">>statByDayOfWeek");

    DataStatWeek[] res = new DataStatWeek[7];
    for (int i = 0; i < res.length; i++) {
      res[i] = new DataStatWeek(i, 0, 0, 0);
    }
    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT ");
        st.append("THE_DAY, ");
        st.append("SUM(THE_TOT_DIST), ");
        st.append("SUM(THE_TOT_TIME), ");
        st.append("COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.dayOfWeek(LAP.start_time) AS THE_DAY,");
        st.append(" LAP.total_dist AS THE_TOT_DIST,");
        st.append(" LAP.total_time AS THE_TOT_TIME,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID");
        if (sportType != -1) {
          st.append(" AND SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_DAY");
        st.append(" ORDER BY THE_DAY");
      }
      else {
        st.append("SELECT ");
        st.append("THE_DAY, ");
        st.append("SUM(THE_TOT_DIST), ");
        st.append("SUM(THE_TOT_TIME), ");
        st.append("COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.dayOfWeek(LAP.start_time) AS THE_DAY,");
        st.append(" LAP.total_dist AS THE_TOT_DIST,");
        st.append(" LAP.total_time AS THE_TOT_TIME,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_DAY");
        st.append(" ORDER BY THE_DAY");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        res[rs.getInt(1) - 1].setDistance(rs.getDouble(2));
        res[rs.getInt(1) - 1].setTimeTot(rs.getDouble(3));
        res[rs.getInt(1) - 1].setNumberRaces(rs.getInt(4));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<statByDayOfWeek");
    return res;
  }

  /**
   * Restitue les stats totale par mois.
   * 
   * @param idUser
   * @return les stats par mois.
   * @throws SQLException
   */
  public DataStatYearMonth[] statByMonth(int idUser, int sportType) throws SQLException {
    log.info(">>statByMonth");

    List<DataStatYearMonth> res = new ArrayList<DataStatYearMonth>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT");
        st.append(" Year(LAP.start_time) AS THE_YEAR,");
        st.append(" Month(LAP.start_time) AS THE_MONTH,");
        st.append(" SUM(LAP.total_dist) AS TOT_DIST, ");
        st.append(" SUM(CASE WHEN LAP.total_moving_time > 0 then LAP.total_moving_time else LAP.total_time end),");
        st.append("COUNT(DISTINCT LAP.id) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" GROUP BY Year(LAP.start_time), Month(LAP.start_time)");
        st.append(" ORDER  BY THE_YEAR, THE_MONTH");
      }
      else {
        st.append("SELECT");
        st.append(" Year(LAP.start_time) AS THE_YEAR,");
        st.append(" Month(LAP.start_time) AS THE_MONTH,");
        st.append(" SUM(LAP.total_dist) AS TOT_DIST, ");
        st.append(" SUM(CASE WHEN LAP.total_moving_time > 0 then LAP.total_moving_time else LAP.total_time end),");
        st.append("COUNT(DISTINCT LAP.id) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" GROUP BY Year(LAP.start_time), Month(LAP.start_time)");
        st.append(" ORDER  BY THE_YEAR, THE_MONTH");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        res.add(new DataStatYearMonth(rs.getInt(1), rs.getInt(2), rs
            .getDouble(3), rs.getDouble(4), rs.getInt(5)));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<statByMonth");

    DataStatYearMonth[] tabRes = new DataStatYearMonth[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue les stats par an.
   * 
   * @param idUser
   * @return les stats par an.
   * @throws SQLException
   */
  public DataStatYear[] statByYear(int idUser, int sportType) throws SQLException {
    log.info(">>statByYear");

    List<DataStatYear> res = new ArrayList<DataStatYear>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT ");
        st.append("Year(LAP.start_time) AS THE_YEAR,");
        st.append("SUM(LAP.total_dist),");
        st.append("SUM(CASE WHEN LAP.total_moving_time > 0 then LAP.total_moving_time else LAP.total_time end),");
        st.append("COUNT(DISTINCT LAP.id) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" GROUP BY Year(LAP.start_time)");
      }
      else {
        st.append("SELECT ");
        st.append("Year(LAP.start_time) AS THE_YEAR,");
        st.append("SUM(LAP.total_dist),");
        st.append("SUM(CASE WHEN LAP.total_moving_time > 0 then LAP.total_moving_time else LAP.total_time end),");
        st.append("COUNT(DISTINCT LAP.id) ");
        st.append("FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" GROUP BY Year(LAP.start_time)");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        res.add(new DataStatYear(rs.getInt(1),
                                 rs.getDouble(2),
                                 rs.getDouble(3),
                                 rs.getInt(4)));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<statByYear");

    DataStatYear[] tabRes = new DataStatYear[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue les informations de statistiques.
   * 
   * @param idUser
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatTot total(int idUser, int sportType) throws SQLException {
    log.info(">>total");

    DataStatTot tot = null;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();

      st.append("SELECT");
      st.append(" SUM(CAST(calories AS INTEGER)) AS CALORIES, ");
      st.append(" SUM(CAST(total_dist AS DOUBLE)), ");
      st.append(" SUM(CAST(total_time AS DOUBLE)) ");
      st.append("FROM ");
      st.append(getTableName() + " LAP, ");
      st.append(RunTableManager.getInstance().getTableName() + " RUN ");
      if (!DataUser.isAllUser(idUser) || sportType != -1) {
        st.append("WHERE LAP.ID = RUN.ID");
      }
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }
      if (sportType != -1) {
        st.append(" AND sport_type=?");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        tot = new DataStatTot(rs.getInt(1), rs.getDouble(2), (int) rs.getDouble(3));
      }
      else {
        tot = new DataStatTot(0, 0, 0);
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<DataStatTot");
    return tot;
  }

  /**
   * Restitue la distance totale par mois.
   * 
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatYearWeek[] distanceByWeek(int idUser, int sportType) throws SQLException {
    log.info(">>distanceByWeek");

    List<DataStatYearWeek> res = new ArrayList<DataStatYearWeek>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();

      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT");
        st.append(" THE_YEAR,");
        st.append(" THE_WEEK,");
        st.append(" SUM(THE_TOT_DIST),");
        st.append(" COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.yearWeek(LAP.start_time) AS THE_YEAR,");
        st.append(" APP.week(LAP.start_time) AS THE_WEEK,");
        st.append(" LAP.total_dist AS THE_TOT_DIST,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID=RUN.ID");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_YEAR, THE_WEEK");
        st.append(" ORDER  BY THE_YEAR, THE_WEEK");
      }
      else {
        st.append("SELECT");
        st.append(" THE_YEAR,");
        st.append(" THE_WEEK,");
        st.append(" SUM(THE_TOT_DIST),");
        st.append(" COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.yearWeek(LAP.start_time) AS THE_YEAR,");
        st.append(" APP.week(LAP.start_time) AS THE_WEEK,");
        st.append(" LAP.total_dist AS THE_TOT_DIST,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_YEAR, THE_WEEK");
        st.append(" ORDER BY THE_YEAR, THE_WEEK");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }
      ResultSet rs = pstmt.executeQuery();

      DataStatYearWeek dLast = new DataStatYearWeek(1, -1, 0, 0, 0);
      while (rs.next()) {
        dLast = new DataStatYearWeek(rs.getInt(1),
                                     rs.getInt(2),
                                     rs.getDouble(3),
                                     0,
                                     rs.getInt(4));
        res.add(dLast);
      }

      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<distanceByWeek");

    DataStatYearWeek[] tabRes = new DataStatYearWeek[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue la distance totale par mois.
   * 
   * @param idUser
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatYearWeek[] timeByWeek(int idUser) throws SQLException {
    log.info(">>timeByWeek");

    List<DataStatYearWeek> res = new ArrayList<DataStatYearWeek>();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT");
        st.append(" THE_YEAR,");
        st.append(" THE_WEEK,");
        st.append(" SUM(total_time) AS TOT_TIME,");
        st.append(" COUNT(DISTINCT id) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.yearWeek(start_time) AS THE_YEAR,");
        st.append(" APP.week(start_time) AS THE_WEEK,");
        st.append(" total_time,");
        st.append(" id");
        st.append(" FROM ");
        st.append(getTableName());
        st.append(" ) tt ");
        st.append(" GROUP BY THE_YEAR, THE_WEEK");
        st.append(" ORDER  BY THE_YEAR, THE_WEEK");
      }
      else {
        st.append("SELECT");
        st.append(" THE_YEAR,");
        st.append(" THE_WEEK,");
        st.append(" SUM(THE_TOTAL_TIME),");
        st.append(" COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.yearWeek(LAP.start_time) AS THE_YEAR,");
        st.append(" APP.week(LAP.start_time) AS THE_WEEK,");
        st.append(" LAP.total_time AS THE_TOTAL_TIME,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        st.append(" ) tt ");
        st.append(" GROUP BY THE_YEAR, THE_WEEK");
        st.append(" ORDER  BY THE_YEAR, THE_WEEK");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      DataStatYearWeek dLast = new DataStatYearWeek(1, -1, 0, 0, 0);
      while (rs.next()) {
        dLast = new DataStatYearWeek(rs.getInt(1),
                                     rs.getInt(2),
                                     0,
                                     rs.getDouble(3),
                                     rs.getInt(4));
        res.add(dLast);
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<timeByWeek");

    DataStatYearWeek[] tabRes = new DataStatYearWeek[res.size()];
    res.toArray(tabRes);
    return tabRes;
  }

  /**
   * Restitue la distance totale par mois.
   * 
   * @param idUser
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatWeek[] distanceByDayOfWeek(int idUser, int sportType) throws SQLException {
    log.info(">>distanceByDayOfWeek");

    DataStatWeek[] res = new DataStatWeek[7];
    for (int i = 0; i < res.length; i++) {
      res[i] = new DataStatWeek(i, 0, 0, 0);
    }

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT ");
        st.append("THE_DAY, ");
        st.append("SUM(total_dist), ");
        st.append("COUNT(DISTINCT id) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.dayOfWeek(LAP.start_time) AS THE_DAY,");
        st.append(" LAP.total_dist,");
        st.append(" LAP.id");
        st.append(" FROM ");
        st.append(getTableName() + " LAP");
        if (sportType != -1) {
          st.append(", " + RunTableManager.getInstance().getTableName()
                    + " RUN ");
          st.append("WHERE LAP.ID = RUN.ID");
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_DAY");
        st.append(" ORDER BY THE_DAY");
      }
      else {
        st.append("SELECT ");
        st.append("THE_DAY, ");
        st.append("SUM(THE_TOT_DIST), ");
        st.append("COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.dayOfWeek(LAP.start_time) AS THE_DAY,");
        st.append(" LAP.total_dist AS THE_TOT_DIST,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        if (sportType != -1) {
          st.append(" AND RUN.SPORT_TYPE=?");
        }
        st.append(" ) tt ");
        st.append(" GROUP BY THE_DAY");
        st.append(" ORDER BY THE_DAY");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      int index = 0;
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }
      if (sportType != -1) {
        pstmt.setInt(++index, sportType);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        res[rs.getInt(1) - 1].setDistance(rs.getDouble(2));
        res[rs.getInt(1) - 1].setNumberRaces(rs.getInt(3));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<distanceByDayOfWeek");
    return res;
  }

  /**
   * Restitue la distance totale par mois.
   * 
   * @param idUser
   * @return la distance totale.
   * @throws SQLException
   */
  public DataStatWeek[] timeByDayOfWeek(int idUser) throws SQLException {
    log.info(">>timeByDayOfWeek");

    DataStatWeek[] res = new DataStatWeek[7];
    for (int i = 0; i < res.length; i++) {
      res[i] = new DataStatWeek(i, 0, 0, 0);
    }
    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      if (DataUser.isAllUser(idUser)) {
        st.append("SELECT");
        st.append(" THE_DAY,");
        st.append(" SUM(total_time),");
        st.append(" COUNT(DISTINCT id) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.dayOfWeek(start_time) AS THE_DAY,");
        st.append(" total_time,");
        st.append(" id");
        st.append(" FROM ");
        st.append(getTableName());
        st.append(" ) tt ");
        st.append(" GROUP BY THE_DAY");
        st.append(" ORDER BY THE_DAY");
      }
      else {
        st.append("SELECT");
        st.append(" THE_DAY,");
        st.append(" SUM(THE_TOT_TIME),");
        st.append(" COUNT(DISTINCT THE_ID) ");
        st.append("FROM ");
        st.append("(SELECT");
        st.append(" APP.dayOfWeek(LAP.start_time) AS THE_DAY,");
        st.append(" LAP.total_time AS THE_TOT_TIME,");
        st.append(" LAP.id AS THE_ID");
        st.append(" FROM ");
        st.append(getTableName() + " LAP, ");
        st.append(RunTableManager.getInstance().getTableName() + " RUN ");
        st.append("WHERE LAP.ID = RUN.ID ");
        st.append(" AND RUN.id_user=?");
        st.append(" ) tt ");
        st.append(" GROUP BY THE_DAY");
        st.append(" ORDER BY THE_DAY");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        res[rs.getInt(1) - 1].setTimeTot(rs.getDouble(2));
        res[rs.getInt(1) - 1].setNumberRaces(rs.getInt(3));
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<timeByDayOfWeek");
    return res;
  }

  /**
   * Restitue le temp total avec les pauses.
   * 
   * @param idRun
   * @return le temp total.
   * @throws SQLException
   */
  public int timeTot(int idRun) throws SQLException {
    log.debug(">>timeTot idRun=" + idRun);

    int timeTot = 0;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append(" SELECT SUM(total_time) FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        timeTot = rs.getInt(1);
        rs.close();
        log.info("timeTot" + timeTot);
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<timeTot");
    return timeTot;
  }

  /**
   * Restitue les denivel&eacute;s.
   * 
   * @param idRun
   * @return idLap
   * @throws SQLException
   */
  public int[] altitude(int idRun, int lapIndex) throws SQLException {
    return altitude(idRun, lapIndex, true);
  }

  /**
   * Restitue les denivel&eacute;s sans correction.
   * 
   * @param idRun
   * @return idLap
   * @throws SQLException
   */
  public int[] altitudeOriginal(int idRun, int lapIndex) throws SQLException {
    return altitude(idRun, lapIndex, false);
  }

  private int[] altitude(int idRun, int lapIndex, boolean isFilter) throws SQLException {
    int[] res = new int[2];

    Connection conn = DatabaseManager.getConnection();

    try {

      StringBuilder st = new StringBuilder();
      st.append("SELECT start_time, total_time FROM ");
      st.append(getTableName());
      st.append(" WHERE id=? AND lap_index=?");
      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idRun);
      pstmt.setInt(2, lapIndex);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        Timestamp dateDeb = rs.getTimestamp(1);

        int totTime = rs.getInt(2);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateDeb);
        cal.add(Calendar.MILLISECOND, totTime * 10);
        Date dateEnd = cal.getTime();

        res = isFilter ? RunTrkTableManager.getInstance().altitude(idRun,
                                                                   dateDeb,
                                                                   dateEnd)
            : RunTrkTableManager.getInstance().altitudeOriginal(idRun,
                                                                dateDeb,
                                                                dateEnd);
      }
      else {
        res[0] = 0;
        res[1] = 0;
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    return res;
  }

}
