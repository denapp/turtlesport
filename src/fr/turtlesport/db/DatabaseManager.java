package fr.turtlesport.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.derby.jdbc.EmbeddedDataSource;

import fr.turtlesport.Configuration;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JSplashScreen;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public final class DatabaseManager {
  private static TurtleLogger            log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(DatabaseManager.class);
  }

  /* Nom de la DB. */
  private static final String            DB_NAME             = "turtlesportDB";

  private static EmbeddedDataSource      ds;

  protected static final String          TABLE_RUN           = "APP.RUN";

  protected static final String          TABLE_RUN_LAP       = "APP.RUNLAP";

  protected static final String          TABLE_RUN_TRK       = "APP.RUNTRK";

  protected static final String          TABLE_USER          = "APP.TURTLEUSER";

  protected static final String          TABLE_EQUIPEMENT    = "APP.EQUIPEMENT";

  protected static final String          TABLE_USER_ACTIVITY = "APP.TURTLEUSERACTIVITY";

  // We want to keep the same connection for a given thread
  // as long as we're in the same transaction
  private static ThreadLocal<Connection> tranConnection      = new ThreadLocal<Connection>();

  private static boolean                 isInit              = false;

  private static boolean                 isNeedCreateIndex   = false;

  private DatabaseManager() {
  }

  /**
   * Initialisation de la database.
   * 
   * @param isDropTables
   * @throws SQLException
   */
  public static synchronized void initDatabase(boolean isDropTables) throws SQLException {

    log.debug(">>initDatabase");

    String derbyHome = Location.userLocation();
    log.info("derby.system.home=" + derbyHome);
    System.setProperty("derby.system.home", derbyHome);

    // Creation de la database.
    ds = new EmbeddedDataSource();
    ds.setDatabaseName(DB_NAME);
    // ds.setUser(user);
    // ds.setPassword(password);
    ds.setCreateDatabase("create");

    if (isDropTables) {
      dropTables();
    }

    isInit = true;
    createTables(null);

    log.debug("<<initDatabase");
  }

  /**
   * Initialisation de la database.
   * 
   * @param isDropTables
   * @throws SQLException
   */
  public static synchronized void initDatabase(JSplashScreen splash) throws SQLException {

    log.debug(">>initDatabase");

    String derbyHome = Location.userLocation();
    log.info("derby.system.home=" + derbyHome);
    System.setProperty("derby.system.home", derbyHome);

    // Creation de la database.
    ds = new EmbeddedDataSource();
    ds.setDatabaseName(DB_NAME);
    // ds.setUser(user);
    // ds.setPassword(password);
    ds.setCreateDatabase("create");

    isInit = true;
    createTables(splash);

    log.debug("<<initDatabase");
  }

  /**
   * Transaction.
   * 
   * @throws SQLException
   */
  public static synchronized void beginTransaction() throws SQLException {
    log.debug(">>beginTransaction");

    checkInit();

    if (tranConnection.get() != null) {
      log.warn("This thread is already in a transaction");
      // throw new SQLException("This thread is already in a transaction");
      return;
    }
    Connection conn = getConnection();
    conn.setAutoCommit(false);
    tranConnection.set(conn);

    log.debug("<<beginTransaction");
  }

  /**
   * D&eacte;termine si une transaction est d&eaute;marr&eaute;.
   * 
   * @throws SQLException
   */
  public static synchronized boolean isInTransaction() {
    return (tranConnection.get() != null);
  }

  /**
   * Commit.
   * 
   * @throws SQLException
   */
  public static void commitTransaction() throws SQLException {
    log.debug(">>commitTransaction");

    if (tranConnection.get() == null) {
      throw new SQLException("Can't commit: this thread isn't currently in a "
                             + "transaction");
    }
    tranConnection.get().commit();
    tranConnection.set(null);

    log.debug("<<commitTransaction");
  }

  /**
   * Rollback.
   * 
   * @throws SQLException
   */
  public static void rollbackTransaction() throws SQLException {
    log.debug(">>rollbackTransaction");

    if (tranConnection.get() == null) {
      throw new SQLException("Can't rollback: this thread isn't currently in a "
                             + "transaction");
    }
    tranConnection.get().rollback();
    tranConnection.set(null);

    log.debug("<<rollbackTransaction");
  }

  /**
   * Recup&eacute; une connection.
   * 
   * @return la connection.
   * @throws SQLException
   */
  public static Connection getConnection() throws SQLException {
    checkInit();
    if (tranConnection.get() != null) {
      return tranConnection.get();
    }
    return ds.getConnection();
  }

  /**
   * Release d'une connection.
   * 
   * @param conn
   *          la connection
   * @throws SQLException
   */
  public static void releaseConnection(Connection conn) throws SQLException {
    // We don't close the connection while we're in a transaction,
    // as it needs to be used by others in the same transaction context
    if (tranConnection.get() == null) {
      conn.close();
    }
  }

  /**
   * Drop les tables.
   * 
   * @throws SQLException
   */
  private static void dropTables() throws SQLException {
    try {
      executeUpdate("DROP TABLE " + TABLE_RUN);
      executeUpdate("DROP TABLE " + TABLE_RUN_LAP);
      executeUpdate("DROP TABLE " + TABLE_RUN_TRK);
      executeUpdate("DROP TABLE " + TABLE_EQUIPEMENT);
      executeUpdate("DROP TABLE " + TABLE_USER);

      executeUpdate("DROP TABLE " + TABLE_USER_ACTIVITY);
      executeUpdate("DROP INDEX TABLE_USER_ACTIVITY_index1");
    }
    catch (SQLException sqle) {
      if (!tableDoesntExist(sqle.getSQLState())) {
        throw sqle;
      }
    }
  }

  /**
   * Vide les tables.
   * 
   * @throws SQLException
   */
  public static void clearTables() throws SQLException {
    checkInit();

    Connection conn = getConnection();
    try {
      executeUpdate("DELETE FROM " + TABLE_RUN);
      executeUpdate("DELETE FROM " + TABLE_RUN_LAP);
      executeUpdate("DELETE FROM " + TABLE_RUN_TRK);
      executeUpdate("DELETE FROM " + TABLE_USER);
      executeUpdate("DELETE FROM " + TABLE_EQUIPEMENT);
      executeUpdate("DELETE FROM " + TABLE_USER_ACTIVITY);
    }
    finally {
      releaseConnection(conn);
    }
  }

  /**
   * Executes the SQL statement.
   * 
   * @param statement
   *          le statement.
   * @return
   * @throws SQLException
   */
  public static int executeUpdate(String statement) throws SQLException {
    Connection conn = getConnection();
    try {
      PreparedStatement ps = conn.prepareStatement(statement);
      return ps.executeUpdate();
    }
    finally {
      releaseConnection(conn);
    }
  }

  /**
   * Executes the SQL query statement.
   * 
   * @param conn
   *          la connection.
   * @param statement
   *          le query statement.
   * @return
   * @throws SQLException
   */
  public static ResultSet executeQueryNoParams(Connection conn, String statement) throws SQLException {
    PreparedStatement ps = conn.prepareStatement(statement);
    return ps.executeQuery();
  }

  /**
   * Determine si la table existe.
   * 
   * @throws SQLException
   */
  private static boolean tableExists(String tableName) throws SQLException {
    Connection conn = getConnection();
    ResultSet rs;
    boolean bExist;

    try {
      int index = tableName.indexOf('.');
      String schemaPattern = tableName.substring(0, index);
      String tableNamePattern = tableName.substring(index + 1);

      DatabaseMetaData md = conn.getMetaData();
      rs = md.getTables(null, schemaPattern, tableNamePattern, null);
      bExist = rs.next();
    }
    finally {
      releaseConnection(conn);
    }

    return bExist;
  }

  /**
   * Creation des tables
   */
  private static void createTables(JSplashScreen splash) throws SQLException {
    log.debug(">>createTables");

    // TABLE_USER
    createTableUser();

    // TABLE_RUN_LAP
    createTableLap();

    // TABLE_RUN_TRK
    createTableTrk();

    // TABLE_RUN
    createTableRun(splash);

    // TABLE_EQUIPEMENT
    createTableEquipement();

    // TABLE_USER_ZONES
    createTableUserActivity();

    log.debug("<<createTables");
  }

  private static void createTableRun(JSplashScreen splash) throws SQLException {
    ArrayList<DataRun> listData = null;

    int idUser = -1;

    if (tableExists(TABLE_RUN)) {
      Connection conn = getConnection();
      try {
        ResultSet rs;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM "
                                                        + TABLE_RUN);
        rs = pstmt.executeQuery();

        if (rs.getMetaData().getColumnCount() != 8) {
          // table existe dans une ancienne version ( < à 0.1.13)
          while (rs.next()) {
            if (listData == null) {
              listData = new ArrayList<DataRun>();
            }
            DataRun r = new DataRun();
            r.id = rs.getInt("id");
            r.sport_type = rs.getInt("sport_type");
            r.program_type = rs.getInt("program_type");
            r.multisport = rs.getInt("multisport");
            r.start_time = rs.getTimestamp("start_time");
            r.comments = rs.getString("comments");
            r.equipement = rs.getString("equipement");
            listData.add(r);
          }
          releaseConnection(conn);
          conn = null;

          executeUpdate("DROP TABLE " + TABLE_RUN);
        }
        else {
          // la table existe
          return;
        }
      }
      finally {
        if (conn != null) {
          releaseConnection(conn);
        }
      }
    }

    log.info("createTableRun");

    StringBuilder st = new StringBuilder();
    st.append("CREATE TABLE ");
    st.append(TABLE_RUN);
    st.append('(');
    st.append("id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, ");
    st.append("id_user INT, ");
    st.append("sport_type SMALLINT, ");
    st.append("program_type SMALLINT, ");
    st.append("multisport INTEGER, ");
    st.append("start_time TIMESTAMP, ");
    st.append("comments VARCHAR(100), ");
    st.append("equipement VARCHAR(50)");
    st.append(')');

    executeUpdate(st.toString());

    if (listData != null) {
      Connection conn = DatabaseManager.getConnection();
      try {
        // recuperation de l utilisateur
        ResultSet rs;
        PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM "
                                                        + TABLE_USER);
        rs = pstmt.executeQuery();
        if (rs.next()) {
          idUser = rs.getInt("id");
        }

        // Run, lap, trk
        st = new StringBuilder();
        st.append("INSERT INTO ");
        st.append(TABLE_RUN);
        st.append("(id_user,");
        st.append(" sport_type,");
        st.append(" program_type,");
        st.append(" multisport,");
        st.append(" start_time,");
        st.append(" comments,");
        st.append(" equipement)");
        st.append("VALUES(?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement pstmt0 = conn.prepareStatement(st.toString());

        st = new StringBuilder();
        st.append("SELECT id FROM ");
        st.append(TABLE_RUN);
        st.append(" ORDER BY id DESC");
        PreparedStatement pstmt1 = conn.prepareStatement(st.toString());

        st = new StringBuilder();
        st.append("UPDATE ");
        st.append(TABLE_RUN_LAP);
        st.append(" SET id=?");
        st.append(" WHERE id = ?");
        PreparedStatement pstmt2 = conn.prepareStatement(st.toString());

        st = new StringBuilder();
        st.append("UPDATE ");
        st.append(TABLE_RUN_TRK);
        st.append(" SET id=?");
        st.append(" WHERE id = ?");
        PreparedStatement pstmt3 = conn.prepareStatement(st.toString());

        if (splash != null) {
          splash.setIndeterminate(false);
          splash.setMaximum(listData.size());
          splash.setMinimum(0);
        }

        for (int i = 0; i < listData.size(); i++) {
          DataRun dr = listData.get(i);
          if (splash != null) {
            splash.setValue(i);
          }
          // creation du run
          pstmt0.setInt(1, idUser);
          pstmt0.setInt(2, dr.sport_type);
          pstmt0.setInt(3, dr.program_type);
          pstmt0.setInt(4, dr.multisport);
          pstmt0.setTimestamp(5, dr.start_time);
          pstmt0.setString(6, dr.comments);
          pstmt0.setString(7, dr.equipement);
          pstmt0.executeUpdate();

          // Recuperation de l'id
          rs = pstmt1.executeQuery();
          rs.next();
          int id = rs.getInt(1);

          // mis a jour des laps
          pstmt2.setInt(1, id);
          pstmt2.setInt(2, dr.id);
          pstmt2.executeUpdate();

          // mis a jour des trks
          pstmt3.setInt(1, id);
          pstmt3.setInt(2, dr.id);
          pstmt3.executeUpdate();
        }
      }
      finally {
        releaseConnection(conn);
      }
    }
  }

  private static void createTableLap() throws SQLException {
    if (tableExists(TABLE_RUN_LAP)) {
      StringBuilder st = new StringBuilder();
      if (isNeedCreateIndex) {
        st.append("CREATE INDEX ");
        st.append("TABLE_RUN_LAP_index1");
        st.append(" ON ");
        st.append(TABLE_RUN_LAP);
        st.append("(id)");
        executeUpdate(st.toString());
      }
      return;
    }

    log.info("createTableLap");

    StringBuilder st = new StringBuilder();
    st.append("CREATE TABLE ");
    st.append(TABLE_RUN_LAP);
    st.append('(');
    st.append("id INT, ");
    st.append("lap_index SMALLINT, ");
    st.append("start_time TIMESTAMP, ");
    st.append("total_time INTEGER, ");
    st.append("total_dist FLOAT, ");
    st.append("max_speed FLOAT, ");
    st.append("calories SMALLINT, ");
    st.append("avg_heart_rate SMALLINT, ");
    st.append("max_heart_rate SMALLINT");
    st.append(')');
    executeUpdate(st.toString());

    st = new StringBuilder();
    st.append("CREATE INDEX ");
    st.append("TABLE_RUN_LAP_index1");
    st.append(" ON ");
    st.append(TABLE_RUN_LAP);
    st.append("(id)");
    executeUpdate(st.toString());
  }

  private static void createTableTrk() throws SQLException {
    if (tableExists(TABLE_RUN_TRK)) {
      if (isNeedCreateIndex) {
        StringBuilder st = new StringBuilder();
        st.append("CREATE INDEX ");
        st.append("TABLE_RUN_TRK_index1");
        st.append(" ON ");
        st.append(TABLE_RUN_TRK);
        st.append("(id)");
        executeUpdate(st.toString());
      }
      return;
    }

    log.info("createTableTrk");

    StringBuilder st = new StringBuilder();
    st.append("CREATE TABLE ");
    st.append(TABLE_RUN_TRK);
    st.append('(');
    st.append("id INT, ");
    st.append("latitude INT, ");
    st.append("longitude INT, ");
    st.append("time TIMESTAMP, ");
    st.append("altitude FLOAT, ");
    st.append("distance FLOAT, ");
    st.append("heart_rate SMALLINT, ");
    st.append("cadence SMALLINT");
    st.append(')');
    executeUpdate(st.toString());

    st = new StringBuilder();
    st.append("CREATE INDEX ");
    st.append("TABLE_RUN_TRK_index1");
    st.append(" ON ");
    st.append(TABLE_RUN_TRK);
    st.append("(id)");
    executeUpdate(st.toString());
  }

  private static void createTableEquipement() throws SQLException {
    // depuis la version 1.10.4 les equipements ne sont plus rattaches a un
    // sport
    // la table change
    ArrayList<DataEquipement> listData = null;

    if (tableExists(TABLE_EQUIPEMENT)) {
      Connection conn = getConnection();
      try {
        ResultSet rs;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM "
                                                        + TABLE_EQUIPEMENT);
        rs = pstmt.executeQuery();

        if (rs.getMetaData().getColumnCount() != 6) {
          // table existe dans une ancienne version ( <= à 0.1.13)
          boolean hasDefault = false;
          while (rs.next()) {
            if (listData == null) {
              listData = new ArrayList<DataEquipement>();
            }
            DataEquipement de = new DataEquipement();
            de.name = rs.getString("name");
            de.alert = rs.getInt("alert");
            de.weight = rs.getFloat("distanceMax");
            de.distanceMax = rs.getFloat("distanceMax");
            de.image_path = rs.getString("image_path");
            if (hasDefault) {
              de.default_equipement = AbstractTableManager
                  .convertToSmallInt(false);
            }
            else {
              de.default_equipement = rs.getInt("default_equipement");
              hasDefault = AbstractTableManager
                  .convertToBoolean(de.default_equipement);
            }
            listData.add(de);
          }
          releaseConnection(conn);
          conn = null;

          try {
            executeUpdate("DROP INDEX TABLE_EQUIPEMENT_index1");
          }
          catch (Throwable e) {
            log.error("", e);
          }
          executeUpdate("DROP TABLE " + TABLE_EQUIPEMENT);
        }
        else {
          // la table existe
          return;
        }
      }
      finally {
        if (conn != null) {
          releaseConnection(conn);
        }
      }
    }

    log.info("createTableEquipement");

    StringBuilder st = new StringBuilder();
    st.append("CREATE TABLE ");
    st.append(TABLE_EQUIPEMENT);
    st.append('(');
    st.append("name VARCHAR(50), ");
    st.append("alert SMALLINT, ");
    st.append("weight FLOAT, ");
    st.append("distanceMax FLOAT, ");
    st.append("image_path VARCHAR(500), ");
    st.append("default_equipement SMALLINT, ");
    st.append("PRIMARY KEY (name)");
    st.append(')');

    executeUpdate(st.toString());

    if (listData != null) {
      Connection conn = DatabaseManager.getConnection();
      try {
        st = new StringBuilder();
        st.append("INSERT INTO ");
        st.append(TABLE_EQUIPEMENT);
        st.append(" VALUES(?, ?, ?, ?, ?, ?)");

        PreparedStatement pstmt = conn.prepareStatement(st.toString());
        for (DataEquipement de : listData) {
          pstmt.setString(1, de.name);
          pstmt.setInt(2, de.alert);
          pstmt.setFloat(3, de.weight);
          pstmt.setFloat(4, de.distanceMax);
          pstmt.setString(5, de.image_path);
          pstmt.setInt(6, de.default_equipement);
          pstmt.executeUpdate();
        }
      }
      finally {
        releaseConnection(conn);
      }
    }

  }

  private static void createTableUser() throws SQLException {
    DataUser du = null;
    if (tableExists(TABLE_USER)) {
      Connection conn = getConnection();
      try {
        ResultSet rs;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM "
                                                        + TABLE_USER);
        rs = pstmt.executeQuery();

        if (rs.getMetaData().getColumnCount() != 8) {
          // table existe dans une ancienne version ( < à 0.1.13)
          // nouvelle version avec creation index
          isNeedCreateIndex = true;
          if (rs.next()) {
            du = new DataUser();
            du.first_name = rs.getString("first_name");
            du.last_name = rs.getString("last_name");
            du.sexe = rs.getInt("sexe");
            du.birthdate = rs.getDate("birthdate");
            du.weight = rs.getFloat("weight");
            du.size = rs.getInt("size");
            du.image_path = rs.getString("image_path");
          }
          releaseConnection(conn);
          conn = null;
          executeUpdate("DROP TABLE " + TABLE_USER);
        }
        else {
          // la table existe
          return;
        }
      }
      finally {
        if (conn != null) {
          releaseConnection(conn);
        }
      }
    }

    log.info("createTableUser");

    StringBuilder st = new StringBuilder();
    st.append("CREATE TABLE ");
    st.append(TABLE_USER);
    st.append('(');
    st.append("id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, ");
    st.append("first_name VARCHAR(100), ");
    st.append("last_name VARCHAR(100), ");
    st.append("sexe SMALLINT, ");
    st.append("birthdate DATE, ");
    st.append("weight FLOAT, ");
    st.append("size SMALLINT, ");
    st.append("image_path VARCHAR(500)");
    st.append(')');

    executeUpdate(st.toString());

    if (du != null) {
      Connection conn = DatabaseManager.getConnection();

      try {
        st = new StringBuilder();
        st.append("INSERT INTO ");
        st.append(TABLE_USER);
        st.append("(first_name,");
        st.append(" last_name,");
        st.append(" sexe,");
        st.append(" birthdate,");
        st.append(" weight,");
        st.append(" size,");
        st.append(" image_path)");
        st.append("VALUES(?, ?, ?, ?, ?, ?, ?)");

        PreparedStatement pstmt = conn.prepareStatement(st.toString());

        pstmt.setString(1, du.first_name);
        pstmt.setString(2, du.last_name);
        pstmt.setInt(3, du.sexe);
        pstmt.setDate(4, du.birthdate);
        pstmt.setFloat(5, du.weight);
        pstmt.setInt(6, du.size);
        pstmt.setString(7, du.image_path);
        pstmt.executeUpdate();
      }
      finally {
        releaseConnection(conn);
      }
    }
  }

  private static void createTableUserActivity() throws SQLException {
    // depuis la version 1.10.4 on peut ajouter des axtivites
    // la table change
    ArrayList<DataUserActivity> listData = null;

    if (tableExists(TABLE_USER_ACTIVITY)) {
      Connection conn = getConnection();
      try {
        ResultSet rs;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM "
                                                        + TABLE_USER_ACTIVITY);
        rs = pstmt.executeQuery();

        if (rs.getMetaData().getColumnCount() != 12) {
          // table existe dans une ancienne version ( < à 0.1.13)
          while (rs.next()) {
            if (listData == null) {
              listData = new ArrayList<DataUserActivity>();
            }
            DataUserActivity dua = new DataUserActivity();
            dua.sport_type = rs.getInt("sport_type");
            dua.max_heart_rate = rs.getInt("max_heart_rate");
            dua.id_zone_heart = rs.getInt("id_zone_heart");
            dua.low_heart_rate = rs.getInt("low_heart_rate");
            dua.high_heart_rate = rs.getInt("high_heart_rate");
            dua.id_speed = rs.getInt("id_speed");
            dua.speed_name = rs.getString("speed_name");
            dua.low_speed = rs.getFloat("low_speed");
            dua.high_speed = rs.getFloat("high_speed");
            dua.unit = rs.getString("unit");
            listData.add(dua);
          }
          releaseConnection(conn);
          conn = null;
          executeUpdate("DROP TABLE " + TABLE_USER_ACTIVITY);
        }
        else {
          // la table existe
          return;
        }
      }
      finally {
        if (conn != null) {
          releaseConnection(conn);
        }
      }
    }

    log.info("createTableUser");

    StringBuilder st = new StringBuilder();
    st.append("CREATE TABLE ");
    st.append(TABLE_USER_ACTIVITY);
    st.append('(');
    st.append("sport_type SMALLINT, ");
    st.append("max_heart_rate SMALLINT, ");
    st.append("id_zone_heart SMALLINT, ");
    st.append("low_heart_rate SMALLINT, ");
    st.append("high_heart_rate SMALLINT, ");
    st.append("id_speed SMALLINT, ");
    st.append("speed_name VARCHAR(16), ");
    st.append("low_speed FLOAT, ");
    st.append("high_speed FLOAT,");
    st.append("unit VARCHAR(10),");
    st.append("name VARCHAR(100),");
    st.append("default_sport SMALLINT");
    st.append(')');

    executeUpdate(st.toString());

    st = new StringBuilder();
    st.append("CREATE INDEX ");
    st.append("TABLE_USER_ACTIVITY_index1");
    st.append(" ON ");
    st.append(TABLE_USER_ACTIVITY);
    st.append("(sport_type)");

    executeUpdate(st.toString());

    if (listData != null) {
      Connection conn = DatabaseManager.getConnection();

      try {
        // recuperation du sport par defaut dans la version < 0.1.12
        int sportType = -1;
        String value = Configuration.getConfig().getProperty("user",
                                                             "defaultSport");
        if (value != null) {
          try {
            sportType = Integer.parseInt(value);
          }
          catch (NumberFormatException e) {
          }
        }

        st = new StringBuilder();
        st.append("INSERT INTO ");
        st.append(TABLE_USER_ACTIVITY);
        st.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");

        PreparedStatement pstmt = conn.prepareStatement(st.toString());

        for (DataUserActivity dua : listData) {
          pstmt.setInt(1, dua.sport_type);
          pstmt.setInt(2, dua.max_heart_rate);
          pstmt.setInt(3, dua.id_zone_heart);
          pstmt.setInt(4, dua.low_heart_rate);
          pstmt.setInt(5, dua.high_heart_rate);
          pstmt.setInt(6, dua.id_speed);
          pstmt.setString(7, dua.speed_name);
          pstmt.setFloat(8, dua.low_speed);
          pstmt.setFloat(9, dua.high_speed);
          pstmt.setString(10, dua.unit);
          pstmt.setString(11, null);
          pstmt.setBoolean(12, dua.sport_type == sportType);
          pstmt.executeUpdate();
        }
      }
      finally {
        releaseConnection(conn);
      }

    }
  }

  /**
   * Determine si la table existe.
   */
  private static boolean tableDoesntExist(String sqlState) {
    return sqlState.equals("42X05") || sqlState.equals("42Y55");
  }

  private static void checkInit() throws SQLException {
    if (!isInit) {
      synchronized (DatabaseManager.class) {
        isInit = true;
        initDatabase(false);
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private static class DataUserActivity {
    int    sport_type;

    int    max_heart_rate;

    int    id_zone_heart;

    int    low_heart_rate;

    int    high_heart_rate;

    int    id_speed;

    String speed_name;

    float  low_speed;

    float  high_speed;

    String unit;

    protected DataUserActivity() {
      super();
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private static class DataEquipement {
    String name;

    int    alert;

    float  weight;

    float  distanceMax;

    String image_path;

    int    default_equipement;

    protected DataEquipement() {
      super();
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private static class DataRun {
    int       id;

    int       id_user;

    int       sport_type;

    int       program_type;

    int       multisport;

    Timestamp start_time;

    String    comments;

    String    equipement;
  }

  private static class DataUser {
    int    id;

    String first_name;

    String last_name;

    int    sexe;

    Date   birthdate;

    float  weight;

    int    size;

    String image_path;
  }

}
