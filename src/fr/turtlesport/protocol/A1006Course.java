package fr.turtlesport.protocol;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import fr.turtlesport.GarminDevice;
import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.D1006CourseType;
import fr.turtlesport.protocol.data.D1007CourseLapType;
import fr.turtlesport.protocol.data.D1012CoursePointType;
import fr.turtlesport.protocol.data.D304TrkPointType;
import fr.turtlesport.protocol.data.D311TrkHdrType;
import fr.turtlesport.protocol.progress.CourseProgressAdaptor;
import fr.turtlesport.protocol.progress.ICourseProgress;
import fr.turtlesport.util.ByteUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class A1006Course extends AbstractTransfertProtocol {
  private static TurtleLogger        log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(A1006Course.class);
  }

  /** Command ID. */
  private short                      commandIdType;

  /** PID Transfer : Cmnd_Transfer_Courses = 1061. */
  private static final short         COMMAND_TRANSFER_COURSES       = 561;

  /** PID Transfer : Cmnd_Transfer_Course_Laps = 562. */
  private static final short         COMMAND_TRANSFER_COURSE_LAPS   = 562;

  /** PID Transfer : Cmnd_Transfer_Course_Points = 563. */
  private static final short         COMMAND_TRANSFER_COURSE_POINTS = 563;

  /** PID Transfer : Cmnd_Transfer_Course_Tracks = 564. */
  private static final short         COMMAND_TRANSFER_COURSE_TRACKS = 564;

  /** Pid reponse : Pid_Course = 1061. */
  private static final short         PID_COURSE                     = 1061;

  /** Pid reponse : Pid_Course_Lap = 1062. */
  private static final short         PID_COURSE_LAP                 = 1062;

  /** Pid reponse : Pid_Course_Point = 1063. */
  private static final short         PID_COURSE_POINT               = 1063;

  /** Pid reponse : Pid_Course_Trk_Hdr = 1064. */
  private static final short         PID_COURSE_TRK_HDR             = 1064;

  /** Pid reponse : Pid_Course_Trk_Data = 1065. */
  private static final short         PID_COURSE_TRK_DATA            = 1065;

  /** Protocole associe a cette commande. */
  private static final String        PROTOCOL_NAME                  = "A1006";

  /** Liste des courses. */
  private ArrayList<D1006CourseType> listCourseType;

  /**
   * 
   */
  public A1006Course() {
  }

  /**
   * Restitue la liste des courses.
   * 
   * @return la liste des courses.
   */
  public ArrayList<D1006CourseType> getListCourseType() {
    return listCourseType;
  }

  /**
   * @param d1006
   */
  public void addCourseType(D1006CourseType d1006) {
    if (d1006 == null) {
      return;
    }
    if (listCourseType == null) {
      listCourseType = new ArrayList<D1006CourseType>();
    }
    listCourseType.add(d1006);
  }

  /**
   * Restitue le nombre de course.
   * 
   * @return le nombre de course.
   */
  public int getListCourseTypeSize() {
    return (listCourseType == null) ? 0 : listCourseType.size();
  }

  /**
   * R&eacute;cup&egrave;re les courses du garmin.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  public void retrieve(ICourseProgress progress) throws UsbProtocolException {
    log.debug(">>retrieve");

    int nbPacket = 0;

    checkInit();
    if (progress == null) {
      progress = new CourseProgressAdaptor();
    }

    Hashtable<Integer, D1006CourseType> hashCourseType = new Hashtable<Integer, D1006CourseType>();
    try {

      // Recuperation 0 a j-1 (cf. 6.19 A1006)
      // ------------------------------------------------------------
      // Notification debut transfert
      progress.beginTransfert();

      log.debug("send COMMAND_TRANSFER_COURSE");
      sendCommand(COMMAND_TRANSFER_COURSES);
      nbPacket = retrievePidRecords();

      // recuperation
      retrievePidCourse(nbPacket, hashCourseType, progress);

      // Recuperation j a k-1 (cf. 6.19 A1006)
      // ------------------------------------------------------------
      if (progress.abortTransfert()) {
        log.info("abortTransfert");
        abortTransfert();
        return;
      }

      // Notification debut transfert
      progress.beginTransfertLap();

      log.debug("send COMMAND_TRANSFER_COURSE_LAPS");
      sendCommand(COMMAND_TRANSFER_COURSE_LAPS);
      nbPacket = retrievePidRecords();

      // recuperation
      retrievePidLap(nbPacket, hashCourseType, progress);

      // Recuperation k a m-1 (cf. 6.19 A1006)
      // ------------------------------------------------------------
      if (progress.abortTransfert()) {
        log.info("abortTransfert");
        abortTransfert();
        return;
      }

      log.debug("send COMMAND_TRANSFER_COURSE_TRACKS");
      sendCommand(COMMAND_TRANSFER_COURSE_TRACKS);
      nbPacket = retrievePidRecords();

      // Notification debut transfert
      progress.beginTransfertTrk(nbPacket);

      // recuperation
      retrievePidTracks(nbPacket, hashCourseType, progress);

      // Recuperation m a n-1 (cf. 6.19 A1006)
      // ------------------------------------------------------------
      if (progress.abortTransfert()) {
        log.info("abortTransfert");
        abortTransfert();
        return;
      }

      log.debug("send COMMAND_TRANSFER_COURSE_POINTS");
      sendCommand(COMMAND_TRANSFER_COURSE_POINTS);
      nbPacket = retrievePidRecords();

      // Notification debut transfert
      progress.beginTransfertPoint(nbPacket);

      // recuperation
      retrievePidPoints(nbPacket, hashCourseType, progress);

      // fin
      // ------------------------------------------------------------
      // Notification fin transfert
      progress.endTransfert();

      // Valorisation des courses.
      log.info("Valorisation des courses.");
      listCourseType = new ArrayList<D1006CourseType>();
      Enumeration<D1006CourseType> e = hashCourseType.elements();
      while (e.hasMoreElements()) {
        D1006CourseType d = e.nextElement();
        listCourseType.add(d);
        log.info("D1006CourseType : " + d.fullDescription());
      }

      // Effacement de la hashtable.
      hashCourseType.clear();
      hashCourseType = null;
    }
    finally {
      try {
        // Fermeture du device
        GarminDevice.close();
      }
      catch (UsbProtocolException e) {
      }

      // fin
      end();
    }

    log.debug("<<retrieve");
  }

  /**
   * Envoie les courses au garmin.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  public void send(ICourseProgress progress) throws UsbProtocolException,
                                            GarminProtocolException {
    log.debug(">>send");
    int nbPacket;

    checkInit();
    if (progress == null) {
      progress = new CourseProgressAdaptor();
    }

    try {
      // Envoie 0 a j-1 (cf. 6.19 A1006) (liste des courses)
      // ------------------------------------------------------------
      // Notification debut transfert
      progress.beginTransfert();

      sendPidRecords(getListCourseTypeSize());
      sendPidCourse(progress);

      // Envoie de j+1 a k-1 (liste des courses lap)
      // ------------------------------------------------------------
      if (progress.abortTransfert()) {
        log.info("abortTransfert");
        abortTransfert();
        return;
      }

      // envoi
      sendPidRecords(lapSize());
      sendPidLap(progress);

      // Recuperation k a m-1 (cf. 6.19 A1006) (trk point)
      // ------------------------------------------------------------
      if (progress.abortTransfert()) {
        log.info("abortTransfert");
        abortTransfert();
        return;
      }

      nbPacket = trkPointTypeSize() + getListCourseTypeSize();

      // Notification debut transfert
      progress.beginTransfertTrk(nbPacket);

      // envoi
      sendPidRecords(nbPacket);
      sendPidTracks(progress);

      // Recuperation k a m-1 (cf. 6.19 A1006) ( point)
      // ------------------------------------------------------------
      if (progress.abortTransfert()) {
        log.info("abortTransfert");
        abortTransfert();
        return;
      }

      nbPacket = pointTypeSize();

      // Notification debut transfert
      progress.beginTransfertPoint(nbPacket);

      // envoi
      sendPidRecords(nbPacket);
      sendPidPoint(progress);

      // Notification fin transfert
      progress.endTransfert();
    }
    finally {
      try {
        // Fermeture du device
        GarminDevice.close();
      }
      catch (UsbProtocolException e) {
      }

      // fin
      end();
    }

    log.debug("<<send");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.AbstractTransfertProtocol#getCommandIdType()
   */
  @Override
  public short getCommandIdType() {
    return commandIdType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.AbstractTransfertProtocol#getProtocolName()
   */
  @Override
  public String getProtocolName() {
    return PROTOCOL_NAME;
  }

  /**
   * Restitue le nombre de points.
   */
  private int trkPointTypeSize() {
    int nb = 0;
    if (listCourseType != null) {
      for (D1006CourseType d1006 : listCourseType) {
        nb += d1006.getListTrkPointTypeSize();
      }
    }
    return nb;
  }

  /**
   * Restitue le nombre de points.
   */
  private int pointTypeSize() {
    int nb = 0;
    if (listCourseType != null) {
      for (D1006CourseType d1006 : listCourseType) {
        nb += d1006.getListPointTypeSize();
      }
    }
    return nb;
  }

  /**
   * Restitue le nombre de points.
   */
  private int lapSize() {
    int nb = 0;
    if (listCourseType != null) {
      for (D1006CourseType d1006 : listCourseType) {
        nb += d1006.getListCourseLapTypeSize();
      }
    }
    return nb;
  }

  /**
   * Envoie de la commande Pid_Records.
   */
  private void sendPidRecords(int size) throws UsbProtocolException {
    log.debug(">>sendPidRecords");

    // construction du packet
    UsbPacket packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                     PID_RECORDS);
    packet.setData(ByteUtil.to2Bytes(size));

    // envoie du packet
    GarminDevice.getDevice().send(packet);

    log.debug("<<sendPidRecords");
  }

  /**
   * Envoie des courses.
   * 
   */
  private void sendPidCourse(ICourseProgress progress) throws UsbProtocolException,
                                                      GarminProtocolException {
    log.debug(">>sendPidCourse");

    UsbPacket packet;
    UsbPacketOutputStream out;

    if (listCourseType != null) {
      for (int i = 0; i < listCourseType.size(); i++) {
        // reuperation de la course et reaffectation des index
        D1006CourseType courseType = listCourseType.get(i);
        courseType.setIndex(i);
        courseType.setTrackIndex(i);

        // serialisation
        out = new UsbPacketOutputStream();
        courseType.serialize(out);

        // construction du packet
        packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER, PID_COURSE);
        packet.setData(out.toByteArray());

        // envoie du packet
        GarminDevice.getDevice().send(packet);

        // abort
        if (progress.abortTransfert()) {
          return;
        }
      }
    }
    sendXferCmplt(COMMAND_TRANSFER_COURSES);

    log.debug("<<sendPidCourse");
  }

  /**
   * Envoie des courses lap.
   */
  private void sendPidLap(ICourseProgress progress) throws UsbProtocolException,
                                                   GarminProtocolException {
    log.debug(">>sendPidLap");

    UsbPacket packet;
    UsbPacketOutputStream out;

    if (listCourseType != null) {
      for (D1006CourseType courseType : listCourseType) {
        for (int i = 0; i < courseType.getListCourseLapTypeSize(); i++) {
          // reaffectation des index
          D1007CourseLapType lap = courseType.getListCourseLapType(i);
          lap.setCourseIndex(courseType.getIndex());
          lap.setLapIndex(i);

          // serialisation
          out = new UsbPacketOutputStream();
          lap.serialize(out);

          // construction du packet
          packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER, PID_COURSE_LAP);
          packet.setData(out.toByteArray());
          // envoie du packet
          GarminDevice.getDevice().send(packet);

          // abort
          if (progress.abortTransfert()) {
            return;
          }
        }
      }
    }
    sendXferCmplt(COMMAND_TRANSFER_COURSE_LAPS);

    log.debug("<<sendPidLap");
  }

  /**
   * Envoie des track points.
   */
  private void sendPidTracks(ICourseProgress progress) throws UsbProtocolException,
                                                      GarminProtocolException {
    log.debug(">>sendPidTracks");

    UsbPacket packet;
    UsbPacketOutputStream out;

    int nbSend = 0;

    if (listCourseType != null) {
      for (D1006CourseType courseType : listCourseType) {
        // serialisation D311
        D311TrkHdrType trkHdrType = new D311TrkHdrType(courseType
            .getTrackIndex());
        out = new UsbPacketOutputStream();
        trkHdrType.serialize(out);

        // construction du packet
        packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                               PID_COURSE_TRK_HDR);
        packet.setData(out.toByteArray());

        // envoie du packet
        GarminDevice.getDevice().send(packet);

        // notification
        nbSend++;
        if (nbSend % progress.pointNotify() == 0) {
          progress.transfertTrk(courseType);
        }

        // abort
        if (progress.abortTransfert()) {
          return;
        }

        for (D304TrkPointType trkPointType : courseType.getListTrkPointType()) {
          // serialisation
          out = new UsbPacketOutputStream();
          trkPointType.serialize(out);

          // construction du packet
          packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                 PID_COURSE_TRK_DATA);
          packet.setData(out.toByteArray());

          // envoie du packet
          GarminDevice.getDevice().send(packet);

          // notification
          nbSend++;
          if (nbSend % progress.pointNotify() == 0) {
            progress.transfertTrk(courseType);
          }

          // abort
          if (progress.abortTransfert()) {
            return;
          }
        }
      }
    }
    sendXferCmplt(COMMAND_TRANSFER_COURSE_TRACKS);

    log.debug("<<sendPidTracks");
  }

  /**
   * Envoie des points.
   */
  private void sendPidPoint(ICourseProgress progress) throws UsbProtocolException,
                                                     GarminProtocolException {
    log.debug(">>sendPidPoint");

    UsbPacket packet;
    UsbPacketOutputStream out;

    int nbSend = 0;

    if (listCourseType != null) {
      for (D1006CourseType courseType : listCourseType) {
        if (courseType.getListPointTypeSize() > 0) {
          for (D1012CoursePointType point : courseType.getListPointType()) {
            // reaffectation des index
            point.setCourseIndex(courseType.getTrackIndex());

            // serialisation
            out = new UsbPacketOutputStream();
            point.serialize(out);

            // construction du packet
            packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                   PID_COURSE_POINT);
            packet.setData(out.toByteArray());

            // envoie du packet
            GarminDevice.getDevice().send(packet);

            // notification
            nbSend++;
            if (nbSend % progress.pointNotify() == 0) {
              progress.transfertPoint(courseType);
            }

            // abort
            if (progress.abortTransfert()) {
              return;
            }
          }
        }
      }
    }

    sendXferCmplt(COMMAND_TRANSFER_COURSE_POINTS);

    log.debug("<<sendPidPoint");
  }

  /**
   * Envoie de la commande Pid_Xfer_Cmplt.
   * 
   */
  private void sendXferCmplt(int commandId) throws UsbProtocolException {
    log.debug(">>sendXferCmplt");

    // construction du packet
    UsbPacket packet = new UsbPacket(ICommand.PACKET_TYPE_APP_LAYER,
                                     ICommand.PID_XFER_CMPLT);

    packet.setData(ByteUtil.to2Bytes(commandId));

    // envoie du packet
    GarminDevice.getDevice().send(packet);

    log.debug("<<sendXferCmplt");
  }

  private int retrievePidRecords() throws UsbProtocolException {
    log.debug(">>retrievePidRecords");

    int nbPaquet = 0;

    // Lecture
    UsbPacket packet = GarminDevice.getDevice().read();

    // Premier paquet Pid_Records (spec 5.4)
    if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
        && packet.getPacketID() == PID_RECORDS) {
      nbPaquet = ByteUtil.toShort(packet.getData()[0], packet.getData()[1]);
    }
    else {
      log.warn("packet.getPacketType()=" + packet.getPacketType());
      log.warn("packet.getPacketID()=" + packet.getPacketID());
      log.warn("PidRecords attendu");
    }

    log.debug("<<retrievePidRecords");
    return nbPaquet;
  }

  /**
   * Recuperation des courses.
   */
  private void retrievePidCourse(int nbPaquet,
                                 Hashtable<Integer, D1006CourseType> hashCourseType,
                                 ICourseProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidCourse nbPaquet=" + nbPaquet);

    UsbPacket packet;
    D1006CourseType d1006;
    int nbPacketRead = 0;
    int nbPacketD1006 = 0;

    while (!progress.abortTransfert()) {
      packet = GarminDevice.getDevice().read();
      nbPacketRead++;

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_COURSE) {
        d1006 = new D1006CourseType();
        d1006.parse(packet);
        hashCourseType.put(d1006.getIndex(), d1006);
        nbPacketD1006++;
        if (log.isInfoEnabled()) {
          StringBuilder st = new StringBuilder();
          st.append("D1006CourseType index=");
          st.append(d1006.getIndex());
          st.append("; name=");
          st.append(d1006.getCourseName());
          st.append("; trackIndex=");
          st.append(d1006.getTrackIndex());
          log.info(st.toString());
        }
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
        log.info("PID_XFER_CMPLT");
        break;
      }
      else {
        StringBuilder st = new StringBuilder();
        st.append("PidCourse attendu : PacketType=");
        st.append(packet.getPacketType());
        st.append("; PacketID=");
        st.append(packet.getPacketID());
        log.warn(st.toString());
      }
    }

    if (nbPaquet != nbPacketRead - 1) {
      StringBuilder st = new StringBuilder();
      st.append("nbPaquet=");
      st.append(nbPaquet);
      st.append("; nbPacketRead=");
      st.append(nbPacketRead);
      st.append(" ; nbPacketD1006=");
      st.append(nbPacketD1006);
      log.warn(st.toString());
    }

    log.info("<<retrievePidCourse");
  }

  /**
   * Recuperation des tours intermediaires.
   */
  private void retrievePidLap(int nbPaquet,
                              Hashtable<Integer, D1006CourseType> hashCourseType,
                              ICourseProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidCourseLap nbPaquet=" + nbPaquet);

    UsbPacket packet;
    D1007CourseLapType d1007;
    int nbPacketRead = 0;
    int nbPacketD1007 = 0;

    while (!progress.abortTransfert()) {

      packet = GarminDevice.getDevice().read();
      nbPacketRead++;

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_COURSE_LAP) {
        d1007 = new D1007CourseLapType();
        d1007.parse(packet);
        nbPacketD1007++;
        if (log.isInfoEnabled()) {
          StringBuilder st = new StringBuilder();
          st.append("D1007CourseLapType index=");
          st.append(d1007.getCourseIndex());
          st.append("; lapIndex=");
          st.append(d1007.getLapIndex());
          st.append("; totalTime=");
          st.append(d1007.getTotalTime());
          st.append("; totalDist=");
          st.append(d1007.getTotalDist());
          st.append("; avg_heart_rate=");
          st.append(d1007.getAvgHeartRate());
          st.append("; intensity=");
          st.append(d1007.getIntensity());
          st.append("; avg_cadence=");
          st.append(d1007.getAvgCadence());
          log.info(st.toString());
        }

        // Ajout du tour intermediaire
        D1006CourseType d1006 = hashCourseType.get(d1007.getCourseIndex());
        if (d1006 != null) {
          log.info("Ajout du tour intermediaire");
          d1006.addCourseLapType(d1007);
        }
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
        log.info("PID_XFER_CMPLT");
        break;
      }
      else {
        StringBuilder st = new StringBuilder();
        st.append("PidCourseLap attendu : PacketType=");
        st.append(packet.getPacketType());
        st.append("; PacketID=");
        st.append(packet.getPacketID());
        log.warn(st.toString());
      }
    }

    if (nbPaquet != nbPacketRead - 1) {
      StringBuilder st = new StringBuilder();
      st.append("nbPaquet=");
      st.append(nbPaquet);
      st.append("; nbPacketRead=");
      st.append(nbPacketRead);
      st.append(" ; nbPacketD1007=");
      st.append(nbPacketD1007);

      log.warn(st.toString());
    }

    log.info("<<retrievePidCourseLap");
  }

  /**
   * Recuperation des trk points.
   */
  private void retrievePidTracks(int nbPaquet,
                                 Hashtable<Integer, D1006CourseType> hashCourseType,
                                 ICourseProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidTracks nbPaquet=" + nbPaquet);

    UsbPacket packet;
    D311TrkHdrType d311 = null;
    D304TrkPointType d304;
    int nbPacketRead = 0;
    int nbPacketD304 = 0;
    int nbPacketD311 = 0;

    do {
      // k+2
      packet = GarminDevice.getDevice().read();
      nbPacketRead++;

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_COURSE_TRK_HDR) {
        d311 = new D311TrkHdrType();
        d311.parse(packet);
        nbPacketD311++;
        log.info("D311TrkHdrType index=" + d311.getIndex());
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               && packet.getPacketID() == PID_COURSE_TRK_DATA) {
        d304 = new D304TrkPointType();
        d304.parse(packet);

        // ajout du point a la course
        D1006CourseType d1006 = hashCourseType.get(d311.getIndex());
        if (d1006 != null) {
          d1006.addTrkPointType(d304);
        }
        nbPacketD304++;
        // notification
        if (nbPacketRead % progress.pointNotify() == 0) {
          progress.transfertTrk(d1006);
        }
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
        log.info("PID_XFER_CMPLT");
        break;
      }
      else {
        StringBuilder st = new StringBuilder();
        st.append("Pid_Course_Trk_Hdr attendu : PacketType=");
        st.append(packet.getPacketType());
        st.append("; PacketID=");
        st.append(packet.getPacketID());
        log.warn(st.toString());
      }

    }
    while (!progress.abortTransfert());

    if (nbPaquet != nbPacketRead) {
      StringBuilder st = new StringBuilder();
      st.append("nbPaquet=");
      st.append(nbPaquet);
      st.append("; nbPacketRead=");
      st.append(nbPacketRead);
      st.append(" ; nbPacketD304=");
      st.append(nbPacketD304);
      st.append(" ; nbPacketD311=");
      st.append(nbPacketD311);

      log.warn(st.toString());
    }

    log.info("nbPacketD311=" + nbPacketD311 + " nbPacketD304=" + nbPacketD304);

    log.info("<<retrievePidTracks");
  }

  /**
   * Recuperation des points.
   */
  private void retrievePidPoints(int nbPaquet,
                                 Hashtable<Integer, D1006CourseType> hashCourseType,
                                 ICourseProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidPoints nbPaquet=" + nbPaquet);

    UsbPacket packet;
    D1012CoursePointType d1012;
    int nbPacketRead = 0;
    int nbPacket = 0;

    while (!progress.abortTransfert()) {

      packet = GarminDevice.getDevice().read();
      nbPacketRead++;

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_COURSE_POINT) {
        d1012 = new D1012CoursePointType();
        d1012.parse(packet);
        nbPacket++;
        if (log.isInfoEnabled()) {
          StringBuilder st = new StringBuilder();
          st.append("D1012CoursePointType=");
          st.append(d1012.getCourseIndex());
          st.append("; courseIndex=");
          st.append(d1012.getCourseIndex());
          st.append("; name=");
          st.append(d1012.getName());
          st.append("; totalDist=");
          st.append(d1012.getTrackPointTime());
          log.info(st.toString());
        }

        // ajout du point a la course
        D1006CourseType d1006 = hashCourseType.get(d1012.getCourseIndex());
        if (d1006 != null) {
          d1006.addPointType(d1012);
        }
        // notification
        if (nbPacket % progress.pointNotify() == 0) {
          progress.transfertPoint(d1006);
        }
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
        log.info("PID_XFER_CMPLT");
        break;
      }
      else {
        StringBuilder st = new StringBuilder();
        st.append("PidCourseLap attendu : PacketType=");
        st.append(packet.getPacketType());
        st.append("; PacketID=");
        st.append(packet.getPacketID());
        log.warn(st.toString());
      }
    }

    if (nbPaquet != nbPacketRead - 1) {
      StringBuilder st = new StringBuilder();
      st.append("nbPaquet=");
      st.append(nbPaquet);
      st.append("; nbPacketRead=");
      st.append(nbPacketRead);
      st.append(" ; nbPacketD1007=");
      st.append(nbPacket);

      log.warn(st.toString());
    }

    log.info("<<retrievePidPoints");
  }

}
