package fr.turtlesport.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.log.TurtleLogger;

/**
 * Table utilsateur.
 * 
 * @author denis
 * 
 */
public final class UserTableManager extends AbstractTableManager {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(UserTableManager.class);
  }

  private static UserTableManager singleton = new UserTableManager();

  /**
   * 
   */
  private UserTableManager() {
    super();
  }

  /**
   * Restitue une instance unique.
   */
  public static UserTableManager getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractTableManager#getTableName()
   */
  @Override
  public String getTableName() {
    return DatabaseManager.TABLE_USER;
  }

  /**
   * Ajoute des utilisateurs.
   * 
   * @param list
   *          les utilisateurs.
   * @throws SQLException
   */
  public void store(List<DataUser> list) throws SQLException {
    if (list == null || list.size() == 0) {
      return;
    }

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    // recuperation de toutes les lignes et suppression des lignes non trouves.
    List<DataUser> listOld = retreive();

    for (DataUser u1 : listOld) {
      boolean bFound = false;
      for (DataUser u2 : list) {
        if (u1.getId() == u2.getId()) {
          bFound = true;
          break;
        }
      }
      // si non trouve on supprime
      if (!bFound) {
        // suppression de l'utilisateur
        delete(u1.getId());
        // suppression des run de l utilisateur
        RunTableManager.getInstance().delete(u1);
      }
    }

    // sauvegarde
    for (DataUser u : list) {
      storeOrUpdate(u);
    }

    if (!isInTransaction) {
      DatabaseManager.commitTransaction();
    }

  }

  /**
   * Ajoute un utilisateur.
   * 
   * @param data
   *          un utilisateur.
   * @throws SQLException
   */
  public void store(DataUser data) throws SQLException {
    log.debug(">>store");

    if (data == null) {
      throw new IllegalArgumentException();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("INSERT INTO ");
      st.append(getTableName());
      st.append("(first_name,");
      st.append(" last_name,");
      st.append(" sexe,");
      st.append(" birthdate,");
      st.append(" weight,");
      st.append(" size,");
      st.append(" image_path)");
      st.append(" VALUES(?, ?, ?, ?, ?, ?, ?)");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      pstmt.setString(1, data.getFirstName());
      pstmt.setString(2, data.getLastName());
      pstmt.setInt(3, convertToSmallInt(data.isMale()));
      pstmt.setDate(4, (data.getBirthDate() == null) ? null
          : new java.sql.Date(data.getBirthDate().getTime()));
      pstmt.setFloat(5, data.getWeight());
      pstmt.setInt(6, (int) data.getHeight());
      pstmt.setString(7, data.getPath());

      pstmt.executeUpdate();
      pstmt.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<store");
  }

  /**
   * Ajoute un utilisateur.
   * 
   * @param data
   *          un utilisateur.
   * @throws SQLException
   */
  public void update(DataUser data) throws SQLException {
    log.debug(">>update");

    if (data == null) {
      throw new IllegalArgumentException();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET sexe=?, birthdate=?, weight=?, size=?, image_path=?");
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      pstmt.setInt(1, convertToSmallInt(data.isMale()));
      pstmt.setDate(2, (data.getBirthDate() == null) ? null
          : new java.sql.Date(data.getBirthDate().getTime()));
      pstmt.setFloat(3, data.getWeight());
      pstmt.setInt(4, (int) data.getHeight());
      pstmt.setString(5, data.getPath());
      pstmt.setInt(6, data.getId());

      pstmt.executeUpdate();
      pstmt.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<store");
  }

  /**
   * D&eacute;termine si cet utilisateur existe.
   * 
   * @param idUser
   *          id de l'utilisateur
   * @return <code>true</code> si l'utilisateur,<code>false</code> sinon.
   */
  public boolean exist(int idUser) throws SQLException {
    log.debug(">>exist idUser=" + idUser);

    boolean isExist = false;

    Connection conn = DatabaseManager.getConnection();
    try {
      // Insertion du nouvel equipement
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idUser);

      ResultSet rs = pstmt.executeQuery();
      isExist = rs.next();

      pstmt.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<exist");
    return isExist;
  }

  /**
   * Restitue les utilisateurs.
   * 
   * @return
   */
  public List<DataUser> retreive() throws SQLException {
    log.debug(">>retreive");

    List<DataUser> list = new ArrayList<DataUser>();

    Connection conn = DatabaseManager.getConnection();

    try {
      // Insertion du nouvel equipement
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" ORDER BY last_name ASC, first_name ASC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        DataUser data = new DataUser();

        data.setId(rs.getInt(1));
        data.setFirstName(rs.getString(2));
        data.setLastName(rs.getString(3));
        data.setMale(convertToBoolean(rs.getInt(4)));
        data.setBirthDate(rs.getDate(5));
        data.setWeight(rs.getFloat(6));
        data.setHeight(rs.getInt(7));
        data.setPath(rs.getString(8));

        list.add(data);
      }

      pstmt.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreive");
    return list;
  }

  /**
   * Insertion ou mis &agrave; jour de l'utilisateur.
   * 
   * @param data
   *          les donn&eacute; de l'utilisateur.
   * @throws SQLException
   */
  public void storeOrUpdate(DataUser data) throws SQLException {
    log.debug(">>storeOrUpdate");

    if (data == null) {
      throw new IllegalArgumentException();
    }

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      if (data.getId() != -1) {
        update(data);
      }
      else {
        store(data);
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
    log.debug("<<storeOrUpdate");
  }

  /**
   * Suppression des utilisateurs.
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
      StringBuilder st = new StringBuilder();
      st.append("DELETE FROM ");
      st.append(getTableName());

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

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
   * Suppression des utilisateurs.
   * 
   * @param equipement
   * @throws SQLException
   */
  public boolean delete(int id) throws SQLException {
    log.debug(">>delete id");

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
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, id);

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

}
