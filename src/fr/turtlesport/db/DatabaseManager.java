package fr.turtlesport.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;

import fr.turtlesport.CantWriteIOException;
import fr.turtlesport.Configuration;
import fr.turtlesport.NotDirIOException;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JSplashScreen;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class DatabaseManager {
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

  protected static final String          TABLE_METEO         = "APP.RUNMETEO";

  // We want to keep the same connection for a given thread
  // as long as we're in the same transaction
  private static ThreadLocal<Connection> tranConnection      = new ThreadLocal<Connection>();

  private static boolean                 isInit              = false;

  private static boolean                 isNeedCreateIndex   = false;

  public DatabaseManager() {
  }

  /**
   * @return Restitue le repertoire par d&eacute;faut database.
   */
  public static String getDefaultDirectory() {
    return Location.userLocation();
  }

  /**
   * @return Restitue le repertoire de la database.
   */
  public static String getDirectory() {
    String dirName = Configuration.getConfig()
        .getProperty("configuration", "database", getDefaultDirectory());
    if (dirName.endsWith(File.separatorChar + DB_NAME)) {
      dirName.substring(0, dirName.lastIndexOf(File.separatorChar));
    }
    try {
      checkDir(new File(dirName));
    }
    catch (IOException e) {
      log.error("", e);
      dirName = Location.userLocation();
    }
    return dirName;
  }

  /**
   * Valorise le nouveau r&eacute;pertoire de la database.
   * 
   * @param Restitue
   *          le repertoire de la database.
   * @throws FileNotFoundException
   * @throws NotDirIOException
   * @throws CantWriteIOException
   */
  public static void setDirectory(File dir) throws FileNotFoundException,
                                           CantWriteIOException,
                                           NotDirIOException {
    checkDir(dir);

    String dirName = dir.getAbsolutePath();
    if (dirName.endsWith(File.separatorChar + DB_NAME)) {
      dirName.substring(0, dirName.lastIndexOf(File.separatorChar));
    }
    Configuration.getConfig().addProperty("configuration", "database", dirName);
  }

  /**
   * Copie la databse courante.
   * 
   * @param dir
   * @throws FileNotFoundException
   * @throws SQLException
   * @throws NotDirIOException
   * @throws CantWriteIOException
   */
  public static void backUpDatabase(File dir) throws FileNotFoundException,
                                             SQLException,
                                             CantWriteIOException,
                                             NotDirIOException {
    checkDir(dir);

    String backupdirectory = dir.getAbsolutePath();
    CallableStatement cs = getConnection()
        .prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
    cs.setString(1, backupdirectory);
    cs.execute();
    cs.close();
    log.warn("backed up database to " + dir);
  }

  private static void logStatements() {
    System.setProperty("derby.infolog.append", "true");
    System.setProperty("derby.language.logStatementText", "true");
    System.setProperty("derby.language.logQueryPlan", "true");
   }

  /**
   * Initialisation de la database.
   * 
   * @param isDropTables
   * @throws SQLException
   */
  public static synchronized void initDatabase(boolean isDropTables) throws SQLException {

    log.debug(">>initDatabase");

    String derbyHome = getDirectory();

    log.warn("derby.system.home=" + derbyHome);
    System.setProperty("derby.system.home", derbyHome);

    logJDBCSupportInVM();
    logDerbyVersion();

    // Creation de la database.
    ds = new EmbeddedDataSource();
    ds.setDatabaseName(DB_NAME);
    // ds.setUser(user);
    // ds.setPassword(password);
    ds.setCreateDatabase("create");

    loadDbVersion();

    if (isDropTables) {
      dropTables();
    }
    // executeUpdate("DROP TABLE " + TABLE_METEO);

    isInit = true;
    if (log.isDebugEnabled()) {
      logStatements();
    }

    // create function
    createFunctions();

    // create tables
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

    String derbyHome = getDirectory();
    log.warn("derby.system.home=" + derbyHome);
    System.setProperty("derby.system.home", derbyHome);

    logJDBCSupportInVM();
    logDerbyVersion();

    // Creation de la database.
    ds = new EmbeddedDataSource();
    ds.setDatabaseName(DB_NAME);
    // ds.setUser(user);
    // ds.setPassword(password);
    ds.setCreateDatabase("create");

    isInit = true;
    if (log.isDebugEnabled()) {
      logStatements();
    }

    loadDbVersion();

    createTables(splash);

    // create function
    createFunctions();

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

      executeUpdate("DROP TABLE " + TABLE_METEO);
      executeUpdate("DROP INDEX TABLE_METEO_index1");
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

  private static void logDerbyVersion() {
    // Connection conn = null;
    // try {
    EmbeddedDriver driver = new EmbeddedDriver();
    int majorVersion = driver.getMajorVersion();
    int minorVersion = driver.getMinorVersion();
    log.warn("Using " + majorVersion + "." + minorVersion);
  }

  private static void loadDbVersion() {
    Connection conn = null;
    try {
      conn = getConnection();
      DatabaseMetaData dbmd = conn.getMetaData();
      String productName = dbmd.getDatabaseProductName();
      String productVersion = dbmd.getDatabaseProductVersion();
      log.warn("Using " + productName + " " + productVersion);
    }
    catch (SQLException e) {
      log.error("", e);
    }
    finally {
      if (conn != null) {
        try {
          releaseConnection(conn);
        }
        catch (SQLException e) {
        }
      }
    }
  }

  private static void logJDBCSupportInVM() {
    /*
     * Check the availability of classes or interfaces introduced in or removed
     * from specific versions of JDBC-related specifications. This will give us
     * an indication of which JDBC version this Java VM is supporting.
     */
    if (haveClass("java.sql.SQLXML")) {
      log.warn("JDBC 4");
    }
    else if (haveClass("java.sql.Savepoint")) {
      // indication of JDBC 3 or JSR-169.
      // JSR-169 is a subset of JDBC 3 which does not include the
      // java.sql.Driver interface
      if (haveClass("java.sql.Driver")) {
        log.warn("JDBC 3");
      }
      else {
        log.warn("JSR-169");
      }
    }
    else if (haveClass("java.sql.Blob")) {
      // new in JDBC 2.0.
      // We already checked for JDBC 3.0, 4.0 and JSR-169, all of which also
      // include this class. Chances are good this is JDBC 2.x
      log.warn("JDBC 2");
    }
    else if (haveClass("java.sql.Connection")) {
      // included in most (all?) JDBC specs
      log.warn("Older than JDBC 2.0");
    }
    else {
      // JDBC support is missing (or is older than JDBC 1.0?)
      log.warn("No valid JDBC support found");
    }
  }

  /**
   * Checks whether or not we can load a specific class.
   * 
   * @param className
   *          Name of class to attempt to load.
   * @return true if class can be loaded, false otherwise.
   */
  private static boolean haveClass(String className) {
    try {
      Class.forName(className);
      return true;
    }
    catch (Exception e) {
      return false;
    }
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

    // TABLE_METEO
    createTableMeteo();

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

        if (rs.getMetaData().getColumnCount() < 8) {
          // table existe dans une ancienne version ( < 0.1.13)
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
        else if (rs.getMetaData().getColumnCount() == 8) {
          // la table existe
          if (rs.getMetaData().getColumnDisplaySize(7) != 500) {
            // on augmente la taille des commentaires
            releaseConnection(conn);
            conn = null;
            StringBuilder st = new StringBuilder();
            st.append("ALTER TABLE ");
            st.append(TABLE_RUN);
            st.append(" ALTER COLUMN comments SET DATA TYPE VARCHAR(500)");
            executeUpdate(st.toString());
          }
          // table existe dans une ancienne version ( = 1.1)
          // nouvelle version avec ajoiut colonne location
          if (conn != null) {
            releaseConnection(conn);
            conn = null;
          }
          StringBuilder st = new StringBuilder();
          st.append("ALTER TABLE ");
          st.append(TABLE_RUN);
          st.append(" ADD COLUMN location VARCHAR(100)");
          executeUpdate(st.toString());
          return;
        }
        else if (rs.getMetaData().getColumnCount() == 9) {
          
          // la table existe ajout produit montre
          if (conn != null) {
            releaseConnection(conn);
            conn = null;
          }
          StringBuilder st = new StringBuilder();
          st.append("ALTER TABLE ");
          st.append(TABLE_RUN);
          st.append(" ADD");
          st.append(" COLUMN product_id VARCHAR(8)");
          executeUpdate(st.toString());

          st = new StringBuilder();
          st.append("ALTER TABLE ");
          st.append(TABLE_RUN);
          st.append(" ADD");
          st.append(" COLUMN product_version VARCHAR(15)");
          executeUpdate(st.toString());

          st = new StringBuilder();
          st.append("ALTER TABLE ");
          st.append(TABLE_RUN);
          st.append(" ADD");
          st.append(" COLUMN product_name VARCHAR(25)");
          executeUpdate(st.toString());

          return;
        }
        else {
          // la table existe
          if (rs.getMetaData().getColumnDisplaySize(7) != 500) {
            // on augmente la taille des commentaires
            releaseConnection(conn);
            conn = null;
            StringBuilder st = new StringBuilder();
            st.append("ALTER TABLE ");
            st.append(TABLE_RUN);
            st.append(" ALTER COLUMN comments SET DATA TYPE VARCHAR(500)");
            executeUpdate(st.toString());
          }
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
    st.append("comments VARCHAR(500), ");
    st.append("equipement VARCHAR(50), ");
    st.append("location VARCHAR(100),");
    st.append("product_id VARCHAR(8),");
    st.append("product_version VARCHAR(15),");
    st.append("product_name VARCHAR(25)");

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

      // suppression des doublons
      Connection conn = DatabaseManager.getConnection();
      try {
        // suppression des doublons --> BUG database
        st = new StringBuilder();
        st.append("DELETE FROM ");
        st.append(TABLE_RUN_LAP);
        st.append(" T1");
        st.append(" WHERE  T1.LAP_INDEX < ANY");
        st.append(" (");
        st.append(" SELECT LAP_INDEX FROM ");
        st.append(TABLE_RUN_LAP);
        st.append(" T2");
        st.append(" WHERE  T1.LAP_INDEX <> T2.LAP_INDEX");
        st.append(" AND  T1.ID = T2.ID");
        st.append(" AND  T1.START_TIME = T2.START_TIME)");
        executeUpdate(st.toString());
      }
      finally {
        DatabaseManager.releaseConnection(conn);
      }

      conn = DatabaseManager.getConnection();
      try {
        ResultSet rs;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM "
                                                        + TABLE_RUN_LAP);
        rs = pstmt.executeQuery();

        if (rs.getMetaData().getColumnCount() != 10) {
          releaseConnection(conn);
          try {

            beginTransaction();

            // modification de la colonne calories de smallint vers int
            st = new StringBuilder();
            st.append("ALTER TABLE ");
            st.append(TABLE_RUN_LAP);
            st.append(" ADD COLUMN CALORIES_INT INTEGER");
            executeUpdate(st.toString());

            st = new StringBuilder();
            st.append("UPDATE ");
            st.append(TABLE_RUN_LAP);
            st.append(" SET CALORIES_INT = CALORIES");
            executeUpdate(st.toString());

            st = new StringBuilder();
            st.append("ALTER TABLE ");
            st.append(TABLE_RUN_LAP);
            st.append(" DROP COLUMN CALORIES");
            executeUpdate(st.toString());

            st = new StringBuilder();
            st.append("RENAME COLUMN ");
            st.append(TABLE_RUN_LAP);
            st.append(".CALORIES_INT TO CALORIES");
            executeUpdate(st.toString());

            // ajout d'une colonne total_moving_time
            st = new StringBuilder();
            st.append("ALTER TABLE ");
            st.append(TABLE_RUN_LAP);
            st.append(" ADD COLUMN total_moving_time INTEGER");
            executeUpdate(st.toString());

            commitTransaction();
          }
          catch (SQLException e) {
            rollbackTransaction();
            throw e;
          }
        }
      }
      finally {
        if (conn != null) {
          releaseConnection(conn);
        }
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
    st.append("avg_heart_rate SMALLINT, ");
    st.append("max_heart_rate SMALLINT, ");
    st.append("calories INTEGER, ");
    st.append("total_moving_time INTEGER");
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

  private static void createTableMeteo() throws SQLException {
    if (tableExists(TABLE_METEO)) {
      return;
    }

    log.info("createTableMeteo");

    StringBuilder st = new StringBuilder();
    st.append("CREATE TABLE ");
    st.append(TABLE_METEO);
    st.append('(');
    st.append("id INT, ");
    st.append("time TIMESTAMP, ");
    st.append("condition SMALLINT, ");
    st.append("temperature SMALLINT, ");
    st.append("humidity SMALLINT, ");
    st.append("wind_speed FLOAT, ");
    st.append("wind_dir VARCHAR(20), ");
    st.append("pression SMALLINT, ");
    st.append("visibility FLOAT, ");
    st.append("PRIMARY KEY (id)");
    st.append(')');
    executeUpdate(st.toString());

    st = new StringBuilder();
    st.append("CREATE INDEX ");
    st.append("TABLE_METEO_index1");
    st.append(" ON ");
    st.append(TABLE_METEO);
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

        switch (rs.getMetaData().getColumnCount()) {
          case 5:
            // table existe dans une ancienne version ( <= à 0.1.13)
            boolean hasDefault = false;
            while (rs.next()) {
              if (listData == null) {
                listData = new ArrayList<DataEquipement>();
              }
              DataEquipement de = new DataEquipement();
              de.name = rs.getString("name");
              de.alert = rs.getInt("alert");
              de.weight = rs.getFloat("weight");
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
            break;

          case 6:
            // table existe dans une ancienne version ( = 0.2)
            // nouvelle version avec creation index
            releaseConnection(conn);
            conn = null;
            StringBuilder st = new StringBuilder();
            st.append("ALTER TABLE ");
            st.append(TABLE_EQUIPEMENT);
            st.append(" ADD COLUMN distance_init SMALLINT");
            executeUpdate(st.toString());
            return;

          default:
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
    st.append("distance_init FLOAT, ");
    st.append("PRIMARY KEY (name)");
    st.append(')');

    executeUpdate(st.toString());

    if (listData != null) {
      Connection conn = DatabaseManager.getConnection();
      try {
        st = new StringBuilder();
        st.append("INSERT INTO ");
        st.append(TABLE_EQUIPEMENT);
        st.append(" VALUES(?, ?, ?, ?, ?, ?, ?)");

        PreparedStatement pstmt = conn.prepareStatement(st.toString());
        for (DataEquipement de : listData) {
          pstmt.setString(1, de.name);
          pstmt.setInt(2, de.alert);
          pstmt.setFloat(3, de.weight);
          pstmt.setFloat(4, de.distanceMax);
          pstmt.setString(5, de.image_path);
          pstmt.setInt(6, de.default_equipement);
          pstmt.setInt(7, 0);
          pstmt.executeUpdate();
        }
      }
      finally {
        releaseConnection(conn);
      }
    }

  }

  /**
   * A partir de la version 1.14 ajout de plusieurs utilisateur. Version 1.16 :
   * ajout d'une colonne pour le fq au repos
   * 
   * @throws SQLException
   */
  private static void createTableUser() throws SQLException {
    DataUser du = null;
    if (tableExists(TABLE_USER)) {
      Connection conn = getConnection();
      try {
        ResultSet rs;
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM "
                                                        + TABLE_USER);
        rs = pstmt.executeQuery();

        switch (rs.getMetaData().getColumnCount()) {
          case 7:
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
            break;

          case 8:
            // table existe dans une ancienne version ( >= 0.1.13 et < 0.1.16)
            // nouvelle version avec creation index
            releaseConnection(conn);
            conn = null;
            StringBuilder st = new StringBuilder();
            st.append("ALTER TABLE ");
            st.append(TABLE_USER);
            st.append(" ADD COLUMN min_heart_rate SMALLINT");
            executeUpdate(st.toString());
            return;

          default:
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
    st.append("image_path VARCHAR(500), ");
    st.append("min_heart_rate SMALLINT");
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
   * Creation des fonctions
   */
  private static void createFunctions() throws SQLException {

    // DayOfWeek
    StringBuilder st = new StringBuilder();
    try {
      executeUpdate("DROP FUNCTION  APP.dayOfWeek");
    }
    catch (SQLException e) {
    }
    st = new StringBuilder();
    st.append("create function APP.dayOfWeek(dateValue TIMESTAMP)");
    st.append(" RETURNS INTEGER");
    st.append(" LANGUAGE JAVA");
    st.append(" EXTERNAL NAME 'fr.turtlesport.db.DatabaseManager.dayOfWeek'");
    st.append(" PARAMETER STYLE java no sql");
    executeUpdate(st.toString());

    // Week
    st = new StringBuilder();
    try {
      executeUpdate("DROP FUNCTION  APP.Week");
    }
    catch (SQLException e) {
    }
    st = new StringBuilder();
    st.append("create function APP.Week(dateValue TIMESTAMP)");
    st.append(" RETURNS INTEGER");
    st.append(" LANGUAGE JAVA");
    st.append(" EXTERNAL NAME 'fr.turtlesport.db.DatabaseManager.week'");
    st.append(" PARAMETER STYLE java no sql");
    executeUpdate(st.toString());

    // Year Week
    st = new StringBuilder();
    try {
      executeUpdate("DROP FUNCTION  APP.YearWeek");
    }
    catch (SQLException e) {
    }
    st = new StringBuilder();
    st.append("create function APP.yearWeek(dateValue TIMESTAMP)");
    st.append(" RETURNS INTEGER");
    st.append(" LANGUAGE JAVA");
    st.append(" EXTERNAL NAME 'fr.turtlesport.db.DatabaseManager.yearweek'");
    st.append(" PARAMETER STYLE java no sql");
    executeUpdate(st.toString());
  }

  /**
   * Function dayOfWeek
   * 
   * @param date
   * @return day of week :
   *         <ul>
   *         <li>1=Sunday</li>
   *         <li>2=Monday</li>
   *         <li>3=Tuesday</li>
   *         <li>4=Wednesday</li>
   *         <li>5=Friday</li>
   *         <li>6=Saturday</li>
   *         <ul>
   */
  public static int dayOfWeek(java.sql.Timestamp date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Function week
   * 
   * @param date
   * @return
   */
  public static int week(java.sql.Timestamp date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    return calendar.get(Calendar.WEEK_OF_YEAR);
  }

  /**
   * Function week
   * 
   * @param date
   * @return
   */
  public static int yearweek(java.sql.Timestamp date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    int week = calendar.get(Calendar.WEEK_OF_YEAR);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    if (month == 0 && week >= 52) {
      year--;
    }
    else if (month == 11 && week == 1) {
      year++;
    }
    return year;
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

  private static void checkDir(File dir) throws FileNotFoundException,
                                        CantWriteIOException,
                                        NotDirIOException {
    if (!dir.exists()) {
      log.warn("le repertoire  n'existe pas " + dir.getAbsolutePath());
      throw new FileNotFoundException(dir.getAbsolutePath());
    }
    if (!dir.isDirectory()) {
      log.warn("ce nest pas un repertoire " + dir.getAbsolutePath());
      throw new NotDirIOException(dir.getAbsolutePath());
    }
    if (!dir.canWrite()) {
      log.warn("Impossible d'ecrire dans" + dir.getAbsolutePath());
      throw new CantWriteIOException(dir.getAbsolutePath());
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

    // version 0.1.14
    double    distance_tot;

    int       timetot;

    int       calories;

    int       max_heart_rate;

    int       min_heart_rate;

    int       avg_heart_rate;

    int       alt_plus;

    int       alt_moins;
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
