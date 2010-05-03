package fr.turtlesport.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class EquipementTableManager extends AbstractTableManager {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(EquipementTableManager.class);
  }

  private static EquipementTableManager singleton = new EquipementTableManager();

  /**
   * 
   */
  private EquipementTableManager() {
    super();
  }

  /**
   * Restitue une instance unique.
   */
  public static EquipementTableManager getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractTableManager#getTableName()
   */
  @Override
  public String getTableName() {
    return DatabaseManager.TABLE_EQUIPEMENT;
  }

  /**
   * D&eacute;termine si cet &eacute;quipement existe.
   * 
   * @param name
   *          nom de l'&eacute;quipement.
   * @throws SQLException
   */
  public boolean exist(String name) throws SQLException {
    log.debug(">>find name=" + name);

    boolean bRes;
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT name FROM ");
      st.append(getTableName());
      st.append(" WHERE name = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, name);

      ResultSet rs = pstmt.executeQuery();
      bRes = rs.next();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<find bRes=" + bRes);
    return bRes;
  }

  /**
   * Restitue l'&equipement.
   * 
   * @param name
   *          nom de l'&eacute;quipement.
   * @throws SQLException
   */
  public DataEquipement retreive(String name) throws SQLException {
    log.debug(">>retreive name=" + name);

    DataEquipement data = null;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE name = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, name);

      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        data = new DataEquipement();
        data.setName(rs.getString(1));
        data.setAlert(convertToBoolean(rs.getInt(2)));
        data.setWeight(rs.getFloat(3));
        data.setDistanceMax(rs.getFloat(4));
        data.setPath(rs.getString(5));
        data.setDefault(convertToBoolean(rs.getInt(6)));

        // recuperation de la distance parouru
        data.setDistance(distance(data.getName()));
        // recuperation des dates d'utilisation
        data.setFirstUsed(firstUsed(data.getName()));
        data.setLastUsed(lastUsed(data.getName()));
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreive");
    return data;
  }

  /**
   * R&eacute;cup&eacute;ration des noms des &eacute;quipements.
   * 
   * @throws SQLException
   */
  public List<String> retreiveNames() throws SQLException {
    log.info(">>retreiveName");

    ArrayList<String> list;
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT name FROM ");
      st.append(getTableName());
      st.append(" ORDER BY NAME ASC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      ResultSet rs = pstmt.executeQuery();

      list = new ArrayList<String>();
      while (rs.next()) {
        list.add(rs.getString(1));
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.info("<<retreiveName");
    return list;
  }

  /**
   * R&eacute;cup&eacute;ration du nom de l'&eacute;quipement par
   * d&eacute;fault.
   * 
   * @throws SQLException
   */
  public String retreiveNameDefault() throws SQLException {
    log.debug(">>retreiveNameDefault sportType");

    String rep = null;
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT name FROM ");
      st.append(getTableName());
      st.append(" WHERE default_equipement = ?");
      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, convertToSmallInt(true));

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        rep = rs.getString(1);
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreiveNameDefault");
    return rep;
  }

  /**
   * @throws SQLException
   */
  public List<DataEquipement> retreive() throws SQLException {
    log.debug(">>retreive");

    ArrayList<DataEquipement> list;
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" ORDER BY NAME ASC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      ResultSet rs = pstmt.executeQuery();

      list = new ArrayList<DataEquipement>();
      DataEquipement data;
      while (rs.next()) {
        data = new DataEquipement();
        data.setName(rs.getString(1));
        data.setAlert(convertToBoolean(rs.getInt(2)));
        data.setWeight(rs.getFloat(3));
        data.setDistanceMax(rs.getFloat(4));
        data.setPath(rs.getString(5));
        data.setDefault(convertToBoolean(rs.getInt(6)));
        
        // recuperation de la distance parouru
        data.setDistance(distance(data.getName()));
        // recuperation des dates d'utilisation
        data.setFirstUsed(firstUsed(data.getName()));
        data.setLastUsed(lastUsed(data.getName()));

        list.add(data);
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreive");
    return list;
  }

  /**
   * Mis &agrave; jour des &eacute;quipments.
   * 
   * @param listData
   *          les &eacute;quipements.
   * @throws SQLException
   */
  public void storeOrUpdate(List<DataEquipement> listData) throws SQLException {
    if (listData == null) {
      return;
    }

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      // recherche de l'equipement par default
      boolean hasDefault = false;
      for (DataEquipement data : listData) {
        if (hasDefault) {
          data.setDefault(false);
        }
        else if (data.isDefault()) {
          hasDefault = true;
        }
      }

      for (DataEquipement data : listData) {
        if (!exist(data.getName())) {
          // nouvel equipement
          store(data);
        }
        else {
          // mis a jour de l'equipement
          delete(data);
          store(data);
        }
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
  }

  /**
   * Ajoute un &eacute;quipement.
   * 
   * @param data
   *          l'&eacute;quipement.
   * @throws SQLException
   */
  public void store(DataEquipement data) throws SQLException {
    if (data == null) {
      throw new IllegalArgumentException();
    }
    File file = null;
    if (data.getPath() != null) {
      file = new File(data.getPath());
      if (!file.isFile()) {
        file = null;
      }
    }
    store(data.getName(), data.isAlert(), data.getWeight(), data
        .getDistanceMax(), file, data.isDefault());
  }

  /**
   * Ajoute un &eacute;quipement.
   * 
   * @param name
   * @param sportType
   * @param isAlert
   * @param weight
   * @param distanceMax
   * @param file
   * @throws SQLException
   */
  public void store(String name,
                    boolean isAlert,
                    double weight,
                    double distanceMax,
                    File file,
                    boolean isDefault) throws SQLException {

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      // Insertion du nouvel equipement
      StringBuilder st = new StringBuilder();
      st.append("INSERT INTO ");
      st.append(getTableName());
      st.append(" VALUES(?, ?, ?, ?, ?, ?)");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      pstmt.setString(1, name);
      pstmt.setInt(2, convertToSmallInt(isAlert));
      pstmt.setFloat(3, (float) weight);
      pstmt.setFloat(4, (float) distanceMax);
      if (file != null && file.isFile()) {
        pstmt.setString(5, file.getAbsolutePath());
      }
      else {
        pstmt.setString(5, null);
      }
      pstmt.setInt(6, convertToSmallInt(isDefault));

      pstmt.executeUpdate();
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
  }

  /**
   * Suppression d'un &eacute;quipment.
   * 
   * @param data
   * @throws SQLException
   */
  public boolean delete(DataEquipement data) throws SQLException {
    if (data == null) {
      throw new IllegalArgumentException();
    }
    return delete(data.getName());
  }

  /**
   * Suppression d'un &eacute;quipment.
   * 
   * @param equipement
   * @throws SQLException
   */
  public boolean delete(String name) throws SQLException {
    log.debug(">>delete name=" + name);

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
      st.append(" WHERE name = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, name);

      int res = pstmt.executeUpdate();
      if (res > 0) {
        bRes = true;
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

    log.debug("<<delete bRes=" + bRes);
    return bRes;
  }

  /**
   * Suppression des &eacute;quipements.
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
   * Restitue la distance parcouru par en km.
   * 
   * @param nom
   *          de l'&eacute;quipement
   * @throws SQLException
   */
  public float distance(String name) throws SQLException {
    log.debug(">>kilometre name=" + name);

    float res = 0;

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT SUM(");
      st.append(DatabaseManager.TABLE_RUN_LAP);
      st.append('.');
      st.append("total_dist)");
      st.append(" FROM ");
      st.append(DatabaseManager.TABLE_RUN);
      st.append(", ");
      st.append(DatabaseManager.TABLE_RUN_LAP);
      st.append(" WHERE ");
      st.append(DatabaseManager.TABLE_RUN);
      st.append(".equipement=? AND ");
      st.append(DatabaseManager.TABLE_RUN);
      st.append(".id");
      st.append('=');
      st.append(DatabaseManager.TABLE_RUN_LAP);
      st.append(".id");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, name);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        res = rs.getFloat(1) / 1000;
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<kilometre =" + res);
    return res;
  }

  /**
   * Restitue la date de premi&agrave;re utilisation.
   * 
   * @param nom
   *          de l'&eacute;quipement
   * @throws SQLException
   */
  public Date firstUsed(String name) throws SQLException {
    return used(name, true);
  }

  /**
   * Restitue la date de premi&agrave;re utilisation.
   * 
   * @param nom
   *          de l'&eacute;quipement
   * @throws SQLException
   */
  public Date lastUsed(String name) throws SQLException {
    return used(name, false);
  }

  /**
   * Restitue la date de premi&agrave;re utilisation.
   * 
   * @param nom
   *          de l'&eacute;quipement
   * @throws SQLException
   */
  public Date used(String name, boolean isFirst) throws SQLException {
    log.debug(">>used name=" + name);

    Date res = null;

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT ");
      st.append(DatabaseManager.TABLE_RUN_LAP);
      st.append('.');
      st.append("start_time");
      st.append(" FROM ");
      st.append(DatabaseManager.TABLE_RUN);
      st.append(", ");
      st.append(DatabaseManager.TABLE_RUN_LAP);
      st.append(" WHERE ");
      st.append(DatabaseManager.TABLE_RUN);
      st.append(".equipement=? AND ");
      st.append(DatabaseManager.TABLE_RUN);
      st.append(".id");
      st.append('=');
      st.append(DatabaseManager.TABLE_RUN_LAP);
      st.append(".id");
      st.append(" ORDER BY ");
      st.append(DatabaseManager.TABLE_RUN_LAP);
      st.append('.');
      st.append("start_time ");
      st.append((isFirst) ? "ASC" : "DESC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, name);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        res = rs.getTimestamp(1);
      }
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<lastUsed =" + res);
    return res;
  }

}
