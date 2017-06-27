package fr.turtlesport.protocol;

import java.util.Hashtable;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbProtocolException;
import fr.turtlesport.device.garmin.GarminUsbDevice;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.data.AbstractRunType;
import fr.turtlesport.protocol.data.AbstractWorkout;
import fr.turtlesport.protocol.data.D1003WorkoutOccurenceType;
import fr.turtlesport.protocol.progress.IRunTransfertProgress;
import fr.turtlesport.protocol.progress.RunTransfertProgressAdaptor;

/**
 * @author Denis Apparicio
 * 
 */
public class A1002WorkoutTransferProtocol extends AbstractTransfertProtocol {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(A1002WorkoutTransferProtocol.class);
  }

  /** Command ID. */
  private short               commandIdType;

  /** PID Transfer : Cmnd_Transfer_Run = 450. */
  private static final short  COMMAND_TRANSFER_WORKOUT            = 451;

  /** PID Transfer : Cmnd_Transfer_Laps = 117. */
  private static final short  COMMAND_TRANSFER_WORKOUT_OCCURENCES = 452;

  /** Pid reponse : Pid_Workout = 991. */
  private static final short  PID_WORKOUT                         = 991;

  /** Pid reponse : Pid_Workout_Occurence = 992. */
  private static final short  PID_WORKOUT_OCCURENCE               = 992;

  /** Protocole associe a cette commande. */
  private static final String PROTOCOL_NAME                       = "A1002";

  /**
   * 
   */
  public A1002WorkoutTransferProtocol() {
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
      log.debug("send COMMAND_TRANSFER_WORKOUT");
      sendCommand(COMMAND_TRANSFER_WORKOUT);
      nbPacket = retrievePidRecords();

      // Notification debut transfert
      progress.beginTransfert(nbPacket);

      // transfert
      retrievePidWorkout(nbPacket, hashRunType, progress);

      // Recuperation j a k-1 (cf. 6.19 A1006)
      // ---------------------------------------------------------------
      if (progress.abortTransfert()) {
        abortTransfert();
        return;
      }

      log.debug("send COMMAND_TRANSFER_WORKOUT_OCCURENCES");
      sendCommand(COMMAND_TRANSFER_WORKOUT_OCCURENCES);
      nbPacket = retrievePidRecords();

      // Notification debut transfert
      progress.beginTransfertLap(nbPacket);

      // transfert
      retrievePidWorkoutOccurence(nbPacket, progress);

      // Notification fin transfert
      progress.endTransfert();
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
  private void retrievePidWorkout(int nbPaquet,
                                  Hashtable<Integer, AbstractRunType> hashRunType,
                                  IRunTransfertProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidWorkout nbPaquet=" + nbPaquet);

    UsbPacket packet;
    AbstractWorkout workout;
    int nbPacketRead = 0;
    int nbPacketStep = 0;

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
          && packet.getPacketID() == PID_WORKOUT) {
        workout = AbstractWorkout.newInstance();
        workout.parse(packet);
        if (log.isInfoEnabled()) {
          log.info("getName()=" + workout.getName());
        }

        // notification
        nbPacketStep++;
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
      st.append(" ; nbPacketStep=");
      st.append(nbPacketStep);

      log.warn(st.toString());
    }

    log.info("<<retrievePidWorkout");
  }

  /**
   * Recuperation des laps.
   */
  private void retrievePidWorkoutOccurence(int nbPaquet,
                                           IRunTransfertProgress progress) throws UsbProtocolException {
    log.info(">>retrievePidWorkoutOccurence nbPaquet=" + nbPaquet);

    UsbPacket packet;
    D1003WorkoutOccurenceType workoutOccurence;
    int nbPacketRead = 0;
    int nbPacketWorkoutOccurence = 0;

    while (!progress.abortTransfert()) {
      // lecture
      packet = GarminUsbDevice.getDevice().read();
      nbPacketRead++;

      // Notification nombre de packet lu
      if (nbPacketRead % progress.intervalNotify() == 0) {
        progress.transfert();
      }

      if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
          && packet.getPacketID() == PID_WORKOUT_OCCURENCE) {
        workoutOccurence = new D1003WorkoutOccurenceType();
        workoutOccurence.parse(packet);

        nbPacketWorkoutOccurence++;
        if (log.isInfoEnabled()) {
          log.info("WorkoutName=" + workoutOccurence.getWorkoutName());
        }
      }
      else if (packet.getPacketType() == PACKET_TYPE_APP_LAYER
               || packet.getPacketID() == PID_XFER_CMPLT) {
        break;
      }
      else {
        StringBuilder st = new StringBuilder();
        st.append("WorkoutName attendu : PacketType=");
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
      st.append(" ; nbPacketWorkoutOccurence=");
      st.append(nbPacketWorkoutOccurence);

      log.warn(st.toString());
    }

    log.info("<<retrievePidWorkoutOccurence");
  }

}
