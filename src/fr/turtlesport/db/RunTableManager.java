package fr.turtlesport.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import fr.turtlesport.db.progress.GeoRouteStoreProgressAdaptor;
import fr.turtlesport.db.progress.IGeoRouteStoreProgress;
import fr.turtlesport.db.progress.IRunStoreProgress;
import fr.turtlesport.db.progress.RunStoreProgressAdaptor;
import fr.turtlesport.geo.IGeoPositionWithAlt;
import fr.turtlesport.geo.IGeoRoute;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.A1000RunTransferProtocol;
import fr.turtlesport.protocol.data.AbstractLapType;
import fr.turtlesport.protocol.data.AbstractRunType;
import fr.turtlesport.protocol.data.AbstractTrkPointType;
import fr.turtlesport.protocol.progress.IRunTransfertProgress;

/**
 * @author denis
 * 
 */
public final class RunTableManager extends AbstractTableManager {
  private static TurtleLogger    log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(RunTableManager.class);
  }

  private static RunTableManager singleton = new RunTableManager();

  /**
   * 
   */
  private RunTableManager() {
    super();
  }

  /**
   * Restitue une instance unique.
   */
  public static RunTableManager getInstance() {
    return singleton;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.db.AbstractTableManager#getTableName()
   */
  @Override
  public String getTableName() {
    return DatabaseManager.TABLE_RUN;
  }

  /**
   * D&eacute;termine si cette ligne existe.
   * 
   * @param idUser
   * @param date
   *          la date.
   * @return l'id du run ou -1 si non trouv&eacute;e.
   */
  public int find(int idUser, Date date) throws SQLException {
    if (log.isInfoEnabled()) {
      log.debug(">>find date=" + date);
    }

    int id = -1;
    long start = System.currentTimeMillis();

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT id FROM ");
      st.append(getTableName());
      st.append(" WHERE start_time=?");
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.MILLISECOND, 0);
      pstmt.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(2, idUser);
      }
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        id = rs.getInt(1);
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isInfoEnabled()) {
      long delay = (System.currentTimeMillis() - start);
      log.info("<<find id=" + id + " delay=" + delay + "ms");
    }
    return id;
  }

  /**
   * Restitue le run pour une date sup&eecute;rieure ou &aecute;gale &aecute;
   * une date.
   * 
   * @param idUser
   * @param date
   * 
   * @return
   * @throws SQLException
   */
  public DataRun findNext(int idUser, Date date) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>find " + date + "  idUser=" + idUser);
    }
    DataRun dataRun = null;

    if (date == null) {
      throw new IllegalArgumentException("date est null");
    }

    long startTime = System.currentTimeMillis();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE start_time >= ? ");
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }
      st.append(" ORDER BY start_time ASC");

      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(2, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        dataRun = new DataRun();
        dataRun.setId(rs.getInt("id"));
        dataRun.setSportType(rs.getInt("sport_type"));
        dataRun.setProgramType(rs.getInt("program_type"));
        dataRun.setMultisport(rs.getInt("multisport"));
        dataRun.setTime(rs.getTimestamp("start_time"));
        dataRun.setComments(rs.getString("comments"));
        dataRun.setEquipement(rs.getString("equipement"));
        dataRun.setLocation(rs.getString("location"));
        log.debug("id" + dataRun.getId());
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isInfoEnabled()) {
      long delay = System.currentTimeMillis() - startTime;
      log.info("<<find delay=" + delay + "ms");
    }
    return dataRun;
  }

  /**
   * Insertion d'un run.
   * 
   * @param a1000
   * @param progress
   * @throws SQLException
   */
  public void store(A1000RunTransferProtocol a1000, IRunStoreProgress progress) throws SQLException {
    log.debug(">>store a1000");

    if (a1000 == null || a1000.getListRunType() == null) {
      return;
    }

    if (progress == null) {
      progress = new RunStoreProgressAdaptor();
    }

    // Debut de tansaction
    DatabaseManager.beginTransaction();

    // Calcul du nombre de line a sauvegarder
    int maxLine = 0;
    for (AbstractRunType runType : a1000.getListRunType()) {
      // run
      maxLine++;
      // lap
      maxLine += runType.sizeLapType();
      // points
      maxLine += runType.sizeTrkPointType();
    }

    // notification
    progress.beginStore(maxLine);
    int nbSave = 0;
    try {
      int id;
      Hashtable<Integer, Integer> hashLap = new Hashtable<Integer, Integer>();
      for (AbstractRunType runType : a1000.getListRunType()) {

        log.info("TrackIndex=" + runType.getTrackIndex());

        // notification
        progress.beginStore(runType);

        id = find(getIdUser(runType), runType.getComputeStartTime());
        boolean isNewRun = false;
        // insertion du run si non present
        if (id == -1) {
          isNewRun = true;

          String comments = null;
          String equipement = null;
          int idUser = -1;
          if (runType.getExtra() != null) {
            comments = ((DataRunExtra) runType.getExtra()).getComments();
            equipement = ((DataRunExtra) runType.getExtra()).getEquipement();
            idUser = ((DataRunExtra) runType.getExtra()).getIdUser();
          }

          id = store(idUser,
                     runType.getSportType(),
                     runType.getProgramType(),
                     runType.getMultisport(),
                     runType.getComputeStartTime(),
                     comments,
                     equipement,
                     null);

          hashLap.put(runType.getTrackIndex(), id);
        }

        // notification
        if (++nbSave % IRunTransfertProgress.POINT_NOTIFY == 0) {
          progress.store(nbSave, maxLine);
        }

        // insertion des run intermediaires.
        boolean hasNewLap = false;
        for (AbstractLapType lap : runType.getListLapType()) {
          log.info("LapIndex=" + lap.getIndex());
          // insertion du lap si non present
          if (!RunLapTableManager.getInstance().findLap(id,
                                                        lap.getIndex(),
                                                        lap.getStartTime())) {
            RunLapTableManager.getInstance().store(id, lap);
            hasNewLap = true;
          }
          // notification
          if (++nbSave % IRunTransfertProgress.POINT_NOTIFY == 0) {
            progress.store(nbSave, maxLine);
          }
        }

        // insertion des points si nouveau tour intermediaire
        if (hasNewLap) {
          log.info("point");

          // notification
          progress.beginStorePoint();
          int maxPoint = runType.sizeTrkPointType();

          // effacement des points pour un run existant
          if (!isNewRun) {
            RunTrkTableManager.getInstance().delete(id);
          }
          // insertion des points
          List<AbstractTrkPointType> listPoint = runType.getListTrkPointType();
          for (int i = 0; i < maxPoint; i++) {
            // notification
            if ((i + 1) % IRunTransfertProgress.POINT_NOTIFY == 0) {
              progress.storePoint(runType, i + 1, maxPoint);
            }
            if (++nbSave % IRunTransfertProgress.POINT_NOTIFY == 0) {
              progress.store(nbSave, maxLine);
            }
            // sauvegarde
            RunTrkTableManager.getInstance().store(id, listPoint.get(i));
          }
        }
        else {
          nbSave += runType.sizeTrkPointType();
          if (++nbSave % IRunTransfertProgress.POINT_NOTIFY == 0) {
            progress.store(nbSave, maxLine);
          }
        }

        // notification
        progress.endStore(runType);
      }

      // logTable();
      // RunLapTableManager.getInstance().logTable();
    }
    catch (SQLException e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw e;
    }
    catch (RuntimeException e) {
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

    // notification
    progress.endStore();

    log.debug("<<store");
  }

  /**
   * Insertion d'un run.
   * 
   * @param route
   * @param progress
   * @throws SQLException
   */
  public void store(List<IGeoRoute> listRoute, IGeoRouteStoreProgress progress) throws SQLException {
    log.debug(">>store route");

    if (listRoute == null || listRoute.size() == 0) {
      return;
    }

    if (progress == null) {
      progress = new GeoRouteStoreProgressAdaptor();
    }

    // Debut de tansaction
    DatabaseManager.beginTransaction();

    // Calcul du nombre de line a sauvegarder
    int maxLine = 0;
    for (IGeoRoute route : listRoute) {
      // run
      maxLine++;
      // lap
      maxLine += route.getSegmentSize();
      // points
      maxLine += route.getAllPoints().size();
    }

    // notification
    progress.beginStore(maxLine);

    try {
      int id;

      int nbSave = 0;
      for (IGeoRoute route : listRoute) {
        // Run
        // --------------
        // notification
        progress.beginStore(route);

        Date startTime = route.getStartTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.set(Calendar.MILLISECOND, 0);
        startTime = cal.getTime();

        if (log.isDebugEnabled()) {
          SimpleDateFormat df = null;
          df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
          log.debug("route startTime=" + df.format(startTime));
        }
        id = find(getIdUser(route), startTime);
        if (id != -1) {
          // suppression du tour a la meme date
          delete(id);
        }

        // insertion du run
        String comments = null;
        String equipement = null;
        int idUser = -1;
        if (route.getExtra() != null) {
          idUser = ((DataRunExtra) route.getExtra()).getIdUser();
          comments = ((DataRunExtra) route.getExtra()).getComments();
          equipement = ((DataRunExtra) route.getExtra()).getEquipement();
        }
        id = store(idUser,
                   route.getSportType(),
                   0,
                   0,
                   startTime,
                   comments,
                   equipement,
                   null);
        // notification
        if (++nbSave % IRunTransfertProgress.POINT_NOTIFY == 0) {
          progress.store(nbSave, maxLine);
        }

        // Lap
        // ------------
        for (int i = 0; i < route.getSegmentSize(); i++) {
          RunLapTableManager.getInstance().store(id, route.getSegment(i));
          nbSave++;
          // notification
          if (++nbSave % IRunTransfertProgress.POINT_NOTIFY == 0) {
            progress.store(nbSave, maxLine);
          }
        }

        // Points
        // ----------------
        log.info("point");

        List<IGeoPositionWithAlt> listPoints = route.getAllPoints();
        int maxPoint = listPoints.size();

        // insertion des points
        for (int i = 0; i < maxPoint; i++) {
          // notification
          if ((i + 1) % IRunTransfertProgress.POINT_NOTIFY == 0) {
            progress.storePoint(route, i + 1, maxPoint);
          }
          if (++nbSave % IRunTransfertProgress.POINT_NOTIFY == 0) {
            progress.store(nbSave, maxLine);
          }
          // sauvegarde
          RunTrkTableManager.getInstance().store(id, listPoints.get(i));
        }

        // notification
        progress.endStore(route);
      }

      // logTable();
      // RunLapTableManager.getInstance().logTable();
    }
    catch (SQLException e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw e;
    }
    catch (RuntimeException e) {
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

    // notification
    progress.endStore();

    log.debug("<<store");
  }

  /**
   * Insertion d'un run sans points..
   * 
   * @param data
   * @throws SQLException
   */
  public void store(DataRunWithoutPoints data) throws SQLException {
    log.debug(">>store  data");

    if (data == null) {
      return;
    }

    // Debut de tansaction
    DatabaseManager.beginTransaction();

    try {
      int id;

      // Run
      // --------------
      Date startTime = data.getStartTime();
      Calendar cal = Calendar.getInstance();
      cal.setTime(startTime);
      cal.set(Calendar.MILLISECOND, 0);
      startTime = cal.getTime();

      if (log.isDebugEnabled()) {
        SimpleDateFormat df = null;
        df = new SimpleDateFormat("dd/MM/yyyy k:mm:ss.S");
        log.debug("route startTime=" + df.format(startTime));
      }
      id = find(data.getIdUser(), startTime);
      if (id != -1) {
        // suppression du tour a la meme date
        delete(id);
      }

      // insertion du run
      id = store(data.getIdUser(),
                 data.getSportType(),
                 0,
                 0,
                 startTime,
                 data.getComments(),
                 data.getEquipement(),
                 null);

      // Lap
      // ------------
      RunLapTableManager.getInstance().store(id,
                                             0,
                                             startTime,
                                             data.getTimeTot(),
                                             (float) data.getDistanceTot(),
                                             0,
                                             0,
                                             0,
                                             0);
    }
    catch (SQLException e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw e;
    }
    catch (RuntimeException e) {
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

    log.debug("<<store");
  }

  /**
   * Insertion d'un run.
   * 
   * @param dataRun
   * @param listTrks
   * @param progress
   * @throws SQLException
   */
  public void store(DataRun dataRun, List<DataRunTrk> listTrks) throws SQLException {
    log.debug(">>store dataRun  listTrks");

    if (dataRun == null || listTrks == null || listTrks.size() == 0) {
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
      st.append(" SET start_time=?");
      st.append(" WHERE id = ?");

      Connection conn = DatabaseManager.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      for (DataRunTrk trk : listTrks) {
        if (trk.getTime() != null) {
          pstmt.setTimestamp(1, trk.getTime());
          break;
        }
      }
      pstmt.setInt(2, id);
      pstmt.executeUpdate();

      // effacement des points pour un run existant
      RunTrkTableManager.getInstance().delete(id);

      // insertion des points
      for (int i = 0; i < listTrks.size(); i++) {
        DataRunTrk trk = listTrks.get(i);
        // sauvegarde
        RunTrkTableManager.getInstance().store(id,
                                               trk.getLatitude(),
                                               trk.getLongitude(),
                                               trk.getTime(),
                                               trk.getAltitude(),
                                               trk.getDistance(),
                                               trk.getHeartRate(),
                                               trk.getCadence());
      }
    }
    catch (SQLException e) {
      log.error("", e);
      // Erreur rollback
      DatabaseManager.rollbackTransaction();
      DatabaseManager.getConnection().close();
      throw e;
    }
    catch (RuntimeException e) {
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

    log.debug("<<store");
  }

  /**
   * Suppression des runs.
   * 
   * @return <code>true</code> si ligne trouv&eacute;e.
   */
  public boolean delete(int idUser, int year, int month) throws SQLException {

    boolean bRes = false;

    List<DataRun> runs = retreiveDesc(idUser, year, month);
    if (runs == null || runs.size() == 0) {
      return false;
    }

    // Debut de tansaction
    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    for (DataRun dr : runs) {
      bRes |= delete(dr.getId());
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
   * Suppression du run.
   * 
   * @return <code>true</code> si ligne trouv&eacute;e.
   */
  public boolean delete(int id) throws SQLException {
    log.debug(">>delete id=" + id);

    boolean bRes = false;

    // Debut de tansaction
    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();
    try {
      // suppression de la course
      StringBuilder st = new StringBuilder();
      st.append("DELETE FROM ");
      st.append(getTableName());
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, id);

      int res = pstmt.executeUpdate();
      if (res > 0) {
        bRes = true;
        // suppression des temps intermediaires
        RunLapTableManager.getInstance().delete(id);

        // suppression des points
        RunTrkTableManager.getInstance().delete(id);

        // suppression de la meteo
        MeteoTableManager.getInstance().delete(id);
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
   * Suppression des courses d'un utilisateur.
   * 
   * @return <code>true</code> si ligne trouv&eacute;e.
   */
  public boolean delete(DataUser user) throws SQLException {
    log.debug(">>delete DataUser");

    if (user == null) {
      return false;
    }

    boolean bRes = false;

    // Debut de tansaction
    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT id FROM ");
      st.append(getTableName());
      st.append(" WHERE id_user=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, user.getId());

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        delete(rs.getInt(1));
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
   * Insertion d'un run.
   * 
   * @param idUser
   * @param sportType
   * @param programType
   * @param multisport
   * @param startTime
   * @param time
   * @param distance
   * @param comments
   * @param location
   * @return
   * @throws SQLException
   */
  protected int store(int idUser,
                      int sportType,
                      int programType,
                      int multisport,
                      Date startTime,
                      String comments,
                      String equipement,
                      String location) throws SQLException {
    log.debug(">>store");
    int id;

    // debut transaction
    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }
    Connection conn = DatabaseManager.getConnection();

    try {
      // Insertion de la course.
      StringBuilder st = new StringBuilder();
      st.append("INSERT INTO ");
      st.append(getTableName());
      st.append("(id_user,");
      st.append(" sport_type,");
      st.append(" program_type,");
      st.append(" multisport,");
      st.append(" start_time,");
      st.append(" comments,");
      st.append(" equipement,");
      st.append(" location)");
      st.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?)");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, idUser);
      pstmt.setInt(2, sportType);
      pstmt.setInt(3, programType);
      pstmt.setInt(4, multisport);
      pstmt.setTimestamp(5, new Timestamp(startTime.getTime()));
      pstmt.setString(6, comments);
      pstmt.setString(7, equipement);
      pstmt.setString(8, location);
      pstmt.executeUpdate();

      // Recuperation de l'id
      st = new StringBuilder();
      st.append("SELECT id FROM ");
      st.append(getTableName());
      st.append(" ORDER BY id DESC");

      pstmt = conn.prepareStatement(st.toString());
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      id = rs.getInt(1);
    }
    catch (SQLException e) {
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw e;
    }
    catch (RuntimeException e) {
      log.error("", e);
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw e;
    }
    catch (Throwable e) {
      log.error("", e);
      if (!isInTransaction) {
        DatabaseManager.rollbackTransaction();
      }
      DatabaseManager.releaseConnection(conn);
      throw new RuntimeException(e);
    }

    // ok
    if (!isInTransaction) {
      DatabaseManager.commitTransaction();
    }
    DatabaseManager.releaseConnection(conn);

    log.debug("<<store id=" + id);
    return id;
  }

  /**
   * D&eacute;termine si cette ligne existe.
   * 
   * @return <code>true</code> si ligne trouv&eacute;e.
   */
  public boolean exist(int id) throws SQLException {
    log.debug(">>exist id=" + id);

    boolean bRes;
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT id FROM ");
      st.append(getTableName());
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, id);

      ResultSet rs = pstmt.executeQuery();
      bRes = rs.next();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<exist bRes=" + bRes);
    return bRes;
  }

  /**
   * D&eacute;termine si cette ligne existe.
   * 
   * @param idUser
   * @param time
   * @return <code>true</code> si ligne trouv&eacute;e.
   */
  public DataRun findPrev(int idUser, Timestamp time) throws SQLException {
    return findNextOrPrev(idUser, time, false);
  }

  /**
   * D&eacute;termine si cette ligne existe.
   * 
   * @param idUser
   * @param time
   * @return <code>true</code> si ligne trouv&eacute;e.
   */
  public DataRun findNext(int idUser, Timestamp time) throws SQLException {
    return findNextOrPrev(idUser, time, true);
  }

  private DataRun findNextOrPrev(int idUser, Timestamp time, boolean isNext) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>findNextOrPrev idUser=" + idUser + " time=" + time
               + " isNext=" + isNext);
    }
    DataRun dataRun = null;

    long start = System.currentTimeMillis();
    Connection conn = DatabaseManager.getConnection();
    try {

      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE start_time ");
      st.append((isNext) ? '>' : '<');
      st.append(" ?");
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }
      st.append(" ORDER BY start_time ");
      st.append((isNext) ? "ASC" : "DESC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setTimestamp(1, time);
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(2, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        dataRun = new DataRun();
        dataRun.setId(rs.getInt("id"));
        dataRun.setSportType(rs.getInt("sport_type"));
        dataRun.setProgramType(rs.getInt("program_type"));
        dataRun.setMultisport(rs.getInt("multisport"));
        dataRun.setTime(rs.getTimestamp("start_time"));
        dataRun.setComments(rs.getString("comments"));
        dataRun.setEquipement(rs.getString("equipement"));
        dataRun.setLocation(rs.getString("location"));
        log.debug("id" + dataRun.getId());
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    long delay = -System.currentTimeMillis() - start;
    if (log.isInfoEnabled()) {
      log.info(dataRun.getTime());
      log.info("<<findNextOrPrev delay=" + delay + "ms");
    }
    return dataRun;
  }

  /**
   * Recuperation des run d'un utilisateur avec ann&eacute; et le mois en
   * criit&egrave;re.
   * 
   * @param idUser
   * @param year
   * @param month
   * @return
   * @throws SQLException
   */
  public List<DataRun> retreiveDesc(int idUser, int year, int month) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>retreiveDesc  idUser=" + idUser + " year=" + year + " month="
               + month);
    }
    List<DataRun> listRun = new ArrayList<DataRun>();

    long startTime = System.currentTimeMillis();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT id, sport_type, start_time FROM ");
      st.append(getTableName());
      st.append(" WHERE YEAR(start_time)=?");
      if (month >= 0 && month <= 12) {
        st.append(" AND MONTH(start_time)=?");
      }
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }
      st.append(" ORDER BY start_time DESC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, year);
      int index = 1;
      if (month >= 0 && month <= 12) {
        pstmt.setInt(++index, month);
      }
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(++index, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        DataRun dataRun = new DataRun();
        dataRun.setId(rs.getInt("id"));
        dataRun.setSportType(rs.getInt("sport_type"));
        dataRun.setTime(rs.getTimestamp("start_time"));
        listRun.add(dataRun);
        log.debug("id" + dataRun.getId());
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isInfoEnabled()) {
      long delay = System.currentTimeMillis() - startTime;
      log.info(">>retreiveDesc  idUser=" + idUser + " delay=" + delay + "ms");
    }
    return listRun;
  }

  /**
   * Recuperation des run d'un utilisateur.
   * 
   * @param idUser
   * @param date
   * 
   * @return
   * @throws SQLException
   */
  public List<DataRun> retreiveDesc(int idUser) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>retreiveDesc  idUser=" + idUser);
    }
    List<DataRun> listRun = new ArrayList<DataRun>();

    long startTime = System.currentTimeMillis();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT id, sport_type, start_time FROM ");
      st.append(getTableName());
      if (!DataUser.isAllUser(idUser)) {
        st.append(" WHERE id_user=?");
      }
      st.append(" ORDER BY start_time DESC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        DataRun dataRun = new DataRun();
        dataRun.setId(rs.getInt("id"));
        dataRun.setSportType(rs.getInt("sport_type"));
        dataRun.setTime(rs.getTimestamp("start_time"));
        listRun.add(dataRun);
        log.debug("id" + dataRun.getId());
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isInfoEnabled()) {
      long delay = System.currentTimeMillis() - startTime;
      log.info("<<retreiveDesc  idUser=" + idUser + " delay=" + delay + "ms");
    }
    return listRun;
  }

  /**
   * Recuperation des run d'un utilisateur.
   * 
   * @param idUser
   * @param date
   * 
   * @return
   * @throws SQLException
   */
  public List<DataRun> retreive(int idUser) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>retreive  idUser=" + idUser);
    }
    List<DataRun> listRun = new ArrayList<DataRun>();

    long startTime = System.currentTimeMillis();

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      if (!DataUser.isAllUser(idUser)) {
        st.append(" WHERE id_user=?");
      }
      st.append(" ORDER BY start_time ASC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        DataRun dataRun = new DataRun();
        dataRun.setId(rs.getInt("id"));
        dataRun.setSportType(rs.getInt("sport_type"));
        dataRun.setProgramType(rs.getInt("program_type"));
        dataRun.setMultisport(rs.getInt("multisport"));
        dataRun.setTime(rs.getTimestamp("start_time"));
        dataRun.setComments(rs.getString("comments"));
        dataRun.setEquipement(rs.getString("equipement"));
        dataRun.setLocation(rs.getString("location"));
        listRun.add(dataRun);
        log.debug("id" + dataRun.getId() + " " + rs.getString("location"));
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isInfoEnabled()) {
      long delay = System.currentTimeMillis() - startTime;
      log.info("<<retreive  idUser=" + idUser + " delay=" + delay + "ms");
    }
    return listRun;
  }

  /**
   * Recuperation des run d'un utilisateur.
   * 
   * @param id
   * 
   * @return
   * @throws SQLException
   */
  public DataRun retreiveWithID(int id) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>retreiveWithID  id=" + id);
    }

    DataRun dataRun = null;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE id=?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, id);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        dataRun = new DataRun();
        dataRun.setId(rs.getInt("id"));
        dataRun.setSportType(rs.getInt("sport_type"));
        dataRun.setProgramType(rs.getInt("program_type"));
        dataRun.setMultisport(rs.getInt("multisport"));
        dataRun.setTime(rs.getTimestamp("start_time"));
        dataRun.setComments(rs.getString("comments"));
        dataRun.setEquipement(rs.getString("equipement"));
        dataRun.setLocation(rs.getString("location"));
        log.debug("id" + dataRun.getId());
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    if (log.isInfoEnabled()) {
      log.info("<<retreive  id=" + id);
    }
    return dataRun;
  }

  /**
   * R&eaute;cup&eaute;ration des dates.
   * 
   * @param idUser
   * @return les dates.
   * @throws SQLException
   */
  public Date[] retrieveDates(int idUser) throws SQLException {
    log.info(">>retrieveDates  idUser");

    if (log.isInfoEnabled()) {
      logTable();
    }

    ArrayList<Date> listDates;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT start_time FROM ");
      st.append(getTableName());
      if (!DataUser.isAllUser(idUser)) {
        st.append(" WHERE id_user=?");
      }
      st.append(" ORDER BY start_time DESC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }

      ResultSet rs = pstmt.executeQuery();

      listDates = new ArrayList<Date>();
      Date date;
      while (rs.next()) {
        date = rs.getTimestamp(1);
        listDates.add(date);
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    Date[] res = new Date[listDates.size()];
    if (res.length > 0) {
      listDates.toArray(res);
    }

    if (log.isInfoEnabled()) {
      SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
      for (Date d : res) {
        log.info(df.format(d));
      }
    }

    log.info("<<retrieveDates");
    return res;
  }

  /**
   * R&eaute;cup&eaute;ration des dates.
   * 
   * @param idUser
   * @param dateFirst
   * @param dateEnd
   * @param idUser
   * @return les dates.
   * @throws SQLException
   */
  public Date[] retrieveDates(int idUser, Date dateFirst, Date dateEnd) throws SQLException {
    if (log.isInfoEnabled()) {
      log.info(">>retrieveDates between idUser");
      logTable();
    }

    ArrayList<Date> listDates;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT start_time FROM ");
      st.append(getTableName());
      st.append(" WHERE (start_time BETWEEN ? AND ?)");
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());

      pstmt.setTimestamp(1, new Timestamp(dateFirst.getTime()));
      pstmt.setTimestamp(2, new Timestamp(dateEnd.getTime()));
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(3, idUser);
      }

      ResultSet rs = pstmt.executeQuery();

      listDates = new ArrayList<Date>();
      Date date;
      while (rs.next()) {
        // date = rs.getDate(1);
        date = rs.getTimestamp(1);
        listDates.add(date);
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    Date[] res = new Date[listDates.size()];
    if (res.length > 0) {
      listDates.toArray(res);
    }

    if (log.isInfoEnabled()) {
      SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
      for (Date d : res) {
        log.info(df.format(d));
      }
    }

    log.info("<<retrieveDates");
    return res;
  }

  /**
   * D&eacute;mine s'il existe une course &agrave; cette date.
   * 
   * @param idUser
   * @param date
   * @return <code>true</code> s'il existe une course &agrave; cette date.
   * @throws SQLException
   */
  public boolean hasDateDay(int idUser, Date date) throws SQLException {
    log.debug(">>hasDateDay date=" + date);

    boolean bRes;

    Connection conn = DatabaseManager.getConnection();
    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE DATE(start_time)=?");
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }

      SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
      log.info("" + ft.format(date));
      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, ft.format(date));
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(2, idUser);
      }
      ResultSet rs = pstmt.executeQuery();

      bRes = rs.next();
      rs.close();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<hasDateDay bRes=" + bRes);
    return bRes;
  }

  /**
   * D&eacute;termine si un exsite > &agrave; cette date.
   * 
   * @param date
   * @param idUser
   * @return
   * @throws SQLException
   */
  public boolean hasNext(int idUser, java.util.Date date) throws SQLException {
    log.debug(">>hasNext  " + date);

    boolean bRes = hasNextOrPrev(idUser, date, true);

    log.debug("<< hasNext " + bRes);
    return bRes;
  }

  /**
   * D&eacute;termine si un existe < &agrave; cette date.
   * 
   * @param idUser
   * @param date
   * 
   * @return
   * @throws SQLException
   */
  public boolean hasPrev(int idUser, java.util.Date date) throws SQLException {
    log.debug(">>hasPrev  " + date);

    boolean bRes = hasNextOrPrev(idUser, date, false);

    log.debug("<< hasPrev " + bRes);
    return bRes;
  }

  private boolean hasNextOrPrev(int idUser, java.util.Date date, boolean isNext) throws SQLException {
    boolean isFound = false;

    if (date == null) {
      throw new IllegalArgumentException("date est null");
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT * FROM ");
      st.append(getTableName());
      st.append(" WHERE start_time ");
      st.append((isNext) ? '>' : '<');
      st.append(" ? ");
      if (!DataUser.isAllUser(idUser)) {
        st.append(" AND id_user=?");
      }

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setTimestamp(1, new Timestamp(date.getTime()));
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(2, idUser);
      }
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        isFound = true;
      }
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    return isFound;
  }

  /**
   * Mis &agrave; jour du type de sportt.
   * 
   * @param id
   * @param sportType
   *          le sport.
   * @throws SQLException
   */
  public void updateSport(int id, int sportType) throws SQLException {
    log.debug(">>updateSport id=" + id);

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET sport_type=?");
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, sportType);
      pstmt.setInt(2, id);

      pstmt.executeUpdate();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<updateSport");
  }

  /**
   * Mis &agrave; jour des commentaires.
   * 
   * @param id
   * @param comments
   *          les commentaires.
   * @param sportType
   *          le sport.
   * @throws SQLException
   */
  public void updateComments(int id, String comments) throws SQLException {
    log.debug(">>updateComments id=" + id);

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET comments=?");
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (comments != null && comments.length() > 500) {
        pstmt.setString(1, comments.substring(0, 499));
      }
      else {
        pstmt.setString(1, comments);
      }
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<updateComments");
  }

  /**
   * Mis &agrave; jour de l'&eacute;quipement.
   * 
   * @param id
   * @param comments
   *          les commentaires.
   * @param equipment
   *          le nom de l'&eacute;quipement.
   * @param sportType
   *          le sport.
   * @throws SQLException
   */
  public void updateEquipment(int id, String equipment) throws SQLException {
    log.debug(">>updateEquipment id=" + id);

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET equipement=?");
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, equipment);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<updateComments");
  }

  /**
   * Mis &agrave; jour de la localisation.
   * 
   * @param id
   * @param localisation
   *          la localisation.
   * @throws SQLException
   */
  public void updateLocation(int id, String localisation) throws SQLException {
    log.debug(">>updateLocation id=" + id);

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET location=?");
      st.append(" WHERE id = ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setString(1, localisation);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<updateLocation");
  }

  /**
   * R&eacute;p&egrave;re les localisations d'un utilisateur.
   * 
   * @param idUser
   *          id de dl'utilisateur.
   * @return la liste des localisations.
   * @throws SQLException
   */
  public List<String> retreiveLocations(int idUser) throws SQLException {
    log.debug(">>retreiveLocations");

    ArrayList<String> list;
    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("SELECT DISTINCT(location) FROM ");
      st.append(getTableName());
      if (!DataUser.isAllUser(idUser)) {
        st.append(" WHERE id_user=?");
      }
      st.append(" ORDER BY location ASC");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      if (!DataUser.isAllUser(idUser)) {
        pstmt.setInt(1, idUser);
      }
      ResultSet rs = pstmt.executeQuery();

      list = new ArrayList<String>();
      while (rs.next()) {
        String s = rs.getString(1);
        if (s != null) {
          list.add(rs.getString(1));
        }
      }

    }
    finally {
      DatabaseManager.releaseConnection(conn);
    }

    log.debug("<<retreiveLocations");
    return list;
  }

  /**
   * Mis &agrave; jour du sport type.
   * 
   * @param oldSportType
   * @param newSportType
   * 
   * @return
   * @throws SQLException
   */
  protected void updateSportType(int oldSportType, int newSportType) throws SQLException {
    log.debug(">>updateSportType");

    boolean isInTransaction = DatabaseManager.isInTransaction();
    if (!isInTransaction) {
      DatabaseManager.beginTransaction();
    }

    Connection conn = DatabaseManager.getConnection();

    try {
      StringBuilder st = new StringBuilder();
      st.append("UPDATE ");
      st.append(getTableName());
      st.append(" SET sport_type=?");
      st.append(" WHERE sport_type= ?");

      PreparedStatement pstmt = conn.prepareStatement(st.toString());
      pstmt.setInt(1, newSportType);
      pstmt.setInt(2, oldSportType);

      pstmt.executeUpdate();
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

    log.debug("<<updateSportType");
  }

  private int getIdUser(AbstractRunType runType) {
    if (runType.getExtra() != null) {
      return ((DataRunExtra) runType.getExtra()).getIdUser();
    }
    return -1;
  }

  private int getIdUser(IGeoRoute route) {
    if (route.getExtra() != null) {
      return ((DataRunExtra) route.getExtra()).getIdUser();
    }
    return -1;
  }

}
