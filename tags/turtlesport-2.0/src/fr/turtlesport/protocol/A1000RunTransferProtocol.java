package fr.turtlesport.protocol;

import java.util.ArrayList;
import java.util.Hashtable;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.device.garmin.GarminUsbDevice;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.AbstractLapType;
import fr.turtlesport.protocol.data.AbstractRunType;
import fr.turtlesport.protocol.data.AbstractTrkPointType;
import fr.turtlesport.protocol.data.D311TrkHdrType;
import fr.turtlesport.protocol.progress.IRunTransfertProgress;
import fr.turtlesport.protocol.progress.RunTransfertProgressAdaptor;

/**
 * @author Denis Apparicio
 * 
 */
public class A1000RunTransferProtocol extends AbstractTransfertProtocol {
  private static TurtleLogger        log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(A1000RunTransferProtocol.class);
  }

  /** Command ID. */
  private short                      commandIdType;

  /** PID Transfer : Cmnd_Transfer_Run = 450. */
  private static final short         COMMAND_TRANSFER_RUN  = 450;

  /** PID Transfer : Cmnd_Transfer_Laps = 117. */
  private static final short         COMMAND_TRANSFER_LAPS = 117;

  /** PID Transfer : Cmnd_Transfer_Trk = 6. */
  private static final short         COMMAND_TRANSFER_TRK  = 6;

  /** Pid reponse : Pid_Run = 990. */
  private static final short         PID_RUN               = 990;

  /** Pid reponse : Pid_Lap = 149. */
  private static final short         PID_LAP               = 149;

  /** Pid reponse : Pid_Trk_Hdr = 99. */
  private static final short         PID_TRK_HDR           = 99;

  /** Pid reponse : Pid_Trk_Data = 1065. */
  private static final short         PID_TRK_DATA          = 34;

  /** Protocole associe a cette commande. */
  private static final String        PROTOCOL_NAME         = "A1000";

  /** Liste des run. */
  private ArrayList<AbstractRunType> listRunType;

  /**
   * 
   */
  public A1000RunTransferProtocol() {
  }

  /**
   * Restitue la liste des courses.
   * 
   * @return la liste des courses.
   */
  public ArrayList<AbstractRunType> getListRunType() {
    return listRunType;
  }

  /**
   * Ajoute une course.
   * 
   * @return la course &agrave; ajouter.
   */
  public void addRunType(AbstractRunType run) {
    if (run == null) {
      return;
    }
    if (listRunType == null) {
      synchronized (A1000RunTransferProtocol.class) {
        listRunType = new ArrayList<AbstractRunType>();
      }
    }
    listRunType.add(run);
  }

  /**
   * Supprime une course.
   * 
   * @return la course &agrave; supprimer.
   */
  public boolean removeRunType(AbstractRunType run) {
    if (run == null || listRunType == null) {
      return false;
    }
    return listRunType.remove(run);
  }

  /**
   * Restitue le nombre de course.
   * 
   * @return le nombre de course.
   */
  public int getListRunTypeSize() {
    return (listRunType == null) ? 0 : listRunType.size();
  }

  /**
   * R&eacute;cup&egrave;re les courses du garmin.
   * 
   * @throws UsbProtocolException
   *           si erreur.
   */
  public void retrieve(IRunTransfertProgress progress) throws UsbProtocolException {
    log.debug(">>retrieve");

    int nbPacket;

    checkInit();
    if (progress == null) {
      progress = new RunTransfertProgressAdaptor();
    }

    Hashtable<Integer, AbstractRunType> hashRunType = new Hashtable<Integer, AbstractRunType>();
    try {
      // Recuperation 0 a j-1
      // ------------------------------------------------------------
      log.debug("send COMMAND_TRANSFER_RUN");
      sendCommand(COMMAND_TRANSFER_RUN);
      nbPacket = retrievePidRecords();

      // Notification debut transfert
      progress.beginTransfert(nbPacket);

      // transfert
      retrievePidRun(nbPacket, hashRunType, progress);

      // Recuperation j a k-1 (cf. 6.19 A1006)
      // ---------------------------------------------------------------
      if (progress.abortTransfert()) {
        abortTransfert();
        return;
      }

      log.debug("send COMMAND_TRANSFER_LAPS");
      sendCommand(COMMAND_TRANSFER_LAPS);
      nbPacket = retrievePidRecords();

      // Notification debut transfert
      progress.beginTransfertLap(nbPacket);

      // transfert
      retrievePidLap(nbPacket, progress);

      // Recuperation k a m-1 (cf. 6.19 A1006)
      // --------------------------------------------------------------
      if (progress.abortTransfert()) {
        abortTransfert();
        return;
      }

      log.debug("send COMMAND_TRANSFER_TRK");
      sendCommand(COMMAND_TRANSFER_TRK);
      nbPacket = retrievePidRecords();

      // Notification debut transfert
      progress.beginTransfertPoint(nbPacket);
      retrievePidTracks(nbPacket, hashRunType, progress);

      // Notification fin transfert
      progress.endTransfert();

      // Effacement des run.
      hashRunType.clear();
      hashRunType = null;

    }
    finally {
      try {
        // Fermeture du device
        GarminUsbDevice.close();
      }
      catch (UsbProtocolException e) {
      }

      // fin
      end();
    }

    log.debug("<<retrieve");
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
   * Recuperation des runs.
   */
  private void retrievePidRun(int nbPaquet,
                              Hashtable<Integer, AbstractRunType> hashRunType,
                              IRunTransfertProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidRun nbPaquet=" + nbPaquet);

    UsbPacket packet;
    AbstractRunType runType;
    int nbPacketRead = 0;
    int nbPacketRunType = 0;

    nbPaquet++;
    while (!progress.abortTransfert()) {
      // lecture
      packet = GarminUsbDevice.getDevice().read();
      nbPacketRead++;

      // Notification nombre de packet lu
      if (nbPacketRead % progress.intervalNotify() == 0) {
        progress.transfert();
      }

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_RUN) {
        runType = AbstractRunType.newInstance();
        runType.parse(packet);
        if (log.isInfoEnabled()) {
          StringBuilder st = new StringBuilder();
          st.append("AbstractRunType trackIndex=");
          st.append(runType.getTrackIndex());
          st.append("; firstLapIndex=");
          st.append(runType.getFirstLapIndex());
          st.append("; lastLapIndex=");
          st.append(runType.getLastLapIndex());
          // st.append("; dist=");
          // st.append(runType.getQuickWorkout().getDistance());
          // st.append("; time=");
          // st.append(runType.getQuickWorkout().getTime());
          log.info(st.toString());
        }

        // notification
        progress.beginTransfertCourse(runType);

        nbPacketRunType++;
        if (runType.hasTrack()) {
          hashRunType.put(runType.getTrackIndex(), runType);
          addRunType(runType);
        }
        else {
          log.warn("AbstractRunType pas de track");
        }

      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
        break;
      }
      else {
        StringBuilder st = new StringBuilder();
        st.append("PidRun attendu : PacketType=");
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
      st.append(" ; nbPacketRunType=");
      st.append(nbPacketRunType);

      log.warn(st.toString());
    }

    log.info("<<retrievePidRun");
  }

  /**
   * Recuperation des laps.
   */
  private void retrievePidLap(int nbPaquet, IRunTransfertProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidLap nbPaquet=" + nbPaquet);

    UsbPacket packet;
    AbstractLapType lapType;
    int nbPacketRead = 0;
    int nbPacketLapType = 0;

    while (!progress.abortTransfert()) {
      // lecture
      packet = GarminUsbDevice.getDevice().read();
      nbPacketRead++;

      // Notification nombre de packet lu
      if (nbPacketRead % progress.intervalNotify() == 0) {
        progress.transfert();
      }

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_LAP) {
        lapType = AbstractLapType.newInstance();
        lapType.parse(packet);
        addLapType(lapType, progress);

        nbPacketLapType++;
        if (log.isInfoEnabled()) {
          StringBuilder st = new StringBuilder();
          st.append("LapType index=");
          st.append(lapType.getIndex());
          st.append(" dist=");
          st.append(lapType.getTotalDist());
          st.append(" time=");
          st.append(lapType.getTotalTime());
          log.info(st.toString());
        }
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
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
      st.append(" ; nbPacketLapType=");
      st.append(nbPacketLapType);

      log.warn(st.toString());
    }

    log.info("<<retrievePidLap");
  }

  /**
   * Recuperation des points.
   */
  private void retrievePidTracks(int nbPaquet,
                                 Hashtable<Integer, AbstractRunType> hashRunType,
                                 IRunTransfertProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidTracks nbPaquet=" + nbPaquet);

    UsbPacket packet;
    D311TrkHdrType d311 = null;
    AbstractTrkPointType trkPointType;
    int nbPacketRead = 0;
    int nbPacketTrkPointType = 0;
    int nbPacketD311 = 0;

    do {
      // lecture
      packet = GarminUsbDevice.getDevice().read();
      nbPacketRead++;

      // Notification nombre de packet lu
      if (nbPacketRead % progress.intervalNotify() == 0) {
        progress.transfert();
      }

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_TRK_HDR) {

        // notification fin de transfert course precedente.
        if (d311 != null) {
          AbstractRunType runType = hashRunType.get(d311.getIndex());
          if (runType != null) {
            progress.endTransfertCourse(runType);
          }
        }

        d311 = new D311TrkHdrType();
        d311.parse(packet);
        nbPacketD311++;
        log.info("D311TrkHdrType index=" + d311.getIndex());
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               && packet.getPacketID() == PID_TRK_DATA) {
        if (d311 == null) {
          log.warn("d311 est null");
        }
        else {
          trkPointType = AbstractTrkPointType.newInstance();
          trkPointType.parse(packet);
          // ajout du point au run
          AbstractRunType runType = hashRunType.get(d311.getIndex());
          if (runType != null) {
            runType.addTrkPointType(trkPointType);
          }
          nbPacketTrkPointType++;
          log.debug("AbstractRunType time=" + trkPointType.getTime());

          // notification
          if (nbPacketTrkPointType % progress.intervalNotify() == 0) {
            progress.transfertPoint(runType);
          }
        }
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
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

    // notification fin de transfert course precedente.
    if (d311 != null) {
      AbstractRunType runType = hashRunType.get(d311.getIndex());
      if (runType != null) {
        progress.endTransfertCourse(runType);
      }
    }

    if (nbPaquet != nbPacketRead) {
      StringBuilder st = new StringBuilder();
      st.append("nbPaquet=");
      st.append(nbPaquet);
      st.append("; nbPacketRead=");
      st.append(nbPacketRead);
      st.append(" ; nbPacketTrkPointType=");
      st.append(nbPacketTrkPointType);
      st.append(" ; nbPacketD311=");
      st.append(nbPacketD311);

      log.warn(st.toString());
    }

    log.info("<<retrievePidCourseTracks");
  }

  /**
   * Ajoute un tour interm&eacute;diaire.
   * 
   */
  private void addLapType(AbstractLapType lapType,
                          IRunTransfertProgress progress) {
    if (lapType == null) {
      return;
    }

    int firstIndex, lastIndex;

    // recherche de la course
    for (AbstractRunType runType : listRunType) {
      firstIndex = runType.getFirstLapIndex();
      lastIndex = runType.getLastLapIndex();
      if (firstIndex <= lapType.getIndex() && lapType.getIndex() <= lastIndex) {
        runType.addLapType(lapType);

        // notification
        progress.transfertLap(runType, lapType);
        break;
      }
    }
  }

}
