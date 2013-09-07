package fr.turtlesport.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author denis
 * 
 */
public final class UserActivityTableManager extends AbstractTableManager {
  private static TurtleLogger             log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UserActivityTableManager.class);
  }

  private static UserActivityTableManager singleton = new UserActivityTableManager();

  /**
   * 
   */
  private UserActivityTableManager() {
    super();

    // creation des sports run velo, other si non present
    try {
      if (retreive(DataActivityRun.SPORT_TYPE) == null) {
        store(FactoryDataActivity.getInstance(DataActivityRun.SPORT_TYPE));
      }
      if (retreive(DataActivityBike.SPORT_TYPE) == null) {
        store(FactoryDataActivity.getInstance(DataActivityBike.SPORT_TYPE));
      }
      if (retreive(DataActivityOther.SPORT_TYPE) == null) {
        store(FactoryDataActivity.getInstance(DataActivityOther.SPORT_TYPE));
      }
    }
    catch (SQLException e) {
      log.error("", e);
    }
  }

  /**
   * Restitue une instance unique.
   */
  public static UserActivityTableManager getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractTableManager#getTableName()
   */
  @Override
  public String getTableName() {
    return DatabaseManager.TABLE_USER_ACTIVITY;
  }

  /**
   * Ajoute une activit&eacute;.
   * 
   * @throws SQLException
   */
  private void storeOrUpdate(AbstractDataActivity data) throws SQLException {
    if (data == null) {
      throw new IllegalArgumentException();
    }

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }
    Connection conn = DatabaseManager.getConnection();

    try {
      if (data.getSportType() != -1) {
        // suppression pour mis a jour
        delete(data.getSportType());
      }
      store(data);
    }
    catch (SQLException e) {
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw e;
    }

    // ok
    if (!isInTransaction) {
      DatabaseManager.commitTransaction();
    }
    DatabaseManager.releaseConnection(conn);
  }

  /**
   * Ajoute une activit&eacute;.
   * 
   * @throws SQLException
   */
  public void store(List<AbstractDataActivity> list) throws SQLException {
    if (list == null || list.size() == 0) {
      return;
    }

    // recuperation de toutes les lignes et suppression des lignes non trouves.
    List<AbstractDataActivity> listOld = retreive();

    for (AbstractDataActivity a1 : listOld) {
      boolean bFound = false;
      for (AbstractDataActivity a2 : list) {
        if (a1.getSportType() == a2.getSportType()) {
          bFound = true;
          break;
        }
      }
      // si non trouve on supprime
      if (!bFound) {
        // suppression
        delete(a1.getSportType());
        // mis a jour du sport type
        RunTableManager.getInstance()
            .updateSportType(a1.getSportType(), DataActivityOther.SPORT_TYPE);
        RunTableManager.getInstance().logTable();
      }
    }

    // sauvegarde
    for (AbstractDataActivity a : list) {
      storeOrUpdate(a);
    }
    
  }

  /**
   * Ajoute une activit&eacute;.
   * 
   * @throws SQLException
   */
  private void store(AbstractDataActivity data) throws SQLException {
    if (data == null) {
      throw new IllegalArgumentException();
    }

    log.debug(">>store sportType=" + data.getSportType());

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st;
      if (data.getSportType() == -1) {
        int sportType = 3;
        PreparedStatement pstmt = conn
            .prepareStatement("SELECT MAX(sport_type) FROM " + getTableName());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
          int val = rs.getInt(1);
          if (val > 2) {
            // 0,1,2 sont reserves pour velo, bike autre sport
            sportType = val+1;
          }
        }
        data.setSportType(sportType);
      }
      st = new StringBuilder();
      st.append("INSERT INTO ");
      st.append(getTableName());
      st.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      // Insertion des zones cardiaques.
      DataHeartZone dhz;
      for (int i = 0; i < data.getHeartZones().length; i++) {
        dhz = data.getHeartZones()[i];
        pstmt.setInt(1, data.getSportType());
        pstmt.setInt(2, data.getMaxHeartRate());
        pstmt.setInt(3, i + 1);
        pstmt.setInt(4, dhz.getLowHeartRate());
        pstmt.setInt(5, dhz.getHighHeartRate());
        pstmt.setInt(6, 0);
        pstmt.setString(7, null);
        pstmt.setFloat(8, 0);
        pstmt.setFloat(9, 0);
        pstmt.setString(10, null);
        pstmt.setString(11, data.getName());
        pstmt.setInt(12, convertToSmallInt(data.isDefaultActivity()));
        pstmt.setString(13, data.getIconName());
        pstmt.executeUpdate();
      }

      // Insertion des zones de vitesse.
      DataSpeedZone dsz;
      for (int i = 0; i < data.getSpeedZones().length; i++) {
        dsz = data.getSpeedZones()[i];
        pstmt.setInt(1, data.getSportType());
        pstmt.setInt(2, data.getMaxHeartRate());
        pstmt.setInt(3, 0);
        pstmt.setInt(4, 0);
        pstmt.setInt(5, 0);
        pstmt.setInt(6, i + 1);
        pstmt.setString(7, dsz.getName());
        pstmt.setFloat(8, dsz.getLowSpeed());
        pstmt.setFloat(9, dsz.getHighSpeed());
        pstmt.setString(10, dsz.getUnit());
        pstmt.executeUpdate();
      }
    }
    catch (SQLException e) {
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw e;
    }

    // ok
    if (!isInTransaction) {
      DatabaseManager.commitTransaction();
    }
    DatabaseManager.releaseConnection(conn);

    log.debug("<<store");
  }

  /**
   * Suppression d'une activit&eacute;.
   * 
   * @param sportType
   *          le sport type.
   * @throws SQLException
   */
  private boolean delete(int sportType) throws SQLException {
    log.debug(">>delete sportType=" + sportType);

    boolean bRes = false;

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("DELETE FROM ");
      st.append(getTableName());
      st.append(" WHERE sport_type = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, sportType);

      int res = pstmt.executeUpdate();
      if (res > 0) {
        bRes = true;
      }
      pstmt.close();
    }
    catch (SQLException e) {
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw e;
    }

    // ok
    if (!isInTransaction) {
      DatabaseManager.commitTransaction();
    }
    DatabaseManager.releaseConnection(conn);

    log.debug("<<delete bRes=" + bRes);
    return bRes;
  }

  /**
   * Suppression des activit&eacute;s.
   * 
   * @throws SQLException
   */
  public boolean delete() throws SQLException {
    log.debug(">>delete");

    boolean bRes = false;

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      PreparedStatement pstmt = conn.prepareStatement("DELETE FROM "
                                                      + getTableName());
      int res = pstmt.executeUpdate();
      if (res > 0) {
        bRes = true;
      }
      pstmt.close();
    }
    catch (SQLException e) {
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw e;
    }

    // ok
    if (!isInTransaction) {
      DatabaseManager.commitTransaction();
    }
    DatabaseManager.releaseConnection(conn);

    log.debug("<<delete bRes=" + bRes);
    return bRes;
  }

  /**
   * Recup&eacute;ration de l'icone d'une activit&eacute;.
   * 
   * @throws SQLException
   */
  public String retreiveIcon(int sportType) throws SQLException {
    if (log.isDebugEnabled()) {
      log.debug(">>retreiveIcon sportType=" + sportType);
    }
    String icon = null;

    Connection conn = DatabaseManager.getConnection();
    try {
      // recuperation infos generales
      StringBuilder st = new StringBuilder();
      st.append("SELECT icon FROM ");
      st.append(getTableName());
      st.append(" WHERE sport_type = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, sportType);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        icon = rs.getString(1);
      }
      pstmt.close();
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isDebugEnabled()) {
      log.debug("<<retreive");
    }
    return icon;
  }

  /**
   * Recup&eacute;ration d'une activit&eacute;.
   * 
   * @throws SQLException
   */
  public AbstractDataActivity retreive(int sportType) throws SQLException {
    log.debug(">>retreive sportType=" + sportType);

    AbstractDataActivity dataActivity;

    Connection conn = DatabaseManager.getConnection();
    try {

      // recuperation infos generales
      StringBuilder st = new StringBuilder();
      st.append("SELECT sport_type, max_heart_rate, icon FROM ");
      st.append(getTableName());
      st.append(" WHERE sport_type = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, sportType);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        try {
          dataActivity = FactoryDataActivity.getInstance(rs.getInt(1));
          dataActivity.setMaxHeartRate(rs.getInt(2));
          dataActivity.setIconName(rs.getString(3));
        }
        catch (IllegalArgumentException e) {
          log.error("sport type invalide", e);
          throw new SQLException("Erreur database");
        }
      }
      else {
        return null;
      }
      pstmt.close();
      rs.close();

      // recuperations des zones cardiaques.
      st = new StringBuilder();
      st.append("SELECT low_heart_rate, high_heart_rate FROM ");
      st.append(getTableName());
      st.append(" WHERE sport_type = ?");
      st.append(" AND id_zone_heart <> 0");
      st.append(" ORDER BY id_zone_heart ASC");

      pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, sportType);
      rs = pstmt.executeQuery();
      int index = 0;
      while (rs.next() && index < AbstractDataActivity.MAX_HEART_ZONE) {
        dataActivity
            .setHeartZone(new DataHeartZone(rs.getInt(1), rs.getInt(2)), index);
        index++;
      }
      pstmt.close();
      rs.close();

      // recuperations des zones de vitesse.
      st = new StringBuilder();
      st.append("SELECT speed_name, low_speed, high_speed, unit FROM ");
      st.append(getTableName());
      st.append(" WHERE sport_type = ?");
      st.append(" AND id_speed <> 0");
      st.append(" ORDER BY id_speed ASC");

      pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, sportType);
      rs = pstmt.executeQuery();
      index = 0;
      DataSpeedZone dsz;
      while (rs.next() && index < AbstractDataActivity.MAX_SPEED_ZONE) {
        dsz = new DataSpeedZone(rs.getString(1),
                                rs.getFloat(2),
                                rs.getFloat(3),
                                rs.getString(4));
        dataActivity.setSpeedZone(dsz, index);
        index++;
      }
      pstmt.close();
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreive");
    return dataActivity;
  }

  /**
   * Recup&eacute;ration du nom de l'activit&eacute;.
   * 
   * @return le nom de l'activit&eacute;.
   * @throws SQLException
   */
  public String retreiveName(int sportType) throws SQLException {
    log.debug(">>retreiveName sportType=" + sportType);

    String name = null;

    Connection conn = DatabaseManager.getConnection();
    try {

      // recuperation infos generales
      StringBuilder st = new StringBuilder();
      st.append("SELECT name FROM ");
      st.append(getTableName());
      st.append(" WHERE sport_type = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, sportType);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        name = rs.getString(1);
      }
      pstmt.close();
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreiveName");
    return name;
  }

  /**
   * Recup&eacute;ration des activit&eacute;es.
   * 
   * @throws SQLException
   */
  public List<AbstractDataActivity> retreive() throws SQLException {
    log.debug(">>retreive sportType");

    ArrayList<AbstractDataActivity> listActivity = new ArrayList<AbstractDataActivity>();
    AbstractDataActivity dataActivity;

    Connection conn = DatabaseManager.getConnection();
    PreparedStatement pstmt1 = null;
    ResultSet rs1 = null;
    try {
      // recuperation infos generales
      pstmt1 = conn
          .prepareStatement("SELECT DISTINCT(sport_type), "
                            + "max_heart_rate, default_sport, name, icon FROM "
                            + getTableName());
      rs1 = pstmt1.executeQuery();

      while (rs1.next()) {
        dataActivity = FactoryDataActivity.getInstance(rs1.getInt(1));
        dataActivity.setMaxHeartRate(rs1.getInt(2));
        dataActivity.setDefault(convertToBoolean(rs1.getInt(3)));
        dataActivity.setName(rs1.getString(4));
        dataActivity.setIconName(rs1.getString(5));
        listActivity.add(dataActivity);

        // recuperations des zones cardiaques.
        StringBuilder st = new StringBuilder();
        st.append("SELECT low_heart_rate, high_heart_rate FROM ");
        st.append(getTableName());
        st.append(" WHERE sport_type = ?");
        st.append(" AND id_zone_heart <> 0");
        st.append(" ORDER BY id_zone_heart ASC");

        PreparedStatement pstmt = conn.prepareStatement(st.toString());
        pstmt = conn.prepareStatement(st.toString());
        pstmt.setInt(1, dataActivity.getSportType());
        ResultSet rs = pstmt.executeQuery();
        int index = 0;
        while (rs.next() && index < AbstractDataActivity.MAX_HEART_ZONE) {
          dataActivity.setHeartZone(new DataHeartZone(rs.getInt(1), rs
                                        .getInt(2)),
                                    index);
          index++;
        }
        pstmt.close();
        rs.close();

        // recuperations des zones de vitesse.
        st = new StringBuilder();
        st.append("SELECT speed_name, low_speed, high_speed, unit FROM ");
        st.append(getTableName());
        st.append(" WHERE sport_type = ?");
        st.append(" AND id_speed <> 0");
        st.append(" ORDER BY id_speed ASC");

        pstmt = conn.prepareStatement(st.toString());
        pstmt.setInt(1, dataActivity.getSportType());
        rs = pstmt.executeQuery();
        index = 0;
        DataSpeedZone dsz;
        while (rs.next() && index < AbstractDataActivity.MAX_SPEED_ZONE) {
          dsz = new DataSpeedZone(rs.getString(1),
                                  rs.getFloat(2),
                                  rs.getFloat(3),
                                  rs.getString(4));
          dataActivity.setSpeedZone(dsz, index);
          index++;
        }
        pstmt.close();
        rs.close();
      }
    }
    finally {
      if (pstmt1 != null) {
        pstmt1.close();
      }
      if (rs1 != null) {
        rs1.close();
      }
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreive");
    return listActivity;
  }

  /**
   * Restitue le sport type de l'activit&eacute; par d&eacute;faut.
   * 
   * @return le sport type.
   * @throws SQLException
   */
  public int retreiveDefaultActivitySportType() throws SQLException {
    log.debug(">>retreiveDefaultActivitySportType");

    int sportType = -1;

    Connection conn = DatabaseManager.getConnection();
    try {

      // recuperation infos generales
      StringBuilder st = new StringBuilder();
      st.append("SELECT sport_type FROM ");
      st.append(getTableName());
      st.append(" WHERE default_sport=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, convertToSmallInt(true));
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        sportType = convertToSmallInt(true);
      }
      pstmt.close();
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreiveDefaultActivitySportType");
    return sportType;

  }

}
